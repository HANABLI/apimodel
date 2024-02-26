package com.apimodel.rest;

import com.apimodel.db.DefaultServiceFactory;
import com.apimodel.db.ServiceFactory;
import com.apimodel.model.config.ConfigKey;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class ApiApplication extends ResourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiApplication.class);
    public ApiApplication(Config config) {
        this(createServiceFactory(config));
    }

    public ApiApplication(ServiceFactory serviceFactory) {
        packages(ApiApplication.class.getPackageName());

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(serviceFactory).to(ServiceFactory.class);
                LOGGER.info("Configuring binder");
            }
        });
    }

    private static ServiceFactory createServiceFactory(Config config) {

        HikariConfig dbConfig = new HikariConfig();
        dbConfig.setDriverClassName(config.getString(ConfigKey.DB_DRIVER.getKey()));
        dbConfig.setJdbcUrl(config.getString(ConfigKey.DB_URL.getKey()));
        dbConfig.setUsername(config.getString(ConfigKey.DB_USERNAME.getKey()));
        dbConfig.setPassword(config.getString(ConfigKey.DB_PASSWORD.getKey()));
        dbConfig.setAutoCommit(false);

        DataSource dataSource = new HikariDataSource(dbConfig);
        return new DefaultServiceFactory(dataSource);
    }
}
