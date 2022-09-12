import axios from "axios";
import {ref} from "vue";
import {DateTime} from "luxon";
import {useRoute} from "vue-router/dist/vue-router";
import router from "../router";
import markerX2Icon from 'leaflet/dist/images/marker-icon-2x.png'
import markerIcon from 'leaflet/dist/images/marker-icon.png'
import markerShadow from 'leaflet/dist/images/marker-shadow.png'

export function useTripMap() {

    const route = useRoute();

    let map = null;
    let polyline = null;
    let currentMarker = null;

    const trip = ref({
        name: '',
        date: route.query.date ? DateTime.fromISO(route.query.date).toMillis() : DateTime.now().toMillis(),
        transportType: '',
        notes: '',
        geopoints: []
    });

    function initMap(enableMapClick = false) {
        // Fix leaflet bug with Vue not displaying marker icon in production build
        // https://stackoverflow.com/a/58254190/11583146
        // https://vitejs.dev/guide/features.html#static-assets
        delete L.Icon.Default.prototype._getIconUrl;
        L.Icon.Default.mergeOptions({
            iconRetinaUrl: markerX2Icon,
            iconUrl: markerIcon,
            shadowUrl: markerShadow
        });

        map = L.map('map').setView([45.659695, 13.794748], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap'
        }).addTo(map);
        if (enableMapClick) map.on('click', onMapClick);
    }

    function onMapClick(e) {
        trip.value.geopoints.push({
            latitude: e.latlng.lat,
            longitude: e.latlng.lng,
            recordedAt: DateTime.now().toMillis(),
            label: ''
        });
        drawMapPath();
    }

    function getAndDrawTripOnMap(tripId) {
        axios.get(`${import.meta.env.VITE_API_URL}/trips/${tripId}`)
            .then(response => {
                trip.value = response.data;
                drawMapPath(true);
            })
            .catch(err => {
                alert('Viaggio non trovato');
                router.push({name: 'trips'});
            });
    }

    function millisTimestampToDateTime(millis) {
        return DateTime.fromMillis(millis).toFormat("yyyy-MM-dd'T'T")
        // return "2017-04-20T11:32";
    }

    function drawMapPath(zoomMap = false) {
        if (polyline !== null) {
            polyline.removeFrom(map);
        }
        clearMarker();
        if (trip.value.geopoints.length > 1) {
            const latlngs = trip.value.geopoints
                .map(g => [g.latitude, g.longitude])
            polyline = L.polyline(latlngs, {color: 'red'}).addTo(map);
            if (zoomMap) {
                map.fitBounds(polyline.getBounds());
            }
        } else if (trip.value.geopoints.length === 1) {
            addMarker(trip.value.geopoints[0]);
            if (zoomMap) {
                map.setView([trip.value.geopoints[0].latitude, trip.value.geopoints[0].longitude], 12);
            }
        }
    }

    function addMarker(geopoint) {
        clearMarker();
        currentMarker = L.marker([geopoint.latitude, geopoint.longitude]).addTo(map);
    }

    function clearMarker() {
        if (currentMarker !== null) {
            currentMarker.removeFrom(map);
            currentMarker = null;
        }
    }

    return {
        map,
        trip,
        initMap,
        getAndDrawTripOnMap,
        millisTimestampToDateTime,
        drawMapPath,
        addMarker,
        clearMarker,
    }
}