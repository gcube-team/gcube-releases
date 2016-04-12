package gr.uoa.di.madgik.hive;

import gr.uoa.di.madgik.hive.analyzer.OperatorAnalyzer;
import gr.uoa.di.madgik.hive.plan.DataSourceNode;
import gr.uoa.di.madgik.hive.plan.Functionality;
import gr.uoa.di.madgik.hive.plan.OperatorNode;
import gr.uoa.di.madgik.hive.plan.PlanNode;
import gr.uoa.di.madgik.hive.representation.TableDesc;
import gr.uoa.di.madgik.hive.representation.TableFieldsAssociation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * This class after parsing a HiveQL query creates an abstract plan.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class HiveQLPlanner {
	private Logger log = LoggerFactory.getLogger(HiveQLPlanner.class.getName());

	private HiveQLParser parser = new HiveQLParser();
	private PlanNode plan;

	private static Map<String, TableDesc> tablesMap = new HashMap<String, TableDesc>();
	private Map<String, String> addedFiles = new HashMap<String, String>();
	private TableFieldsAssociation fieldsToTableMap = new TableFieldsAssociation();
	private String clusterBy;
	
	private static final String DEFDELIMITER = "\\" + Character.toString((char) 1);

	/**
	 * Process each HiveQL command separately. Line is split to multiple
	 * commands before processing.
	 * 
	 * @param line
	 *            Multiple queries line
	 * @throws Exception
	 *             if parsing fails
	 */
	public void processLine(String line) throws Exception {
		PrintStream errorStream = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			private String str = "";
			@Override
			public void write(int b) throws IOException {
				if((char) b == '\n') {
					if (str.contains("FAILED"))
						log.error(str);
					else if (str.contains("OK"))
						log.debug(str);
					str = "";
				}
				else
					str += (char) b;
			}
		}));
		
		String command = "";
		for (String oneCmd : line.split(";")) {

			if (StringUtils.endsWith(oneCmd, "\\")) {
				command += StringUtils.chop(oneCmd) + ";";
				continue;
			} else {
				command += oneCmd;
			}
			if (StringUtils.isBlank(command)) {
				continue;
			}
			log.debug("command: {\n" + command.trim() + "\n}");
			processCmd(command);
			command = "";
		}
		System.setErr(errorStream);
	}

	/**
	 * Process a single HiveQL command
	 * 
	 * @param command
	 * @throws Exception
	 */
	private void processCmd(String command) throws Exception {
		parser.parse(command);
		int ret = parser.compile();
		
		if (ret != 0)
			throw new Exception("parsing compile failed with code: " + ret);
		
		CommonTree tree;
		switch (parser.getQueryType()) {
		case CREATE:
			parser.execute();

			tree = parser.getASTree();

			// Create table
			String name = getNodes(tree, "TOK_CREATETABLE", "TOK_TABNAME").get(0);
			String delimiter = removeQuotes(getNodes(tree, "TOK_CREATETABLE", "TOK_TABLEROWFORMAT", "TOK_SERDEPROPS", "TOK_TABLEROWFORMATFIELD").isEmpty() ? DEFDELIMITER
					: StringEscapeUtils.unescapeJava(getNodes(tree, "TOK_CREATETABLE", "TOK_TABLEROWFORMAT", "TOK_SERDEPROPS", "TOK_TABLEROWFORMATFIELD").get(0)));

//			delimiter = Characct XXX here
			TableDesc table = new TableDesc(name, delimiter);

			// Add columns
			List<String> colList = getNodes(tree, "TOK_CREATETABLE", "TOK_TABCOLLIST", "TOK_TABCOL");
			for (int i = 0; i < colList.size();) {
				table.addColumn(colList.get(i++), colList.get(i++));
			}

			// Keep table instance of future reference
			tablesMap.put(table.getName(), table);

			// Track which fields are referenced in which tables
			for (String col : table.getColumns().keySet()) {
				fieldsToTableMap.asscociate(col, table.getName());
			}

			log.debug("Created table: " + table);

			break;
		case DROP:
			parser.execute();

			tree = parser.getASTree();

			// Remove table
			name = getNodes(tree, "TOK_DROPTABLE", "TOK_TABNAME").get(0);
			tablesMap.remove(name);

			// Remove table reference for removed table's fields
			fieldsToTableMap.removeTableAssociations(name);

			break;
		case LOAD:
			tree = parser.getASTree();

			String source = removeQuotes(getNodes(tree, "TOK_LOAD").get(0));

			// Get uri
			source = getUri(source);

			name = getNodes(tree, "TOK_LOAD", "TOK_TAB", "TOK_TABNAME").get(0);
			table = tablesMap.get(name);

			if (table == null) {
				log.error("table does not exist");
				throw new Exception("tables does not exist");
			} else if (table.getSource() != null)
				log.warn("table already has data. Going to ovewrite");

			// Update table info
			table.setSource(source);
			tablesMap.put(name, table);

			log.debug("Load data: " + name + "[" + source + "]");

			break;
		case QUERY:
			tree = parser.getASTree();

			String sink = null;
			// If it is a simple query else it is an output query
			if (!getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_TAB").isEmpty()) {
				
				plan = parser.constructPlan();
				
				if (!getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_LOCAL_DIR").isEmpty()) {
					sink = getUri(removeQuotes(getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_LOCAL_DIR").get(0)));
					
					plan.getFunctionalArgs().put("sink", sink);
					plan = makeFinalReplacements();
				}

				if (!getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DISTRIBUTEBY", "TOK_TABLE_OR_COL").isEmpty()) {
					List<String> clusterCols = getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DISTRIBUTEBY", "TOK_TABLE_OR_COL");
					List<String> expList = getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_SELECT", "TOK_SELEXPR", "TOK_TRANSFORM", "TOK_EXPLIST", "TOK_TABLE_OR_COL");
					
					String clusterBy = "[";
					for (String col : clusterCols){
						clusterBy += expList.indexOf(col) != -1? expList.indexOf(col) + ", ": "";
					}
					clusterBy = clusterBy.substring(0, clusterBy.length()-2) + "]";
					
					if (clusterBy.length() > 2)
						this.clusterBy = clusterBy;
				}
			} else if (!getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_LOCAL_DIR").isEmpty()) {
				sink = getUri(removeQuotes(getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_LOCAL_DIR").get(0)));

				if (plan != null)
					plan = OperatorAnalyzer.concatPlans(plan, parser.constructPlan());
				else
					plan = parser.constructPlan();

				plan.getFunctionalArgs().put("sink", sink);
				
				plan = makeFinalReplacements();
			}

			if (!(plan instanceof OperatorNode))
				throw new Exception("constructed plan has no operator node as root");

			break;
		case ADD:
			// Keep added files into a map
			String fname = command.trim().split("\\s+")[2].split("/")[command.trim().split("/").length - 1];
			String file = command.trim().split("\\s+")[2];
			addedFiles.put(fname, file);

			log.debug("Add File: " + fname + "[" + file + "]");

			break;
		default:
			throw new Exception("Unexpected command: " + command);
		}
	}

	private PlanNode makeFinalReplacements() throws Exception {
		// add source location for scripts
		LinkedList<OperatorNode> operList = new LinkedList<OperatorNode>();
		if (plan instanceof OperatorNode)
			operList.add((OperatorNode) plan);

		while (!operList.isEmpty()) {
			OperatorNode op = operList.removeFirst();
			
			for (PlanNode ch : op.getChildren()) {
				if (ch instanceof OperatorNode)
					operList.add((OperatorNode) ch);
				else if (ch instanceof DataSourceNode) // Set input source for DataSource nodes
					ch.getFunctionalArgs().put("source", tablesMap.get(((DataSourceNode)ch).getSource()).getSource());
			}
			
			// Set input file for script nodes
			if (op.getFunctionality() == Functionality.SCRIPT) {
				String scriptCmd = op.getFunctionalArgs().get("scriptCmd");
				String script = scriptCmd.split(" ")[scriptCmd.split(" ").length - 1];

				if (scriptCmd.split(" ").length > 1) {
					if (!addedFiles.containsKey(script))
						throw new Exception("script has not been loaded. script: " + script);

					String scriptLoc = addedFiles.get(script);

					File fileScript = null;
					boolean local = false;
					try { // try to download
						URL url = new URL(scriptLoc);
						fileScript = File.createTempFile("script", ".tmp");
						FileUtils.copyURLToFile(url, fileScript);
					} catch (MalformedURLException e) {
						local = true;
						fileScript = new File(scriptLoc);
					} finally {
						op.getFunctionalArgs().put("CDATA:" + script, Base64.encode(comressedContentOfFile(fileScript)));
						if (!local)
							fileScript.delete();
					}
				}
			}
			
			if (op.getFunctionality() == Functionality.PARTITION) {
				if (clusterBy != null)
						op.getFunctionalArgs().put("clusterBy", clusterBy);
			}

		}

		return plan;
	}
	
	public PlanNode getCreatedPlan() {
		return plan;
	}

	public static TableDesc getTablesSource(String table) {
		return tablesMap.get(table);
	}

	private String getAddedFiles(String file) {
		return addedFiles.get(file);
	}

	protected static String removeQuotes(String str) {
		if ((str.startsWith("'") && str.endsWith("'")) || (str.startsWith("\"") && str.endsWith("\"")))
			str = str.substring(1, str.length() - 1);
		return str;
	}

	private String getUri(String source) throws UnsupportedEncodingException {
		// Get uri
		if (source.startsWith("ftp://")) {
		} else if (source.startsWith("jdbc:")) {
			source = source.substring(0, source.lastIndexOf("/") + 1) + URLEncoder.encode(source.substring(source.lastIndexOf("/") + 1), "UTF-8");
		} else if (source.startsWith("tm:")) {
		} else {
			source = new File(source).toURI().toASCIIString();
		}

		return source;
	}

	public static List<String> getNodes(CommonTree tree, String... path) {
		List<String> list = new ArrayList<String>();

		if (path.length == 0) {
			list.add(tree.getText());
			return list;
		}

		if (tree.getText().matches(path[0])) {
			String restPath[] = new String[path.length - 1];
			System.arraycopy(path, 1, restPath, 0, restPath.length);
			if (tree.getChildCount() > 0)
				for (Object n : tree.getChildren()) {
					CommonTree child = (CommonTree) n;
					list.addAll(getNodes(child, restPath));
				}
		}

		return list;
	}

	private CommonTree getSubtree(CommonTree tree, String name) throws Exception {
		for (Object child : tree.getChildren()) {
			if (((CommonTree) child).getText().equals(name)) {
				return (CommonTree) child;
			}
		}
		throw new Exception("Unkown subtree: " + name + " for tree: " + tree.toStringTree());
	}

	private String getColumn(CommonTree child) {
		if (child.getText().equals(".")) {
			String fileTable = getNodes(child, "\\.", "TOK_TABLE_OR_COL").get(0);
			String fileName = getNodes(child, "\\.").get(1);
			return fileTable + "." + fileName;
		} else if (child.getText().equals("TOK_TABLE_OR_COL")) {
			return getNodes(child, "TOK_TABLE_OR_COL").get(0);
		} else if (child.getText().equals("TOK_ALLCOLREF")) {
			return getNodes(child, "TOK_ALLCOLREF", "TOK_TABNAME").get(0) + "." + "*";
		} else {
			return child.getText();
		}
	}
	
	public byte[] comressedContentOfFile(File file) {
		byte[] buffer = new byte[1024];
		byte[] compressed = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BufferedOutputStream bfos = new BufferedOutputStream(bos);
			GZIPOutputStream gz = new GZIPOutputStream(bfos);

			FileInputStream in = new FileInputStream(file);

			int len;
			while ((len = in.read(buffer)) > 0) {
				gz.write(buffer, 0, len);
			}

			gz.finish();
			gz.flush();
			bos.flush();

			compressed = bos.toByteArray();

			in.close();
			bos.close();
			bfos.close();
			gz.close();
		} catch (IOException e) {
			log.error("compression failed", e);
		}

		return compressed;
	}
	
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		HiveQLPlanner test = new HiveQLPlanner();
//
//		byte[] b = test.comressedContentOfFile(new File("/home/jgerbe/Desktop/script.py"));
//		
//		String s = Base64.encodeBase64String(b);
//		
//		System.out.println(s = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(b));
//		
//		System.out.println(ScriptOp.decompressToFile(com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(s)));
//	}
}
