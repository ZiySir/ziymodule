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
public final class BaseEnumDeserializer extends ValueDeserializer<BaseEnum<?>> {

    public static final BaseEnumDeserializer INSTANCE = new BaseEnumDeserializer();

    private @Nullable Class<?> baseEnumCls;

    private BaseEnumDeserializer() {}

    /**
     * BaseEnum反序列化.
     */
    @Override
    public BaseEnum<?> deserialize(JsonParser parser, DeserializationContext _ctxt) {
        // 获取JSON输入值（支持数字或字符串类型的code）
        Integer codeValue = parser.readValueAs(Integer.class);
        // 调用BaseEnum的fromCode方法获取枚举实例
        Class<?> cls = Preconditions.checkNotNull(baseEnumCls, "BaseEnum class not set");
        BaseEnum<?> baseEnum = fromCodeChecked(codeValue, cls);
        if (baseEnum == null) {
            throw new IllegalArgumentException("无效值" + codeValue);
        }
        return baseEnum;
    }

    /**
     * 在运行时已知Class一定为枚举类型的前提下做受控转换.
     */
    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & BaseEnum<E>> @Nullable BaseEnum<?> fromCodeChecked(Integer code, Class<?> cls) {
        return BaseEnum.fromCode(code, (Class<E>) cls);
    }

    @Override
    public ValueDeserializer<?> createContextual(
            DeserializationContext _deserializationContext, BeanProperty beanProperty) {
        BaseEnumDeserializer deserializer = new BaseEnumDeserializer();
        JavaType type = beanProperty.getType();
        deserializer.baseEnumCls = type.getRawClass();
        return deserializer;
    }
}
