package com.luopc.platform.web.redis.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private Integer age;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
