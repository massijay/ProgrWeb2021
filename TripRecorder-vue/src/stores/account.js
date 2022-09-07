import {defineStore} from 'pinia';
import {computed, ref} from 'vue';
import axios from 'axios';

export const useAccountStore = defineStore('account', () => {
    const apiKey = ref(null);

    const isAuthenticated = computed(() => apiKey.value !== null);

    function login(username, password) {
        return axios.post(import.meta.env.VITE_API_URL + '/auth/login',
            {
                'username': username,
                'password': password
            })
            .then(response => {
                apiKey.value = response.data['token'];
            });
    }

    return {apiKey, isAuthenticated, login};

})