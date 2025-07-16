<template>
  <div class="container">
    <!-- 1. 日期选择 -->
    <div class="block">
      <span class="demonstration">日期选择：</span>
      <el-date-picker
        v-model="date"
        type="date"
        value-format="yyyy-MM-dd"
        placeholder="选择日期"
        :picker-options="pickerOptions"
        @change="fetchData"
      />
    </div>

    <!-- 2. 总中奖人数 -->
    <el-descriptions class="margin-top" title="中奖统计" :column="1" border>
      <el-descriptions-item label="总中奖人数">
        {{ summary.totalWinners }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 3. 各公司中奖人数柱状图 -->
    <el-card class="margin-top">
      <div ref="winnerChart" class="chart-container"></div>
    </el-card>

    <!-- 4. 各公司中奖列表 -->
    <el-table :data="companyWinners" stripe border class="margin-top">
      <el-table-column prop="company" label="公司" />
      <el-table-column prop="count" label="中奖人数" />
    </el-table>

    <!-- 5. 每人中奖详情 -->
    <el-table :data="userWinners" stripe border class="margin-top">
      <el-table-column prop="company" label="公司" />
      <el-table-column prop="nickname" label="姓名" />
      <el-table-column prop="prize_name" label="奖项" />
      <el-table-column prop="prize_amt" label="金额" />
      <el-table-column prop="draw_time" label="抽中时间" />
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'LotteryStatistics',
  data () {
    return {
      date: new Date().toISOString().slice(0, 10),
      pickerOptions: {
        disabledDate (t) {
          return t.getTime() > Date.now()
        },
        shortcuts: [
          {
            text: '今天',
            onClick (p) {
              p.$emit('pick', new Date())
            }
          }
        ]
      },
      summary: {
        totalWinners: 0
      },
      companyWinners: [],
      userWinners: []
    }
  },
  mounted () {
    this.fetchData()
  },
  methods: {
    fetchData () {
      this.$axios
        .post('http://127.0.0.1:8088/QuizJavaAPI_war/statistics/lottery', { date: this.date })
        .then(res => {
          const body = res.data
          if (body.code === 200) {
            const vo = body.object
            this.summary        = vo.summary
            this.companyWinners = vo.companyWinners
            this.userWinners    = vo.userWinners
            this.$nextTick(() => {
              this.renderChart()
            })
          } else {
            this.$message.error(body.message)
          }
        })
    },
    renderChart () {
      const chart = this.$echarts.init(this.$refs.winnerChart)
      chart.setOption({
        title: {
          text: '各公司中奖人数'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: this.companyWinners.map(i => i.company)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'bar',
            data: this.companyWinners.map(i => i.count)
          }
        ]
      })
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
}
.block {
  margin-bottom: 20px;
}
.margin-top {
  margin-top: 20px;
}
.chart-container {
  width: 100%;
  height: 300px;
}
</style>
