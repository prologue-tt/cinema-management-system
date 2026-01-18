<template>
  <div>
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner">🍿</div>
      <p class="loading-text">正在加载票房数据...</p>
    </div>

    <!-- 电影列表 -->
    <div v-else class="main">
      <div class="board" v-for="(item, index) in totalBoxOfficeList" :key="item.movieId" :class="{ top3: index < 3 }">
        <!-- 排名 -->
        <div class="left">
          <i class="board-index" :class="getRankClass(index)">{{ index + 1 }}</i>
        </div>

        <!-- 海报 -->
        <div class="middle1">
          <a :href="'/movieInfo/' + item.movieId" class="poster-link">
            <img
              :src="getPosterUrl(item)"
              :alt="item.movieName"
              class="movie-poster"
              @error="handleImageError($event)"
            >
          </a>
        </div>

        <!-- 电影信息 -->
        <div class="middle2">
          <a :href="'/movieInfo/' + item.movieId"><p class="name">{{ item.movieName }}</p></a>
          <p class="releaseTime">
            <i class="iconfont icon-r-calendar" style="margin-right: 5px; color: #FF6B8B;"></i>
            {{ item.releaseDate.split(" ")[0] }}
          </p>
          <!-- 上映地区 -->
          <p class="release-area" v-if="item.movieArea">
            <i class="iconfont icon-r-map-marker" style="margin-right: 5px; color: #9370DB;"></i>
            {{ item.movieArea }}
          </p>
        </div>

        <!-- 票房数据 -->
        <div class="right">
          <div class="boxoffice-info">
            <div class="amount">{{ item.movieBoxOffice || '--' }}<span class="unit">元</span></div>

          </div>
        </div>
      </div>

      <!-- 空数据提示 -->
      <div v-if="totalBoxOfficeList.length === 0" class="empty-container">
        <img src="https://picsum.photos/seed/movieempty/200/200" alt="暂无数据" class="empty-img">
        <p class="empty-text">暂无票房数据哦~</p>
        <button class="refresh-btn" @click="getTotalBoxOfficeList">
          <i class="iconfont icon-r-refresh" style="margin-right: 5px;"></i>
          重新加载
        </button>
      </div>
    </div>
  </div>
</template>

<script>
// 保留原始数据获取逻辑，不做修改
export default {
  name: "TotalBoxOfficeList",
  data(){
    return{
      queryInfo:{
        pageNum: 1,
        pageSize: 10
      },
      totalBoxOfficeList: [],
      loading: false,
      defaultPoster: 'https://picsum.photos/seed/movieposter/160/220' // 默认海报
    }
  },
  created() {
    this.getTotalBoxOfficeList()
  },
  methods:{
    async getTotalBoxOfficeList(){
      this.loading = true; // 开始加载
      try {
        const {data: resp} = await axios.get('sysMovie/find/rankingList/1', {params: this.queryInfo})
        console.log(resp)
        if(resp.code !== 200) return this.$message.error(resp.msg)
        this.totalBoxOfficeList = resp.data
      } catch (error) {
        console.error('获取数据失败:', error);
        this.$message.error('获取票房数据失败，请稍后重试');
      } finally {
        this.loading = false; // 结束加载
      }
    },
    // 获取海报URL（增加容错处理）
    getPosterUrl(item) {
      if (!item.moviePoster) return this.defaultPoster;

      try {
        const posters = JSON.parse(item.moviePoster);
        return posters.length > 0 ? `${this.global?.base || ''}${posters[0]}` : this.defaultPoster;
      } catch (e) {
        return this.defaultPoster;
      }
    },
    // 处理图片加载失败
    handleImageError(event) {
      event.target.src = this.defaultPoster;
    },
    // 获取排名样式类
    getRankClass(index) {
      if (index === 0) return 'first';
      if (index === 1) return 'second';
      if (index === 2) return 'third';
      return '';
    }
  }
}
</script>

<style scoped>
/* 基础容器样式 */
.main{
  width: 950px;
  margin: 0 auto;
  padding: 20px 0;
}

/* 加载状态样式 */
.loading-container {
  text-align: center;
  padding: 80px 0;
}

.loading-spinner {
  font-size: 40px;
  margin-bottom: 20px;
  animation: spin 1.5s linear infinite;
}

.loading-text {
  color: #666;
  font-size: 16px;
  font-weight: 500;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 列表项样式 */
.board{
  display: flex;
  margin: 25px 0;
  background-color: #fff;
  border-radius: 15px;
  padding: 15px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  align-items: center;
}

.board:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(255, 107, 139, 0.15);
}

/* 前三名特殊样式 */
.board.top3 {
  position: relative;
  overflow: hidden;
}

.board.top3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 8px;
}

.board.top3:nth-child(1)::before {
  background-color: #FFD700; /* 金牌色 */
}

.board.top3:nth-child(2)::before {
  background-color: #C0C0C0; /* 银牌色 */
}

.board.top3:nth-child(3)::before {
  background-color: #CD7F32; /* 铜牌色 */
}

/* 排名样式 */
.left{
  display: flex;
  align-items: center;
  margin-right: 25px;
}

.board-index{
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  line-height: 50px;
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 前三名颜色 */
.board-index.first {
  background-color: #FFD700;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.5);
}

.board-index.second {
  background-color: #C0C0C0;
  box-shadow: 0 0 15px rgba(192, 192, 192, 0.5);
}

.board-index.third {
  background-color: #CD7F32;
  box-shadow: 0 0 15px rgba(205, 127, 50, 0.5);
}

/* 海报样式 */
.middle1{
  display: flex;
  align-items: center;
}

.poster-link {
  display: inline-block;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.poster-link:hover {
  transform: scale(1.05) rotate(2deg);
}

.movie-poster{
  width: 160px;
  height: 220px;
  object-fit: cover;
  display: block;
}

/* 电影信息样式 */
.middle2{
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-left: 25px;
  width: 300px;
}

.middle2 > a {
  color: #373d41;
  font-size: 26px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.middle2 > a:hover {
  color: #FF6B8B;
}

.name, .releaseTime, .movie-type, .release-area {
  margin-top: 8px;
  margin-bottom: 8px;
}

.releaseTime, .movie-type, .release-area {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
}

/* 票房数据样式 */
.right{
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: auto;
  padding-right: 10px;
}

.boxoffice-info {
  text-align: center;
}

.amount {
  font-size: 30px;
  font-weight: 800;
  font-style: italic;
  color: #FFD700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: baseline;
}

.unit {
  font-size: 24px;
  margin-left: 5px;
  color: #FFC107;
}

.change-rate {
  margin-top: 5px;
  font-size: 16px;
}

.up {
  color: #FF4D4F;
}

.down {
  color: #52C41A;
}

/* 空数据样式 */
.empty-container {
  text-align: center;
  padding: 60px 0;
  background-color: #fff;
  border-radius: 15px;
  margin-top: 25px;
}

.empty-img {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px;
  border: 5px solid #FFF0F3;
}

.empty-text {
  color: #FF6B8B;
  font-size: 18px;
  margin: 0 0 20px 0;
}

.refresh-btn {
  background-color: #FF6B8B;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 107, 139, 0.3);
}

.refresh-btn:hover {
  background-color: #ff527a;
  transform: translateY(-2px);
}

/* 响应式调整 */
@media (max-width: 992px) {
  .main {
    width: 100%;
    padding: 0 15px;
  }

  .board {
    flex-wrap: wrap;
    justify-content: center;
  }

  .right {
    margin-left: 0;
    margin-top: 20px;
    padding-right: 0;
    width: 100%;
  }
}

@media (max-width: 768px) {
  .middle2 {
    width: 100%;
    margin-left: 0;
    margin-top: 20px;
    align-items: center;
    text-align: center;
  }

  .amount {
    font-size: 36px;
  }
}
</style>
