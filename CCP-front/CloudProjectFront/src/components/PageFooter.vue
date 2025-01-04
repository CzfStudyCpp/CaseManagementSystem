<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- 功能模块 -->
      <div class="footer-section">
        <h3>我有需求</h3>
        <ul>
          <li><router-link to="/user/manage-my-requirements">发布需求</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>我有技术</h3>
        <ul>
          <li><router-link to="/demand">解决需求</router-link></li>
        </ul>
      </div>
      <div class="footer-section">
        <h3>帮助中心</h3>
        <ul>
          <li><router-link to="/help">查看帮助</router-link></li>
        </ul>
      </div>

      <!-- 服务支持 -->
      <div class="footer-support">
        <h3>服务与支持</h3>
        <p>周一至周日 08:00 - 23:00</p>
        <p>官方邮箱:sestriver@mail.scut.edu.cn</p>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="footer-bottom">
      <p>© 2024 云协作案例共享开发平台 - czf</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer',
};
</script>

<style scoped>
.footer {
  background-color: #333; /* 深灰背景 */
  color: #fff; /* 白色字体 */
  padding: 2rem 1rem;
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  bottom: 0;
  width: 100%;
}

/* 页脚内容容器 */
.footer-container {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

/* 单个功能模块 */
.footer-section {
  text-align: left;
  margin: 1rem;
}

.footer-section h3 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
  color: #f0f0f0; /* 淡白色标题 */
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li {
  margin: 0.3rem 0;
}

.footer-section ul li a {
  color: #aaa; /* 链接颜色 */
  text-decoration: none;
  font-size: 0.9rem;
}

.footer-section ul li a:hover {
  color: #00d4ff; /* 悬停时高亮 */
}

/* 服务支持部分 */
.footer-support {
  text-align: left;
}

.footer-support h3 {
  margin-bottom: 0.5rem;
}

.footer-support p {
  margin: 0.3rem 0;
  font-size: 0.9rem;
}

/* 底部版权信息 */
.footer-bottom {
  border-top: 1px solid #444; /* 添加顶部边框 */
  padding-top: 1rem;
  font-size: 0.8rem;
  color: #aaa;
}
</style>
