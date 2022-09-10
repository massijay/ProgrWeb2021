<template>
  <div class="container my-5">
    <div class="text-center" v-show="route.params.trip_id && !trip.id">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Caricamento...</span>
      </div>
    </div>
    <div :class="(!route.params.trip_id || trip.id) ? '' : 'opacity-0'">
      <div class="row">
        <div class="col">
          <h6>
            <span>{{ DateTime.fromMillis(trip.date).toLocaleString(DateTime.DATE_FULL) }}</span>
            <span class="badge text-bg-light fs-6 text-uppercase ms-1">{{ trip.transportType }}</span>
          </h6>
          <h1>{{ trip.name }}</h1>
          <p class="text-muted">{{ trip.notes }}</p>
        </div>
        <div class="col-auto">
          <RouterLink class="btn btn-primary" :to="{name: 'trip_edit', param: {trip_id: trip.id }}">Modifica</RouterLink>
        </div>
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
              <div class="col-auto">
                <MapPinIcon style="height: 1.5rem" class="text-danger"></MapPinIcon>
              </div>
              <div class="col px-2">
                <p class="mb-0" :class="point.label ? '' : 'text-muted'">
                  {{ point.label ? point.label : `Tappa ${index + 1}` }}</p>
              </div>
              <div class="col-auto">
                <p class="badge bg-light rounded-pill text-bg-light d-block border-0 mb-0">
                  {{ DateTime.fromMillis(point.recordedAt).toLocaleString(DateTime.DATETIME_SHORT) }}
                </p>
              </div>
            </li>
          </ul>
          <ul class="list-group" v-if="trip.geopoints.length===0">
            <li class="list-group-item d-flex justify-content-between align-items-center text-muted py-3">
              Nessuna tappa aggiunta al viaggio
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {DateTime} from "luxon";
import {MapPinIcon} from "@heroicons/vue/24/outline"
import {useRoute} from "vue-router";
import {useTripMap} from "../composables/tripMap";
import {onMounted} from "vue";

const route = useRoute();

const {
  map,
  trip,
  initMap,
  getAndDrawTripOnMap,
  addMarker,
  clearMarker,
} = useTripMap();

onMounted(() => {
  initMap(true);
  getAndDrawTripOnMap(route.params.trip_id);
});
</script>
