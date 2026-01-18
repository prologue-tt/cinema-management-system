<template>
  <div class="session-management-container">
    <!--页面标题-->
    <div class="page-title">
      <h1>场次信息管理</h1>
      <p>管理影院所有电影场次的排期、时间和座位信息</p>
    </div>

    <!--卡片视图-->
    <el-card class="box-card">
      <el-row :gutter="24" class="search-row">
        <el-col :span="4" :xs="24" :sm="12">
          <el-select v-model="selectedHallId" placeholder="请选择影厅名称" clearable class="search-select">
            <el-option
              v-for="item in hallList"
              :key="item.hallId"
              :label="item.hallName"
              :value="item.hallId">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="4" :xs="24" :sm="12">
          <el-select v-model="selectedMovieId" placeholder="请选择电影名称" clearable class="search-select">
            <el-option
              v-for="item in movieList"
              :key="item.movieId"
              :label="item.movieName"
              :value="item.movieId">
            </el-option>
          </el-select>
        </el-col>

        <el-col :span="10" :xs="24" :sm="24">
          <el-date-picker
            v-model="selectedDate"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期"
            class="date-picker">
          </el-date-picker>
        </el-col>
        <el-col :span="6" :xs="24" :sm="24" class="search-button-container">
          <el-button
            class="search-button"
            icon="el-icon-search"
            @click="getSessionList">
            搜索
          </el-button>
        </el-col>
      </el-row>
      <br>
      <el-row class="button-row">
        <el-col :span="24" class="button-group">
          <el-button type="primary" @click="showAddDialog" class="action-button add-button">
            <i class="el-icon-plus"></i> 添加场次
          </el-button>
          <el-button type="danger" @click="isAbleMultipleDelete" class="action-button delete-button">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </el-col>
      </el-row>

      <!--影厅分类列表-->
      <el-table :data="sessionList" style="width: 100%"  @selection-change="handleSelectionChange" class="session-table">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="sessionId" label="#" width="40"></el-table-column>
        <el-table-column prop="hall.hallName" label="影厅名称"></el-table-column>
        <el-table-column prop="movie.movieName" label="电影名称"></el-table-column>
        <el-table-column prop="languageVersion" label="语言"></el-table-column>
        <el-table-column prop="sessionDate" label="场次时间" width="100"></el-table-column>
        <el-table-column prop="playTime" label="播放时间" width="110">
          <template slot-scope="scope">
            <span>{{scope.row.playTime}} - {{scope.row.endTime}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sessionPrice" label="场次价格"></el-table-column>
        <el-table-column prop="seatNums" label="总座位数"></el-table-column>
        <el-table-column prop="sallNums" label="已售"></el-table-column>
        <el-table-column prop="seatNums - sallNums" label="剩余">
          <template slot-scope="scope">
            <span>{{scope.row.seatNums - scope.row.sallNums}}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              @click="isAbleEdit(scope.row.sessionId)"
              class="operation-btn edit-btn">
              编辑
            </el-button>
            <el-button
              type="danger"
              size="mini"
              @click="isAbleDelete(scope.row)"
              :disabled="scope.row.sallNums > 0"
              class="operation-btn delete-btn">
              <i class="el-icon-trash"></i> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!--分页区域-->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryInfo.pageNum"
          :page-sizes="[5, 7, 9]"
          :page-size="queryInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="pagination">
        </el-pagination>
      </div>

    </el-card>

    <!--添加场次对话框-->
    <el-dialog title="添加场次" :visible.sync="addDialogVisible" width="60%" @close="addDialogClosed" class="custom-dialog">
      <!--内容主题区-->
      <el-form :model="addForm" :rules="addFormRules" ref="addFormRef" label-width="150px" class="form-container">
        <el-form-item label="影厅名称" prop="hallId">
          <el-select v-model="addForm.hallId" placeholder="请选择影厅名称" clearable >
            <el-option
              v-for="item in hallList"
              :key="item.hallId"
              :label="item.hallName"
              :value="item.hallId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="语言版本" prop="languageVersion">
          <el-input v-model="addForm.languageVersion"></el-input>
        </el-form-item>
        <el-form-item label="电影名称" prop="movieId">
          <el-select v-model="addForm.movieId" placeholder="请选择电影名称" clearable >
            <el-option
              v-for="item in movieList"
              :key="item.movieId"
              :label="item.movieName"
              :value="item.movieId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="场次日期" prop="sessionDate">
          <el-date-picker
            v-model="addForm.sessionDate"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="播放时间" prop="sessionTime">
          <el-time-picker
            v-model="addForm.playTime"
            value-format = 'HH:mm'
            placeholder="选择播放时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="场次价格" prop="sessionPrice">
          <el-input v-model="addForm.sessionPrice"></el-input>
        </el-form-item>
      </el-form>
      <!--底部区域-->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false" class="dialog-btn cancel-btn">取 消</el-button>
        <el-button type="primary" @click="addSession" class="dialog-btn confirm-btn"> 确 定</el-button>
      </span>
    </el-dialog>

    <!--修改场次对话框-->
    <el-dialog title="修改场次" :visible.sync="editDialogVisible" width="60%" @close="editDialogClosed" class="custom-dialog">
      <el-form :model="editForm" :rules="editFormRules" ref="editFormRef" label-width="150px" class="form-container">
        <el-form-item label="影厅名称" prop="hallId">
          <el-select v-model="editForm.hallId" placeholder="请选择影厅名称" clearable >
            <el-option
              v-for="item in hallList"
              :key="item.hallId"
              :label="item.hallName"
              :value="item.hallId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="语言版本" prop="languageVersion">
          <el-input v-model="editForm.languageVersion"></el-input>
        </el-form-item>
        <el-form-item label="电影名称" prop="movieId">
          <el-select v-model="editForm.movieId" placeholder="请选择电影名称" clearable >
            <el-option
              v-for="item in movieList"
              :key="item.movieId"
              :label="item.movieName"
              :value="item.movieId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="场次日期" prop="sessionDate">
          <el-date-picker
            v-model="editForm.sessionDate"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="播放时间" prop="sessionTime">
          <el-time-picker
            v-model="editForm.playTime"
            value-format = 'HH:mm'
            placeholder="选择播放时间">
          </el-time-picker>
        </el-form-item>
        <el-form-item label="场次价格" prop="sessionPrice">
          <el-input v-model="editForm.sessionPrice"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" class="dialog-btn-btn cancel-btn">取 消</el-button>
        <el-button type="primary" @click="editSessionInfo" class="dialog-btn confirm-btn"> 确 定</el-button>
      </span>
    </el-dialog>

    <!--安排座位对话框-->
    <el-dialog title="查看座位" :visible.sync="checkDialogVisible" :width="checkDialogWidth" class="seat-dialog">
      <div class="seat-legend">
        <div class="legend-item">
          <span class="seat" :class="isSelected[0]"></span>
          <span>可用座位</span>
        </div>
        <div class="legend-item">
          <span class="seat" :class="isSelected[1]"></span>
          <span>禁用座位</span>
        </div>
        <div class="legend-item">
          <span class="seat" :class="isSelected[2]"></span>
          <span>已售出</span>
        </div>
      </div>
      <br/>
      <div class="screen-indicator">屏幕</div>
      <div class="row" v-for="(value, key) in seats" :key="key">
        <span class="row-label">{{key}}</span>
        <div class="seats-row">
          <span class="seat" :class="isSelected[seats[key][index]]" v-for="(item, index) in value" :key="index"></span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import global from "@/assets/css/global.css"
import echarts from 'echarts'
import moment from 'moment'
export default {
  name: "SessionManagement",
  data() {
    return {
      queryInfo: {
        hallId: '',
        movieId: '',
        sessionDate: '',
        pageNum: 1,
        pageSize: 7
      },
      sessionList: [],
      total: 0,
      //控制对话框的显示与隐藏
      addDialogVisible: false,
      //添加影厅的表单数据
      addForm: {
        hallId: '',
        languageVersion: '',
        movieId: '',
        playTime: '',
        endTime: '',
        deadline: '',
        sessionDate: '',
        sessionPrice: '',
        sessionTips: '',
        sessionSeats: '',
        seatNums: 0
      },
      //验证表单规则对象
      addFormRules: {
        hallId: [
          { required: true, message: '请选择影厅名称', trigger: 'change' }
        ],
        languageVersion: [
          { required: true, message: '请输入电影语言', trigger: 'blur' }
        ],
        movieId: [
          { required: true, message: '请选择电影名称', trigger: 'change' }
        ],
        sessionDate: [
          { required: true, message: '请选择场次日期', trigger: 'blur' }
        ],
        playTime: [
          { required: true, message: '请选择播放时间', trigger: 'blur' }
        ],
        sessionPrice: [
          { required: true, message: '请输入场次价格', trigger: 'blur' }
        ]
      },
      editDialogVisible: false,
      editForm: {},
      checkAbleId: {},
      editFormRules: {
        hallId: [
          { required: true, message: '请选择影厅名称', trigger: 'change' }
        ],
        languageVersion: [
          { required: true, message: '请输入电影语言', trigger: 'blur' }
        ],
        movieId: [
          { required: true, message: '请选择电影名称', trigger: 'change' }
        ],
        sessionDate: [
          { required: true, message: '请选择场次日期', trigger: 'blur' }
        ],
        playTime: [
          { required: true, message: '请选择播放时间', trigger: 'blur' }
        ],
        sessionPrice: [
          { required: true, message: '请输入场次价格', trigger: 'blur' }
        ]
      },
      selectedHallId: '',
      endPlayTime: '',
      selectedMovieId: '',
      selectedDate: '',
      hallList: [],
      movieList: [],
      multipleSelection: [],
      checkDialogVisible: false,
      seats: {},
      col: '',
      isSelected: {
        0: 'seat-default',
        1: 'seat-choose',
        2: 'seat-sold'
      },
      editSeat: {},
      checkDialogWidth: '',
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        }
      },
      rowNums: '',
      colNums: '',
      rowStart: '',
      loginUser: JSON.parse(window.sessionStorage.getItem('loginUser'))
    }
  },
  created() {
    this.getSessionList()
    this.getHallList()
    this.getMovieList()
  },
  methods: {
    getSessionList() {
      this.queryInfo.hallId = this.selectedHallId
      this.queryInfo.movieId = this.selectedMovieId
      this.queryInfo.sessionDate = this.selectedDate
      const _this = this;
      axios.get('sysSession', {params: _this.queryInfo}).then(resp => {
        _this.sessionList = resp.data.data;
        _this.total = resp.data.total;
        _this.queryInfo.pageSize = resp.data.pageSize;
        _this.queryInfo.pageNum = resp.data.pageNum;
      })
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getSessionList()
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getSessionList()
    },
    showAddDialog(){
      this.addDialogVisible = true
    },
    // 监听添加对话框的关闭事件
    addDialogClosed(){
      this.$refs.addFormRef.resetFields()
    },
    // 监听添加按钮
    addSession(){
      const _this = this
      // 处理结束时间和截止时间
      this.handleEndTimeAndDeadline(_this.addForm.movieId, _this.addForm.sessionDate, _this.addForm.playTime)
      this.$refs.addFormRef.validate(async valid => {
        if (!valid) return this.$message.error('添加场次信息失败')

        let seats = ''
        let seatNums = 0
        //查询影厅信息，查出对应排数和排座位数，填充sessionSeats字段
        await axios.get('sysHall/' + this.addForm.hallId).then(resp => {
          _this.rowNums = resp.data.data.rowNums
          _this.colNums = resp.data.data.seatNumsRow
          _this.rowStart = resp.data.data.rowStart
          seatNums = resp.data.data.seatNums
          seats = resp.data.data.seatState
        })

        this.$set(this.addForm, 'seatNums', seatNums)
        this.$set(this.addForm, 'sessionSeats', seats)

        // 预校验成功，发网络请求
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        await axios.post('sysSession', JSON.stringify(_this.addForm)).then(resp => {
          if (resp.data.code !== 200){
            this.$message.error('添加场次信息失败！')
          }
        })

        //隐藏添加对话框
        this.addDialogVisible = false
        //重新加载列表
        this.getSessionList()
        this.$message.success('添加场次信息成功！')
      })
    },
    // 修改：移除了对未完成场次的编辑限制，仅检查是否有相关订单
    isAbleEdit(id) {
      this.checkAbleId.sessionId = id
      axios.get('sysBill', {params: this.checkAbleId}).then(response => {
        let bills = response.data.data
        // 只有当没有相关订单时才允许编辑
        if (response.data.total === 0) {
          this.showEditDialog(id)
        } else {
          let billIds = ''
          for (let temp of bills) {
            billIds += temp.billId+' '
          }
          this.$alert('有人买了这场电影的票~暂时无法修改场次信息\n', '哎呀😢', {
            confirmButtonText: '朕已阅',
            callback: action => {
              this.$router.push('/session')
            }
          })
        }
      })
    },
    // 显示修改对话框，回显数据
    async showEditDialog(id){
      const _this = this
      const { data : res} = await axios.get('sysSession/find/' + id)
      this.editForm = res.data
      this.editDialogVisible = true
    },
    // 监听修改对话框的关闭事件
    editDialogClosed(){
      this.$refs.editFormRef.resetFields()
    },
    // 修改场次信息并提交
    editSessionInfo(){
      this.handleEndTimeAndDeadline(this.editForm.movieId, this.editForm.sessionDate, this.editForm.playTime)
      let row = this.editForm.rowNums
      let col = this.editForm.seatNumsRow
      this.$set(this.editForm, 'seatNums', row * col)
      let seat = {}
      for (let i = 0; i < row; i++) {
        let arr = []
        for (let j = 0; j < col; j++) {
          arr.push(0)
        }
        seat[isNaN(parseInt(this.editForm.rowStart)) ? String.fromCharCode(i + this.editForm.rowStart.charCodeAt(0)) : i + parseInt(this.editForm.rowStart)] = arr
      }
      this.$set(this.editForm, 'seatState', JSON.stringify(seat))

      this.$refs.editFormRef.validate(async valid => {
        const _this = this
        if (!valid) return
        let success = true
        axios.defaults.headers.put['Content-Type'] = 'application/json'
        await axios.put('sysSession', JSON.stringify(_this.editForm)).then(resp => {
          if (resp.data.code !== 200){
            this.$message.error('修改场次信息失败！')
            success = false
          }
        })
        if (!success) return
        this.editDialogVisible = false
        this.getSessionList()
        this.$message.success('修改场次信息成功！')
      })
    },
    //处理散场时间和片场截止时间
    handleEndTimeAndDeadline(movieId, sessionDate, playTime){
      this.addForm.deadline = ''
      this.editForm.deadline = ''
      this.addForm.endTime = ''
      this.editForm.endTime = ''
      let resultEndTime = ''
      //电影时长
      let movieLengthTime
      for (let item of this.movieList) {
        if (item.movieId === movieId) {
          movieLengthTime = item.movieLength
          break
        }
      }
      //临时时间，用于记录开播时间，使用moment工具
      let tempTime = new Date(sessionDate)
      //分离开播时间
      let hoursMin = playTime.split(':')
      tempTime.setHours(hoursMin[0])
      tempTime.setMinutes(hoursMin[1])
      tempTime.setSeconds(0)
      let tempEndTime = moment(tempTime).add(movieLengthTime,'m')._d
      // 片场结束时间再加20分钟
      let tempDeadline = moment(moment(tempEndTime).add(20,'m')._d).format('YYYY-MM-DD HH:mm:ss')

      // 设置deadline
      this.addForm.deadline = tempDeadline
      this.editForm.deadline = tempDeadline
      let endHours = tempEndTime.getHours()
      let endMins = tempEndTime.getMinutes()
      //时间格式化
      if (endHours < 10) {
        resultEndTime = '0'+endHours+':'
      } else {
        resultEndTime = endHours+':'
      }
      if (endMins < 10) {
        resultEndTime += '0'+endMins
      } else {
        resultEndTime += endMins
      }
      this.addForm.endTime = resultEndTime
      this.editForm.endTime = resultEndTime
    },
    // 监听多选框变化
    handleSelectionChange(val){
      this.multipleSelection = val
    },
    // 批量删除前校验 - 已购买的场次无法删除
    async isAbleMultipleDelete() {
      // 检查是否有选中的场次
      if (this.multipleSelection.length === 0) {
        return this.$message.warning('请先选择要删除的场次')
      }

      let hasPurchased = false
      let purchasedIds = []

      // 检查选中的场次中是否有已售座位
      for (let session of this.multipleSelection) {
        if (session.sallNums > 0) {
          hasPurchased = true
          purchasedIds.push(session.sessionId)
        }
      }

      // 如果有已售场次，提示无法删除
      if (hasPurchased) {
        return this.$alert(
          `以下场次已有观众购买，无法删除：\n${purchasedIds.join('、')}`,
          '删除失败',
          {
            confirmButtonText: '朕已阅',
            type: 'error'
          }
        )
      }

      // 所有选中场次都未售出，可以执行删除
      await this.multipleDelete()
    },
    async multipleDelete(){
      const _this = this
      //询问用户是否确认删除
      const resp = await this.$confirm('此操作将永久删除这些条目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      if (resp === 'cancel'){
        return _this.$message.info('已取消删除')
      }

      let ids = []
      this.multipleSelection.forEach(data => {
        ids.push(data.sessionId)
      })
      await axios.delete('sysSession/' + ids).then(resp => {
        if (resp.data.code !== 200){
          this.$message.error('批量删除场次失败！')
        } else {
          this.$message.success('批量删除场次成功！')
        }
      })
      this.getSessionList()
    },
    // 单个删除前校验 - 已购买的场次无法删除
    async isAbleDelete(session) {
      // 检查是否有已售座位
      if (session.sallNums > 0) {
        return this.$alert(
          '该场次已有观众购买电影票，无法删除',
          '删除失败',
          {
            confirmButtonText: '朕已阅',
            type: 'error'
          }
        )
      }

      // 没有已售座位，执行删除
      await this.deleteSessionById(session.sessionId)
    },
    //根据id删除对应的场次
    async deleteSessionById(id){
      const _this = this
      //询问用户是否确认删除
      const resp = await this.$confirm('此操作将永久删除该条目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      if (resp === 'cancel'){
        return _this.$message.info('已取消删除')
      }

      await axios.delete('sysSession/' + id).then(resp => {
        if (resp.data.code !== 200){
          _this.$message.error('删除场次信息失败！')
        } else {
          _this.$message.success('删除场次信息成功！')
        }
      })
      this.getSessionList()
    },
    getHallList(){
      const _this = this
      axios.get('sysHall').then(resp => {
        _this.hallList = resp.data.data;
      })
    },
    getMovieList(){
      const _this = this
      axios.get('sysMovie/find').then(resp => {
        _this.movieList = resp.data.data;
      })
    },
    async checkSeat(id){
      const _this = this
      await axios.get('sysSession/find/' + id).then(resp => {
        _this.seats = JSON.parse(resp.data.data.sessionSeats)
        _this.col = resp.data.data.hall.seatNumsRow
      })
      this.checkDialogWidth = 200 + this.col * 40 + 'px'
      this.checkDialogVisible = true
    }
  }
}
</script>

<style scoped>
/* 全局容器样式 */
.session-management-container {
  padding: 20px;
  background-color: #fff5f7;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 页面标题样式 */
.page-title {
  margin-bottom: 20px;
  text-align: center;
  padding: 15px;
  background-color: #fff0f3;
  border-radius: 12px;
  box-shadow: 0 3px 10px rgba(255, 153, 170, 0.15);
}
.page-title h1 {
  font-size: 1.8rem;
  color: #ff6b8b;
  margin-bottom: 8px;
  font-weight: 600;
}

.page-title p {
  color: #ff8fab;
  font-size: 0.95rem;
}

/* 卡片样式 */
.box-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 5px 15px rgba(255, 183, 197, 0.15) !important;
  overflow: hidden;
  background-color: #ffffff !important;
}

/* 搜索区域样式 */
.search-row {
  padding: 15px 20px 0;
}

.search-select, .date-picker {
  width: 100%;
  border-radius: 8px !important;
  border-color: #ffccd5 !important;
  transition: all 0.3s ease;
}

.search-select:focus, .date-picker:focus {
  box-shadow: 0 0 0 2px rgba(255, 153, 170, 0.2) !important;
}

.search-button-container {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.search-button {
  background-color: #ff99aa !important;
  border-color: #ff99aa !important;
  color: white !important;
  padding: 10px 20px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgba(255, 153, 170, 0.3) !important;
}

.search-button:hover {
  background-color: #ff7a93 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(255, 153, 170, 0.4) !important;
}

/* 按钮区域样式 */
.button-row {
  padding: 10px 20px;
}

.button-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-button {
  padding: 10px 20px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 5px rgb(246, 229, 240) !important;
}

.add-button {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
}

.add-button:hover {
  background-color: #85c787 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(163, 217, 165, 0.4) !important;
}

.delete-button {
  background-color: #ffb3ba !important;
  border-color: #ffb3ba !important;
  color: white !important;
}

.delete-button:hover {
  background-color: #ff8f9a !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 8px rgba(255, 179, 186, 0.4) !important;
}

/* 表格样式 - 关键修改部分 */
.session-table {
  margin-top: 10px;
  border-radius: 10px !important;
  overflow: hidden !important;
  border: none !important; /* 移除表格外边框 */
}

/* 移除表格的灰色间隔线和背景色 */
.session-table th {
  background-color: #fff !important; /* 表头背景改为白色 */
  color: #ff6b8b !important;
  font-weight: 500 !important;
  border-bottom: none !important; /* 移除表头下边框 */
}

.session-table td {
  background-color: #fff !important; /* 单元格背景改为白色 */
  border-bottom: none !important; /* 移除单元格下边框 */
  transition: background-color 0.2s ease !important;
}

/* 移除斑马条纹效果 */
.session-table.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #fff !important;
}

/* 移除表格边框 */
.session-table .el-table__cell {
  border-right: none !important;
}

.session-table .el-table__header th {
  border-right: none !important;
}

/* 保留hover效果但使用更浅的颜色 */
.session-table tr:hover td {
  background-color: #f6e5f0 !important;
}

/* 操作按钮样式 */
.operation-btn {
  margin-right: 5px !important;
  border-radius: 6px !important;
  transition: all 0.2s ease !important;
}

.edit-btn {
  background-color: #bde0fe !important;
  border-color: #bde0fe !important;
  color: #4a90e2 !important;
}

.edit-btn:hover {
  background-color: #a8d1fc !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 2px 5px rgba(189, 224, 254, 0.5) !important;
}

.delete-btn {
  background-color: #ffccd5 !important;
  border-color: #ffccd5 !important;
  color: #e63946 !important;
}

.delete-btn:hover {
  background-color: #ffb3c1 !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 2px 5px rgba(255, 204, 213, 0.5) !important;
}

.delete-btn:disabled {
  background-color: #f8d7da !important;
  border-color: #f5c6cb !important;
  color: #721c24 !important;
  cursor: not-allowed !important;
  opacity: 0.7 !important;
}

/* 分页样式 */
.pagination-container {
  padding: 15px 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination {
  color: #ff6b8b !important;
}

.pagination .el-pager li {
  border-radius: 4px !important;
  margin: 0 3px !important;
}

.pagination .el-pager li.active {
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
.form-container .el-select,
.form-container .el-date-picker,
.form-container .el-time-picker {
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

.dialog-btn {
  padding: 8px 20px !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.cancel-btn {
  background-color: #f0f0f0 !important;
  border-color: #f0f0f0 !important;
  color: #666 !important;
}

.cancel-btn:hover {
  background-color: #e0e0e0 !important;
}

.confirm-btn {
  background-color: #a3d9a5 !important;
  border-color: #a3d9a5 !important;
  color: white !important;
}

.confirm-btn:hover {
  background-color: #85c787 !important;
}

/* 座位对话框样式 */
.seat-dialog {
  border-radius: 12px !important;
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

.row {
  white-space: nowrap;
  margin-top: 10px;
  padding: 0 10px;
  display: flex;
  align-items: center;
}

.row-label {
  width: 40px;
  text-align: center;
  font-weight: 500;
  color: #666;
  margin-right: 15px;
}

.seats-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  flex: 1;
}

.seat {
  display: inline-block;
  width: 30px;
  height: 26px;
  margin: 0 5px;
  background-position: 0 1px;
  transition: transform 0.2s;
}

.seat:hover {
  transform: scale(1.1);
}

.seat-default {
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAHKSURBVEhLxZfbysIwDMezKeJUVAR9A/H9X0FQZMOLeem14sWc4AnP9muybM4557eT/mCYdLT/tknaqQgJ/ACVf7/Oz4TfbvVisYDlcgm1Wg2SRON0OoGqqtDr9bglAAoHGY1GQgqzl5zz+SyGwyF7z7wITyYTtrJD13W2HrzEuFqtspUdnU4Hbrcbew5fSa5SqfQbYbmzoCgKew5fK6disciWgyd8vV5hPB5DoVAA27bpSct6vQbLsmjFhmFwK0MpJjFNky2H3W4nZEf24rPZbMTlcmFPiPv9LgaDAXu+rNY0jS0HzG5Zh+zFRwo9bS/GuNvtgpwM+SQsVwetVosa/GDnpMhFsfUAJ3I4HMgmYRQIZl1awsbzTyYyqzHRkhK2Yj+RwklwBYMHRpDMhd0tTrXiT52jwCsxCrqPZc3RARLM7O1266V/XMrlMlQqFfYcVqsVZXa9Xo8Wzhq/cOYx/i+e8KeYZIFfgyyMxWw2o4Y8mc/nXty9jz08NqfTKTSbTapBLAt8kh6buDocGh+3xNrtNjQaDbLxRSj7/f7pNolLv98Xx+ORvVfeBla+S3VJ4K7hGO/I7S8MDht2UbjklspRokj+NRQKwB/pWISi3oSUQQAAAABJRU5ErkJggg==") no-repeat;
}

.seat-choose{
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAKVSURBVEhLxZXPThpRFMY/hhFBG4okbdiYxgXpxmJi0phi4sKlL+HWlSsfwMSFr+DKjSufwMSFiyakaaagQaOxW1txUUjDHwVh5vacmdtxgGEsA1N/yYHLvdzzzT33nDMhQeAFUOT3f+fFhAeGutRs4tfpKV5dXECoqpz9R8hlKxqFsrqK96mUnOzGVfhrsYh3+/tIXV8Dk5Nydkh0HW3a+21zE59WVuTkE33C5/k8PmxvW4ITE/SPkFzxQacDUOS0nR18XFiQkxZ9dzx9dARwaCOR0UQZ9kP29vAQupz6S39y8UmVMeZcOIwInfp54QAQdJDe2LkLt9tAq2Vmp294L/vhe6Zxb13YwrSMvKZRZMIoLy2hvLYG3N/7E+c9FN7f2SzK8/MQiQS0szO5aGFndfHkBJndXSAeNzc2ZmehUw3GCwUru4eBTlpbXETs6grqwwNgGBBkX7a2kF1eNv9inziWy1minFxU/NM3N2jHYr5PbJAPtdGwHpp8hiiS6YMDUPBNTOE6WfL21sxAGx7Xav5KivaIx0fy7kghGqv0QHR+6yd/GGQUcx52M0Idu+0UDn+ORxovLp24i8CEO9SrvQhM+LlL8hQWnJHcBHxYiKvDA7OOqzTobGwgWalYjZ2hjlObm0N7Zmb4kqIkipZKmLq7e6oU8ldJJqHu7YGK1kOYke3OF1xKzvLsEfa+Y34IDrcfc4q6YAsrfDcBozgy3RSeIvuRyVidinsrNfixW7WKn+m0qcXYLwlum9+Pj5G4vITO709KEDaDmrsfFPLBrtnMrkhvqDfr63gt13nBlYZhiFyhIH8Nz2dNE005dmNgcgl6Fxv8dvGJXq9DcIgHYId63LBbvqpBeJfTCHiJMoEJewP8Afy6sw903o8jAAAAAElFTkSuQmCC") no-repeat;
}

.seat-sold{
  background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAANwSURBVEhLxZZLTBNRFIb/mWlpjbWAYgISNBofGyMGwQdEg0sTdaciaNy40UTxmeiKuDPGxwZXsjAuDNHohmhcYARjjGJBoYSFhAUYA+EpSksfQ8dz7gwwbacj5SEfXMq9c+757z3n3DuVNALLgGx8/neWTThlqIMjgL+3Gd3B11AklzE6NzTEkKFloTT/LNZtzDJG47EUbm/twouhk+hTOpAhGYNpMkXNSX+qPO+wv6xcHzSRJNzW/hm1A3vhcgD0C2mewswUeQ6T+IUcPwqLthujOkk5bh68AycpOklwIaKMQvMdCvBqsNoYmSVJOENeiQXqxcECEYzrHRP/paplKVnGUlil3ERiVJ2W9Z4atg9Gab4xN0qfXGRWB2dWmJ598bVAURzY4T6CA9mXEUpDnO1GQ8D5nfXYlFmMICkWeSux2XEQ3ow8fPX5DUudmapuamlA3dhReKiweCTXsRVrXdvQFWyAYw5JH54ErhY/pfqQ8aCtAnuyD6Mn0IgQ/bBAjBZyxvMBZaVlwn5mx22jT4Qon1sXjQ6o3+GWssWkKE0KUAitds9jLHqtuF707/oq4HGyDy8mtZA4HexToepuGD8tbBhdOAIMSa1xCeejMBEZFvkqyTuGm7ubMEKhNIvz/xxe3qlG2bzvq8Qat34MI1NBKirDkBC+ZRW0FoGuxbmkiSY7gUTV6CSL9z+e4+dEB2r2NYrdTYvzQq4Y4b3nq8JqQ5SRLC8Baea5LmxlY8JLV/Uj/0X0Bzpxq6xRCCaGN2fFrChjVclmzNG1hXdT57+E/okuXC95hnOFtXHhTURVqShssBVOXDWL885/h/vhdmSRaHx44/hHFG2FnbJbXCbmxmF/3FWNh+2nkEmifEEk2nBzKR5xIlKhn+MwcKNpA/44+kQ1M/xmyXfuglcpIAdUfWkgQ8Gg2olhtTvO3yp1PW6X99KqbIQZNrZbtR3sJtGXWdg21DyRb635NLOoFUKYa0ilq22piSE6cwcIYYmKZAuOI0CpDHOjh4vdJshvQeyQ0BKaIseERnl+2/oSPeE3kDWnuHm4xWLpFdY0siyL4yga7dXrysWJohpIdNEIWNiKqZCmtX7yG730afn4TdOiRseClMU1qQbopf7L6KVPIDyGkJq6bpK+ZS4W7Nb6RaFje5wWgp0os2TC9gB/AXsuAMA34noyAAAAAElFTkSuQmCC") no-repeat;
}

/* 响应式样式 */
@media (max-width: 992px) {
  .search-button-container {
    justify-content: flex-start;
    margin-top: 10px;
  }

  .button-group {
    justify-content: center;
  }

  .operation-btn {
    margin-bottom: 5px !important;
  }
}

@media (max-width: 768px) {
  .session-management-container {
    padding: 10px;
  }

  .page-title h1 {
    font-size: 1.5rem;
  }

  .pagination-container {
    justify-content: center;
  }
}
</style>
