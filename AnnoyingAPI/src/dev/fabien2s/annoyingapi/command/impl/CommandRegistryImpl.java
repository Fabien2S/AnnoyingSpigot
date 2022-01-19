package dev.fabien2s.annoyingapi.command.impl;

import dev.fabien2s.annoyingapi.command.ICommandRegistry;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.command.argument.EnumArgumentConverter;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;

import java.util.HashSet;
import java.util.Set;

public abstract class CommandRegistryImpl<T> implements ICommandRegistry<T> {

    private final Set<ArgumentConverterEntry<?>> converterEntries = new HashSet<>();

    public CommandRegistryImpl() {
        this.registerArgumentConverter(Enum.class, new EnumArgumentConverter<>());
    }

    @Override
    public <U> void registerArgumentConverter(Class<U> argClass, ICommandArgumentConverter<T, U, ?> argumentConverter) {
        this.converterEntries.add(new ArgumentConverterEntry<>(
                argClass, argumentConverter
        ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> ICommandArgumentConverter<T, U, ?> getArgumentConverter(Class<U> argClass) {
        for (ArgumentConverterEntry<?> converterEntry : converterEntries) {
            if (converterEntry.clazz.isAssignableFrom(argClass))
                return (ICommandArgumentConverter<T, U, ?>) converterEntry.converter;
        }
        return null;
    }

    @RequiredArgsConstructor
    private class ArgumentConverterEntry<U> {
        private final Class<U> clazz;
        private final ICommandArgumentConverter<T, U, ?> converter;
    }

}
