package me.ziyframework.module.webmvc.jackson;

import me.ziyframework.framework.enumeration.BaseEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 枚举反序列化为整数类型.<br />
 * created on 2025-02
 *
 * @author ziy
 */
public final class BaseEnumSerializer extends ValueSerializer<BaseEnum> {

    public static final BaseEnumSerializer INSTANCE = new BaseEnumSerializer();

    private BaseEnumSerializer() {}

    @Override
    public void serialize(BaseEnum value, JsonGenerator gen, SerializationContext _ctxt) throws JacksonException {
        gen.writeNumber(value.getCode());
    }
}
