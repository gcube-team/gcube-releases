/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * The Class WorkspaceFolderImpl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 15, 2018
 */
@Getter

/**
 * Instantiates a new workspace folder impl.
 */
@NoArgsConstructor
@Setter
@ToString(callSuper=true)
public class WorkspaceSharedFolder extends WorkspaceFolder implements org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder{

	private static final long serialVersionUID = -6493111521916500793L;

	private boolean isVreFolder;

}