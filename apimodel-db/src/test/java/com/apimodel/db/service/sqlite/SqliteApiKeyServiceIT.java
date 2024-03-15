package com.apimodel.db.service.sqlite;

import com.apimodel.db.DataSourceExtension;
import com.apimodel.model.user.ApiKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.util.Optional;

@ExtendWith(DataSourceExtension.class)
public class SqliteApiKeyServiceIT {
    private final SqliteApiKeyService apiKeyService;

    public SqliteApiKeyServiceIT(DataSource dataSource) {
        this.apiKeyService = new SqliteApiKeyService(dataSource);
    }

    @Test
    public void testGetWithNokeys() {
        Optional<ApiKey> fetched = apiKeyService.get("id");
        Assertions.assertTrue(fetched.isEmpty());
    }
}
