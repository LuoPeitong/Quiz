import random

from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker, declarative_base
from sqlalchemy import Column, Integer, String, Text, JSON, TIMESTAMP, func, select
from typing import List, Optional, Dict
from pydantic import BaseModel
from contextlib import asynccontextmanager

# === 数据库配置 ===
DB_USER = "root"
DB_PASSWORD = "123"
DB_HOST = "localhost"
DB_PORT = "3306"
DB_NAME = "quiz_db"

DATABASE_URL = f"mysql+aiomysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}?charset=utf8mb4"

# === SQLAlchemy 配置 ===
Base = declarative_base()
engine = create_async_engine(DATABASE_URL, echo=False, future=True)
AsyncSessionLocal = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

# === 模型定义 ===
class Question(Base):
    __tablename__ = "questions"
    id = Column(Integer, primary_key=True, index=True)
    question = Column(Text, nullable=False)
    options = Column(JSON, nullable=True)
    answer = Column(String(255), nullable=False)
    type = Column(String(50), nullable=False)

class UserAnswer(Base):
    __tablename__ = "user_answers"
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(String(64), nullable=False)
    score = Column(Integer, nullable=False)
    time_used = Column(Integer, nullable=False)
    submit_time = Column(TIMESTAMP, server_default=func.now())

# === 模型定义 ===
class Answer(Base):
    __tablename__ = "answers"
    id = Column(Integer, primary_key=True, index=True)
    submission_id = Column(Integer, ForeignKey("submissions.id"), nullable=False)
    question_id = Column(Integer, ForeignKey("questions.id"), nullable=False)
    user_answer = Column(Text, nullable=False)
    correct_answer = Column(Text, nullable=False)
    is_correct = Column(Boolean)


# === 提交答题接口 ===
class AnswerItem(BaseModel):
    question_id: int
    user_answer: str
    correct_answer: str
    is_correct: bool

class SubmitRequest(BaseModel):
    openid: str
    time_used: int
    score: int
    answers: List[AnswerItem]

# === 生命周期管理 ===
@asynccontextmanager
async def lifespan(app: FastAPI):
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield
    await engine.dispose()

# === FastAPI 实例 ===
app = FastAPI(lifespan=lifespan)

# === 获取数据库会话 ===
async def get_session() -> AsyncSession:
    async with AsyncSessionLocal() as session:
        yield session

# === 获取题目接口 ===
@app.get("/api/questions")
async def get_questions(session: AsyncSession = Depends(get_session)):
    result = await session.execute(select(Question))
    questions = result.scalars().all()

    # 随机抽取最多15道题
    selected_questions = random.sample(questions, min(15, len(questions)))

    return [
        {
            "id": q.id,
            "question": q.question,
            "type": q.type,
            "options": q.options,
            "answer": q.answer.split(",")
        }
        for q in selected_questions
    ]

@app.post("/api/submit")
async def submit_quiz(data: SubmitRequest, session: AsyncSession = Depends(get_session)):
    try:
        # 保存总记录
        new_record = UserAnswer(
            user_id=data.openid,
            score=data.score,
            time_used=data.time_used
        )
        session.add(new_record)
        await session.flush()  # 获取 new_record.id

        # 保存每题答题详情
        for ans in data.answers:
            detail = Answer(  # ✅ 修改了这里
                submission_id=new_record.id,
                question_id=ans.question_id,
                user_answer=ans.user_answer,
                correct_answer=ans.correct_answer,
                is_correct=ans.is_correct
            )
            session.add(detail)

        await session.commit()
        return {"code": 0, "msg": "提交成功", "submission_id": new_record.id}
    except Exception as e:
        await session.rollback()
        raise HTTPException(status_code=500, detail=f"提交失败: {str(e)}")

# === 排行榜接口 ===
@app.get("/api/rank")
async def get_rank(session: AsyncSession = Depends(get_session)):
    result = await session.execute(
        select(UserAnswer).order_by(
            UserAnswer.score.desc(),
            UserAnswer.time_used.asc()
        ).limit(50)
    )
    rows = result.scalars().all()
    return [
        {
            "user_id": row.user_id,
            "score": row.score,
            "time_used": row.time_used,
            "submit_time": row.submit_time
        }
        for row in rows
    ]
