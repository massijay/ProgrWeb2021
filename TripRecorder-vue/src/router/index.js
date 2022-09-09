import {createRouter, createWebHistory} from 'vue-router'
import MainView from '../views/MainView.vue'
import TripsView from '../views/TripsView.vue'
import LoginView from '../views/LoginView.vue'
import TripDetailView from '../views/TripDetailView.vue'
import TripFormView from '../views/TripFormView.vue'
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
                    redirect: {name: 'trips'}
                },
                {
                    path: 'trips',
                    name: 'trips',
                    component: TripsView
                },
                {
                    path: 'trips/:trip_id',
                    name: 'trip_detail',
                    component: TripDetailView
                },
                {
                    path: 'trips/new',
                    name: 'trip_create',
                    component: TripFormView
                },
                {
                    path: 'trips/:trip_id/edit',
                    name: 'trip_edit',
                    component: TripFormView
                }
            ]
        },
        {
            path: '/login',
            beforeEnter: (to, from) =>
                useAccountStore().isAuthenticated ? {name: 'trips'} : true
            ,
            name: 'login',
            component: LoginView
        }
    ]
})

export default router
