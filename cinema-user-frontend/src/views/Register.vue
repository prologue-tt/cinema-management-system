<template>
  <div class="register-container">
    <!-- 头部标题区域 -->
    <div class="header-section">
      <div class="cinema-tag">默片时光电影院</div>
      <div class="title-box">
        <i class="iconfont icon-r-add" style="font-size: 32px; color: #FF6B8B;"> </i>
        <h1>注册新用户</h1>
      </div>
    </div>

    <!-- 注册表单主体 -->
    <div class="register-body">
      <el-form
        class="register-form"
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerFormRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="registerForm.userName"
            placeholder="请输入用户名"
            clearable
            class="custom-input"
          ></el-input>
        </el-form-item>

        <el-form-item label="用户密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            class="custom-input"
          ></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请输入确认密码"
            show-password
            class="custom-input"
          ></el-input>
        </el-form-item>

        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input
            v-model="registerForm.phoneNumber"
            type="text"
            placeholder="请输入手机号"
            clearable
            class="custom-input"
          ></el-input>
        </el-form-item>

        <el-form-item label="性别">
          <div class="gender-options">
            <el-radio v-model="gender" label="1" class="gender-radio">
              <i class="iconfont icon-r-male" style="margin-right: 5px;"></i>男
            </el-radio>
            <el-radio v-model="gender" label="0" class="gender-radio">
              <i class="iconfont icon-r-female" style="margin-right: 5px;"></i>女
            </el-radio>
          </div>
        </el-form-item>

        <el-form-item class="submit-btn-container">
          <el-button
            type="primary"
            @click="register"
            icon="iconfont icon-r-yes"
            class="register-btn"
          >
            立即注册
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <div class="decor-circle pink"></div>
      <div class="decor-circle yellow"></div>
      <div class="decor-circle blue"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    // 密码验证
    let validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    // 手机号验证
    let checkMobile = (rule, value, cb) => {
      const regMobile = /^(0|86|17951)?(13[0-9]|15[0123456789]|17[678]|18[0-9]|14[57])[0-9]{8}$/
      if (regMobile.test(value)){
        return cb()
      }
      cb(new Error('请输入合法的手机号码'))
    }

    return {
      gender: '1',
      registerForm: {
        userName: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        sex: ''
      },
      registerFormRules: {
        userName: [
          { required: true, message: "请输入用户名称", trigger: "blur"},
          { min: 2, max: 20, message: "用户名称长度在2到20个字符之间", trigger: "blur"}
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur"},
          { min:6, max: 16, message: "长度在6到16个字符之间", trigger: "blur"}
        ],
        confirmPassword: [
          { required: true, validator: validatePass, message: "两次密码输入不一致", trigger: "blur"},
        ]
        ,
        phoneNumber: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { validator: checkMobile, trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    register() {
      this.$refs.registerFormRef.validate(async valid => {
        if(!valid) return
        this.registerForm.sex = this.gender === '1'
        axios.defaults.headers.post['Content-Type'] = 'application/json'
        const { data: res} = await axios.post('sysUser/register', JSON.stringify(this.registerForm));
        if(res.code !== 200) return this.$message.error(res.msg);

        this.$message.success("注册完成！");
        this.$router.push('/login')
        this.$refs.registerFormRef.resetFields()
      })
    },
    toWelcome() {
      this.$router.push('/welcome')
    }
  }
}
</script>

<style scoped>
/* 基础容器样式 */
.register-container {
  width: 100%;
  min-height: 100vh;
  background-color: #FFF9F9;
  padding: 30px 0;
  box-sizing: border-box;
  /* 可爱背景图案 */
  background-image:
    radial-gradient(#FFE0E0 0.5px, transparent 0.5px),
    radial-gradient(#FFE0E0 0.5px, #FFF9F9 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

/* 头部区域 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.cinema-tag {
  display: inline-block;
  background-color: #FFF0F3;
  color: #FF6B8B;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 15px;
  box-shadow: 0 2px 5px rgba(255, 107, 139, 0.1);
}

.title-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.title-box h1 {
  color: #333;
  font-size: 28px;
  margin: 0;
  text-shadow: 1px 1px 3px rgba(255, 107, 139, 0.1);
}

/* 表单主体 */
.register-body {
  max-width: 500px;
  margin: 0 auto;
  padding: 0 20px;
}

.register-form {
  background-color: #fff;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 5px 20px rgba(255, 107, 139, 0.1);
}

/* 自定义输入框 */
.custom-input {
  border-radius: 15px !important;
  height: 45px !important;
  border-color: #FFD1DC !important;
  transition: all 0.3s ease !important;
}

.custom-input:focus {
  border-color: #FF6B8B !important;
  box-shadow: 0 0 0 2px rgba(255, 107, 139, 0.2) !important;
}

/* 性别选项 */
.gender-options {
  display: flex;
  gap: 20px;
  padding-top: 5px;
}

.gender-radio {
  color: #666 !important;
  font-size: 15px !important;
  display: flex !important;
  align-items: center !important;
}

/* 提交按钮 */
.submit-btn-container {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.register-btn {
  background-color: #FF6B8B !important;
  border-color: #FF6B8B !important;
  width: 100%;
  height: 48px;
  border-radius: 25px !important;
  font-size: 16px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 10px rgba(255, 107, 139, 0.2) !important;
}

.register-btn:hover {
  background-color: #ff527a !important;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 107, 139, 0.3) !important;
}

/* 底部装饰 */
.footer-decoration {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 50px;
}

.decor-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.pink {
  background-color: #FF6B8B;
}

.yellow {
  background-color: #FFD04B;
}

.blue {
  background-color: #87CEEB;
}

/* 响应式调整 */
@media (max-width: 576px) {
  .register-form {
    padding: 20px 15px;
  }

  .title-box h1 {
    font-size: 24px;
  }

  .gender-options {
    flex-direction: column;
    gap: 10px;
  }
}
</style>

