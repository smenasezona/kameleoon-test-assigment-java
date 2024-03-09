import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        String apiKey = "68563960d8d155dceaa2bf642c137a4c";
        WeatherSDK sdk = new WeatherSDK(apiKey);
        try {
            WeatherSDK.WeatherData data = sdk.getWeather("London");
            System.out.println(data.getJson());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
