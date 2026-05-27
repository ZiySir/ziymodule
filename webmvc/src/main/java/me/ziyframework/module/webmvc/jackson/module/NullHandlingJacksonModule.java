package me.ziyframework.module.webmvc.jackson.module;

import me.ziyframework.module.webmvc.jackson.modifier.NullValueSerializerModifier;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;

public class NullHandlingJacksonModule extends JacksonModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModuleName() {
        return "nullHandingJacksonModule";
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
        context.addSerializerModifier(NullValueSerializerModifier.INSTANCE);
    }
}
