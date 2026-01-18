<template>
  <div>
    <div class="whole">
      <div class="middle">
        <div class="panel">
          <div class="panel-header">
            <div style="color: #e48485;font-size: 50px;text-align:center;">
              <i class="iconfont icon-r-team" style="font-size: 36px;"> </i>
              <b>最新上映</b>
            </div>
            <a href="/movie/movieOngoing">全部</a>
          </div>
          <div class="panel-content">
            <movie-item :movieItem="item" v-for="(item, index) in ongoingMovieList" :key="index"></movie-item>
          </div>
        </div>
        <div class="panel">
          <div class="panel-header">
            <div style="
              color: #85c5f6;
               font-size: 28px;
                 display: flex;
                  justify-content: center;
                   align-items: center;
                    gap: 8px;">
              <i class="iconfont icon-r-refresh" style="font-size: 30px;"></i>
              <b>即将上映</b>
            </div>
            <a href="/movie/movieUpcoming">全部</a>
          </div>
          <div class="panel-content">
            <div class="panel-content">
              <movie-item :movieItem="item" v-for="(item,index) in upcomingMovieList" :key="index"></movie-item>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import movieItem from '../../components/movie/movie-item'
import moment from 'moment'
export default {
  name: "Home",
  components: {
    movieItem
  },
  data() {
    return {
      queryInfo1: {
        total: 0,
        pageSize:10,
        pageNum: 1,
        startDate: moment().subtract(60, "days").format("YYYY-MM-DD"),
        endDate: moment().format('YYYY-MM-DD'),
      },
      queryInfo2: {
        total: 0,
        pageSize:10,
        pageNum: 1,
        startDate: moment().format('YYYY-MM-DD')
      },
      queryInfo3: {
        total: 0,
        pageSize: 10,
        pageNum: 1
      },
      queryInfo4:{
        pageSize: 10,
        pageNum: 1
      },
      ongoingMovieList: [],
      upcomingMovieList: [],
      classicMovieList: [],
      totalBoxOfficeList: []
    }
  },
  created() {
    this.getOngoingMovieList()
    this.getUpcomingMovieList()
    this.getClassicMovieList()
    this.getHeight()
    this.getTotalBoxOfficeList()
  },
  methods:{
    async getOngoingMovieList() {
      const { data : res } = await axios.get('sysMovie/find', {params: this.queryInfo1})
      this.ongoingMovieList = res.data
      this.total = res.total
    },
    async getUpcomingMovieList() {
      const {data: res} = await axios.get('sysMovie/find', {params: this.queryInfo2})
      this.upcomingMovieList = res.data
      this.total = res.total
    },
    async getClassicMovieList() {
      const { data : res } = await axios.get('sysMovie/find', {params: this.queryInfo3})
      this.classicMovieList = res.data
      this.total = res.total
    },
    getHeight() {
      let clientWidth =   `${document.documentElement.clientWidth}`
      clientWidth *= 0.8
      this.carouselHeight = clientWidth / 1700 * 520 + 'px'
    },
    async getTotalBoxOfficeList(){
      const {data: resp} = await axios.get('sysMovie/find/rankingList/1', {params: this.queryInfo4})
      console.log(resp)
      if(resp.code !== 200) return this.$message.error(resp.msg)
      this.totalBoxOfficeList = resp.data
    }
  }
}
</script>

<style scoped>
/* 移除轮播相关样式中的动态效果 */
.el-carousel {
  width: 80%;
  margin: 30px auto;
}

.el-carousel__item > img {
  width: 100%;
  height: auto;
}

.whole{
  background: #fff5fb;
  width: 1600px;
  margin: 30px auto;
  display: flex;
}

.middle{
  background: radial-gradient( pink, #f3ffeb);
  width: 90%;
}

h2{
  font-size: 26px;
}

/* 链接样式，移除悬停效果 */
.panel-header > a{
  text-align: center;
  text-decoration: none;
  color: #277301;
  padding-right: 14px;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAOCAYAAAASVl2WAAAABmJLR0QA/wD/AP+gvaeTAAAAv0lEQVQY013RTUpDQRAE4G8eghcR8ScgKCIugpJFjuIjqAvBc7jxj0muEnCjiIQQJOImB3GnbnpkfL1qpqqrunpSzvkDPxjhGdq2VarBF3q4wRHknP8RzvCEQzzguCalaHZwiwHecY6XogCf8TjFHh7Rh9Tx3AylIZa4TgWpSBuY4BSrYlFXKsr4bjrTW5HkJJa9SBW4jbtukmKxG5MDLOKqfzEPcB9LzQN8LSdfwxj7eMMlZvV/NFiPzFddEH4Bt5Y1mf3fnDwAAAAASUVORK5CYII=) no-repeat 100%;
  /* 移除所有链接的动态效果 */
  transition: none !important;
}

.panel-header > a:hover,
.panel-header > a:focus,
.panel-header > a:active {
  color: #277301 !important;
  text-decoration: none !important;
  background-position: 100% !important;
}

.panel-header{
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 电影卡片样式，移除所有动态效果和鼠标交互 */
.movie-item{
  margin-left: 0;
  margin-right: 30px;
  /* 确保没有默认的动态效果 */
  transition: none !important;
  transform: none !important;
  box-shadow: none !important;
}

/* 移除鼠标悬停、点击等状态的样式变化 */
.movie-item:hover,
.movie-item:focus,
.movie-item:active {
  transform: none !important;
  box-shadow: none !important;
  opacity: 1 !important;
  /* 其他可能的动态效果属性重置 */
  filter: none !important;
  -webkit-filter: none !important;
}

.movie-item:nth-child(4n){
  margin-right: 0;
}

.board{
  display: flex;
  margin: 10px 10px;
}

.board-left{
  display: flex;
  align-items: center;
}

.board-middle{
  display: flex;
  margin-left: 10px;
  width: 150px;
  font-size: 18px;
}

.board-middle > a{
  text-decoration: none;
  color: #333;
  /* 移除链接的动态效果 */
  transition: none !important;
}

.board-middle > a:hover,
.board-middle > a:focus,
.board-middle > a:active {
  color: #333 !important;
  text-decoration: none !important;
}

.panel-content{
  margin: 0px 0px 50px 0px;
}
</style>
