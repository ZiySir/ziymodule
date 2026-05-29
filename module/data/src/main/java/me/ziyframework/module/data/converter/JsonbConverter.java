package me.ziyframework.module.data.converter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Converter
public class JsonbConverter implements AttributeConverter<Object, String> {

    public static final JsonbConverter INSTANCE = new JsonbConverter();

    private static final JsonMapper JSON_MAPPER;

    static {
        // 创建更安全的多态类型验证器，限制允许的基础类型
        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                // 限制仅允许基本数据类型和常用类型
                .allowIfBaseType(Boolean.class)
                .allowIfBaseType(Character.class)
                .allowIfBaseType(Number.class)
                .allowIfBaseType(String.class)
                .allowIfBaseType(Enum.class)
                .allowIfBaseType(Object.class)
                // 必须禁止的类
                .denyForExactBaseType(Runtime.class)
                .denyForExactBaseType(Process.class)
                .denyForExactBaseType(Class.class)
                .denyForExactBaseType(ClassLoader.class)
                .denyForExactBaseType(Thread.class)
                .denyForExactBaseType(System.class)
                .build();

        JSON_MAPPER = JsonMapper.builder()
                // 按照key的字母顺序进行排序，使相同内容输出的json完全相同
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                // 禁用可能导致安全问题的功能
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                // 启用安全的默认类型识别，使用@class属性存储类型信息
                .activateDefaultTypingAsProperty(typeValidator, DefaultTyping.NON_FINAL, "@class")
                // 设置属性包含策略，忽略空值
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(Include.NON_NULL))
                .build();
    }

    /**
     * 序列化.
     */
    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return JSON_MAPPER.writeValueAsString(new Json(attribute));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * 反序列化.
     */
    @Override
    public @Nullable Object convertToEntityAttribute(@Nullable String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // 使用带类型的读取器，提高反序列化准确性
            Json json = JSON_MAPPER.readValue(dbData, Json.class);
            return json.value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to object", e);
        }
    }

    record Json(Object value) {}
}
