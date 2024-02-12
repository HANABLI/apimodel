package com.apimodel.rest.security;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

public class RapidApiSecurityContext implements SecurityContext {
    private static final String  AUTHENTICATION_SCHEME = "RapidAPI";
    private final RapidApiPrincipal principal;

    public RapidApiSecurityContext(RapidApiPrincipal principal) {
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return principal.getSubscription() == Subscription.from(role).orElse(null);
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return AUTHENTICATION_SCHEME;
    }
}
