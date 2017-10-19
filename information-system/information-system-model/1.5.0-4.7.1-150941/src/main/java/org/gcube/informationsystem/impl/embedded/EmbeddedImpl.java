/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import org.gcube.informationsystem.impl.ISManageableImpl;
import org.gcube.informationsystem.model.embedded.Embedded;

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
