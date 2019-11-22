/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Instantiates a new workspace folder impl.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString(callSuper=true)
public class WorkspaceSharedFolder extends WorkspaceFolder implements org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder{

	private static final long serialVersionUID = -6493111521916500793L;

	private boolean isVreFolder;

}