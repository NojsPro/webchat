import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user:{},
    current:0,
  },
  mutations: {
    userlogin(state,info){
      state.user=info;
      state.current = 1;
    },
    userloginout(state){
      state.user={};
      state.current = 0;
    }
  },
  actions: {

  }
})
