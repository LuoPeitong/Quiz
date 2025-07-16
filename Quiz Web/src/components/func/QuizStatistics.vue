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

    <!-- 2. 总答题人数 -->
    <el-descriptions class="margin-top" title="统计信息" :column="1" border>
      <el-descriptions-item label="总答题人数">
        {{ summary.total }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 3. 各公司答题人数柱状图 -->
    <el-card class="margin-top">
      <div ref="companyChart" class="chart-container"></div>
    </el-card>

    <!-- 4. 各公司答题人数列表 -->
    <el-table :data="companyStats" stripe border class="margin-top">
      <el-table-column prop="company" label="公司" />
      <el-table-column prop="count"   label="人数" />
    </el-table>

    <!-- 5. 每个人答题详情 -->
    <el-table :data="userStats" stripe border class="margin-top">
      <el-table-column prop="company"     label="公司" />
      <el-table-column prop="nickname"    label="姓名" />
      <el-table-column prop="score"       label="得分" />
      <el-table-column prop="time_used"   label="用时（秒）" />
      <el-table-column prop="submit_time" label="提交时间" />
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'QuizStatistics',
  data () {
    return {
      date: new Date().toISOString().slice(0, 10),
      pickerOptions: {
        disabledDate (t) { return t.getTime() > Date.now() },
        shortcuts: [{
          text: '今天',
          onClick (p) { p.$emit('pick', new Date()) }
        }]
      },
      summary: { total: 0 },
      companyStats: [],
      userStats: []
    }
  },
  mounted () {
    this.fetchData()
  },
  methods: {
    fetchData () {
      this.$axios
        .post('/http://127.0.0.1:8088/QuizJavaAPI_war/statistics/quiz', { date: this.date })
        .then(res => {
          const body = res.data
          if (body.code === 200) {
            const vo = body.object
            this.summary      = vo.summary
            this.companyStats = vo.companyStats
            this.userStats    = vo.userStats
            this.$nextTick(this.renderChart)
          } else {
            this.$message.error(body.message)
          }
        })
    },
    renderChart () {
      const chart = this.$echarts.init(this.$refs.companyChart)
      chart.setOption({
        title:  { text: '各公司答题人数' },
        tooltip:{},
        xAxis:  { type: 'category', data: this.companyStats.map(i => i.company) },
        yAxis:  { type: 'value' },
        series: [{
          type: 'bar',
          data: this.companyStats.map(i => i.count)
        }]
      })
    }
  }
}
</script>

<style scoped>
.container { padding: 20px; }
.block     { margin-bottom: 20px; }
.margin-top{ margin-top: 20px; }
.chart-container { width: 100%; height: 300px; }
</style>
