/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.documentstore.records.implementation.FieldDecorator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FieldDecorator(action=MoveToTaskStartTimeAction.class) 
public @interface MoveToTaskStartTime {
	
}
