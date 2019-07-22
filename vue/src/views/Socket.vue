<template>
  <div id="socket" :style="{background: bg}">
    <!-- <span>{{em}}</span> -->
    <VEmojiPicker :pack="pack" @select="selectEmoji" class="emojibox" v-if="em"/>
    <div class="tools">
      <el-popover placement="bottom" width="200" trigger="click">
        <div class="bgTheme">
          <div class="themeColor">
            <div class="colorExp">
              <span :style="{background: '#B0E2FF'}" @click="bgtemp = '#B0E2FF'">#B0E2FF</span>
              <span :style="{background: '#FFC1C1'}" @click="bgtemp = '#FFC1C1'">#FFC1C1</span>
            </div>
            <input type="text" v-model="bgtemp" @keyup.enter="colorChange">
          </div>
          <div class="themeButton">
            <el-button @click="resetColor">恢复默认</el-button>
            <el-button @click="colorChange">确定</el-button>
          </div>
        </div>
        <div class="clearStorage" slot="reference">
          <i class="el-icon-brush"></i>
        </div>
        <!-- <el-button slot="reference">click 激活</el-button> -->
      </el-popover>
      <div class="clearStorage" @click="removeStorage">
        <el-tooltip class="item" effect="dark" content="删除聊天记录" placement="bottom">
          <i class="el-icon-delete"></i>
        </el-tooltip>
      </div>
      <div class="clearStorage" @click="loginout">
        <el-tooltip class="item" effect="dark" content="退出登录" placement="bottom">
          <i class="el-icon-switch-button"></i>
        </el-tooltip>
      </div>
    </div>

    <div
      class="socket_list"
      @mousedown="listStart"
      @mouseup="listEnd"
      ref="listbox"
      :style="{ top: list_xy.y +'px', left: list_xy.x + 'px' }"
    >
      <div class="list_top">
        <div class="user_box">
          <div class="name_img">
            <avatar :username="msid.name" v-if="avaterif" :size="35"></avatar>
            <h5>{{msid.name}}</h5>
          </div>
          <span style="cursor: pointer;" @click="getOnlineUser" @click.stop>
            <i class="el-icon-refresh"></i>
          </span>
        </div>
        <div class="search_box">
          <i class="el-icon-search"></i>
          <input placeholder="请输入内容" v-model="search">
        </div>
      </div>
      <div class="list_bodys">
        <ul>
          <li class="list_li" @click="publicButton">
            <div class="list_name_img">
              <avatar username="Public Chat" :size="35"></avatar>
              <div class="name_msg">
                <h5>Public Chat</h5>
                <!-- <span>{{item}}</span> -->
              </div>
            </div>
            <!-- <span class="list_time">11:15 AM</span> -->
          </li>
          <li
            class="list_li"
            v-for="(item, i) in userinfo"
            :key="i"
            @click="clickButton(item.uid, item.name)"
          >
            <div class="list_name_img">
              <avatar :username="item.name" :size="35"></avatar>
              <div class="name_msg">
                <h5>{{item.name}}</h5>
                <!-- <span>{{item}}</span> -->
              </div>
            </div>
            <!-- <span class="list_time">11:15 AM</span> -->
          </li>
        </ul>
      </div>
    </div>
    <div
      class="socket_talk"
      ref="talkbox"
      v-if="winshow"
      :style="{ top: talk_xy.y +'px', left: talk_xy.x + 'px' }"
    >
      <div class="talk_top" @mousedown="talkStart" @mouseup="talkEnd">
        <h4>{{this.winname}}</h4>
        <!-- <div class="talk_link">
          <input type="text" placeholder="用户名">
          <button>连接</button>
        </div>-->
        <span @click="close" @click.stop>
          <i class="el-icon-close"></i>
        </span>
      </div>
      <div class="talk_body" ref="talkbody" @click="em = false">
        <ul>
          <li
            class="talk_msg"
            :style="{ flexDirection:item.id==1?'row':'row-reverse'}"
            v-for="(item,i) in messageList"
            :key="i"
          >
            <avatar v-if="item.id==1" :username="item.name" :size="35"></avatar>
            <avatar v-else :username="msid.name" :size="35"></avatar>
            <div :style="{ flexDirection:item.id==0?'row':'row-reverse'}">
              <span v-if="item.id==1">{{item.name}}</span>
              <span v-else>{{msid.name}}</span>
              <p class="talk_content">{{item.msg}}</p>
            </div>
          </li>
          <!-- <li
            class="talk_msg"
            v-for="(item,i) in messageList"
            :key="i"
          >
            <img src="../assets/logo.png" alt="avater">
            <div>
              <p>luo</p>
              <span>{{item}}</span>
            </div>
          </li>-->
        </ul>
      </div>
      <div class="talk_send">
        <input type="text" class="talk_input" v-model="msginput" @keyup.enter="sendMessage">
        
        <!-- <svg  style="width:20px;height:20px" ><use  xlink:href="/emoig.svg" /></svg> -->
        <span class="talk_truck_icon" @click="em = !em">
          <i class="el-icon-truck"></i>
        </span>
        <span class="talk_send_icon" @click="sendMessage">
          <i class="el-icon-s-promotion"></i>
        </span>
      </div>
    </div>
  </div>
</template>
<script>
import Avatar from "vue-avatar";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import axios from "axios";
import Qs from "qs";
import { async } from "q";
import VEmojiPicker from "v-emoji-picker";
import packData from "v-emoji-picker/data/emojis.json";
import { setTimeout } from "timers";
export default {
  components: {
    VEmojiPicker,
    Avatar
  },
  data: function() {
    return {
      pack: packData,
      em: false,
      mouselist: false,
      winshow: true,
      search: "",
      stompClient: "",
      timer: "",
      winname: "Public Chat",
      imageUrl: "",
      talk_xy: {
        x: 450,
        y: 50
      },
      list_xy: {
        x: 50,
        y: 50
      },
      movexy: {
        x: 0,
        y: 0
      },
      dialogFormVisible: false,
      form: {
        name: "",
        email: ""
      },
      formLabelWidth: "80px",
      bg: "",
      bgtemp: "",
      avaterif: false,
      usid: [],
      public: [],
      msid: "",
      osid: "",
      msginput: "",
      messageList: [],
      messagestore: [],
      userinfo: []
    };
  },
  mounted() {
    if(localStorage.getItem("bgbase")){
      this.bg = localStorage.getItem("bgbase");
    }else{
      this.resetColor();
    }
    if (localStorage.getItem("messagestore"))
      this.messagestore = JSON.parse(localStorage.getItem("messagestore"));
    // let temp = this.$store.state;
    //console.log(this.$store.state);
    
    if (localStorage.getItem("login")) {
      this.$store.commit(
        "userlogin",
        JSON.parse(localStorage.getItem("login"))
      );
      //console.log(this.$store.state.user);
    }

    if (!localStorage.getItem("login")) {
      //this.$router.push({ path: "/login" });
    } else {
      this.msid = JSON.parse(localStorage.getItem("login"));
      //console.log("en:", this.msid);
      this.initWebSocket();
      this.publicButton();
    }

    // window.unload = () => {
    //   this.stompClient.send(
    //     "/app/offline",
    //     {},
    //     JSON.stringify({ userid: this.msid.uid })
    //   );
    // };
  },

  beforeDestroy() {
    // 页面离开时断开连接,清除定时器

    if (this.$store.state.current == 0) {
      this.disconnect();

      clearInterval(this.timer);
    }
  },
  methods: {
    selectEmoji(emoji) {
      // console.log(emoji);
      // this.em = emoji.emoji;
      this.msginput = this.msginput + emoji.emoji;
    },
    initWebSocket() {
      this.connection();
      let that = this;
      // 断开重连机制,尝试发送消息,捕获异常发生时重连
      this.timer = setInterval(() => {
        try {
          //that.stompClient.send("test");
        } catch (err) {
          console.log("断线了: " + err);
          this.stompClient.send(
            "/app/offline",
            {},
            JSON.stringify({ uid: this.msid.uid })
          );
          that.connection();
        }
      }, 5000);
    },
    connection() {
      // 建立连接对象
      const that = this;
      let socket = new SockJS("/queueServer");
      // 获取STOMP子协议的客户端对象
      this.stompClient = Stomp.over(socket);
      // 定义客户端的认证信息,按需求配置
      let headers = {};
      // 向服务器发起websocket连接
      this.stompClient.connect(
        {},
        () => {
          this.stompClient.send(
            "/app/online",
            {},
            JSON.stringify({ uid: this.msid.uid })
          );
          //this.getOnlineUser();
          this.stompClient.subscribe(
            "/user/" + this.msid.uid + "/message",
            res => {
              //this.messagestore.push(JSON.parse(res.body))
              let hh = res.body;
              let nn = JSON.parse(hh);
              this.newMessage(nn.form);
              let arr = this.messagestore;
              arr.push(JSON.parse(hh));
              localStorage.setItem("messagestore", JSON.stringify(arr));
              this.messagestore = arr;
              if (this.osid == JSON.parse(hh).form)
                this.clickButton(this.osid, this.winname);
            }
          );

          this.stompClient.subscribe("/user/public/message", res => {
            //console.log("public", res);
            let hh = res.body;
            let nn = JSON.parse(hh);
            this.public.push(JSON.parse(hh));
            if (this.osid == "public") {
              //console.log(this.public);
              this.publicButton();
            }
            // this.userlist.push(res.body);
          });

          this.stompClient.subscribe("/user/online/message", res => {
            setTimeout(() => {
              this.getOnlineUser();
            }, 200);
          });

          this.stompClient.subscribe("/user/offline/message", res => {
            setTimeout(() => {
              this.getOnlineUser();
            }, 200);
          });
          axios
            .get(
              `/line/online?name=${this.msid.name}&avater=1&uid=${
                this.msid.uid
              }`
            )
            .then(res => {
              this.getOnlineUser();
              console.log("在线");
            });
        },
        err => {
          // 连接发生错误时的处理函数
          console.log("失败");
          console.log(err);
        }
      );
    }, //连接 后台
    disconnect() {
      if (this.stompClient) {
        this.stompClient.send(
          "/app/offline",
          {},
          JSON.stringify({ uid: this.msid.uid })
        );
        this.stompClient.disconnect();
      }
    }, // 断开连接
    getOnlineUser() {
      axios.get(`/line/list`).then(res => {
        //console.log(res);
        this.avaterif = true;
        this.userinfo = [];
        res.data.map(value => {
          if (this.msid.uid != value.uid) {
            this.userinfo.push(value);
          }
          return value;
        });
      });
    },
    newMessage(uid) {
      //console.log(uid);
      let list = this.userinfo;
      list.map(value => {
        if (value.uid == uid) {
          this.$message("你有一条来自" + value.name + "新消息");
          var notification = new Notification("收到新信息", {
            body: "你有一条来自" + value.name + "新消息"
          });
        }
      });
    },
    uidgetName(uid) {
      let list = this.userinfo;
      let name = "";
      list.map(value => {
        if (value.uid == uid) {
          name = value.name;
        }
        return value;
      });
      return name;
    },
    publicButton() {
      //添加按钮事件向服务端发送数据
      let e = "public";
      this.winshow = true;
      this.osid = e;
      this.winname = "Public Chat";
      this.messageList = [];
      this.public.map(value => {
        if (value.form == this.msid.uid && value.to == e) {
          this.messageList.push({
            id: 0,
            msg: value.message,
            name: this.msid.name
          });
        } else if (value.to == e) {
          this.messageList.push({
            id: 1,
            msg: value.message,
            name: this.uidgetName(value.form)
          });
        }
        return value;
      });
      let ele = this.$refs.talkbody;
      setTimeout(() => {
        if (ele.scrollHeight) ele.scrollTop = ele.scrollHeight + 60;
      }, 50);
    },
    clickButton(e, name) {
      //添加按钮事件向服务端发送数据
      this.winshow = true;
      this.osid = e;
      this.winname = name;
      this.messageList = [];
      this.messagestore.map(value => {
        if (value.form == e && value.to == this.msid.uid) {
          this.messageList.push({
            id: 1,
            msg: value.message,
            name: this.uidgetName(value.form)
          });
        } else if (value.form == this.msid.uid && value.to == e) {
          this.messageList.push({
            id: 0,
            msg: value.message,
            name: this.msid.name
          });
        }
        return value;
      });

      let ele = this.$refs.talkbody;
      setTimeout(() => {
        if (ele.scrollHeight) ele.scrollTop = ele.scrollHeight + 60;
      }, 50);
    },
    sendMessage() {
      //console.log(this.msginput);
      this.stompClient.send(
        "/app/queue",
        {},
        JSON.stringify({
          username: this.msid.uid,
          sendname: this.osid,
          content: this.msginput
        })
      );
      let arr = {
        form: this.msid.uid,
        message: this.msginput,
        to: this.osid
      };
      this.messagestore.push(arr);

      localStorage.setItem("messagestore", JSON.stringify(this.messagestore));

      this.msginput = "";
      if (this.osid == "public") this.publicButton();
      else this.clickButton(this.osid, this.winname);

      //this.stompClient.send("/app/online", {}, JSON.stringify({'userid': '2016127214'}))
    },
    handleAvatarSuccess(res, file) {
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      // console.log(file);
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }
      this.getBase64(file, imageUrl => {
        // console.log(imageUrl);
      });
      return false;
    },
    getBase64(img, callback) {
      const reader = new FileReader();
      reader.addEventListener("load", () => callback(reader.result));
      reader.readAsDataURL(img);
    },
    close() {
      // console.log("关闭窗口");
      //console.log(this.messagestore);
      this.me = false;
      this.winshow = false;
    },
    resetColor() {
      this.bg = "url('/default.jpg') no-repeat center";
      this.bgtemp = "";
    },
    colorChange() {
      let check = this.bgtemp;
      if (check != "") {
        if (check.length == 7 && check[0] == "#") {
          this.bg = check;
          this.bgtemp = "";
        } else if (check.indexOf("http") == 0) {
          this.bg = `url('${this.bgtemp}') no-repeat center`;
          this.bgtemp = "";
        }
      }
      localStorage.setItem('bgbase', this.bg);
    },
    removeStorage() {
      localStorage.removeItem("messagestore");
      this.$message({
        message: "刷新后删除所有聊天记录",
        type: "success"
      });
    },
    message() {
      const h = this.$createElement;

      this.$notify({
        title: "标题名称",
        message: h(
          "i",
          { style: "color: teal" },
          "这是提示文案这是提示文案这是提示文案这是提示文案这是提示文案这是提示文案这是提示文案这是提示文案"
        )
      });
    },
    listStart(e) {
      let { x, y } = this.list_xy;
      let screen = {
        x: e.screenX - x,
        y: e.screenY - y
      };
      this.movexy = screen;
      this.mouselist = true;
      this.$refs.listbox.addEventListener("mousemove", this.listMove, false);
      // console.log(this.$refs.listbox.offsetLeft, this.$refs.listbox.offsetTop);
    },
    listMove(e) {
      if (this.mouselist) {
        this.list_xy = {
          x: e.screenX - this.movexy.x,
          y: e.screenY - this.movexy.y
        };
        // this.$refs.listbox.addEventListener('mouseup', this.listEnd,false)
      }
    },
    listEnd() {
      this.mouselist = false;
      this.$refs.listbox.removeEventListener("mousemove", this.listMove, false);
    },
    talkStart(e) {
      let { x, y } = this.talk_xy;
      let screen = {
        x: e.screenX - x,
        y: e.screenY - y
      };
      this.movexy = screen;
      this.mouselist = true;
      this.$refs.talkbox.addEventListener("mousemove", this.talkMove, false);
    },
    talkMove(e) {
      if (this.mouselist) {
        this.talk_xy = {
          x: e.screenX - this.movexy.x,
          y: e.screenY - this.movexy.y
        };
        // this.$refs.listbox.addEventListener('mouseup', this.listEnd,false)
      }
    },
    talkEnd() {
      this.mouselist = false;
      this.$refs.talkbox.removeEventListener("mousemove", this.talkMove, false);
    },
    loginout() {
      this.$store.commit("userloginout");
      localStorage.removeItem("login");
      this.disconnect();
      axios.get(`/line/offline?uid=${this.msid.uid}`).then(res => {
        console.log("退出登录成功");
        this.$router.push({ path: "/login" });
      });
    }
  }
};
</script>
<style scoped>
#socket {
  width: 100%;
  height: 100vh;
  background-size: cover;
  position: relative;
  top: 0;
  left: 0;
  overflow: hidden;
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;
}
#socket ul {
  margin: 0;
  padding: 0;
}
.socket_list {
  width: 280px;
  height: 450px;
  background: rgba(40, 26, 52, 1);
  position: absolute;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}
.socket_talk {
  width: 600px;
  height: 450px;
  background: #f2f6fc;
  position: absolute;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}
.emojibox {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
}
.list_top {
  height: 120px;
  width: 100%;
  box-sizing: border-box;
  padding: 15px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.list_bodys {
  width: 100%;
  height: 320px;
  overflow: auto;
}
.list_bodys::-webkit-scrollbar-track-piece {
  background-color: rgba(40, 26, 52, 1);
}

.list_bodys::-webkit-scrollbar {
  width: 3px;
  height: 8px;
}

.list_bodys::-webkit-scrollbar-thumb {
  background-color: rgba(76, 49, 100, 1);
  background-clip: padding-box;
  min-height: 28px;
}

.list_bodys::-webkit-scrollbar-thumb:hover {
  background-color: rgba(76, 49, 100, 1);
}
.user_box {
  height: 35px;
  width: 100%;
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #f2f6fc;
}
.user_box .name_img {
  display: flex;
  align-items: center;
}
.user_box .name_img h5 {
  margin: 0 10px;
}
.user_box .name_img img {
  border-radius: 50%;
  margin-right: 8px;
  height: 30px;
  width: 30px;
}
.list_li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  width: 100%;

  box-sizing: border-box;
  padding: 15px 20px;
}
.list_li:hover {
  background: rgb(94, 72, 114);
}
.list_time {
  font-size: 10px;
  color: #ffffff;
}
.list_name_img {
  display: flex;
  align-items: center;
}
.list_name_img > div {
  margin-right: 8px;
}
.name_msg {
  height: 35px;
  display: flex;
  align-items: center;
}
.name_msg h5 {
  margin: 0;
  color: #f2f6fc;
}
.name_msg span {
  font-size: 12px;
  color: rgba(76, 49, 100, 1);
}

.search_box {
  position: relative;
  width: 100%;
}
.search_box .el-icon-search {
  color: #f2cff7;
  position: absolute;
  top: 8.5px;
  left: 10px;
}
.search_box input {
  background: rgb(55, 36, 73);
  border: none;
  height: 30px;
  width: 100%;
  border-radius: 15px;
  box-sizing: border-box;
  padding-left: 30px;
  font-size: 12px;
  outline: none;
  color: #f2f6fc;
}
.talk_top {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 50px;
  background: #ffffff;
  box-sizing: border-box;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.talk_top span {
  cursor: pointer;
}
.talk_link input {
  border: 1px solid rgba(0, 0, 0, 0.1);
  height: 20px;
  outline: none;
  padding: 3px 5px;
  margin-right: 20px;
  border-radius: 3px;
  width: 150px;
}
.talk_link button {
  border: 1px solid rgba(0, 0, 0, 0.1);
  outline: none;
  padding: 3.5px 7px;
  color: #606266;
  cursor: pointer;
  border-radius: 3px;
  background: #ffffff;
}
.talk_body {
  margin-top: 50px;
  box-sizing: border-box;
  padding: 15px;
  overflow: auto;
  height: 330px;
}
.talk_msg {
  list-style: none;
  min-height: 35px;
  width: 100%;
  display: flex;
  align-items: center;
  flex-direction: row-reverse;
  margin: 10px 0;
}
.talk_msg img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #ffffff;
  margin: 0 5px;
}
.talk_msg > div {
  display: flex;
  align-items: center;
}
.talk_msg span {
  margin: 5px;
  font-size: 12px;
  color: #d9d9d9;
  font-weight: normal;
}
.talk_msg .talk_content {
  display: block;
  min-height: 30px;
  padding: 5px 10px;
  background: #ffffff;
  border-radius: 5px;
  line-height: 30px;
  margin: 5px;
  max-width: 450px;
}
.talk_send {
  position: absolute;
  bottom: 15px;
  left: 5%;
  width: 90%;
  height: 35px;
  border-radius: 5px;
  box-shadow: 0px 0px 3px rgba(0, 0, 0, 0.03);
  background: #ffffff;
}
.talk_input {
  width: 100%;
  height: 35px;
  border-radius: 5px;
  border: none;
  outline: none;
  box-sizing: border-box;
  padding: 5px 10px;
  padding-right: 75px;
  color: #606266;
}
.talk_send_icon {
  position: absolute;
  right: 10px;
  top: 3px;
  font-size: 24px;
  cursor: pointer;
  color: #409eff;
}
.talk_truck_icon {
  position: absolute;
  right: 50px;
  top: 3px;
  font-size: 24px;
  cursor: pointer;
  color: #409eff;
}
.talk_truck_icon:hover,
.talk_send_icon:hover {
  color: #67c23a;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.el-upload {
  margin: 0 auto;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
input[type="file"] {
  display: none;
}
.clearStorage {
  width: 30px;
  height: 30px;
  margin: 0 5px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #40a0ffb4;
  border-radius: 50%;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.1);
  color: #ffffff;
}
.tools {
  position: fixed;
  top: 10px;
  right: 10px;
  display: flex;
  width: 500px;
  justify-content: flex-end;
}
.bgTheme {
  width: 100%;
  min-height: 150px;
  box-sizing: border-box;
  padding: 15px;
}
.themeColor input {
  width: 100%;
  outline: none;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  box-sizing: border-box;
  padding: 0px 8px;
  height: 25px;
  color: #909399;
  font-size: 14px;
}
.colorExp {
  display: flex;
  margin-bottom: 10px;
}
.colorExp span {
  display: block;
  height: 30px;
  width: 50%;
  text-align: center;
  line-height: 30px;
  color: #ffffff;
  font-size: 12px;
  cursor: pointer;
}
.themeButton {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}
.themeButton button {
  width: 45%;
  height: 30px;
  padding: 0;
  font-size: 12px;
}
</style>
