package me.ziyframework.module.webmvc.jackson.modifier;

import java.util.Collection;
import java.util.List;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

/**
 * 处理Null的反序列化.<br />
 * created on 2025-01
 * @author ziy
 */
public class NullValueSerializerModifier extends ValueSerializerModifier {

    public static final NullValueSerializerModifier INSTANCE = new NullValueSerializerModifier();

    @Override
    public final List<BeanPropertyWriter> changeProperties(
            SerializationConfig _config, BeanDescription.Supplier _beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            JavaType type = beanProperty.getType();
            if (isArrayLikeType(type)) {
                beanProperty.assignNullSerializer(NullArrayLikeSerializer.INSTANCE);
            } else if (isMapType(type)) {
                beanProperty.assignNullSerializer(NullMapSerializer.INSTANCE);
            }
        }
        return beanProperties;
    }

    private boolean isMapType(JavaType type) {
        return type.isMapLikeType();
    }

    private boolean isArrayLikeType(JavaType type) {
        Class<?> rawClass = type.getRawClass();
        return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);
    }

    /**
     * 处理空数组/集合等类似结构的序列化.
     */
    public static final class NullArrayLikeSerializer extends ValueSerializer<Object> {

        public static final NullArrayLikeSerializer INSTANCE = new NullArrayLikeSerializer();

        private NullArrayLikeSerializer() {}

        @Override
        public void serialize(Object _value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            gen.writeStartArray();
            gen.writeEndArray();
        }
    }

    // null Map序列化.
    public static final class NullMapSerializer extends ValueSerializer<Object> {

        public static final NullMapSerializer INSTANCE = new NullMapSerializer();

        private NullMapSerializer() {}

        @Override
        public void serialize(Object _value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            gen.writeStartObject();
            gen.writeEndObject();
        }
    }
}
