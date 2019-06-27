/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.impl;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 22, 2018
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMap implements org.gcube.common.storagehubwrapper.shared.tohl.items.PropertyMap{

	Map<String, Object> values = new HashMap<String, Object>();
}
