/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.io.Serializable;
import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 12, 2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WorkspaceFileVersion  implements org.gcube.common.storagehubwrapper.shared.tohl.items.WorkspaceVersion, Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 7925779107708330163L;

	String id;
	String name;
	Calendar created;
	String owner;
	String remotePath;
	Long size;
	boolean isCurrentVersion;

}
