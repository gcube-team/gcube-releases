package org.gcube.dataanalysis.dataminerpoolmanager;

import java.net.ProxySelector;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.PropertiesBasedProxySelector;

public class DataminerPoolManagerTest {

  /*
  private static Map<String, Domain> domains = new HashMap<>();
  private static Map<String, Cluster> clusters = new HashMap<>();
  
  private static Dependency createDependency(String depName) {
    String[] parts = depName.split(":");
    Dependency out = new Dependency();
    if(parts.length>1) {
      out.setType(parts[0]);
      out.setName(parts[1]);
    } else {
      out.setType("os");
      out.setName(depName);
    }
    return out;
  }
  
  private static Algorithm createAlgorithm(String name, String ... deps) {
    Algorithm a = new Algorithm();
    a.setName(name);
    for(String dep:deps) {
      a.addDependency(createDependency(dep));
    }
    return a;
  }
  
  private static AlgorithmSet createAlgorithmSet(String name, Algorithm ... algs) {
    AlgorithmSet out = new AlgorithmSet();
    out.setName(name);
    for(Algorithm a:algs) {
      out.addAlgorithm(a);
    }
    return out;
  }
  
  private static Domain getDomain(String name) {
    if(domains.get(name)==null) {
      Domain d = new Domain();
      d.setName(name);
      domains.put(name, d);
      return d;
    } else {
      return domains.get(name);
    }
  }
  
  private static Host createHost(String hostname, String domainName) {
    Host out = new Host();
    out.setName(hostname);
    Domain d = getDomain(domainName);
    out.setDomain(d);
    return out;
  }
  
  private static Cluster getCluster(String name) {
    if(clusters.get(name)==null) {
      Cluster d = new Cluster();
      d.setName(name);
      clusters.put(name, d);
      return d;
    } else {
      return clusters.get(name);
    }
  }  
  
  private static Collection<Dependency> extractDependencies() {
    Collection<Dependency> out = new TreeSet<>(new DependencyComparator());
    for(Cluster c:clusters.values()) {
      for(AlgorithmSet as:c.getAlgorithmSets()) {
        for(Algorithm a:as.getAlgorithms()) {
          for(Dependency d:a.getDependencies()) {
            out.add(d);
          }
        }
      }
    }
    return out;
  }

  private static Collection<Algorithm> extractAlgorithms() {
    Collection<Algorithm> out = new TreeSet<>(new AlgorithmComparator());
    for(Cluster c:clusters.values()) {
      for(AlgorithmSet as:c.getAlgorithmSets()) {
        for(Algorithm a:as.getAlgorithms()) {
          out.add(a);
        }
      }
    }
    return out;
  }

  static {
    
    Algorithm ewe = createAlgorithm("ewe", "mono", "latex", "cran:some_R_package", "custom:some_git_package");
    Algorithm ensemble = createAlgorithm("ensemble", "python");
    Algorithm voodoo = createAlgorithm("voodoo", "os:latex", "custom:blah");
    
    AlgorithmSet as1 = createAlgorithmSet("as1-fishes", ewe);
    AlgorithmSet as2 = createAlgorithmSet("as2-stat", ensemble);
    AlgorithmSet as3 = createAlgorithmSet("as3-blackmagic", voodoo, ewe);

    Cluster cluster1 = getCluster("cluster-1");
    cluster1.addHost(createHost("host1", "domain1"));
    cluster1.addHost(createHost("host2", "domain1"));
    cluster1.addHost(createHost("host3", "domain1"));
    cluster1.addHost(createHost("host1", "domain2"));
    cluster1.addHost(createHost("host2", "domain2"));

    Cluster cluster2 = getCluster("cluster-2");
    cluster2.addHost(createHost("host4", "domain1"));
    cluster2.addHost(createHost("host5", "domain1"));
    cluster2.addHost(createHost("host6", "domain1"));
    cluster2.addHost(createHost("host3", "domain2"));
    cluster2.addHost(createHost("host4", "domain2"));
    cluster2.addHost(createHost("host5", "domain2"));
    
    cluster1.addAlgorithmSet(as1);
    cluster1.addAlgorithmSet(as2);

    cluster2.addAlgorithmSet(as1);
    cluster2.addAlgorithmSet(as3);
    
  }
  */
  
  public static void main(String[] args) throws Exception {
    /*
    AnsibleBridge ab = new AnsibleBridge();
    ab.printInventoryByDomainAndSets(clusters.values());
    System.out.println("-----------");
    ab.printInventoryBySets(clusters.values());

    AnsibleWorker worker = ab.createWorker();
    
    for(Algorithm a:extractAlgorithms()) {
      for(Role r:ab.generateRoles(a)) {
        worker.addRole(r);
      }
    }

    for(Dependency d:extractDependencies()) {
      for(Role r:ab.generateRoles(d)) {
        worker.addRole(r);
      }
    }
    */
    
    ScopeProvider.instance.set("/gcube/devNext/NextNext");
    
    ProxySelector.setDefault(new PropertiesBasedProxySelector("/home/ngalante/.proxy-settings"));
//    
//    // create the algorithm (download it, etc etc)
//    Algorithm algorithm = new Algorithm();
//    algorithm.setName("ichtyop");
//    algorithm.setClazz("org.gcube...");
//    algorithm.setDescription("some description");
//    
//    Dependency d = new Dependency();
//    d.setName("libpng");
//    d.setType("os");
//    algorithm.addDependency(d);
//
//    d = new Dependency();
//    d.setName("some-r-package");
//    d.setType("cran");
//    algorithm.addDependency(d);
//
//    d = new Dependency();
//    d.setName("some-other-r-package");
//    d.setType("cran");
//    algorithm.addDependency(d);
//
//    d = new Dependency();
//    d.setName("voodoo");
//    d.setType("custom");
//    algorithm.addDependency(d);
//
//    // create the algorithm (download it, etc etc)
//    Algorithm ewe = new Algorithm();
//    ewe.setName("ewe");
//
//    d = new Dependency();
//    d.setName("voodoo");
//    d.setType("custom");
//    ewe.addDependency(d);

    AlgorithmSet algorithms = new AlgorithmSet();
    algorithms.setName("dummy-set");
    

//    algorithms.addAlgorithm(algorithm);
//    algorithms.addAlgorithm(ewe);

    Algorithm ensemble = new Algorithm();
    ensemble.setName("ensemble");
    ensemble.setCategory("ICHTHYOP_MODEL");
    ensemble.setAlgorithmType("transducerers");
    ensemble.setPackageURL("http://data.d4science.org/R0FqV2lNOW1jMkxuUEIrWXY4aUhvSENHSmVMQks4NjdHbWJQNStIS0N6Yz0");
    ensemble.setClazz("org.gcube.dataanalysis.executor.rscripts.Ichthyopmodelonebyone");
    ensemble.setSkipJava("N");
    ensemble.setDescription("test");
    
    Dependency d = new Dependency();
    d.setName("libpng3");
    d.setType("os");
    ensemble.addDependency(d);
    algorithms.addAlgorithm(ensemble);
    
    new DataminerPoolManager().addAlgorithmsToVRE(algorithms, "/gcube/devNext/NextNext", "test"+UUID.randomUUID(), false);

  }

}
