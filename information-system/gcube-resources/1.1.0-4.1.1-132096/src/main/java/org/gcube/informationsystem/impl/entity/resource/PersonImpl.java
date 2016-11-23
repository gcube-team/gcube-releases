/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.Person;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Person.NAME)
public class PersonImpl extends ActorImpl implements Person {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 8490450905022409272L;
	
}
