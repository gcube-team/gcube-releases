package org.gcube.data.analysis.tabulardata.operation.data;

import java.util.regex.Pattern;

import org.gcube.data.analysis.tabulardata.operation.data.transformation.csquare.DownScaleCsquareFactory;
import org.junit.Test;

public class RegexpTest {

	@Test
	public void testRegexp(){
		String code="1000:100:1";
		Pattern.matches(DownScaleCsquareFactory.CSQUARE_REGEXP, code);
	}
	
}
