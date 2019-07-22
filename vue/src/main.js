import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false;
// Vue.use(new VueSocketIO({
//   connection: 'http://localhost:7001', //Optional options
//   options: { path: "/queueServer/" },
  // options: {

  //   // 实际使用中可以在这里传递参数
  //   query: {
  //     room: 'demo',
  //     userId: `client_${Math.random()}`,
  //   },

  //   transports: ['websocket']
  // },
  // vuex: {
  //   store,
  //   actionPrefix: "SOCKET_",
  //   mutationPrefix: "SOCKET_"
  // }
// }))
Vue.use(ElementUI);
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')