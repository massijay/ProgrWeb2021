import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import 'bootstrap'
import 'leaflet'

import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/main.css'
import 'leaflet/dist/leaflet.css'

// https://stackoverflow.com/questions/65547199/using-bootstrap-5-with-vue-3

const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)

app.mount('#app')
