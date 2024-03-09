import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

    public class WeatherSDK {
    private final String apiKey;
    private final Map<String, WeatherData> cache = new HashMap<>();
    private static final int MAX_CACHE_SIZE = 10;
    private static final long CACHE_EXPIRY_TIME = 600000;

    public WeatherSDK(String apiKey) {
        this.apiKey = apiKey;
    }

    public WeatherData getWeather(String city) throws IOException, URISyntaxException {
        if (cache.containsKey(city)) {
            WeatherData cachedData = cache.get(city);
            if (System.currentTimeMillis() - cachedData.getTimestamp() < CACHE_EXPIRY_TIME) {
                return cachedData;
            }
        }

        WeatherData weatherData = WeatherDataParser.parseWeatherData(city, apiKey);

        if (cache.size() >= MAX_CACHE_SIZE) {
            String oldestCity = null;
            long oldestTimestamp = Long.MAX_VALUE;
            for (Map.Entry<String, WeatherData> entry : cache.entrySet()) {
                if (entry.getValue().getTimestamp() < oldestTimestamp) {
                    oldestCity = entry.getKey();
                    oldestTimestamp = entry.getValue().getTimestamp();
                }
            }
            cache.remove(oldestCity);
        }
        cache.put(city, weatherData);

        return weatherData;
    }

    public static class WeatherData {
        private final String json;
        private final long timestamp;

        public WeatherData(String json) {
            this.json = json;
            this.timestamp = System.currentTimeMillis();
        }

        public String getJson() {
            return json;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
