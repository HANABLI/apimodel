package com.apimodel.server.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemKeyTest {
    @Test
    public void testGetKey() {
        Assertions.assertEquals("mode", SystemKey.MODE.getKey());
        Assertions.assertEquals("port", SystemKey.PORT.getKey());
    }

    @Test
    public void testGetDefaultValue() {
        Assertions.assertEquals("8443", SystemKey.PORT.getDefaultValue());
        Assertions.assertEquals("dev", SystemKey.MODE.getDefaultValue());
    }
}
