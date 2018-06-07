package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

interface NetworkConfiguration {

  public String getProxyHost();

  public String getProxyPort();

  public String getProxyUser();

  public String getProxyPassword();

  public String getNonProxyHosts();

}

class FileBasedProxyConfiguration implements NetworkConfiguration {

  private static PropertiesConfiguration configuration;

  public FileBasedProxyConfiguration(String path) {
    try {
      // load the configuration
      configuration = new PropertiesConfiguration(path);
      // set the reloading strategy to enable hot-configuration
      FileChangedReloadingStrategy fcrs = new FileChangedReloadingStrategy();
      configuration.setReloadingStrategy(fcrs);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getProxyHost() {
    return configuration.getString("proxyHost");
  }

  @Override
  public String getProxyPort() {
    return configuration.getString("proxyPort");
  }

  @Override
  public String getProxyUser() {
    return configuration.getString("proxyUser");
  }

  @Override
  public String getProxyPassword() {
    return configuration.getString("proxyPassword");
  }

  @Override
  public String getNonProxyHosts() {
    return configuration.getString("nonProxyHosts");
  }

}

public class PropertiesBasedProxySelector extends ProxySelector {

  List<Proxy> proxies = null;

  List<String> nonProxyHosts = null;

  public PropertiesBasedProxySelector(String proxySettingsPath) {
    this(new FileBasedProxyConfiguration(proxySettingsPath));
  }

  public PropertiesBasedProxySelector(NetworkConfiguration config) {
    if (config == null || config.getProxyHost() == null) {
      this.proxies = null;
      return;
    }

    String host = config.getProxyHost();

    int port = 80;

    if (config.getProxyPort() != null) {
      port = Integer.valueOf(config.getProxyPort());
    }

    if (config.getNonProxyHosts() != null) {
      this.nonProxyHosts = Arrays
          .asList(config.getNonProxyHosts().split("\\|"));
    }

    this.proxies = new ArrayList<Proxy>();
    this.proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,
        port)));

    if (config.getProxyUser() != null) {
      final String username = config.getProxyUser();
      final String password = config.getProxyPassword();

      Authenticator.setDefault(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password.toCharArray());
        }
      });

    }
  }

  @Override
  public List<Proxy> select(URI uri) {
    if (this.nonProxyHosts == null) {
      return Arrays.asList(Proxy.NO_PROXY);
    } else {
      for (String entry : this.nonProxyHosts) {
        entry = entry.trim();
        if (entry.startsWith("*") && uri.getHost().endsWith(entry.substring(1))) {
          return Arrays.asList(Proxy.NO_PROXY);
        }
        if (uri.getHost().equals(entry)) {
          return Arrays.asList(Proxy.NO_PROXY);
        }
      }
      return this.proxies;
    }
  }

  @Override
  public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {

  }
}
