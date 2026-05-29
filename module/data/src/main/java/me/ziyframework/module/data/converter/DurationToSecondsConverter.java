package me.ziyframework.module.data.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter
public class DurationToSecondsConverter implements AttributeConverter<Duration, Long> {

    /**
     * {@inheritDoc}.
     */
    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        return attribute.toSeconds();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        return Duration.ofSeconds(dbData);
    }
}
