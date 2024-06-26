package com.apimodel.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.*;
import org.sqlite.JDBC;

import javax.sql.DataSource;

public class DataSourceExtension implements BeforeEachCallback, ParameterResolver {
    private final DataSource dataSource;
    private final ServiceFactory serviceFactory;

    public DataSourceExtension() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC.class.getName());
        config.setJdbcUrl("jdbc:sqlite::memory:");
        config.setUsername("user");
        config.setPassword("pass");
        config.setAutoCommit(false);

        dataSource = new HikariDataSource(config);
        serviceFactory = new DefaultServiceFactory(dataSource);
    }

    //will be invoked before each test
    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        serviceFactory.getTodoItemService().truncate();
        serviceFactory.getTodoListService().truncate();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(DataSource.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return dataSource;
    }
}
