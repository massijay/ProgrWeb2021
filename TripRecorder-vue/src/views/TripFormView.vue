<template>
  <div class="container my-5">
    <div class="text-center" v-show="(route.params.trip_id && !trip.id) || isLoading">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Caricamento...</span>
      </div>
    </div>
    <form @submit.prevent="route.params.trip_id ? patchExistingTrip() :postNewTrip()"
          :class="(!route.params.trip_id || trip.id || isLoading) ? '' : 'opacity-0'">
      <div class="row align-items-center mb-3">
        <div class="col">
          <h1 class="mb-0">{{ route.params.trip_id ? 'Modifica viaggio' : 'Nuovo viaggio' }}</h1>
        </div>
        <div class="col-auto">
          <button type="submit" class="btn btn-primary"
                  :disabled="isLoading">
            {{ route.params.trip_id ? 'Aggiorna' : 'Aggiungi' }}
          </button>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col col-4">
          <label for="nameInput" class="form-label">Nome</label>
          <input type="text" required class="form-control" id="nameInput" v-model="trip.name">
        </div>
        <div class="col col-4">

          <label for="dateInput" class="form-label">Data</label>
          <input type="date" required class="form-control" id="dateInput" v-model="selectedDate">
        </div>
        <div class="col col-4">
          <div class="position-relative">
            <label for="typeSelect" class="form-label">Mezzo utilizzato</label>
            <Combobox v-model="trip.transportType">
              <ComboboxButton class="bg-transparent border-0 p-0 w-100">
                <ComboboxInput id="typeSelect" type="text" required class="form-select"
                               @change="transportTypeQuery = $event.target.value"/>
              </ComboboxButton>
              <ComboboxOptions class="bg-white border list-unstyled position-absolute rounded w-100 overflow-hidden"
                               id="combo-list"
                               style="z-index: 10">
                <ComboboxOption v-if="transportTypeQuery" :value="transportTypeQuery" class="border-bottom px-3 py-2">
                  {{ transportTypeQuery }}
                </ComboboxOption>
                <ComboboxOption class="border-bottom px-3 py-2"
                                v-for="type in filteredTransportTypes"
                                :key="type" :value="type">
                  {{ type }}
                </ComboboxOption>
              </ComboboxOptions>
            </Combobox>
          </div>
        </div>
      </div>
      <div class="mb-3">
        <label for="notesTextarea" class="form-label">Note</label>
        <textarea class="form-control" id="notesTextarea" rows="3" v-model="trip.notes"></textarea>
      </div>
      <div class="row my-3">
        <div class="col">

          <div id="map" style="height: 400px; position: relative;"
               class="leaflet-container leaflet-touch leaflet-retina leaflet-fade-anim leaflet-grab leaflet-touch-drag leaflet-touch-zoom"
               tabindex="0">
          </div>
        </div>
        <div class="col-12 col-md-4">
          <ul class="list-group">
            <li class="list-group-item d-flex align-items-center justify-content-between"
                v-for="(point,index) in trip.geopoints" style="min-height: 3rem;"
                @mouseenter="addMarker(point)" @mouseleave="clearMarker()">
              <div class="col-auto toggle-hover">
                <MapPinIcon style="height: 1.5rem" class="text-danger toggle-hover-hide"></MapPinIcon>
                <TrashIcon style="height: 1.5rem" class="text-danger toggle-hover-show cursor-pointer"
                           @click="removeGeopoint(index)"></TrashIcon>
              </div>
              <div class="col px-2"><input type="text" name=""
                                           class="border-top-0 border-start-0 border-end-0 ms-1 form-control rounded-0 p-0"
                                           style="border-bottom-style: dotted;" :placeholder="`Tappa ${index+1} ✏️`"
                                           v-model="point.label">
              </div>
              <div class="col-auto"><input type="datetime-local" name=""
                                           class="badge bg-light rounded-pill text-bg-light d-block border-0"
                                           @change="sortGeopointsbyTime()"
                                           :value="millisTimestampToDateTime(point.recordedAt)"
                                           @input="point.recordedAt=DateTimeToMillisTimestamp($event.target.value)">
              </div>
            </li>
          </ul>
          <ul class="list-group" v-if="trip.geopoints.length===0">
            <li class="list-group-item d-flex justify-content-between align-items-center text-muted py-3">
              Seleziona un punto sulla mappa per aggiungere una tappa
            </li>
          </ul>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>

import {computed, onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {DateTime} from "luxon";
import {MapPinIcon, TrashIcon} from "@heroicons/vue/24/outline"
import {
  Combobox,
  ComboboxInput,
  ComboboxOptions,
  ComboboxOption,
  ComboboxButton,
} from '@headlessui/vue'
import axios from "axios";
import router from "../router";
import {useTripMap} from "../composables/tripMap";

const {
  map,
  trip,
  initMap,
  getAndDrawTripOnMap,
  millisTimestampToDateTime,
  drawMapPath,
  addMarker,
  clearMarker,
} = useTripMap();

const commonTransportTypes = [
  'Macchina',
  'Bicicletta',
  'Camminata',
  'Autobus',
  'Treno',
  'Aereo',
  'Nave'
]

const transportTypeQuery = ref('');
const isLoading = ref(false);

const route = useRoute();

const filteredTransportTypes = computed(() =>
    transportTypeQuery.value === ''
        ? commonTransportTypes
        : commonTransportTypes.filter((type) => {
          return type.toLowerCase().includes(transportTypeQuery.value.toLowerCase())
        })
)

const selectedDate = computed({
  get: () => DateTime.fromMillis(trip.value.date).toISODate(),
  set: newValue => trip.value.date = DateTime.fromISO(newValue).toMillis()
})

function DateTimeToMillisTimestamp(dateTimeString) {
  return DateTime.fromISO(dateTimeString).toMillis();
}

onMounted(() => {
  initMap(true);
  if (route.params.trip_id) {
    getAndDrawTripOnMap(route.params.trip_id);
  }
});

function sortGeopointsbyTime() {
  trip.value.geopoints.sort((p1, p2) => p1.recordedAt - p2.recordedAt);
  drawMapPath();
}

function postNewTrip() {
  isLoading.value = true;
  return axios.post(`${import.meta.env.VITE_API_URL}/trips`, {
    ...trip.value,
    geopoints: undefined
  }).then(putGeopoints);
}

function patchExistingTrip() {
  isLoading.value = true;
  return axios.patch(`${import.meta.env.VITE_API_URL}/trips/${trip.value.id}`, {
    ...trip.value,
    geopoints: undefined
  }).then(putGeopoints);
}

async function putGeopoints(response) {
  await axios.put(`${import.meta.env.VITE_API_URL}/trips/${response.data.id}/geopoints`, trip.value.geopoints);
  await router.push({name: 'trip_detail', params: {trip_id: response.data.id}})
  isLoading.value = false;
}

function removeGeopoint(index) {
  trip.value.geopoints.splice(index, 1);
  drawMapPath();
}
</script>
