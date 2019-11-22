

import java.net.ProxySelector;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.ISClient;
import org.gcube.dataanalysis.dataminer.poolmanager.util.PropertiesBasedProxySelector;

public class ISClientTest {

  public static void main(String[] args) {
    ProxySelector.setDefault(new PropertiesBasedProxySelector("/home/ngalante/.proxy-settings"));
    ScopeProvider.instance.set("/gcube/devNext/NextNext");
    System.out.println(new ISClient().listDataminersInVRE());
  }

}
