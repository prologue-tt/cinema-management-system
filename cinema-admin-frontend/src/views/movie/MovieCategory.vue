<template>
  <div class="movie-category-container">
    <!-- 顶部标题区 -->
    <div class="header">
      <h1 class="page-title">🎬 电影类别管理</h1>
    </div>

    <!-- 核心功能卡片 -->
    <el-card class="box-card shadow-sm">
      <!-- 1. 操作栏：添加按钮 -->
      <div class="operation-bar mb-6">
        <el-button
          type="primary"
          @click="addDialogVisible = true"
          class="add-button"
        >
          <i class="el-icon-plus mr-2"></i> 添加类别
        </el-button>
      </div>

      <!-- 2. 搜索区 -->
<!--      <div class="search-container mb-8">-->
<!--        <el-row :gutter="20">-->
<!--          <el-col :span="18" :xs="24">-->
<!--            <el-input-->
<!--              v-model="queryInfo.query"-->
<!--              placeholder="搜索电影类别（如：喜剧、动作）..."-->
<!--              clearable-->
<!--              prefix-icon="el-icon-search"-->
<!--              class="search-input"-->
<!--              @keyup.enter.native="getMovieCategoryList"-->
<!--            ></el-input>-->
<!--          </el-col>-->
<!--          <el-col :span="6" :xs="24" class="search-btn-col">-->
<!--            <el-button-->
<!--              type="success"-->
<!--              @click="getMovieCategoryList"-->
<!--              class="search-button"-->
<!--            >-->
<!--              <i class="el-icon-search mr-1"></i> 搜索-->
<!--            </el-button>-->
<!--          </el-col>-->
<!--        </el-row>-->
<!--      </div>-->

      <!-- 3. 卡片列表区（核心改造部分） -->
      <div class="category-card-list">
        <!-- 加载中/空状态 -->
        <div v-if="loading" class="empty-state">
          <i class="el-icon-loading loading-icon"></i>
          <p class="empty-text">正在加载类别数据...</p>
        </div>
        <div v-else-if="movieCategoryList.length === 0" class="empty-state">
          <i class="el-icon-document-empty empty-icon"></i>
          <p class="empty-text">暂无电影类别数据，点击"添加类别"创建吧～</p>
        </div>
        <!-- 卡片网格：一行6个卡片 -->
        <div class="card-grid" v-else>
          <div
            class="category-card"
            v-for="(category, index) in movieCategoryList"
            :key="category.movieCategoryId + index"
          >
            <!-- 卡片内容 -->
            <div class="card-content">
              <!-- 类别名称 -->
              <div class="category-name">{{ category.movieCategoryName }}</div>
              <!-- 类别编号（可选显示） -->
              <div class="category-id">ID: {{ category.movieCategoryId }}</div>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-tooltip effect="light" content="编辑类别" placement="top">
                <button
                  class="edit-btn"
                  @click.stop="showEditDialog(category.movieCategoryId)"
                >
                  <i class="el-icon-edit"></i>
                </button>
              </el-tooltip>
              <el-tooltip effect="light" content="删除类别" placement="top">
                <button
                  class="delete-btn"
                  @click.stop="deleteMovieCategoryById(category.movieCategoryId)"
                >
                  <i class="el-icon-close"></i>
                </button>
              </el-tooltip>
            </div>
          </div>
        </div>
      </div>

      <!-- 4. 分页区 -->
      <div class="pagination-container mt-8" v-if="total > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryInfo.pageNum"
          :page-sizes="[6, 12, 18, 24]"
        :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 添加类别对话框 -->
    <el-dialog
      title="添加电影类别"
      :visible.sync="addDialogVisible"
      width="50%"
      @close="addDialogClosed"
      custom-class="custom-dialog"
      :before-close="handleDialogBeforeClose"
    >
      <el-form
        :model="addForm"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="120px"
        class="form-container"
      >
        <el-form-item label="电影类别" prop="movieCategoryName">
          <el-input
            v-model="addForm.movieCategoryName"
            placeholder="请输入电影类别名称（如：喜剧、动作）"
            class="form-input"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false" class="cancel-button">取 消</el-button>
        <el-button type="primary" @click="addMovieCategory" class="confirm-button">
          <i class="el-icon-check mr-1"></i>确 定
        </el-button>
      </span>
    </el-dialog>

    <!-- 修改类别对话框 -->
    <el-dialog
      title="修改电影类别"
      :visible.sync="editDialogVisible"
      width="50%"
      @close="editDialogClosed"
      custom-class="custom-dialog"
      :before-close="handleDialogBeforeClose"
    >
      <el-form
        :model="editForm"
        :rules="editFormRules"
        ref="editFormRef"
        label-width="120px"
        class="form-container"
      >
        <el-form-item label="类别编号">
          <el-input
            v-model="editForm.movieCategoryId"
            disabled
            class="form-input disabled-input"
          ></el-input>
        </el-form-item>
        <el-form-item label="电影类别" prop="movieCategoryName">
          <el-input
            v-model="editForm.movieCategoryName"
            placeholder="请输入电影类别名称"
            class="form-input"
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" class="cancel-button">取 消</el-button>
        <el-button type="primary" @click="editMovieCategoryInfo" class="confirm-button">
          <i class="el-icon-check mr-1"></i>确 定
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "MovieCategory",
  data() {
    return {
      queryInfo: {
        query: '',
        pageNum: 1,
        pageSize: 12 // 默认为6个卡片/页，配合一行6个的布局
      },
      movieCategoryList: [], // 类别列表（卡片数据源）
      total: 0, // 总数据量
      loading: false, // 加载状态
      // 对话框控制
      addDialogVisible: false,
      editDialogVisible: false,
      // 表单数据与规则
      addForm: { movieCategoryName: '' },
      addFormRules: {
        movieCategoryName: [
          { required: true, message: '请输入电影类别', trigger: 'blur' },
          { min: 2, max: 10, message: '类别名称长度应在2-10个字符之间', trigger: 'blur' }
        ]
      },
      editForm: {},
      editFormRules: {
        movieCategoryName: [
          { required: true, message: '请输入电影类别', trigger: 'blur' },
          { min: 2, max: 10, message: '类别名称长度应在2-10个字符之间', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getMovieCategoryList()
  },
  methods: {
    // 1. 获取类别列表
    getMovieCategoryList() {
      const _this = this;
      this.loading = true // 开启加载
      axios.get('sysMovieCategory/find', { params: _this.queryInfo }).then(resp => {
        _this.movieCategoryList = resp.data.data || [];
        _this.total = resp.data.total || 0;
        _this.queryInfo.pageSize = resp.data.pageSize || 6;
        _this.queryInfo.pageNum = resp.data.pageNum || 1;
      }).catch(error => {
        console.error('加载电影类别错误：', error)
        this.$message.error('加载失败，请稍后重试')
      }).finally(() => {
        this.loading = false // 关闭加载
      })
    },

    // 2. 分页相关
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getMovieCategoryList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getMovieCategoryList()
    },

    // 3. 添加类别
    addDialogClosed() {
      this.$refs.addFormRef?.resetFields()
    },
    addMovieCategory() {
      this.$refs.addFormRef.validate(async valid => {
        if (!valid) return
        try {
          axios.defaults.headers.post['Content-Type'] = 'application/json'
          const resp = await axios.post('sysMovieCategory', JSON.stringify(this.addForm))
          if (resp.data.code !== 200) {
            return this.$message.error('添加电影类别失败！')
          }
          this.addDialogVisible = false
          this.getMovieCategoryList()
          this.$message.success('添加电影类别成功！')
        } catch (error) {
          this.$message.error('添加过程中出现错误，请稍后重试')
          console.error('添加电影类别错误：', error)
        }
      })
    },

    // 4. 编辑类别
    showEditDialog(id) {
      axios.get('sysMovieCategory/' + id).then(resp => {
        this.editForm = resp.data.data || {}
        this.editDialogVisible = true
      }).catch(error => {
        this.$message.error('加载类别详情失败')
        console.error('加载类别详情错误：', error)
      })
    },
    editDialogClosed() {
      this.$refs.editFormRef?.resetFields()
    },
    editMovieCategoryInfo() {
      this.$refs.editFormRef.validate(async valid => {
        if (!valid) return
        try {
          axios.defaults.headers.put['Content-Type'] = 'application/json'
          const resp = await axios.put('sysMovieCategory', JSON.stringify(this.editForm))
          if (resp.data.code !== 200) {
            return this.$message.error('修改电影类别失败！')
          }
          this.editDialogVisible = false
          this.getMovieCategoryList()
          this.$message.success('修改电影类别成功！')
        } catch (error) {
          this.$message.error('修改过程中出现错误，请稍后重试')
          console.error('修改电影类别错误：', error)
        }
      })
    },

    // 5. 删除相关
    handleDialogBeforeClose(done) {
      this.$confirm('确定要关闭吗？已输入的内容可能会丢失。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => done()).catch(() => done(false))
    },
    deleteMovieCategoryById(id) {
      this.$confirm('此操作将永久删除该电影类别, 是否继续?', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const resp = await axios.delete('sysMovieCategory/' + id)
          if (resp.data.code !== 200) {
            return this.$message.error('删除电影类别失败！')
          }
          this.getMovieCategoryList()
          this.$message.success('删除电影类别成功！')
        } catch (error) {
          this.$message.error('删除过程中出现错误，请稍后重试')
          console.error('删除类别错误：', error)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    }
  }
}
</script>

<style scoped>
/* 1. 全局容器样式 */
.movie-category-container {
  padding: 24px;
  background-color: #fff5f7;
  min-height: calc(100vh - 64px);
  background-image:
    radial-gradient(#ffccd5 0.5px, transparent 0.5px),
    radial-gradient(#ffccd5 0.5px, #fff5f7 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
  font-size: 16px;
}

/* 2. 顶部标题样式 */
.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #f6e5f5 0%, #d8e1f8 100%);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.page-title {
  color: #5a68a5;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
}

/* 3. 卡片容器样式 */
.box-card {
  border-radius: 16px;
  box-shadow: 0 5px 15px rgba(255, 183, 197, 0.1);
  border: none;
  overflow: hidden;
  background-color: white;
  transition: all 0.3s ease;
  padding: 24px;
}
.box-card:hover {
  box-shadow: 0 8px 25px rgba(255, 183, 197, 0.15);
}

/* 4. 操作按钮区域 */
.operation-bar {
  margin-bottom: 20px;
  padding-top: 10px;
}
.add-button {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  padding: 12px 24px;
  border-radius: 50px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(163, 217, 165, 0.3);
}
.add-button:hover {
  background-color: #85c787 !important;
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(163, 217, 165, 0.4);
}

/* 5. 搜索区域 */
.search-container {
  margin-bottom: 24px;
}
.search-input {
  border-radius: 50px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s;
  height: 46px;
  font-size: 16px;
  padding-left: 20px;
}
.search-input:focus {
  border-color: #ff9aa2 !important;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2) !important;
}
.search-btn-col {
  display: flex;
  align-items: center;
}
.search-button {
  background-color: #bde0fe !important;
  border-color: #bde0fe !important;
  color: #4a90e2 !important;
  padding: 12px 24px;
  border-radius: 50px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(189, 224, 254, 0.3);
  width: 100%;
}
.search-button:hover {
  background-color: #a8d1fc !important;
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(189, 224, 254, 0.4);
}

/* 6. 卡片网格布局 - 一行6个卡片 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr); /* 6列布局 */
  gap: 16px;
}

/* 7. 单个卡片样式 */
.category-card {
  background-color: white;
  border-radius: 12px;
  border: 1px solid #ffe6eb;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  min-height: 120px;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 183, 197, 0.1);
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(255, 183, 197, 0.2);
  border-color: #ffccd5;
}

/* 卡片内容 */
.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: #fdb0c1;
  margin-bottom: 8px;
  padding: 8px 16px;
  background-color: #d7eafa;
  border-radius: 20px;
}

.category-id {
  font-size: 14px;
  color: #8c8c8c;
}

/* 卡片操作按钮 */
.card-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 4px;
}

.edit-btn, .delete-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.edit-btn {
  background-color: #fff3cd;
  color: #856404;
}

.edit-btn:hover {
  background-color: #ffeeba;
  transform: scale(1.1);
}

.delete-btn {
  background-color: #ffb3ba;
  color: #d1495b;
}

.delete-btn:hover {
  background-color: #ff8fa3;
  transform: scale(1.1);
}

/* 8. 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #ff8fab;
}

.loading-icon, .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  animation: spin 1.5s linear infinite;
}

.empty-text {
  font-size: 16px;
  margin: 0;
}

/* 9. 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  color: #ff8fab;
  font-size: 16px;
}
.el-pagination button,
.el-pagination span:not([class*='el-icon']),
.el-pagination .el-pager li {
  color: #ff8fab;
  font-size: 16px;
  padding: 6px 12px;
}
.el-pagination .el-pager li {
  border-radius: 50% !important;
  width: 36px;
  height: 36px;
  line-height: 36px;
  margin: 0 4px;
  transition: all 0.3s ease;
}
.el-pagination .el-pager li:hover {
  background-color: #fff0f3;
  transform: translateY(-2px);
}
.el-pagination .el-pager li.active {
  color: white !important;
  background-color: #ff8fab !important;
  border-color: #ff8fab !important;
}

/* 10. 对话框样式 */
.custom-dialog {
  border-radius: 16px !important;
  overflow: hidden !important;
}
.custom-dialog .el-dialog__header {
  background-color: #fff0f3 !important;
  padding: 20px 24px;
  border-bottom: 1px solid #ffe6eb !important;
}
.custom-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #ff6b8b !important;
}
.custom-dialog .el-dialog__body {
  padding: 28px;
  font-size: 16px;
}
.form-input {
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s;
  height: 46px;
  font-size: 16px;
}
.form-input:focus {
  border-color: #ff9aa2 !important;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2) !important;
}
.el-form-item__label {
  font-size: 16px;
  color: #ff8fab !important;
  font-weight: 500;
}

/* 11. 对话框按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.cancel-button {
  background-color: #f0f0f0 !important;
  color: #666 !important;
  border-color: #f0f0f0 !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  padding: 10px 20px !important;
  font-size: 16px !important;
}
.confirm-button {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  padding: 10px 20px !important;
  font-size: 16px !important;
}

/* 12. 响应式适配 */
@media (max-width: 1200px) {
  .card-grid {
    grid-template-columns: repeat(4, 1fr); /* 中等屏幕一行4个 */
  }
}

@media (max-width: 992px) {
  .card-grid {
    grid-template-columns: repeat(3, 1fr); /* 平板一行3个 */
  }
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr); /* 小平板一行2个 */
  }

  .movie-category-container {
    padding: 15px;
    font-size: 15px;
  }

  .add-button, .search-button {
    width: 100%;
    margin-bottom: 10px;
    padding: 10px 20px;
    font-size: 15px;
  }
}

@media (max-width: 576px) {
  .card-grid {
    grid-template-columns: 1fr; /* 手机一行1个 */
  }
}

/* 动画效果 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
