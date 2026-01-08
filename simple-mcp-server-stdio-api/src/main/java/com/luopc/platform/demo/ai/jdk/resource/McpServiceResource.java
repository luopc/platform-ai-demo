package com.luopc.platform.demo.ai.jdk.resource;

import com.luopc.platform.demo.ai.jdk.model.JavaVersion;
import com.luopc.platform.demo.ai.jdk.model.WeatherResponse;
import com.luopc.platform.demo.ai.jdk.service.JavaVersionService;
import com.luopc.platform.demo.ai.jdk.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class McpServiceResource {

    @Resource
    private JavaVersionService javaVersionService;
    @Resource
    private WeatherService weatherService;

    @Tool(name = "getJdkLink", description = "get jdk download link via java version")
    public String getJdkLink(JavaVersion jdkVersion) {
        String link = javaVersionService.getDownloadUrl(jdkVersion);
        return Objects.nonNull(link) ? link : "抱歉：未查询到对应的下载地址！";
    }


    @McpTool(name = "getWeatherByCity", description = "Get weather by city name")
    public String getWeatherByCity(@McpToolParam(description = "city name", required = true) String city) {
        log.info("City: {}", city);
        WeatherResponse weather = weatherService.getWeather(city);
        return Objects.nonNull(weather) ? weather.toString() : "抱歉：未查询到对应城市！";
    }
}
