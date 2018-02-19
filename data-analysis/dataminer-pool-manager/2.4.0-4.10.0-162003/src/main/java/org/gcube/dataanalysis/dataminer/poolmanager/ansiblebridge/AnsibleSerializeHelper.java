package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.AnsibleHost;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.HostGroup;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Inventory;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Playbook;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.RoleFile;

public class AnsibleSerializeHelper {
  
  public static void serialize(Inventory inventory, File inventoryFile) throws IOException {
    String out = "";
    for(HostGroup hg:inventory.getHostGroups()) {
      out+=String.format("[%s]\n", hg.getName());
      for(AnsibleHost h:hg.getHosts()) {
        out+=h.getName()+"\n";
      }
      out+="\n";
    }
    out = out.trim();
    serialize(out, inventoryFile);
  }
  
  public static void serialize(Playbook playbook, File playbookFile) throws IOException {
    String out = "- hosts: " + playbook.getHostGroupName() +  "\n";
    out += "  remote_user: "+playbook.getRemote_user()+"\n";
    out+="  roles:\n";
    for(String r:playbook.getRoles()) {
      out+="    - " + r+"\n";
    }
    out+="  vars:\n";
    out+="    os_package_state: present\n";
    out = out.trim();
    serialize(out, playbookFile);
  }

  public static void serializeRole(Role r, File dir) throws IOException {
    // create root
    File root = new File(dir, r.getName());
    root.mkdirs();
    
    // create tasks
    if(r.getTaskFiles().size()>0) {
      File tasks = new File(root, "tasks");
      tasks.mkdirs();
      for(RoleFile tf: r.getTaskFiles()) {
        serializeTask(tf, tasks);
      }
    }
    
    // create meta
    if(r.getMeta().size()>0) {
      File meta = new File(root, "meta");
      meta.mkdirs();
      for(RoleFile tf: r.getMeta()) {
        serializeTask(tf, meta);
      }
    }
  }

  public static void serializeTask(RoleFile tf, File dir) throws IOException {
    File f = new File(dir, tf.getName());
    serialize(tf.getContent().trim(), f);
  }

  public static void serialize(String s, File f) throws IOException {
    PrintWriter out = new PrintWriter(f);
    out.println(s);
    out.close();
  }
  
  public static Role deserializeRoleFromFilesystem(File roleDir) throws IOException {
    Role out = new Role();
    out.setName(roleDir.getName());
    
    if(!roleDir.exists()) {
      throw new FileNotFoundException();
    }
    
    try {
      File tasksDir = new File(roleDir, "tasks");
      if(tasksDir.exists()) {
        for(File main:tasksDir.listFiles()) {
          String content = IOUtils.toString(new FileInputStream(main), "UTF-8");
          RoleFile tf = new RoleFile(main.getName(), content);
          tf.setPath(main.getAbsolutePath().substring(roleDir.getAbsolutePath().length()+1));
          out.addTaskFile(tf);
        }
      }
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    }
    
    try {
      File metaDir = new File(roleDir, "meta");
      if(metaDir.exists()) {
        for(File main:metaDir.listFiles()) {
          String content = IOUtils.toString(new FileInputStream(main), "UTF-8");
          RoleFile tf = new RoleFile(main.getName(), content);
          tf.setPath(main.getAbsolutePath().substring(roleDir.getAbsolutePath().length()+1));
          out.addMeta(tf);
        }
      }
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    }
    
    return out;
  }
  
}
