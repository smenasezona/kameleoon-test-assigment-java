import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private String fetchJSON(String urlString) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    public WeatherData getWeather(String city) throws IOException, URISyntaxException {
        if (cache.containsKey(city)) {
            WeatherData cachedData = cache.get(city);
            if (System.currentTimeMillis() - cachedData.getTimestamp() < CACHE_EXPIRY_TIME) {
                return cachedData;
            }
        }

        String coordUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" +
                URLEncoder.encode(city, StandardCharsets.UTF_8) + "&limit=1&appid=" + apiKey;
        JSONArray coordResponse = new JSONArray(fetchJSON(coordUrl));
        JSONObject coordObject = coordResponse.getJSONObject(0);
        double lat = coordObject.getDouble("lat");
        double lon = coordObject.getDouble("lon");

        String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        String weatherResponse = fetchJSON(weatherUrl);

        JSONObject weatherObject = new JSONObject(weatherResponse);
        JSONObject formattedResponse = new JSONObject();

        JSONObject weather = new JSONObject();
        JSONArray weatherArray = weatherObject.getJSONArray("weather");
        JSONObject weatherInfo = weatherArray.getJSONObject(0);
        weather.put("main", weatherInfo.getString("main"));
        weather.put("description", weatherInfo.getString("description"));
        formattedResponse.put("weather", weather);

        JSONObject temperature = new JSONObject();
        JSONObject main = weatherObject.getJSONObject("main");
        temperature.put("temp", main.getDouble("temp"));
        temperature.put("feels_like", main.getDouble("feels_like"));
        formattedResponse.put("temperature", temperature);

        formattedResponse.put("visibility", weatherObject.getInt("visibility"));
        formattedResponse.put("wind", weatherObject.getJSONObject("wind"));
        formattedResponse.put("datetime", weatherObject.getLong("dt"));
        formattedResponse.put("sys", weatherObject.getJSONObject("sys"));
        formattedResponse.put("timezone", weatherObject.getInt("timezone"));
        formattedResponse.put("name", weatherObject.getString("name"));

        WeatherData weatherData = new WeatherData(formattedResponse.toString());
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
