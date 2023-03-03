package dev.banji.LibReserve.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalTime;

@Converter
public class LocalTimeToHourConverter implements AttributeConverter<LocalTime, Integer> {
    @Override
    public Integer convertToDatabaseColumn(LocalTime time) {
        return time.getHour();
    }

    @Override
    public LocalTime convertToEntityAttribute(Integer hour) {
        return LocalTime.of(hour,0);
    }
}
