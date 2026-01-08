package com.luopc.platform.demo.ai.jdk.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse  {
    private double precipitation;
    private double temperature;
    private int pressure;
    private int humidity;
    private String windDirection;
    private int windDirectionDegree;
    private double windSpeed;
    private String windScale;
    private int code;
    private String place;

    public String toCnString() {
        StringBuilder sb = new StringBuilder();
        sb.append("当前温度：").append(temperature).append("℃\n");
        sb.append("风力等级：").append(windScale).append("\n");
        sb.append("降水量：").append(precipitation).append("mm\n");
        sb.append("气压：").append(pressure).append("hPa\n");
        sb.append("湿度：").append(humidity).append("%\n");
        sb.append("风向：").append(windDirection).append("\n");
        sb.append("风向度数：").append(windDirectionDegree).append("\n");
        sb.append("风速：").append(windSpeed).append("m/s\n");
        sb.append("地点：").append(place).append("\n");
        return sb.toString();
    }
}
