package com.mcris.triprecorder.models.utils;

import com.mcris.triprecorder.models.entities.Session;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class SessionSecurityContext implements SecurityContext {
    private final Session session;

    public SessionSecurityContext(Session session) {
        this.session = session;
    }

    @Override
    public Principal getUserPrincipal() {
        return session.getUser();
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Token-Based-Auth-Scheme";
    }

    public Session getSession() {
        return session;
    }
}
