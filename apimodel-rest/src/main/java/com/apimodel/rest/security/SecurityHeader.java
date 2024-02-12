package com.apimodel.rest.security;

public enum SecurityHeader {
    RAPIDAPI_PROXY_SECRET("X-RapidAPI-Proxy-Secret"),
    RAPIDAPI_USER("X-RapidAPI-User"),
    RAPID_SUBSCRIPTION("X-RapidAPI-Subscription");

    private final String header;

    SecurityHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
