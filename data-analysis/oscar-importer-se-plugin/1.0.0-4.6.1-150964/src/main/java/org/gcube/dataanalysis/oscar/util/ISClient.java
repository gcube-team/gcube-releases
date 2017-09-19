package org.gcube.dataanalysis.oscar.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

public class ISClient {

//  public List<URL> retrieveServiceAddress(String Category, String Name, String exclude) {
//    SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
//    query.addCondition("$resource/Profile/Category/text() eq '" + Category + "'").addCondition("$resource/Profile/Name/text() eq '" + Name+ "'").addCondition("$resource/Profile[Name[not(contains(., '" + exclude + "'))]]").setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
//    DiscoveryClient<String> client = ICFactory.client();
//    return this.toURL(client.submit(query));
//  }  
  
  public List<URL> getThreddsServicesIDs() {
    return this.retrieveService("SDI", "Thredds");
  }
  
  private List<URL> retrieveService(String serviceClass, String serviceName) {
    SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
    query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'");
    query.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");
    query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");
    DiscoveryClient<String> client = ICFactory.client();
    return this.toURL(client.submit(query));
  }

//  public List<URL> retrieveService(String service) {
//    SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
//    query.addCondition("$resource/Profile/ServiceName/text() eq '"+service+"'").setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");
//    DiscoveryClient<String> client = ICFactory.client();
//    return this.toURL(client.submit(query));
//  }
  
  private List<URL> toURL(List<String> addresses) {
    List<URL> out = new Vector<>();
    for(String s:addresses) {
      try {
        out.add(new URL(s));
      } catch(MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return out;
  }
  
}
