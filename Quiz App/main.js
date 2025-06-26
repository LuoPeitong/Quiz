import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import 'uni.promisify.adaptor'
import uView from 'uview-ui';
import './uni.scss'
Vue.use(uView);
Vue.config.productionTip = false

// 服务器地址
//Vue.prototype.$baseUrl = "http://8.138.216.22:8088/quizAPI/"
// 本地地址
Vue.prototype.$baseUrl = "http://localhost:8088/quizAPI_war_exploded/"


App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif