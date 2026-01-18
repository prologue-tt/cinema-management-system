<template>
  <div class="movie-container">
    <div class="header">
      <div class="header-inner clearfix">
        <div class="movie-info-left">
          <!-- * 电影海报-->
          <div class="avatar-shadow">
            <img class="avatar" :src="movieInfo.moviePoster">
          </div>
        </div>
        <div class="movie-info-right">
          <div class="movie-info-msg">
            <h1 class="movie-name">{{movieInfo.movieName}}</h1>
            <ul>
              <li>{{movieInfo.movieCategoryList}}</li>
              <li>{{movieInfo.movieArea}} / {{movieInfo.movieLength}}分钟</li>
              <li>{{movieInfo.releaseDate}} {{movieInfo.movieArea}} 上映</li>

              <li v-if="averageStar !== null">
                平均评分:
                <span class="average-star">{{averageStar.toFixed(1)}}</span>
                <div class="star-rating">
                  <i v-for="i in 5" :key="i" class="el-icon-star" :class="{ 'star-filled': i <= Math.round(averageStar) }"></i>
                </div>
              </li>


            </ul>
          </div>
          <div class="movie-info-btn">
            <el-button class="buy-btn" type="primary" @click="toSelectSession">特惠购票</el-button>
          </div>
          <div class="movie-info-score">
            <div class="movie-index box-office-container">
              <span class="movie-index-title">累计票房</span>
              <div style="display: flex;align-items: flex-end;">
                <span class="box-office">{{movieInfo.movieBoxOffice}}</span>
                <span class="unit">元</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="movie-info-detail-container">
      <div class="movie-info-detail clearfix">
        <div class="main-content">
          <!--          <div class="crumbs-nav-container">-->
          <!--            <a href="/welcome">电影院</a> &gt; <a href="/movie">电影</a> &gt; {{movieInfo.movieName}}-->
          <!--          </div>-->
          <el-tabs v-model="activeName">
            <el-tab-pane label="介绍" name="introduction">
              <div class="tab-body">
                <!-- 剧情简介 -->
                <div class="module">
                  <div class="mod-title">
                    <h2>剧情简介</h2>
                  </div>
                  <div class="mod-content">
                    <span class="dra">
                      {{movieInfo.movieIntroduction}}
                    </span>
                  </div>
                </div>
              </div>
            </el-tab-pane>


            <el-tab-pane label="评论" name="comments">
              <div class="tab-body">
                <!-- 评论统计 -->
                <div class="module comment-stats">
                  <div class="stats-container">
                    <div class="average-rating">
                      <span class="rating-value">{{averageStar !== null ? averageStar.toFixed(1) : '暂无评分'}}</span>
                      <div class="star-rating">
                        <i v-for="i in 5" :key="i" class="el-icon-star" :class="{ 'star-filled': i <= Math.round(averageStar || 0) }"></i>
                      </div>
                    </div>
                    <div class="rating-distribution">
                      <div class="distribution-item" v-for="i in 5" :key="i">
                        <span class="star">{{i}}星</span>
                        <div class="progress-bar">
                          <div class="progress" :style="{ width: (starDistribution[i] || 0) + '%' }"></div>
                        </div>
                        <span class="percentage">{{starDistribution[i] || 0}}%</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 评论列表 -->
                <div class="module comments-list">
                  <div class="mod-title">
                    <h2>用户评论 ({{comments.length}})</h2>
                  </div>


                  <!-- 评论列表 -->
                  <div class="comment-item" v-for="comment in comments" :key="comment.commentId">
                    <div class="comment-header">
                      <div class="user-info">
                        <!--                        <el-avatar :src="comment.userAvatar || defaultAvatar"></el-avatar>-->
                        <!--                        <el-avatar :src="comment.sysUser.userPicture "></el-avatar>-->
                        <el-avatar :src="getUserAvatar(comment.user)"></el-avatar>
                        <span class="user-name">{{comment.user.userName || '匿名用户'}}</span>
                      </div>
                      <div class="comment-time">
                        <span>{{formatDate(comment.commentTime)}}</span>
                        <el-rate
                          v-model="comment.star"
                          :max="5"
                          disabled
                          show-score
                          score-template="{value}"></el-rate>
                      </div>
                    </div>
                    <div class="comment-content">
                      {{comment.commentContent}}
                    </div>
                  </div>

                  <!-- 无评论提示 -->
                  <div v-if="comments.length === 0" class="no-comments">
                    暂无评论，快来发表你的看法吧！
                  </div>

                  <!-- 分页 -->
                  <el-pagination
                    v-if="comments.length > 0"
                    layout="prev, pager, next"
                    :total="comments.length"
                    :page-size="pageSize"
                    @current-change="handlePageChange">
                  </el-pagination>
                </div>
              </div>
            </el-tab-pane>


          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import movieItem from './../../components/movie/movie-item';
import moment from 'moment'
export default {
  name: "MovieInfo",
  components:{
    movieItem
  },
  data() {
    return {
      movieInfo: {
        moviePictures: []
      },
      movieId: this.$route.params.movieId,
      activeName: 'introduction',
      // colors: ['#317ee8', '#5285ac', '#bc955c'],
      httpURL: this.global.base,

// 评论相关数据
      comments: [],
      averageStar: null,
      starDistribution: {},
      pageSize: 5,
      currentPage: 1

    }
  },
  created() {
    this.getMovieInfo()
    this.getCommentsByMovieId();
    this.getAverageStar();
  },
  methods: {
    async getMovieInfo(){
      const _this = this
      const {data : res} = await axios.get('sysMovie/find/' + this.movieId)
      if(res.code !== 200) return this.$message.error('数据查询失败')
      this.movieInfo = res.data
      this.movieInfo.moviePoster = this.httpURL + JSON.parse(res.data.moviePoster)[0]
      this.movieInfo.moviePictures = JSON.parse(this.movieInfo.moviePictures).map((obj, index) => {
        return this.httpURL + obj
      })
      this.movieInfo.movieCategoryList = this.movieInfo.movieCategoryList.map((obj,index) => {
        return obj.movieCategoryName;
      }).join(" ")
    },

    // showPictures(){
    //   this.activeName = 'pictures'
    // },
    //转到购票页面
    toSelectSession(){
      let cinemaId = 1

      //this.$router.push('/selectSession/' + cinemaId)
      this.$router.push('/selectSession/' + cinemaId + '/' + this.movieId)
    },

    // 获取电影评论
    async getCommentsByMovieId() {
      try {
        const { data: res } = await axios.get(`/comments/${this.movieId}`);
        if (res.code === 200) {
          this.comments = res.data;
          console.log('获取到的评论数据:', this.comments);
          this.calculateStarDistribution();
        } else {
          this.$message.error('获取评论失败');
        }
      } catch (error) {
        console.error('获取评论出错:', error);
        this.$message.error('获取评论时发生错误');
      }
    },

    // 获取平均星级
    async getAverageStar() {
      try {
        const { data: res } = await axios.get(`/comment/${this.movieId}/average-star`);
        if (res.code === 200) {
          this.averageStar = res.data || 0;
        } else {
          this.$message.error('获取评分失败');
        }
      } catch (error) {
        console.error('获取评分出错:', error);
        this.$message.error('获取评分时发生错误');
      }
    },
    // 计算星级分布
    calculateStarDistribution() {
      if (!this.comments.length) {
        this.starDistribution = {};
        return;
      }

      const total = this.comments.length;
      for (let i = 1; i <= 5; i++) {
        const count = this.comments.filter(comment => comment.star === i).length;
        this.starDistribution[i] = Math.round((count / total) * 100);
      }
    },
    formatDate(date) {
      if (!date) return '';
      // 使用moment格式化日期
      return moment(date).format('YYYY-MM-DD');
    },
    getUserAvatar(sysUser) {
      // 检查sysUser和userPicture是否存在
      if (!sysUser || !sysUser.userPicture) {
        return 'https://picsum.photos/id/64/200'; // 默认头像
      }

      try {
        // 解析存储的数组格式路径
        const pictureArray = Array.isArray(sysUser.userPicture)
          ? sysUser.userPicture
          : JSON.parse(sysUser.userPicture);

        // 取数组一个图片路径并拼接基础URL
        if (pictureArray && pictureArray.length > 0) {
          return this.httpURL + pictureArray[0];
        }

        // 如果数组为空，返回默认头像
        return 'https://picsum.photos/id/64/200';
      } catch (error) {
        console.error('解析用户头像失败:', error);
        return 'https://picsum.photos/id/64/200'; // 解析失败时返回默认头像
      }
    }


  }
}
</script>

<style scoped>
.movie-container{

}

a{
  text-decoration: none;
  cursor:pointer;
}

.header{

  padding: 0;
  width: 100%;
  min-width: 1200px;
  /* background: url('../../assets/.jpg') */
  background: radial-gradient(ellipse at center, #444 0%, #222 100%);
  color: #fff;
}

.header-inner{
  width: 1200px;
  margin: 0 auto;
  position: relative;
}

.clearfix::before, .clearfix::after{
  content: " ";
  display: table;
}

.clearfix::after{
  clear: both;
}

.movie-info-left{
  width: 300px;
  height: 370px;
  float: left;
  position: relative;
  top: 30px;
  overflow: hidden;
  z-index: 9;
}

.avatar-shadow{
  position: relative;
  margin: 0 30px;
  width: 240px;
  height: 330px;
  padding-bottom: 40px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAAAyAgMAAAAfG76+AAAADFBMVEUAAAAAAAAAAAAAAAA16TeWAAAABHRSTlMOAgoGQ0SIeAAAADpJREFUSMdjGAWjYBRgAasoAAwdFACKbB7VPEI076YAUGbzfwrAqOYRormcAjCANodSAEY1j2oexJoBlx1+yE7RXIIAAAAASUVORK5CYII=) no-repeat bottom;
}

.avatar{
  border: 4px solid #fff;
  height: 322px;
  width: 232px;
}

.movie-info-msg{
  position: absolute;
  color: #fafcfc;
  font-size: 14px;
  z-index: 1;
}

.movie-name{
  width: 900px;
  margin-top: 0;
  font-size: 26px;
  line-height: 32px;
  font-weight: 700;
  margin-bottom: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  max-height: 64px;
}

.movie-ename{
  width: 340px;
  font-size: 18px;
  line-height: 1.3;
  margin-bottom: 14px;
}

ul{
  width: 250px;
  list-style: none;
  padding-left: 0;
  margin-bottom: 20px;
}

ul li{
  margin: 12px 0;
  line-height: 100%;
}

.movie-info-btn{
  position: absolute;
  bottom: 20px;
}

.buy-btn{
  margin-top: 10px;
  width: 250px;
  height: 40px;
  font-size: 16px;
  text-align: center;
}

.movie-info-score{
  position: absolute;
  top: 145px;
  left: 342px;
}

.movie-index {
  margin-bottom: 16px;
  color: #fafcfc;
}

.box-office-container{
  display: flex;
  flex-direction: column;
}

.movie-index-title{
  font-size: 12px;
  margin-bottom: 8px;
}

.movie-score-num{
  font-size: 30px;
  color: #ffc600;
  height: 30px;
  line-height: 30px;
  margin-right: 10px;
}

.score-container{
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
}

.box-office{
  font-size: 30px;
}

.unit{
  font-size: 12px;
  padding-left: 1px;
  line-height: 24px;
}

.movie-info-right{
  height: 300px;
  position: relative;
  margin-right: 30px;
  margin-left: 300px;
  margin-top: 70px;
}

.movie-info-detail-container{
  width:1200px;
  margin: 0 auto;
}

.movie-info-detail{
  margin-top: 80px;
}

.main-content {
  width: 730px;
  float: left;
  margin-bottom: 20px;
}

.crumbs-nav-container {
  margin-bottom: 25px;
  color: #333;
}

.crumbs-nav-container a{
  color: inherit;
}

.el-tabs >>> .el-tabs__item{
  font-size: 20px;
}

.tab-body{
  margin-top: 40px;
}


.mod-title h2{
  display: inline-block;
  margin: 0;
  padding: 0;
  font-weight: 400;
  font-size: 18px;
  color: #333;
  line-height: 18px;
}

.mod-title h2:before {
  float: left;
  content: "";
  display: inline-block;
  width: 4px;
  height: 18px;
  margin-right: 6px;
  background-color: #409EFF;
}

.mod-content{
  margin-top: 20px;
  color: #333;
}

.mod-content .dra{
  font-size: 14px;
  line-height: 26px;
}

.more{
  float: right;
  cursor: pointer;
  font-size: 14px;
  color: #999;
  padding-right: 14px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAOCAYAAAASVl2WAAAABmJLR0QA/wD/AP+gvaeTAAAAv0lEQVQY013RTUpDQRAE4G8eghcR8ScgKCIugpJFjuIjqAvBc7jxj0muEnCjiIQQJOImB3GnbnpkfL1qpqqrunpSzvkDPxjhGdq2VarBF3q4wRHknP8RzvCEQzzguCalaHZwiwHecY6XogCf8TjFHh7Rh9Tx3AylIZa4TgWpSBuY4BSrYlFXKsr4bjrTW5HkJJa9SBW4jbtukmKxG5MDLOKqfzEPcB9LzQN8LSdfwxj7eMMlZvV/NFiPzFddEH4Bt5Y1mf3fnDwAAAAASUVORK5CYII=) no-repeat 100%;
}

.portrait{
  margin-bottom: 6px;
  width: 128px;
  height: 170px;
  overflow: hidden;
}

.portrait .default-img{
  width: 128px;
  height: 170px;
}

.pictures-list{
  display: flex;
}

.pictures-list>.el-image:first-child{
  width: 465px;
  height: 258px;
}

.pictures-list .default-img{
  border-style: none;
  cursor: pointer;
  width: 126px;
  height: 126px;
}

.little-pictures{
  width: 262px;
  height: 262px;
  display: flex;
  flex-wrap: wrap;
  margin-left: 10px;
  justify-content: space-between;
}

#pane-pictures .little-pictures{
  width: 100%;
  justify-content: flex-start;
  margin-left: 0;
}

#pane-pictures .default-img{
  margin-top: 10px;
  margin-left: 10px;
}

.user-avatar{
  width: 50px;
  height: 50px;
  margin-right: 20px;
}


.main-header{
  font-size: 16px;
}

.user-name{
  margin-top: 2px;
}


/* 评论相关样式 */
.comment-stats {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 4px;
}

.stats-container {
  display: flex;
  align-items: center;
}

.average-rating {
  flex: 0 0 150px;
  text-align: center;
}

.rating-value {
  font-size: 36px;
  color: #ff9900;
  font-weight: bold;
}

.star-rating {
  color: #ccc;
  margin: 10px 0;
}

.star-filled {
  color: #ff9900 !important;
}

.average-star {
  color: #ff9900;
  font-size: 18px;
  font-weight: bold;
  margin: 0 5px;
}

.rating-distribution {
  flex: 1;
  margin-left: 30px;
}

.distribution-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.star {
  flex: 0 0 30px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #eee;
  border-radius: 4px;
  overflow: hidden;
  margin: 0 10px;
}

.progress {
  height: 100%;
  background-color: #ff9900;
}

.percentage {
  flex: 0 0 40px;
  text-align: right;
}

.comments-list {
  margin-top: 30px;
}

.add-comment {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 30px;
}

.add-comment .el-input {
  margin: 15px 0;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  margin-left: 10px;
  font-weight: 500;
}

.comment-time {
  color: #999;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.comment-time .el-rate {
  margin-left: 10px;
}

.comment-content {
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  color: #999;
  font-size: 12px;
}

.action-btn {
  cursor: pointer;
  margin-right: 20px;
}

.action-btn:hover {
  color: #409EFF;
}

.no-comments {
  text-align: center;
  padding: 50px 0;
  color: #999;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.el-pagination {
  margin-top: 30px;
  text-align: center;
}

/* 其他原有样式保持不变 */
.more{
  float: right;
  cursor: pointer;
  font-size: 14px;
  color: #999;
  padding-right: 14px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAOCAYAAAASVl2WAAAABmJLR0QA/wD/AP+gvaeTAAAAv0lEQVQY013RTUpDQRAE4G8eghcR8ScgKCIugpJFjuIjqAvBc7jxj0muEnCjiIQQJOImB3GnbnpkfL1qpqqrunpSzvkDPxjhGdq2VarBF3q4wRHknP8RzvCEQzzguCalaHZwiwHecY6XogCf8TjFHh7Rh9Tx3AylIZa4TgWpSBuY4BSrYlFXKsr4bjrTW5HkJJa9SBW4jbtukmKxG5MDLOKqfzEPcB9LzQN8LSdfwxj7eMMlZvV/NFiPzFddEH4Bt5Y1mf3fnDwAAAAASUVORK5CYII=) no-repeat 100%;
}

.portrait{
  margin-bottom: 6px;
  width: 128px;
  height: 170px;
  overflow: hidden;
}

.portrait .default-img{
  width: 128px;
  height: 170px;
}

.pictures-list{
  display: flex;
}

.pictures-list>.el-image:first-child{
  width: 465px;
  height: 258px;
}

.pictures-list .default-img{
  border-style: none;
  cursor: pointer;
  width: 126px;
  height: 126px;
}

.little-pictures{
  width: 262px;
  height: 262px;
  display: flex;
  flex-wrap: wrap;
  margin-left: 10px;
  justify-content: space-between;
}

#pane-pictures .little-pictures{
  width: 100%;
  justify-content: flex-start;
  margin-left: 0;
}

#pane-pictures .default-img{
  margin-top: 10px;
  margin-left: 10px;
}

</style>
