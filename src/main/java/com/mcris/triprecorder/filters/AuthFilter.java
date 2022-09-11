package com.mcris.triprecorder.filters;

import com.mcris.triprecorder.models.SessionSecurityContext;
import com.mcris.triprecorder.models.entities.Session;
import com.mcris.triprecorder.providers.DBProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.sql.Timestamp;
import java.time.Instant;

@Provider
public class AuthFilter implements ContainerRequestFilter {
    // https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/content/en/part1/chapter15/programmatic_security.html
    // https://itnext.io/how-to-implement-a-jax-rs-authentication-filter-3eee64b34b99
    // https://dzone.com/articles/custom-security-context-injax-rs

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String path = containerRequestContext.getUriInfo().getPath();
        if (path.equals("auth/login") || path.equals("auth/register")) {
            return;
        }

        String authHeader = containerRequestContext.getHeaderString("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        String[] parts = authHeader.split(" ");
        if (parts.length < 2) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        String token = parts[1];
        Session session = DBProvider.getInstance().getSession(token);
        if (session == null || session.getUser() == null) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        Instant now = Instant.now();
        Timestamp tsNow = Timestamp.from(now);
        if (tsNow.after(session.getExpireAt())) {
            DBProvider.getInstance().deleteSession(session.getToken());
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        // TODO: magic numbers
        Instant instant = Instant.now().plusSeconds(1800);
        session.setExpireAt(Timestamp.from(instant));
        session = DBProvider.getInstance().updateSession(session);
        containerRequestContext.setSecurityContext(new SessionSecurityContext(session));
    }
}
