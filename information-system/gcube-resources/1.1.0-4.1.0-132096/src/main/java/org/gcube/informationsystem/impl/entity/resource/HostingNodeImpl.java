/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.HostingNode;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=HostingNode.NAME)
public class HostingNodeImpl extends ServiceImpl implements HostingNode {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 4432884828103841956L;
	
}
