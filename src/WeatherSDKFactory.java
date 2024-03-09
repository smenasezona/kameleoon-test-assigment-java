import java.util.HashMap;
import java.util.Map;

public class WeatherSDKFactory {
    private static final Map<String, WeatherSDK> instances = new HashMap<>();

    public static WeatherSDK getSDK(String apiKey) {
        if (!instances.containsKey(apiKey)) {
            synchronized (instances) {
                if (!instances.containsKey(apiKey)) {
                    instances.put(apiKey, new WeatherSDK(apiKey));
                }
            }
        }
        return instances.get(apiKey);
    }
}
