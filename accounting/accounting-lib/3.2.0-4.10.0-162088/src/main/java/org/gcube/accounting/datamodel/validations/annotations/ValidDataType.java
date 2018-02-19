package org.gcube.accounting.datamodel.validations.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.accounting.datamodel.validations.validators.ValidDataTypeValidator;
import org.gcube.documentstore.records.implementation.FieldDecorator;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FieldDecorator(action=ValidDataTypeValidator.class) 
public @interface ValidDataType {

}
