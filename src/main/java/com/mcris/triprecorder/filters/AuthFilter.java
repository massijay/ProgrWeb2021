package com.mcris.triprecorder.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

public class AuthFilter implements ContainerRequestFilter {
    // https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/content/en/part1/chapter15/programmatic_security.html
    // https://itnext.io/how-to-implement-a-jax-rs-authentication-filter-3eee64b34b99
    // https://dzone.com/articles/custom-security-context-injax-rs

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        containerRequestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new Principal() {
                    @Override
                    public String getName() {
                        return "utente_test";
                    }
                };
            }

            @Override
            public boolean isUserInRole(String s) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return "Token-Based-Auth-Scheme";
            }
        });
    }
}
