package com.herhelevych.taskflow.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventIfArchived {
    TargetType type() default TargetType.PROJECT;
    String idParam() default "projectId";
}
