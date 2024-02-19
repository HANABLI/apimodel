package com.apimodel.server;


import com.apimodel.rest.ApiApplication;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static com.apimodel.model.config.ConfigKey.*;
import static com.apimodel.model.config.SystemKey.MODE;
import static com.apimodel.model.config.SystemKey.PORT;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.eclipse.jetty.http.HttpScheme.HTTPS;
import static org.eclipse.jetty.http.HttpVersion.HTTP_1_1;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class ModelAPIServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAPIServer.class);
    private static final String ROOT_CONTEXT = "/";
    private static final String API_PATTERN = "/api/*";

    private static Server createJettyServer(int port, Config config) throws IOException {

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme(HTTPS.asString());
        httpConfiguration.setSecurePort(port);
        httpConfiguration.addCustomizer(new SecureRequestCustomizer());
        httpConfiguration.setSendXPoweredBy(false);

        HttpConnectionFactory httpsConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(config.getString(SERVER_KEYSTORE_FILE.getKey()));
        sslContextFactory.setKeyStoreType(config.getString(SERVER_KEYSTORE_TYPE.getKey()));
        sslContextFactory.setKeyStorePassword(config.getString(SERVER_KEYSTORE_PASSWORD.getKey()));
        sslContextFactory.setKeyManagerPassword(config.getString(SERVER_KEYSTORE_PASSWORD.getKey()));
        sslContextFactory.setTrustAll(true);

        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory((SslContextFactory.Server) sslContextFactory, HTTP_1_1.asString());

        Server server = new Server();
        ServerConnector httpsConnector = new ServerConnector(server, sslConnectionFactory, httpsConnectionFactory);
        httpsConnector.setName("secure");
        httpsConnector.setPort(httpConfiguration.getSecurePort());

        server.addConnector(httpsConnector);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
        servletContextHandler.setContextPath(ROOT_CONTEXT);
        servletContextHandler.setBaseResource(Resource.newResource(config.getString(SERVER_WEB_CONTENT.getKey())));
        servletContextHandler.addServlet(DefaultServlet.class, ROOT_CONTEXT);

        server.setHandler(servletContextHandler);

        ServletHolder apiServletHolder = new ServletHolder(new ServletContainer(new ApiApplication(config)));
        servletContextHandler.addServlet(ServletContainer.class, API_PATTERN);


        apiServletHolder.setInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, ApiApplication.class.getName());

        return server;
    }

    public static void main(String...  args) throws Exception {
        int port = Integer.parseInt(ofNullable(System.getProperty(PORT.getKey())).orElse(PORT.getDefaultValue()));
        String mode = ofNullable(System.getProperty(MODE.getKey())).orElse(MODE.getDefaultValue());
        String url = format("https://raw.githubusercontent.com/HANABLI/apimodel/main/system-%s.properties", mode);
        Config config = ConfigFactory.parseURL(new URL(url));
        LOGGER.info("Keystore: {}", config.getString(SERVER_KEYSTORE_FILE.getKey()));

        Server server = createJettyServer(port, config);

        LOGGER.info("Server starting in port : {}", port); //Using string interpolation
        server.start();
        server.join();
    }
}
