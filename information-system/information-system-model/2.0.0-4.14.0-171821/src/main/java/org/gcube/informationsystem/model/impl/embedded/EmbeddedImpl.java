/**
 * 
 */
package org.gcube.informationsystem.model.impl.embedded;

import org.gcube.informationsystem.model.impl.ISManageableImpl;
import org.gcube.informationsystem.model.reference.embedded.Embedded;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@JsonTypeName(value=Embedded.NAME)
public class EmbeddedImpl extends ISManageableImpl implements Embedded {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1396998430221747445L;

	public EmbeddedImpl() {
		super();
	}
}
