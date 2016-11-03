package org.gcube.documentstore.records.implementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotations indicates that the field is related to an aggregation
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregatedField {

}
