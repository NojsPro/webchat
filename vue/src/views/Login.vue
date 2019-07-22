<template>
  <div style="font-size: 24px; text-align: center" id="login">
    <div class="login_box" v-if="islogin">
      <h3>Web Talk</h3>
      <div class="login_input">
        <input placeholder="用户名" v-model="login.name">
        <input placeholder="密码" type="password" v-model="login.password">
      </div>
      <a href="#" class="login_tip">忘记密码了？</a>
      <div class="login_button">
        <button @click="islogin = false">注册</button>
        <button @click="loginsubmit">登录</button>
      </div>
    </div>
    <div class="login_box" v-else>
      <h4>来加入我们吧</h4>
      <div class="login_input">
        <input placeholder="用户名" v-model="register.name">
        <input placeholder="邮箱" type="email" v-model="register.email">
        <input placeholder="密码" type="password" v-model="register.password">
      </div>
      <div class="login_button">
        <button @click="islogin = true">返回</button>
        <button @click="registerSubmit">注册</button>
      </div>
    </div>
  </div>
</template>
<style>
</style>
<script type="text/babel">
import axios from "axios";
import Qs from "qs";
export default {
  data: function() {
    return {
      title: "我是頭部",
      islogin: true,
      login: {
        name: "",
        password: ""
      },
      register: {
        email: "",
        name: "",
        password: ""
      }
    };
  },
  mounted() {
    //console.log(jsonp)
    Notification.requestPermission();
  },
  methods: {
    loginsubmit() {
      //console.log(this.$store.state);
      let { name, password } = this.login;
      if (name != "" && password != "") {
        // this.$store.commit("userlogin", { name, userid: name });
        // this.$router.push({ path: "/s" });
        axios
          .post(
            "/user/login",
            Qs.stringify({
              name,
              password
            })
          )
          .then(res => {
            if (res.data.length == 1) {
              localStorage.setItem("login", JSON.stringify(res.data[0]));
              this.$store.commit("userlogin", res.data[0]);
              this.$router.push({ path: "/" });
            }
          });
      }
    },
    registerSubmit() {
      let uid = this.productUid();

      let { name, password, email } = this.register;
      if (name.length >= 3 && password.length >= 6) {
        axios
          .get(
            `/user/register?name=${name}&email=${email}&password=${password}&uid=${uid}`
          )
          .then(res => {
            this.islogin = true;
            this.$message({
              message: "注册成功，请进行登录吧！",
              type: "success"
            });
          })
          .catch(err => {
            this.$message.error("账号名重复");
          });
      }else{
        this.$message.error("账号名不得少于三位且密码不得少于六位数");
      }
    },
    productUid() {
      let uid = "";
      for (let i = 0; i < 6; i++) {
        uid += Math.floor(Math.random() * 10);
      }
      return uid;
    }
  }
};
</script>
<style lang="css" scoped>
#login {
  width: 100%;
  height: 100vh;
  background: #f2f6fc;
  margin: 0;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login_box {
  width: 350px;
  height: 400px;
  background: #ffffff;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  font-family: Arial, Tahoma, Geneva, Verdana, sans-serif;
  color: #606266;
  box-sizing: border-box;
  padding-top: 20px;
  position: relative;
  overflow: hidden;
}
.login_input {
  margin-top: 50px;
  width: 100%;
}
.login_input input {
  width: 100%;
  height: 50px;
  border: 0;
  background: #f2f6fc;
  text-align: center;
  box-sizing: border-box;
  padding: 10px;
  font-size: 14px;
  outline: none;
}
.login_button {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 50px;
  background: rgb(88, 163, 50);
  display: flex;
  align-items: center;
}
.login_button button:first-child {
  background: #67c23a;
}
.login_button button {
  width: 50%;
  border: 0;
  height: 50px;
  background: rgb(63, 130, 197);
  color: #ffffff;
  font-size: 18px;
}
.login_tip {
  display: block;
  margin-top: 50px;
  font-size: 12px;
}
.title {
  color: red;
}
</style>
