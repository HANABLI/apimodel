package com.apimodel.rest.security;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RapidApiSecurityContextTest {

    @Test
    public void testConstructor() {
        RapidApiPrincipal principal = new RapidApiPrincipal("proxy", "user", Subscription.BASIC);
        RapidApiSecurityContext rapidApiSecurityContext = new RapidApiSecurityContext(principal);
        Assertions.assertEquals(principal, rapidApiSecurityContext.getUserPrincipal());
        Assertions.assertTrue(rapidApiSecurityContext.isUserInRole("BASIC"));
        Assertions.assertEquals("RapidAPI", rapidApiSecurityContext.getAuthenticationScheme());
        Assertions.assertTrue(rapidApiSecurityContext.isSecure());
    }
}
