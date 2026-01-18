<template>
  <!-- 唯一根元素：将所有内容包裹在一个div中 -->
  <div class="bill-list-container">
    <div class="profile-title">我的订单</div>

    <!-- 订单列表 -->
    <div
      class="order-box"
      v-for="item in billList"
      :key="item.billId"
      v-if="item?.session?.hall?.cinema && item?.session?.movie"
    >
      <div class="order-header">
        <span class="order-date">订单时间：{{ item.createTime || '暂无时间' }}</span>
        <span class="order-id">订单编号: {{ item.billId || '暂无编号' }}</span>
        <span class="del-order" @click="deleteBill(item.billId)">
          <i class="iconfont icon-r-delete"></i>
        </span>
      </div>
      <div class="order-body">
        <div class="poster">
          <img
            :src="item.session.movie.moviePoster || require('@/assets/default-poster.png')"
            class="poster-img"
            alt="电影海报"
          >
        </div>
        <div class="order-content">
          <div class="movie-name">《{{ item.session.movie.movieName || '未知电影' }}》</div>
          <div class="cinema-name">{{ item.session.hall.cinema?.cinemaName || '未知影院' }}</div>
          <div class="hall-ticket">
            <span>
              {{ item.session.hall.hallName || '未知厅' }}
              ({{ item.session.hall.hallCategory || '普通厅' }})
            </span>
          </div>
          <div class="show-time">
            播放时间：{{ (item.session.sessionDate || '') + ' ' + (item.session.playTime || '') + ' - ' + (item.session.endTime || '') }}
          </div>
        </div>
        <div class="order-price">
          ￥{{
            item.session.sessionPrice && item.seats
              ? (item.session.sessionPrice * JSON.parse(item.seats || '[]').length)
              : 0
          }}
          <div class="price-detail">
            ({{ item.seats ? JSON.parse(item.seats || '[]').length : 0 }}×{{ item.session.sessionPrice || 0 }}/张)
          </div>
        </div>
        <div class="order-status">
          <span :class="item.payState ? 'status-completed' : 'status-pending'">
            {{ item.payState ? '已完成' : '未完成' }}
          </span>
        </div>
        <div class="actions">
          <el-button
            @click="toBillDetail(item.billId)"
            class="detail-btn"
            icon="iconfont icon-r-find"
            style="margin-right:8px"
          >
            详情
          </el-button>
          <el-button
            v-if="item.payState === true && item.reviewState === false"
            @click="openReviewDialog(item)"
            class="review-btn"
            icon="iconfont icon-r-comment"
            style="margin-right:8px"
          >
            评价
          </el-button>
          <el-button
            v-if="item.payState === true && item.reviewState === true"
            class="view-review-btn"
            icon="iconfont icon-r-eye"
            disabled
          >
            已评价
          </el-button>
        </div>
      </div>
    </div>

    <!-- 评论对话框 -->
    <el-dialog
      title="请给本次观影体验评分"
      :visible.sync="reviewDialogVisible"
      width="40%"
      @close="handleDialogBeforeClose"
    >
      <div class="stars-container">
        <span class="star-label">您的评分：</span>
        <!-- 使用Element UI官方星星图标组件 -->
        <el-rate
          v-model="reviewForm.star"
          :max="5"
          :colors="['#999', '#999', '#f59e0b', '#f59e0b', '#f59e0b']"
          :size="30"
          @change="handleRatingChange"
        ></el-rate>
        <span class="rating-text">{{ ratingText }}</span>
      </div>

      <div class="comment-section">
        <h3 class="comment-title">请写下您的评价</h3>
        <textarea
          v-model="reviewForm.commentContent"
          class="comment-input"
          placeholder="分享您的观影感受，帮助更多人做出选择..."
          :maxlength="maxCommentLength"
          @input="updateCommentLength"
        ></textarea>
        <div class="comment-footer">
          <span class="char-count">{{ commentLength }}/{{ maxCommentLength }}</span>
          <el-button
            type="primary"
            class="submit-btn"
            @click="submitReview"
            :disabled="!reviewForm.star || !reviewForm.commentContent.trim()"
          >
            提交评价
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 空状态提示 -->
    <div class="empty-state" v-if="isBillListEmpty">
      <div class="empty-icon">🎫</div>
      <p class="empty-text">暂无订单记录哦~</p>
    </div>
  </div>
</template>

<script>
import moment from 'moment'
import billDetail from '../pay/BillDetail'
import defaultPoster from '@/assets/default-poster.png'

export default {
  name: "BillInfo",
  computed: {
    isBillListEmpty() {
      return (
        !this.billList ||
        this.billList.length === 0 ||
        (this.billList.length === 1 && !this.billList[0].billId)
      );
    },
    ratingText() {
      const texts = ['请评分', '很差', '一般', '良好', '很好', '非常好'];
      return texts[this.reviewForm.star || 0];
    }
  },
  data() {
    return {
      queryInfo: {
        userId: ''
      },
      billList: [],
      reviewDialogVisible: false,
      maxCommentLength: 200,
      commentLength: 0,
      reviewForm: {
        star: null,
        commentContent: '',
        commentTime: '',
        billId: null,
        movieId: null,
        userId: null
      }
    }
  },
  created() {
    this.getUser();
    this.getBillList();
  },
  mounted() {
    this.commentLength = this.reviewForm.commentContent.length;
  },
  methods: {
    getUser() {
      try {
        const loginUser = JSON.parse(window.sessionStorage.getItem('loginUser'));
        if (!loginUser?.userId) throw new Error('用户未登录');
        this.queryInfo.userId = loginUser.userId;
        this.reviewForm.userId = loginUser.userId;
      } catch (error) {
        console.error('获取登录用户失败：', error);
        this.$message.error('请先登录');
        this.$router.push('/login');
      }
    },
    async getBillList() {
      try {
        const response = await axios.get('sysBill', { params: this.queryInfo });
        this.billList = response.data.data || [];

        this.billList.forEach(item => {
          if (item.session) {
            item.session.sessionDate = item.session.sessionDate
              ? moment(item.session.sessionDate).format('YYYY年MM月DD日')
              : '';
            try {
              const posterArr = JSON.parse(item.session.movie.moviePoster || '[]');
              item.session.movie.moviePoster = posterArr.length
                ? this.global.base + posterArr[0]
                : defaultPoster;
            } catch (error) {
              item.session.movie.moviePoster = defaultPoster;
            }
          }
          item.reviewState = item.reviewState ?? false;
        });
      } catch (error) {
        console.error('获取订单列表失败：', error);
        this.$message.error('加载订单失败，请重试');
        this.billList = [];
      }
    },
    toBillDetail(id) {
      if (id) this.$router.push(`/billDetail/${id}`);
    },
    async deleteBill(id) {
      if (!id) return;

      try {
        const confirmResult = await this.$confirm(
          '此操作将永久删除订单信息,是否继续?',
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        );

        if (confirmResult !== 'confirm') {
          return this.$message.info('已取消删除');
        }

        const response = await axios.delete(`sysBill/${id}`);
        if (response.data.code === 200) {
          this.$message.success('删除成功！');
          await this.getBillList();
        } else {
          this.$message.error('删除失败！');
        }
      } catch (error) {
        console.error('删除订单失败：', error);
        this.$message.error('删除失败，请重试');
      }
    },
    openReviewDialog(item) {
      this.reviewDialogVisible = true;
      this.reviewForm = {
        star: null,
        commentContent: '',
        commentTime: '',
        billId: item.billId,
        movieId: item.session.movie.movieId,
        userId: this.queryInfo.userId
      };
      this.commentLength = 0;
    },
    updateCommentLength() {
      this.commentLength = this.reviewForm.commentContent.length;
    },
    async submitReview() {
      if (!this.reviewForm.star || this.reviewForm.star < 1 || this.reviewForm.star > 5) {
        return this.$message.warning('请选择1-5星评分');
      }
      if (!this.reviewForm.commentContent.trim()) {
        return this.$message.warning('请输入评价内容');
      }

      try {
        this.reviewForm.commentTime = moment().format('YYYY-MM-DD HH:mm:ss');
        const response = await axios.post('comment', this.reviewForm);

        if (response.data.code === 200) {
          this.$message.success('评价提交成功，感谢您的反馈！');
          this.reviewDialogVisible = false;
          await this.getBillList();
        } else {
          this.$message.error(`提交失败: ${response.data.msg || '未知错误'}`);
        }
      } catch (error) {
        console.error('提交评价失败：', error);
        if (error.response) {
          this.$message.error(`网络错误: ${error.response.status} - ${error.response.statusText}`);
        } else {
          this.$message.error('提交评价失败，请检查网络');
        }
      }
    },
    resetReviewForm() {
      this.reviewForm = {
        star: null,
        commentContent: '',
        commentTime: '',
        billId: null,
        movieId: null,
        userId: this.queryInfo.userId
      };
      this.commentLength = 0;
    },
    handleDialogBeforeClose() {
      this.resetReviewForm();
    },
    handleRatingChange(value) {
      // 这个方法必须存在以匹配模板中的@change="handleRatingChange"
      this.reviewForm.star = value; // 同步评分值到表单
      // 可以添加其他逻辑，如console.log('评分改变为:', value)
    },
  }
}
</script>

<style scoped>
.bill-list-container{
  min-height: 900px;
  padding: 20px 30px;
  background-color: #fff5f7;
}

.profile-title {
  padding: 20px 0;
  color: #f87171;
  font-size: 22px;
  font-weight: 600;
  border-bottom: 1px solid #fecdd3;
  margin-bottom: 25px;
  position: relative;
}

.profile-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -1px;
  width: 60px;
  height: 3px;
  background-color: #fda4af;
}

.order-box {
  border: 1px solid #fecdd3;
  border-radius: 12px;
  margin: 0 0 20px 0;
  background-color: white;
  box-shadow: 0 4px 12px rgba(254, 205, 211, 0.15);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.order-box:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(254, 205, 211, 0.2);
}

.order-header {
  background-color: #fff1f2;
  font-size: 14px;
  padding: 14px 20px;
  border-radius: 12px 12px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-header .order-date {
  color: #d946ef;
  display: inline-block;
  margin-right: 30px;
  font-weight: 500;
}

.order-header .order-id {
  color: #a855f7;
}

.del-order{
  color: #f43f5e;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px;
  border-radius: 50%;
}

.del-order:hover {
  background-color: #fee2e2;
  transform: scale(1.1);
}
.del-order .icon-r-delete {
  font-size: 20px;
}

.order-body {
  padding: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.poster {
  width: 72px;
  height: 104px;
  margin-right: 16px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 3px 8px rgba(252, 165, 165, 0.2);
  flex-shrink: 0;
}

.poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.poster:hover .poster-img {
  transform: scale(1.05);
}

.order-content{
  width: 45%;
  min-width: 250px;
}

.movie-name{
  font-size: 17px;
  font-weight: 600;
  color: #c026d3;
  margin: 0 0 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cinema-name, .hall-ticket {
  font-size: 13px;
  color: #7c3aed;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.show-time {
  font-size: 13px;
  color: #ec4899;
  margin-top: 8px;
  font-weight: 500;
}

.order-price {
  font-size: 16px;
  color: #db2777;
  width: 15%;
  text-align: center;
  line-height: 1.4;
  font-weight: 600;
  min-width: 100px;
}

.price-detail {
  font-size: 12px;
  color: #f472b6;
  margin-top: 4px;
  font-weight: normal;
}

.order-status {
  font-size: 14px;
  width: 12%;
  text-align: center;
  line-height: 95px;
  min-width: 80px;
}

.status-completed {
  color: #10b981;
  background-color: #dcfce7;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-pending {
  color: #f59e0b;
  background-color: #fef3c7;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.actions{
  font-size: 40px;
  width: 15%;
  text-align: center;
  min-width:100px;
}

.detail-btn {
  background-color: #f472b6;
  color: white;
  border-color: #f472b6;
  font-size: 14px;
  padding: 6px 15px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.detail-btn:hover {
  background-color: #ec4899;
  border-color: #ec4899;
  transform: translateY(-2px);
}

.review-btn {
  background-color: #f6de3d;
  color: #fa99d8;
  border-color: #f6de3d;
  font-size: 14px;
  padding: 6px 15px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.review-btn:hover {
  background-color: #f6de3d;
  border-color: #f6de3d;
  transform: translateY(-2px);
}

.view-review-btn {
  background-color: #ccc;
  color: white;
  border-color: #ccc;
  font-size: 14px;
  padding: 6px 15px;
  border-radius: 20px;
}

.empty-state {
  text-align: center;
  padding: 100px 0;
  color: #f472b6;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 18px;
  font-weight: 500;
}

.stars-container {
  display: flex;
  align-items: center;
  margin: 20px 0;
  padding: 10px 0;
  gap: 15px; /* 增加元素间距 */
}

.star-label {
  font-size: 16px;
  color:#333;
  white-space: nowrap; /* 防止标签换行 margin-right: 15px;*/
}
/* 调整Element UI评分组件的位置 */
::v-deep .el-rate {
  margin: 0; /* 去除默认margin */
}
.el-icon-star {
  font-size: 28px;
  color: #ddd;
  margin-right: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.el-icon-star.active {
  color: #f59e0b;
  transform: scale(1.1);
}

.rating-text {
  margin-left: 16px;
  font-size: 18px;
  color: #666;
  font-weight: 500;
}

.comment-section {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #f5f5f5;
}

.comment-title {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 500;
}

.comment-input {
  width: 100%;
  min-height: 120px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  line-height: 1.5;
  box-sizing: border-box;
}

.comment-input:focus {
  outline: none;
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.char-count {
  font-size: 14px;
  color: #999;
}

.submit-btn {
  padding: 8px 20px;
}



  /* 响应式调整 */
  @media (max-width: 768px) {


    .stars-container {
      flex-wrap: wrap; /* 小屏幕允许换行 */
    }

    .order-body {
      padding: 15px;
    }

    .order-content, .order-price, .order-status, .actions {
      width: 100%;
      margin-bottom: 15px;
      line-height: normal;
      text-align: left;
    }

    .order-status {
      order: 3;
    }

    .order-price {
      order: 2;
      padding-top: 10px;
      border-top: 1px dashed #fecdd3;
    }

    .actions {
      order: 4;
      justify-content: flex-end;
    }
    .rating-text {
      width: 100%;
      margin-top: 10px;
    }
  }

</style>
