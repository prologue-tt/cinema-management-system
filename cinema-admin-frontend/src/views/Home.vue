<template>
  <el-container class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header-container">
      <div class="header-logo" @click="toWelcome">
        <img src="../assets/logo02.jpg" alt="电影院管理系统logo" class="logo-img">
        <div class="header-text">
          <div class="system-name">电影院管理系统</div>
          <div class="welcome-text"><(￣︶￣)↗[GO!] 欢迎访问管理后台</div>
        </div>
      </div>

      <!-- 顶部菜单 -->
      <el-menu
        mode="horizontal"
        background-color="#f0f7ff"
        text-color="#2a69b8"
        active-text-color="#165dff"
        :router="true"
        :default-active="$route.path"
        class="top-menu">
        <el-menu-item
          v-for="menu in menuList"
          :key="menu.path"
          :index="menu.path"
          class="menu-item">
          {{ menu.name }}
        </el-menu-item>
      </el-menu>

      <el-button
        @click="logout"
        class="logout-btn">
        退出
      </el-button>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="main-content">
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      // 前端直接定义菜单，不再从数据库获取
      menuList: [
        { name: "我的影院", path: "/cinema" },
        { name: "用户管理", path: "/user" },
        { name: "电影列表", path: "/movie" },
        { name: "类别管理", path: "/movieCategory" },
        { name: "影厅管理", path: "/hall" },
        { name: "场次安排", path: "/session" },
        { name: "订单管理", path: "/bill" }
      ]
    }
  },
  created(){
    // 页面加载时默认跳转到欢迎页
    this.$router.push('/welcome')
  },
  methods: {
    logout() {
      // 清空sessionStorage并跳转到登录页
      window.sessionStorage.clear();
      this.$router.push('/login')
    },
    toWelcome(){
      this.$router.push('/welcome')
    }
  }
}
</script>

<style scoped>
.home-container{
  height: 100vh;
  background-color: #f9fcff;
  display: flex;
  flex-direction: column;
}

/* 头部样式 */
.header-container {
  background: linear-gradient(135deg, #b8e0ff 0%, #84cafc 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 3px 15px rgba(132, 202, 252, 0.2);
  height: 80px;
}

.header-logo {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.header-logo:hover {
  transform: scale(1.02);
}

.logo-img {
  height: 50px;
  width: 50px;
  border-radius: 50%;
  margin-right: 15px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  border: 2px solid white;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.system-name {
  font-size: 24px;
  font-weight: 700;
  color: #1e5aa8;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.6);
}

.welcome-text {
  font-size: 16px;
  color: #2a69b8;
  margin-top: 2px;
  font-weight: 500;
}

/* 顶部菜单样式 */
.top-menu {
  flex: 1;
  margin: 0 20px;
  border-bottom: none;
  height: 100%;
  display: flex;
  justify-content: center;
}

.menu-item {
  color: #2a69b8;
  font-size: 16px;
  font-weight: 600;
  margin: 0 10px;
  transition: all 0.3s ease;
  height: 100%;
  line-height: 80px;
  padding: 0 15px;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.4) !important;
  color: #165dff !important;
  border-radius: 8px 8px 0 0;
}

/* 退出按钮样式 */
.logout-btn {
  background-color: #4096ff;
  color: white;
  border: none;
  padding: 8px 18px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(64, 150, 255, 0.3);
}

.logout-btn:hover {
  background-color: #165dff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.4);
}

/* 主内容区域 */
.main-content {
  background-color: white;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
  border-radius: 15px;
  margin: 15px;
  box-shadow: 0 3px 15px rgba(132, 202, 252, 0.1);
}
</style>
