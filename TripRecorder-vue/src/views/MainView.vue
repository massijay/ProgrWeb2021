<template>
  <nav class="navbar navbar-expand-lg bg-light">
    <div class="container-fluid">
      <RouterLink class="navbar-brand" to="/">✈️ Trip Recorder</RouterLink>
      <!--      <a class="navbar-brand" href="#">Navbar w/ text</a>-->
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
              aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <RouterLink class="nav-link active" aria-current="page" to="/">Home</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/login">Login</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/">Pricing</RouterLink>
          </li>
        </ul>
        <span class="navbar-text">
          Ciao {{ accountStore.user?.username }}
      </span>
        <ul class="navbar-nav mb-2 mb-lg-0">
          <li class="nav-item">
            <button class="nav-link" @click="accountStore.logout()">Logout</button>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <RouterView/>
</template>

<script setup>
import {onBeforeMount} from 'vue'
import {useAccountStore} from "../stores/account";

const accountStore = useAccountStore();
onBeforeMount(() => {
  accountStore.getUserData();
});

</script>
