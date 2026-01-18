<template>
  <div class="error-container">
    <!-- 中央404数字 -->
    <div class="central-content">
      <div class="number-404">404</div>
      <p class="message">页面未找到</p>
      <el-button @click="toWelcome" type="primary" round>返回主页</el-button>
    </div>

    <!-- 简单的背景点缀 -->
    <div class="background-elements">
      <div class="dot"></div>
      <div class="dot"></div>
      <div class="dot"></div>
      <div class="dot"></div>
      <div class="dot"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Error404",
  methods: {
    toWelcome(){
      this.$router.push('/Welcome')
      return false
    }
  }
}
</script>

<style scoped>
/* 基础样式 */
.error-container {
  width: 100%;
  height: 100vh;
  background-color: #f8f9fa;
  position: relative;
  overflow: hidden;
}

/* 中央内容区 */
.central-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 10;
}

/* 404数字样式 */
.number-404 {
  font-size: 150px;
  font-weight: bold;
  color: #343a40;
  margin: 0;
  letter-spacing: -10px;
  position: relative;
  animation: pulse 3s infinite ease-in-out;
}

/* 提示信息 */
.message {
  font-size: 24px;
  color: #6c757d;
  margin: 20px 0 30px;
}

/* 按钮样式 */
.el-button {
  padding: 12px 30px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 背景点缀元素 */
.background-elements {
  width: 100%;
  height: 100%;
  position: absolute;
}

.dot {
  position: absolute;
  width: 6px;
  height: 6px;
  background-color: #dee2e6;
  border-radius: 50%;
  animation: float 8s infinite ease-in-out;
}

.dot:nth-child(1) {
  top: 20%;
  left: 15%;
  animation-delay: 0s;
}

.dot:nth-child(2) {
  top: 70%;
  left: 80%;
  animation-delay: 1s;
}

.dot:nth-child(3) {
  top: 40%;
  left: 90%;
  animation-delay: 2s;
}

.dot:nth-child(4) {
  top: 80%;
  left: 10%;
  animation-delay: 3s;
}

.dot:nth-child(5) {
  top: 30%;
  left: 50%;
  animation-delay: 4s;
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .number-404 {
    font-size: 100px;
    letter-spacing: -5px;
  }

  .message {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .number-404 {
    font-size: 80px;
  }
}
</style>
