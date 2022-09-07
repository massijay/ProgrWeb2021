<template>
  <div class="container my-5" style="max-width: 320px;">
    <h1>Login</h1>
    <div class="alert alert-danger" role="alert" v-if="error!==''">
      {{ error }}
    </div>
    <form @submit.prevent="login">
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input type="text" required autofocus class="form-control" id="username" v-model="username">
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input type="password" required class="form-control" id="password" v-model="password">
      </div>
      <button type="submit" class="btn btn-primary" :disabled="loading">Login</button>
    </form>
  </div>
</template>

<script setup>
import {ref} from "vue";
import {useAccountStore} from "../stores/account";
import router from "../router";

const username = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');
const accountStore = useAccountStore();

async function login() {
  loading.value = true;
  error.value = '';
  accountStore.login(username.value, password.value)
      .then(() => {
        router.push({name: 'home'})
      })
      .catch((err) => {
        loading.value = false;
        switch (err.response.status) {
          case 401:
            error.value = 'Credenziali errate';
            break;
          default:
            console.log("Errore nel login", err);
            error.value = 'Errore, riprova più tardi';
            break;
        }
      });
}
</script>
