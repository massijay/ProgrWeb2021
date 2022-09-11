<template>
  <div class="container my-5" style="max-width: 320px;">
    <h1>Il tuo profilo</h1>
    <div class="alert alert-danger" role="alert" v-if="error!==''">
      {{ error }}
    </div>
    <div class="alert alert-success" role="alert" v-if="success!==''">
      {{ success }}
    </div>
    <form @submit.prevent="updateProfile">
      <div class="mb-3">
        <label for="username" class="form-label">Username</label>
        <input type="text" required class="form-control" id="username"
               v-model="userAccount.username">
      </div>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" required class="form-control" id="email"
               v-model="userAccount.email">
      </div>
      <div class="form-check mb-2">
        <input class="form-check-input" type="checkbox" value="" id="passwordCheckbox" v-model="passwordChange">
        <label class="form-check-label" for="passwordCheckbox">
          Vuoi modificare la password?
        </label>
      </div>
      <div class="mb-3" v-if="passwordChange">
        <label for="password" class="form-label">Password</label>
        <input type="password" required class="form-control" id="password" v-model="userAccount.password">
      </div>
      <div class="mb-3" v-if="passwordChange">
        <label for="confirm_password" class="form-label">Conferma password</label>
        <input type="password" required class="form-control" id="confirm_password" v-model="confirmPassword">
      </div>
      <div class="row justify-content-end align-items-center">
        <div class="col-auto">
          <button type="submit" class="btn btn-primary" :disabled="loading">Aggiorna</button>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import {ref, watch} from "vue";
import {useAccountStore} from "../stores/account";

const confirmPassword = ref('');
const loading = ref(false);
const error = ref('');
const success = ref('');
const accountStore = useAccountStore();
const passwordChange = ref(false);
const userAccount = ref(accountStore.user ? {...accountStore.user} : {
  username: '',
  email: '',
  password: ''
});

watch(accountStore, (state) => {
  userAccount.value = {...state.user};
})

async function updateProfile() {
  if (passwordChange.value && userAccount.value.password !== confirmPassword.value) {
    error.value = 'Le password non coincidono';
    return;
  }
  loading.value = true;
  error.value = '';
  success.value = '';

  accountStore.updateUserData({
    username: userAccount.value.username,
    email: userAccount.value.email,
    password: passwordChange.value ? userAccount.value.password : undefined
  })
      .then(() => {
        loading.value = false;
        success.value = 'Profilo aggiornato con successo!';
        userAccount.value.password = '';
        confirmPassword.value = '';
        passwordChange.value = false;
        setTimeout(() => {
          success.value = '';
        }, 2000);
      })
      .catch((err) => {
        loading.value = false;
        switch (err.response.status) {
          case 422:
            if (err.response.data?.errorCode === 1) {
              error.value = 'Username già utilizzato';
            } else if (err.response.data?.errorCode === 2) {
              error.value = 'Indirizzo email già utilizzato';
            } else if (err.response.data?.errorCode === 3) {
              error.value = 'Indirizzo email non valido';
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
