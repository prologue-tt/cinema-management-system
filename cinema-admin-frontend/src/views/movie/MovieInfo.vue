<template>
  <div class="movie-management-container">
    <!-- 顶部导航区 -->
    <header class="header">
      <div class="header-content">
        <h1 class="page-title">🎬 电影信息管理</h1>
        <div class="header-decoration">
          <span class="deco-item">🍿</span>
          <span class="deco-item">🎭</span>
          <span class="deco-item">🎟️</span>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon">🎬</div>
          <div class="stat-info">
            <div class="stat-value">{{ totalMovies }}</div>
            <div class="stat-label">总电影数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🌍</div>
          <div class="stat-info">
            <div class="stat-value">{{ movieAreaList.length }}</div>
            <div class="stat-label">地区数量</div>
          </div>
        </div>
      </div>

      <!-- 搜索和操作区 -->
      <div class="control-panel">
        <!-- 搜索区域 -->
        <div class="search-panel">
          <el-card class="search-card">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-input
                  v-model="inputMovieName"
                  placeholder="🔍 请输入电影名称"
                  clearable
                  class="search-input">
                </el-input>
              </el-col>
              <el-col :span="8">
                <el-select
                  v-model="selectedMovieArea"
                  placeholder="🌍 请选择电影地区"
                  clearable
                  class="search-select">
                  <el-option
                    v-for="item in movieAreaList"
                    :key="item"
                    :label="item"
                    :value="item">
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="8">
                <el-button
                  class="search-button"
                  icon="el-icon-search"
                  @click="getMovieList">
                  搜索电影
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </div>

        <!-- 操作按钮区域 -->
        <div class="action-buttons">
          <el-button
            type="primary"
            @click="addDialogVisible = true"
            class="add-button dopamine-pink">
            <i class="el-icon-plus"></i> 添加电影
          </el-button>
          <el-button
            type="danger"
            @click="isAbleMultipleDelete"
            class="delete-button dopamine-red">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </div>
      </div>

      <!-- 电影信息卡片列表 -->
      <el-card class="movie-cards-container">
        <div class="movie-grid">
          <!-- 电影卡片 -->
          <div
            v-for="movie in movieList"
            :key="movie.movieId"
            class="movie-card"
            :class="{ 'movie-card-selected': isSelected(movie.movieId) }"
          >
            <!-- 选择框 -->
            <div class="movie-selector">
              <el-checkbox
                v-model="selectedMovies"
                :label="movie.movieId"
                @change="handleSelectionChange"
              ></el-checkbox>
            </div>

            <!-- 电影海报 -->
            <div class="movie-poster">
              <img
                :src="movie.moviePoster ? (global.base + JSON.parse(movie.moviePoster)[0]) : 'https://picsum.photos/300/450?grayscale'"
                :alt="movie.movieName"
                class="poster-image"
              >
            </div>

            <!-- 电影信息 -->
            <div class="movie-info">
              <h3 class="movie-title">{{ movie.movieName }}</h3>

              <div class="movie-details">
                <div class="detail-item">
                  <span class="detail-label">地区：</span>
                  <span class="detail-value">{{ movie.movieArea }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">时长：</span>
                  <span class="detail-value">{{ movie.movieLength }} 分钟</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">上映：</span>
                  <span class="detail-value">{{ formatDate(movie.releaseDate) }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">票房：</span>
                  <span class="detail-value">{{ movie.movieBoxOffice || '暂无数据' }}</span>
                </div>

                <!-- 电影类别标签 -->
                <div class="movie-categories">
                  <el-tag
                    v-for="category in movie.movieCategoryList"
                    :key="category.movieCategoryId"
                    class="category-tag"
                  >
                    {{ category.movieCategoryName }}
                  </el-tag>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="movie-actions">
                <el-tooltip effect="light" content="修改" placement="top">
                  <el-button
                    icon="el-icon-edit"
                    @click="isAbleEdit(movie.movieId)"
                    size="mini"
                    class="action-btn dopamine-blue">
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="light" content="删除" placement="top">
                  <el-button
                    icon="el-icon-delete"
                    @click="isAbleDelete(movie.movieId)"
                    size="mini"
                    class="action-btn dopamine-red">
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="light" content="类别" placement="top">
                  <el-button
                    icon="el-icon-edit"
                    @click="showEditCategoryDialog(movie.movieId)"
                    size="mini"
                    class="action-btn dopamine-green">
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="light" content="评价" placement="top">
                  <el-button
                    icon="el-icon-chat-round"
                    @click="showMovieReviews(movie.movieId)"
                    size="mini"
                    class="action-btn dopamine-yellow">
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="movieList.length === 0" class="empty-state">
            <div class="empty-icon">🎬</div>
            <p class="empty-text">没有找到相关电影</p>
            <el-button
              type="primary"
              @click="resetSearch()"
              class="empty-action dopamine-purple">
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
            :page-sizes="[8, 12, 16]"
            :page-size="queryInfo.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            class="pagination">
          </el-pagination>
        </div>
      </el-card>
    </main>

    <!-- 添加电影对话框 -->
    <el-dialog
      title="添加电影"
      :visible.sync="addDialogVisible"
      width="70%"
      @close="addDialogClosed"
      custom-class="custom-dialog">
      <!-- 内容主题区 -->
      <el-form
        :model="addForm"
        :rules="addFormRules"
        ref="addFormRef"
        label-width="100px"
        class="form-container">
        <el-form-item label="电影名称" prop="movieName">
          <el-input v-model="addForm.movieName" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="电影区域" prop="movieArea">
          <el-select
            v-model="addForm.movieArea"
            placeholder="请选择电影区域"
            clearable
            class="form-select">
            <el-option
              v-for="item in movieAreaList"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="电影时长/分钟" prop="movieLength">
          <el-input v-model="addForm.movieLength" class="form-input"></el-input>
        </el-form-item>
        <el-form-item label="上映时间" prop="releaseDate">
          <el-date-picker
            v-model="addForm.releaseDate"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期"
            class="form-datepicker">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="电影简介" prop="movieIntroduction">
          <el-input
            v-model="addForm.movieIntroduction"
            type="textarea"
            rows="4"
            class="form-input">
          </el-input>
        </el-form-item>

        <!-- 电影封面上传 -->
        <el-form-item label="电影封面">
          <el-upload
            action=""
            list-type="picture-card"
            :auto-upload="false"
            :limit="1"
            :file-list="poster"
            :on-exceed="handleExceed"
            :on-change="handleChangeP"
            :on-success="handleSuccessP"
            :on-error="handleError"
            ref="posterRef"
            :http-request="submitFileP"
            class="uploader">
            <i slot="default" class="el-icon-plus"></i>
            <div slot="file" slot-scope="{file}">
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreviewP(file)">
                  <i class="el-icon-zoom-in"></i>
                </span>
                <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemoveP(file)">
                  <i class="el-icon-delete"></i>
                </span>
              </span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false" class="dialog-button cancel-button dopamine-gray">取 消</el-button>
        <el-button type="primary" @click="addMovie" class="dialog-button confirm-button dopamine-green">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 修改电影信息对话框 -->
    <el-dialog
      :title="isReadOnly ? '查看电影信息' : '修改电影信息'"
      :visible.sync="editDialogVisible"
      width="70%"
      @close="editDialogClosed"
      custom-class="custom-dialog">
      <el-form
        :model="editForm"
        :rules="editFormRules"
        ref="editFormRef"
        label-width="100px"
        class="form-container">
        <el-form-item label="电影名称" prop="movieName">
          <el-input v-model="editForm.movieName" :disabled="true" class="form-input"></el-input>
        </el-form-item>

        <el-form-item label="电影区域" prop="movieArea">
          <el-select
            v-model="editForm.movieArea"
            placeholder="请选择电影区域"
            clearable
            class="form-select"
            :disabled="isReadOnly">
            <el-option
              v-for="item in movieAreaList"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="电影时长/分钟" prop="movieLength">
          <el-input v-model="editForm.movieLength" class="form-input" :disabled="isReadOnly"></el-input>
        </el-form-item>
        <el-form-item label="上映时间" prop="releaseDate">
          <el-date-picker
            v-model="editForm.releaseDate"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期"
            class="form-datepicker"
            :disabled="isReadOnly">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="电影简介" prop="movieIntroduction">
          <el-input
            v-model="editForm.movieIntroduction"
            type="textarea"
            rows="4"
            class="form-input"
            :disabled="isReadOnly">
          </el-input>
        </el-form-item>

        <!-- 修改封面 -->
        <el-form-item label="电影封面" v-if="!isReadOnly">
          <el-upload
            action=""
            list-type="picture-card"
            :auto-upload="false"
            :limit="1"
            :file-list="poster"
            :on-exceed="handleExceed"
            :on-change="handleChangeP"
            :on-success="handleSuccessP"
            :on-error="handleError"
            ref="posterEditRef"
            :http-request="submitFileP"
            class="uploader">
            <i slot="default" class="el-icon-plus"></i>
            <div slot="file" slot-scope="{file}">
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreviewP(file)">
                  <i class="el-icon-zoom-in"></i>
                </span>
                <span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemoveP(file)">
                  <i class="el-icon-delete"></i>
                </span>
              </span>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 只读状态下的封面显示 -->
        <el-form-item label="电影封面" v-if="isReadOnly">
          <img
            :src="poster[0]?.url || 'https://picsum.photos/300/450?grayscale'"
            alt="电影封面"
            style="max-width: 200px; max-height: 300px; border-radius: 8px;"
          >
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false" class="dialog-button cancel-button dopamine-gray">关 闭</el-button>
        <el-button
          type="primary"
          @click="editMovieInfo"
          class="dialog-button confirm-button dopamine-blue"
          v-if="!isReadOnly">
          确 定
        </el-button>
      </span>
    </el-dialog>

    <!-- 电影类型管理界面 -->
    <el-dialog
      title="电影类型管理"
      :visible.sync="editCategoryVisible"
      width="60%"
      @close="editCategoryDialogClosed"
      custom-class="category-dialog">
      <div class="category-management-container">
        <div class="category-info">
          <h3>当前电影类型</h3>
          <p class="category-description">添加或移除电影所属类型，帮助用户更好地分类和发现电影</p>
        </div>

        <div class="category-input-section">
          <el-select
            v-model="selectedMovieCategory"
            placeholder="请选择类型名称"
            clearable
            class="category-selector">
            <el-option
              v-for="item in categoryList"
              :key="item.movieCategoryId"
              :label="item.movieCategoryName"
              :value="item.movieCategoryId"
              v-if="ifShow(item.movieCategoryId)">
            </el-option>
          </el-select>
          <el-button
            type="primary"
            @click="addCategory()"
            class="add-category-btn dopamine-purple"
            :disabled="!selectedMovieCategory">
            <i class="el-icon-plus"></i> 添加类型
          </el-button>
        </div>

        <div class="current-categories">
          <h4>已选类型</h4>
          <div class="category-tags-container">
            <el-tag
              v-for="tag in editCategoryForm"
              :key="tag.movieCategoryName"
              closable
              @close="deleteCategory(tag.movieCategoryId)"
              class="custom-category-tag">
              {{tag.movieCategoryName}}
            </el-tag>
            <div v-if="editCategoryForm.length === 0" class="no-categories">
              <p>暂无已选类型，请添加</p>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 电影评价管理对话框 -->
    <el-dialog
      title="电影评价管理"
      :visible.sync="reviewDialogVisible"
      width="80%"
      @close="reviewDialogClosed"
      custom-class="custom-dialog">
      <el-table
        :data="movieReviews"
        style="width: 100%"
        border
        stripe
        class="review-table">
        <el-table-column prop="commentId" label="评价ID" min-width="80"></el-table-column>
        <el-table-column prop="user.userId" label="用户ID" min-width="80"></el-table-column>
        <el-table-column prop="user.userName" label="用户名称" min-width="120"></el-table-column>
        <el-table-column prop="star" label="评分" min-width="130">
          <template slot-scope="scope">
            <el-rate
              :value="scope.row.star"
              disabled
              :max="10"
              :show-score="true"
              score-template="{value}分">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="commentContent" label="评价内容" min-width="200"></el-table-column>
        <el-table-column label="评价时间" min-width="160">
          <template slot-scope="scope">
            {{ formatReviewDate(scope.row.commentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120">
          <template slot-scope="scope">
            <el-button
              icon="el-icon-delete"
              size="mini"
              @click="deleteReview(scope.row.commentId)"
              class="delete-review-button dopamine-red">
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="review-pagination-container">
        <el-pagination
          @size-change="handleReviewSizeChange"
          @current-change="handleReviewCurrentChange"
          :current-page="reviewQueryInfo.pageNum"
          :page-sizes="[ 100]"
          :page-size="reviewQueryInfo.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="reviewTotal"
          class="pagination">
        </el-pagination>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false" class="dialog-button cancel-button dopamine-gray">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible" append-to-body class="image-preview-dialog">
      <img width="100%" :src="dialogImageUrl" alt="图片预览">
    </el-dialog>

    <!-- 自定义删除异常通知组件 -->
    <el-dialog
      title="操作提示"
      :visible.sync="showDeleteErrorDialog"
      width="500px"
      custom-class="error-dialog"
      :show-close="false">
      <div class="error-dialog-content">
        <div class="error-icon">⚠️</div>
        <div class="error-message">
          <h3>无法执行删除操作</h3>
          <p>抱歉！有未完成电影场次，不能删除电影信息。</p>
          <p v-if="errorMovieIds" class="error-ids">
            受影响的电影编号：<span>{{ errorMovieIds }}</span>
          </p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="showDeleteErrorDialog = false"
          class="confirm-button dopamine-red">
          我知道了
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "MovieInfo",
  data() {
    return {
      // 新增：控制是否为只读状态
      isReadOnly: false,

      // 统计数据
      totalMovies: 0,
      totalReviews: 0,

      queryInfo: {
        movieId: '',
        movieArea: '',
        movieName: '',
        startDate: '',
        endDate: '',
        pageNum: 1,
        pageSize: 8
      },
      // 评价管理相关数据
      reviewQueryInfo: {
        movieId: '',
        pageNum: 1,
        pageSize: 10
      },
      reviewTotal: 0,
      reviewDialogVisible: false,
      movieReviews: [],  // 存储电影评价列表数据
      total: 0,
      url: 'http://localhost:8585/',
      // 控制对话框的显示与隐藏
      addDialogVisible: false,
      selectedMovieCategory:[],
      selectedMovieArea: '',
      selectedDate: [],
      inputMovieName: '',
      categoryList:[],
      movieList: [],
      movieAreaList: ["中国大陆", "美国", "韩国", "日本", "中国香港", "中国台湾", "泰国", "印度",
        "法国", "英国", "俄罗斯", "意大利", "西班牙", "德国", "波兰", "澳大利亚", "伊朗", "其他"],
      editForm: {},
      checkAbleId: {},
      editCategoryForm:[],
      movieId:'',
      editDialogVisible: false,
      editCategoryVisible: false,
      // 新增：删除异常对话框控制
      showDeleteErrorDialog: false,
      errorMovieIds: '',
      multipleSelection: [],
      dialogVisible: false,
      dialogImageUrl: '',
      disabled:false,
      // 电影封面增删变量
      poster: [],
      // 发送给后端的JSON图片
      posterL: [],
      deletePostList:[],
      // 添加删除图片集 动态绑定图片列表
      pics: [],
      // 发送给后端的JSON图片数组
      pictureList: [],
      picNums:0,
      deletePicList:[],
      selectedMovies: [],
      // 添加电影表单数据
      addForm: {
        movieId: '',
        movieName: '',
        movieArea: '',
        movieLength: 0,
        releaseDate: '',
        movieBoxOffice: '',
        movieIntroduction: '',
        moviePictures: '',
        moviePoster:''
      },
      // 添加验证表单规则对象
      addFormRules: {
        movieName: [
          {required: true, message: '请输入电影名称', trigger: 'blur'}
        ],
        movieArea: [
          {required: true, message: '请选择电影区域', trigger: 'blur'}
        ]
      },
      // 修改验证表单规则对象
      editFormRules: {
        movieName: [
          {required: true, message: '请输入电影名称', trigger: 'blur'}
        ],
        movieArea: [
          {required: true, message: '请选择电影区域', trigger: 'blur'}
        ]
      }
    }
  },
  created() {
    this.getMovieList()
    this.getCategoryList()
  },
  methods: {
    // 判断电影是否被选中
    isSelected(movieId) {
      return this.selectedMovies.includes(movieId);
    },

    // 格式化日期显示
    formatDate(dateString) {
      if (!dateString) return '未知';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },

    // 重置搜索条件
    resetSearch() {
      this.inputMovieName = '';
      this.selectedMovieArea = '';
      this.selectedDate = [];
      this.queryInfo.pageNum = 1;
      this.getMovieList();
    },
    getMovieList() {
      this.queryInfo.movieName = this.inputMovieName
      this.queryInfo.movieArea = this.selectedMovieArea
      if (this.selectedDate && this.selectedDate.length > 0) {
        this.queryInfo.startDate = this.selectedDate[0]
        this.queryInfo.endDate = this.selectedDate[1]
      } else {
        this.queryInfo.startDate = ''
        this.queryInfo.endDate = ''
      }
      const _this = this;
      axios.get('sysMovie/find', {params: _this.queryInfo}).then(resp => {
        console.log(resp)
        _this.movieList = resp.data.data;
        _this.total = resp.data.total;
        _this.totalMovies = resp.data.total; // 更新总电影数统计
        _this.queryInfo.pageSize = resp.data.pageSize;
        _this.queryInfo.pageNum = resp.data.pageNum;
      })
    },
    getCategoryList() {
      const _this = this
      axios.get('sysMovieCategory/find').then(resp=>{
        _this.categoryList = resp.data.data;
        console.log(_this.categoryList)
      })
    },
    // 获取电影评价列表
    async getMovieReviews() {
      try {
        const { data: res } = await axios.get(`/comments/${this.movieId}`,{
          params: {
            pageNum: this.reviewQueryInfo.pageNum,
            pageSize: this.reviewQueryInfo.pageSize}});
        if (res.code === 200) {
          this.movieReviews = res.data;
          this.reviewTotal = res.total;
          console.log('获取到的评论数据:', this.comments);
        } else {
          this.$message.error('获取评论失败');
        }
      } catch (error) {
        console.error('获取评论出错:', error);
        this.$message.error('获取评论时发生错误');
      }
    },
    // 显示电影评价管理对话框
    showMovieReviews(movieId) {
      this.reviewQueryInfo.movieId = movieId;
      this.movieId = movieId;
      this.reviewQueryInfo.pageNum = 1; // 重置页码
      this.getMovieReviews();
      this.reviewDialogVisible = true;
    },
    // 删除电影评价
    async deleteReview(commentId) {
      const _this = this;
      // 询问用户是否确认删除
      const resp = await this.$confirm('是否确认删除该评价, 是否继续?', '确认操作', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      // 用户确认删除
      if (resp === 'cancel') {
        return _this.$message.info('已取消删除')
      }

      try {
        const response = await axios.delete('commentDelete/' + commentId)
        if (response.data.code === 200) {
          this.$message.success('评价删除成功！');
          this.getMovieReviews(); // 重新加载评价列表
        } else {
          this.$message.error('评价删除失败！');
        }
      } catch (error) {
        console.error('删除评价失败:', error);
        this.$message.error('删除评价失败，请重试');
      }
    },
    // 评价分页相关方法
    handleReviewSizeChange(newSize) {
      this.reviewQueryInfo.pageSize = newSize;
      this.getMovieReviews();
    },
    handleReviewCurrentChange(newPage) {
      this.reviewQueryInfo.pageNum = newPage;
      this.getMovieReviews();
    },
    // 评价对话框关闭事件
    reviewDialogClosed() {
      // 清空评价数据
      this.movieReviews = [];
      this.reviewTotal = 0;
    },

    ifShow(id) {
      let l = true
      for (let i = 0; i < this.editCategoryForm.length; i++) {
        if (id === this.editCategoryForm[i].movieCategoryId) {
          l =false
        }
      }
      return l
    },
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getMovieList()
      console.log(newSize)
    },
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getMovieList()
      console.log(newPage)
    },
    async submitFile() {
      const _this = this
      for (let i = 0; i < this.pics.length; i++) {
        let formData = new FormData()
        if (this.pics[i].status === 'success') {
          let s = this.pics[i].url
          this.pictureList.push(s.substring(s.indexOf('/images')))
          continue
        }
        let file = this.pics[i].raw
        formData.append('file', file)
        await axios.post('upload/movie', formData).then(response => {
          _this.pictureList.push(response.data.data)
        })
      }
    },
    async submitFileP() {
      const _this = this
      for (let i = 0; i < this.poster.length; i++) {
        let formData = new FormData()
        if (this.poster[i].status === 'success') {
          let s = this.poster[i].url
          this.posterL.push(s.substring(s.indexOf('/images')))
          continue
        }
        let file = this.poster[i].raw
        formData.append('file', file)
        await axios.post('upload/movie', formData).then(response => {
          _this.posterL.push(response.data.data)
        })
      }
    },
    // 监听添加对话框的关闭事件
    addDialogClosed() {
      this.$refs.addFormRef.resetFields()
      this.$refs.pictureRef.clearFiles()
      this.$refs.posterRef.clearFiles()
      this.poster = []
      this.posterL=[]
      this.pics = []
      this.pictureList = []
    },
    // 监听添加按钮
    async addMovie() {
      await this.submitFile()
      await this.submitFileP()
      this.addForm.moviePictures = JSON.stringify(this.pictureList)
      this.addForm.moviePoster = JSON.stringify(this.posterL)
      const _this = this
      this.$refs.addFormRef.validate(async valid => {
        console.log(valid)
        if (!valid) return
        // 预校验成功，发网络请求
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        await axios.post('sysMovie', JSON.stringify(_this.addForm)).then(resp => {
          console.log(resp)
          if (resp.data.code !== 200) {
            this.$message.error('添加电影信息失败！')
          }
        })
        // 隐藏添加对话框
        this.addDialogVisible = false
        // 重新加载列表
        this.getMovieList()
        this.$message.success('添加电影信息成功！')
      })
    },
    // 监听修改对话框的关闭事件
    editDialogClosed() {
      this.$refs.editFormRef.resetFields()
      this.$refs.pictureEditRef.clearFiles()
      this.$refs.posterEditRef.clearFiles()
      this.pics = []
      this.pictureList = []
      this.posterL = []
      this.poster = []
      this.isReadOnly = false; // 重置只读状态
    },
    // 监听修改类别对话框的关闭事件
    editCategoryDialogClosed(){
      this.selectedMovieCategory = ''
    },
    // 修改电影信息对话框
    async editMovieInfo() {
      await this.submitFile()
      this.editForm.moviePictures = JSON.stringify(this.pictureList)
      await this.submitFileP()
      this.editForm.moviePoster = JSON.stringify(this.posterL)
      this.$refs.editFormRef.validate(async valid => {
        const _this = this
        if (!valid) return
        let success = true
        axios.defaults.headers.put['Content-Type'] = 'application/json'
        await axios.put('sysMovie', JSON.stringify(_this.editForm)).then(resp => {
          if (resp.data.code !== 200) {
            this.$message.error('修改电影信息失败！')
            success = false
          }
        })
        if (!success) return
        for(let s of this.deletePicList){
          await axios.get('/upload/delete?filePath=' + s.substring(s.indexOf('/images')))
        }
        for(let s of this.deletePostList){
          await axios.get('/upload/delete?filePath=' + s.substring(s.indexOf('/images')))
        }
        this.editDialogVisible = false
        this.getMovieList()
        this.$message.success('修改电影信息成功！')
      })
    },
    handleChange(file, filelist) {
      this.pics = filelist.slice(0)
      console.log(this.pics)
    },
    handleChangeP(file, filelist) {
      this.poster = filelist
      console.log(this.poster)
    },
    handleSuccess(response) {
      this.pictureList.push(response.data)
      this.addForm = JSON.stringify(this.pictureList)
      this.editForm = JSON.stringify(this.pictureList)
    },
    handleSuccessP(response) {
      this.posterL.push(response.data)
      this.addForm = JSON.stringify(this.posterL)
      this.editForm = JSON.stringify(this.posterL)
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handlePictureCardPreviewP(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleRemove(file,filelist) {
      const filePath = file.url
      console.log(filePath)
      const idx = this.pics.findIndex(x => x.url === filePath)
      if(file.status === 'success'){
        this.deletePicList.push(file.url)
      }
      this.pics.splice(idx, 1)
    },
    handleRemoveP(file) {
      const filePath = file.url
      console.log(filePath)
      const idx = this.poster.findIndex(x=> x.url === filePath)
      if(file.status === 'success'){
        this.deletePostList.push(file.url)
      }
      this.poster.splice(idx,1)
    },
    handleError(err) {
      console.log(err)
    },
    toMovieInfo(id) {
      window.open('http://localhost:8587/movieInfo/' + id)
    },
    isAbleEdit(id) {
      this.checkAbleId.movieId = id
      axios.get('sysSession/isAbleEdit', {params: this.checkAbleId}).then(response => {
        console.log(response.data.total)
        let sessions = response.data.data
        // 无论是否可编辑，都显示电影信息
        this.showEditDialog(id, response.data.total > 0)

        // 如果有未完成场次，显示提示信息
        if (response.data.total > 0) {
          console.log('有未完成session，设置为只读')
          let sessionIds = ''
          for (let temp of sessions) {
            sessionIds += temp.sessionId + ' '
          }
          this.$message.warning('有未完成电影场次，暂时无法修改电影信息');
        }
      })
    },
    // 显示修改对话框，回显数据，新增isReadOnly参数控制是否只读
    async showEditDialog(id, isReadOnly = false) {
      const _this = this
      this.isReadOnly = isReadOnly; // 设置只读状态

      await axios.get('sysMovie/find/' + id).then(resp => {
        console.log(resp)
        _this.editForm = resp.data.data
      })

      // 加载海报
      this.poster = [];
      if (this.editForm.moviePoster) {
        for (const item of JSON.parse(this.editForm.moviePoster)) {
          let pic = {}
          pic['name'] = ''
          pic['url'] = this.global.base + item
          this.poster.push(pic)
        }
      }

      // 加载图片
      this.pics = [];
      if (this.editForm.moviePictures) {
        for (const item of JSON.parse(this.editForm.moviePictures)) {
          let pic = {}
          pic['name'] = ''
          pic['url'] = this.global.base + item
          this.pics.push(pic)
        }
      }

      this.editDialogVisible = true
    },
    // 显示修改类别对话框,回显数据
    async showEditCategoryDialog(id){
      const _this = this
      _this.movieId = id
      await axios.get('sysMovie/find/'+id).then(response=>{
        _this.editCategoryForm = response.data.data.movieCategoryList
      })
      this.editCategoryVisible = true
    },
    // 取消修改
    cancelEdit(){
      this.editDialogVisible = false
      this.deletePicList.splice(0,this.deletePicList.length)
      this.deletePostList.splice(0,this.deletePostList.length)
    },
    // 监听多选框变化
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    // 批量删除前校验
    async isAbleMultipleDelete() {
      let ableDelete = true
      let ids = ''
      for (let i = 0; i < this.multipleSelection.length; i++) {
        this.checkAbleId.movieId = this.multipleSelection[i].movieId
        const { data : res } = await axios.get('sysSession/isAbleEdit', {params: this.checkAbleId})
        if (res.data.length !== 0) {
          ids += this.multipleSelection[i].movieId + ' '
          ableDelete = false
        }
      }
      if (ableDelete === false) {
        // 使用自定义对话框替代原有alert
        this.errorMovieIds = ids;
        this.showDeleteErrorDialog = true;
        return
      }
      await this.multipleDelete()
    },
    // 批量删除电影
    async multipleDelete() {
      const _this = this
      // 询问用户是否确认删除
      const resp = await this.$confirm('确认要永久删除这些电影吗?', '确认操作', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      // 用户确认删除, resp为字符串"confirm"
      // 用户取消删除，resp为字符串"cancel"
      if (resp === 'cancel') {
        return _this.$message.info('已取消删除')
      }

      let ids = []
      this.multipleSelection.forEach(data => {
        let item = data.movieId
        ids.push(item)
      })
      console.log(ids)
      axios.defaults.headers.post['Content-Type'] = 'application/json'
      await axios.delete('sysMovie/' + ids, JSON.stringify(ids)).then(resp => {
        console.log(123)
        if (resp.data.code !== 200) {
          this.$message.success('批量删除电影失败！')
        }
      })
      this.getMovieList()
      this.$message.success('批量删除电影成功！')
    },
    async isAbleDelete(id) {
      this.checkAbleId.movieId = id
      await axios.get('sysSession/isAbleEdit', {params: this.checkAbleId}).then(response => {
        console.log(response.data.total)
        let sessions = response.data.data
        if (response.data.total === 0) {
          this.deleteMovieById(id)
        } else {
          console.log('删除前判断有未完成session')
          let sessionIds = ''
          for (let temp of sessions) {
            console.log(temp)
            sessionIds += temp.sessionId+' '
          }
          console.log('sessionIds is : '+sessionIds)
          // 使用自定义对话框替代原有alert
          this.errorMovieIds = id;
          this.showDeleteErrorDialog = true;
        }
      })
    },
    // 单个删除电影
    async deleteMovieById(id) {
      let deleteInfo = id
      const _this = this
      // 询问用户是否确认删除
      const resp = await this.$confirm('此操作将永久删除该条目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch(err => err)

      // 用户确认删除, resp为字符串"confirm"
      // 用户取消删除，resp为字符串"cancel"
      console.log(resp)
      if (resp === 'cancel') {
        return _this.$message.info('已取消删除')
      }

      console.log(deleteInfo)
      axios.defaults.headers.post['Content-Type'] = 'application/json'
      await axios.delete('sysMovie/' + deleteInfo, JSON.stringify(deleteInfo)).then(resp => {
        if (resp.data.code !== 200) {
          _this.$message.success('删除电影信息失败！')
        }
      })
      this.getMovieList()
      this.$message.success('删除电影信息成功！')
    },

    async deleteCategory(categoryId){
      console.log('类型id')
      console.log(categoryId)
      const _this = this
      await axios.delete('sysMovieToCategory/'+this.movieId+'/'+categoryId).then(resp=>{
        console.log(resp)
        _this.$message.success('删除类型成功')
      })
      await axios.get('sysMovie/find/'+this.movieId).then(response=>{
        _this.editCategoryForm = response.data.data.movieCategoryList
      })
    },
    handleExceed(){
      this.$message.error('电影封面不能超过一张!')
    },
    async addCategory(){
      const _this = this
      await axios.post('sysMovieToCategory/'+this.movieId+'/'+this.selectedMovieCategory).then(resp=>{
        console.log(resp)
        if (resp.data.code !==200) return this.$alert('添加电影类别失败', '添加电影类别异常通知', {
          confirmButtonText: '我知道了'
        })
        this.$message.success("添加电影类别成功")
      })
      axios.get('sysMovie/find/'+this.movieId).then(response=>{
        _this.editCategoryForm = response.data.data.movieCategoryList
      })
    },
    formatReviewDate(dateString) {
      if (!dateString) return '未知';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.movie-management-container {
  padding: 20px;
  background-color: #fff9f9;
  min-height: 100vh;
  font-family: 'Comic Sans MS', 'Marker Felt', sans-serif;
}

/* 多巴胺色系定义 */
.dopamine-pink {
  background-color: #ffd3dd !important;
  border-color: #f4c4cf !important;
  color: white !important;
}

.dopamine-pink:hover {
  background-color: #FF4A72 !important;
  border-color: #FF4A72 !important;
}

.dopamine-blue {
  background-color: #b6cdef !important;
  border-color: #c4d9f8 !important;
  color: white !important;
}

.dopamine-blue:hover {
  background-color: #7796bf !important;
  border-color: #7796bf !important;
}

.dopamine-green {
  background-color: #a8d3a9 !important;
  border-color: #a8d3a9 !important;
  color: white !important;
}

.dopamine-green:hover {
  background-color: #85a886 !important;
  border-color: #85a886 !important;
}

.dopamine-yellow {
  background-color: #FFD166 !important;
  border-color: #FFD166 !important;
  color: #333 !important;
}

.dopamine-yellow:hover {
  background-color: #FFC433 !important;
  border-color: #FFC433 !important;
}

.dopamine-red {
  background-color: #EF476F !important;
  border-color: #EF476F !important;
  color: white !important;
}

.dopamine-red:hover {
  background-color: #DC2F4B !important;
  border-color: #DC2F4B !important;
}

.dopamine-purple {
  background-color: #9B5DE5 !important;
  border-color: #9B5DE5 !important;
  color: white !important;
}

.dopamine-purple:hover {
  background-color: #8A4FFF !important;
  border-color: #8A4FFF !important;
}

.dopamine-gray {
  background-color: #6B7C93 !important;
  border-color: #6B7C93 !important;
  color: white !important;
}

.dopamine-gray:hover {
  background-color: #5A6880 !important;
  border-color: #5A6880 !important;
}

/* 顶部标题 */
.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #ffe6f2 0%, #e6f7ff 100%);
  border-radius: 20px;
  box-shadow: 0 8px 20px rgba(255, 180, 200, 0.15);
}

.header-content {
  position: relative;
}

.page-title {
  color: #ff6b8b;
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  text-shadow: 1px 1px 3px rgba(255, 107, 139, 0.2);
}

.header-decoration {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  gap: 15px;
  font-size: 20px;
}

/* 统计卡片 */
.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.stat-card {
  flex: 1;
  min-width: 200px;
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 6px 15px rgba(220, 231, 255, 0.3);
  display: flex;
  align-items: center;
  gap: 15px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 50px;
  height: 50px;
  background-color: #fff0f3;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #ff6b8b;
}

.stat-info .stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #5a5a5a;
}

.stat-info .stat-label {
  font-size: 14px;
  color: #888;
}

/* 控制面板 */
.control-panel {
  margin-bottom: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 搜索区域 */
.search-panel {
  width: 100%;
}

.search-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 6px 15px rgba(220, 231, 255, 0.3);
  padding: 20px;
  background-color: white;
}

.search-input, .search-select {
  width: 100%;
  border-radius: 30px;
  border-color: #ffd9e3;
  transition: all 0.3s ease;
  height: 45px;
  padding-left: 20px;
}

.search-input:focus, .search-select:focus {
  border-color: #ff99b3;
  box-shadow: 0 0 0 3px rgba(255, 153, 179, 0.2);
}

.search-button {
  width: 100%;
  background-color: #ff99b3;
  border-color: #ff99b3;
  color: white;
  border-radius: 30px;
  height: 45px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.search-button:hover {
  background-color: #ff6b8b;
  border-color: #ff6b8b;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(255, 107, 139, 0.3);
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.add-button, .delete-button {
  border-radius: 30px;
  padding: 8px 25px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.add-button:hover, .delete-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.delete-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 电影卡片容器 */
.movie-cards-container {
  border-radius: 16px;
  border: none;
  box-shadow: 0 6px 15px rgba(220, 231, 255, 0.3);
  padding: 20px;
  margin-bottom: 30px;
  background-color: white;
}

/* 电影卡片网格布局 */
.movie-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

/* 电影卡片样式 */
.movie-card {
  background-color: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  flex-direction: column;
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.12);
}

.movie-card-selected {
  border: 2px solid #ff6b8b;
  box-shadow: 0 8px 20px rgba(255, 107, 139, 0.2);
}

/* 选择框位置 */
.movie-selector {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

/* 电影海报 */
.movie-poster {
  width: 100%;
  height: 220px;
  overflow: hidden;
  position: relative;
}

.poster-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.movie-card:hover .poster-image {
  transform: scale(1.05);
}

/* 电影信息区域 */
.movie-info {
  padding: 18px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.movie-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 电影详情 */
.movie-details {
  margin-bottom: 15px;
  flex: 1;
}

.detail-item {
  display: flex;
  margin-bottom: 6px;
  font-size: 14px;
}

.detail-label {
  color: #888;
  width: 60px;
  flex-shrink: 0;
}

.detail-value {
  color: #555;
  flex-grow: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 电影类别标签 */
.movie-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}

.category-tag {
  background-color: #fff0f3;
  color: #ff6b8b;
  border-color: #ffccd5;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
}

/* 操作按钮区域 */
.movie-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: scale(1.15) rotate(5deg);
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
  border-radius: 30px;
  padding: 8px 25px;
}

/* 分页区域 */
.pagination-container, .review-pagination-container {
  padding: 20px;
  display: flex;
  justify-content: center;
  background-color: white;
  border-top: 1px solid #f0f7ff;
  margin-top: 20px;
}

.pagination {
  margin: 0;
}

.el-pagination__sizes,
.el-pagination__total,
.el-pagination__jump {
  color: #6b7c93;
}

.el-pagination button:disabled {
  background-color: #f0f4f8;
  color: #cbd5e0;
}

.el-pagination .el-pager li {
  border-radius: 50%;
  margin: 0 5px;
  min-width: 30px;
  height: 30px;
  line-height: 30px;
}

.el-pagination .el-pager li.active {
  background-color: #ff99b3;
  color: white;
  border-color: #ff99b3;
}

/* 对话框样式 */
.custom-dialog {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(255, 180, 200, 0.2);
}

.custom-dialog .el-dialog__header {
  background: linear-gradient(135deg, #ffe6f2 0%, #e6f7ff 100%);
  padding: 18px 25px;
  border-bottom: none;
}

.custom-dialog .el-dialog__title {
  color: #ff6b8b;
  font-weight: 600;
  font-size: 18px;
}

.custom-dialog .el-dialog__body {
  padding: 25px 25px;
  max-height: 60vh;
  overflow-y: auto;
}

/* 表单样式 */
.form-container {
  background-color: white;
  padding: 20px;
  border-radius: 16px;
}

.form-input, .form-select, .form-datepicker {
  width: 100%;
  border-radius: 30px;
  border-color: #ffd9e3;
  transition: all 0.3s ease;
  height: 45px;
  padding-left: 20px;
}

.form-input:focus, .form-select:focus, .form-datepicker:focus {
  border-color: #ff99b3;
  box-shadow: 0 0 0 3px rgba(255, 153, 179, 0.2);
}

.el-textarea.form-input {
  border-radius: 16px;
  padding: 15px 20px;
  height: auto;
}

/* 上传组件 */
.uploader {
  margin-top: 10px;
}

.uploader .el-upload--picture-card {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  border: 2px dashed #ffd9e3;
  transition: all 0.3s ease;
}

.uploader .el-upload--picture-card:hover {
  border-color: #ff99b3;
  background-color: #fff0f3;
  transform: scale(1.05);
}

/* 对话框按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 15px 25px;
  background-color: #f9f9f9;
  border-top: 1px solid #eee;
}

.dialog-button {
  padding: 8px 25px;
  border-radius: 30px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.dialog-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 评价表格样式 */
.review-table {
  border-radius: 16px 16px 0 0;
  border-collapse: separate;
  border-spacing: 0;
  margin-bottom: 0;
}

.review-table th {
  background-color: #f0f7ff;
  color: #5a68a5;
  font-weight: 500;
  text-align: center;
  border-bottom: 2px solid #e6f0ff;
}

.review-table td {
  text-align: center;
  vertical-align: middle;
  border-bottom: 1px solid #f0f7ff;
}

.review-table tr:hover {
  background-color: #fff5f8;
}

.delete-review-button {
  border-radius: 50%;
  width: 30px;
  height: 30px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图片预览对话框 */
.image-preview-dialog .el-dialog__body {
  padding: 0;
}

.image-preview-dialog img {
  border-radius: 16px;
}

/* 电影类型管理对话框 */
.category-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.category-dialog .el-dialog__header {
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f7ff 100%);
  padding: 20px 25px;
  border-bottom: 1px solid #e6f0ff;
}

.category-dialog .el-dialog__title {
  color: #3385ff;
  font-size: 20px;
  font-weight: 600;
}

.category-dialog .el-dialog__body {
  padding: 25px;
  background-color: #f9fbff;
}

.category-management-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.category-info {
  text-align: center;
  margin-bottom: 10px;
}

.category-info h3 {
  color: #1f2d3d;
  margin-bottom: 8px;
  font-size: 18px;
}

.category-description {
  color: #8392a5;
  font-size: 14px;
  margin: 0;
}

.category-input-section {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 15px;
}

.category-selector {
  flex: 1;
  height: 45px;
  border-radius: 8px;
  border-color: #c9d8e7;
}

.add-category-btn {
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.add-category-btn:hover {
  transform: translateY(-2px);
}

.add-category-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.current-categories {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.current-categories h4 {
  color: #4e5969;
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
  display: flex;
  align-items: center;
}

.current-categories h4::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 16px;
  background-color: #3385ff;
  margin-right: 8px;
  border-radius: 2px;
}

.category-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  min-height: 40px;
}

.custom-category-tag {
  background-color: #e8f3ff;
  color: #3385ff;
  border-color: #b3d1ff;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.custom-category-tag:hover {
  background-color: #d1e7ff;
  transform: translateY(-2px);
}

.custom-category-tag .el-tag__close {
  color: #3385ff;
  margin-left: 5px;
}

.no-categories {
  width: 100%;
  text-align: center;
  padding: 20px 0;
  color: #8392a5;
  font-size: 14px;
  background-color: #f7fafc;
  border-radius: 8px;
  border: 1px dashed #c9d8e7;
}

/* 错误通知对话框样式 */
.error-dialog {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(220, 53, 69, 0.2);
}

.error-dialog .el-dialog__header {
  background-color: #f8d7da;
  padding: 15px 20px;
  border-bottom: 1px solid #f5c6cb;
}

.error-dialog .el-dialog__title {
  color: #721c24;
  font-size: 18px;
  font-weight: 600;
}

.error-dialog .el-dialog__body {
  padding: 30px 20px;
  text-align: center;
}

.error-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 20px;
  color: #dc3545;
}

.error-message h3 {
  color: #721c24;
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 18px;
}

.error-message p {
  color: #721c24;
  margin: 0 0 10px 0;
  font-size: 14px;
  line-height: 1.6;
}

.error-ids {
  background-color: #f8d7da;
  padding: 10px 15px;
  border-radius: 6px;
  margin-top: 15px;
  display: inline-block;
}

.error-ids span {
  font-weight: 600;
}

.error-dialog .dialog-footer {
  background-color: #fff5f5;
  justify-content: center;
  padding: 15px;
  border-top: 1px solid #f5c6cb;
}

.error-dialog .confirm-button {
  padding: 8px 30px;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .action-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }

  .category-select {
    width: 60%;
  }

  .movie-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .movie-management-container {
    padding: 15px;
  }

  .stats-cards {
    flex-direction: column;
  }

  .search-container {
    padding: 15px;
  }

  .category-select {
    width: 100%;
    margin-bottom: 10px;
  }

  .add-category-button {
    width: 100%;
  }

  .el-col {
    margin-bottom: 15px;
  }

  .el-col:last-child {
    margin-bottom: 0;
  }

  .movie-grid {
    grid-template-columns: 1fr;
  }

  .movie-poster {
    height: 200px;
  }

  .category-input-section {
    flex-direction: column;
  }

  .category-selector {
    width: 100%;
  }

  .add-category-btn {
    width: 100%;
  }
}
</style>
