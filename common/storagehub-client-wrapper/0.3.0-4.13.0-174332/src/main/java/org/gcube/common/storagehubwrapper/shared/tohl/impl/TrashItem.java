/**
 *
 */

package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.gcube.common.storagehubwrapper.shared.tohl.TrashedItem;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TrashItem extends WorkspaceItem implements TrashedItem{

	/**
	 *
	 */
	private static final long serialVersionUID = -2070116308660673836L;

	String name;
	String deletedBy;
	String originalParentId;
	String deletedFrom;
	Calendar deletedTime;
	String mimeType;
	long lenght;
	//DO NOT ADD isFolder. The field is implemnted in the WorkspaceItem

}
