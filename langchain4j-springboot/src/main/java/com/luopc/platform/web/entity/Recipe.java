package com.luopc.platform.web.entity;


import lombok.Data;
import java.util.List;

/**
 * 食谱实体类，用于表示一个完整的食谱信息
 */
@Data // Lombok注解，自动生成getter, setter, toString等
public class Recipe {
    /** 食谱标题 */
    private String title;

    /** 食谱描述 */
    private String description;

    /** 食材列表 */
    private List<String> ingredients;

    /** 制作步骤列表 */
    private List<String> instructions;
}
