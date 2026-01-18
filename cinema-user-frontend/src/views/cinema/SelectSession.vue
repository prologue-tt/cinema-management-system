<template>
  <div>
    <!-- 头部信息 -->
    <div class="header">
      <div class="header-inner">
        <div class="movie-info-left">
          <div class="avatar-shadow">
            <img class="avatar" :src="selectSession.moviePoster" :alt="selectSession.movieName">
          </div>
        </div>
        <div class="movie-info-right">
          <div class="movie-info-msg">
            <h1 class="movie-name">{{selectSession.movieName}}</h1>
            <ul>
              <li>{{selectSession.movieCategoryList}}</li>
              <li>{{selectSession.movieArea}} / {{selectSession.movieLength}}分钟</li>
              <li>{{selectSession.releaseDate}} {{selectSession.movieArea}} 上映</li>
            </ul>
          </div>
          <div class="movie-info-actions">
            <div class="movie-info-btn">
              <el-button class="buy-btn" type="primary" @click="toMovieInfo">电影详情</el-button>
            </div>
            <div class="movie-info-score">
              <div class="movie-index box-office-container">
                <span class="movie-index-title">累计票房</span>
                <div style="display: flex;align-items: flex-end;">
                  <span class="box-office">{{selectSession.movieBoxOffice}}</span>
                  <span class="unit">元</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 影院和场次信息 -->
    <div class="movie-info-detail-container">
      <div class="movie-info-detail">
        <div class="main-content">
          <div class="crumbs-nav-container">
            <a href="/welcome">电影院</a> &gt;
            <a href="/aboutUs">{{ cinemaInfo.cinemaName }}</a>
          </div>

          <!-- 影院信息 -->
          <div class="cinema-info" v-if="cinemaInfo.cinemaName">
            <h2>{{ cinemaInfo.cinemaName }}</h2>
            <p>{{ cinemaInfo.cinemaAddress }}</p>
          </div>

          <!-- 日期选择 -->
          <div class="show-date" v-if="dateList.length > 0">
            <span>观影时间 :</span>
            <span
              class="date-item"
              :class="{ active: activeDate === date }"
              v-for="(date, index) in dateList"
              :key="index"
              @click="activeDate = date"
            >
              {{ formatDateForDisplay(date) }}
            </span>
          </div>

          <!-- 场次列表 -->
          <div class="session-info" v-if="hasSessionsForActiveDate">
            <el-table :data="sessionDict[activeDate]" stripe style="width: 100%">
              <el-table-column label="放映时间" width="180">
                <template slot-scope="scope">
                  <span class="begin-time">{{ scope.row.playTime }}</span>
                  <br />
                  <span class="end-time">{{ calculateEndTime(scope.row.playTime, selectSession.movieLength) }}散场</span>
                </template>
              </el-table-column>
              <el-table-column prop="languageVersion" label="语言版本"></el-table-column>
              <el-table-column label="放映厅">
                <template slot-scope="scope">
                  {{ scope.row.hall ? scope.row.hall.hallName : '未知影厅' }}
                </template>
              </el-table-column>
              <el-table-column label="售价">
                <template slot-scope="scope">
                  <span class="sell-price"><span class="stonefont">{{ scope.row.sessionPrice }}</span></span>
                </template>
              </el-table-column>
              <el-table-column label="选座购票" width="180">
                <template slot-scope="scope">
                  <el-button
                    type="danger"
                    round
                    @click="toChooseSeat(scope.row.sessionId)"
                    :disabled="isSessionExpired(scope.row.playTime, activeDate)"
                    :class="{ 'disabled-btn': isSessionExpired(scope.row.playTime, activeDate) }"
                  >
                    <i class="iconfont icon-r-yes"></i>
                    {{ isSessionExpired(scope.row.playTime, activeDate) ? '已截止' : '选座购票' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 无场次提示 -->
          <div class="empty-tip" v-if="shouldShowEmptyTip">
            暂无排片信息
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import moment from 'moment';

export default {
  name: "SelectSession",
  data() {
    return {
      cinemaId: this.$route.params.cinemaId,
      movieId: this.$route.params.movieId, // 从路由获取movieId
      cinemaInfo: {},
      selectSession: {
        movieLength: 120, // 默认值，防止未加载时出错
        moviePoster: '',
        movieName: '',
        movieCategoryList: '',
        movieArea: '',
        releaseDate: '',
        movieBoxOffice: '0'
      },
      sessions: [],
      sessionDict: {}, // 按日期分组的场次
      activeDate: '',
      dateList: [], // 日期列表，用于显示和选择
      httpURL: this.global.base,
      timeRefreshInterval: null // 定时器ID
    };
  },
  created() {
    this.fetchCinemaData();
    // 添加实时刷新，每分钟更新一次按钮状态
    this.timeRefreshInterval = setInterval(() => {
      this.$forceUpdate();
    }, 60000);
  },
  beforeDestroy() {
    // 清除定时器
    if (this.timeRefreshInterval) {
      clearInterval(this.timeRefreshInterval);
    }
  },
  computed: {
    hasSessionsForActiveDate() {
      return this.activeDate && this.sessionDict[this.activeDate] && this.sessionDict[this.activeDate].length > 0;
    },
    shouldShowEmptyTip() {
      if (Object.keys(this.sessionDict).length === 0) return true;
      return this.activeDate && (!this.sessionDict[this.activeDate] || this.sessionDict[this.activeDate].length === 0);
    }
  },
  methods: {
    async fetchCinemaData() {
      try {
        const url = `${this.httpURL}/sysCinema/find/${this.cinemaId}/${this.movieId}`;
        const { data: res } = await axios.get(url);

        if (res.code !== 200) {
          this.$message.error(res.msg || '获取数据失败');
          return;
        }

        // 处理影院信息
        this.cinemaInfo = res.data.cinema || {};
        if (this.cinemaInfo.cinemaPicture) {
          this.cinemaInfo.cinemaPicture = JSON.parse(this.cinemaInfo.cinemaPicture).map(
            obj => this.httpURL + obj
          );
        }

        // 处理电影信息
        if (res.data.movie) {
          this.selectSession = { ...this.selectSession, ...res.data.movie };
          // 处理海报图片
          if (this.selectSession.moviePoster) {
            try {
              const posters = JSON.parse(this.selectSession.moviePoster);
              this.selectSession.moviePoster = this.httpURL + (posters[0] || '');
            } catch (e) {
              console.error('解析海报图片失败:', e);
            }
          }
          // 处理电影类别
          if (this.selectSession.movieCategoryList && Array.isArray(this.selectSession.movieCategoryList)) {
            this.selectSession.movieCategoryList = this.selectSession.movieCategoryList
              .map(obj => obj.movieCategoryName)
              .join(' / ');
          }
        }

        // 处理场次信息
        this.sessions = res.data.sessions || [];
        this.processSessionData();

      } catch (error) {
        console.error('获取数据失败:', error);
        this.$message.error(`获取数据失败: ${error.message}`);
      }
    },

    processSessionData() {
      this.sessionDict = {};

      this.sessions.forEach(session => {
        // 确保有日期字段
        const sessionDate = session.sessionDate ||
          moment().format('YYYY-MM-DD'); // 默认使用今天

        if (!this.sessionDict[sessionDate]) {
          this.sessionDict[sessionDate] = [];
        }
        this.sessionDict[sessionDate].push(session);
      });

      // 对每个日期的场次按时间排序
      Object.keys(this.sessionDict).forEach(date => {
        this.sessionDict[date].sort((a, b) => {
          // 比较播放时间，按从早到晚排序
          return a.playTime.localeCompare(b.playTime);
        });
      });

      // 生成日期列表并排序
      this.dateList = Object.keys(this.sessionDict).sort((a, b) => new Date(a) - new Date(b));

      // 如果没有场次数据，生成未来7天的日期作为可选日期
      if (this.dateList.length === 0) {
        this.generateDefaultDateList();
      }

      // 设置默认选中日期
      if (this.dateList.length > 0) {
        this.activeDate = this.dateList[0];
      }
    },

    generateDefaultDateList() {
      const today = moment();
      this.dateList = [];

      for (let i = 0; i < 7; i++) {
        const date = today.clone().add(i, 'days').format('YYYY-MM-DD');
        this.dateList.push(date);
        this.sessionDict[date] = [];
      }
    },

    formatDateForDisplay(date) {
      const mom = moment(date);
      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const today = moment().format('YYYY-MM-DD');
      const tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');

      if (date === today) {
        return '今天 ' + mom.format('MM月DD日');
      } else if (date === tomorrow) {
        return '明天 ' + mom.format('MM月DD日');
      } else {
        const weekIndex = mom.day();
        return `${weekMap[weekIndex]} ${mom.format('MM月DD日')}`;
      }
    },

    // 格式化时间显示
    formatTime(time) {
      return time || '未知时间';
    },

    // 计算结束时间
    calculateEndTime(playTime, duration) {
      if (!playTime || !duration) return '未知';
      return moment(playTime, 'HH:mm').add(duration, 'minutes').format('HH:mm');
    },

    // 检查场次是否已过截止时间
    isSessionExpired(playTime, date) {
      // 验证时间格式
      if (!playTime || !date) return true;

      // 组合日期和时间，创建完整的场次时间对象
      const sessionDateTimeStr = `${date} ${playTime}`;
      const sessionTime = moment(sessionDateTimeStr, 'YYYY-MM-DD HH:mm');

      // 验证时间是否有效
      if (!sessionTime.isValid()) return true;

      // 获取当前时间
      const currentTime = moment();

      // 比较当前时间是否晚于场次时间
      return currentTime.isAfter(sessionTime);
    },

    toChooseSeat(sessionId) {
      const session = this.sessions.find(s => s.sessionId === sessionId);
      if (!session) {
        this.$message.warning('未找到该场次信息');
        return;
      }

      if (this.isSessionExpired(session.playTime, this.activeDate)) {
        this.$message.warning('该场次已截止购票');
        return;
      }
      this.$router.push(`/chooseSeat/${sessionId}`);
    },

    toMovieInfo() {
      this.$router.push(`/movieInfo/${this.movieId}`);
    }
  }
};
</script>

<style scoped>
a {
  text-decoration: none;
  cursor: pointer;
}

.header {
  padding: 30px 0;
  width: 100%;
  background: radial-gradient(ellipse at center, #444 0%, #222 100%);
  color: #fff;
}

.header-inner {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: flex-start;
  gap: 30px;
}

.movie-info-left {
  flex: 0 0 auto;
}

.avatar-shadow {
  position: relative;
  width: 240px;
  height: 330px;
  padding-bottom: 40px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAAAyAgMAAAAfG76+AAAADFBMVEUAAAAAAAAAAAAAAAA16TeWAAAABHRSTlMOAgoGQ0SIeAAAADpJREFUSMdjGAWjYBRgAasoAAwdFACKbB7VPEI076YAUGbzfwrAqOYRormcAjCANodSAEY1j2oexJoBlx1+yE7RXIIAAAAASUVORK5CYII=) no-repeat bottom;
}

.avatar {
  border: 4px solid #fff;
  width: 240px;
  height: 330px;
  object-fit: cover;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.movie-info-right {
  flex: 1;
  padding-top: 20px;
}

.movie-info-msg {
  margin-bottom: 30px;
}

.movie-name {
  margin: 0 0 20px 0;
  font-size: 28px;
  line-height: 1.2;
  font-weight: 700;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

ul li {
  margin: 10px 0;
  font-size: 16px;
  line-height: 1.5;
}

.movie-info-actions {
  display: flex;
  align-items: center;
  gap: 40px;
  margin-top: 20px;
}

.movie-info-btn .buy-btn {
  font-size: 16px;
  padding: 10px 24px;
}

.movie-info-score {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 15px 20px;
  border-radius: 8px;
}

.movie-index-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 5px;
}

.box-office {
  font-size: 24px;
  font-weight: bold;
  margin-right: 5px;
}

.unit {
  font-size: 16px;
  margin-bottom: 3px;
  color: rgba(255, 255, 255, 0.9);
}

.movie-info-detail-container {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 0;
}

.movie-info-detail {
  display: flex;
}

.main-content {
  width: 100%;
}

.crumbs-nav-container {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.crumbs-nav-container a {
  color: inherit;
}

.cinema-info {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.cinema-info h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333;
}

.cinema-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.show-date {
  margin: 20px 0 30px 0;
  padding: 15px 0;
  border-bottom: 1px solid #e5e5e5;
  overflow-x: auto;
  white-space: nowrap;
}

.show-date::-webkit-scrollbar {
  height: 6px;
}

.show-date::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.show-date span:first-child {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  margin-right: 15px;
}

.date-item {
  display: inline-block;
  padding: 8px 18px;
  margin-right: 12px;
  cursor: pointer;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  transition: all 0.2s ease;
}

.date-item.active {
  background-color: #f03d37;
  color: #fff;
}

.date-item:hover:not(.active) {
  color: #f03d37;
  background-color: #fef0f0;
}

.session-info {
  margin-top: 20px;
}

.session-info >>> .el-table {
  border-radius: 8px;
  overflow: hidden;
}

.session-info >>> .el-table th {
  background-color: #f9f9f9;
  font-weight: 500;
}

.session-info >>> tr {
  height: 80px;
}

.session-info >>> td {
  text-align: center;
}

.begin-time {
  font-size: 18px;
  color: #333;
  font-weight: 700;
}

.end-time {
  font-size: 12px;
  color: #999;
}

.sell-price {
  font-size: 18px;
  color: #f03d37;
  font-weight: 700;
}

.sell-price:before {
  content: "¥";
  font-size: 12px;
}

/* 已截止按钮样式 */
.session-info >>> .disabled-btn {
  background-color: #ccc !important;
  border-color: #ccc !important;
  cursor: not-allowed;
}

.empty-tip {
  text-align: center;
  padding: 60px 0;
  color: #999;
  font-size: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .header-inner,
  .movie-info-detail-container {
    width: 95%;
  }
}

@media (max-width: 768px) {
  .header-inner {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .movie-info-actions {
    justify-content: center;
  }
}
</style>
