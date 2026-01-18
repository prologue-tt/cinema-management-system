<!--
 * 注册页面
 2.不会跳转到登录页面，估计没有注册好
-->

<template>
  <div class="register_container">
    <div class="register_box">
      <div class="title_box">
        <p style="color: #0994e7"><i class="iconfont icon-login-title" style="font-size: 36px;"> </i>注册电影院管理系统</p>
      </div>
      <!-- 登录表单区域 -->
      <el-form class="register_form" :model="registerForm" :rules="registerFormRules" ref="registerFormRef">
        <!-- 用户名 -->
        <el-form-item prop="userName">
          <el-input v-model="registerForm.userName" placeholder="请创建用户名" clearable
                    prefix-icon="iconfont icon-user"></el-input>
        </el-form-item>
        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password
                    prefix-icon="iconfont icon-lock"></el-input>
        </el-form-item>
        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword" type="password" placeholder="请再次确认密码" show-password
                      prefix-icon="iconfont icon-lock"></el-input>
        </el-form-item>


        <!-- 按钮区域 -->
        <el-form-item class="btns">
          <div style="display: flex; justify-content: space-between; width: 100%;">
            <!-- 左侧登录按钮 -->
            <el-button style="font-size: 22px;" size="small" type="success" @click="goToLogin">
              <i class="iconfont icon-sure-button" style="font-size: 22px;"></i>
              登录
            </el-button>

            <!-- 右侧注册按钮 -->
            <el-button style="font-size: 22px;" size="small" type="primary" @click="register">
              <i class="iconfont icon-register" style="font-size: 22px;"></i>
              注册
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
// 使用普通函数 + bind 确保 this 指向正确
    const validateConfirmPassword = function(rule, value, callback) {
      if (!value) {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致！'));
      } else {
        callback();
      }
    }.bind(this);

    return {
      //注册表单数据对象
      registerForm: {
        userName: '',
        password: '',
        confirmPassword: ''
      },
      //表单验证规则
      registerFormRules: {
        //验证用户名
        userName: [
          { required: true, message: "请输入用户名称", trigger: "blur"},
          { min:2, max: 20, message: "长度在2到20个字符之间", trigger: "blur"}
        ],
        //验证密码
        password: [
          { required: true, message: "请输入密码", trigger: "blur"},
          { min:6, max: 16, message: "长度在6到16个字符之间", trigger: "blur"}
        ],
        //验证两次输入相同
        confirmPassword: [
          { required: true, message: '请再次确认密码', trigger: 'blur' },
          { min:6, max: 16, message: "长度在6到16个字符之间", trigger: "blur"},
          { validator: validateConfirmPassword, trigger: ['blur', 'change'] }
          // {
          //   validator: (rule, value, callback) => {
          //     if (value !== form.value.password) {
          //       callback(new Error('密码不匹配'))
          //     } else {
          //       callback()
          //     }
          //   }, trigger: 'blur'
          // }

        ]
      }
    }
  },
  methods:{
    success(params) {
      this.register()
    },
    register() {
      // 1. 表单验证
      this.$refs.registerFormRef.validate(async valid => {
        if (!valid) return; // 验证失败直接返回

        try {
          // 2. 设置请求头（确保后端接收JSON格式）
          axios.defaults.headers.post['Content-Type'] = 'application/json';

          // 3. 发送注册请求
          const { data: res } = await axios.post(
              'user/register',
              JSON.stringify(this.registerForm) // 提交注册表单数据
          );

          // 4. 处理响应
          if (res.code !== 200) {
            this.$message.error(res.msg || '注册失败'); // 显示后端返回的错误信息
            return;
          }

          // 5. 注册成功提示
          this.$message.success({
            message: '注册成功！即将跳转到登录页',
            duration: 1500,
            onClose: () => {
              // 6. 跳转到登录页（或根据需求跳转到其他页面）
              this.$router.push('/login');
            }
          });

        } catch (error) {
          // 7. 捕获异常（网络错误/后端500错误等）
          console.error('注册请求失败:', error);
          this.$message.error(
              error.response?.data?.msg ||
              error.message ||
              '服务器繁忙，请稍后重试'
          );
        }
      });
    },
    // async register() {
    //   try {
    //     // 先验证表单
    //     const valid = await registerFormRef.value.validate();
    //     if (!valid) return;
    //
    //     // 调用注册 API
    //     const response = await auth.register({
    //       username: this.form.username,
    //       password: this.form.password
    //     });
    //
    //     if (response.data.success) {
    //       ElMessage.success('注册成功！');
    //       await this.$router.push('/login'); // 跳转到登录页
    //     } else {
    //       ElMessage.error(response.data.message || '注册失败');
    //     }
    //   } catch (error) {
    //     ElMessage.error('注册失败，请稍后重试');
    //     console.error('注册错误:', error);
    //   }
    // },

    goToLogin() {
      this.$router.push('/login'); // 注意：这里应该用 this.$router 而不是 router
    }
  }
}
</script>

<style scoped>
.register_container{
  height: 100%;

  background-size: cover;
}

.register_box{
  opacity:0.9;
  width: 450px;
  height: 350px;
  background-color: #fff;
  border-radius: 8px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid #ffffff;
}

.avatar_box{
  height: 130px;
  width: 130px;
  border: 1px solid #eee;
  border-radius: 50%;
  padding: 10px;
  box-shadow: 0 0 10px #ddd;
  position: absolute;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
}

.avatar_box > img{
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background-color: #eee;
}

.title_box{
  text-align: center;
  font-size: 200%;
}

.register_form{
  position: absolute;
  bottom: 0;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.btns{
  display: flex;
  justify-content: center;
}
</style>
