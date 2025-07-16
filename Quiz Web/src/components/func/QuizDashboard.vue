<template>
  <div class="quiz-dashboard">
    <!-- 1. 公司统计查询表单 -->
    <el-card class="box-card" shadow="hover" style="margin-bottom: 24px">
      <div slot="header">
        <span>公司员工统计</span>
      </div>
      <el-form :inline="true" :model="companyFilter" class="filter-form">
        <el-form-item label="公司名称">
          <el-select
            v-model="companyFilter.company"
            placeholder="请选择公司"
            filterable
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="comp in companyList"
              :key="comp"
              :label="comp"
              :value="comp"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchEmployeeStats">查询</el-button>
          <el-button @click="resetCompanyFilter">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="employeeStats"
        v-if="employeeStats.length"
        class="data-table"
        stripe
      >
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="participationCount" label="答题次数" width="120" />
        <el-table-column prop="firstPrizeCount" label="一等奖次数" width="120" />
        <el-table-column prop="secondPrizeCount" label="二等奖次数" width="120" />
        <el-table-column prop="thirdPrizeCount" label="三等奖次数" width="120" />
        <el-table-column prop="noPrizeCount" label="未中奖次数" width="120" />
      </el-table>
    </el-card>

    <!-- 2. 排名明细查询表单 -->
    <el-card class="box-card" shadow="hover">
      <div slot="header">
        <span>员工排名明细</span>
      </div>
      <el-form :inline="true" :model="rankFilter" class="filter-form">
        <el-form-item label="排序依据">
          <el-select
            v-model="rankFilter.sortField"
            placeholder="请选择排序依据"
            style="width: 240px"
          >
            <el-option label="答题次数" value="participationCount" />
            <el-option label="一等奖次数" value="firstPrizeCount" />
            <el-option label="二等奖次数" value="secondPrizeCount" />
            <el-option label="三等奖次数" value="thirdPrizeCount" />
            <el-option label="未中奖次数" value="noPrizeCount" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRankList">查询</el-button>
          <el-button @click="resetRankFilter">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="rankList"
        v-if="rankList.length"
        class="data-table"
        stripe
      >
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column prop="company" label="公司名称" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <!-- 新增：动态次数列 -->
        <el-table-column prop="count" :label="countLabel" width="120" />
      </el-table>
      <div class="pagination" v-if="total">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="rankFilter.pageSize"
          :current-page.sync="rankFilter.page"
          @current-change="fetchRankList"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'QuizDashboard',
  data() {
    return {
      // 公司统计查询
      companyFilter: { company: '' },
      employeeStats: [],
      companyList: [],
      // 排名明细查询
      rankFilter: { sortField: '', page: 1, pageSize: 10 },
      rankList: [],
      total: 0
    }
  },
  mounted() {
    this.loadCompanies()
  },
  computed: {
    countLabel() {
      const map = {
        participationCount: '答题次数',
        firstPrizeCount:    '一等奖次数',
        secondPrizeCount:   '二等奖次数',
        thirdPrizeCount:    '三等奖次数',
        noPrizeCount:       '未中奖次数'
      };
      return map[this.rankFilter.sortField] || '次数';
    }
  },
  methods: {
    // 加载公司列表
    async loadCompanies() {
      try {
        const res = await this.$axios.get('dashboard/getAllCompanies')
        this.companyList = res.data.object || []
      } catch (e) {
        console.error('获取公司列表失败', e)
      }
    },
    // 1. 查询该公司员工统计
    async searchEmployeeStats() {
      if (!this.companyFilter.company) {
        this.$message.warning('请选择公司')
        return
      }
      try {
        const res = await this.$axios.post('dashboard/getCompanyEmployeeStats', {
          company: this.companyFilter.company
        })
        this.employeeStats = res.data.object || []
      } catch (e) {
        console.error('员工统计获取失败', e)
      }
    },
    resetCompanyFilter() {
      this.companyFilter.company = ''
      this.employeeStats = []
    },
    // 2. 查询排名明细
    async searchRankList() {
      this.rankFilter.page = 1
      this.fetchRankList(this.rankFilter.page)
    },
    async fetchRankList(page) {
      try {
        const params = {
          sortField: this.rankFilter.sortField,
          page,
          pageSize: this.rankFilter.pageSize
        }
        const res = await this.$axios.post('dashboard/getEmployeeRankList', params)
        const obj = res.data.object
        if (obj && Array.isArray(obj.records)) {
          this.rankList = obj.records
          this.total = obj.total || 0
        } else {
          this.rankList = []
          this.total = 0
        }
      } catch (e) {
        console.error('排名明细获取失败', e)
      }
    },
    resetRankFilter() {
      this.rankFilter = { sortField: '', page: 1, pageSize: 10 }
      this.rankList = []
      this.total = 0
    }
  }
}
</script>

<style scoped>
.quiz-dashboard {
  padding: 16px;
}
.filter-form {
  margin-bottom: 16px;
}
.data-table {
  margin-top: 12px;
}
.pagination {
  text-align: right;
  margin-top: 12px;
}
</style>
