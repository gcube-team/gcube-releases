/**
 * 
 */
package org.gcube.informationsystem.model.entity.resource;

import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Hosting_Node
 */
@JsonDeserialize(as=HostingNodeImpl.class)
public interface HostingNode extends Service {

	public static final String NAME = "HostingNode"; //HostingNode.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Hosting Node information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
