package gr.uoa.di.madgik.hive;

import gr.uoa.di.madgik.hive.analyzer.OperatorAnalyzer;
import gr.uoa.di.madgik.hive.plan.PlanNode;
import gr.uoa.di.madgik.hive.query.QueryType;
import gr.uoa.di.madgik.hive.utils.MaskString;

import java.io.IOException;

import org.antlr.runtime.tree.CommonTree;
import org.apache.hadoop.hive.cli.CliSessionState;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Context;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.mr.MapRedTask;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.ParseUtils;
import org.apache.hadoop.hive.ql.parse.VariableSubstitution;
import org.apache.hadoop.hive.ql.plan.MapredWork;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parser of Hive Query Language.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class HiveQLParser {
	private Logger log = LoggerFactory.getLogger(HiveQLParser.class.getName());

	/**
	 * Hive configuration
	 */
	private Driver driver;
	private HiveConf conf;
	private String command;
	private ASTNode tree;

	public HiveQLParser() {
		CliSessionState ss = new CliSessionState(new HiveConf(SessionState.class));

		conf = ss.getConf();

		// Setting in-memory derby db
		conf.set("javax.jdo.option.ConnectionURL", "jdbc:derby:memory:metastore_db;create=true");

//		System.out.println(conf.get("javax.jdo.PersistenceManagerFactoryClass"));
//		conf.set("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");

//		System.out.println(conf.get("datanucleus.identifierFactory"));
//		conf.set("datanucleus.identifierFactory", "datanucleus1");
		
		// Setting metastore location in /tmp
		conf.set("hive.metastore.warehouse.dir", "/tmp/hive/warehouse");

		SessionState.start(ss);
		driver = new Driver(conf);
	}

	/**
	 * Parses the HiveQL query command
	 * 
	 * @param command
	 *            The query to be parsed
	 * @throws Exception
	 *             If query parsing failed
	 */
	public void parse(String command) throws Exception {
		this.command = command;

		if (!command.trim().toLowerCase().startsWith("add"))
			tree = parseAST(command);
	}

	/**
	 * Get last parsed query type
	 * 
	 * @return The {@link QueryType}
	 */
	public QueryType getQueryType() {
		QueryType queryType = null;

		if (command.trim().toLowerCase().startsWith("add")) {
			queryType = QueryType.ADD;
		} else {
			String tok = tree.getText();
			if (tok.equals("TOK_CREATETABLE")) {
				queryType = QueryType.CREATE;
			}else if (tok.equals("TOK_LOAD")) {
				queryType = QueryType.LOAD;
			}else if (tok.equals("TOK_QUERY")) {
				queryType = QueryType.QUERY;
			}else if (tok.equals("TOK_DROPTABLE")) {
				queryType = QueryType.DROP;
			}else{
				log.warn("unkown query type");
			}
		}

		return queryType;
	}

	private ASTNode parseAST(String command) throws Exception {
		command = new VariableSubstitution().substitute(conf, command);
		Context ctx;
		tree = null;
		try {
			ctx = new Context(conf);
			ctx.setTryCount(Integer.MAX_VALUE);
			ctx.setCmd(command);
			ctx.setHDFSCleanup(true);

			ParseDriver pd = new ParseDriver();
			tree = pd.parse(command, ctx);
			tree = ParseUtils.findRootNonNullToken(tree);
		} catch (Exception e) {
			log.error("parse error: ", e);
			throw e;
		}

		return tree;
	}

	public int compile() {
		if (command.trim().toLowerCase().startsWith("add")) {
			if (command.trim().matches("add\\s*FILE\\s*\\S*"))
				return 0;
			else
				return 1;
		}

		if (command.trim().toLowerCase().startsWith("load")) {
			MaskString mask = new MaskString(command, HiveQLPlanner.removeQuotes(tree.getChild(0).getText()));
			return driver.compile(mask.hide());
		}
		
		if (command.trim().toLowerCase().startsWith("insert")) {
			MaskString mask = new MaskString(command, HiveQLPlanner.getNodes(tree, "TOK_QUERY", "TOK_INSERT", "TOK_DESTINATION", "TOK_LOCAL_DIR").get(0));
			return driver.compile(mask.hideWithQuotes());
		}
		return driver.compile(command);
	}

	public int execute() {
		int ret = 0;
		try {
			ret = driver.execute();
		} catch (CommandNeedRetryException e) {
			log.warn("Query execution failed: " + command, e);
		}
		return ret;
	}

	public PlanNode constructPlan() throws Exception {
		PlanNode plan = null;
		
		MapRedTask task = ((MapRedTask) driver.getPlan().getRootTasks().get(0));
		MapredWork work = task.getWork();

//		System.out.println(work.toXML());

		
		for (Operator<?> entry : work.getMapWork().getWorks()) {
			OperatorAnalyzer.analyzeOperator(entry);
		}

		if (work.getReduceWork() != null)
			OperatorAnalyzer.analyzeOperator(work.getReduceWork().getReducer());
		
		plan = OperatorAnalyzer.getPlan();
		
		return plan;
	}

	/**
	 * Get the Abstract Syntax Tree
	 * 
	 * @return The parsed query's AST
	 */
	public CommonTree getASTree() {
		return tree;
	}
}
