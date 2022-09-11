package com.mcris.triprecorder.resources;

import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.models.entities.User;
import com.mcris.triprecorder.models.utils.ErrorMessage;
import com.mcris.triprecorder.models.utils.PasswordUtils;
import com.mcris.triprecorder.models.utils.SessionSecurityContext;
import com.mcris.triprecorder.providers.DBProvider;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/auth")
public class AuthResources {
    static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", Pattern.CASE_INSENSITIVE);

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context ContainerRequest containerRequest) {
        User authenticated = DBProvider.getInstance().getUserByUsername(user.getUsername());
        if (authenticated == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        boolean isPasswordValid = PasswordUtils.validatePassword(user.getPassword(), authenticated.getPassword());
        if (!isPasswordValid) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Session s = DBProvider.getInstance().createNewSession(authenticated);
        return Response.ok(s).build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user, @Context ContainerRequest containerRequest) {
        if (user == null || user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            return Response.status(422).build(); // UNPROCESSABLE ENTITY
        }
        Matcher matcher = emailPattern.matcher(user.getEmail());
        if (!matcher.find()) {
            ErrorMessage em = new ErrorMessage("Invalid email address", ErrorMessage.INVALID_EMAIL);
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        user.setId(0);
        boolean usernameAlreadyExist = DBProvider.getInstance().getUserByUsername(user.getUsername()) != null;
        if (usernameAlreadyExist) {
            ErrorMessage em = new ErrorMessage("An user with this username already exist", ErrorMessage.USERNAME_ALREADY_EXISTS);
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        boolean emailAlreadyExist = DBProvider.getInstance().getUserByEmail(user.getEmail()) != null;
        if (emailAlreadyExist) {
            ErrorMessage em = new ErrorMessage("An user with this email already exist", ErrorMessage.EMAIL_ALREADY_EXISTS);
            return Response.status(422).entity(em).build(); // UNPROCESSABLE ENTITY
        }
        user.setPassword(PasswordUtils.hashAndSaltPassword(user.getPassword()));
        boolean result = DBProvider.getInstance().insertNewUser(user);
        return Response.status(result ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST).build();
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

