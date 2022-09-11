package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.PasswordUtils;
import com.mcris.triprecorder.models.entities.User;
import com.mcris.triprecorder.providers.DBProvider;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;

import static com.mcris.triprecorder.resources.AuthResources.emailPattern;

@Path("/profile")
public class UsersResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getLoggedUser(@Context ContainerRequest containerRequest) {
        return (User) containerRequest.getSecurityContext().getUserPrincipal();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchUser(User newUser, @Context ContainerRequest containerRequest) {
        if (newUser == null) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        Matcher matcher = emailPattern.matcher(newUser.getEmail());
        if (!matcher.find()) {
            ErrorMessage em = new ErrorMessage();
            em.message = "Invalid email address";
            em.errorCode = 3;
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        User existingUser = (User) containerRequest.getSecurityContext().getUserPrincipal();
        User found = DBProvider.getInstance().getUserByUsername(newUser.getUsername());
        if (found != null && found.getId() != existingUser.getId()) {
            ErrorMessage em = new ErrorMessage();
            em.message = "An user with this username already exist";
            em.errorCode = 1;
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        found = DBProvider.getInstance().getUserByEmail(newUser.getEmail());
        if (found != null && found.getId() != existingUser.getId()) {
            ErrorMessage em = new ErrorMessage();
            em.message = "An user with this email already exist";
            em.errorCode = 2;
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        if (newUser.getUsername() != null) {
            existingUser.setUsername(newUser.getUsername());
        }
        if (newUser.getUsername() != null) {
            existingUser.setUsername(newUser.getUsername());
        }
        if (newUser.getPassword() != null) {
            existingUser.setPassword(PasswordUtils.hashAndSaltPassword(newUser.getPassword()));
        }
        User updated = DBProvider.getInstance().updateUser(existingUser);
        return Response.ok(updated).build();
    }
}
