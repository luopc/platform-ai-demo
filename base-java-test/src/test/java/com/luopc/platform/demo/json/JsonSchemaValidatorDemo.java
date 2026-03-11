package com.luopc.platform.demo.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;

import java.util.List;

/**
 * json-schema-validator 2.0.0+ 版本 JSON 校验 Demo
 * 包含 username/email/password/confirmPassword 字段校验
 */
public class JsonSchemaValidatorDemo {

    // 全局 ObjectMapper（Jackson 2.x）
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        // 1. 定义 JSON Schema 规则
        String schemaJson = buildSchema();

        // 2. 待校验的 JSON 数据（测试用例1：合法数据）
        String validJson = """
                {
                    "username": "test_user123",
                    "email": "test@example.com",
                    "password": "Test@123456",
                    "confirmPassword": "Test@123456"
                }
                """;

        // 3. 待校验的 JSON 数据（测试用例2：非法数据，用于验证错误提示）
        String invalidJson = """
                {
                    "username": "tu",
                    "email": "test@example",
                    "password": "123456",
                    "confirmPassword": "1234567"
                }
                """;

        // 4. 初始化 Schema 注册表（核心工厂类）
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);

        try {
            // 5. 加载 Schema 规则
            JsonNode schemaNode = OBJECT_MAPPER.readTree(schemaJson);
            Schema schema = schemaRegistry.getSchema(schemaNode);

            // ========== 测试合法数据 ==========
            System.out.println("===== 校验合法数据 =====");
            validateJson(schema, validJson);

            // ========== 测试非法数据 ==========
            System.out.println("\n===== 校验非法数据 =====");
            validateJson(schema, invalidJson);

        } catch (JsonProcessingException e) {
            System.err.println("JSON 解析失败：" + e.getMessage());
        }
    }

    /**
     * 构建 JSON Schema 规则
     * 规则说明：
     * - username：非空、长度3-20、仅字母/数字/下划线
     * - email：非空、符合邮箱格式
     * - password：非空、长度8-20、包含大小写+数字+特殊字符
     * - confirmPassword：与 password 一致
     */
    private static String buildSchema() {
        return """
                {
                    "$schema": "https://json-schema.org/draft/2020-12/schema",
                    "type": "object",
                    "properties": {
                        "username": {
                            "type": "string",
                            "description": "用户名",
                            "minLength": 3,
                            "maxLength": 20,
                            "pattern": "^[a-zA-Z0-9_]+$",
                            "errorMessage": {
                                "minLength": "用户名长度不能少于3位",
                                "maxLength": "用户名长度不能超过20位",
                                "pattern": "用户名仅支持字母、数字、下划线"
                            }
                        },
                        "email": {
                            "type": "string",
                            "description": "邮箱",
                            "format": "email",
                            "errorMessage": {
                                "format": "邮箱格式不正确"
                            }
                        },
                        "password": {
                            "type": "string",
                            "description": "密码",
                            "minLength": 8,
                            "maxLength": 20,
                            "pattern": "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@$!%*?&])[A-Za-z\\\\d@$!%*?&]{8,20}$",
                            "errorMessage": {
                                "minLength": "密码长度不能少于8位",
                                "maxLength": "密码长度不能超过20位",
                                "pattern": "密码需包含大小写字母、数字和特殊字符"
                            }
                        },
                        "confirmPassword": {
                            "type": "string",
                            "description": "确认密码",
                            "const": { "$data": "1/password" },
                            "errorMessage": {
                                "const": "两次输入的密码不一致"
                            }
                        }
                    },
                    "required": ["username", "email", "password", "confirmPassword"],
                    "errorMessage": {
                        "required": {
                            "username": "用户名不能为空",
                            "email": "邮箱不能为空",
                            "password": "密码不能为空",
                            "confirmPassword": "确认密码不能为空"
                        }
                    }
                }
                """;
    }

    /**
     * 执行 JSON 校验并输出结果
     * @param schema 预加载的 Schema 规则
     * @param jsonStr 待校验的 JSON 字符串
     */
    private static void validateJson(Schema schema, String jsonStr) throws JsonProcessingException {
        // 1. 解析 JSON 数据
        JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonStr);

        // 2. 执行校验（启用 format 断言 + 自定义错误消息）
        List<Error> errors = schema.validate(jsonNode);

        // 3. 输出校验结果
        if (errors.isEmpty()) {
            System.out.println("✅ JSON 校验通过！");
        } else {
            System.out.println("❌ JSON 校验失败，错误数：" + errors.size());
            for (Error error : errors) {
                // 获取错误路径（如 /username）
                String instancePath = error.getInstanceLocation().toString();
                // 获取错误提示（优先自定义消息，无则用默认）
                String errorMsg =error.getMessage();
                // 输出错误详情
                System.out.printf("路径：%s | 错误：%s%n", instancePath, errorMsg);
            }
        }
    }
}
