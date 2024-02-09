package com.apimodel.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class ApiApplication extends ResourceConfig {
    public ApiApplication() {
        packages(ApiApplication.class.getPackageName());
    }
}
