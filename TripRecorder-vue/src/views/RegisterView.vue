<template>
  <div class="container my-5" style="max-width: 320px;">
    <h1>Registrati</h1>
    <div class="alert alert-danger" role="alert" v-if="error!==''">
      {{ error }}
    </div>
    <form @submit.prevent="register">
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input type="text" required autofocus class="form-control" id="username" v-model="userAccount.username">
      </div>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" required class="form-control" id="email" v-model="userAccount.email">
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input type="password" required class="form-control" id="password" v-model="userAccount.password">
      </div>
      <div class="mb-3">
        <label for="confirm_password" class="form-label">Conferma password</label>
        <input type="password" required class="form-control" id="confirm_password" v-model="confirmPassword">
      </div>
      <div class="row justify-content-between align-items-center">
        <div class="col-auto">
          <RouterLink :to="{name: 'login'}">Hai già un account?</RouterLink>
        </div>
        <div class="col-auto">
          <button type="submit" class="btn btn-primary" :disabled="loading">Registrati</button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import {ref} from "vue";
import {useAccountStore} from "../stores/account";
import router from "../router";

const userAccount = ref({
  username: '',
  email: '',
  password: ''
});
const confirmPassword = ref('');
const loading = ref(false);
const error = ref('');
const accountStore = useAccountStore();

async function register() {
  if (userAccount.value.password !== confirmPassword.value) {
    error.value = 'Le password non coincidono';
    return;
  }
  loading.value = true;
  error.value = '';

  accountStore.register(userAccount.value)
      .then(() => {
        router.push({name: 'login', query: {registered: userAccount.value.username}})
      })
      .catch((err) => {
        loading.value = false;
        switch (err.response.status) {
          case 422:
            if (err.response.data?.errorCode === 1) {
              error.value = 'Username già utilizzato';
            } else if (err.response.data?.errorCode === 2) {
              error.value = 'Indirizzo email già utilizzato';
            } else {
              error.value = 'Dati inseriti non validi';
            }

            break;
          default:
            console.log("Errore nella registrazione", err);
            error.value = 'Errore, riprova più tardi';
            break;
        }
      });
}
</script>
