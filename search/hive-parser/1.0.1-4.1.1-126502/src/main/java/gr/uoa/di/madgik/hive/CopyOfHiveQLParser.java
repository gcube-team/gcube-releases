//package gr.uoa.di.madgik.hive;
//
//import gr.uoa.di.madgik.hive.parse.responses.CreateResponse;
//import gr.uoa.di.madgik.hive.parse.responses.LoadResponse;
//import gr.uoa.di.madgik.hive.parse.responses.ParseResponse;
//import gr.uoa.di.madgik.hive.query.QueryType;
//import gr.uoa.di.madgik.hive.utils.MaskString;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.antlr.runtime.tree.CommonTree;
//import org.apache.hadoop.hive.cli.CliSessionState;
//import org.apache.hadoop.hive.conf.HiveConf;
//import org.apache.hadoop.hive.ql.CommandNeedRetryException;
//import org.apache.hadoop.hive.ql.Context;
//import org.apache.hadoop.hive.ql.Driver;
//import org.apache.hadoop.hive.ql.exec.MapRedTask;
//import org.apache.hadoop.hive.ql.lib.Node;
//import org.apache.hadoop.hive.ql.parse.ASTNode;
//import org.apache.hadoop.hive.ql.parse.ParseDriver;
//import org.apache.hadoop.hive.ql.parse.ParseException;
//import org.apache.hadoop.hive.ql.parse.ParseUtils;
//import org.apache.hadoop.hive.ql.parse.VariableSubstitution;
//import org.apache.hadoop.hive.ql.plan.DDLWork;
//import org.apache.hadoop.hive.ql.session.SessionState;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * A parser of Hive Query Language.
// * 
// * @author john.gerbesiotis - DI NKUA
// * 
// */
//public class HiveQLParser {
//	private Logger log = LoggerFactory.getLogger(HiveQLParser.class.getName());
//	/**
//	 * Hive configuration
//	 */
//	private Driver driver;
//	private HiveConf conf;
//	private String command;
//	private ASTNode tree;
//	private Map<String, String> addedFiles = new HashMap<String, String>();
//	private Map<String, String> tablesToUri = new HashMap<String, String>();
//
//	public HiveQLParser() {
//		CliSessionState ss = new CliSessionState(new HiveConf(SessionState.class));
//
//		conf = ss.getConf();
//		
//		// Setting in-memory derby db
//		conf.set("javax.jdo.option.ConnectionURL", "jdbc:derby:memory:metastore_db;create=true");
//		
//		// Setting metastore location in /tmp
//		conf.set("hive.metastore.warehouse.dir", "/tmp/hive/warehouse");
//		
//		SessionState.start(ss);
//		driver = new Driver(conf);
//	}
//
//	/**
//	 * Parses the HiveQL query command
//	 * 
//	 * @param command
//	 *            The query to be parsed
//	 * @return return code of parsing
//	 */
//	public int parse(String command) {
//		int ret;
//		this.command = command;
//
//		ret = compile();
//		System.out.println(tree.dump());
//		switch (getQueryType()) {
//		case CREATE:
//			execute();
//			break;
//		case DROP:
//			execute();
//			tablesToUri.remove(((CreateResponse) getWork()).getAffectedTable());
//			break;
//		case LOAD:
//			LoadResponse resp = ((LoadResponse) getWork());
//			tablesToUri.put(resp.getToPath(), resp.getFromPath());
//			break;
//		case QUERY:
//			test();
//			getWork();
//			break;
//		case ADD:
//			addedFiles.put(command.trim().split("\\s+")[2].split("/")[command.trim().split("/").length - 1], command.trim().split("\\s+")[2]);
//			break;
//		default:
//			break;
//		}
//
//		return ret;
//	}
//
//	/**
//	 * Get last parsed query type
//	 * 
//	 * @return The {@link QueryType}
//	 */
//	public QueryType getQueryType() {
//		QueryType queryType = null;
//
//		if (command.trim().toLowerCase().startsWith("add")) {
//			queryType = QueryType.ADD;
//		} else {
//			switch (tree.getToken().getText()) {
//			case "TOK_CREATETABLE":
//				queryType = QueryType.CREATE;
//				break;
//			case "TOK_LOAD":
//				queryType = QueryType.LOAD;
//				break;
//			case "TOK_QUERY":
//				queryType = QueryType.QUERY;
//				break;
//			case "TOK_DROPTABLE":
//				queryType = QueryType.DROP;
//				break;
//			default:
//				break;
//			}
//		}
//
//		log.debug("Query type: " + queryType);
//
//		return queryType;
//	}
//
//	public ParseResponse getWork() {
//		ParseResponse response = null;
//
//		if (command.startsWith("add")) {
//			// XXX implement it
//		} else {
//			switch (tree.getToken().getText()) {
//			case "TOK_CREATETABLE":
//				CreateResponse createResp = new CreateResponse();
//				createResp.setAffectedTable(tree.getChild(0).getChild(0).getText());
//
//				response = createResp;
//				break;
//			case "TOK_LOAD":
//				LoadResponse loadresp = new LoadResponse();
//				String fromPath = removeQuotes(tree.getChild(0).getText());
//
//				if (fromPath.startsWith("ftp://")) {
//				} else if (fromPath.startsWith("jdbc:")) {
//				} else {
//					fromPath = new File(fromPath).toURI().toASCIIString();
//				}
//
//				loadresp.setFromPath(fromPath);
//				loadresp.setToPath(tree.getChild(1).getChild(0).getChild(0).getText());
//
//				response = loadresp;
//				break;
//			case "TOK_QUERY":
//				System.out.println("query");
//				break;
//			case "TOK_DROPTABLE":
//				CreateResponse dropResp = new CreateResponse();
//				dropResp.setAffectedTable(tree.getChild(0).getChild(0).getText());
//
//				response = dropResp;
//				break;
//			default:
//				break;
//			}
//		}
//
//		return response;
//	}
//
//	public int compile() {
//		if (command.trim().toLowerCase().startsWith("add")) {
//			if (command.trim().matches("add\\s*FILE\\s*\\S*"))
//				return 0;
//			else
//				return 1;
//		}
//		parseAST();
//
//		if (command.trim().toLowerCase().startsWith("load")) {
//			MaskString mask = new MaskString(command, removeQuotes(tree.getChild(0).getText()));
//			return driver.compile(mask.hide());
//		}
//		return driver.compile(command);
//	}
//
//	private void parseAST() {
//		command = new VariableSubstitution().substitute(conf, command);
//		Context ctx;
//		tree = null;
//		try {
//			ctx = new Context(conf);
//			ctx.setTryCount(Integer.MAX_VALUE);
//			ctx.setCmd(command);
//			ctx.setHDFSCleanup(true);
//
//			ParseDriver pd = new ParseDriver();
//			tree = pd.parse(command, ctx);
//			tree = ParseUtils.findRootNonNullToken(tree);
//		} catch (ParseException | IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private int execute() {
//		int ret = 0;
//		try {
//			ret = driver.execute();
//		} catch (CommandNeedRetryException e) {
//			log.warn("Query execution failed: " + command, e);
//		}
//		return ret;
//	}
//
//	/**
//	 * @return the command
//	 */
//	public String getCommand() {
//		return command;
//	}
//
//	public String getTablesSource(String table) {
//		return tablesToUri.get(table);
//	}
//
//	public String getAddedFiles(String file) {
//		return addedFiles.get(file);
//	}
//
//	private static String removeQuotes(String str) {
//		if ((str.startsWith("'") && str.endsWith("'")) || (str.startsWith("\"") && str.endsWith("\"")))
//			str = str.substring(1, str.length() - 1);
//		return str;
//	}
//
//	private ASTNode getChild(String... path) {
//		String token = null;
//
//		ASTNode node = tree;
//		ASTNode child = null;
//		for (String step : path) {
//			for (Node n : node.getChildren()) {
//				child = (ASTNode) n;
//				if (child.getText().equalsIgnoreCase(step)) {
//					node = child;
//					break;
//				}
//			}
//			if (node != child)
//				return null;
//		}
//
//		return child;
//	}
//
//	private String getToken(String... strs) {
//		String token = null;
//		ASTNode node = getChild(strs);
//		if (node == null)
//			return null;
//
//		token = node.toStringTree();
//
//		if (node.getChildCount() == 0)
//			return null;
//
//		token = token.substring(2 + node.getText().length(), token.length() - 1);
//
//		return token;
//	}
//
//	private String getToken(int ind, String... strs) {
//		String token = null;
//		ASTNode node = getChild(strs);
//		if (node == null || ind >= node.getChildCount())
//			return null;
//
//		token = node.getChild(ind).toStringTree();
//
//		if (token.startsWith("(") && token.endsWith(""))
//			token = token.substring(1, token.length() - 1);
//
//		return token;
//	}
//
//	private void test() {
//		System.out.println("plan\n" + driver.getPlan().toString());
//		System.out.println("queryPlan\n" + driver.getPlan().getInputs());
////		System.out.println(((MapRedTask)driver.getPlan().getRootTasks().get(0)).getWork().toXML());
//		{
////			MapRedTask work = ((MapRedTask)driver.getPlan().getRootTasks().get(0).getWork());
////			System.out.println(work.toString());
////			System.out.println(((DDLWork)driver.getPlan().getRootTasks().get(0)).getWork().toXML());
////			for (Operator<? extends OperatorDesc> op : work.getAliasToWork().get("u_data").getChildOperators()) {
////				for(Entry<String, ExprNodeDesc> col: op.getColumnExprMap().entrySet()) {
////					System.out.println("col: " + col.getKey() + " val: " + col.getValue().getExprString());
////
////				}
////			}
//		}
//		System.exit(0);
//	}
//
//	public CommonTree getASTree() {
//		
//		return tree;
//	}
//}
