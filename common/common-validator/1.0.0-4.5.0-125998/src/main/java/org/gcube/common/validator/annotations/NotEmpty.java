package org.gcube.common.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.common.validator.annotations.validators.NotEmptyValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ValidityChecker(managed=NotEmptyValidator.class) 
public @interface NotEmpty {

}
