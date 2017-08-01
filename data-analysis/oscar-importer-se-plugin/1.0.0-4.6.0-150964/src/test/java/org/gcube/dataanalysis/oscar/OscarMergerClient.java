package org.gcube.dataanalysis.oscar;

import java.net.ProxySelector;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.oscar.util.PropertiesBasedProxySelector;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.InputsNullException;
import org.gcube.vremanagement.executor.exception.LaunchException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.quartz.CronExpression;

public class OscarMergerClient {

  public void launch(boolean scheduled) throws InputsNullException, PluginNotFoundException, LaunchException, ExecutorException, ParseException {
    
    SmartExecutorProxy proxy = ExecutorPlugin.getExecutorProxy("oscar-importer-se-plugin").build();
    Map<String, Object> params = new HashMap<>();
    params.put("debug", Boolean.TRUE);
    params.put("removeTmpFiles", Boolean.FALSE);
    params.put("uploadWithCurl", Boolean.TRUE);
    params.put("intervalSize", new Integer(1));
    LaunchParameter parameter = new LaunchParameter("oscar-importer-se-plugin", params);
    
    if(scheduled) {
      CronExpression cronExpression = new CronExpression("0 */10 * * * ?"); // every 10 minutes starting from now
      Scheduling scheduling = new Scheduling(cronExpression, true);
      parameter.setScheduling(scheduling);
    }
      
    // after a while you can check the status of the launch
    String uuidPluginLaunched = proxy.launch(parameter);
    System.out.println(uuidPluginLaunched);
    
    PluginStateEvolution state = proxy.getStateEvolution(uuidPluginLaunched);
    System.out.println(state.getPluginState());
  }

  private void stop(String id) throws ExecutorException {
    SmartExecutorProxy proxy = ExecutorPlugin.getExecutorProxy("oscar-importer-se-plugin").build();
    boolean unscheduled = proxy.unSchedule(id, true);
    System.out.println(unscheduled);
  }

  public static void main(String[] args) throws Exception {
    ProxySelector.setDefault(new PropertiesBasedProxySelector("/home/paolo/.proxy-settings"));
    ScopeProvider.instance.set("/gcube/devNext/NextNext");
    SecurityTokenProvider.instance.set("-----");
    new OscarMergerClient().launch(false);
  }
  
}
