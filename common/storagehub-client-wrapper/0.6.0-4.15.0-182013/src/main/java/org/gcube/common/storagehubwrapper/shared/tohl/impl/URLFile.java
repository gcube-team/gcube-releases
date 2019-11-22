/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.net.URL;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 21, 2018
 */
@Getter
@Setter
@ToString
public class URLFile extends WorkspaceItem implements org.gcube.common.storagehubwrapper.shared.tohl.items.URLItem{

	private static final long serialVersionUID = -3729978614980700669L;
	
	private URL value;


}
