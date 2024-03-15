package com.apimodel.model.user;

import com.apimodel.model.Subscription;

import java.util.Objects;

public class ApiKey {
    private String apiKey;
    private String user;
    private Subscription subscription;


    public String getApiKey() {
        return apiKey;
    }

    public ApiKey setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ApiKey setUser(String user) {
        this.user = user;
        return this;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public ApiKey setSubscription(Subscription subscription) {
        this.subscription = subscription;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiKey apiKey1 = (ApiKey) o;
        return Objects.equals(apiKey, apiKey1.apiKey) && Objects.equals(user, apiKey1.user) && subscription == apiKey1.subscription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiKey, user, subscription);
    }

    @Override
    public String toString() {
        return "ApiKey{" +
                "apiKey='" + apiKey + '\'' +
                ", user='" + user + '\'' +
                ", subscription=" + subscription +
                '}';
    }
}
