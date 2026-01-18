<template>
  <div class="cinema-page">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="container">
        <i class="iconfont icon-r-film" style="font-size: 24px; color: #FF6B8B;"></i>
        <span class="nav-title">影院信息</span>
        <div class="navbar-decoration">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
        </div>
      </div>
    </div>

    <div class="container main-content">
      <!-- 影院基本信息 -->
      <div class="cinema-card">
        <div class="cinema-header">
          <h1 class="cinema-name">{{ cinemaInfo.cinemaName || '默片时光电影院' }}</h1>
        </div>

        <div class="cinema-details">
          <div class="detail-item">
            <i class="iconfont icon-r-location" style="color: #666;"></i>
            <span>地址：{{ cinemaInfo.cinemaAddress || '济南市' }}</span>
          </div>
          <div class="detail-item">
            <i class="iconfont icon-r-phone" style="color: #666;"></i>
            <span>电话：{{ cinemaInfo.cinemaPhone || '123456789' }}</span>
          </div>
        </div>

        <!-- 影厅类型标签 -->
        <div class="hall-types">
          <span class="tag-title">影厅类型：</span>
          <el-tag
            v-for="hall in halls"
            :key="hall"
            class="hall-tag"
          >
            {{ hall }}
          </el-tag>
        </div>
      </div>

      <!-- 影院图片展示 -->
      <div class="cinema-gallery">
        <h2 class="section-title">
          <i class="iconfont icon-r-picture" style="margin-right: 8px;"></i>影院环境
          <span class="title-decoration">✨</span>
        </h2>
        <div class="gallery-grid">
          <el-popover
            placement="top"
            trigger="click"
            width="800"
            v-for="(item, index) in pics"
            :key="index"
          >
            <img :src="item.url" :alt="`影院图片 ${index + 1}`" class="popup-img"/>
            <img
              slot="reference"
              :src="item.url"
              :alt="`影院图片 ${index + 1}`"
              class="thumbnail-img"
              @error="handleImageError($event)"
            />
          </el-popover>
          <!-- 当没有图片时显示默认图片 -->
          <div v-if="pics.length === 0" class="empty-gallery">
            <img src="https://picsum.photos/seed/cinema/400/300" alt="影院环境图" class="empty-img">
            <p class="empty-text">暂无影院图片哦~</p>
          </div>
        </div>
      </div>

      <!-- 服务说明 -->
      <div class="services-section">
        <h2 class="section-title">
          <i class="iconfont icon-r-service" style="margin-right: 8px;"></i>服务说明
          <span class="title-decoration">🎈</span>
        </h2>
        <div class="services-grid">
          <div class="service-card refund">
            <div class="service-icon">
              <i class="iconfont icon-r-refund"></i>
            </div>
            <div class="service-content">
              <h3 class="service-title">退票政策</h3>
              <p class="service-desc">本电影院暂不支持退票~请确认后再进行支付！感谢理解~</p>
            </div>
            <div class="service-decoration">💸</div>
          </div>

          <div class="service-card change">
            <div class="service-icon">
              <i class="iconfont icon-r-exchange"></i>
            </div>
            <div class="service-content">
              <h3 class="service-title">改签政策</h3>
              <p class="service-desc">本电影院暂不支持线上改签~请确认好观影时间，提前出发！如有特殊情况请联系人工线下服务~</p>
            </div>
            <div class="service-decoration">🔄</div>
          </div>

          <div class="service-card glasses">
            <div class="service-icon">
              <i class="iconfont icon-r-glasses"></i>
            </div>
            <div class="service-content">
              <h3 class="service-title">3D眼镜服务</h3>
              <p class="service-desc">免押金提供通用款3D眼镜使用，映后归还影城工作人员，损坏照价赔偿</p>
            </div>
            <div class="service-decoration">👓</div>
          </div>

          <div class="service-card child">
            <div class="service-icon">
              <i class="iconfont icon-r-child"></i>
            </div>
            <div class="service-content">
              <h3 class="service-title">儿童优惠</h3>
              <p class="service-desc">1m以下儿童观影免票，每位成年人仅限带一名儿童（不占座）</p>
            </div>
            <div class="service-decoration">👶</div>
          </div>

          <div class="service-card parking">
            <div class="service-icon">
              <i class="iconfont icon-r-car"></i>
            </div>
            <div class="service-content">
              <h3 class="service-title">停车信息</h3>
              <p class="service-desc">商场地下、地面有停车场，收费标注以物业公告为准</p>
            </div>
            <div class="service-decoration">🚗</div>
          </div>
        </div>
      </div>

      <!-- 可爱提示区 -->
      <div class="cute-notice">
        <div class="notice-content">
          <i class="iconfont icon-r-heart" style="color: #FF6B8B;"></i>
          <p>时光会说话，默片在发芽，期待您的光临~</p>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <p>© {{ new Date().getFullYear() }} {{ cinemaInfo.cinemaName || '默片时光电影院' }} 版权所有</p>
          <div class="footer-decorations">
            <span>🍿</span>
            <span>🎬</span>
            <span>🍭</span>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: "Cinema",
  data() {
    return {
      cinemaInfo: {},
      halls: [],
      pics: [],
      sorry: false,
      defaultImage: 'https://picsum.photos/seed/cinema/400/300' // 默认图片占位符
    }
  },
  created() {
    this.getCinemaInfo()
  },
  methods: {
    async getCinemaInfo(){
      try {
        const resp = await axios.get('sysCinema')
        this.cinemaInfo = resp.data.data || {}

        // 处理影院图片
        this.pics = []
        const pictures = JSON.parse(this.cinemaInfo.cinemaPicture || '[]')
        pictures.forEach((item, index) => {
          this.pics.push({
            id: index,
            url: this.global?.base ? this.global.base + item : this.defaultImage
          })
        })

        // 处理影厅类型，默认添加一些影厅类型如果数据为空
        const hallList = JSON.parse(this.cinemaInfo.hallCategoryList || '[]')
        this.halls = hallList.length > 0 ? hallList : ['普通厅', '3D厅', 'IMAX厅', 'VIP厅']
      } catch (error) {
        console.error('获取影院信息失败:', error)
        this.sorry = true
        // 出错时显示默认数据
        this.halls = ['普通厅', '3D厅', 'IMAX厅']
        this.pics = [
          { id: 1, url: this.defaultImage },
          { id: 2, url: 'https://picsum.photos/seed/cinema2/400/300' }
        ]
      }
    },
    toChooseSession(id){
      this.$router.push('/chooseSession/' + id)
    },
    handleImageError(event) {
      // 图片加载失败时显示默认图片
      event.target.src = this.defaultImage
    }
  }
}
</script>

<style scoped>
/* 基础样式 - 可爱风格 */
.cinema-page {
  background-color: #FFF9F9;
  min-height: 100vh;
  font-family: 'Comic Sans MS', 'Marker Felt', sans-serif;
  background-image:
    radial-gradient(#FFE0E0 0.5px, transparent 0.5px),
    radial-gradient(#FFE0E0 0.5px, #FFF9F9 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

/* 导航栏 - 可爱化处理 */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 15px rgba(255, 107, 139, 0.15);
  padding: 15px 0;
  margin-bottom: 30px;
  border-radius: 0 0 20px 20px;
}

.nav-title {
  font-size: 22px;
  font-weight: 700;
  color: #FF6B8B;
  margin-left: 10px;
}

.navbar-decoration {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #FFD1DC;
}

/* 主内容区 */
.main-content {
  padding-bottom: 60px;
}

/* 标题样式 - 可爱化 */
.section-title {
  font-size: 20px;
  color: #FF6B8B;
  padding-bottom: 8px;
  border-bottom: 2px dashed #FFB6C1;
  margin: 40px 0 25px;
  display: inline-block;
  position: relative;
}

.title-decoration {
  margin-left: 10px;
  font-size: 18px;
}

/* 影院卡片 - 可爱化 */
.cinema-card {
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 5px 20px rgba(255, 107, 139, 0.1);
  padding: 25px;
  margin-bottom: 30px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cinema-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
  background: linear-gradient(90deg, #FF6B8B, #FFB6C1, #FFD1DC, #FFB6C1, #FF6B8B);
  background-size: 200% 100%;
  animation: rainbow 5s ease infinite;
}

@keyframes rainbow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.cinema-card:hover {
  transform: translateY(-5px) rotate(0.5deg);
  box-shadow: 0 8px 30px rgba(255, 107, 139, 0.15);
}

.cinema-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.cinema-name {
  font-size: 26px;
  font-weight: 700;
  color: #FF6B8B;
  margin: 0;
  text-shadow: 1px 1px 3px rgba(255, 107, 139, 0.2);
}

.buy-tickets-btn {
  background-color: #FF6B8B;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 107, 139, 0.3);
}

.buy-tickets-btn:hover {
  background-color: #ff527a;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 15px rgba(255, 107, 139, 0.4);
}

/* 影院详情 */
.cinema-details {
  margin-bottom: 25px;
  background-color: #FFF0F3;
  padding: 15px;
  border-radius: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
  color: #666;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item i {
  margin-right: 10px;
  width: 20px;
  text-align: center;
  color: #FF6B8B;
}

/* 影厅类型 */
.hall-types {
  padding-top: 15px;
  border-top: 1px dashed #FFE0E0;
}

.tag-title {
  color: #999;
  margin-right: 10px;
  font-size: 14px;
}

.hall-tag {
  margin: 0 8px 8px 0 !important;
  background-color: #FFF0F3 !important;
  color: #FF6B8B !important;
  border-color: #FFD1DC !important;
  border-radius: 20px !important;
  padding: 5px 12px !important;
  font-size: 14px !important;
}

/* 图片画廊 */
.cinema-gallery {
  margin-bottom: 20px;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.thumbnail-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 15px;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  border: 3px solid white;
}

.thumbnail-img:hover {
  transform: scale(1.05) rotate(2deg);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.popup-img {
  width: 100%;
  height: auto;
  border-radius: 15px;
  border: 5px solid white;
}

/* 空画廊提示 */
.empty-gallery {
  grid-column: 1 / -1;
  text-align: center;
  padding: 30px;
  background-color: #FFF0F3;
  border-radius: 15px;
}

.empty-img {
  max-width: 300px;
  border-radius: 15px;
  margin-bottom: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.empty-text {
  color: #FF6B8B;
  font-size: 16px;
  margin: 0;
}

/* 服务说明区 - 可爱化 */
.services-section {
  margin-bottom: 40px;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.service-card {
  background-color: #fff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  display: flex;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}

.service-card:hover {
  transform: translateY(-8px) rotate(-1deg);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
}

.service-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
  font-size: 24px;
}

.service-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.service-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.service-decoration {
  position: absolute;
  bottom: 10px;
  right: 15px;
  font-size: 20px;
  opacity: 0.7;
}

/* 服务卡片颜色区分 - 更柔和可爱的色调 */
.refund .service-icon {
  background-color: #FFF0F0;
  color: #FF6B8B;
}

.change .service-icon {
  background-color: #FFF7E6;
  color: #FFB85C;
}

.glasses .service-icon {
  background-color: #F0F9EB;
  color: #7CCC56;
}

.child .service-icon {
  background-color: #E6F7FF;
  color: #5BC8AF;
}

.parking .service-icon {
  background-color: #F9F0FF;
  color: #B388EB;
}

/* 可爱提示区 */
.cute-notice {
  background-color: #FFF0F3;
  border-radius: 20px;
  padding: 20px;
  margin: 30px 0;
  text-align: center;
  box-shadow: 0 5px 15px rgba(255, 107, 139, 0.1);
}

.notice-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #FF6B8B;
  font-size: 16px;
}

/* 页脚 - 可爱化 */
.footer {
  background-color: #fff;
  color: #FF6B8B;
  padding: 30px 0 20px;
  margin-top: 50px;
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -2px 15px rgba(255, 107, 139, 0.1);
}

.footer-content {
  text-align: center;
}

.footer p {
  margin: 0 0 15px 0;
  font-size: 14px;
}

.footer-decorations {
  display: flex;
  justify-content: center;
  gap: 15px;
  font-size: 20px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .services-grid {
    grid-template-columns: 1fr;
  }

  .gallery-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .cinema-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .buy-tickets-btn {
    margin-top: 15px;
    width: 100%;
  }
}
</style>
