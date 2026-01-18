<template>
  <div class="cinema-management-container">

    <!-- 卡片视图 -->
    <el-card class="box-card cinema-card">
      <!-- 标题区域 -->
      <div class="page-title">
        <h1>影院信息详情</h1>
        <p>查看和管理影院的基本信息</p>
      </div>

      <!-- 表格显示影院信息 -->
      <el-form :model="cinemaInfo" label-width="150px" class="cinema-info-form">
        <el-form-item label="影院名称: " prop="cinemaName" class="info-form-item">
          <el-input
            class="el-input-show"
            v-model="cinemaInfo.cinemaName"
            disabled
            placeholder="暂无影院名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="影院地址: " prop="cinemaAddress" class="info-form-item">
          <el-input
            class="el-input-show"
            v-model="cinemaInfo.cinemaAddress"
            disabled
            placeholder="暂无影院地址"
          ></el-input>
        </el-form-item>
        <el-form-item label="影院电话: " prop="cinemaPhone" class="info-form-item">
          <el-input
            class="el-input-show"
            v-model="cinemaInfo.cinemaPhone"
            disabled
            placeholder="暂无影院电话"
          ></el-input>
        </el-form-item>
        <el-form-item label="营业时间: " prop="cinemaPhone" class="info-form-item time-form-item">
          <el-input
            class="el-input-show-time"
            v-model="cinemaInfo.workStartTime"
            disabled
            placeholder="开始时间"
          ></el-input>
          <span class="time-separator">至</span>
          <el-input
            class="el-input-show-time"
            v-model="cinemaInfo.workEndTime"
            disabled
            placeholder="结束时间"
          ></el-input>
        </el-form-item>
        <el-form-item label="拥有影厅类型: " prop="hallCategory" class="info-form-item hall-form-item">
          <div class="hall-tag-container">
            <el-tag
              v-for="hall in halls"
              :key="hall.id"
              class="hall-tag"
            >{{hall}}</el-tag>
            <span v-if="halls.length === 0" class="empty-tip">暂无影厅类型数据</span>
          </div>
        </el-form-item>
        <el-form-item label="影院图片: " class="info-form-item pic-form-item">
          <div class="pic-container">
            <span v-for="item in pics" :key="item.id" class="pic-item">
              <el-popover
                placement="left"
                trigger="click"
                width="300"
                class="pic-popover"
              >
                <img :src="item.url" width="200%" class="popover-img" />
                <img
                  slot="reference"
                  :src="item.url"
                  :alt="item"
                  style="max-height: 300px;max-width: 300px;padding: 10px"
                  class="reference-img"
                  loading="lazy"
                />
              </el-popover>
            </span>
            <span v-if="pics.length === 0" class="empty-pic-tip">
              <i class="el-icon-picture empty-icon"></i>
              <span class="empty-text">暂无影院图片数据</span>
            </span>
          </div>
        </el-form-item>
        <el-form-item label="" prop="cinemaName" class="info-form-item btn-form-item">
          <el-button
            type="primary"
            @click="showEditDialog"
            class="edit-btn"
            icon="el-icon-edit"
          >
            修改影院信息
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!--修改影院对话框-->
    <el-dialog title="修改影院" :visible.sync="editDialogVisible" width="60%" @close="editDialogClosed" custom-class="edit-dialog">
      <div class="dialog-scroll-container">
        <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="150px">
          <el-form-item label="影院名称" prop="cinemaName">
            <el-input v-model="editForm.cinemaName" class="edit-input"></el-input>
          </el-form-item>
          <el-form-item label="影院地址" prop="cinemaAddress">
            <el-input v-model="editForm.cinemaAddress" class="edit-input"></el-input>
          </el-form-item>
          <el-form-item label="影院电话" prop="cinemaPhone">
            <el-input v-model="editForm.cinemaPhone" class="edit-input"></el-input>
          </el-form-item>
          <el-form-item label="开始营业时间" prop="workStartTime">
            <el-time-picker
              v-model="editForm.workStartTime"
              value-format="HH:mm"
              placeholder="选择开始营业时间"
              class="time-picker"
            ></el-time-picker>
          </el-form-item>
          <el-form-item label="结束营业时间" prop="workEndTime">
            <el-time-picker
              v-model="editForm.workEndTime"
              value-format="HH:mm"
              placeholder="选择结束营业时间"
              class="time-picker"
            ></el-time-picker>
          </el-form-item>
          <el-form-item label="拥有影厅类型" prop="hallCategoryList" class="hall-edit-item">
            <el-input class="el-input-hall" placeholder="请输入添加影厅类别名称" v-model="inputHall" clearable></el-input>
            <el-button type="primary" @click="addHallCategory()" class="add-hall-btn">添加</el-button>
          </el-form-item>
          <el-form-item class="hall-tag-item">
            <div class="edit-hall-tag-container">
              <el-tag
                v-for="(item, index) in halls"
                :key="index"
                closable
                @close="deleteHallCategory(index)"
                class="edit-hall-tag"
              >{{item}}</el-tag>
              <span v-if="halls.length === 0" class="edit-empty-tip">暂无影厅类型数据</span>
            </div>
          </el-form-item>

          <el-form-item label="影院图片" class="pic-edit-item">
            <el-upload action="" list-type="picture-card"
                       :auto-upload="false"
                       :file-list="pics"
                       :on-change="handleChange"
                       :on-success="handleSuccess"
                       :on-error="handleError"
                       ref="pictureEditRef"
                       :http-request="submitFile"
                       class="pic-upload">
              <i slot="default" class="el-icon-plus upload-plus-icon"></i>
              <div slot="file" slot-scope="{file}" class="upload-file-item">
                <img class="el-upload-list__item-thumbnail upload-thumbnail"
                     :src="file.url" alt="">
                <span class="el-upload-list__item-actions upload-item-actions">
                  <span class="el-upload-list__item-preview upload-item-preview"
                        @click="handlePictureCardPreview(file)">
                    <i class="el-icon-zoom-in"></i>
                  </span>
                  <span class="el-upload-list__item-delete upload-item-delete"
                        @click="handleRemove(file)">
                    <i class="el-icon-delete"></i>
                  </span>
                </span>
              </div>
            </el-upload>
            <p class="upload-tip">点击加号上传图片，最多上传5张</p>
            <!--放大预览-->
            <el-dialog :visible.sync="dialogVisible" append-to-body class="pic-preview-dialog">
              <img width="100%" :src="dialogImageUrl" alt="" class="preview-img">
            </el-dialog>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer edit-dialog-footer">
        <el-button @click="cancelEdit" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="editCinemaInfo" class="confirm-btn" icon="iconfont icon-sure-button"> 确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import global from "../../assets/css/global.css"

export default {
  data() {
    return {
      // 控制对话框的显示与隐藏
      editDialogVisible: false,
      editForm: {},
      cinemaInfo: {},
      editFormRules: {
        cinemaName: [
          { required: true, message: '请输入影院名称', trigger: 'change' }
        ],
        cinemaAddress: [
          { required: true, message: '请输入影院地址', trigger: 'change' }
        ],
        cinemaPhone: [
          { required: true, message: '请输入影院电话', trigger: 'change' },

        ]
      },
      inputHall: '',

      dialogImageUrl: '',
      dialogVisible: false,
      hideUpload: false,
      // 添加删除图片 动态绑定图片列表
      pics: [],
      // 添加删除影厅类别 动态绑定影厅列表
      halls: [],
      // 发送给后端的JSON图片数组
      pictureList: [],
      picNums: 0,
      deletePicList: []
    }
  },
  created() {
    this.getCinemaInfo()
  },
  methods: {
    async getCinemaInfo() {
      const _this = this
      // 加载状态提示
      // this.$message.info('正在加载影院信息...')
      try {
        const resp = await axios.get('sysCinema')
        _this.cinemaInfo = resp.data.data
        _this.pics = []
        _this.halls = []

        // 处理影院图片数据
        const pictureData = _this.cinemaInfo.cinemaPicture ? JSON.parse(_this.cinemaInfo.cinemaPicture) : []
        for (const item of pictureData) {
          let pic = {}
          pic['name'] = ''
          pic['url'] = this.global.base + item
          this.pics.push(pic)
        }

        // 处理影厅类型数据
        const hallData = _this.cinemaInfo.hallCategoryList ? JSON.parse(_this.cinemaInfo.hallCategoryList) : []
        for (const item of hallData) {
          this.halls.push(item)
        }
        // this.$message.success('影院信息加载成功！')
      } catch (error) {
        this.$message.error('影院信息加载失败，请稍后重试！')
        console.error('加载影院信息错误：', error)
      }
    },
    // 显示修改对话框，回显数据
    async showEditDialog() {
      this.editForm = this.cinemaInfo
      this.editDialogVisible = true
    },
    // 监听修改对话框的关闭事件
    editDialogClosed() {
      this.$refs.editFormRef.resetFields()
      this.$refs.pictureEditRef.clearFiles()
      this.pics = []
      this.pictureList = []
      this.halls = []
      this.getCinemaInfo()
    },
    // 取消修改
    cancelEdit() {
      this.editDialogVisible = false
      this.deletePicList.splice(0, this.deletePicList.length)
    },
    async editCinemaInfo() {
      await this.submitFile()
      this.editForm.cinemaPicture = JSON.stringify(this.pictureList)
      this.editForm.hallCategoryList = JSON.stringify(this.halls)
      this.$refs.editFormRef.validate(async valid => {
        const _this = this
        if (!valid) return
        let success = true
        axios.defaults.headers.put['Content-Type'] = 'application/json'
        await axios.put('sysCinema/update', JSON.stringify(_this.editForm)).then(resp => {
          if (resp.data.code !== 200){
            this.$message.error('修改影院信息失败！')
            success = false
          }
        })
        if (!success) return
        this.editDialogVisible = false
        await this.getCinemaInfo()
        this.$message.success('修改影院信息成功！')
        for(let item of this.deletePicList) {
          await axios.get('/upload/delete?filePath=' + item.substring(item.indexOf('/images')))
        }
      })
    },
    addHallCategory() {
      if (this.inputHall === '' || this.inputHall === null) {
        this.$alert('影厅类别添加失败！原因：所添加的影厅类别不能为空。', '影厅类别添加异常', {
          confirmButtonText: '我知道了'
        })
        return
      } else if (!this.halls.includes(this.inputHall)) {
        this.halls.push(this.inputHall)
      } else {
        console.log('已存在')
        this.$alert('影厅类别添加失败！原因：所添加的影厅类别已存在。', '影厅类别添加异常', {
          confirmButtonText: '我知道了'
        })
      }
      this.inputHall = ''
    },
    deleteHallCategory(index) {
      this.halls.splice(index, 1)
      console.log(this.halls)
    },
    handleRemove(file, fileList) {
      const filePath = file.url
      console.log(filePath)
      const idx = this.pics.findIndex(x => x.url === filePath)
      if (file.status === 'success') {
        this.deletePicList.push(file.url)
      }
      this.pics.splice(idx, 1)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    handleChange(file, filelist){
      this.pics = filelist.slice(0)
    },
    handleSuccess(response){
      this.pictureList.push(response.data)
      this.editForm = JSON.stringify(this.pictureList)
    },
    handleError(err){
      console.log(err)
    },
    async submitFile() {
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
        await axios.post('upload/cinema', formData).then(response =>{
          _this.pictureList.push(response.data.data)
        })
      }
    }
  }
}
</script>

<style scoped>
/* 全局容器样式 - 清新可爱风格基础 */
.cinema-management-container {
  padding: 20px;
  background-color: #fff5f7; /* 浅粉色背景 */
  min-height: calc(100vh - 64px); /* 适配顶部导航栏高度 */
  color: #5a5a5a;
}

/* 面包屑导航样式 */
.breadcrumb-container {
  margin-bottom: 20px;
  padding: 10px 15px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(255, 183, 197, 0.2);
}
.breadcrumb {
  font-size: 14px;
}
.breadcrumb-item {
  color: #ff8fab;
  transition: color 0.3s, transform 0.2s;
}
.breadcrumb-item:hover {
  color: #ff6b8b;
  transform: translateY(-1px);
}
.active-breadcrumb {
  color: #ff6b8b;
  font-weight: 600;
}

/* 标题区域样式 */
.page-title {
  margin-bottom: 30px;
  text-align: center;
  padding: 15px;
  background-color: #fff0f3;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(255, 153, 170, 0.15);
}

.page-title h1 {
  font-size: 1.8rem;
  margin-bottom: 10px;
  color: #ff6b8b;
  font-weight: 600;
}

.page-title p {
  color: #ff8fab;
  font-size: 0.95rem;
}

/* 卡片样式优化 - 清新可爱风格 */
.cinema-card {
  border-radius: 16px !important;
  box-shadow: 0 5px 15px rgba(255, 183, 197, 0.15) !important;
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  background-color: white;
}
.cinema-card:hover {
  box-shadow: 0 8px 25px rgba(255, 183, 197, 0.2) !important;
  transform: translateY(-3px);
}

/* 影院信息表单样式 */
.cinema-info-form {
  padding: 30px;
}
.info-form-item {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}
.info-form-item:last-child {
  margin-bottom: 0;
}
.el-input-show {
  width: 420px;
  background-color: #fff0f3;
  border-color: #ffccd5;
  color: #5a5a5a;
  border-radius: 8px;
  transition: all 0.3s ease;
}
.el-input-show:focus {
  border-color: #ffb3c1;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2);
}

/* 时间选择器样式 */
.time-form-item {
  display: flex;
  align-items: center;
  gap: 16px;
}
.el-input-show-time {
  width: 100px;
  background-color: #fff0f3;
  border-color: #ffccd5;
  border-radius: 8px;
}
.time-separator {
  color: #ff8fab;
  font-size: 16px;
  font-weight: 500;
}

/* 影厅类型样式 */
.hall-form-item {
  align-items: flex-start;
}
.hall-tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 4px;
}
.hall-tag {
  background-color: #ffe6f0;
  color: #ff6b8b;
  border-color: #ffccd5;
  border-radius: 20px;
  padding: 5px 15px;
  font-size: 14px;
  transition: all 0.3s ease;
}
.hall-tag:hover {
  background-color: #ffccd5;
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(255, 183, 197, 0.3);
}

/* 图片展示样式 */
.pic-form-item {
  align-items: flex-start;
}
.pic-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 4px;
}
.pic-item {
  display: inline-block;
}
.reference-img {
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 2px solid #fff0f3;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
}
.reference-img:hover {
  transform: scale(1.03) rotate(1deg);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  border-color: #ffccd5;
}
.popover-img {
  border-radius: 8px;
}
.empty-pic-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ff8fab;
  font-size: 14px;
  padding: 30px 20px;
  background-color: #fff0f3;
  border-radius: 12px;
  border: 1px dashed #ffccd5;
}
.empty-icon {
  font-size: 20px;
}

/* 按钮样式 - 清新可爱风格 */
.btn-form-item {
  margin-top: 16px;
  justify-content: center;
}
.edit-btn {
  padding: 10px 24px;
  border-radius: 50px;
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  font-size: 15px;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgba(163, 217, 165, 0.3) !important;
}
.edit-btn:hover {
  background-color: #85c787 !important;
  transform: translateY(-3px) !important;
  box-shadow: 0 4px 10px rgba(163, 217, 165, 0.4) !important;
}

/* 编辑对话框样式 */
.edit-dialog {
  border-radius: 16px !important;
  overflow: hidden !important;
}
.edit-dialog .el-dialog__header {
  background-color: #fff0f3 !important;
  border-bottom: 1px solid #ffe6eb !important;
  padding: 16px 24px !important;
}
.edit-dialog .el-dialog__title {
  font-size: 18px !important;
  font-weight: 600 !important;
  color: #ff6b8b !important;
}
.edit-dialog .el-dialog__body {
  padding: 0 !important; /* 去掉默认内边距，由滚动容器控制 */
  max-height: 70vh; /* 限制最大高度为视口的70% */
  overflow: hidden !important; /* 隐藏默认滚动 */
}
/* 滚动容器样式 */
.dialog-scroll-container {
  padding: 24px;
  height: 100%;
  max-height: 70vh; /* 与对话框body保持一致 */
  overflow-y: auto; /* 仅在内容溢出时显示垂直滚动条 */
  scrollbar-width: thin; /* 优化滚动条样式 */
}
/* 滚动条美化 */
.dialog-scroll-container::-webkit-scrollbar {
  width: 6px;
}
.dialog-scroll-container::-webkit-scrollbar-track {
  background: #fff0f3;
  border-radius: 10px;
}
.dialog-scroll-container::-webkit-scrollbar-thumb {
  background: #ffccd5;
  border-radius: 10px;
}
.dialog-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #ffb3c1;
}
.edit-dialog-footer {
  padding: 16px 24px !important;
  border-top: 1px solid #ffe6eb !important;
  background-color: #fff0f3 !important;
}

/* 编辑表单样式 */
.edit-form {
  width: 100%;
}
.edit-form-item {
  margin-bottom: 20px;
}
.edit-form-item:last-child {
  margin-bottom: 0;
}
.edit-input {
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s ease !important;
}
.edit-input:focus {
  border-color: #ff9aa2 !important;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2) !important;
}

/* 影厅编辑样式 */
.hall-edit-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.el-input-hall {
  width: 240px;
  margin: 0;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
}
.add-hall-btn {
  padding: 8px 16px !important;
  border-radius: 8px !important;
  background-color: #bde0fe !important;
  border-color: #bde0fe !important;
  color: #4a90e2 !important;
  transition: all 0.3s ease !important;
}
.add-hall-btn:hover {
  background-color: #a8d1fc !important;
  transform: translateY(-2px) !important;
}
.hall-tag-item {
  margin-top: -8px;
}
.edit-hall-tag-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}
.edit-hall-tag {
  background-color: #ffe6f0 !important;
  color: #ff6b8b !important;
  border-color: #ffccd5 !important;
  border-radius: 20px !important;
  padding: 4px 12px !important;
  transition: all 0.3s ease !important;
}
.edit-hall-tag:hover {
  background-color: #ffccd5 !important;
  transform: translateY(-2px) !important;
}
.edit-empty-tip {
  color: #ff8fab;
  font-size: 14px;
  padding: 8px 0;
}

/* 时间选择器样式 */
.time-picker {
  width: 180px;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
}

/* 图片上传样式 */
.pic-edit-item {
  margin-top: 12px;
}
.pic-upload {
  margin-top: 8px;
}
.upload-plus-icon {
  font-size: 24px;
  color: #ff8fab;
  transition: all 0.3s ease;
}
.upload-plus-icon:hover {
  color: #ff6b8b;
  transform: scale(1.1);
}
.upload-file-item {
  border-radius: 12px !important;
  overflow: hidden !important;
  transition: all 0.3s ease !important;
}
.upload-file-item:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1) !important;
}
.upload-thumbnail {
  border-radius: 12px !important;
}
.upload-item-actions {
  opacity: 0;
  transition: opacity 0.3s;
  background: rgba(0, 0, 0, 0.5);
}
.upload-file-item:hover .upload-item-actions {
  opacity: 1;
}
.upload-item-preview,
.upload-item-delete {
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #ff8fab;
}

/* 图片预览弹窗样式 */
.pic-preview-dialog .el-dialog__body {
  padding: 16px !important;
  text-align: center;
}
.preview-img {
  border-radius: 12px;
  max-height: 80vh;
  object-fit: contain;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
}

/* 按钮样式优化 - 清新可爱风格 */
.cancel-btn {
  border-radius: 8px !important;
  margin-right: 8px;
  background-color: #f0f0f0 !important;
  color: #666 !important;
  border-color: #f0f0f0 !important;
  transition: all 0.3s ease !important;
}
.cancel-btn:hover {
  background-color: #e0e0e0 !important;
  transform: translateY(-2px) !important;
}
.confirm-btn {
  border-radius: 8px !important;
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  transition: all 0.3s ease !important;
}
.confirm-btn:hover {
  background-color: #85c787 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(163, 217, 165, 0.4) !important;
}

/* 空状态提示样式 */
.empty-tip {
  color: #ff8fab;
  font-size: 14px;
  padding: 8px 0;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .el-input-show {
    width: 360px;
  }
}
@media (max-width: 992px) {
  .el-input-show {
    width: 300px;
  }
  .edit-dialog {
    width: 80% !important;
  }
  .dialog-scroll-container,
  .edit-dialog .el-dialog__body {
    max-height: 60vh;
  }
}
@media (max-width: 768px) {
  .cinema-management-container {
    padding: 10px;
  }
  .cinema-info-form {
    padding: 20px 10px;
  }
  .el-input-show {
    width: 100%;
  }
  .info-form-item {
    flex-direction: column;
    align-items: flex-start;
  }
  .time-form-item {
    flex-direction: row;
    flex-wrap: wrap;
  }
  .hall-form-item {
    flex-direction: column;
    align-items: flex-start;
  }
  .pic-container {
    justify-content: center;
  }
  .edit-dialog {
    width: 95% !important;
  }
  .hall-edit-item {
    flex-direction: column;
    align-items: flex-start;
  }
  .el-input-hall {
    width: 100%;
    margin-bottom: 8px;
  }

  .edit-dialog .el-dialog__body {
    max-height: 50vh;
  }
}
</style>
