package org.gcube.data.analysis.tabulardata.operation.test.util;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CodelistHelperTest {
	
	@Inject
	CodelistHelper codelistHelper;
	
	@Test
	public void test(){
		codelistHelper.createSpeciesCodelist();
	}

}
