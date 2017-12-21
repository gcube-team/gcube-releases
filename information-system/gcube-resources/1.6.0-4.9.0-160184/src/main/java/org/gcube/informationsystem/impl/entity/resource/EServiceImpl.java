/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.EService;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=EService.NAME)
public class EServiceImpl extends ServiceImpl implements EService {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -1211338661607479729L;

}
