package org.gcube.accounting.datamodel.deprecationmanagement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.documentstore.records.implementation.FieldDecorator;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FieldDecorator(action=DeprecatedWarningAction.class) 
public @interface DeprecatedWarning {

}
