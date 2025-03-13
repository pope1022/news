<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>总用户数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.userCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>新闻总数</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.newsCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>商品总数</span>
              <el-icon><Goods /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.productCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>订单总数</span>
              <el-icon><List /></el-icon>
            </div>
          </template>
          <div class="card-value">{{ statistics.orderCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近7天新闻发布趋势</span>
            </div>
          </template>
          <div class="chart" ref="newsChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近7天订单趋势</span>
            </div>
          </template>
          <div class="chart" ref="orderChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="table-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新新闻</span>
              <el-button text type="primary" @click="$router.push('/admin/news')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="latestNews" stripe style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="categoryId" label="分类" width="120">
              <template #default="{ row }">
                <span>{{ getCategoryName(row?.categoryId) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="发布时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
              <el-button text type="primary" @click="$router.push('/admin/orders')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="latestOrders" stripe style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="180" />
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Document, Goods, List } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const statistics = ref({
  userCount: 0,
  newsCount: 0,
  productCount: 0,
  orderCount: 0
})

const latestNews = ref([])
const latestOrders = ref([])
const newsChartRef = ref()
const orderChartRef = ref()
const categories = ref([])

const getOrderStatusType = (status: string) => {
  const map: Record<string, string> = {
    '待支付': 'warning',
    '已支付': 'success',
    '已发货': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

const initCharts = () => {
  const newsChart = echarts.init(newsChartRef.value)
  const orderChart = echarts.init(orderChartRef.value)

  // 配置新闻趋势图表
  newsChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [10, 15, 8, 20, 12, 18, 25],
      type: 'line',
      smooth: true
    }]
  })

  // 配置订单趋势图表
  orderChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [5, 8, 12, 6, 15, 10, 20],
      type: 'line',
      smooth: true
    }]
  })

  window.addEventListener('resize', () => {
    newsChart.resize()
    orderChart.resize()
  })
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await fetch('/api/admin/categories')
    const data = await response.json()
    if (data.code === 200) {
      categories.value = data.data.records
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId);
  return category ? category.name : '未知分类';
};

onMounted(async () => {
  loadCategories()
  try {
    // 获取统计数据
    const statsResponse = await fetch('/api/admin/statistics')
    const statsData = await statsResponse.json()
    if (statsData.code === 200) {
      statistics.value = statsData.data
    }

    // 获取最新新闻
    const newsResponse = await fetch('/api/admin/news/latest')
    const newsData = await newsResponse.json()
    if (newsData.code === 200) {
      latestNews.value = newsData.data.records
    }

    // 获取最新订单
    const ordersResponse = await fetch('/api/admin/orders/latest')
    const ordersData = await ordersResponse.json()
    if (ordersData.code === 200) {
      latestOrders.value = ordersData.data.records
    }

    // 初始化图表
    initCharts()
  } catch (error) {
    console.error('获取数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  margin-top: 10px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.table-row {
  margin-bottom: 20px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  font-weight: bold;
}
</style> 