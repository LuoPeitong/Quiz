package org.example.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuizSqlProvider {

    // Java8 兼容的常量 Map 构造：前端 sortField → SQL 列名
    private static final Map<String,String> ALLOWED_SORTS;
    static {
        Map<String,String> tmp = new HashMap<>();
        tmp.put("participationCount", "participation_count");
        tmp.put("firstPrizeCount",    "first_prize_count");
        tmp.put("secondPrizeCount",   "second_prize_count");
        tmp.put("thirdPrizeCount",    "third_prize_count");
        tmp.put("noPrizeCount",       "no_prize_count");
        ALLOWED_SORTS = Collections.unmodifiableMap(tmp);
    }

    /**
     * 动态生成员工排行榜分页 SQL
     * 返回字段：name, company, phone, count（对应前端选的统计项）
     */
    public String employeeRankList(Map<String,Object> params) {
        // 前端传入的排序字段（如 participationCount、firstPrizeCount 等）
        String sortKey  = (String) params.get("sortField");
        // 映射到子查询里的真实列名
        String orderCol = ALLOWED_SORTS.getOrDefault(sortKey, "participation_count");
        int offset      = (Integer) params.get("offset");
        int pageSize    = (Integer) params.get("pageSize");

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t.name, t.company, t.phone, ")
                // 把动态列映射成通用的 count 字段
                .append("       t.").append(orderCol).append(" AS count ")
                .append("FROM ( ")
                .append("  SELECT u.id, u.nickname AS name, u.company, u.phone, ")
                .append("         COALESCE(q.cnt, 0) AS participation_count, ")
                .append("         COALESCE(l.first, 0) AS first_prize_count, ")
                .append("         COALESCE(l.second, 0) AS second_prize_count, ")
                .append("         COALESCE(l.third, 0) AS third_prize_count, ")
                .append("         COALESCE(l.none, 0)  AS no_prize_count ")
                .append("    FROM users u ")
                // 子查询 q：统计每人答题次数
                .append("    LEFT JOIN (SELECT user_id, COUNT(*) AS cnt ")
                .append("               FROM quiz_records ")
                .append("              GROUP BY user_id) q ")
                .append("      ON q.user_id = u.id ")
                // 子查询 l：统计每人各级中奖次数
                .append("    LEFT JOIN ( ")
                .append("      SELECT user_id, ")
                .append("             SUM(CASE WHEN prize_name='一等奖' THEN 1 ELSE 0 END)   AS first, ")
                .append("             SUM(CASE WHEN prize_name='二等奖' THEN 1 ELSE 0 END)   AS second, ")
                .append("             SUM(CASE WHEN prize_name='三等奖' THEN 1 ELSE 0 END)   AS third, ")
                .append("             SUM(CASE WHEN prize_name='未中奖' THEN 1 ELSE 0 END)   AS none ")
                .append("        FROM lottery_records ")
                .append("       GROUP BY user_id ")
                .append("    ) l ON l.user_id = u.id ")
                // 只保留至少答过一次题的用户
                .append("   WHERE q.cnt IS NOT NULL ")
                .append(") t ")
                // 按选中的列倒序排序，并分页
                .append("ORDER BY t.").append(orderCol).append(" DESC ")
                .append("LIMIT ").append(offset).append(", ").append(pageSize);

        return sb.toString();
    }


    /**
     * 员工排行榜总条数：统计所有至少做过一次答题的用户数量
     */
    public String employeeRankCount() {
        return "SELECT COUNT(*) FROM (SELECT user_id FROM quiz_records GROUP BY user_id) x";
    }
}
