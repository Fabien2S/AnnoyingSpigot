package dev.fabien2s.annoyingapi.reflection;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FastReflection {

    public static Field getField(Class<?> clazz, Class<?> typeClass, int index) {

        var tmp = clazz;
        do {

            int i = 0;
            for (Field field : tmp.getDeclaredFields()) {

                Class<?> fieldTypeClass = field.getType();
                if (!fieldTypeClass.isAssignableFrom(typeClass))
                    continue;

                if (i++ != index)
                    continue;

                field.setAccessible(true);
                return field;
            }


        } while ((tmp = tmp.getSuperclass()) != null);

        throw new IllegalArgumentException("Cannot find the field #" + index + " of type " + typeClass.getName() + " in " + clazz.getName());
    }

    public static Collection<Field> getFields(Class<?> clazz, Class<?> typeClass) {
        Set<Field> fieldSet = new HashSet<>();

        var tmp = clazz;
        do {
            int i = 0;
            for (Field field : tmp.getDeclaredFields()) {

                Class<?> fieldTypeClass = field.getType();
                if (!fieldTypeClass.isAssignableFrom(typeClass))
                    continue;

                if (fieldSet.add(field))
                    field.setAccessible(true);
            }


        } while ((tmp = tmp.getSuperclass()) != null);

        return fieldSet;
    }
}
