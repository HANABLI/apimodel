package com.apimodel.model;

import java.security.Principal;
import java.util.Objects;

public class RapidApiPrincipal implements Principal {
    private final String proxySecret;
    private final String user;
    private final Subscription subscription;

    public RapidApiPrincipal(String proxySecret ,String user, Subscription subscription) {
        this.proxySecret = proxySecret;
        this.user = user;
        this.subscription = subscription;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getProxySecret() {
        return proxySecret;
    }

    public String getUser() {
        return user;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        RapidApiPrincipal that = (RapidApiPrincipal) object;
        return Objects.equals(proxySecret, that.proxySecret) &&
                Objects.equals(user, that.user) &&
                subscription == that.subscription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxySecret ,user, subscription);
    }

    @Override
    public String toString() {
        return "RapidApiPrincipal{" +
                "proxySecret='" + proxySecret + '\'' +
                ", user='" + user + '\'' +
                ", subscription=" + subscription +
                '}';
    }
}
