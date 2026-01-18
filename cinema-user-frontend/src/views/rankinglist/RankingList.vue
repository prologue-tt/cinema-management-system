<template>
  <div class="box-office-page">
    <!-- 导航栏（添加可爱样式） -->
    <div class="navbar">
      <div class="container">
        <!-- 新增导航标题（带胶片图标） -->
        <div class="nav-left">
          <i class="iconfont icon-r-film" style="font-size: 24px; color: #FF6B8B;"></i>
          <span class="nav-title">电影票房榜单</span>
        </div>

        <el-menu
          :default-active="$route.path"
          class="el-menu-cute"
          mode="horizontal"
          background-color="transparent"
          text-color="#666"
          active-text-color="#FF6B8B"
          router
        >
        </el-menu>

        <!-- 导航装饰点 -->
        <div class="navbar-decoration">
          <span class="dot dot-pink"></span>
          <span class="dot dot-yellow"></span>
        </div>
      </div>
    </div>

    <!-- 主内容区（添加卡片样式） -->
    <div class="container main-content">
      <!-- 路由视图容器（添加圆角和阴影） -->
      <div class="router-view-container">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BoxOfficeRanking",
  data() {
    return {

    };
  }
};
</script>

<style scoped>
/* 基础背景：可爱纹理 */
.box-office-page {
  background-color: #FFF9F9;
  min-height: 100vh;
  background-image:
    radial-gradient(#FFE0E0 0.5px, transparent 0.5px),
    radial-gradient(#FFE0E0 0.5px, #FFF9F9 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 容器：统一宽度和内边距 */
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 导航栏：可爱化 */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(255, 107, 139, 0.1);
  padding: 12px 0;
  margin-bottom: 30px;
  border-radius: 0 0 20px 20px; /* 底部圆角 */
}

.navbar .container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-left {
  display: flex;
  align-items: center;
}

.nav-title {
  font-size: 20px;
  font-weight: 700;
  color: #FF6B8B;
  margin-left: 8px;
}

/* 导航菜单样式 */
.el-menu-cute {
  border-bottom: none !important; /* 去掉原生下划线 */
  flex: 1;
}

.menu-item-cute {
  font-size: 15px !important;
  margin: 0 10px !important;
  border-radius: 18px !important; /* 菜单圆角 */
  transition: background-color 0.3s ease !important;
}

.menu-item-cute:hover,
.menu-item-cute.is-active {
  background-color: #FFF0F3 !important; /* hover/活跃背景 */
}

/* 导航装饰点 */
.navbar-decoration {
  display: flex;
  gap: 8px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot-pink {
  background-color: #FF6B8B;
}

.dot-yellow {
  background-color: #FFD04B;
}

/* 主内容区 */
.main-content {
  padding-bottom: 50px;
}

/* 路由视图容器：卡片化 */
.router-view-container {
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 5px 20px rgba(255, 107, 139, 0.1);
  padding: 25px;
  margin-top: 15px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .navbar .container {
    flex-direction: column;
    gap: 12px;
    padding: 0 15px;
  }

  .nav-left {
    align-self: flex-start;
  }

  .el-menu-cute {
    width: 100%;
    overflow-x: auto;
    padding-bottom: 8px;
  }
}
</style>
