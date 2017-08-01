package org.gcube.informationsystem.resource_checker;

import java.util.HashMap;
import java.util.Map;

import org.gcube.informationsystem.resource_checker.ResourceCheckerPlugin;
import org.gcube.informationsystem.resource_checker.utils.SendNotification;

public class TestResourceChecker{

	//@Test
	public void launchPlugin() throws Exception{

		//		ScopeProvider.instance.set(....);
		//		SecurityTokenProvider.instance.set(....);
		ResourceCheckerPlugin checker = new ResourceCheckerPlugin(null);
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put(SendNotification.RECIPIENT_KEY, null);
		inputs.put(ResourceCheckerPlugin.ROLE_TO_NOTIFY, null);
		checker.launch(inputs);

	}

}
