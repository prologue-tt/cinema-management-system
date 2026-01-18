<template>
  <el-container>
    <el-header>
      <div class="header-inner">
        <a href="/welcome" style="text-decoration: none;">
          <div style="color: #efe8e8; font-size: 26px; margin-top: 20px;">
            <img style="height: 30px; margin-bottom: -5px;" src="@/assets/logo.jpg">
            <!-- 添加 font-family -->
            <span style="color: rgba(1,0,4,0.71); font-family: 'Ma Shan Zheng', cursive;font-size: 32px;">默片时光电影院</span>
          </div>
        </a>
        <el-menu
          :default-active="activeUrl"
          class="nav-menu"
          mode="horizontal"
          :router="true"
          active-text-color=#84b5e4
          text-color="#000000">
          <el-menu-item :index="item.path" v-for="item in menuList" :key="item.id">{{item.name}}</el-menu-item>
        </el-menu>
        <div class="searchContainer">
          <el-input v-model="kw" class="searchBar" placeholder="兰台搜异，光影录千秋"></el-input>
          <el-button id="searchBtn" icon="iconfont el-icon-search" style="background-color:#c3dbf2 ;border-color: #fafcfc" circle @click="search"></el-button>
        </div>
        <!-- 用户操作按钮组 -->
        <div class="user-actions">
          <el-button v-if="isToken" @click="handleUserAction('logout')" style="margin-right: 10px;">退出</el-button>
          <template v-if="!isToken">
            <el-button @click="handleUserAction('login')" style="margin-right: 10px;">登录</el-button>
            <el-button @click="handleUserAction('register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
    <el-footer>
      <div class="footer-mini"></div>
      <div class="footer" style="padding-bottom: 15px;">

      </div>
      <el-backtop></el-backtop>
    </el-footer>
  </el-container>
</template>

<script>
export default {
  name: "Welcome",
  data() {
    return {
      isToken: '',
      cinemaInfo: '',
      url: '',
      activeUrl: this.$route.path.substring(0, this.$route.path.indexOf('/',1) === -1 ? this.$route.path.length : this.$route.path.indexOf('/',1)),
      menuList: [
        {
          id: 1,
          name: '首页',
          path: '/home'
        },
        {
          id: 2,
          name: '影片',
          path: '/movie'
        },
        {
          id: 3,
          name: '排行榜',
          path: '/rankingList'
        },
        {
          id: 4,
          name: '联系我们',
          path: '/aboutUs'
        },
        {
          id: 5,
          name: '个人中心',
          path: '/userMenu'  // 确保这个路径正确指向个人中心页面
        }
      ],
      //搜索关键字
      kw: ''
    }
  },
  created() {
    this.getCinemaInfo()
    this.isToken = window.sessionStorage.getItem("token")
    const userPicture = JSON.parse(window.sessionStorage.getItem('loginUser')).userPicture
    const picture = JSON.parse(userPicture)
    if(picture === null || picture.length === 0) return;
    this.url = this.global.base + picture[0]
  },
  watch: {
    '$route'() {
      this.activeUrl = this.$route.path.substring(0, this.$route.path.indexOf('/',1) === -1 ? this.$route.path.length : this.$route.path.indexOf('/',1))
    }
  },
  methods: {
    async getCinemaInfo(){
      const _this = this
      await axios.get('sysCinema').then(resp => {
        _this.cinemaInfo = resp.data.data
      })
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    search() {
      let tmp = this.kw
      this.$router.push('/search/searchMovie?kw=' + tmp)
      this.kw = ''
    },
    handleUserAction(action) {
      if(action === 'logout') {
        window.sessionStorage.clear();
        return this.$router.push('/login')
      }
      this.$router.push('/' + action)
    },
    toWelcome() {
      this.$router.push('/welcome')
    }
  }
}
</script>

<style scoped>
.el-header{
  background-color: #b5e484;
  height: 80px !important;
  border-bottom: 1px solid #e6e6e6;
}

.header-inner{
  width: 90%;
  margin: 0 12.5%;
  display: flex;
  align-items: center;
}

.logo{
  height: 80px;
  width: 200px;
}

.nav-menu{
  background-color: #b5e484;
  border-bottom: 0px solid #eee !important;
  height: 60px;
  margin: 0 auto;
}

::v-deep .el-menu-item {
  font-family: 'Ma Shan Zheng', 'STXingkai', 'KaiTi', cursive !important;
  font-size: 16px; /* 可选：调整字号 */
}

/* 如果需要同时修改激活状态的字体 */
::v-deep .el-menu-item.is-active {
  font-family: 'Ma Shan Zheng', 'STXingkai', 'KaiTi', cursive !important;
}

.el-menu-item{
  font-size: 20px;
}

.el-menu-item:hover{
  color: #3186cb !important;
}

.searchContainer{
  padding: 20px 0;
  display: flex;
  margin: 0 15px;
}

.searchBar >>> input{
  border-radius: 50px;
}

#searchBtn{
  transform: translate(-100%, 0);
}

/* 用户操作按钮样式 */
.user-actions {
  margin-left: auto;
}

.user-actions .el-button {
  background-color: #c3dbf2;
  border-color: #fafcfc;
  color: #000;
}

.user-actions .el-button:hover {
  background-color: #a8c7e6;
  border-color: #e6f0fa;
}

.footer-mini{
  border-top: 1px solid #EEE;
  padding-top: 20px;
  text-align: center;
}

.footer{
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.el-main{
  padding: 0px;
}
</style>
