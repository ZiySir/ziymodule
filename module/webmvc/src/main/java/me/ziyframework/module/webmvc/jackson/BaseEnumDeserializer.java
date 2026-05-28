package me.ziyframework.module.webmvc.jackson;

import com.google.common.base.Preconditions;
import me.ziyframework.framework.enumeration.BaseEnum;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * BaseEnum反序列化.<br />
 * created on 2025-03
 *
 * @author ziy
 */
public final class BaseEnumDeserializer extends ValueDeserializer<BaseEnum> {

    public static final BaseEnumDeserializer INSTANCE = new BaseEnumDeserializer();

    private @Nullable Class<? extends BaseEnum> baseEnumCls;

    private BaseEnumDeserializer() {}

    /**
     * BaseEnum反序列化.
     */
    @Override
    public BaseEnum deserialize(JsonParser parser, DeserializationContext _ctxt) {
        // 获取JSON输入值（支持数字或字符串类型的code）
        Integer codeValue = parser.readValueAs(Integer.class);
        // 调用BaseEnum的fromCode方法获取枚举实例
        BaseEnum baseEnum =
                BaseEnum.fromCode(codeValue, Preconditions.checkNotNull(baseEnumCls, "BaseEnum class not set"));
        if (baseEnum == null) {
            throw new IllegalArgumentException("无效值" + codeValue);
        }
        return baseEnum;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueDeserializer<?> createContextual(
            DeserializationContext _deserializationContext, BeanProperty beanProperty) {
        BaseEnumDeserializer deserializer = new BaseEnumDeserializer();
        JavaType type = beanProperty.getType();
        Class<?> rawClass = type.getRawClass();
        deserializer.baseEnumCls = (Class<? extends BaseEnum>) rawClass;
        return deserializer;
    }
}
