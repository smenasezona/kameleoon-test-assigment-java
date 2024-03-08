import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

    private Object getJSON(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        String jsonText = result.toString();
        if (jsonText.startsWith("{")) {
            return new JSONObject(jsonText);
        } else if (jsonText.startsWith("[")) {
            return new JSONArray(jsonText);
        } else {
            throw new JSONException("Invalid JSON text: " + jsonText);
        }
    }


    public WeatherData getWeather(String city) throws IOException {
        if (cache.containsKey(city)) {
            WeatherData cachedData = cache.get(city);
            if (System.currentTimeMillis() - cachedData.timestamp < CACHE_EXPIRY_TIME) {
                return cachedData;
            }
        }

        // Get the coordinates of the city
        String coordUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + URLEncoder.encode(city, StandardCharsets.UTF_8) + "&limit=1&appid=" + apiKey;
        JSONArray coordResponse = (JSONArray) getJSON(coordUrl);
        double lat = coordResponse.getJSONObject(0).getDouble("lat");
        double lon = coordResponse.getJSONObject(0).getDouble("lon");

        String weatherUrl = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely,hourly,daily,alerts" + "&appid=" + apiKey;
        JSONObject weatherResponse = (JSONObject) getJSON(weatherUrl);

        WeatherData weatherData = new WeatherData(weatherResponse.toString());
        if (cache.size() >= MAX_CACHE_SIZE) {
            String oldestCity = null;
            long oldestTimestamp = Long.MAX_VALUE;
            for (Map.Entry<String, WeatherData> entry : cache.entrySet()) {
                if (entry.getValue().timestamp < oldestTimestamp) {
                    oldestCity = entry.getKey();
                    oldestTimestamp = entry.getValue().timestamp;
                }
            }
            cache.remove(oldestCity);
        }
        cache.put(city, weatherData);

        return weatherData;
    }

    public static class WeatherData {
        public String json;
        public long timestamp;
        public WeatherData(String json) {
            this.json = json;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
