package com.apimodel.db.service.sqlite;

import com.apimodel.db.service.ApiKeyService;
import com.apimodel.model.Subscription;
import com.apimodel.model.user.ApiKey;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SqliteApiKeyService implements ApiKeyService {
    private final DataSource dataSource;

    public SqliteApiKeyService(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Optional<ApiKey> get(String apikey) {

        String sql = "SELECT * FROM api_keys WHERE apikey = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, apikey);
            try (ResultSet resultSet = ps.executeQuery()) {
                if(resultSet.next()) {
                    Optional<Subscription> subscription = Subscription.from(resultSet.getString("subscription"));
                    if(!subscription.isPresent()) {
                        return Optional.empty();
                    }
                    return Optional.of(
                            new ApiKey()
                                    .setApiKey(resultSet.getString("apikey"))
                                    .setUser(resultSet.getString("user_id"))
                                    .setSubscription(subscription.get())
                    );
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetsh apiKey: " + exception.getMessage(), exception);
        }
    }

    @Override
    public int truncate() {
        String sql = "DELETE FROM api_keys";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            int deleted = preparedStatement.executeUpdate();
            if (deleted > 0) {
                connection.commit();
            }
            return deleted;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to truncate api keys: " + e.getMessage(), e);
        }
    }
}
