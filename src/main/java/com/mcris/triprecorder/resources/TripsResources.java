package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.entities.Geopoint;
import com.mcris.triprecorder.models.entities.Trip;
import com.mcris.triprecorder.models.entities.User;
import com.mcris.triprecorder.providers.DBProvider;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

@Path("/trips")
public class TripsResources {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Trip> getUserTrips(@Context ContainerRequest containerRequest) {
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        return DBProvider.getInstance().getUserTrips(user);
    }

    @GET
    @Path("{trip_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Trip getUserTrip(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId == 0) {
            return null;
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        return DBProvider.getInstance().getUserTrip(tripId, user.getId());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Trip postTrip(Trip trip, @Context ContainerRequest containerRequest) {
        if (trip == null || trip.getId() != 0) {
            // TODO: ritonare http error? o errore in json con http ok?
            return null;
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        if (!(trip.getUserId() == 0 || trip.getUserId() == user.getId())) {
            // TODO: ritonare http error? o errore in json con http ok?
            return null;
        }
        trip.setUserId(user.getId());
        return DBProvider.getInstance().addOrUpdateTrip(trip);
    }

    @DELETE
    @Path("{trip_id}")
    public Response deleteTrip(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        boolean result = DBProvider.getInstance().deleteTrip(tripId, user.getId());
        return Response.status(result ? Response.Status.OK : Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("{trip_id}/geopoints")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Geopoint> getTripGeopoints(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId == 0) {
            return null;
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        return DBProvider.getInstance().getTripGeopoints(tripId, user.getId());
    }

    @POST
    @Path("{trip_id}/geopoints")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Geopoint postGeopoint(Geopoint geopoint, @PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId == 0) {
            return null;
        }
        if (geopoint == null || geopoint.getId() != 0) {
            return null;
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        Trip trip = DBProvider.getInstance().getUserTrip(tripId, user.getId());
        if (trip == null) {
            return null;
        }
        geopoint.setTripId(tripId);
        return DBProvider.getInstance().addOrUpdateGeopoint(geopoint);
    }
}
