package me.ziyframework.module.webmvc.jackson.module;

import me.ziyframework.framework.enumeration.BaseEnum;
import me.ziyframework.module.webmvc.jackson.BaseEnumDeserializer;
import me.ziyframework.module.webmvc.jackson.BaseEnumSerializer;
import me.ziyframework.module.webmvc.jackson.modifier.BaseEnumValueDeserializerModifier;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleSerializers;

public class BaseEnumJacksonModule extends JacksonModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return "BaseEnumJacksonModule";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer((Class) BaseEnum.class, BaseEnumSerializer.INSTANCE);
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer((Class) BaseEnum.class, BaseEnumDeserializer.INSTANCE);
        context.addDeserializers(deserializers);

        context.addDeserializerModifier(BaseEnumValueDeserializerModifier.INSTANCE);
    }
}
