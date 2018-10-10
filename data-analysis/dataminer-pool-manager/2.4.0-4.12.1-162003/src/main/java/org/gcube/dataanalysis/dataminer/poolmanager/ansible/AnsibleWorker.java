package org.gcube.dataanalysis.dataminer.poolmanager.ansible;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Inventory;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Playbook;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleSerializeHelper;
import org.tmatesoft.svn.core.SVNException;

/**
 * This class is responsible for the interface with ansible, retrieving log,
 * etc. etc. It's not supposed to access templates and static stuff files. It
 * does not know the service datamodel.
 * 
 * @author paolo
 * 
 */
public class AnsibleWorker {
  
  /**
   * The name of the inventory 
   */
  private static String INVENTORY_NAME = "inventory.yaml";
  
  /**
   * The directory containing roles
   */
  private static String ROLES_DIR = "roles";

  /**
   * The name of the playbook
   */
  private static String PLAYBOOK_NAME = "playbook.yaml";

  /**
   * The root of the worker. This corresponds to a standard ansible working dir.
   */
  private File workerRoot;

  public AnsibleWorker(File root) {
    this.workerRoot = root;
    this.ensureWorkStructure();
  }

//  public File getWorkdir() {
//    return this.workerRoot;
//  }

  public File getRolesDir() {
    return new File(this.workerRoot, ROLES_DIR);
  }

  public String getWorkerId() {
    return this.workerRoot.getName();
  }

  public void ensureWorkStructure() {
    // generate root
    this.workerRoot.mkdirs();
  }

  public void removeWorkStructure() {
    // remove the working dir
    this.workerRoot.delete();
  }

  public File getPlaybookFile() {
    return new File(this.workerRoot, PLAYBOOK_NAME);
  }

  public File getInventoryFile() {
    return new File(this.workerRoot, INVENTORY_NAME);
  }


  public void setInventory(Inventory inventory) throws IOException {
    // serialize the string to the 'inventory' file
    AnsibleSerializeHelper.serialize(inventory, this.getInventoryFile());
  }

  public void setPlaybook(Playbook playbook) throws IOException {
    // serialize the string to the 'playbook' file
    AnsibleSerializeHelper.serialize(playbook, this.getPlaybookFile());
  }

  public void addRole(Role r) throws IOException {
    // Serialize role in the workdir
    AnsibleSerializeHelper.serializeRole(r, this.getRolesDir());
  }


	public int execute(PrintStream ps)
			throws IOException, InterruptedException, SVNException {

		System.out.println(this.workerRoot);
		try {
			Process p = Runtime.getRuntime().exec("ansible-playbook -v -i " + this.getInventoryFile().getAbsolutePath()
					+ " " + this.getPlaybookFile().getAbsolutePath());

			inheritIO(p.getInputStream(), ps);
			inheritIO(p.getErrorStream(), ps);

//			writer.println(this.getStatus(p.waitFor()));
//			writer.close();

			return p.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

  private static void inheritIO(final InputStream src, final PrintStream dest) {
	    new Thread(new Runnable() {
	        public void run() {
	            Scanner sc = new Scanner(src);
	            while (sc.hasNextLine()) {
	                dest.println(sc.nextLine());
	            }
	            sc.close();
	        }
	    }).start();
	}
  
  /**
   * Destroy the worker: 
   * - remove the working dir
   */
  public void destroy() {
    this.removeWorkStructure();
  }
  
}
