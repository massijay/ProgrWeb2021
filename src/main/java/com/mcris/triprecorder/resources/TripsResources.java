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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Path("/trips")
public class TripsResources {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTrips(@QueryParam("date") String date, @Context ContainerRequest containerRequest) {
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        if (date != null) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                Collection<Trip> trips = DBProvider.getInstance().getUserTrips(user, localDate);
                return Response.ok(trips).build();
            } catch (DateTimeParseException ex) {
                return Response.status(422).build(); // UNPROCESSABLE ENTITY
            }
        }
        Collection<Trip> trips = DBProvider.getInstance().getUserTrips(user, null);
        return Response.ok(trips).build();
    }

    @GET
    @Path("{trip_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTrip(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId <= 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        Trip trip = DBProvider.getInstance().getUserTrip(tripId, user.getId());
        return trip != null ? Response.ok(trip).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postTrip(Trip trip, @Context ContainerRequest containerRequest) {
        if (trip == null || trip.getId() != 0) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        if (!(trip.getUserId() == 0 || trip.getUserId() == user.getId())) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        trip.setUserId(user.getId());
        Trip newTrip = DBProvider.getInstance().addOrUpdateTrip(trip);
        return Response.ok(newTrip).build();
    }

    @PATCH
    @Path("{trip_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchTrip(Trip trip, @PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (trip == null || tripId <= 0) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        if (!(trip.getUserId() == 0 || trip.getUserId() == user.getId())) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        trip.setUserId(user.getId());
        trip.setId(tripId);
        Trip updatedTrip = DBProvider.getInstance().addOrUpdateTrip(trip);
        return Response.ok(updatedTrip).build();
    }

    @DELETE
    @Path("{trip_id}")
    public Response deleteTrip(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId <= 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        Trip toDelete = DBProvider.getInstance().getUserTrip(tripId, user.getId());
        if (toDelete == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        boolean result = DBProvider.getInstance().deleteTripWithItsGeopoints(tripId, user.getId());
        return Response.status(result ? Response.Status.OK : Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{trip_id}/geopoints")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTripGeopoints(@PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId <= 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        List<Geopoint> geopoints = DBProvider.getInstance().getTripGeopoints(tripId, user.getId());
        return geopoints != null ? Response.ok(geopoints).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{trip_id}/geopoints")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putGeopoints(List<Geopoint> geopoints, @PathParam("trip_id") int tripId, @Context ContainerRequest containerRequest) {
        if (tripId <= 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (geopoints == null) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        Trip trip = DBProvider.getInstance().getUserTrip(tripId, user.getId());
        if (trip == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        boolean deleteOk = DBProvider.getInstance().deleteGeopointsOfTrip(tripId);
        if (!deleteOk) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        List<Geopoint> newGeopoints = DBProvider.getInstance()
                .addOrUpdateGeopoints(geopoints.stream().peek(p -> p.setId(0)).collect(Collectors.toList()), tripId);
        return Response.ok(newGeopoints).build();
    }


}
