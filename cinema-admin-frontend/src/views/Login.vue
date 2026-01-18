<template>
  <div class="login_container">
    <!-- 装饰元素 -->
    <div class="decor-circle circle-1"></div>
    <div class="decor-circle circle-2"></div>
    <div class="decor-circle circle-3"></div>

    <div class="login_box">
      <!-- 标题区域 -->
      <div class="title_box">
        <div class="title-icon">
          <i class="iconfont icon-login-title"></i>
        </div>
        <h1>电影院管理系统</h1>
        <p class="subtitle">请登录您的账号</p>
      </div>

      <!-- 登录表单区域 -->
      <el-form class="login_form" :model="loginForm" :rules="loginFormRules" ref="loginFormRef">
        <!-- 用户名 -->
        <el-form-item prop="userName">
          <el-input
            v-model="loginForm.userName"
            placeholder="请输入用户名"
            clearable
            prefix-icon="iconfont icon-user"
            class="custom-input"
          ></el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            prefix-icon="iconfont icon-lock"
            class="custom-input"
          ></el-input>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item class="btns">
          <el-button
            size="large"
            type="primary"
            @click="login"
            class="login-btn"
          >
            <i class="iconfont icon-sure-button"></i>
            登录
          </el-button>


        </el-form-item>
      </el-form>
    </div>

    <!-- 页脚信息 -->
    <div class="footer-info">
      <p>© 2025默片时光电影院管理系统 - 版权所有</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      // 登录表单数据对象
      loginForm: {
        userName: '',
        password: ''
      },
      // 表单验证规则
      loginFormRules: {
        // 验证用户名
        userName: [
          { required: true, message: "请输入用户名称", trigger: "blur"},
          { min: 2, max: 20, message: "长度在2到20个字符之间", trigger: "blur"}
        ],
        // 验证密码
        password: [
          { required: true, message: "请输入密码", trigger: "blur"},
          { min: 6, max: 16, message: "长度在6到16个字符之间", trigger: "blur"}
        ]
      }
    }
  },
  methods:{
    login() {
      this.$refs.loginFormRef.validate(async valid => {
        if(!valid) return;
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        const { data: res} = await axios.post('user/login', JSON.stringify(this.loginForm));

        console.log('完整响应:', res);
        console.log('用户角色信息:', res.data.user.role);

        if(res.code !== 200) return this.$message.error(res.msg);

        // 检查权限逻辑
        if(res.data.user.role.children === null || res.data.user.role.children[0] === null) {
          this.$message.error("抱歉，您没有权限登录，请联系管理员获取权限")
          return
        }

        this.$message.success("登录成功")

        // 保存登录信息
        window.sessionStorage.setItem("token", res.data.token)
        window.sessionStorage.setItem("loginUser", JSON.stringify({
          user: res.data.user,
          cinemaId: res.data.cinemaId,
          cinemaName: res.data.cinemaName
        }));
        window.sessionStorage.setItem("btnPermission", res.data.user.role.roleId === 1 ? "admin" : "normal")

        console.log('登录信息保存完成，准备跳转...');
        console.log('token:', window.sessionStorage.getItem("token"));
        console.log('loginUser:', window.sessionStorage.getItem("loginUser"));

        // 尝试路由跳转
        try {
          await this.$router.push('/welcome');
          console.log('路由跳转成功');
        } catch (error) {
          console.error('路由跳转失败:', error);
          // 尝试备用跳转方式
          this.$router.replace('/welcome');
        }
      })
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
}
</script>

<style scoped>
/* 基础样式设置 */
.login_container{
  height: 100vh;
  width: 100%;
  position: relative;
  overflow: hidden;
  /* 可爱清新的蓝色渐变背景 */
  background: linear-gradient(135deg, #e6f7ff 0%, #b3e0ff 100%);
}

/* 装饰圆形元素 */
.decor-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  filter: blur(30px);
  z-index: 1;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -150px;
  left: -150px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -100px;
  right: -100px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 30%;
  right: 10%;
}

/* 登录盒子样式 */
.login_box{
  width: 420px;
  padding: 40px 30px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 10px 30px rgba(100, 180, 255, 0.2);
  z-index: 2;
  transition: all 0.3s ease;
}

.login_box:hover {
  box-shadow: 0 15px 35px rgba(80, 160, 255, 0.3);
  transform: translate(-50%, -51%);
}

/* 标题区域样式 */
.title_box{
  text-align: center;
  margin-bottom: 30px;
}

.title-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 15px;
  background: linear-gradient(135deg, #64b5f6 0%, #42a5f5 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 5px 15px rgba(66, 165, 245, 0.3);
}

.title-icon i {
  color: white;
  font-size: 40px;
}

.title_box h1 {
  color: #1976d2;
  font-size: 26px;
  margin: 0 0 10px;
  font-weight: 600;
}

.subtitle {
  color: #64b5f6;
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

/* 表单样式 */
.login_form{
  width: 100%;
  box-sizing: border-box;
}

/* 自定义输入框样式 */
.custom-input {
  border-radius: 12px;
  border: 1px solid #e3f2fd;
  transition: all 0.3s ease;
  height: 50px;
  font-size: 15px;
}

.custom-input:hover, .custom-input:focus-within {
  border-color: #90caf9;
  box-shadow: 0 0 0 3px rgba(144, 202, 249, 0.2);
}

.custom-input .el-input__prefix {
  color: #64b5f6;
  font-size: 18px;
}

/* 按钮区域样式 */
.btns{
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  gap: 15px;
}

.login-btn {
  flex: 1;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #42a5f5 0%, #1976d2 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(66, 165, 245, 0.3);
}

.login-btn:hover {
  background: linear-gradient(135deg, #2196f3 0%, #0d47a1 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(66, 165, 245, 0.4);
}

.register-btn {
  flex: 1;
  height: 50px;
  border-radius: 12px;
  background-color: #f0f7ff;
  color: #1976d2;
  border: 1px solid #bbdefb;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register-btn:hover {
  background-color: #e3f2fd;
  color: #0d47a1;
  border-color: #90caf9;
  transform: translateY(-2px);
}

/* 按钮图标样式 */
.login-btn i, .register-btn i {
  margin-right: 6px;
  font-size: 18px;
}

/* 页脚信息 */
.footer-info {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  color: #64b5f6;
  font-size: 12px;
  opacity: 0.7;
  z-index: 2;
}

/* 适配不同屏幕尺寸 */
@media (max-width: 500px) {
  .login_box {
    width: 90%;
    padding: 30px 20px;
  }

  .title-icon {
    width: 60px;
    height: 60px;
  }

  .title-icon i {
    font-size: 30px;
  }

  .title_box h1 {
    font-size: 22px;
  }
}
</style>
