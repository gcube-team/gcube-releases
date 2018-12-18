/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



/**
 * The Class WorkspaceFolder.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 20, 2018
 */

/**
 * Instantiates a new workspace folder.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper=true)
public class WorkspaceFolder extends WorkspaceItem implements org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder{
	/*
	 *
	 */
	private static final long serialVersionUID = -3767943529796942863L;

	private boolean isPublicFolder;



}
