import {createRouter, createWebHistory} from 'vue-router'
import MainView from '../views/MainView.vue'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import {useAccountStore} from "../stores/account";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            beforeEnter: (to, from) =>
                useAccountStore().isAuthenticated ? true : {name: 'login'},
            name: 'main',
            component: MainView,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: HomeView
                }
            ]
        },
        {
            path: '/login',
            beforeEnter: (to, from) =>
                useAccountStore().isAuthenticated ? {name: 'home'} : true
            ,
            name: 'login',
            component: LoginView
        }
    ]
})

export default router
