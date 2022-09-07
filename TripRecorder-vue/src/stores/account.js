import {defineStore} from 'pinia';
import {computed, ref} from 'vue';
import axios from 'axios';
import router from "../router";

export const useAccountStore = defineStore('account', () => {
    const apiKey = ref(null);
    const user = ref(null);
    let axiosInterceptor = null;

    const isAuthenticated = computed(() => {
        if (apiKey.value !== null) {
            return true;
        }
        const api = localStorage.getItem('apiKey');
        if (api !== null) {
            apiKey.value = api;
            setupAxios();
            return true;
        }
        return false;
    });

    function getUserData() {
        return axios.get(import.meta.env.VITE_API_URL + '/profile')
            .then(response => user.value = response.data)
            .catch(err => console.log("Impossibile caricare i dati dell'utente", err));
    }

    function login(username, password) {
        return axios.post(import.meta.env.VITE_API_URL + '/auth/login',
            {
                'username': username,
                'password': password
            })
            .then(response => {
                apiKey.value = response.data['token'];
                setupAxios();
                localStorage.setItem("apiKey", apiKey.value);
            });
    }

    function logout() {
        return axios.post(import.meta.env.VITE_API_URL + '/auth/logout', {})
            .then(response => {
                clearSessionData();
                router.push({name: 'login'}).then(() => {
                });
            })
            .catch(err => {
                clearSessionData()
                console.log("Impossibile contattare il server", err)
                router.push({name: 'login'}).then(() => {
                });
            });
    }

    function clearSessionData() {
        apiKey.value = null;
        localStorage.removeItem('apiKey');
        resetAxios();
    }

    function setupAxios() {
        axios.defaults.headers.common['Authorization'] = `Bearer ${apiKey.value}`;
        if (axiosInterceptor === null) {
            axiosInterceptor = axios.interceptors.response.use(function (response) {
                // Any status code that lie within the range of 2xx cause this function to trigger
                // Do something with response data
                return response;
            }, function (error) {
                if (error.response.status === 401) {
                    clearSessionData();
                    router.push({name: 'login'}).then(() => {
                    });
                    //https://github.com/axios/axios/issues/583#issuecomment-504317347
                    return new Promise(() => {
                    });
                }
                return Promise.reject(error);
            });
        }
    }

    function resetAxios() {
        delete axios.defaults.headers.common['Authorization'];
        if (axiosInterceptor !== null) {
            axios.interceptors.response.eject(axiosInterceptor);
            axiosInterceptor = null;
        }
    }

    return {isAuthenticated, user, getUserData, login, logout};

})