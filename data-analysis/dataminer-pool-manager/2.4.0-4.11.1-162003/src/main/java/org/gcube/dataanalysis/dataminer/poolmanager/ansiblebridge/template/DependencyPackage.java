package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyPackage {

	private Logger logger;
	
  private Dependency dependency;

  public DependencyPackage(Dependency dependency) {
    this.dependency = dependency;
    this.logger = LoggerFactory.getLogger(DependencyPackage.class);
  }

  protected Map<String, String> getDictionary(Dependency d) {
    Map<String, String> out = new HashMap<String, String>();
    out.put("name", d.getName());
    out.put("type", d.getType());
    return out;
  }
  
  protected Dependency getDependency() {
    return this.dependency;
  }
  
  public Collection<Role> getRoles(TemplateManager tm) {
    Collection<Role> out = new Vector<>();
    for(String mode:new String[]{"add"}) {  // "remove", "update"
      String roleName = this.getDependency().getType()+"-"+this.getDependency().getName().replaceAll("/",  "-")+("add".equals(mode) ? "" : "-"+mode);
      try {
        // find template
        Role template = tm.getRoleTemplate(this.getDependency().getType()+"-package-"+mode);
        // 
        if(template!=null) {
          Map<String, String> dictionary = this.getDictionary(this.getDependency());
          Role r = tm.fillRoleTemplate(template, dictionary);
          r.setName(roleName);
          out.add(r);
        } else {
          this.logger.warn("WARNING: template is null");
        }
      } catch (NoSuchElementException e) {
//        e.printStackTrace();
    	  this.logger.warn("WARNING: no template found for " + roleName);
      }
    }
    return out;
  }

}
