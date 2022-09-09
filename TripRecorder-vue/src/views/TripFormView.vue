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

          <label for="typeSelect" class="form-label">Mezzo utilizzato</label>
          <select class="form-select" aria-label="Mezzo utilizzato" id="typeSelect" v-model="trip.transportType">
            <option selected value="walk">Walk</option>
            <option value="bike">Bike</option>
            <option value="bus">Bus</option>
            <option value="train">Train</option>
            <option value="">Other</option>
          </select>
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
            <div class="leaflet-pane leaflet-map-pane" style="transform: translate3d(-190px, 0px, 0px);">
              <div class="leaflet-pane leaflet-tile-pane">
                <div class="leaflet-layer " style="z-index: 1; opacity: 1;">
                  <div class="leaflet-tile-container leaflet-zoom-animated"
                       style="z-index: 18; transform: translate3d(299px, 135px, 0px) scale(0.5);"></div>
                  <div class="leaflet-tile-container leaflet-zoom-animated"
                       style="z-index: 19; transform: translate3d(214px, 77px, 0px) scale(1);"><img alt=""
                                                                                                    role="presentation"
                                                                                                    src="https://b.tile.openstreetmap.org/6/32/20.png"
                                                                                                    class="leaflet-tile leaflet-tile-loaded"
                                                                                                    style="width: 256px; height: 256px; transform: translate3d(175px, -214px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://c.tile.openstreetmap.org/6/32/21.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(175px, 42px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://a.tile.openstreetmap.org/6/31/20.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(-81px, -214px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://c.tile.openstreetmap.org/6/33/20.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(431px, -214px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://b.tile.openstreetmap.org/6/31/21.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(-81px, 42px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://a.tile.openstreetmap.org/6/33/21.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(431px, 42px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://a.tile.openstreetmap.org/6/32/22.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(175px, 298px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://b.tile.openstreetmap.org/6/33/22.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(431px, 298px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://b.tile.openstreetmap.org/6/34/21.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(687px, 42px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://a.tile.openstreetmap.org/6/34/20.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(687px, -214px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://c.tile.openstreetmap.org/6/31/22.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(-81px, 298px, 0px); opacity: 1;"><img
                      alt="" role="presentation" src="https://c.tile.openstreetmap.org/6/34/22.png"
                      class="leaflet-tile leaflet-tile-loaded"
                      style="width: 256px; height: 256px; transform: translate3d(687px, 298px, 0px); opacity: 1;"></div>
                </div>
              </div>
              <div class="leaflet-pane leaflet-overlay-pane"></div>
              <div class="leaflet-pane leaflet-shadow-pane"></div>
              <div class="leaflet-pane leaflet-marker-pane"></div>
              <div class="leaflet-pane leaflet-tooltip-pane"></div>
              <div class="leaflet-pane leaflet-popup-pane">
                <div class="leaflet-popup  leaflet-zoom-animated"
                     style="opacity: 1; transform: translate3d(385px, 190px, 0px); bottom: -7px; left: -171px;">
                  <div class="leaflet-popup-content-wrapper">
                    <div class="leaflet-popup-content" style="width: 296px;">You clicked the map at LatLng(51.5159,
                      -0.098619)
                    </div>
                  </div>
                  <div class="leaflet-popup-tip-container">
                    <div class="leaflet-popup-tip"></div>
                  </div>
                  <a class="leaflet-popup-close-button" role="button" aria-label="Close popup" href="#close"><span
                      aria-hidden="true">×</span></a></div>
              </div>
              <div class="leaflet-proxy leaflet-zoom-animated"
                   style="transform: translate3d(8361px, 5457px, 0px) scale(32);"></div>
            </div>
            <div class="leaflet-control-container">
              <div class="leaflet-top leaflet-left">
                <div class="leaflet-control-zoom leaflet-bar leaflet-control"><a class="leaflet-control-zoom-in"
                                                                                 href="#" title="Zoom in" role="button"
                                                                                 aria-label="Zoom in"
                                                                                 aria-disabled="false"><span
                    aria-hidden="true">+</span></a><a class="leaflet-control-zoom-out" href="#" title="Zoom out"
                                                      role="button" aria-label="Zoom out" aria-disabled="false"><span
                    aria-hidden="true">−</span></a></div>
              </div>
              <div class="leaflet-top leaflet-right"></div>
              <div class="leaflet-bottom leaflet-left"></div>
              <div class="leaflet-bottom leaflet-right">
                <div class="leaflet-control-attribution leaflet-control"><a href="https://leafletjs.com"
                                                                            title="A JavaScript library for interactive maps">
                  <svg aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="12" height="8">
                    <path fill="#4C7BE1" d="M0 0h12v4H0z"></path>
                    <path fill="#FFD500" d="M0 4h12v3H0z"></path>
                    <path fill="#E0BC00" d="M0 7h12v1H0z"></path>
                  </svg>
                  Leaflet</a> <span aria-hidden="true">|</span> © OpenStreetMap
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-12 col-md-4">
          <ul class="list-group">
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" style="width: 1.2rem; height: 1.2rem;">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z"></path>
              </svg>
              <input type="text" name="" class="border-0 ms-1 me-auto w-50" placeholder="Pin 1 ✏️" value="Casa">
              <input type="datetime-local" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="text-warning"
                   style="width: 1.2rem; height: 1.2rem;">
                <path fill-rule="evenodd"
                      d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                      clip-rule="evenodd"></path>
              </svg>

              <input type="text" name="" class="border-0 ms-1 me-auto" placeholder="Pin 2 ✏️">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" style="width: 1.2rem; height: 1.2rem;">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z"></path>
              </svg>
              <input type="text" name="" class="border-0 ms-1 me-auto" placeholder="Pin 3 ✏️" value="Stazione">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center text-muted">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" style="width: 1.2rem; height: 1.2rem;">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z"></path>
              </svg>
              <input type="text" name="" class="border-0 ms-1 me-auto" placeholder="Pin 4 ✏️">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
          </ul>
          <ul class="list-group  my-3">
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="text-warning"
                   style="width: 1.2rem; height: 1.2rem;">
                <path fill-rule="evenodd"
                      d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                      clip-rule="evenodd"></path>
              </svg>
              <input type="text" name="" class="border-top-0 border-start-0 border-end-0 ms-1 me-auto"
                     style="border-bottom-style: dotted;" placeholder="Pin 1 ✏️" value="Casa">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" style="width: 1.2rem; height: 1.2rem;">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z"></path>
              </svg>
              <input type="text" name="" class="border-top-0 border-start-0 border-end-0 ms-1 me-auto"
                     style="border-bottom-style: dotted;" placeholder="Pin 2 ✏️">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5"
                   stroke="currentColor" style="width: 1.2rem; height: 1.2rem;">
                <path stroke-linecap="round" stroke-linejoin="round"
                      d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z"></path>
              </svg>
              <input type="text" name="" class="border-top-0 border-start-0 border-end-0 ms-1 me-auto"
                     style="border-bottom-style: dotted;" placeholder="Pin 3 ✏️" value="Stazione">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
            <li class="list-group-item d-flex align-items-center">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="text-warning"
                   style="width: 1.2rem; height: 1.2rem;">
                <path fill-rule="evenodd"
                      d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.007 5.404.433c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.433 2.082-5.006z"
                      clip-rule="evenodd"></path>
              </svg>
              <input type="text" name="" class="border-top-0 border-start-0 border-end-0 ms-1 me-auto"
                     style="border-bottom-style: dotted;" placeholder="Pin 4 ✏️">
              <input type="time" name="" class="badge bg-light rounded-pill text-bg-light d-block border-0"
                     value="14:00">
            </li>
          </ul>
          <ul class="list-group">
            <li class="list-group-item d-flex justify-content-between align-items-center text-muted py-3">
              Seleziona un punto sulla mappa per iniziare
            </li>
          </ul>
        </div>
      </div>
      <button type="submit" class="btn btn-primary">Create</button>
    </form>
  </div>
</template>

<script setup>

import {computed, onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {DateTime} from "luxon";

const route = useRoute();
const trip = ref({
  name: '',
  date: route.query.date ? DateTime.fromISO(route.query.date).toMillis() : DateTime.now().toMillis(),
  transportType: '',
  notes: '',
  geopoints: []
});

const selectedDate = computed({
  get: () => DateTime.fromMillis(trip.value.date).toISODate(),
  set: newValue => trip.value.date = DateTime.fromISO(newValue).toMillis()
})

function millisTimestampToDateTime(millis){
  return DateTime.fromMillis(trip.value.date).toISO();
}

function DateTimeToMillisTimestamp(dateTimeString){
  return DateTime.fromISO(dateTimeString).toMillis();
}

onMounted(() => {
  const map = L.map('map').setView([51.505, -0.09], 13);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
  }).addTo(map);
  const popup = L.popup();

  function onMapClick(e) {
    popup
        .setLatLng(e.latlng)
        .setContent("You clicked the map at " + e.latlng.toString())
        .openOn(map);
  }

  map.on('click', onMapClick);
});


</script>
