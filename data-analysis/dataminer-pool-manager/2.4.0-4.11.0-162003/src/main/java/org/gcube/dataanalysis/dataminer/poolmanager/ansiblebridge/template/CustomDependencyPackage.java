package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.slf4j.Logger;

public class CustomDependencyPackage extends DependencyPackage {

	private Logger logger;
	
  public CustomDependencyPackage(Dependency dependency) 
  {
    super(dependency);
  }

//  private String getCustomRepositoryLocation(String ansibleRoot) {
//    return ansibleRoot+"/custom";
//  }

  /*
  public void serializeTo(String ansibleRoot) {
    for(String mode:new String[]{"add", "remove", "update"}) {
      // look for roles in the 'custom' repository
      try {
        // role name
        String roleName = this.getDependency().getType()+"-"+this.getDependency().getName()+("add".equals(mode) ? "" : "-"+mode);
        // look for the custom role
        File src = new File(this.getCustomRepositoryLocation(ansibleRoot)+"/"+roleName);
        System.out.println("** CUSTOM ** " + src);
        if(src.exists()) {
          // do copy
          System.out.println("copying CUSTOM role");
          File dest = new File(ansibleRoot+"/work/"+roleName);
          FileUtils.copyDirectory(src, dest);
        }
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
  }
  */
  
  public Collection<Role> getRoles(CustomRoleManager crm) {
    Collection<Role> out = new Vector<>();
//    for(String mode:new String[]{"add", "remove", "update"}) {
    for(String mode:new String[]{"add"}) {  // "remove", "update"
      // role name
      String roleName = this.getDependency().getType()+"-"+this.getDependency().getName()+("add".equals(mode) ? "" : "-"+mode);
      try {
        // look for custom role
        Role role = crm.getRole(roleName);
        if(role!=null) {
          out.add(role);
        }
      } catch (NoSuchElementException e) {
//        e.printStackTrace();
        this.logger.warn("WARNING: no custom role found for " + roleName);
      }
    }
    return out;
  }

  

}
