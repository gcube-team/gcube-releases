/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.documentstore.records.implementation.FieldDecorator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@FieldDecorator(action=MoveToTaskWallDurationAction.class) 
public @interface MoveToTaskWallDuration { }