package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleSerializeHelper;

public class CustomRoleManager {

  private String root;

  public CustomRoleManager() {

  }

  public String getRoot() {
	    String input = AnsibleBridge.class.getClassLoader().getResource("custom").getPath();

    return input;
  }

  public Role getRole(String roleName) throws NoSuchElementException {
    File f = new File(this.getRoot(), roleName);
    try {
      return AnsibleSerializeHelper.deserializeRoleFromFilesystem(f);
    } catch (IOException e) {
//      e.printStackTrace();
      throw new NoSuchElementException("unable to find " + roleName);
    }
  }

}
