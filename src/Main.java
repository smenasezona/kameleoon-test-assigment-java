import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {

        String apiKey1 = "68563960d8d155dceaa2bf642c137a4c";
        String apiKey2 = "cfe6733e91a4dd47d99756ce2fd9f0b5";


        WeatherSDK sdk1 = WeatherSDKFactory.getSDK(apiKey1);
        WeatherSDK sdk2 = WeatherSDKFactory.getSDK(apiKey2);

        try {
            WeatherSDK.WeatherData data1 = sdk1.getWeather("London");
            System.out.println("Weather data for London (API key 1):");
            System.out.println(data1.getJson());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            WeatherSDK.WeatherData data2 = sdk2.getWeather("Paris");
            System.out.println("Weather data for Paris (API key 2):");
            System.out.println(data2.getJson());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
