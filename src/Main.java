import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        WeatherSDK sdk = new WeatherSDK("68563960d8d155dceaa2bf642c137a4c");
        try {
            WeatherSDK.WeatherData data = sdk.getWeather("London");
            System.out.println(data.json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
