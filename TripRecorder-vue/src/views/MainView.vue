<template>
  <nav class="navbar navbar-expand-lg bg-light">
    <div class="container">
      <RouterLink class="navbar-brand" :to="{name: 'home'}">✈️ Trip Recorder</RouterLink>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
              aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <RouterLink class="nav-link active" :class="router.hasRoute('trips') ? 'active' : ''" aria-current="page"
                        :to="{name: 'trips'}">
              Viaggi
            </RouterLink>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              Ciao {{ accountStore.user?.username }}
            </a>
            <ul class="dropdown-menu">
              <li>
                <RouterLink class="dropdown-item" :to="{name: 'profile'}">Profilo</RouterLink>
              </li>
              <li>
                <hr class="dropdown-divider">
              </li>
              <li><a class="dropdown-item" @click.prevent="logout()" href="#">Logout</a></li>
            </ul>
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
import router from "../router";

const accountStore = useAccountStore();
onBeforeMount(async () => {
  await accountStore.getUserData();
});

function logout() {
  accountStore.logout()
      .then(() => router.push({name: 'login'}));
}

</script>
