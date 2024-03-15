package com.apimodel.model.config;

import static java.util.Locale.ENGLISH;

public enum ConfigKey {
    SERVER_KEYSTORE_FILE,
    SERVER_KEYSTORE_TYPE,
    SERVER_KEYSTORE_PASSWORD,
    SERVER_WEB_CONTENT,
    DB_DRIVER,
    DB_URL,
    DB_USERNAME,
    DB_PASSWORD,

    RAPIDAPI_PROXY_SECRET;

    public String getKey() {
        return name().toLowerCase(ENGLISH).replace("_", ".");
    }
}
