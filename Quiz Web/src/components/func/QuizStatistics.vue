<template>
  <div class="quiz-monitoring">
    <!-- 饼图：当前选定日期下中奖分布 -->
    <div class="controls">
      <label for="datePicker">选择日期：</label>
      <input id="datePicker" type="date" v-model="selectedDate" @change="onDateChange" />
    </div>
    <div ref="pieChart" class="chart small-chart"></div>
    <!-- 折线图：按日期答题人数变化 -->
    <div ref="lineChart" class="chart"></div>
    <!-- 柱状图：参与人数前5公司答题次数 -->
    <div ref="barChart" class="chart"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'QuizStatistics',
  data() {
    return {
      participantData: [],      // [{ date, count }]
      winnerDistribution: [],   // [{ name, value }]
      companyStats: [],         // [{ company, count }]
      selectedDate: ''          // 日期选择
    }
  },
  mounted() {
    // 初始化选中日期为今天
    this.selectedDate = this.formatDate(new Date())
    this.getInfo()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.resizeCharts)
  },
  methods: {
    // 格式化日期
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return `${y}-${m}-${d}`
    },
    // 获取所有图表数据
    getInfo() {
      this.$axios.post('dashboard/getQuizStats', { date: this.selectedDate })
        .then(({ data }) => {
          if (data.code === 200) {
            this.participantData    = data.object.participantData.map(item => ({
              date: new Date(item.date)
                .toLocaleDateString('en-CA', { timeZone: 'Asia/Shanghai' }),
              count: item.count
            }))
            this.winnerDistribution = data.object.winnerDistribution
            this.companyStats       = data.object.companyStats
            this.renderCharts()
          } else {
            this.$message.warning(data.message)
          }
        })
        .catch(err => {
          console.error('数据获取失败', err)
        })
    },
    // 渲染或更新图表
    renderCharts() {
      this.renderLineChart()
      this.renderPieChart()
      this.renderBarChart()
    },
    renderLineChart() {
      const chart = echarts.init(this.$refs.lineChart)
      chart.setOption({
        title: { text: '答题人数变化（按日期）' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.participantData.map(i => i.date) },
        yAxis: { type: 'value', name: '人数' },
        series: [{ type: 'line', data: this.participantData.map(i => i.count), smooth: true }]
      })
    },
    renderPieChart() {
      const chart = echarts.init(this.$refs.pieChart)
      chart.setOption({
        title: { text: `中奖分布 (${this.selectedDate})`, left: 'center' },
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '中奖数', type: 'pie', radius: '50%',
          data: this.winnerDistribution,
          label: { formatter: '{b}: {c} ({d}%)' },
          emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.5)' } }
        }]
      })
    },
    renderBarChart() {
      const chart = echarts.init(this.$refs.barChart)
      chart.setOption({
        title: { text: '前5公司答题次数' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.companyStats.map(i => i.company), axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', name: '答题次数' },
        series: [{ type: 'bar', data: this.companyStats.map(i => i.count), barWidth: '50%' }]
      })
    },
    // 窗口大小改变时自适应
    resizeCharts() {
      [this.$refs.lineChart, this.$refs.pieChart, this.$refs.barChart].forEach(ref => {
        const chart = echarts.getInstanceByDom(this.$refs[ref])
        if (chart) chart.resize()
      })
    },
    // 选中日期改变时刷新饼图数据
    onDateChange() {
      this.getInfo()
    }
  }
}
</script>

<style scoped>
.container { padding: 20px; }
.block     { margin-bottom: 20px; }
.margin-top{ margin-top: 20px; }
.chart-container { width: 100%; height: 300px; }
.quiz-monitoring {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.chart {
  width: 100%;
  height: 350px;
}
.small-chart {
  max-width: 400px;
  height: 350px;
}
.controls {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
