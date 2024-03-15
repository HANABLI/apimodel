package com.apimodel.rest.security;

public enum SecurityHeader {
    EXEMPLE_API_KEY("X-ExampleAPI-Key"),
    RAPIDAPI_PROXY_SECRET("X-RapidAPI-Proxy-Secret"),
    RAPIDAPI_USER("X-RapidAPI-User"),
    RAPIDAPI_SUBSCRIPTION("X-RapidAPI-Subscription");

    private final String header;

    SecurityHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
