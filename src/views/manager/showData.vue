<template>
  <!-- 模板部分保持不变 -->
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>商品销量排行</span>
              <el-select v-model="quantitySize" @change="fetchQuantityData" style="width: 100px">
                <el-option
                  v-for="item in sizeOptions"
                  :key="item"
                  :label="`前${item}名`"
                  :value="item"
                />
              </el-select>
            </div>
          </template>
          <div class="chart-wrapper">
            <div ref="quantityBarChart" style="width: 100%; height: 400px"></div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>商品销售额排行</span>
              <el-select v-model="priceSize" @change="fetchPriceData" style="width: 100px">
                <el-option
                  v-for="item in sizeOptions"
                  :key="item"
                  :label="`前${item}名`"
                  :value="item"
                />
              </el-select>
            </div>
          </template>
          <div class="chart-wrapper">
            <div ref="pricePieChart" style="width: 100%; height: 400px"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { ref, onMounted } from 'vue'
import { getMostPrices, getMostQuantity } from '@/api/trade'

export default {
  name: 'SalesDashboard',
  setup() {
    const quantityBarChart = ref(null)
    const pricePieChart = ref(null)
    const quantitySize = ref(5)
    const priceSize = ref(5)
    const sizeOptions = [3, 5, 10, 15, 20]
    
    let quantityChartInstance = null
    let priceChartInstance = null
    
    // 初始化图表
    const initCharts = () => {
      quantityChartInstance = echarts.init(quantityBarChart.value)
      priceChartInstance = echarts.init(pricePieChart.value)
      
      window.addEventListener('resize', () => {
        quantityChartInstance.resize()
        priceChartInstance.resize()
      })
    }
    
    // 获取销量数据
    const fetchQuantityData = async () => {
      try {
        const response = await getMostQuantity(1, quantitySize.value)
        renderQuantityChart(response.data.list)
      } catch (error) {
        console.error('获取销量数据失败:', error)
      }
    }
    
    // 获取销售额数据
    const fetchPriceData = async () => {
      try {
        const response = await getMostPrices(1, priceSize.value)
        renderPriceChart(response.data.list)
      } catch (error) {
        console.error('获取销售额数据失败:', error)
      }
    }
    
    // 渲染销量柱状图（保持不变）
    const renderQuantityChart = (data) => {
      const option = {
        title: {
          text: '商品销量排行',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '销量',
          axisLabel: {
            formatter: '{value} 件'
          }
        },
        yAxis: {
          type: 'category',
          data: data.map(item => item.productName),
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        series: [
          {
            name: '销量',
            type: 'bar',
            data: data.map(item => item.totalQuantity),
            itemStyle: {
              color: function(params) {
                const colorList = ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE']
                return colorList[params.dataIndex % colorList.length]
              }
            },
            label: {
              show: true,
              position: 'right',
              formatter: '{c} 件'
            }
          }
        ]
      }
      quantityChartInstance.setOption(option)
    }
    
    // 渲染销售额饼图（保持不变）
    const renderPriceChart = (data) => {
      const option = {
        title: {
          text: '商品销售额排行',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: data.map(item => item.productName)
        },
        series: [
          {
            name: '销售额',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold',
                formatter: '{b}\n¥{c}'
              }
            },
            labelLine: {
              show: false
            },
            data: data.map(item => ({
              value: item.totalPrice,
              name: item.productName
            }))
          }
        ]
      }
      priceChartInstance.setOption(option)
    }
    
    onMounted(() => {
      initCharts()
      fetchQuantityData()
      fetchPriceData()
    })
    
    return {
      quantityBarChart,
      pricePieChart,
      quantitySize,
      priceSize,
      sizeOptions,
      fetchQuantityData,
      fetchPriceData
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.chart-card {
  margin-bottom: 20px;
  height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-wrapper {
  width: 100%;
  height: 100%;
}
</style>