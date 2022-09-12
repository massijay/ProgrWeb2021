<template>
  <div class="container my-5">
    <div class="row justify-content-between align-items-center my-3">
      <div class="col-auto"><h1 class="mb-2 mb-md-0">Trips</h1></div>
      <div class="col-12 col-md-auto d-flex">
        <div class="input-group me-2">
          <input type="date" class="form-control" aria-label="Filtra per data" v-model="dateFilter">
          <button class="btn btn-outline-secondary" type="button" v-if="dateFilter!==''" @click="dateFilter=''">
            Reset
          </button>
        </div>
        <RouterLink class="btn btn-primary" :to="{name: 'trip_create', query:{date: dateFilter}}">
          Aggiungi
        </RouterLink>
      </div>
    </div>

    <div class="list-group">
      <RouterLink :to="{name: 'trip_detail',params: {trip_id: trip.id}}" class="list-group-item list-group-item-action"
                  v-for="trip in trips">
        <div class="d-flex w-100 justify-content-between">
          <h5 class="mb-1">{{ trip.name }}</h5>
          <small><span class="badge text-bg-light text-uppercase">{{ trip.transportType }}</span></small>
        </div>
        <p class="mb-1 text-muted">{{ trip.notes }}</p>
        <small>{{ DateTime.fromMillis(trip.date).toLocaleString(DateTime.DATE_FULL) }}</small>
      </RouterLink>
      <RouterLink :to="{name: 'trip_create', query:{date: dateFilter}}"
                  class="list-group-item list-group-item-action"
                  v-if="trips.length === 0 && !isLoading">
        <p class="mb-1 text-center text-muted">Non sono stati trovati viaggi,
          <span class="text-decoration-underline text-primary">aggiungine uno!</span>
        </p>
      </RouterLink>
      <a href="#" class="list-group-item list-group-item-action disabled" v-if="isLoading">
        <p class="mb-1 text-center text-muted">Caricamento...</p>
      </a>
    </div>
    <p class="mb-1 text-center text-muted mt-3" v-if="!isLoading && !dateFilter && trips.length">
      <InformationCircleIcon style="width: 1rem;" class="me-1 mb-1"></InformationCircleIcon>
      Stai visualizzando gli utlimi 10 viaggi, seleziona una data per filtrarli
    </p>
  </div>
</template>

<script setup>
import {onBeforeMount, ref, watch} from "vue";
import axios from "axios";
import {DateTime} from "luxon";
import {InformationCircleIcon} from '@heroicons/vue/24/outline'

const dateFilter = ref('');
const trips = ref([]);
const isLoading = ref(true);

onBeforeMount(() => {
  loadTrips('');
})

watch(dateFilter, () => {
  loadTrips(dateFilter.value !== '' ? `?date=${dateFilter.value}` : '');
});

async function loadTrips(filters) {
  isLoading.value = true;
  trips.value = [];
  trips.value = (await axios.get(`${import.meta.env.VITE_API_URL}/trips${filters}`)).data;
  isLoading.value = false;
}

</script>

