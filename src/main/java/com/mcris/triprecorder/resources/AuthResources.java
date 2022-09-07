package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.SessionSecurityContext;
import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.models.entities.User;
import com.mcris.triprecorder.providers.DBProvider;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthResources {
    // TODO: hash and salt passwords

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context ContainerRequest containerRequest) {
        // TODO: check if already logged?
        User authenticated = DBProvider.getInstance().getUserbyUsernameAndPassword(user.getUsername(), user.getPassword());
        if (authenticated != null) {
            Session s = DBProvider.getInstance().createNewSession(authenticated);
            return Response.ok(s).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context ContainerRequest containerRequest) {
        Session current = ((SessionSecurityContext) containerRequest.getSecurityContext()).getSession();
        boolean result = DBProvider.getInstance().deleteSession(current.getToken());
        return Response.status(result ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST).build();
    }
}
