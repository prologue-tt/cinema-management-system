<!--
 * 登录页面（蓝色系优化版）
-->
<template>
  <div class="login_container">
    <div class="login_box">
      <!-- 装饰元素 -->
      <div class="decor-top"></div>
      <div class="decor-bottom"></div>

      <div class="title_box">
        <p>
          <i class="iconfont icon-login-title" style="font-size: 36px;color: #1E88E5;"></i>
          <span style="color:#1E88E5;"> 登录影院用户端 </span>
        </p>
      </div>

      <!-- 登录表单区域 -->
      <el-form
        class="login_form"
        :model="loginForm"
        :rules="loginFormRules"
        ref="loginFormRef"
      >
        <!-- 用户名 -->
        <el-form-item prop="userName">
          <el-row>
            <el-col :span="2">
              <i
                class="iconfont icon-user-logo"
                style="font-size: 28px; color: #1E88E5"
              ></i>
            </el-col>
            <el-col :span="22">
              <el-input
                v-model="loginForm.userName"
                placeholder="请输入用户名"
                clearable
                class="custom-input"
              ></el-input>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-row>
            <el-col :span="2">
              <i
                class="iconfont icon-password-logo"
                style="font-size: 28px; color: #1E88E5"
              ></i>
            </el-col>
            <el-col :span="22">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
                class="custom-input"
              ></el-input>
            </el-col>
          </el-row>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item class="btns">
          <el-button
            icon="iconfont icon-sure-button"
            type="primary"
            @click="login"
            class="login-btn"
          >
            登录
          </el-button>
          <el-button
            icon="iconfont icon-add-button"
            type="info"
            @click="registerAccount"
            class="register-btn"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      // 登录表单数据对象（保留原逻辑）
      loginForm: {
        userName: "",
        password: "",
      },
      // 登录表单验证规则（保留原逻辑）
      loginFormRules: {
        // 验证用户名格式
        userName: [
          {
            required: true,
            message: "请输入用户名称",
            trigger: "blur",
          },
          {
            min: 2,
            max: 20,
            message: "用户名称长度在2到20个字符之间",
            trigger: "blur",
          },
        ],
        // 验证密码格式
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          {
            min: 6,
            max: 16,
            message: "登录密码长度在6到16个字符之间",
            trigger: "blur",
          },
        ],
      },
      sessionId: 0,
    };
  },
  created() {
    this.sessionId = window.sessionStorage.getItem("sessionId");
    console.log("this sessionId is : " + this.sessionId);
    window.sessionStorage.setItem("sessionId", 0);
  },
  methods: {
    success(params) {
      this.login();
    },
    login() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (!valid) return;
        axios.defaults.headers.post["Content-Type"] =
          "application/json";
        const { data: res } = await axios.post(
          "user/login",
          JSON.stringify(this.loginForm)
        );
        if (res.code !== 200) return this.$message.error(res.msg);

        this.$message.success({ message: "登录成功", duration: 1000 });
        console.log(res.data);
        // 保存token
        window.sessionStorage.setItem("token", res.data.token);
        res.data.user.role = null;
        window.sessionStorage.setItem(
          "loginUser",
          JSON.stringify(res.data.user)
        );
        if (
          this.sessionId !== 0 &&
          this.sessionId !== "0" &&
          this.sessionId !== null
        ) {
          this.$router.push("/chooseSeat/" + this.sessionId);
          return;
        }
        // 导航跳转到首页
        this.$router.push("/welcome");
      });
    },
    registerAccount() {
      this.$router.push("/register");
    },
  },
};
</script>

<style scoped>
/* 背景容器 */
.login_container {
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  height: 100vh;
  overflow: hidden;
  position: relative;
}

/* 背景装饰元素 */
.login_container::before {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(30, 136, 229, 0.1);
  top: -100px;
  right: -100px;
}

.login_container::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(100, 181, 246, 0.15);
  bottom: -50px;
  left: -50px;
}

/* 登录盒子 */
.login_box {
  width: 450px;
  height: 350px;
  background-color: #fff;
  border-radius: 15px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 10;
}

/* 顶部和底部装饰条 */
.decor-top {
  height: 6px;
  background: linear-gradient(90deg, #1E88E5, #64B5F6);
}

.decor-bottom {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #64B5F6, #1E88E5);
}

/* 标题样式 */
.title_box {
  text-align: center;
  font-size: 180%;
  padding: 30px 0 20px;
  font-weight: 600;
}

/* 表单样式 */
.login_form {
  position: absolute;
  bottom: 0;
  width: 100%;
  padding: 0 40px 40px;
  box-sizing: border-box;
}

/* 自定义输入框 */
.custom-input {
  border-radius: 30px !important;
  height: 45px !important;
  border-color: #BBDEFB !important;
  transition: all 0.3s ease !important;
}

.custom-input:focus {
  border-color: #1E88E5 !important;
  box-shadow: 0 0 0 2px rgba(30, 136, 229, 0.2) !important;
}

/* 按钮区域 */
.btns {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.login-btn {
  background-color: #1E88E5 !important;
  border-color: #1E88E5 !important;
  width: 120px;
  height: 45px;
  border-radius: 30px !important;
  font-size: 16px !important;
  transition: all 0.3s ease !important;
}

.login-btn:hover {
  background-color: #1976D2 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.3);
}

.register-btn {
  background-color: #E3F2FD !important;
  border-color: #BBDEFB !important;
  color: #1E88E5 !important;
  width: 120px;
  height: 45px;
  border-radius: 30px !important;
  font-size: 16px !important;
  transition: all 0.3s ease !important;
}

.register-btn:hover {
  background-color: #BBDEFB !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(100, 181, 246, 0.3);
}

/* 响应式调整 */
@media (max-width: 500px) {
  .login_box {
    width: 90%;
    height: auto;
    padding-bottom: 30px;
  }

  .title_box {
    font-size: 150%;
    padding: 25px 0 15px;
  }

  .login_form {
    position: static;
    padding: 0 20px;
  }

  .btns {
    flex-direction: column;
    gap: 10px;
  }

  .login-btn, .register-btn {
    width: 100%;
  }
}
</style>

