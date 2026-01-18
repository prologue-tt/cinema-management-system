<template>
  <div class="bill-page">
    <!-- 顶部导航 -->
    <div class="bill-header">
      <button @click="$router.go(-1)" class="back-btn">
        <i class="iconfont icon-r-left"></i> 返回
      </button>
      <h1>订单详情</h1>
      <div class="empty-space"></div>
    </div>

    <div class="bill-container">
      <!-- 订单状态卡片 -->
      <div class="status-card"
           :class="{
             'pending': payState === false && cancelState === false && (minutes > 0 || seconds > 0),
             'expired': payState === false && cancelState === true && cancelTime === null && minutes <= 0 && seconds <= 0,
             'completed': payState === true,
             'cancelled': cancelState === true && cancelTime !== null
           }">
        <div class="status-icon">
          <template v-if="payState === false && cancelState === false && (minutes > 0 || seconds > 0)">
            <img src="../../assets/money.png" alt="待支付图标">
          </template>
          <template v-if="payState === false && cancelState === true && cancelTime === null && minutes <= 0 && seconds <= 0">
            <img src="../../assets/bill-invalid.png" alt="已失效图标">
          </template>
          <template v-if="payState === true">
            <i class="iconfont icon-r-yes"></i>
          </template>
          <template v-if="cancelState === true && cancelTime !== null">
            <i class="iconfont icon-r-delete"></i>
          </template>
        </div>
        <div class="status-info">
          <div class="status-text">
            <template v-if="payState === false && cancelState === false && (minutes > 0 || seconds > 0)">待支付</template>
            <template v-if="payState === false && cancelState === true && cancelTime === null && minutes <= 0 && seconds <= 0">由于订单超时未支付，已失效</template>
            <template v-if="payState === true">已完成</template>
            <template v-if="cancelState === true && cancelTime !== null">订单已取消</template>
          </div>
          <div class="status-desc" v-if="payState === false && cancelState === false && (minutes > 0 || seconds > 0)">
            还有<span class="countdown">{{minutes}}:{{seconds | formatTime}}</span> 将自动取消该订单（注意，本电影院不支持退票改签服务~）
          </div>
        </div>
      </div>

      <!-- 订单信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>订单信息</h2>
          <span class="order-number">订单编号: {{billInfo.billId}}</span>
        </div>

        <div class="movie-info">
          <div class="movie-poster">
            <img :src="billInfo.session.movie.moviePoster || 'https://picsum.photos/200/300'"
                 alt="电影海报">
          </div>
          <div class="movie-details">
            <h3 class="movie-title">《{{billInfo.session.movie.movieName}}》</h3>
            <div class="movie-meta">
              <div class="meta-item">语言版本: {{billInfo.session.languageVersion}}</div>
              <div class="meta-item">播放时间: {{billInfo.session.sessionDate}} {{billInfo.session.playTime}} - {{billInfo.session.endTime}}</div>
              <div class="meta-item">影厅: {{billInfo.session.hall.hallName}}</div>
              <div class="meta-item">
                座位:
                <div class="seats-container">
                  <span class="seat-item" v-for="item in billSeats" :key="item">{{item}}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 影院信息卡片 -->
      <div class="info-card cinema-card">
        <div class="card-header">
          <h2>影院信息</h2>
        </div>
        <div class="cinema-details">
          <h3 class="cinema-name">{{billInfo.session.hall.cinema.cinemaName}}</h3>
          <div class="cinema-address">
            <i class="iconfont icon-location"></i>
            {{billInfo.session.hall.cinema.cinemaAddress}}
          </div>
          <div class="cinema-contact">
            <i class="iconfont icon-phone"></i>
            {{billInfo.session.hall.cinema.cinemaPhone}}
          </div>
          <div class="customer-service">
            <span>有订单问题可拨打客服电话</span>
            <span class="service-phone">{{billInfo.session.hall.cinema.cinemaPhone}}</span>
            <span>，工作时间: {{billInfo.session.hall.cinema.workStartTime}}-{{billInfo.session.hall.cinema.workEndTime}}</span>
          </div>
        </div>
      </div>

      <!-- 价格和操作区 -->
      <div class="action-bar">
        <div class="total-price">
          总价: <span class="price-amount">¥{{(billInfo.session.sessionPrice * billSeats.length).toFixed(1)}}</span>
        </div>
        <div class="action-buttons" v-if="payState === false && cancelState === false && (minutes > 0 || seconds > 0)">
          <el-button @click="cancelForBill" class="cancel-btn" round>
            <i class="iconfont icon-r-delete"></i> 取消订单
          </el-button>
          <el-button @click="payForBill" class="pay-btn" round>
            <i class="iconfont icon-r-yes"></i> 立即支付
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import defaultPoster from "@/assets/default-poster.png";
export default {
  name: "BillDetail",
  filters: {
    formatTime(value) {
      return value < 10 ? '0' + value : value
    }
  },
  data() {
    return {
      billId: this.$route.params.billId,
      billInfo: {
        session: {
          movie: {},
          hall: {
            cinema: {
              user: {}
            }
          }
        }
      },
      billSeats: [],
      payState: null,
      cancelState: null,
      cancelTime: null,
      // 支付时间延长到30分钟
      minutes: 30,
      seconds: 0,
    }
  },
  created() {
    this.getBillInfo()
  },
  mounted() {
    this.add()
  },
  methods: {
    async getBillInfo() {
      const { data : res } = await axios.get('sysBill/' + this.billId)
      if(res.code !== 200) return this.$message.error('获取信息失败')
      let userId = JSON.parse(window.sessionStorage.getItem('loginUser')).userId
      if (userId !== res.data.userId) {
        this.$alert('非法操作！操作非本人订单。', '非法操作订单警告', {
          confirmButtonText: '我知道了',
          callback: action => {
            this.$router.push('/bill')
          }
        })
        return
      }
      this.billInfo = res.data

      const posterArr = JSON.parse(this.billInfo.session.movie.moviePoster || '[]');
      this.billInfo.session.movie.moviePoster = posterArr.length
        ? this.global.base + posterArr[0]
        : defaultPoster;

      this.cancelTime = this.billInfo.cancelTime
      // 处理订单座位信息
      this.billSeats = JSON.parse(this.billInfo.seats)
      this.payState = this.billInfo.payState
      this.cancelState = this.billInfo.cancelState
      this.computeLeftTime()
    },
    async payForBill() {
      // 更新订单状态
      this.billInfo.payState = true
      // 更新订单信息和场次座位信息
      axios.defaults.headers.put['Content-Type'] = 'application/json'
      const { data: res} = await axios.put('sysBill', JSON.stringify(this.billInfo))
      if(res.code !== 200) return this.$message.error('支付失败')
      this.payState = true
      this.$alert('支付成功！', '支付通知', {
        confirmButtonText: '我知道了',
        callback: action => {
          this.$router.push('/bill')
        }
      })
    },
    async cancelForBill() {
      // 更新订单状态
      this.billInfo.cancelState = true
      this.billInfo.cancelTime = moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
      // 获取场次座位信息
      const { data : curSession } = await axios.get('sysSession/find/' + this.billInfo.sessionId)
      let sessionSeats = JSON.parse(curSession.data.sessionSeats)
      // 解析出订单选择的座位，更新座位信息
      for(let seat of this.billSeats){
        let row = seat.substring(0, seat.indexOf('排'))
        let col = Number.parseInt(seat.substring(seat.indexOf('排') + 1, seat.length - 1))
        sessionSeats[row][col - 1] = 0
      }
      // 更新订单信息和场次座位信息
      axios.defaults.headers.put['Content-Type'] = 'application/json'
      const { data: res } = await axios.put('sysBill/cancel',JSON.stringify({ticket: this.billInfo, sessionSeats: JSON.stringify(sessionSeats)}))
      if(res.code !== 200) return this.$message.error('取消失败')

      this.payState = false
      this.cancelState = true
      this.$alert('取消成功！', '取消通知', {
        confirmButtonText: '我知道了',
        callback: action => {
          this.$router.push('/billDetail/' + this.billInfo.billId)
        }
      })
    },
    computeLeftTime() {
      let cur = new Date()
      let createTime = new Date(this.billInfo.createTime)
      // 支付时间延长到30分钟
      let diff = 30 * 60 - (cur - createTime) / 1000
      if (diff < 0) {
        this.minutes = 0
        this.seconds = 0
      } else {
        this.minutes = Math.floor(diff / 60)
        this.seconds = Math.floor(diff % 60)
      }
    },
    // 计时走动
    add: function () {
      let _this = this
      let time = window.setInterval(function () {
        if (_this.seconds === 0 && _this.minutes !== 0) {
          _this.seconds = 59
          _this.minutes -= 1
        } else if (_this.minutes === 0 && _this.seconds === 0) {
          _this.seconds = 0
          window.clearInterval(time)
          // 倒计时结束，检查是否需要自动取消订单并释放座位
          _this.handleOrderTimeout()
        } else {
          _this.seconds -= 1
        }
      }, 1000)
    },

    // 处理订单超时
    async handleOrderTimeout() {
      // 只有未支付且未取消的订单才需要自动处理
      if (!this.payState && !this.cancelState) {
        try {
          // 更新订单状态
          this.billInfo.cancelState = true
          this.billInfo.cancelTime = moment(new Date()).format('YYYY-MM-DD HH:mm:ss')

          // 获取场次座位信息
          const { data : curSession } = await axios.get('sysSession/find/' + this.billInfo.sessionId)
          let sessionSeats = JSON.parse(curSession.data.sessionSeats)

          // 解析出订单选择的座位，更新座位信息
          for(let seat of this.billSeats){
            let row = seat.substring(0, seat.indexOf('排'))
            let col = Number.parseInt(seat.substring(seat.indexOf('排') + 1, seat.length - 1))
            sessionSeats[row][col - 1] = 0
          }

          // 更新订单信息和场次座位信息
          axios.defaults.headers.put['Content-Type'] = 'application/json'
          const { data: res } = await axios.put('sysBill/cancel',JSON.stringify({
            ticket: this.billInfo,
            sessionSeats: JSON.stringify(sessionSeats)
          }))

          if(res.code === 200) {
            this.cancelState = true
            this.$message.success('订单已超时，已自动取消并释放座位')
          } else {
            this.$message.error('自动取消订单失败，请联系客服')
          }
        } catch (error) {
          console.error('处理超时订单失败:', error)
          this.$message.error('处理超时订单失败，请联系客服')
        }
      }
    }
  }
}
</script>

<style scoped>
.bill-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 120px;
}

.bill-header {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  background: none;
  border: none;
  display: flex;
  align-items: center;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  padding: 5px 10px;
}

.back-btn .iconfont {
  margin-right: 6px;
}

.bill-header h1 {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

.empty-space {
  width: 50px;
}

.bill-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 15px;
}

.status-card {
  background-color: #fff8e6;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  border-left: 4px solid #ff9800;
  transition: all 0.3s ease;
}

.status-card.pending {
  background-color: #fff8e6;
  border-left-color: #ff9800;
}

.status-card.expired,
.status-card.cancelled {
  background-color: #fff0f0;
  border-left-color: #f5222d;
}

.status-card.completed {
  background-color: #f0fff4;
  border-left-color: #52c41a;
}

.status-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.status-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.status-icon .iconfont {
  font-size: 32px;
}

.status-card.completed .status-icon .iconfont {
  color: #52c41a;
}

.status-card.expired .status-icon .iconfont,
.status-card.cancelled .status-icon .iconfont {
  color: #f5222d;
}

.status-info {
  flex: 1;
}

.status-text {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 4px;
}

.status-card.pending .status-text {
  color: #e67700;
}

.status-card.completed .status-text {
  color: #52c41a;
}

.status-card.expired .status-text,
.status-card.cancelled .status-text {
  color: #f5222d;
}

.status-desc {
  font-size: 14px;
  color: #666;
}

.countdown {
  color: #e67700;
  font-weight: 500;
  font-size: 15px;
  margin: 0 4px;
}

.info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 15px;
  position: relative;
}

.card-header h2 {
  font-size: 17px;
  font-weight: 500;
  margin: 0;
  color: #1a1a1a;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.order-number {
  position: absolute;
  right: 0;
  top: 0;
  font-size: 13px;
  color: #888;
}

.movie-info {
  display: flex;
}

.movie-poster {
  width: 100px;
  height: 145px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.movie-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.movie-details {
  flex: 1;
  margin-left: 15px;
}

.movie-title {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 12px 0;
  color: #1a1a1a;
}

.movie-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.seats-container {
  display: inline-flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
}

.seat-item {
  background-color: #f5f5f5;
  color: #555;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 13px;
}

.cinema-card .card-header {
  margin-bottom: 12px;
}

.cinema-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 12px 0;
  color: #1a1a1a;
}

.cinema-address,
.cinema-contact {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.cinema-address .iconfont,
.cinema-contact .iconfont {
  margin-right: 6px;
  color: #888;
  font-size: 15px;
}

.customer-service {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #f0f0f0;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.service-phone {
  color: #1890ff;
  margin: 0 4px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  max-width: 600px;
  margin: 0 auto;
}

.total-price {
  font-size: 16px;
  color: #333;
}

.price-amount {
  font-size: 20px;
  font-weight: 500;
  color: #f5222d;
  margin-left: 5px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #555;
  border-color: #f5f5f5;
  padding: 8px 20px;
  font-size: 15px;
}

.pay-btn {
  background-color: #1890ff;
  border-color: #1890ff;
  color: #fff;
  padding: 8px 28px;
  font-size: 15px;
}

.pay-btn:hover {
  background-color: #096dd9;
  border-color: #096dd9;
  color: #fff;
}

/* 动画效果 */
.status-card, .info-card {
  transform: translateY(0);
  opacity: 1;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    transform: translateY(10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.seat-item {
  transition: all 0.2s ease;
}

.seat-item:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

.action-buttons .el-button {
  transition: all 0.2s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
}

.action-buttons .el-button:active {
  transform: translateY(0);
}
</style>
