<template>
  <div class="order-management-container">
    <!-- 卡片视图 -->
    <el-card class="box-card shadow-sm">
      <!-- 搜索和筛选区域 -->
      <div class="filter-container mb-6">
        <el-row :gutter="20" class="filter-row">
          <el-col :span="6" :xs="24" class="filter-item">
            <el-input
              v-model="inputUserName"
              placeholder="请输入用户名"
              clearable
              class="search-input"
              prefix-icon="el-icon-user">
            </el-input>
          </el-col>
          <el-col :span="6" :xs="24" class="filter-item">
            <el-select
              v-model="selectedState"
              placeholder="请选择订单状态"
              clearable
              class="search-select">
              <el-option
                v-for="item in payStates"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="8" :xs="24" class="filter-item">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期时间"
              class="date-picker"
              prefix-icon="el-icon-calendar">
            </el-date-picker>
          </el-col>
        </el-row>

        <!-- 操作按钮区域 -->
        <el-row :gutter="15" class="button-row mt-4">
          <el-col :span="24" class="button-group">
            <el-button
              @click="getBillList"
              class="search-button">
              <i class="el-icon-search mr-2"></i> 搜索
            </el-button>
            <el-button
              type="danger"
              @click="multipleDelete"
              :disabled="multipleSelection.length === 0"
              class="delete-button">
              <i class="el-icon-delete mr-2"></i> 批量删除
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 订单卡片列表 -->
      <div class="order-cards-container">
        <div
          v-for="(order, index) in billList"
          :key="order.billId"
          class="order-card"
          :class="{ 'selected-card': isSelected(order.billId) }"
        >
          <!-- 选择框 -->
          <div class="card-selector">
            <el-checkbox
              v-model="selectedOrders[order.billId]"
              @change="handleOrderSelect(order, $event)"
              class="order-checkbox">
            </el-checkbox>
          </div>

          <!-- 订单卡片内容 -->
          <div class="card-content">
            <div class="card-header">
              <div class="order-number">#{{ order.billId }}</div>
              <span
                class="status-badge"
                :class="{
                  'completed': order.payState === true,
                  'pending': order.payState !== true && order.cancelState !== true,
                  'timeout': order.cancelState === true && order.cancelTime === null,
                  'canceled': order.cancelState === true && order.cancelTime !== null
                }">
                {{ getStatusText(order) }}
              </span>
            </div>

            <div class="card-body">
              <div class="movie-info">
                <h3 class="movie-title">{{ order.session.movie.movieName }}</h3>
                <p class="hall-info">
                  <i class="el-icon-video-camera mr-1"></i>
                  {{ order.session.hall.hallName }}
                </p>
              </div>

              <div class="order-details">
                <div class="detail-item">
                  <span class="detail-label">用户:</span>
                  <span class="detail-value">{{ order.user.userName }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">日期:</span>
                  <span class="detail-value">{{ order.session.sessionDate }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">时间:</span>
                  <span class="detail-value">{{ order.session.playTime }}-{{ order.session.endTime }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">座位:</span>
                  <span class="detail-value">{{ order.seats }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">订于:</span>
                  <span class="detail-value">{{ order.createTime }}</span>
                </div>
              </div>
            </div>

            <div class="card-footer">
              <el-button
                type="text"
                @click="showEditDialog(order.billId)"
                :disabled="order.payState === true || order.cancelState === true"
                class="edit-button">
                <i class="el-icon-edit mr-1"></i> 编辑
              </el-button>
              <el-button
                type="text"
                @click="deleteBillById(order.billId)"
                class="delete-button">
                <i class="el-icon-delete mr-1"></i> 删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态显示 -->
        <div v-if="billList.length === 0" class="empty-state">
          <el-empty
            description="没有找到匹配的订单"
            class="empty-component">
          </el-empty>
        </div>
      </div>

      <!-- 分页区域 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryInfo.pageNum"
        :page-sizes="[3, 6, 9]"
        :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination-container mt-6">
      </el-pagination>
    </el-card>

    <!-- 修改订单对话框 -->
    <el-dialog
      title="修改订单"
      :visible.sync="editDialogVisible"
      width="60%"
      @close="editDialogClosed"
      custom-class="custom-dialog">
      <el-form
        :model="editForm"
        ref="editFormRef"
        label-width="120px"
        class="form-container">
        <el-form-item label="订单编号" prop="billId">
          <el-input v-model="editForm.billId" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="user.userName">
          <el-input v-model="editForm.user.userName" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="电影名称" prop="session.movie.movieName">
          <el-input v-model="editForm.session.movie.movieName" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="影厅名称" prop="session.hall.hallName">
          <el-input v-model="editForm.session.hall.hallName" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="座位" prop="seats">
          <el-input v-model="editForm.seats" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="场次日期" prop="session.sessionDate">
          <el-input v-model="editForm.session.sessionDate" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="播放时间" prop="session.playTime">
          <el-input v-model="editForm.session.playTime" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="散场时间" prop="session.endTime">
          <el-input v-model="editForm.session.endTime" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="订单时间" prop="createTime">
          <el-input v-model="editForm.createTime" disabled class="form-input disabled-input"></el-input>
        </el-form-item>
        <el-form-item label="支付状态" prop="payState" v-if="!editForm.cancelState">
          <el-select
            v-model="editForm.payState"
            placeholder="请选择支付状态"
            clearable
            class="form-select">
            <el-option
              v-for="item in payStates"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="取消状态" prop="cancelState" v-if="!editForm.payState">
          <el-select
            v-model="editForm.cancelState"
            placeholder="请选择取消状态"
            clearable
            class="form-select">
            <el-option
              v-for="item in cancelStates"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" class="cancel-button">取 消</el-button>
        <el-button
          type="primary"
          @click="editBillInfo"
          class="confirm-button">
          <i class="el-icon-check mr-1"></i> 确 定
        </el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import global from "@/assets/css/global.css"
import moment from 'moment'
export default {
  name: "billInfo",
  data() {
    return {
      queryInfo: {
        queryByUserName: '',
        payState: '',
        createTime: '',
        pageNum: 1,
        pageSize: 6
      },
      billList: [],
      total: 0,
      multipleSelection: [],
      selectedOrders: {}, // 用于存储选中的订单ID
      inputUserName: '',
      selectedState: '',
      selectedDate: '',
      editDialogVisible: false,
      editForm: {
        user: {
          userName: ''
        },
        session: {
          movie: {},
          hall: {}
        }
      },
      payStates: [
        {
          id: 0,
          name: '未支付'
        },
        {
          id: 1,
          name: '已完成'
        }
      ],
      cancelStates: [
        {
          id: 0,
          name: '未取消'
        },
        {
          id: 1,
          name: '已取消'
        }
      ],
    }
  },
  created() {
    this.getBillList()
  },
  methods: {
    async getBillList() {
      this.queryInfo.payState = this.selectedState
      this.queryInfo.createTime = this.selectedDate
      this.queryInfo.queryByUserName = this.inputUserName

      const _this = this;
      try {
        const resp = await axios.get('sysBill', {params: _this.queryInfo})
        _this.billList = resp.data.data || [];
        _this.total = resp.data.total || 0;
        _this.queryInfo.pageSize = resp.data.pageSize || 7;
        _this.queryInfo.pageNum = resp.data.pageNum || 1;

        // 重置选中状态
        this.selectedOrders = {};
      } catch (error) {
        console.error('加载订单列表错误：', error)
        this.$message.error('加载订单失败，请稍后重试')
      }
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getBillList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getBillList()
    },
    // 处理订单选择
    handleOrderSelect(order, isSelected) {
      this.$set(this.selectedOrders, order.billId, isSelected);

      // 更新多选列表
      if (isSelected) {
        this.multipleSelection.push(order);
      } else {
        this.multipleSelection = this.multipleSelection.filter(item => item.billId !== order.billId);
      }
    },
    // 检查订单是否被选中
    isSelected(billId) {
      return this.selectedOrders[billId];
    },
    // 获取订单状态文本
    getStatusText(order) {
      if (order.payState === true) return "已完成";
      if (order.cancelState === true && order.cancelTime === null) return "超时取消";
      if (order.cancelState === true && order.cancelTime !== null) return "用户取消";
      return "未支付";
    },
    // 显示修改对话框，回显数据
    async showEditDialog(id) {
      let isAbleEdit = true
      try {
        const resp = await axios.get('sysBill/' + id)
        this.editForm = resp.data.data || {}
        this.editForm.user = resp.data.data.user || {}

        if (this.editForm.payState === true || this.editForm.cancelState === true) {
          isAbleEdit = false
          this.$alert('抱歉！订单已完成或已取消，不能修改。', '修改请求异常通知', {
            confirmButtonText: '我知道了',
            callback: action => {
              this.$router.push('/bill')
            }
          })
        }
      } catch (error) {
        console.error('加载订单详情错误：', error)
        this.$message.error('加载订单详情失败')
        return
      }

      if (isAbleEdit) {
        this.editForm.payState = 0
        this.editForm.cancelState = 0
        this.editDialogVisible = true
      }
    },
    // 监听修改对话框的关闭事件
    editDialogClosed(){
      if (this.$refs.editFormRef) {
        this.$refs.editFormRef.resetFields()
      }
    },
    // 修改订单信息并提交
    async editBillInfo() {
      const _this = this

      if (_this.editForm.cancelState && _this.editForm.payState) {
        this.$alert('抱歉，修改失败！取消状态和支付状态不能同时为真。', '修改订单信息异常', {
          confirmButtonText: '我知道了'
        })
        return
      }

      try {
        if (_this.editForm.payState) {
          const { data: res} = await axios.put('sysBill', JSON.stringify(_this.editForm))
          if(res.code !== 200) return this.$message.error('支付失败')
          this.$message.success('支付订单成功')
          this.editDialogVisible = false
          this.getBillList()
          return
        } else if (_this.editForm.cancelState) {
          _this.editForm.cancelState = true
          _this.editForm.cancelTime = moment(new Date()).format('YYYY-MM-DD HH:mm:ss')

          // 获取场次座位信息
          const { data : curSession } = await axios.get('sysSession/find/' + _this.editForm.sessionId)
          let sessionSeats = JSON.parse(curSession.data.sessionSeats)

          // 解析出订单选择的座位，更新座位信息
          for(let seat of JSON.parse(_this.editForm.seats)){
            let row = seat.substring(0, seat.indexOf('排'))
            let col = Number.parseInt(seat.substring(seat.indexOf('排') + 1, seat.length - 1))
            sessionSeats[row][col - 1] = 0
          }

          // 更新订单信息和场次座位信息
          axios.defaults.headers.put['Content-Type'] = 'application/json'
          const { data: resp } = await axios.put('sysBill/cancel',
            JSON.stringify({ticket: _this.editForm, sessionSeats: JSON.stringify(sessionSeats)}))

          if(resp.code !== 200) return this.$message.error('取消失败')
          this.getBillList()
          this.editDialogVisible = false
          this.$message.success('取消订单成功')
        }
      } catch (error) {
        console.error('修改订单错误：', error)
        this.$message.error('操作失败，请稍后重试')
      }
    },

    async multipleDelete(){
      if (this.multipleSelection.length === 0) {
        this.$message.warning('请先选择要删除的订单')
        return
      }

      const _this = this
      try {
        // 询问用户是否确认删除
        const resp = await this.$confirm('此操作将永久删除这些条目, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        let ids = []
        this.multipleSelection.forEach(data => {
          ids.push(data.billId)
        })

        const response = await axios.delete('sysBill/' + ids)
        if (response.data.code !== 200) {
          return this.$message.error('批量删除失败！')
        }

        this.getBillList()
        this.$message.success('批量删除成功！')
      } catch (err) {
        if (err === 'cancel') {
          return _this.$message.info('已取消删除')
        }
        console.error('批量删除错误：', err)
        this.$message.error('删除失败，请稍后重试')
      }
    },

    // 根据id删除对应的订单
    async deleteBillById(id){
      const _this = this
      try {
        // 询问用户是否确认删除
        const resp = await this.$confirm('此操作将永久删除该条目, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await axios.delete('sysBill/' + id)
        if (response.data.code !== 200) {
          return _this.$message.error('删除订单失败！')
        }

        this.getBillList()
        this.$message.success('删除订单成功！')
      } catch (err) {
        if (err === 'cancel') {
          return _this.$message.info('已取消删除')
        }
        console.error('删除订单错误：', err)
        this.$message.error('删除失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
/* 全局容器样式 - 清新清新可爱风格基础 */
.order-management-container {
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

/* 面包屑导航样式 */
.board {
  padding: 12px 16px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(255, 183, 197, 0.2);
}
.breadcrumb {
  font-size: 16px;
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

/* 卡片样式 - 清新可爱风格 */
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

/* 筛选区域样式 */
.filter-container {
  margin-bottom: 24px;
  padding: 16px;
  background-color: #fff0f3;
  border-radius: 12px;
}
.filter-row {
  display: flex;
  flex-wrap: wrap;
}
.filter-item {
  margin-bottom: 12px;
}
.search-input, .search-select, .date-picker {
  width: 100%;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s;
  height: 46px;
  font-size: 16px;
}
.search-input:focus, .search-select:focus, .date-picker:focus {
  border-color: #ff9aa2 !important;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2) !important;
}

/* 按钮样式 */
.button-row {
  display: flex;
  justify-content: flex-start;
}
.button-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
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
}
.search-button:hover {
  background-color: #a8d1fc !important;
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(189, 224, 254, 0.4);
}
.delete-button {
  background-color: #ffb3ba !important;
  border-color: #ffb3ba !important;
  color: #d1495b !important;
  padding: 12px 24px;
  border-radius: 50px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(255, 179, 186, 0.3);
}
.delete-button:hover:not(:disabled) {
  background-color: #ff8fa3 !important;
  transform: translateY(-3px);
  box-shadow: 0 4px 10px rgba(255, 179, 186, 0.4);
}
.delete-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 订单卡片卡片卡片容器 */
.order-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

/* 订单卡片卡片样式 */
.order-card {
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(255, 183, 197, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  position: relative;
}

.order-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(255, 183, 197, 0.15);
}

/* 选中状态样式 */
.selected-card {
  border: 2px solid #ff8fab;
}

/* 选择框样式 */
.card-selector {
  padding: 16px 12px;
  background-color: #fff0f3;
}

/* 卡片内容样式 */
.card-content {
  flex: 1;
  padding: 16px;
}

/* 卡片头部头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.order-number {
  font-weight: 600;
  color: #666;
  font-size: 15px;
}

/* 状态标签样式 */
.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}
.completed {
  background-color: #e6f7ed;
  color: #00875a;
}
.pending {
  background-color: #e8f3ff;
  color: #1890ff;
}
.timeout {
  background-color: #fff8e6;
  color: #faad14;
}
.canceled {
  background-color: #ffe8e8;
  color: #f5222d;
}

/* 卡片主体内容 */
.card-body {
  margin-bottom: 16px;
}

.movie-info {
  margin-bottom: 16px;
}

.movie-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  transition: color 0.3s;
}

.movie-title:hover {
  color: #ff6b8b;
}

.hall-info {
  color: #666;
  font-size: 14px;
}

/* 订单详情细 */
.order-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.detail-item {
  font-size: 14px;
}

.detail-label {
  color: #999;
  margin-right: 4px;
}

.detail-value {
  color: #555;
  font-weight: 500;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.edit-button {
  color: #4a90e2 !important;
  transition: all 0.2s;
}

.edit-button:hover {
  color: #1890ff !important;
  background-color: rgba(24, 144, 255, 0.05) !important;
}

.edit-button:disabled {
  color: #c9c9c9 !important;
  cursor: not-allowed;
}

.card-footer .delete-button {
  color: #f5222d !important;
  background: none !important;
  padding: 5px 10px !important;
  box-shadow: none !important;
  transition: all 0.2s;
}

.card-footer .delete-button:hover {
  color: #d93025 !important;
  background-color: rgba(217, 48, 37, 0.05) !important;
  transform: none !important;
}

/* 空状态样式 */
.empty-state {
  grid-column: 1 / -1;
  padding: 60px 20px;
  text-align: center;
}

/* 分页选框复选框 */
.order-checkbox .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #ff8fab;
  border-color: #ff8fab;
}

.order-checkbox .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #ff8fab;
}

/* 分页样式 */
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
.el-pagination__sizes .el-input .el-input__inner {
  font-size: 16px;
  height: 36px;
  border-radius: 8px;
  border-color: #ffccd5;
}
.el-pagination .btn-next,
.el-pagination .btn-prev {
  border-radius: 50% !important;
  width: 36px;
  height: 36px;
  border-color: #ffccd5 !important;
}
.el-pagination .btn-next:hover,
.el-pagination .btn-prev:hover {
  background-color: #fff0f3;
}

/* 对话框样式 */
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
  max-height: 70vh;
  overflow-y: auto;
}
.custom-dialog .el-dialog__body::-webkit-scrollbar {
  width: 6px;
}
.custom-dialog .el-dialog__body::-webkit-scrollbar-track {
  background: #fff0f3;
  border-radius: 10px;
}
.custom-dialog .el-dialog__body::-webkit-scrollbar-thumb {
  background: #ffccd5;
  border-radius: 10px;
}
.custom-dialog .el-dialog__footer {
  padding: 18px 24px;
  border-top: 1px solid #ffe6eb !important;
  background-color: #fff0f3 !important;
}

/* 表单样式 */
.form-container {
  width: 100%;
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
.form-select {
  width: 100%;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s;
  height: 46px;
  font-size: 16px;
}
.form-select:focus {
  border-color: #ff9aa2 !important;
  box-shadow: 0 0 0 2px rgba(255, 183, 197, 0.2) !important;
}
.disabled-input {
  background-color: #f5f7fa;
  color: #8c8c8c;
  border-color: #d9d9d9;
}
.el-form-item__label {
  font-size: 16px;
  color: #ff8fab !important;
  font-weight: 500;
}

/* 对话框按钮 */
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
.cancel-button:hover {
  background-color: #e0e0e0 !important;
  transform: translateY(-2px) !important;
}
.confirm-button {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  padding: 10px 20px !important;
  font-size: 16px !important;
  box-shadow: 0 2px 5px rgba(163, 217, 165, 0.3) !important;
}
.confirm-button:hover {
  background-color: #85c787 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 10px rgba(163, 217, 165, 0.4) !important;
}

/* 间距工具类 */
.mb-4 {
  margin-bottom: 16px !important;
}
.mb-6 {
  margin-bottom: 24px !important;
}
.mt-4 {
  margin-top: 16px !important;
}
.mt-6 {
  margin-top: 24px !important;
}
.mr-2 {
  margin-right: 8px !important;
}
.mr-1 {
  margin-right: 4px !important;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .filter-item {
    flex: 0 0 50%;
    max-width: 50%;
  }

  .order-cards-container {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .order-management-container {
    padding: 15px;
    font-size: 15px;
  }
  .filter-item {
    flex: 0 0 100%;
    max-width: 100%;
  }
  .button-group {
    flex-direction: column;
    width: 100%;
  }
  .search-button, .delete-button {
    width: 100%;
    margin-bottom: 10px;
  }
  .custom-dialog {
    width: 95% !important;
  }
  .order-cards-container {
    grid-template-columns: 1fr;
  }
  .order-details {
    grid-template-columns: 1fr;
  }
}
</style>
