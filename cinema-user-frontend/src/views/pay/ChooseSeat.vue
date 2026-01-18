<template>
  <div>
    <div class="whole">
      <div class="left">
        <!-- 座位图例 -->
        <div class="seat-legend">
          <div class="legend-item">
            <span class="seat-icon available"></span>
            <span>可选座位</span>
          </div>
          <div class="legend-item">
            <span class="seat-icon sold"></span>
            <span>已售座位</span>
          </div>
          <div class="legend-item">
            <span class="seat-icon selected"></span>
            <span>已选座位</span>
          </div>
        </div>

        <!-- 银幕 -->
        <div class="screen-indicator">银幕中央</div>

        <!-- 座位区域 -->
        <div class="seat-grid">
          <div class="seat-row" v-for="(value, key) in seats" :key="key">
            <div class="seats-in-row">
              <span
                class="seat"
                :class="getSeatClass(seats[key][index])"
                @click="pressSeat(key, index)"
                v-for="(item, index) in value"
                :key="index"
              ></span>
            </div>
          </div>
        </div>
      </div>

      <div class="right">
        <div class="right-content">
          <div class="right-header">
            <div class="poster">
              <img :src="session.movie.moviePoster" alt="电影海报" />
            </div>
            <div class="movie-info">
              <p style="font-size: 20px; font-weight: 700">
                {{ session.movie.movieName }}
              </p>
              <span>类型：{{ session.movie.movieCategoryList.join("/") }}</span>
              <span>时长：{{ session.movie.movieLength }}分钟</span>
            </div>
          </div>
          <div class="right-content">
            <div class="info-item">
              <span>影厅：</span>
              <span>{{ session.hall.hallName }}</span>
            </div>
            <div class="info-item">
              <span>版本：</span>
              <span>{{ session.languageVersion }}</span>
            </div>
            <div class="info-item">
              <span>场次：</span>
              <span>{{ session.sessionDate }} {{ session.playTime }}</span>
            </div>
            <div class="info-item">
              <span>票价：</span>
              <span>￥{{ session.sessionPrice }}/张</span>
            </div>
            <span style="color: #999">座位：</span>
            <div class="seat-chose">
              <span
                class="ticket"
                v-if="pickedSeats.length > 0"
                v-for="item in pickedSeats"
                :key="item"
              >{{ item }}</span>
            </div>
            <div class="info-item" style="align-items: center">
              <span style="color: #333">总价：</span>
              <span style="font-size: 20px; color: #f03d37"
              >￥{{ (session.sessionPrice * pickedSeats.length).toFixed(2) }}</span>
            </div>
            <div style="text-align: center; margin-top: 30px">
              <el-button
                type="danger"
                round
                @click="submitBill"
                style="font-size: 22px"
              >
                <i style="font-size: 22px" class="iconfont icon-r-yes"></i>
                提交订单
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from "moment";
import { Message } from "element-ui";
export default {
  name: "ChooseSeat",
  data() {
    return {
      // 座位状态映射关系
      seatStatus: {
        0: "available",   // 可选座位
        1: "unavailable", // 不可用座位
        2: "selected",    // 已选座位
        3: "sold"         // 已售座位
      },
      seats: {},
      sessionId: this.$route.params.sessionId,
      session: {
        movie: {
          movieCategoryList: [],
        },
        hall: {},
      },
      pickedSeats: [],
      addForm: {
        userId: 0,
        sessionId: 0,
        seats: "",
      },
    };
  },
  created() {
    this.getSession();
  },
  methods: {
    // 获取座位状态对应的CSS类名
    getSeatClass(status) {
      return `seat-${this.seatStatus[status]}`;
    },

    async getSession() {
      const { data: resp } = await axios.get(
        "sysSession/find/" + this.sessionId
      );
      if (resp.code !== 200) return this.$message.error(resp.msg);
      this.session = resp.data;
      this.session.movie.moviePoster =
        this.global.base + JSON.parse(this.session.movie.moviePoster)[0];
      this.seats = JSON.parse(resp.data.sessionSeats);
      this.session.movie.movieCategoryList =
        this.session.movie.movieCategoryList.map((obj) => obj.movieCategoryName);
    },

    pressSeat(key, idx) {
      let seat_str = key + "排" + (idx + 1) + "座";
      // 选座
      if (this.seats[key][idx] === 0) {
        if (this.pickedSeats.length === 20) {
          this.$alert("您最多选择二十个座位", "提示", {
            confirmButtonText: "确定",
            type: "warning",
          }).catch((err) => err);
          return;
        }
        this.$set(this.seats[key], idx, 2);
        this.pickedSeats.push(seat_str);
      } else if (this.seats[key][idx] === 2) {
        // 取消选座
        this.$set(this.seats[key], idx, 0);
        this.pickedSeats.splice(this.pickedSeats.indexOf(seat_str), 1);
      }
    },

    async submitBill() {
      // 获取token，校验登录
      const token = window.sessionStorage.getItem("token");
      if (!token) {
        window.sessionStorage.setItem("sessionId", this.session.sessionId);
        this.$alert("抱歉！提交订单前，请先登录", "提交订单异常通知", {
          confirmButtonText: "我知道了",
          callback: (action) => {
            this.$router.push("/login");
          },
        });
        return;
      }

      // 校验是否选座，未选座则警告
      if (this.pickedSeats.length === 0) {
        this.$alert(
          "抱歉！您暂时未选座，无法提交订单，请选座后提交订单。",
          "提交订单异常通知",
          {
            confirmButtonText: "我知道了",
            callback: (action) => {
              this.$router.push("/chooseSeat/" + this.sessionId);
            },
          }
        );
        return;
      }

      // 获取场次座位信息
      const { data: curSession } = await axios.get("sysSession/find/" + this.sessionId);
      let sessionSeats = JSON.parse(curSession.data.sessionSeats);
      const conflictSeats = [];

      // 检查已选座位是否有冲突
      for (let seat of this.pickedSeats) {
        let row = seat.substring(0, seat.indexOf("排"));
        let col = Number.parseInt(seat.substring(seat.indexOf("排") + 1, seat.length - 1));

        if (sessionSeats[row][col - 1] === 3) {
          // 记录冲突座位
          conflictSeats.push(seat);
        }
      }

      // 如果有冲突座位，处理冲突
      if (conflictSeats.length > 0) {
        // 更新座位状态，只还原冲突的座位
        this.seats = JSON.parse(curSession.data.sessionSeats);

        // 从已选座位中移除冲突座位
        this.pickedSeats = this.pickedSeats.filter(
          seat => !conflictSeats.includes(seat)
        );

        // 显示冲突提示
        this.$alert(
          `您所选的座位${conflictSeats.join('、')}已被人抢先一步😢请重新选择吧~`,
          "哎呀",
          {
            confirmButtonText: "我知道了"
          }
        );
        return;
      }

      // 没有冲突，更新座位状态并提交订单
      for (let seat of this.pickedSeats) {
        let row = seat.substring(0, seat.indexOf("排"));
        let col = Number.parseInt(seat.substring(seat.indexOf("排") + 1, seat.length - 1));
        sessionSeats[row][col - 1] = 3;
      }

      this.addForm.userId = JSON.parse(window.sessionStorage.getItem("loginUser")).userId;
      this.addForm.sessionId = this.sessionId;
      this.addForm.seats = JSON.stringify(this.pickedSeats);

      axios.defaults.headers.post["Content-Type"] = "application/json";
      const { data: res } = await axios.post(
        "sysBill",
        JSON.stringify({
          ticket: this.addForm,
          sessionSeats: JSON.stringify(sessionSeats),
        })
      );

      if (res.code !== 200) return this.$message.error("添加订单失败！");
      await this.$router.push("/billDetail/" + res.data.billId);
    },
  },
};
</script>

<style scoped>
/* 样式保持不变 */
.whole {
  width: 1200px;
  margin: 0 auto;
  border: 1px solid #e5e5e5;
  display: flex;
  background-color: #fff5f7;
}

.left {
  width: 830px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-right: 1px solid #e5e5e5;
  padding: 20px;
}

.right {
  width: 340px;
  background-color: #e8f3fd;
  padding: 20px;
}

/* 座位图例样式 */
.seat-legend {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
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
  background-color: #e0f0fb;
}

.seat-icon.sold {
  background-color: #ffccd5;
}

.seat-icon.selected {
  background-color: rgba(118, 197, 80, 0.85);
}

/* 银幕样式 */
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

/* 座位网格样式 */
.seat-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
  overflow-x: auto;
  padding: 10px;
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

/* 座位样式 */
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

.seat-available {
  background-color: #e0f0fb;
}

.seat-unavailable {
  background-color: #ffccd5;
}

.seat-selected {
  background-color:  rgba(118, 197, 80, 0.85);
}

.seat-sold {
  background-color: #ffccd5;
}

/* 右侧信息区域样式 */
.poster {
  width: 115px;
  height: 158px;
}

.poster > img {
  width: 100%;
  height: 100%;
}

.right-header {
  display: flex;
}

.movie-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-left: 20px;
}

.info-item {
  display: flex;
  margin: 15px 0;
  font-size: 14px;
}

.info-item :first-child {
  color: #999999;
}

.seat-chose {
  margin-top: 20px;
  margin-left: 42px;
  margin-bottom: 20px;
  position: relative;
  top: -5px;
}

.ticket {
  cursor: default;
  position: relative;
  font-size: 13px;
  color: #f03d37;
  display: inline-block;
  width: 60px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  margin: 0 12px 10px 0;
  background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAAeCAYAAABwmH1PAAAAAXNSR0IArs4c6QAAAXlJREFUWAlj/Oho7cXAzLTg/89fogzDGDCys71m+PsvgeGji+2r38eP/h/u4Pexo/9BfmUCxSyLhdUwjluI11gsrRhAfmUa9j5F8+Coh9ECZNhxR2N42EUpmodGZgx/TYz9+nvzhn9ogTEsuUysDGxS/548jP4+qe/0l6zUL//fvh2WHoV5ihHGALa0GD872rQxamrm8EybzQMTH070RzsLBngeZmRk/M+7/0jVv9s3rw7n5A33MCgmQZ4GRnn7r3Vrvw+nmEX2C4qHQRLMPxlO/X38kA1Z0XBiY3h4OHkOm18wPPyXncGMWVb+FzbFw0EMxcOgkvo/A0MlW1Aw53DwHDY/wD0Mq5aYVNW1WX0D4OLYNA1lMZavtraSoGT8ydWukklTU5u7uWNY1sGwSGL5zfDrGYuM6ldQMh7OMQv3MIjBPX8xN0xguNPDNq/iirhRD+MKmeEiPhrDwyUmcflj5MUwaM7lz/FjuAJk2IiD/AjyK+NIm0wDAACxUs8MaULTAAAAAElFTkSuQmCC)
  no-repeat;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .whole {
    width: 100%;
    flex-direction: column;
  }

  .left, .right {
    width: 100%;
    border-right: none;
  }
}
</style>
