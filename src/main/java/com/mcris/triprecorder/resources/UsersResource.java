package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.entities.User;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/profile")
public class UsersResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getLoggedUser(@Context ContainerRequest containerRequest) {
        return (User) containerRequest.getSecurityContext().getUserPrincipal();
    }

//    @PATCH
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public User patchUser(User patchedUser,@Context ContainerRequest containerRequest){
//        User current = (User) containerRequest.getSecurityContext().getUserPrincipal();
//        if(patchedUser)
//    }
}
