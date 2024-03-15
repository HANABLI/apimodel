package com.apimodel.db.service;

import com.apimodel.model.user.ApiKey;
import java.util.Optional;

public interface ApiKeyService {
    Optional<ApiKey> get(String key);
    int truncate();
}
