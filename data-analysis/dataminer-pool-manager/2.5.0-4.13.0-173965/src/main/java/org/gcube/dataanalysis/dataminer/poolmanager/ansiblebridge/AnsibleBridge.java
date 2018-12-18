package org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;

import org.gcube.dataanalysis.dataminer.poolmanager.ansible.AnsibleWorker;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.AnsibleHost;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Inventory;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Playbook;
import org.gcube.dataanalysis.dataminer.poolmanager.ansible.model.Role;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.AlgorithmPackage;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.CranDependencyPackage;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.CustomDependencyPackage;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.CustomRoleManager;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.OSDependencyPackage;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.StaticRoleManager;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.template.TemplateManager;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator.HostComparator;

public class AnsibleBridge {
	// private static final org.slf4j.Logger LOGGER =
	// LoggerFactory.getLogger(AnsibleBridge.class);

	private String dpmRoot;

	// public AnsibleBridge() {
	// this(System.getProperty("user.home")+File.separator+"dataminer-pool-manager");
	// //this(System.getProperty("/home/gcube/dataminer-pool-manager"));
	//
	// }

	public AnsibleBridge(String root) {
		this.dpmRoot = root;
		this.ensureServiceRoot();
	}

	private void ensureServiceRoot() {
		// generate root
		new File(dpmRoot).mkdirs();
		// 'template' is for template roles
		// this.getTemplatesDir().mkdirs();
		// 'static' is for custom roles
		// this.getCustomDir().mkdirs();
		// 'work' is for temporary working directories
		this.getWorkDir().mkdirs();
	}

	private File getWorkDir() {
		return new File(this.dpmRoot, "work");
	}

	// private String getTemplatesDir() {
	// String input = null;
	// input =
	// AnsibleBridge.class.getClassLoader().getResource("templates").getPath();
	// return input;
	// }
	//
	//
	// private String getCustomDir() {
	// String input = null;
	// input =
	// AnsibleBridge.class.getClassLoader().getResource("custom").getPath();
	// return input;
	// }

	public AnsibleWorker createWorker(Algorithm algorithm, Cluster dataminerCluster,
			boolean includeAlgorithmDependencies, String user) throws IOException {
		File workerRoot = new File(this.getWorkDir(), UUID.randomUUID().toString());
		AnsibleWorker worker = new AnsibleWorker(workerRoot);

		List<Role> algoRoles = new Vector<>();

		// add algorithms and dependencies to the worker
		for (Role r : this.generateRoles(algorithm, includeAlgorithmDependencies)) {
			algoRoles.add(r);
			worker.addRole(r);
		}

		// to comment the for in case of just install algo
		if (includeAlgorithmDependencies) {
			for (Dependency d : algorithm.getDependencies()) {
				for (Role r : this.generateRoles(d)) {
					worker.addRole(r);
				}
			}
		}

		// add static roles
		for (Role r : this.getStaticRoleManager().getStaticRoles()) {
			worker.addRole(r);
		}

		// generate the inventory
		Inventory inventory = new Inventory();
		for (Host h : dataminerCluster.getHosts()) {
			AnsibleHost ah = new AnsibleHost(h.getName());
			inventory.addHost(ah, "universe");
			inventory.addHost(ah, "d4science");
		}
		worker.setInventory(inventory);

		// generate the playbook
		Playbook playbook = new Playbook();
		playbook.setRemote_user(user);
		playbook.applyTo("universe");
		for (Role r : algoRoles) {
			// add only 'add' roles
			if (!r.getName().endsWith("remove")) {
				playbook.addRole(r.getName());
			}
		}

		worker.setPlaybook(playbook);

		return worker;
	}

	public void printInventoryByDomainAndSets(Collection<Cluster> clusters) {
		Map<String, Set<Host>> inventory = new TreeMap<>();
		for (Cluster cluster : clusters) {
			for (AlgorithmSet as : cluster.getAlgorithmSets()) {
				String asName = as.getName();
				for (Host h : cluster.getHosts()) {
					String domain = h.getDomain().getName();
					String key = String.format("[%s@%s]", asName, domain);
					Set<Host> hosts = inventory.get(key);
					if (hosts == null) {
						hosts = new TreeSet<>(new HostComparator());
						inventory.put(key, hosts);
					}
					hosts.add(h);
				}

			}
		}
		for (String key : inventory.keySet()) {
			System.out.println(key);
			Collection<Host> hosts = inventory.get(key);
			for (Host h : hosts) {
				System.out.println(h.getName() + "." + h.getDomain().getName());
			}
			System.out.println();
		}
	}

	public void printInventoryBySets(Collection<Cluster> clusters) {
		Map<String, Set<Host>> inventory = new TreeMap<>();
		for (Cluster cluster : clusters) {
			for (AlgorithmSet as : cluster.getAlgorithmSets()) {
				String asName = as.getName();
				for (Host h : cluster.getHosts()) {
					String key = String.format("[%s]", asName);
					Set<Host> hosts = inventory.get(key);
					if (hosts == null) {
						hosts = new TreeSet<>(new HostComparator());
						inventory.put(key, hosts);
					}
					hosts.add(h);
				}

			}
		}
		for (String key : inventory.keySet()) {
			System.out.println(key);
			Collection<Host> hosts = inventory.get(key);
			for (Host h : hosts) {
				System.out.println(h.getName() + "." + h.getDomain().getName());
			}
			System.out.println();
		}
	}

	// public AnsibleWorker applyAlgorithmSetToCluster(AlgorithmSet as, Cluster
	// cluster, /*boolean updateSVN,*/ boolean test) throws IOException,
	// InterruptedException, SVNException {
	//
	//
	// return applyAlgorithmSetToCluster
	// (as,cluster,UUID.randomUUID().toString(),/*updateSVN,*/ test);
	// }

	// public AnsibleWorker applyAlgorithmSetToCluster(AlgorithmSet as, Cluster
	// cluster,String uuid, /*boolean updateSVN,*/ boolean test) throws
	// IOException, InterruptedException, SVNException {
	// AnsibleWorker worker = new AnsibleWorker(new File(this.getWorkDir(),
	// uuid));
	//
	//
	// List<Role> algoRoles = new Vector<>();
	//
	// // add algorithms and dependencies to the worker
	// for (Algorithm a : as.getAlgorithms()) {
	// for (Role r : this.generateRoles(a)) {
	// algoRoles.add(r);
	// worker.addRole(r);
	// }
	// //to comment the for in case of just install algo
	// if(test){
	// for (Dependency d : a.getDependencies()) {
	// for (Role r : this.generateRoles(d)) {
	// worker.addRole(r);
	// }
	// }
	// }
	// }
	//
	// // add static roles
	// for(Role r:this.getStaticRoleManager().getStaticRoles()) {
	// worker.addRole(r);
	// }
	//
	// // generate the inventory
	// Inventory inventory = new Inventory();
	// for (Host h : cluster.getHosts()) {
	// AnsibleHost ah = new AnsibleHost(h.getName());
	// inventory.addHost(ah, "universe");
	// inventory.addHost(ah, "d4science");
	// }
	// worker.setInventory(inventory);
	//
	// // generate the playbook
	// Playbook playbook = new Playbook();
	// if(test){
	// playbook.setRemote_user("root");}
	// playbook.setRemote_user("gcube");
	// playbook.applyTo("universe");
	// for(Role r:algoRoles) {
	// // add only 'add' roles
	// if(!r.getName().endsWith("remove")) {
	// playbook.addRole(r.getName());
	// }
	// }
	//
	// worker.setPlaybook(playbook);
	//
	// // execute and save log locally
	// //PrintStream console = System.out;
	// File path = new File(worker.getWorkdir() + File.separator + "logs");
	// path.mkdirs();
	// File n = new File(path + File.separator + worker.getWorkerId());
	// FileOutputStream fos = new FileOutputStream(n);
	// PrintStream ps = new PrintStream(fos);
	//
	// //System.setErr(console);
	//
	// worker.apply(as,ps,test);
	// //System.setOut(console);
	// //worker.apply();
	// System.out.println("Log stored to to " + n.getAbsolutePath());
	//
	// // destroy the worker
	// worker.destroy();
	// return worker;
	// }

	private TemplateManager getTemplateManager() {
		return new TemplateManager();
	}

	private CustomRoleManager getCustomRoleManager() {
		return new CustomRoleManager();
	}

	private StaticRoleManager getStaticRoleManager() {
		return new StaticRoleManager();
	}

	/**
	 * Generate all roles for this dependency
	 * 
	 * @param dep
	 *            Dependency
	 * @return Collection of Roles
	 */
	public Collection<Role> generateRoles(Dependency dep) {
		Collection<Role> roles = new Vector<>();

		if ("os".equalsIgnoreCase(dep.getType())) {
			OSDependencyPackage pkg = new OSDependencyPackage(dep);
			if (pkg != null) {
				roles.addAll(pkg.getRoles(this.getTemplateManager()));
			}

		} else if ("custom".equalsIgnoreCase(dep.getType())) {
			CustomDependencyPackage pkg = new CustomDependencyPackage(dep);
			if (pkg != null) {
				roles.addAll(pkg.getRoles(this.getCustomRoleManager()));
			}
		} else if ("github".equalsIgnoreCase(dep.getType())) {
			CranDependencyPackage pkg = new CranDependencyPackage(dep);
			if (pkg != null) {
				roles.addAll(pkg.getRoles(this.getTemplateManager()));
			}
		} else if ("cran".equalsIgnoreCase(dep.getType())) {
			CranDependencyPackage pkg = new CranDependencyPackage(dep);
			if (pkg != null) {
				roles.addAll(pkg.getRoles(this.getTemplateManager()));
			}

		}
		return roles;
	}

	public Collection<Role> generateRoles(Algorithm a, boolean includeAlgorithmDependencies) {
		AlgorithmPackage pkg = new AlgorithmPackage(a, includeAlgorithmDependencies);
		return pkg.getRoles(this.getTemplateManager());

	}

}
