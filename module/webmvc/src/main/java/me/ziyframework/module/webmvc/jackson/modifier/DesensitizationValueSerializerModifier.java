package me.ziyframework.module.webmvc.jackson.modifier;

import java.util.List;
import me.ziyframework.boot.core.SpringHolder;
import me.ziyframework.module.webmvc.common.desensitize.DesensitizationStrategy;
import me.ziyframework.module.webmvc.common.desensitize.DesensitizationUtil;
import me.ziyframework.module.webmvc.common.desensitize.Desensitized;
import me.ziyframework.module.webmvc.common.desensitize.IndexDesensitized;
import me.ziyframework.module.webmvc.common.desensitize.RegexDesensitized;
import me.ziyframework.module.webmvc.common.desensitize.SlideDesensitized;
import org.springframework.core.annotation.AnnotationUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

/**
 * 脱敏Jackson序列化器.<br />
 * created on 2025-02
 *
 * @author ziy
 */
@SuppressWarnings("checkstyle:InnerAssignment")
public class DesensitizationValueSerializerModifier extends ValueSerializerModifier {

    public static final DesensitizationValueSerializerModifier INSTANCE = new DesensitizationValueSerializerModifier();

    @Override
    public final List<BeanPropertyWriter> changeProperties(
            SerializationConfig _config, BeanDescription.Supplier _beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            Desensitized desensitized;
            SlideDesensitized slideDesensitized;
            RegexDesensitized regexDesensitized;
            IndexDesensitized indexDesensitized;
            if ((desensitized = beanProperty.getAnnotation(Desensitized.class)) != null) {
                beanProperty.assignSerializer(new DesensitizedJsonSerializer(desensitized));
                continue;
            }
            if (beanProperty.getType().getRawClass() == String.class) {
                if ((slideDesensitized = beanProperty.getAnnotation(SlideDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new SlideDesensitizedJsonSerializer(slideDesensitized));
                } else if ((regexDesensitized = beanProperty.getAnnotation(RegexDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new RegexDesensitizedJsonSerializer(regexDesensitized));
                } else if ((indexDesensitized = beanProperty.getAnnotation(IndexDesensitized.class)) != null) {
                    beanProperty.assignSerializer(new IndexDesensitizedJsonSerializer(indexDesensitized));
                }
            }
        }
        return beanProperties;
    }

    private static final class DesensitizedJsonSerializer extends ValueSerializer<Object> {

        private final Desensitized desensitized;

        private DesensitizedJsonSerializer(Desensitized desensitized) {
            this.desensitized = desensitized;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            String bean = (String) AnnotationUtils.getValue(desensitized);
            DesensitizationStrategy strategy;
            if (bean == null || bean.isEmpty()) {
                strategy = desensitized.strategy();
            } else {
                strategy = SpringHolder.getBean(bean, DesensitizationStrategy.class);
            }
            Object desensitize = strategy.desensitize(value);
            gen.writePOJO(desensitize);
        }
    }

    private static final class SlideDesensitizedJsonSerializer extends ValueSerializer<Object> {

        private final SlideDesensitized desensitized;

        private SlideDesensitizedJsonSerializer(SlideDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.slide(
                        str, desensitized.left(), desensitized.right(), desensitized.mask(), desensitized.reverse()));
            }
        }
    }

    private static final class RegexDesensitizedJsonSerializer extends ValueSerializer<Object> {

        private final RegexDesensitized desensitized;

        private RegexDesensitizedJsonSerializer(RegexDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.regex(str, desensitized.regex(), desensitized.replace()));
            }
        }
    }

    private static final class IndexDesensitizedJsonSerializer extends ValueSerializer<Object> {

        private final IndexDesensitized desensitized;

        private IndexDesensitizedJsonSerializer(IndexDesensitized desensitized) {
            this.desensitized = desensitized;
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
            if (value instanceof String str) {
                gen.writeString(DesensitizationUtil.index(
                        str, desensitized.mask(), desensitized.reverse(), desensitized.indexes()));
            }
        }
    }
}
