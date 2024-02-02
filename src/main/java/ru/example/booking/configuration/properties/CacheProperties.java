package ru.example.booking.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.cache.properties")
public class CacheProperties {

    private List<String> cacheNames = new ArrayList<>();

    private final Map<String, CacheSettings> caches = new HashMap<>();

    @Data
    public static class CacheSettings {
        private Duration expiry = Duration.ZERO;
    }

    public interface CacheNames {
        String ALL_ROOMS = "allRooms";
        String ALL_ROOMS_WITH_FILTER = "allRoomsWithFilter";
        String ROOM_BY_ID = "roomById";
    }
}
