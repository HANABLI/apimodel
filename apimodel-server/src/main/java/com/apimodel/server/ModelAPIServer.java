package com.apimodel.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;
public class ModelAPIServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAPIServer.class);

    public static void main(String...  args) {
        LOGGER.info("Hello World");

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme(HTTPS.asString());
    }
}
