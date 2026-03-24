package com.luopc.platform.web.tools;


import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WeatherTools {

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool("""
        Gets the current weather forecast for a given latitude and longitude.
        It's important to use this tool ONLY when user asks about weather.
        """)
    public String getWeatherForecast(double latitude, double longitude) {
        System.out.printf("Tool executed: getWeatherForecast(lat=%.4f, lon=%.4f)%n", latitude, longitude);

        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=Asia/Shanghai",
                latitude, longitude
        );

        try {
            // 使用RestTemplate调用API
            // 我们期望返回一个Map结构的JSON对象
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);

            if (response != null && response.containsKey("daily")) {
                Map<String, Object> dailyData = (Map<String, Object>) response.get("daily");
                // 简单地提取第一天的数据作为“当前”预报
                double maxTemp = ((java.util.List<Double>) dailyData.get("temperature_2m_max")).get(0);
                double minTemp = ((java.util.List<Double>) dailyData.get("temperature_2m_min")).get(0);
                return String.format("The weather forecast is: min temperature %.1f°C, max temperature %.1f°C.", minTemp, maxTemp);
            }
            return "Could not retrieve weather data.";

        } catch (Exception e) {
            System.err.println("Error calling weather API: " + e.getMessage());
            return "Error: Unable to get weather forecast.";
        }
    }
}
