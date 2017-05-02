package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;

public class AlgorithmPackage {

  private Algorithm algorithm;

  public AlgorithmPackage(Algorithm a) {
    this.algorithm = a;
  }

  protected Map<String, String> getDictionary(Algorithm a) {
    Map<String, String> out = new HashMap<String, String>();
    out.put("name", a.getName());
    out.put("category", a.getCategory());
    out.put("class", a.getClazz());
    out.put("atype", a.getAlgorithmType());
    out.put("skipjava", a.getSkipJava());
    out.put("vre", ScopeProvider.instance.get());
    //out.put("vre", "FAKE_VRE");
    out.put("packageurl", a.getPackageURL());
    out.put("description", a.getDescription());
    String deps = "";
    
    for(Dependency d:a.getDependencies()) {
      deps+=String.format("- { role: %s }\n", d.getType()+"-"+d.getName().replaceAll("/", "-"));
    }
    deps = deps.trim();
    out.put("dependencies", deps);
    return out;
  }
  
  protected Algorithm getAlgorithm() {
    return this.algorithm;
  }
  
  public Collection<Role> getRoles(TemplateManager tm) {
    Collection<Role> out = new Vector<>();
    for(String mode:new String[]{"add"}) {  // "remove", "update"
      String roleName = "gcube-algorithm-"+this.getAlgorithm().getName()+("add".equals(mode) ? "" : "-"+mode);
      try {
        // find template
        Role template = tm.getRoleTemplate("gcube-algorithm-" + mode);
        // 
        if(template!=null) {
          Map<String, String> dictionary = this.getDictionary(this.getAlgorithm());
          Role r = tm.fillRoleTemplate(template, dictionary);
          r.setName(roleName);
          out.add(r);
        } else {
          System.out.println("WARNING: template is null");
        }
      } catch (NoSuchElementException e) {
//        e.printStackTrace();
        System.out.println("WARNING: no template found for " + roleName);
      }
    }
    return out;
  }

}
