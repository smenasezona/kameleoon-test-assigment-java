# Тестовое задание в Kameleoon

**Цель тестового задания**: Создание собственного SDK для работы с [OpenWeatherMapAPI](https://openweathermap.org/).

Выбранный язык для реализации - Java.

Выполнил: [Кияшко Артём](https://hh.ru/resume/7c4c1c18ff0c7bb9430039ed1f575473717058).

## Примеры использования

### Получение данных о погоде для города

```java
WeatherSDK sdk = new WeatherSDK("your-api-key");
try{
WeatherSDK.WeatherData data = sdk.getWeather("London");
    System.out.

println("Weather data for London:");
    System.out.

println(data.getJson());
        }catch(IOException |
URISyntaxException e){
        e.

printStackTrace();
}

```

## API Reference

### WeatherSDK

Конструктор

```java
public WeatherSDK(String apiKey);
```

Создает новый объект WeatherSDK с указанным API ключом OpenWeatherMap.

Метод ``getWeather``

```java
public WeatherData getWeather(String city) throws IOException, URISyntaxException;
```

Получает данные о погоде для указанного города.

### WeatherDataParser

Метод ``parseWeatherData``

```java
public static WeatherSDK.WeatherData parseWeatherData(String city, String apiKey) throws IOException,
        URISyntaxException;
```

Парсит данные о погоде из API OpenWeatherMap для указанного города и API ключа.

### WeatherData

Конструктор

```java
public WeatherData(String json);
```

Создает новый объект WeatherData с указанным JSON представлением данных о погоде.

Метод ``getJson``

```java
public String getJson();
```

Возвращает JSON представление данных о погоде.

Метод ``getTimestamp``

```java
public long getTimestamp();
```

Возвращает временную метку создания объекта WeatherData.



