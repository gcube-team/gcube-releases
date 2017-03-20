package org.gcube.data.transfer.plugin.decompress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugins.decompress.DecompressPlugin;
import org.gcube.data.transfer.plugins.decompress.DecompressPluginFactory;
import org.junit.Assert;

public class UnzipTest {

	public static void main(String[] args) throws IOException {
//		DecompressPlugin.unzip(new File("/home/fabio/workspaces/home-library-jcr-PRE-PROD.jar"), "/home/fabio/workspaces/unzips/hl", true);
		
		
		Map<String,String> params=new HashMap<>();
		params.put(DecompressPluginFactory.DESTINATION_PARAMETER, "here");
		params.put(DecompressPluginFactory.SOURCE_PARAMETER, "/home/fabio/workspaces/home-library-jcr-PRE-PROD.jar");
		PluginInvocation invocation=new PluginInvocation("", params);
		
		
		new DecompressPluginFactory().createWorker(invocation).execute();
		
		
		
		
	}

}
