

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.AnsibleWorker;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Inventory;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Playbook;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.RoleFile;

public class AnsibleWorkerTest {

  public static void main(String[] args) throws IOException {
    AnsibleWorker worker = new AnsibleWorker(new File("/home/nagalante/gcube/dataminer-pool-manager/work/"+UUID.randomUUID().toString()));
    
    System.out.println("created worker named " + worker.getWorkerId());
    
    worker.setInventory(new Inventory());
    worker.setPlaybook(new Playbook());
    
    Role r = new Role();
    r.setName("latex");
    
    RoleFile tf = new RoleFile("main", "do something special for " + r.getName());
    r.addTaskFile(tf);
    worker.addRole(r);
    
    //worker.apply();
  }
  
}
