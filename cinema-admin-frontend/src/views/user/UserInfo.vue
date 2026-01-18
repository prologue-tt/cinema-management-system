<template>
  <div class="user-management-container">
    <!-- 顶部装饰条 -->
    <div class="top-decoration"></div>

    <!-- 页面标题区域 -->
    <div class="header-section">
      <h2 class="page-title">
        <i class="el-icon-user"></i> 用户信息管理
      </h2>
    </div>

    <!-- 主内容卡片 -->
    <el-card class="main-card">
      <!-- 搜索和操作区 -->
      <div class="search-and-actions">
        <!-- 搜索区域 -->
        <div class="search-grid">
          <el-input
            placeholder="请输入用户名"
            v-model="inputUserName"
            clearable
            class="search-input"
          />
        </div>

        <!-- 操作按钮组 -->
        <div class="action-buttons">
          <el-button @click="getUserList" class="btn-search">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button type="primary" @click="showAddDialog" class="btn-add">
            <i class="el-icon-plus"></i> 添加用户
          </el-button>
          <el-button type="danger" @click="multipleDelete" class="btn-delete">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </div>
      </div>
      <!-- 两列用户卡片展示区域 -->
      <div class="user-cards-container">
        <!-- 单个用户卡片 -->
        <div
          class="user-card"
          v-for="user in userList"
          :key="user.userId"
          @click="handleCardSelect(user)"
          :class="{ 'selected': isSelected(user) }"
        >
          <!-- 选择框 -->
          <div class="card-checkbox" @click.stop="toggleSelection(user)">
            <i class="el-icon-check" v-if="isSelected(user)"></i>
          </div>

          <!-- 用户头像 -->
          <div class="user-avatar">
            <img
              :src="user.userPicture ? (global.base + JSON.parse(user.userPicture)[0]) : defaultAvatar"
              alt="用户头像"
              class="avatar-img"
            >
          </div>

          <!-- 用户信息 -->
          <div class="user-info">
            <h3 class="user-name">{{ user.userName }}</h3>
            <div class="info-row">
              <i class="el-icon-phone"></i>
              <span>{{ user.phoneNumber || '未设置' }}</span>
            </div>
            <div class="info-row">
              <i class="el-icon-flag"></i>
              <span>{{ sexFormat(user) }}</span>
            </div>
            <div class="info-row">
              <i class="el-icon-user-circle"></i>
              <span>{{ user.role?.roleName || '普通用户' }}</span>
            </div>


          <!-- 操作按钮 -->
          <div class="card-actions">
            <el-tooltip effect="light" content="修改用户信息" placement="top">
              <el-button
                type="primary"
                icon="el-icon-edit"
                size="mini"
                @click.stop="showEditDialog(user.userId)"
                class="btn-action edit-btn"
              ></el-button>
            </el-tooltip>
            <el-tooltip effect="light" content="删除用户" placement="top">
              <el-button
                type="danger"
                icon="el-icon-delete"
                size="mini"
                @click.stop="deleteUserById(user.userId)"
                class="btn-action delete-btn"
              ></el-button>
            </el-tooltip>
          </div>
        </div>
      </div>

      </div>


      <!-- 分页区域 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryInfo.pageNum"
          :page-sizes="[6]"
          :page-size="queryInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="pagination"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 添加用户对话框 -->
    <el-dialog
      title="添加用户"
      :visible.sync="addDialogVisible"
      width="60%"
      @close="addDialogClosed"
      custom-class="custom-dialog"
    >
      <!-- 内容主题区 -->
      <el-form
        :model="addForm"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="150px"
        class="form-container"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="addForm.userName" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" show-password class="form-input"></el-input>
        </el-form-item>

        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="addForm.phoneNumber" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="addForm.sex" placeholder="请选择性别" clearable class="form-select">
            <el-option
              v-for="item in gender"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="roleId">
          <el-select v-model="addForm.roleId" placeholder="请选择权限" clearable class="form-select">
            <el-option
              v-for="item in roleList"
              :key="item.roleId"
              :label="item.roleName"
              :value="item.roleId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
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
            <div class="upload-icon-container">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </div>
            <div slot="file" slot-scope="{file}">
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="用户头像">
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                  <i class="el-icon-zoom-in"></i>
                </span>
                <span class="el-upload-list__item-delete" @click="handleRemove(file)">
                  <i class="el-icon-delete"></i>
                </span>
              </span>
            </div>
          </el-upload>
          <!-- 放大预览 -->
          <el-dialog :visible.sync="dialogVisible" append-to-body class="preview-dialog">
            <img width="100%" :src="dialogImageUrl" alt="头像预览">
          </el-dialog>
        </el-form-item>
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false" class="dialog-btn cancel-btn">取 消</el-button>
        <el-button type="primary" @click="addUser" class="dialog-btn confirm-btn">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 修改用户对话框 -->
    <el-dialog
      title="修改用户信息"
      :visible.sync="editDialogVisible"
      width="60%"
      @close="editDialogClosed"
      custom-class="custom-dialog"
    >
      <el-form
        :model="editForm"
        :rules="editFormRules"
        ref="editFormRef"
        label-width="150px"
        class="form-container"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="editForm.userName" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="editForm.password" type="password" show-password class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="editForm.phoneNumber" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-select v-model="editForm.sex" placeholder="请选择性别" clearable class="form-select">
            <el-option
              v-for="item in gender"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权限" prop="roleId">
          <el-select v-model="editForm.roleId" placeholder="请选择权限" clearable class="form-select">
            <el-option
              v-for="item in roleList"
              :key="item.roleId"
              :label="item.roleName"
              :value="item.roleId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
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
            ref="pictureEditRef"
            :http-request="submitFile"
            class="avatar-uploader"
          >
            <div class="upload-icon-container">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </div>
            <div slot="file" slot-scope="{file}">
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="用户头像">
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                  <i class="el-icon-zoom-in"></i>
                </span>
                <span class="el-upload-list__item-delete" @click="handleRemove(file)">
                  <i class="el-icon-delete"></i>
                </span>
              </span>
            </div>
          </el-upload>
          <!-- 放大预览 -->
          <el-dialog :visible.sync="dialogVisible" append-to-body class="preview-dialog">
            <img width="100%" :src="dialogImageUrl" alt="头像预览">
          </el-dialog>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelEdit" class="dialog-btn cancel-btn">取 消</el-button>
        <el-button type="primary" @click="editUserInfo" class="dialog-btn confirm-btn">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import global from "../../assets/css/global.css"
export default {
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
      queryInfo: {
        userName: '',
        phoneNumber: '',
        email: '',
        sex: '',
        pageNum: 1,
        pageSize: 6
      },
      total: 0,
      // 控制对话框的显示与隐藏
      addDialogVisible: false,
      // 添加用户的表单数据
      addForm: {
        userId: '',
        userName: '',
        password: '',
        email: '',
        phoneNumber: '',
        sex: '',
        userPicture: ''
      },
      // 验证表单规则对象
      addFormRules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
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
        ]
      },
      editDialogVisible: false,
      editForm: {},
      editFormRules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
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
        ]
      },
      inputUserName: '',
      inputEmail: '',
      inputPhoneNumber: '',
      selectedSex: '',
      userList: [],
      gender: [
        {
          id: false,
          name: '女'
        },
        {
          id: true,
          name: '男'
        }
      ],
      multipleSelection: [],
      dialogImageUrl: '',
      dialogVisible: false,
      // 添加删除图片 动态绑定图片列表
      pics: [],
      // 发送给后端的JSON图片数组
      pictureList: [],
      picNums: 0,
      deletePicList: [],
      roleList: [],
      defaultAvatar:'https://cn.bing.com/images/search?q=%e9%bb%98%e8%ae%a4%e5%a4%b4%e5%83%8f&id=88096C8C1D7696112C3B5C56EE8CE04A33FE26E2&form=IACFIR&first=1&disoverlay=1'
    }
  },
  created() {
    this.getUserList()
    this.getRoleList()
  },
  methods: {// 判断用户是否被选中
    isSelected(user) {
      return this.multipleSelection.some(item => item.userId === user.userId)
    },

    // 切换用户选择状态
    toggleSelection(user) {
      const index = this.multipleSelection.findIndex(item => item.userId === user.userId)
      if (index > -1) {
        // 已选中，取消选择
        this.multipleSelection.splice(index, 1)
      } else {
        // 未选中，添加选择
        this.multipleSelection.push(user)
      }
    },

    // 点击卡片选中用户
    handleCardSelect(user) {
      // 这里可以添加查看用户详情的逻辑
      console.log('查看用户详情:', user)
    },
    getUserList() {
      this.queryInfo.userName = this.inputUserName
      this.queryInfo.email = this.inputEmail
      this.queryInfo.phoneNumber = this.inputPhoneNumber
      this.queryInfo.sex = this.selectedSex
      const _this = this;
      axios.get('sysUser', {params: _this.queryInfo}).then(resp => {
        console.log(resp)
        _this.userList = resp.data.data;
        _this.total = resp.data.total;
        _this.queryInfo.pageSize = resp.data.pageSize;
        _this.queryInfo.pageNum = resp.data.pageNum;
      })
    },
    getRoleList(){
      const _this = this
      axios.get('sysRole').then(resp => {
        _this.roleList = resp.data.data
      })
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getUserList()
      console.log(newSize)
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getUserList()
      console.log(newPage)
    },
    showAddDialog(){
      this.addDialogVisible = true
    },
    // 监听添加对话框的关闭事件
    addDialogClosed(){
      this.$refs.addFormRef.resetFields()
      this.$refs.pictureRef.clearFiles()
      this.pics = []
      this.pictureList = []
    },
    // 监听添加按钮
    async addUser(){
      await this.submitFile()
      console.log(this.pictureList)
      this.addForm.userPicture = JSON.stringify(this.pictureList)
      let success = true
      const _this = this
      this.$refs.addFormRef.validate(async valid => {
        console.log(valid)
        if (!valid) return
        // 预校验成功，发网络请求
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        await axios.post('sysUser', JSON.stringify(_this.addForm)).then(resp => {
          console.log(resp)
          if (resp.data.code !== 200){
            this.$message.error('添加用户失败！')
            success = false
          }
        })
        this.getUserList()
        if (!success) return
        //隐藏添加对话框
        this.addDialogVisible = false
        this.$message.success('添加用户成功！')
      })
    },
    // 显示修改对话框，回显数据
    async showEditDialog(id){
      const _this = this
      await axios.get('sysUser/' + id).then(resp => {
        _this.editForm = resp.data.data
      })
      for (const item of JSON.parse(this.editForm.userPicture)) {
        let pic = {}
        pic['name'] = ''
        pic['url'] = this.global.base + item
        this.pics.push(pic)
      }
      this.editDialogVisible = true
    },
    // 监听修改对话框的关闭事件
    editDialogClosed(){
      this.$refs.editFormRef.resetFields()
      this.$refs.pictureEditRef.clearFiles()
      this.pics = []
      this.pictureList = []
    },
    // 取消修改
    cancelEdit(){
      this.editDialogVisible = false
      this.deletePicList.splice(0, this.deletePicList.length)
    },
    async editUserInfo(){
      await this.submitFile()
      this.editForm.userPicture = JSON.stringify(this.pictureList)
      this.$refs.editFormRef.validate(async valid => {
        const _this = this
        if (!valid) return
        let success = true
        axios.defaults.headers.put['Content-Type'] = 'application/json'
        await axios.put('sysUser', JSON.stringify(_this.editForm)).then(resp => {
          if (resp.data.code !== 200){
            this.$message.error('修改用户信息失败！')
            success = false
          }
        })
        if (!success) return
        this.editDialogVisible = false
        this.getUserList()
        this.$message.success('修改用户信息成功！')
        for(let s of this.deletePicList){
          await axios.get('/upload/delete?filePath=' + s.substring(s.indexOf('/images')))
        }
      })
    },
    // 监听多选框变化
    handleSelectionChange(val){
      this.multipleSelection = val
    },
    async multipleDelete(){
      const _this = this
      //询问用户是否确认删除
      const resp = await this.$confirm('此操作将永久删除这些条目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      // 用户确认删除, resp为字符串"confirm"
      // 用户取消删除，resp为字符串"cancel"
      if (resp === 'cancel'){
        return _this.$message.info('已取消删除')
      }

      let ids = []
      this.multipleSelection.forEach(data => {
        ids.push(data.userId)
      })
      console.log(ids)
      await axios.delete('sysUser/' + ids).then(resp => {
        if (resp.data.code !== 200){
          this.$message.success('批量删除用户失败！')
        }
      })
      this.getUserList()
      this.$message.success('批量删除用户成功！')
    },
    //根据id删除对应的用户
    async deleteUserById(id){
      const _this = this
      //询问用户是否确认删除
      const resp = await this.$confirm('此操作将永久删除该条目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      // 用户确认删除, resp为字符串"confirm"
      // 用户取消删除，resp为字符串"cancel"
      console.log(resp)
      if (resp === 'cancel'){
        return _this.$message.info('已取消删除')
      }

      await axios.delete('sysUser/' + id).then(resp => {
        if (resp.data.code !== 200){
          _this.$message.success('删除用户失败！')
        }
      })
      this.getUserList()
      this.$message.success('删除用户成功！')
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
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleChange(file, filelist){
      this.pics = filelist.slice(0)
    },
    handleSuccess(response){
      this.pictureList.push(response.data)
      this.addForm = JSON.stringify(this.pictureList)
      this.editForm = JSON.stringify(this.pictureList)
    },
    handleError(err){
      console.log(err)
    },
    handleExceed(){
      this.$message.error("用户头像最多上传一张！")
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
          _this.pictureList.push(response.data.data)
        })
      }
    },
    sexFormat(row){
      if (row.sex === true){
        return '男'
      }else {
        return '女'
      }
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.user-management-container {
  padding: 20px;
  background-color: #fff5f7;
  min-height: 100vh;
  position: relative;
}

/* 顶部装饰条 - 粉蓝渐变 */
.top-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #ffbad2, #a7c7e7, #ffc2d1);
  margin-bottom: 25px;
}

/* 头部区域 */
.header-section {
  margin-bottom: 25px;
  text-align: center;
}

.header-section::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ffccd5, #a7c7e7, transparent);
}

.page-title {
  color: #ff6b8b;
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 20px;
  background-color: #ffffff;
  border-radius: 30px;
  box-shadow: 0 4px 12px rgba(255, 107, 139, 0.15);
}

.page-title i {
  font-size: 28px;
  color: #ff6b8b;
}
.page-description {
  color: #8a8a8a;
  font-size: 14px;
  margin-top: 8px;
}


.breadcrumb {
  color: #7b93b8;
  font-size: 14px;
}

/* 主卡片 */
.main-card {
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(167, 199, 231, 0.15);
  border: none;
  overflow: hidden;
  background-color: #fff;
}

/* 搜索和操作区 */
.search-and-actions {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 18px;
  margin-bottom: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}
.search-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  align-items: center;
}
.search-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.search-input, .search-select {
  border-radius: 25px;
  border: 1px solid #ffccd5;
  transition: all 0.3s ease;
  height: 40px;
  flex: 1;
  min-width: 200px;
}

.search-input:focus, .search-select:focus {
  border-color: #ff9bb3;
  box-shadow: 0 0 0 3px rgba(255, 155, 179, 0.2);
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}


.btn-delete:disabled {
  background-color: #f9f9f9;
  color: #bbbbbb;
  border-color: #dddddd;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-search {
  background-color: #ffe6ec;
  color: #ff6b8b;
  border: none;
  border-radius: 25px;
  padding: 8px 22px;
  font-size: 14px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-search:hover {
  background-color: #ffccd5;
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(255, 107, 139, 0.2);
  color: #ff6b8b;
}

.btn-add {
  background-color: #e6f7ee;
  color: #36b37e;
  border-color: #b7ebcc;
}

.btn-add:hover {
  background-color: #ccf5e1;
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(54, 179, 126, 0.2);
  color: #36b37e;
}

.btn-delete {
  background-color: #ffebe6;
  color: #ff5630;
  border-color: #ffc5b3;
}

.btn-delete:hover {
  background-color: #ffded4;
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(255, 86, 48, 0.2);
  color: #ff5630;
}

/* 表格区域 */
.table-wrapper {
  padding: 20px;
  overflow-x: auto;
}

.user-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e6f7ff;
}

.user-table th {
  background-color: #f0f7ff;
  color: #4e6e9e;
  font-weight: 500;
  border-bottom: 1px solid #d1e7dd;
}

.user-table tr:hover > td {
  background-color: #f9f9ff !important;
}

.user-table tr.el-table__row--striped td {
  background-color: #f0f7ff;
}

/* 操作按钮 */
.btn-action {
  border-radius: 50%;
  width: 36px;
  height: 36px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.btn-action:hover {
  transform: scale(1.1);
}

.edit-btn {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.delete-btn {
  background-color: #fff0f0;
  color: #ff4d4f;
  border-color: #ffb4a9;
}

/* 分页区域 */
.pagination-container {
  padding: 15px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
}

.pagination {
  margin: 0;
}

.el-pagination button, .el-pagination span:not([class*='el-icon']) {
  color: #666666;
}

.el-pagination .btn-prev:disabled, .el-pagination .btn-next:disabled {
  color: #cccccc;
}

.el-pagination .el-pager li {
  color: #666666;
  border-radius: 6px;
  margin: 0 4px;
}

.el-pagination .el-pager li.active {
  background-color: #ff9bb3;
  color: #fff;
}


/* 对话框样式 */
.custom-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.custom-dialog .el-dialog__header {
  background-color: #fff5f7;
  padding: 20px 25px;
  border-bottom: 1px solid #ffe6ec;
}

.custom-dialog .el-dialog__title {
  color: #ff6b8b;
  font-weight: 600;
  font-size: 18px;
}

.form-container {
  padding: 25px;
}

.form-input, .form-select {
  border-radius: 10px;
  border-color: #ffe6ec;
  height: 42px;
}

.form-input:focus, .form-select:focus {
  border-color: #ff9bb3;
  box-shadow: 0 0 0 3px rgba(255, 155, 179, 0.2);
}
.el-form-item__label {
  color: #666666;
}
/* 头像上传 */
.avatar-uploader {
  margin-top: 15px;
}

.upload-icon-container {
  width: 180px;
  height: 180px;
  border-radius: 16px;
  background-color: #fff5f7;
  border: 2px dashed #ffccd5;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.upload-icon-container:hover {
  background-color: #fff0f4;
  border-color: #ff9bb3;
}

.avatar-uploader-icon {
  font-size: 36px;
  color: #ff9bb3;
}

.preview-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.preview-dialog .el-dialog__body {
  padding: 0;
}

/* 对话框底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 18px 25px;
  border-top: 1px solid #ffe6ec;
  background-color: #fff5f7;
}

.dialog-btn {
  border-radius: 25px;
  padding: 10px 28px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #f8f8f8;
  color: #666666;
  border-color: #dddddd;
}

.cancel-btn:hover {
  background-color: #f0f0f0;
  color: #333333;
}

.confirm-btn {
  background-color: #ff9bb3;
  color: #ffffff;
  border-color: #ff9bb3;
}

.confirm-btn:hover {
  background-color: #ff6b8b;
  border-color: #ff6b8b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 139, 0.3);
}

/* 两列用户卡片容器 */
.user-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

/* 用户卡片样式 */
.user-card {
  background-color: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #f0f0f0;
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.08);
  border-color: #ffe6ec;
}

.user-card.selected {
  border-color: #ff9bb3;
  background-color: #fff8fa;
}

/* 卡片选择框 */
.card-checkbox {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid #ffccd5;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  z-index: 1;
  cursor: pointer;
}

.card-checkbox .el-icon-check {
  color: #ff6b8b;
  font-size: 16px;
  display: none;
}

.user-card.selected .card-checkbox .el-icon-check {
  display: block;
}

.user-card.selected .card-checkbox {
  background-color: #fff0f4;
}
.user-avatar {
  text-align: center;
  margin-bottom: 18px;
}

.avatar-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff0f4;
  box-shadow: 0 4px 10px rgba(255, 107, 139, 0.1);
}


/* 用户信息 */
.user-info {
  text-align: center;
  margin-bottom: 15px;
}

.user-name {
  color: #333333;
  font-size: 18px;
  margin-bottom: 12px;
  font-weight: 600;
  position: relative;
  display: inline-block;
}

.user-name::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background-color: #ffccd5;
  border-radius: 2px;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #666666;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-row i {
  color: #ff9bb3;
  font-size: 16px;
}

/* 卡片操作按钮 */
.card-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .search-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    justify-content: center;
  }
  .breadcrumb {
    justify-content: center;
    display: flex;
  }
  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input, .search-select {
    width: 100%;
  }

  .action-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }

  .user-cards-container {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 22px;
    padding: 6px 16px;
  }
}
</style>
