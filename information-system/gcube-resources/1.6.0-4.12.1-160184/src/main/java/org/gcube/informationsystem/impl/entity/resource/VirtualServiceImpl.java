/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.VirtualService;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=VirtualService.NAME)
public class VirtualServiceImpl extends ServiceImpl implements VirtualService {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 4784559176034478276L;

}
