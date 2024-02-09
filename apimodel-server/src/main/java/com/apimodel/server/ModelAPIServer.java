package com.apimodel.server;


import com.apimodel.rest.ApiApplication;
import jakarta.servlet.ServletContext;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.jetty.http.HttpScheme.HTTPS;
import static org.eclipse.jetty.http.HttpVersion.HTTP_1_1;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class ModelAPIServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelAPIServer.class);

    public static void main(String...  args) throws Exception {

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

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.setBaseResource(Resource.newResource("apimodel-server/src/main/resources/www"));
        servletContextHandler.addServlet(DefaultServlet.class, "/");

        server.setHandler(servletContextHandler);

        ServletHolder apiServletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");


        apiServletHolder.setInitParameter("jakarta.ws.rs.Application", ApiApplication.class.getName());

        LOGGER.info("Server starting");
        server.start();
        server.join();
    }
}
