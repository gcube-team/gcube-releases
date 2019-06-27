/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;

import java.io.Serializable;


/**
 * The Enum FilterOperator.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 12, 2018
 */
public enum FilterOperator implements Serializable{

	LOGICAL_OR,
	LOGICAL_AND,
	LOGICAL_NOT;
}
