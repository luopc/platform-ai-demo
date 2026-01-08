package com.luopc.platform.demo.ai.jdk.service;

import com.luopc.platform.demo.ai.jdk.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherService {

    public WeatherResponse getWeather(String city) {

        return WeatherResponse.builder()
                .place(city)
                .temperature(25)
                .windScale("2级")
                .precipitation(0)
                .pressure(97)
                .humidity(50)
                .windDirection("南风")
                .windDirectionDegree(180)
                .windSpeed(2)
                .build();
    }
}
