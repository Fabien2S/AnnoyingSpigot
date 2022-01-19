package dev.fabien2s.annoyingapi.command.annotation;

import com.mojang.brigadier.arguments.StringArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {

    String name();

    StringArgumentType.StringType type() default StringArgumentType.StringType.SINGLE_WORD;

    Class<?> suggestionProvider() default Object.class;

    double min() default Double.NaN;

    double max() default Double.NaN;

}
