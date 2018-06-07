/**
 * 
 */
package org.gcube.common.homelibrary;

import junit.framework.Assert;

import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.junit.Test;

/**
 * @author lucio
 *
 */
public class GenericTest {

	@Test
	public void mimeType(){
		String res = MimeTypeUtil.getExtension("text/html");
		Assert.assertEquals("stm", res);
	}
	
}
