package org.gcube.data.analysis.tconnector.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;

import static org.gcube.data.analysis.rconnector.client.Constants.*;
public class TESTConnection {

	@Test
	public void connect(){
		ScopeProvider.instance.set("/gcube/devsec");
		SecurityTokenProvider.instance.set("df75336d-0944-4324-b444-c711d21f705b");
		System.out.println(rConnector().build().connect());
	}

	@Test
	public void  testte(){
		Pattern pattern = Pattern.compile("[^/]*//([^:]*)[^/]*/(.*)");
		
		Matcher m = pattern.matcher("jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/tabulardata");
		
		m.find();
		System.out.println(m.group(1));
		System.out.println(m.group(2));
		
		
	}

}
