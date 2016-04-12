package gr.uoa.di.madgik.workflow.adaptor.hive.nodeselection;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.cost.BestNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.cost.DistanceNodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.ru.MRUNodeSelector;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.element.execution.ExecutionServer;
import gr.uoa.di.madgik.rr.element.execution.RRExecutionServer2HnAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for picking best suited execution nodes, for any
 * case during transformation process.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class NodePicker {
	/** The logger. */
	private Logger log = LoggerFactory.getLogger(NodePicker.class.getName());

	/**
	 * The node selector which will be used to select which available nodes for
	 * operation.
	 */
	private NodeSelector nodeSelector;

	/** List of all Hosting nodes */
	private List<HostingNode> hns;

	private Map<String, Integer> mapPorts;

	private static Boolean localFound = null;
	private static final boolean ExcludeLocalDef = true;
	private boolean excludeLocal = ExcludeLocalDef;
	private boolean isComplex = true;

	public NodePicker(String requirements) throws Exception {
		this(new MRUNodeSelector(new DistanceNodeSelector(
				new BestNodeSelector())), requirements);
	}

	public NodePicker(NodeSelector nodeSelector, String requirements)
			throws Exception {
		this.nodeSelector = nodeSelector;

		RRExecutionServer2HnAdapter adapter = new RRExecutionServer2HnAdapter();
		List<ExecutionServer> esn = ExecutionServer.getMatchingNodes(true,
				requirements, new String());
		hns = adapter.adaptAll(esn);
		mapPorts = new HashMap<String, Integer>();
		for (ExecutionServer es : esn) {
			mapPorts.put(es.getHostingNode().getID(),
					Integer.parseInt(es.getPort()));
		}
		if (localFound == null) {
			localFound = false;
			for (HostingNode hn : hns) {
				if (hn.isLocal()) {
					localFound = true;
					break;
				}
			}
		}
	}

	public String selectMergerExecutionNode() {
		HostingNode hn = nodeSelector.selectNode(hns);
		if (hn != null)
			return new String(
					hn.getPropertyByName(HostingNode.HostnameProperty) + ":"
							+ mapPorts.get(hn.getId()));
		return null;
	}

	public String selectDataSourceExecutionNode() {
		HostingNode hn = nodeSelector.selectNode(hns);
		if (hn != null)
			return new String(
					hn.getPropertyByName(HostingNode.HostnameProperty) + ":"
							+ mapPorts.get(hn.getId()));
		return null;
	}

	public String selectOperatorExecutionNode() {
		HostingNode hn = nodeSelector.selectNode(hns);
		if (hn != null)
			return new String(
					hn.getPropertyByName(HostingNode.HostnameProperty) + ":"
							+ mapPorts.get(hn.getId()));
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		String scope = "/gcube/devNext";
		String providerInformationName = "gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";

		EnvHintCollection hints = new EnvHintCollection();
		hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));

		InformationSystem.Init(providerInformationName, hints);

		ResourceRegistry.startBridging();

		while (!ResourceRegistry.isInitialBridgingComplete()) {
			System.out.println("waiting...");
			Thread.sleep(5000);
		}

		HostingNode node;
		NodePicker picker = new NodePicker(""/* "dl12.execution == true" */);
		System.out.println("Node picking\n");
		RuleSet a;
		System.out.println(picker.selectMergerExecutionNode());
		System.out.println(picker.selectDataSourceExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.out.println(picker.selectOperatorExecutionNode());
		System.err.println();
		
	}
}
