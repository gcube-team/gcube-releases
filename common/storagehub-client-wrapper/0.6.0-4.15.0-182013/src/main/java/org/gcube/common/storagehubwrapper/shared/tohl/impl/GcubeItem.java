/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GcubeItem extends FileItem implements org.gcube.common.storagehubwrapper.shared.tohl.items.GCubeItem {

	/**
	 *
	 */
	private static final long serialVersionUID = -4523330832448380056L;


	String[] scopes;

	String creator;

	String itemType;

	String properties;

	boolean shared;

	PropertyMap property;


}
