<template>
  <div class="container my-5">
    <h1>New Trip</h1>
    <form @submit.prevent>
      <div class="row mb-3">
        <div class="col col-4">

          <label for="nameInput" class="form-label">Nome</label>
          <input type="text" class="form-control" id="nameInput" v-model="trip.name">
        </div>
        <div class="col col-4">

          <label for="dateInput" class="form-label">Data</label>
          <input type="date" class="form-control" id="dateInput" v-model="selectedDate">
        </div>
        <div class="col col-4">
          <div class="position-relative">
            <label for="typeSelect" class="form-label">Mezzo utilizzato</label>
            <Combobox v-model="trip.transportType">
              <ComboboxButton class="bg-transparent border-0 p-0 w-100">
                <ComboboxInput id="typeSelect" type="text" class="form-select"
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
                v-for="(point,index) in trip.geopoints">
              <div class="col-auto">
                <MapPinIcon style="width: 1.5rem" class="text-danger"></MapPinIcon>
              </div>
              <div class="col px-2"><input type="text" name=""
                                           class="border-top-0 border-start-0 border-end-0 ms-1 form-control rounded-0 p-0"
                                           style="border-bottom-style: dotted;" :placeholder="`Pin ${++index} ✏️`"
                                           v-model="point.label">
              </div>
              <div class="col-auto"><input type="datetime-local" name=""
                                           class="badge bg-light rounded-pill text-bg-light d-block border-0"
                                           :value="millisTimestampToDateTime(point.recordedAt)"
                                           @input="point.recordedAt=DateTimeToMillisTimestamp($event.target.value)">
              </div>
            </li>
          </ul>
          <ul class="list-group" v-if="trip.geopoints.length===0">
            <li class="list-group-item d-flex justify-content-between align-items-center text-muted py-3">
              Seleziona un punto sulla mappa per iniziare
            </li>
          </ul>
        </div>
      </div>
      <button type="submit" class="btn btn-primary" @click.prevent="postNewTrip()" :disabled="isLoading">
        Aggiungi!
      </button>
    </form>
  </div>
</template>

<script setup>

import {computed, onBeforeMount, onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {DateTime} from "luxon";
import {MapPinIcon} from "@heroicons/vue/24/outline"
import {
  Combobox,
  ComboboxInput,
  ComboboxOptions,
  ComboboxOption,
  ComboboxButton,
} from '@headlessui/vue'
import axios from "axios";
import router from "../router";

const commonTransportTypes = [
  'Macchina',
  'Bicicletta',
  'Piedi',
  'Autobus',
  'Treno',
  'Aereo',
  'Nave'
]

let map = null;
let polyline = null;

const transportTypeQuery = ref('');
const isLoading = ref(false);

const route = useRoute();
const trip = ref({
  name: '',
  date: route.query.date ? DateTime.fromISO(route.query.date).toMillis() : DateTime.now().toMillis(),
  transportType: '',
  notes: '',
  geopoints: []
});


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

function millisTimestampToDateTime(millis) {
  return DateTime.fromMillis(millis).toFormat("yyyy-MM-dd'T'T")
  // return "2017-04-20T11:32";
}

function DateTimeToMillisTimestamp(dateTimeString) {
  return DateTime.fromISO(dateTimeString).toMillis();
}

onBeforeMount(() => {
  if (route.params.trip_id) {
    axios.get(`${import.meta.env.VITE_API_URL}/trips/${route.params.trip_id}`)
        .then(response => {
          trip.value = response.data;
          drawMapPath();
        });
  }
});

onMounted(() => {
  map = L.map('map').setView([51.505, -0.09], 13);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
  }).addTo(map);

  map.on('click', onMapClick);
});

function onMapClick(e) {
  trip.value.geopoints.push({
    latitude: e.latlng.lat,
    longitude: e.latlng.lng,
    recordedAt: DateTime.now().toMillis(),
    label: ''
  });
  drawMapPath();
}

function drawMapPath() {
  if (polyline !== null) {
    polyline.removeFrom(map);
  }
  const latlngs = trip.value.geopoints
      .map(g => [g.latitude, g.longitude])
  polyline = L.polyline(latlngs, {color: 'red'}).addTo(map);
}

function postNewTrip() {
  isLoading.value = true;
  return axios.post(import.meta.env.VITE_API_URL + '/trips', {
    ...trip.value,
    geopoints: undefined
  })
      .then(async response => {
        await trip.value.geopoints.forEach(async p =>
            await axios.post(`${import.meta.env.VITE_API_URL}/trips/${response.data.id}/geopoints`, p)
        );
        await router.push({name: 'trip_detail', params: {trip_id: response.data.id}})
        isLoading.value = false;
      })
}

</script>
