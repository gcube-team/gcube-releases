package org.gcube.dataanalysis.dataminer.poolmanager.ansible;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Inventory;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Playbook;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleSerializeHelper;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.internal.wc.admin.SVNChecksumInputStream;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

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

  public File getWorkdir() {
    return this.workerRoot;
  }

  public File getRolesDir() {
    return new File(this.getWorkdir(), ROLES_DIR);
  }

  public String getWorkerId() {
    return this.workerRoot.getName();
  }

  public void ensureWorkStructure() {
    // generate root
    this.getWorkdir().mkdirs();
  }

  public void removeWorkStructure() {
    // remove the working dir
    this.getWorkdir().delete();
  }

  public File getPlaybookFile() {
    return new File(this.getWorkdir(), PLAYBOOK_NAME);
  }

  public File getInventoryFile() {
    return new File(this.getWorkdir(), INVENTORY_NAME);
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

		System.out.println(this.getWorkdir());
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
