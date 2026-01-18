<template>
  <div class="hall-management-container">
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 内容区域 - 移除了侧边栏 -->
      <div class="content full-width">
        <!-- 标题区域 -->
        <div class="page-title">
          <h1>影厅信息管理</h1>
          <p>管理影院所有影厅的基本信息及座位安排</p>
        </div>

        <!-- 卡片视图 -->
        <div class="card-container">
          <!-- 搜索和操作区域 -->
          <div class="search-and-actions">
            <div class="search-group">
              <el-select
                v-model="selectedHallCategory"
                placeholder="请选择影厅类别"
                clearable
                class="category-select">
                <el-option
                  v-for="item in hallCategoryList"
                  :key="item"
                  :label="item"
                  :value="item">
                </el-option>
              </el-select>

              <el-button
                class="search-btn"
                icon="el-icon-search"
                @click="getHallList">
                搜索
              </el-button>
            </div>

            <div class="action-buttons">
              <el-button
                type="primary"
                @click="addDialogVisible = true"
                class="add-btn">
                <i class="iconfont icon-add"></i> 添加影厅
              </el-button>

            </div>
          </div>

          <!-- 影厅卡片列表 -->
          <div class="hall-cards-grid">
            <!-- 影厅卡片 -->
            <div
              v-for="hall in hallList"
              :key="hall.hallId"
              class="hall-card"
              :class="{ 'hall-card-selected': isSelected(hall.hallId) }"
            >
              <!-- 卡片内容 -->
              <div class="hall-card-content">
                <div class="hall-header">
                  <h3 class="hall-name">{{ hall.hallName }}</h3>
                  <span class="hall-id">#{{ hall.hallId }}</span>
                </div>

                <div class="hall-category">
                  <span class="category-label">{{ hall.hallCategory }}</span>
                </div>

                <div class="hall-stats">
                  <div class="stat-item">
                    <span class="stat-label">排数</span>
                    <span class="stat-value">{{ hall.rowNums }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">列数</span>
                    <span class="stat-value">{{ hall.seatNumsRow }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">总座位</span>
                    <span class="stat-value">{{ hall.seatNums }}</span>
                  </div>
                </div>

                <!-- 座位预览 -->
                <div class="seat-preview">
                  <div class="preview-label">座位预览:</div>
                  <div class="seat-miniature">
                    <!-- 外层循环 - 行 -->
                    <div v-for="(row, rowIdx) in getMiniatureSeats(hall)" :key="rowIdx" class="mini-seat-row">
                      <!-- 内层循环 - 每行的座位 -->
                      <div
                        class="mini-seat"
                        v-for="(seat, seatIdx) in row"
                        :key="seatIdx"
                        :class="seat === 0 ? 'seat-available' : 'seat-unavailable'"
                      ></div>
                    </div>
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="hall-actions">
                  <el-button
                    type="primary"
                    size="mini"
                    @click="isAbleEdit(hall.hallId)"
                    class="operation-btn edit-btn">
                    <i class="iconfont icon-edit"></i> 编辑
                  </el-button>

                  <el-button
                    type="danger"
                    size="mini"
                    @click="isAbleDelete(hall.hallId)"
                    class="operation-btn delete-btn">
                    <i class="iconfont icon-trash"></i> 删除
                  </el-button>

                  <el-button
                    type="warning"
                    size="mini"
                    @click="arrangeSeat(hall.hallId)"
                    class="operation-btn seat-btn">
                    <i class="iconfont icon-seat"></i> 座位
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="hallList.length === 0" class="empty-state">
              <div class="empty-icon">🎬</div>
              <p class="empty-text">没有找到相关影厅</p>
              <el-button
                type="primary"
                @click="resetSearch()"
                class="empty-action">
                重置搜索
              </el-button>
            </div>
          </div>

          <!-- 分页区域 -->
          <div class="pagination-container">
            <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="queryInfo.pageNum"
              :page-sizes="[4, 8, 12]"
              :page-size="queryInfo.pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加影厅对话框 -->
    <el-dialog
      title="添加影厅"
      :visible.sync="addDialogVisible"
      width="500px"
      @close="addDialogClosed"
      custom-class="custom-dialog">
      <!-- 内容主题区 -->
      <el-form
        :model="addForm"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="120px"
        class="form-container">
        <el-form-item label="影厅名称" prop="hallName">
          <el-input v-model="addForm.hallName" placeholder="请输入影厅名称"></el-input>
        </el-form-item>
        <el-form-item label="影厅类别" prop="hallCategory">
          <el-select
            v-model="addForm.hallCategory"
            placeholder="请选择影厅类别"
            clearable>
            <el-option
              v-for="item in hallCategoryList"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排数" prop="rowNums">
          <el-input v-model="addForm.rowNums" placeholder="请输入影厅排数"></el-input>
        </el-form-item>
        <el-form-item label="列数" prop="seatNumsRow">
          <el-input v-model="addForm.seatNumsRow" placeholder="请输入每行座位数"></el-input>
        </el-form-item>
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="addHall" class="confirm-btn">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 修改影厅对话框 -->
    <el-dialog
      title="修改影厅"
      :visible.sync="editDialogVisible"
      width="500px"
      @close="editDialogClosed"
      custom-class="custom-dialog">
      <el-form
        :model="editForm"
        :rules="editFormRules"
        ref="editFormRef"
        label-width="120px"
        class="form-container">
        <el-form-item label="影厅编号" prop="hallId">
          <el-input v-model="editForm.hallId" disabled></el-input>
        </el-form-item>
        <el-form-item label="影厅名称" prop="hallName">
          <el-input v-model="editForm.hallName" disabled placeholder="影厅名称不可更改"></el-input>
        </el-form-item>
        <el-form-item label="影厅类别" prop="hallCategory">
          <el-select
            v-model="editForm.hallCategory"
            placeholder="请选择影厅类别"
            clearable>
            <el-option
              v-for="item in hallCategoryList"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排数" prop="rowNums">
          <el-input v-model="editForm.rowNums" placeholder="请输入影厅排数"></el-input>
        </el-form-item>
        <el-form-item label="列数" prop="seatNumsRow">
          <el-input v-model="editForm.seatNumsRow" placeholder="请输入每行座位数"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="editHallInfo" class="confirm-btn">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 安排座位对话框 -->
    <el-dialog
      title="安排座位"
      :visible.sync="arrangeDialogVisible"
      :width="arrangeDialogWidth"
      custom-class="seat-dialog">
      <div class="seat-legend">
        <div class="legend-item">
          <span class="seat-icon available"></span>
          <span>可用座位</span>
        </div>
        <div class="legend-item">
          <span class="seat-icon unavailable"></span>
          <span>禁用座位</span>
        </div>
      </div>

      <div class="screen-indicator">屏幕</div>

      <div class="seat-grid">
        <div class="seat-row" v-for="(value, key) in seats" :key="key">
          <div class="row-label">{{ key }}</div>
          <div class="seats-in-row">
            <span
              class="seat"
              :class="value[index] === 0 ? 'seat-available' : 'seat-unavailable'"
              :key="index"
              @click="pressSeat(key, index)"
              v-for="(item, index) in value">
            </span>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="arrangeDialogVisible = false" class="cancel-btn">取 消</el-button>
        <el-button type="primary" @click="saveSeat" class="confirm-btn">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import global from "../../assets/css/global.css"
export default {
  name: "HallInfo",
  data() {
    return {
      queryInfo: {
        hallName: '',
        hallCategory: '',
        pageNum: 1,
        pageSize: 8
      },
      hallList: [],
      cinemaInfo: {},
      total: 0,
      url: 'http://localhost:9231/',
      // 控制对话框的显示与隐藏
      addDialogVisible: false,
      // 添加影厅的表单数据 - 起始行默认为1且不在页面展示
      addForm: {
        hallId: '',
        cinemaId: '1',
        hallName: '',
        hallCategory: '',
        rowStart: '1', // 起始行默认为1
        rowNums: 10,
        seatNumsRow: 10,
        seatNums: 100,
        seatState: '',
        delState: false
      },
      // 验证表单规则对象
      addFormRules: {
        hallName: [
          { required: true, message: '请输入影厅名称', trigger: 'blur' }
        ],
        hallCategory: [
          { required: true, message: '请选择影厅类别', trigger: 'blur' }
        ]
      },
      editDialogVisible: false,
      editForm: {},
      checkAbleId: {},
      editFormRules: {
        hallName: [
          { required: true, message: '请输入影厅名称', trigger: 'blur' }
        ],
        hallCategory: [
          { required: true, message: '请选择影厅类别', trigger: 'blur' }
        ]
      },
      inputHallName: '',
      selectedHallCategory: '',
      hallCategoryList: [],
      multipleSelection: [],
      arrangeDialogVisible: false,
      seats: {},
      editSeat: {},
      arrangeDialogWidth: '',
    }
  },
  created() {
    this.getHallList()
    this.getHallCategoryList()
  },
  methods: {
    // 判断影厅是否被选中
    isSelected(hallId) {
      return this.multipleSelection.some(item => item.hallId === hallId);
    },

    // 重置搜索条件
    resetSearch() {
      this.inputHallName = '';
      this.selectedHallCategory = '';
      this.queryInfo.pageNum = 1;
      this.getHallList();
    },

    // 获取座位缩略图数据（用于卡片预览）
    getMiniatureSeats(hall) {
      try {
        const seats = JSON.parse(hall.seatState);
        // 只返回前3行的前5个座位用于预览
        const rows = Object.values(seats).slice(0, 3);
        return rows.map(row => row.slice(0, 5));
      } catch (e) {
        // 处理解析错误，返回默认预览
        return Array(3).fill(0).map(() => Array(5).fill(0));
      }
    },

    getHallList() {
      this.queryInfo.hallName = this.inputHallName
      this.queryInfo.hallCategory = this.selectedHallCategory
      const _this = this;
      axios.get('sysHall', {params: _this.queryInfo}).then(resp => {
        _this.hallList = resp.data.data;
        _this.total = resp.data.total;
        _this.queryInfo.pageSize = resp.data.pageSize;
        _this.queryInfo.pageNum = resp.data.pageNum;
      })
    },
    async getHallCategoryList(){
      const _this = this
      await axios.get('sysCinema').then(resp => {
        _this.cinemaInfo = resp.data.data
      })
      _this.hallCategoryList = []
      for (const item of JSON.parse(this.cinemaInfo.hallCategoryList)) {
        this.hallCategoryList.push(item)
      }
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getHallList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getHallList()
    },
    // 监听添加对话框的关闭事件
    addDialogClosed(){
      this.$refs.addFormRef.resetFields()
      // 重置时确保起始行保持为1
      this.addForm.rowStart = '1'
    },
    // 监听添加按钮
    addHall(){
      let row = this.addForm.rowNums
      let col = this.addForm.seatNumsRow
      this.$set(this.addForm, 'seatNums', row * col)
      let seat = {}
      // 使用默认的起始行1来生成座位排号
      for (let i = 0; i < row; i++) {
        let arr = []
        for (let j = 0; j < col; j++) {
          arr.push(0)
        }
        // 基于起始行1生成排号
        seat[i + parseInt(this.addForm.rowStart)] = arr
      }
      this.$set(this.addForm, 'seatState', JSON.stringify(seat))

      const _this = this;
      this.$refs.addFormRef.validate(async valid => {
        if (!valid) return
        // 预校验成功，发网络请求
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        const {data : res } = await axios.post('sysHall', JSON.stringify(_this.addForm))
        if(res.code !== 200) return this.$message.error("添加影厅信息失败 " + res.msg)
        // 隐藏添加对话框
        this.addDialogVisible = false
        // 重新加载列表
        this.getHallList()
        this.$message.success('添加影厅信息成功！')
      })
    },
    isAbleEdit(id) {
      this.checkAbleId.hallId = id
      axios.get('sysSession/isAbleEdit', {params: this.checkAbleId}).then(response => {
        let sessions = response.data.data
        if (response.data.total === 0) {
          this.showEditDialog(id)
        } else {
          let sessionIds = ''
          for (let temp of sessions) {
            sessionIds += temp.sessionId+' '
          }
          // 使用自定义样式的提示框
          this.$alert(
            `<div class="custom-alert-content">
              <div class="alert-icon">😢</div>
              <p class="alert-message">操作失败啦~ 有未完成的电影场次</p>
              <p class="alert-details">相关场次: ${sessionIds}</p>
            </div>`,
            '哎呀',
            {
              confirmButtonText: '知道了',
              dangerouslyUseHTMLString: true,
              customClass: 'custom-alert',
              callback: action => {
                this.$router.push('/hall')
              }
            }
          )
        }
      })
    },
    // 显示修改对话框，回显数据
    showEditDialog(id){
      const _this = this
      axios.get('sysHall/' + id ).then(resp => {
        _this.editForm = resp.data.data
        // 确保编辑时起始行仍为1且不展示
        _this.editForm.rowStart = '1'
      })
      this.editDialogVisible = true
    },
    // 监听修改对话框的关闭事件
    editDialogClosed(){
      this.$refs.editFormRef.resetFields()
    },
    // 修改影厅分类信息并提交
    editHallInfo(){
      let row = this.editForm.rowNums
      let col = this.editForm.seatNumsRow
      this.$set(this.editForm, 'seatNums', row * col)
      let seat = {}
      // 编辑时仍使用默认的起始行1
      for (let i = 0; i < row; i++) {
        let arr = []
        for (let j = 0; j < col; j++) {
          arr.push(0)
        }
        seat[i + parseInt(this.editForm.rowStart)] = arr
      }
      this.$set(this.editForm, 'seatState', JSON.stringify(seat))

      this.$refs.editFormRef.validate(async valid => {
        const _this = this
        if (!valid) return
        let success = true
        axios.defaults.headers.put['Content-Type'] = 'application/json'
        await axios.put('sysHall', JSON.stringify(_this.editForm)).then(resp => {
          if (resp.data.code !== 200){
            this.$message.error('修改影厅信息失败！')
            success = false
          }
        })
        if (!success) return
        this.editDialogVisible = false
        this.getHallList()
        this.$message.success('修改影厅信息成功！')
      })
    },
    // 监听多选框变化
    handleSelectionChange(val){
      this.multipleSelection = val
    },
    async isAbleDelete(id) {
      this.checkAbleId.hallId = id
      await axios.get('sysSession/isAbleEdit', {params: this.checkAbleId}).then(response => {
        let sessions = response.data.data
        if (response.data.total === 0) {
          this.deleteHallById(id)
        } else {
          let sessionIds = ''
          for (let temp of sessions) {
            sessionIds += temp.sessionId+' '
          }
          // 使用自定义样式的提示框
          this.$alert(
            `<div class="custom-alert-content">
              <div class="alert-icon">😢</div>
              <p class="alert-message">操作失败啦~ 有未完成的电影场次</p>
              <p class="alert-details">相关场次: ${sessionIds}</p>
            </div>`,
            '哎呀',
            {
              confirmButtonText: '知道了',
              dangerouslyUseHTMLString: true,
              customClass: 'custom-alert',
              callback: action => {
                this.$router.push('/hall')
              }
            }
          )
        }
      })
    },
    // 根据id删除对应的影厅分类
    async deleteHallById(id){
      let deleteInfo = {
        hallId: id
      }
      const _this = this
      // 询问用户是否确认删除
      const resp = await this.$confirm('确认删除该影厅?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      if (resp === 'cancel'){
        return _this.$message.info('已取消删除')
      }

      axios.defaults.headers.post['Content-Type'] = 'application/json'
      await axios.post('sysHall/delete', JSON.stringify(deleteInfo)).then(resp => {
        if (resp.data.code !== 200){
          _this.$message.success('删除影厅失败！')
        }
      })
      this.getHallList()
      this.$message.success('删除影厅成功！')
    },
    async arrangeSeat(id){
      const _this = this
      await axios.get('sysHall/' + id).then(resp => {
        _this.editSeat = resp.data.data
        _this.seats = JSON.parse(resp.data.data.seatState)
      })
      this.arrangeDialogWidth = 200 + this.editSeat.seatNumsRow * 40 + 'px'
      this.arrangeDialogVisible = true
    },
    pressSeat(key, idx){
      this.$set(this.seats[key], idx, (this.seats[key][idx] === 0 ? 1 : 0))
    },
    async saveSeat(){
      this.editSeat.seatState = JSON.stringify(this.seats)
      let totalSeats = 0
      for(let key in this.seats){
        for(let j = 0; j < this.seats[key].length; j++){
          if(this.seats[key][j] === 0){
            totalSeats++
          }
        }
      }

      this.$set(this.editSeat, 'seatNums', totalSeats)
      const _this = this
      axios.defaults.headers.put['Content-Type'] = 'application/json'
      await axios.put('sysHall', JSON.stringify(_this.editSeat)).then(resp => {
        if (resp.data.code !== 200){
          this.$message.error('安排座位失败！')
        }
      })
      this.arrangeDialogVisible = false
      this.getHallList()
      this.$message.success('安排座位成功！')
    }
  }
}
</script>

<style scoped>
.mini-seat-row {
  display: flex;
}

/* 自定义提示框样式 - 清新可爱风格 */
.custom-alert {
  border-radius: 20px !important;
  overflow: hidden !important;
  box-shadow: 0 10px 30px rgba(255, 183, 197, 0.2) !important;
  border: none !important;
}

.custom-alert .el-message-box__header {
  background: linear-gradient(135deg, #fff0f3 0%, #ffe6eb 100%) !important;
  padding: 20px 25px !important;
  border-bottom: none !important;
}

.custom-alert .el-message-box__title {
  color: #ff6b8b !important;
  font-size: 1.3rem !important;
  font-weight: 600 !important;
  text-align: center !important;
  width: 100% !important;
}

.custom-alert .el-message-box__content {
  padding: 30px 25px 20px !important;
  font-size: 1rem !important;
}

.custom-alert-content {
  text-align: center;
  padding: 10px 0;
}

.alert-icon {
  font-size: 56px !important;
  margin-bottom: 20px !important;
  color: #ff99aa !important;
  text-shadow: 0 2px 4px rgba(255, 153, 170, 0.2) !important;
}

.alert-message {
  font-size: 1.1rem !important;
  color: #5a5a5a !important;
  margin-bottom: 15px !important;
  line-height: 1.6 !important;
  font-weight: 500 !important;
}

.alert-details {
  font-size: 0.9rem !important;
  color: #888 !important;
  line-height: 1.6 !important;
  margin-top: 10px !important;
  padding: 12px 15px !important;
  background-color: #fff5f7 !important;
  border-radius: 12px !important;
  display: inline-block !important;
  border: 1px dashed #ffccd5 !important;
}

.custom-alert .el-message-box__footer {
  padding: 15px 25px 25px !important;
  border-top: 1px solid #ffe6eb !important;
  display: flex !important;
  justify-content: center !important;
}

.custom-alert .el-button--primary {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  border-radius: 30px !important;
  padding: 8px 35px !important;
  font-size: 1rem !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 12px rgba(163, 217, 165, 0.3) !important;
}

.custom-alert .el-button--primary:hover {
  background-color: #85c787 !important;
  transform: translateY(-3px) !important;
  box-shadow: 0 6px 15px rgba(163, 217, 165, 0.4) !important;
}

/* 全局样式 - 采用可爱清新风格的柔和色彩 */
.hall-management-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #fff5f7; /* 浅粉色背景 */
  color: #5a5a5a;
  padding: 20px;
}

/* 面包屑样式 */
.breadcrumb {
  margin-bottom: 20px;
  font-size: 0.9rem;
  color: #ff8fab;
  padding: 10px 15px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(255, 183, 197, 0.2);
}

/* 页面标题样式 */
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

/* 卡片容器样式 - 圆润边角和柔和阴影 */
.card-container {
  background-color: white;
  border-radius: 16px !important;
  box-shadow: 0 5px 15px rgba(255, 183, 197, 0.15) !important;
  padding: 20px;
  overflow: hidden;
}

/* 搜索和操作区域样式 */
.search-and-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-select {
  width: 200px;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s ease;
}

.category-select:focus {
  box-shadow: 0 0 0 2px rgba(255, 153, 170, 0.2) !important;
}

/* 按钮样式 - 可爱清新风格的颜色和圆角 */
.search-btn {
  background-color: #ffb6c1 !important;
  border-color: #ffb6c1 !important;
  color: white !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgba(255, 182, 193, 0.3) !important;
}

.search-btn:hover {
  background-color: #ff9aa2 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(255, 182, 193, 0.4) !important;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.add-btn {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgba(163, 217, 165, 0.3) !important;
}

.add-btn:hover {
  background-color: #85c787 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(163, 217, 165, 0.4) !important;
}

.batch-delete-btn {
  background-color: #ffccd5 !important;
  border-color: #ffccd5 !important;
  color: #e63946 !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgba(255, 204, 213, 0.3) !important;
}

.batch-delete-btn:hover:enabled {
  background-color: #ffb3c1 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(255, 204, 213, 0.4) !important;
}

.batch-delete-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 影厅卡片网格布局 */
.hall-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

/* 影厅卡片样式 */
.hall-card {
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid #f5f5f5;
}

.hall-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(255, 153, 170, 0.15);
}

.hall-card-selected {
  border-color: #ff99aa;
  box-shadow: 0 5px 15px rgba(255, 153, 170, 0.2);
}

/* 选择框位置 */
.hall-selector {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 10;
}

/* 卡片内容 */
.hall-card-content {
  padding: 20px;
}

.hall-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.hall-name {
  font-size: 1.2rem;
  margin: 0;
  color: #333;
  font-weight: 600;
}

.hall-id {
  color: #999;
  font-size: 0.85rem;
  background-color: #f9f9f9;
  padding: 3px 8px;
  border-radius: 12px;
}

.hall-category {
  margin-bottom: 15px;
}

.category-label {
  display: inline-block;
  background-color: #f0f7ff;
  color: #4a90e2;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
}

/* 统计信息 */
.hall-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #999;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #555;
}

/* 座位预览 */
.seat-preview {
  margin-bottom: 20px;
}

.preview-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
  display: block;
}

.seat-miniature {
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
}

.mini-seat {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

/* 操作按钮区域 */
.hall-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.operation-btn {
  margin-right: 5px !important;
  padding: 5px 10px !important;
  border-radius: 6px !important;
  transition: all 0.2s ease !important;
}

.operation-btn:hover {
  transform: translateY(-2px) !important;
}

.edit-btn {
  background-color: #bde0fe !important;
  border-color: #bde0fe !important;
  color: #4a90e2 !important;
}

.edit-btn:hover {
  background-color: #a8d1fc !important;
  box-shadow: 0 2px 5px rgba(189, 224, 254, 0.5) !important;
}

.delete-btn {
  background-color: #ffccd5 !important;
  border-color: #ffccd5 !important;
  color: #e63946 !important;
}

.delete-btn:hover {
  background-color: #ffb3c1 !important;
  box-shadow: 0 2px 5px rgba(255, 204, 213, 0.5) !important;
}

.seat-btn {
  background-color: #ffdfba !important;
  border-color: #ffdfba !important;
  color: #e67e22 !important;
}

.seat-btn:hover {
  background-color: #ffd39b !important;
  box-shadow: 0 2px 5px rgba(255, 223, 186, 0.5) !important;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-pagination {
  color: #ff6b8b !important;
}

.el-pagination .el-pager li {
  border-radius: 4px !important;
  margin: 0 3px !important;
}

.el-pagination .el-pager li.active {
  background-color: #ff99aa !important;
  color: white !important;
}

/* 对话框样式 */
.custom-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
}

.custom-dialog .el-dialog__header {
  background-color: #fff0f3 !important;
  padding: 15px 20px !important;
  border-bottom: 1px solid #ffe6eb !important;
}

.custom-dialog .el-dialog__title {
  color: #ff6b8b !important;
  font-weight: 600 !important;
}

.form-container {
  margin-top: 15px;
}

.form-container .el-form-item {
  margin-bottom: 20px !important;
}

.form-container .el-input,
.form-container .el-select {
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.cancel-btn {
  background-color: #f0f0f0 !important;
  color: #666 !important;
  border-color: #f0f0f0 !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.cancel-btn:hover {
  background-color: #e0e0e0 !important;
}

.confirm-btn {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.confirm-btn:hover {
  background-color: #85c787 !important;
}

/* 座位安排对话框样式 */
.seat-dialog {
  border-radius: 12px !important;
}

.seat-dialog .el-dialog__body {
  padding: 20px;
  overflow-y: auto;
  max-height: 70vh;
}

.seat-legend {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.seat-icon {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 3px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.seat-icon.available {
  background-color: #a3d9a5;
}

.seat-icon.unavailable {
  background-color: #ffccd5;
}

.screen-indicator {
  text-align: center;
  background-color: #f1f1f1;
  padding: 8px;
  margin-bottom: 30px;
  border-radius: 20px;
  color: #7f8c8d;
  font-weight: 500;
  width: 80%;
  margin-left: auto;
  margin-right: auto;
}

.seat-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.seat-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.row-label {
  width: 40px;
  text-align: center;
  font-weight: 500;
  color: #666;
}

.seats-in-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  flex: 1;
}

.seat {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.seat:hover {
  transform: scale(1.1);
}

.seat-available, .mini-seat.seat-available {
  background-color: #a3d9a5;
}

.seat-unavailable, .mini-seat.seat-unavailable {
  background-color: #ffccd5;
}

/* 空状态样式 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #888;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  color: #e0e0e0;
}

.empty-text {
  font-size: 18px;
  margin-bottom: 25px;
}

.empty-action {
  background-color: #ff99b3;
  border-color: #ff99b3;
  color: white;
  border-radius: 30px;
  padding: 8px 25px;
}

.empty-action:hover {
  background-color: #ff6b8b;
  border-color: #ff6b8b;
}

/* 响应式样式 */
@media (max-width: 992px) {
  .search-and-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-buttons {
    width: 100%;
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .category-select {
    width: 100%;
  }

  .search-group {
    width: 100%;
  }

  .search-btn {
    width: 100%;
  }

  .action-buttons {
    flex-wrap: wrap;
  }

  .action-buttons button {
    flex: 1;
    min-width: 120px;
  }

  .hall-cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>
