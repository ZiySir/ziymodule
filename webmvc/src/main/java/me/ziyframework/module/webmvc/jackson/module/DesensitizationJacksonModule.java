package me.ziyframework.module.webmvc.jackson.module;

import me.ziyframework.module.webmvc.jackson.modifier.DesensitizationValueSerializerModifier;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;

public class DesensitizationJacksonModule extends JacksonModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return "DesensitizationJacksonModule";
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
    public void setupModule(SetupContext context) {
        context.addSerializerModifier(DesensitizationValueSerializerModifier.INSTANCE);
    }
}
