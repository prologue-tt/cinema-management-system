
<template>
  <el-container class="movie-container">
    <el-header class="movie-header">
      <div class="header-inner">
        <!-- 导航标题 -->
        <div class="header-title">
          <i class="iconfont icon-r-film" style="font-size: 28px; color: #FF6B8B;"></i>
          <span class="title-text">电影专区</span>
        </div>

        <!-- 导航菜单 -->
        <el-menu
          :default-active="$route.path"
          class="nav-menu"
          mode="horizontal"
          :router="true"
          background-color="transparent"
          text-color="#666"
          active-text-color="#FF6B8B"
        >
          <el-menu-item
            :index="item.path"
            v-for="item in menuList"
            :key="item.id"
            class="menu-item"
          >
            <i :class="item.icon" style="font-size: 22px; margin-right: 8px;"> </i>
            {{ item.name }}
          </el-menu-item>
        </el-menu>

        <!-- 装饰点 -->
        <div class="header-decoration">
          <span class="dot dot-pink"></span>
          <span class="dot dot-yellow"></span>
          <span class="dot dot-blue"></span>
        </div>
      </div>
    </el-header>
    <!-- 主内容区 -->
    <el-main class="movie-main">
      <div class="tags-container">
        <div class="tags-line">
          <div class="tags-title">
            <i class="iconfont icon-r-tag" style="color: #FF6B8B; margin-right: 8px;"></i>
            电影分类：
          </div>
          <el-radio-group v-model="categoryRadio" class="radio-group">
            <el-radio-button :label="0" class="radio-btn">全部</el-radio-button>
            <el-radio-button
              :label="item.movieCategoryId"
              v-for="item in categoryList"
              :key="item.movieCategoryId"
              class="radio-btn"
            >{{ item.movieCategoryName }}</el-radio-button
            >
          </el-radio-group>
        </div>
      </div>

      <!-- 路由视图（传递原有参数） -->
      <router-view
        :movieCategoryId="categoryRadio"
        :orderByColumn="orderByColumn"
        class="router-view-content"
      ></router-view>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "Movie",
  data() {
    return {
      menuList: [
        {
          id: "1",
          name: "正在热映",
          path: "/movie/movieOngoing",
          icon: "iconfont icon-r-team",
        },
        {
          id: "2",
          name: "即将上映",
          path: "/movie/movieUpcoming",
          icon: "iconfont icon-r-refresh",
        },
      ],
      categoryRadio: 0,
      categoryList: [],
      orderByColumn: "releaseDate",
    };
  },
  created() {
    this.getCategoryList();
  },
  methods: {
    async getCategoryList() {
      const { data: res } = await axios.get("sysMovieCategory/find");
      if (res.code !== 200)
        return this.$message.error("获取服务器信息失败");
      this.categoryList = res.data;
    },
  },
  watch: {
    $route() {
      //页面发生变化时初始化类别选项（原有逻辑保留）
      this.categoryRadio = 0;
      this.orderByColumn = "releaseDate";
    },
  },
};
</script>

<style scoped>
/* 基础容器样式 */
.movie-container {
  background-color: #FFF9F9;
  min-height: 100vh;
  font-family: 'Microsoft YaHei', sans-serif;
  /* 可爱背景纹理 */
  background-image:
    radial-gradient(#FFE0E0 0.5px, transparent 0.5px),
    radial-gradient(#FFE0E0 0.5px, #FFF9F9 0.5px);
  background-size: 24px 24px;
  background-position: 0 0, 12px 12px;
}

/* 顶部导航栏 */
.movie-header {
  padding: 12px 0 !important;
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(255, 107, 139, 0.1);
  border-radius: 0 0 24px 24px;
}

.header-inner {
  width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
}

.header-title {
  display: flex;
  align-items: center;
}

.title-text {
  font-size: 24px;
  font-weight: 700;
  color: #FF6B8B;
  margin-left: 10px;
  text-shadow: 1px 1px 3px rgba(255, 107, 139, 0.2);
}

/* 导航菜单 */
.nav-menu {
  border-bottom: none !important;
}

.menu-item {
  font-size: 16px !important;
  margin: 0 15px !important;
  border-radius: 20px !important;
  transition: all 0.3s ease !important;
}

.menu-item:hover,
.menu-item.is-active {
  background-color: #FFF0F3 !important;
  transform: translateY(-2px);
}

/* 导航装饰点 */
.header-decoration {
  display: flex;
  gap: 10px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.dot-pink {
  background-color: #FF6B8B;
}

.dot-yellow {
  background-color: #FFD04B;
}

.dot-blue {
  background-color: #87CEEB;
}

/* 主内容区 */
.movie-main {
  width: 1200px;
  margin: 0 auto;
  padding: 30px 20px !important;
  box-sizing: border-box;
}

/* 类别查询容器 */
.tags-container {
  background-color: #fff;
  border-radius: 15px;
  box-shadow: 0 3px 15px rgba(255, 107, 139, 0.08);
  padding: 15px 20px;
  margin-bottom: 30px;
}

.tags-line {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.tags-title {
  font-size: 16px;
  color: #333;
  font-weight: 600;
  display: flex;
  align-items: center;
}

/* 单选按钮组 */
.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 自定义单选按钮样式 */
.radio-btn {
  --el-radio-button-bg-color: #FFF0F3 !important;
  --el-radio-button-text-color: #FF6B8B !important;
  --el-radio-button-hover-bg-color: #FFE6ED !important;
  --el-radio-button-active-bg-color: #FF6B8B !important;
  --el-radio-button-active-text-color: #fff !important;
  --el-radio-button-border-color: #FFD1DC !important;
  border-radius: 20px !important;
  padding: 6px 16px !important;
  font-size: 14px !important;
  transition: all 0.3s ease !important;
}

/* 路由视图内容区 */
.router-view-content {
  min-height: 500px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .header-inner,
  .movie-main {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .header-inner {
    flex-direction: column;
    gap: 15px;
    padding: 0 15px;
  }

  .header-title {
    align-self: flex-start;
  }

  .nav-menu {
    width: 100%;
    overflow-x: auto;
    padding-bottom: 8px;
  }

  .menu-item {
    margin: 0 8px !important;
    white-space: nowrap;
  }

  .header-decoration {
    align-self: flex-end;
  }

  .tags-line {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .radio-group {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .title-text {
    font-size: 20px;
  }

  .tags-title {
    font-size: 14px;
  }

  .radio-btn {
    padding: 4px 12px !important;
    font-size: 13px !important;
  }
}
</style>
