package org.gcube.informationsystem.model.reference.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.relation.Relation;

/**
 * @author Luca Frosini (ISTI - CNR)
 * It indicates that the {@link Entity} or the {@link Relation} is abstract and
 * cannot be instantiated.
 * This is needed because the type definition is made with interface so that
 * even used the java abstract keyword is useless because it cannot be retrieved
 * using reflection (interfaces are always abstract)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Abstract {

}
