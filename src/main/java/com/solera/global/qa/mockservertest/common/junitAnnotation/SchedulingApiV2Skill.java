package com.solera.global.qa.mockservertest.common.junitAnnotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.solera.global.qa.mockservertest.common.junitExtension.SchedulingApiV2SkillExtension;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

@Target({TYPE, METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@ExtendWith(SchedulingApiV2SkillExtension.class)
public @interface SchedulingApiV2Skill {}
