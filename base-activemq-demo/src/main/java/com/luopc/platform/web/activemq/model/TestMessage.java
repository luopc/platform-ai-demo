package com.luopc.platform.web.activemq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TestMessage {

    @Schema(description = "sender")
    private String sender;
    @Schema(description = "msg content")
    private String content;
}
