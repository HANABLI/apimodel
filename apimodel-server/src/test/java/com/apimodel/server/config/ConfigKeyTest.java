package com.apimodel.server.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigKeyTest {
    @Test
    public void testGetKey() {
        Assertions.assertEquals("server.keystore.file", ConfigKey.SERVER_KEYSTORE_FILE.getKey());
        Assertions.assertEquals("server.keystore.password", ConfigKey.SERVER_KEYSTORE_PASSWORD.getKey());
        Assertions.assertEquals("server.keystore.type", ConfigKey.SERVER_KEYSTORE_TYPE.getKey());
        Assertions.assertEquals("server.web.content", ConfigKey.SERVER_WEB_CONTENT.getKey());
    }
}
