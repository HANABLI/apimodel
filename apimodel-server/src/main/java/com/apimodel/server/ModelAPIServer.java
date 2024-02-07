package com.apimodel.server;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;
import static org.eclipse.jetty.http.HttpVersion.HTTP_1_1;

public class ModelAPIServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAPIServer.class);

    public static void main(String...  args) throws Exception {
        LOGGER.info("Hello World");

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme(HTTPS.asString());
        httpConfiguration.setSecurePort(8443);
        httpConfiguration.addCustomizer(new SecureRequestCustomizer());
        httpConfiguration.setSendXPoweredBy(false);

        HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath("apimodel-server/src/main/resources/certs/modelapi.p12");
        sslContextFactory.setKeyStoreType("pkcs12");
        sslContextFactory.setKeyStorePassword("changeit");
        sslContextFactory.setKeyManagerPassword("changeit");
        sslContextFactory.setTrustAll(true);

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory((SslContextFactory.Server) sslContextFactory, HTTP_1_1.asString());

        Server server = new Server();
        ServerConnector httpsConnector = new ServerConnector(server, sslConnectionFactory, httpsConnectionFactory);
        httpsConnector.setName("secure");
        httpsConnector.setPort(httpConfiguration.getSecurePort());

        server.addConnector(httpsConnector);

        server.start();
        server.join();
    }
}
