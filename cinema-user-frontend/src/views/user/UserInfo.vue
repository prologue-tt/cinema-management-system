<template>
  <div class="user-info-container">
    <!-- 页面标题 -->
    <div class="page-title">
      <i class="iconfont icon-r-user" style="font-size: 24px; color: #FF6B8B; margin-right: 10px;"></i>
      <h2>个人信息中心</h2>
    </div>

    <div class="content-wrapper">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <h3 class="section-title">
          <i class="iconfont icon-r-photo" style="margin-right: 8px; color: #FF6B8B;"></i>
          用户头像
        </h3>
        <el-upload
          action=""
          list-type="picture-card"
          :auto-upload="false"
          :limit="1"
          :file-list="pics"
          :on-change="handleChange"
          :on-success="handleSuccess"
          :on-error="handleError"
          :on-exceed="handleExceed"
          ref="pictureRef"
          :http-request="submitFile"
          class="avatar-uploader"
        >
          <i slot="default" class="el-icon-plus avatar-upload-plus"></i>
          <div slot="file" slot-scope="{file}" class="avatar-file">
            <img
              class="el-upload-list__item-thumbnail avatar-img"
              :src="(picDialog? '' : global.base)+file.url"
              alt="用户头像"
            >
            <span class="el-upload-list__item-actions avatar-actions">
            <span class="el-upload-list__item-delete" @click="handleRemove(file)">
              <i class="el-icon-delete"></i>
            </span>
          </span>
          </div>
        </el-upload>

        <p class="avatar-hint">点击上传或更换头像（仅支持单张图片）</p>
      </div>

      <!-- 电影偏好评分条区域 -->
      <div class="movie-preference-section">
        <h3 class="section-title">
          <i class="iconfont icon-r-film" style="margin-right: 8px; color: #FF6B8B;"></i>
          电影类型偏好
        </h3>

        <div class="preference-container">
          <!-- 平均偏好度 -->
<!--          <div v-if="movieTypeData && Object.keys(movieTypeData).length > 0" class="average-preference">-->
<!--            <span class="average-label">平均偏好度</span>-->
<!--            <span class="average-score">{{ averagePreference.toFixed(1) }}</span>-->
<!--          </div>-->

          <!-- 偏好类型列表 -->
          <div v-if="movieTypeData && Object.keys(movieTypeData).length > 0" class="preference-list">
            <div v-for="(count, type) in movieTypeData" :key="type" class="preference-item">
              <div class="type-name">{{ type }}</div>
              <div class="progress-bar-container">
                <div
                  class="progress-bar"
                  :style="{ width: `${getPercentage(count)}%`, backgroundColor: getTypeColor(type) }"
                ></div>
              </div>
              <div class="type-count">{{ count }}次</div>
            </div>
          </div>

          <!-- 无数据状态 -->
          <div v-else class="no-preference-data">
            <i class="el-icon-film" style="font-size: 24px; color: #ddd; margin-bottom: 10px;"></i>
            <p>暂无观影记录</p>
          </div>
        </div>

        <p class="preference-hint">基于您的电影票购买记录生成（展示TOP3类型+其他）</p>
      </div>

      <!-- 信息表单区域 -->
      <div class="info-form-section">
        <h3 class="section-title">
          <i class="iconfont icon-r-edit" style="margin-right: 8px; color: #FF6B8B;"></i>
          个人资料
        </h3>

        <el-form
          :model="loginUser"
          :rules="loginUserRules"
          ref="editFormRef"
          label-width="100px"
          class="user-info-form"
        >
          <el-form-item label="用户名" prop="userName">
            <el-input v-model="loginUser.userName" class="custom-input"></el-input>
          </el-form-item>

          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input v-model="loginUser.phoneNumber" class="custom-input"></el-input>
          </el-form-item>

          <el-form-item label="性别" prop="sex">
            <div class="gender-options">
              <el-radio v-model="loginUser.sex" :label="true" class="gender-radio">
                <i class="iconfont icon-r-male" style="margin-right: 5px;"></i>男
              </el-radio>
              <el-radio v-model="loginUser.sex" :label="false" class="gender-radio">
                <i class="iconfont icon-r-female" style="margin-right: 5px;"></i>女
              </el-radio>
            </div>
          </el-form-item>

          <el-form-item label="生日" prop="birthday">
            <el-date-picker
              v-model="loginUser.birthday"
              value-format="yyyy-MM-dd"
              type="date"
              placeholder="选择日期"
              class="custom-datepicker"
            ></el-date-picker>
          </el-form-item>

          <el-form-item label="个性签名" prop="autograph">
            <el-input
              v-model="loginUser.autograph"
              class="custom-input"
              placeholder="说点什么介绍自己吧~"
            ></el-input>
          </el-form-item>

          <el-form-item class="submit-btn-container">
            <el-button
              type="primary"
              @click="editUser"
              icon="iconfont icon-r-yes"
              class="save-btn"
            >
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <div class="decor-circle pink"></div>
      <div class="decor-circle yellow"></div>
      <div class="decor-circle blue"></div>
    </div>
  </div>
</template>

<script>
// 移除了echarts导入
export default {
  name: "UserInfo",
  data() {
    let checkEmail = (rule, value, cb) => {
      const regEmail = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/
      if (regEmail.test(value)){
        return cb()
      }
      cb(new Error('请输入合法的邮箱'))
    }
    let checkMobile = (rule, value, cb) => {
      const regMobile = /^(0|86|17951)?(13[0-9]|15[0123456789]|17[678]|18[0-9]|14[57])[0-9]{8}$/
      if (regMobile.test(value)){
        return cb()
      }
      cb(new Error('请输入合法的手机号码'))
    }
    return {
      picDialog: false,
      loginUser: {},
      pics: [],
      pictureList: [],
      picSrc: "",
      deletePicList: [],
      movieTypeData: null,
      maxCount: 0, // 用于计算百分比的最大次数
      averagePreference: 0, // 平均偏好度

      // 电影类型对应的颜色，确保和样式中的一致
      typeColors: {
        '动作': '#FF6B8B',
        '喜剧': '#FFD04B',
        '爱情': '#FF9BB3',
        '科幻': '#87CEEB',
        '悬疑': '#B8E986',
        '恐怖': '#C59AFF',
        '动画': '#FFB8D1',
        '纪录片': '#BBD1EA'
      },

      //验证表单规则对象
      loginUserRules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur'}
        ],
        phoneNumber: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { validator: checkMobile, trigger: 'blur'}
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        birthday: [
          { required: true, message: '请选择生日', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.getUser()
  },
  methods:{
    async getUser(){
      const { data : res } = await axios.get('sysUser/'+JSON.parse(window.sessionStorage.getItem('loginUser')).userId)
      this.loginUser = res.data
      this.pics.push({name:'picture',url:JSON.parse(this.loginUser.userPicture)[0]})
      this.loadMoviePreferenceData()
    },
    async handleSuccess(response, file, fileList){
      this.pics = fileList
      console.log(this.pics)
      await this.submitFile()
      this.picSrc = this.pictureList[0]
    },
    handleError(err){
      console.log(err)
    },
    handleChange(file, filelist){
      this.pics = filelist.slice(0)
      console.log(111)
      console.log(this.pics)
      this.picDialog = true
    },
    handleRemove(file) {
      const filePath = file.url
      console.log(filePath)
      const idx = this.pics.findIndex(x => x.url === filePath)
      if(file.status === 'success'){
        this.deletePicList.push(file.url)
      }
      this.pics.splice(idx, 1)
    },
    async editUser(){
      await this.submitFile()
      this.loginUser.userPicture = JSON.stringify(this.pictureList)
      const _this = this
      let success = true
      axios.defaults.headers.put['Content-Type'] = 'application/json'
      await axios.put('sysUser',JSON.stringify(this.loginUser)).then(resp=>{
        if (resp.data.code !== 200){
          this.$message.error('修改用户信息失败！')
          success = false
        }
      })
      if(!success) return
      this.$message.success('修改用户信息成功!')
      this.pics = []
      this.pictureList = []
      await this.getUser()
      this.picDialog = false
    },
    async submitFile(){
      const _this = this
      for (let i = 0; i < this.pics.length; i++){
        let formData = new FormData()
        if (this.pics[i].status === 'success') {
          let s = this.pics[i].url
          this.pictureList.push(s.substring(s.indexOf('/images')))
          continue
        }
        let file = this.pics[i].raw
        formData.append('file', file)
        await axios.post('upload/user', formData).then(response =>{
          console.log(response)
          _this.pictureList.push(response.data.data)
        })
      }
    },
    handleExceed(){
      this.$message.error("用户头像最多上传一张！")
    },

    // 加载电影偏好数据
    async loadMoviePreferenceData() {
      try {
        // 获取当前用户ID
        const userId = JSON.parse(window.sessionStorage.getItem('loginUser')).userId
        const { data: res } = await axios.get(`/sysMovieToCategorys/${userId}`)

        if (res.code === 200 && res.data) {
          this.movieTypeData = res.data
          this.calculatePreferenceStats()
        } else {
          this.movieTypeData = null
        }
      } catch (error) {
        console.error('获取电影偏好数据失败:', error)
        this.movieTypeData = null
      }
    },

    // 计算偏好统计数据
    calculatePreferenceStats() {
      if (!this.movieTypeData) return

      // 获取所有类型的观影次数
      const counts = Object.values(this.movieTypeData)

      // 找到最大次数，用于计算百分比
      this.maxCount = Math.max(...counts)

      // 计算平均偏好度（1-5分）
      const total = counts.reduce((sum, count) => sum + count, 0)
      const avgCount = total / counts.length
      this.averagePreference = 1 + (avgCount / this.maxCount) * 4 // 映射到1-5分范围
    },

    // 获取类型的百分比宽度
    getPercentage(count) {
      return (count / this.maxCount) * 100
    },

    // 获取类型对应的颜色
    getTypeColor(type) {
      // 如果有预定义颜色则使用，否则随机从颜色列表中取一个
      return this.typeColors[type] || Object.values(this.typeColors)[
        Math.floor(Math.random() * Object.values(this.typeColors).length)
        ]
    }
  },
  beforeDestroy() {
    // 移除事件监听
    window.removeEventListener('resize', () => {})
  }
}
</script>

<style scoped>
/* 基础容器样式 */
.user-info-container {
  width: 100%;
  min-height: 100vh;
  background-color: #FFF9F9;
  padding: 30px 20px;
  box-sizing: border-box;
  /* 可爱背景图案 */
  background-image:
    radial-gradient(#FFE0E0 0.5px, transparent 0.5px),
    radial-gradient(#FFE0E0 0.5px, #FFF9F9 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

/* 页面标题 */
.page-title {
  text-align: center;
  margin-bottom: 40px;
  color: #333;
  padding: 15px;
}

.page-title h2 {
  display: inline-block;
  font-size: 26px;
  margin: 0;
  text-shadow: 1px 1px 3px rgba(255, 107, 139, 0.1);
}

/* 内容容器 */
.content-wrapper {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  gap: 40px;
}

/* 头像区域 */
.avatar-section {
  flex: 0 0 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 区域标题 */
.section-title {
  color: #333;
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 8px;
  border-bottom: 2px solid #FFE0E0;
  width: 100%;
  display: flex;
  align-items: center;
}

/* 头像上传器 */
.avatar-uploader {
  margin-bottom: 20px;
}

.avatar-upload-plus {
  font-size: 30px !important;
  color: #FFB6C1 !important;
  width: 180px !important;
  height: 180px !important;
  line-height: 180px !important;
  border-color: #FFD1DC !important;
  border-width: 2px !important;
  border-style: dashed !important;
  border-radius: 50% !important;
  transition: all 0.3s ease !important;
}

.avatar-upload-plus:hover {
  background-color: #FFF0F3 !important;
  border-color: #FF6B8B !important;
}

.avatar-file {
  width: 180px !important;
  height: 180px !important;
  border-radius: 50% !important;
  overflow: hidden !important;
  border: 3px solid #FFF0F3 !important;
  box-shadow: 0 4px 10px rgba(255, 107, 139, 0.1) !important;
}

.avatar-img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
}

.avatar-actions {
  background-color: rgba(0, 0, 0, 0.3) !important;
  opacity: 0 !important;
  transition: opacity 0.3s ease !important;
}

.avatar-file:hover .avatar-actions {
  opacity: 1 !important;
}

.avatar-hint {
  color: #999;
  font-size: 14px;
  text-align: center;
  margin: 0;
  padding: 0 10px;
}

/* 表单区域 */
.info-form-section {
  flex: 1;
  background-color: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(255, 107, 139, 0.1);
}

.user-info-form {
  margin-top: 20px;
}

/* 自定义输入框 */
.custom-input {
  border-radius: 15px !important;
  height: 45px !important;
  border-color: #FFD1DC !important;
  transition: all 0.3s ease !important;
}

.custom-input:focus {
  border-color: #FF6B8B !important;
  box-shadow: 0 0 0 2px rgba(255, 107, 139, 0.2) !important;
}

/* 性别选项 */
.gender-options {
  display: flex;
  gap: 20px;
  padding-top: 5px;
}

.gender-radio {
  color: #666 !important;
  font-size: 15px !important;
  display: flex !important;
  align-items: center !important;
}

/* 日期选择器 */
.custom-datepicker {
  width: 100% !important;
  border-radius: 15px !important;
  height: 45px !important;
  border-color: #FFD1DC !important;
}

.custom-datepicker:focus-within {
  border-color: #FF6B8B !important;
  box-shadow: 0 0 0 2px rgba(255, 107, 139, 0.2) !important;
}

/* 提交按钮 */
.submit-btn-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.save-btn {
  background-color: #FF6B8B !important;
  border-color: #FF6B8B !important;
  padding: 10px 40px !important;
  border-radius: 25px !important;
  font-size: 16px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 10px rgba(255, 107, 139, 0.2) !important;
}

.save-btn:hover {
  background-color: #ff527a !important;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 107, 139, 0.3) !important;
}

/* 底部装饰 */
.footer-decoration {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 60px;
}

.decor-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.pink {
  background-color: #FF6B8B;
}

.yellow {
  background-color: #FFD04B;
}

.blue {
  background-color: #87CEEB;
}

/* 电影偏好区域 */
.movie-preference-section {
  flex: 0 0 200px;
  background-color: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(255, 107, 139, 0.1);
}

/* 偏好容器样式 */
.preference-container {
  width: 100%;
  padding: 10px 0;
}

/* 平均偏好度 */
.average-preference {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #FFE0E0;
}

.average-label {
  color: #666;
  font-size: 14px;
  margin-right: 10px;
}

.average-score {
  font-size: 28px;
  font-weight: bold;
  color: #FF6B8B;
}

/* 偏好列表 */
.preference-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 单个偏好项 */
.preference-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 类型名称 */
.type-name {
  flex: 0 0 80px;
  font-size: 15px;
  color: #333;
  text-align: right;
}

/* 进度条容器 */
.progress-bar-container {
  flex: 1;
  height: 12px;
  background-color: #f5f5f5;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
}

/* 进度条 */
.progress-bar {
  height: 100%;
  border-radius: 6px;
  transition: width 0.5s ease;
}

/* 观影次数 */
.type-count {
  flex: 0 0 60px;
  font-size: 14px;
  color: #999;
  text-align: left;
}

/* 无数据状态 */
.no-preference-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #999;
}

/* 提示文字 */
.preference-hint {
  color: #999;
  font-size: 14px;
  text-align: center;
  margin-top: 30px;
  line-height: 1.5;
  padding-top: 15px;
  border-top: 1px dashed #FFE0E0;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .content-wrapper {
    flex-direction: column;
    align-items: center;
  }

  .avatar-section {
    flex: none;
    width: 100%;
    max-width: 300px;
  }

  .movie-preference-section {
    flex: none;
    width: 100%;
    max-width: 400px;
  }
}

@media (max-width: 576px) {
  .info-form-section {
    padding: 20px 15px;
  }

  .section-title {
    font-size: 16px;
  }

  .save-btn {
    width: 100%;
  }

  .gender-options {
    flex-direction: column;
    gap: 10px;
  }

  .preference-item {
    gap: 10px;
  }

  .type-name {
    flex: 0 0 60px;
    font-size: 14px;
  }

  .type-count {
    flex: 0 0 50px;
    font-size: 13px;
  }
}
</style>
