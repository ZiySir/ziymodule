package me.ziyframework.module.webmvc.jackson.modifier;

import me.ziyframework.framework.enumeration.BaseEnum;
import me.ziyframework.module.webmvc.jackson.BaseEnumDeserializer;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.BeanDescription.Supplier;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.ValueDeserializerModifier;

public final class BaseEnumValueDeserializerModifier extends ValueDeserializerModifier {

    public static final BaseEnumValueDeserializerModifier INSTANCE = new BaseEnumValueDeserializerModifier();

    private BaseEnumValueDeserializerModifier() {}

    @Override
    public ValueDeserializer<?> modifyDeserializer(
            DeserializationConfig _config, BeanDescription.Supplier beanDesc, ValueDeserializer<?> deserializer) {
        Class<?> cls = beanDesc.getBeanClass();
        if (BaseEnum.class.isAssignableFrom(cls)) {
            return BaseEnumDeserializer.INSTANCE;
        }
        return deserializer;
    }

    @Override
    public ValueDeserializer<?> modifyEnumDeserializer(
            DeserializationConfig _config, JavaType _type, Supplier beanDescRef, ValueDeserializer<?> deserializer) {
        Class<?> cls = beanDescRef.getBeanClass();
        if (BaseEnum.class.isAssignableFrom(cls)) {
            return BaseEnumDeserializer.INSTANCE;
        }
        return deserializer;
    }
}
