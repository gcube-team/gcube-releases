package org.gcube.documentstore.records.implementation.validations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.documentstore.records.implementation.FieldDecorator;
import org.gcube.documentstore.records.implementation.validations.validators.NotEmptyValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FieldDecorator(action=NotEmptyValidator.class) 
public @interface NotEmpty {

}
