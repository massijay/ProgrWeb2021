package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.entities.User;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UsersResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getLoggedUser(@Context ContainerRequest containerRequest){
        User user = (User) containerRequest.getSecurityContext().getUserPrincipal();
        user.setTrips(null);
        return user;
    }
}
