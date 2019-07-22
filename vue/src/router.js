import Vue from 'vue'
import Router from 'vue-router'
import Login from './views/Login.vue'
import Sockets from './views/Socket.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Sockets
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    }
  ]
})
