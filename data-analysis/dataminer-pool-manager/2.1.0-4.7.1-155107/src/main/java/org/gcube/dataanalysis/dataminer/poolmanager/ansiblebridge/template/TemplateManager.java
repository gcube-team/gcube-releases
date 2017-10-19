package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.RoleFile;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleSerializeHelper;
import org.stringtemplate.v4.ST;

public class TemplateManager {

  public TemplateManager() {
    
  }

  public String getTemplateRoot() {
	    String input = AnsibleBridge.class.getClassLoader().getResource("templates").getPath();
    return input;
  }

 
//  private String readTemplate(String templateName) throws IOException {
//    File templateFile = new File(this.getTemplateRoot(), templateName + ".yaml");
//    System.out.println("looking for file " + templateFile.getName());
//    String out = IOUtils.toString(new FileInputStream(templateFile), "UTF-8");
//    return out;
//  }

 
//  public String getTemplate(String templateName) throws NoSuchElementException {
//    String template = null;
//    try {
//      template = this.readTemplate(templateName);
//    } catch (IOException e) {
//      throw new NoSuchElementException();
//    }
//    return template;
//  }
  
  public Role fillRoleTemplate(Role template, Map<String, String> dictionary) {
    Role out = new Role();
    out.setName(template.getName());
    for(RoleFile tf:template.getTaskFiles()) {
      out.addTaskFile(this.fillTaskTemplate(tf, dictionary));
    }
    for(RoleFile tf:template.getMeta()) {
      out.addMeta(this.fillTaskTemplate(tf, dictionary));
    }
    return out;
  }

  private RoleFile fillTaskTemplate(RoleFile template, Map<String, String> dictionary) {
    RoleFile out = new RoleFile();
    out.setName(template.getName());
    out.setContent(this.fillTemplate(template.getContent(), dictionary));
    return out;
  }

  private String fillTemplate(String template, Map<String, String> dictionary) {
    if (template != null) {
      ST t = new ST(template);
      for (String key : dictionary.keySet()) {
        t.add(key, dictionary.get(key));
      }
      String output = t.render();
      return output;
    }
    return template;
  }

  public Role getRoleTemplate(String roleName) throws NoSuchElementException {
    File f = new File(this.getTemplateRoot(), roleName);
    try {
      return AnsibleSerializeHelper.deserializeRoleFromFilesystem(f);
    } catch (IOException e) {
//      e.printStackTrace();
      throw new NoSuchElementException("unable to find " + roleName);
    }
  }

}
