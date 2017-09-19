package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

  import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleSerializeHelper;

  public class StaticRoleManager {


    public StaticRoleManager() {
      
    }

    public String getRoot() {
    String input = AnsibleBridge.class.getClassLoader().getResource("static").getPath();
    return input;
    }

    public Collection<Role> getStaticRoles() {
      Collection<Role> out = new Vector<>();
      for(File f: new File(this.getRoot()).listFiles()) {
    	  try {
          out.add(AnsibleSerializeHelper.deserializeRoleFromFilesystem(f));
        } catch(IOException e) {
          e.printStackTrace();
        }
      }
      return out;
    }
    


  }
