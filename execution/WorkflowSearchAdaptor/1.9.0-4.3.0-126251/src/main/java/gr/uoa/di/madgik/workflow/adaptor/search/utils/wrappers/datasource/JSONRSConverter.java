package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

public class JSONRSConverter implements IObjectConverter {

	private Gson gson = new Gson();
	
	@Override
	public Object Convert(String serialization) throws Exception {
		Map<String, String> resp = gson.fromJson(serialization,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp.get("grslocator");
	}

	@Override
	public String Convert(Object o) throws Exception {
		String rsLocator = o.toString();
		Map<String, String> resp = new HashMap<String, String>();
		resp.put("grslocator",	rsLocator);
		return gson.toJson(resp);
	}

}
