package org.gcube.data.analysis.tabulardata.commons.templates;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.utils.EntityList;
import org.junit.Test;

public class IteratorTest {

	@Test
	public void iterate(){
		List<String> entities = Arrays.asList("u(lucio.lelii)","u(lucio)","g(/gcube/devsec/devvre)");
			
		Iterator<String> it = EntityList.getUserList(entities).iterator();
		while (it.hasNext())
			System.out.println(it.next());
	}
}
