import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WeatherDataParser {
    public static WeatherSDK.WeatherData parseWeatherData(String city, String apiKey) throws IOException, URISyntaxException {
        try {
            String coordUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" +
                    URLEncoder.encode(city, StandardCharsets.UTF_8) + "&limit=1&appid=" + apiKey;
            JSONArray coordResponse = new JSONArray(HttpUtils.fetchJSON(coordUrl));

            if (coordResponse.isEmpty()) {
                throw new IOException("City not found");
            }

            JSONObject coordObject = coordResponse.getJSONObject(0);
            double lat = coordObject.getDouble("lat");
            double lon = coordObject.getDouble("lon");

            String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
            String weatherResponse = HttpUtils.fetchJSON(weatherUrl);

            return parseWeatherResponse(weatherResponse);
        } catch (IOException e) {
            throw new IOException("Failed to parse weather data: " + e.getMessage());
        }
    }

    private static WeatherSDK.WeatherData parseWeatherResponse(String response) {
        JSONObject weatherObject = new JSONObject(response);
        return new WeatherSDK.WeatherData(weatherObject.toString());
    }
}