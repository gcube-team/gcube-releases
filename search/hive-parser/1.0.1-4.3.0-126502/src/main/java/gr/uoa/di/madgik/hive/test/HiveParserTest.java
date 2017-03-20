package gr.uoa.di.madgik.hive.test;
import gr.uoa.di.madgik.hive.HiveQLPlanner;
import gr.uoa.di.madgik.hive.analyzer.OperatorAnalyzer;
import gr.uoa.di.madgik.hive.plan.DataSourceNode;
import gr.uoa.di.madgik.hive.plan.Functionality;
import gr.uoa.di.madgik.hive.plan.OperatorNode;
import gr.uoa.di.madgik.hive.plan.PlanNode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class HiveParserTest {
	/**
	 * The logger used by the class
	 */
	private static Logger log = LoggerFactory.getLogger(HiveParserTest.class.getName());

//	private static HiveQLPlanner planner;
	
//	public static String getLine() throws Exception {
//		String line = "";
//		line += "CREATE TABLE u_data (\n" +
//				"	\tuserid INT,\n" +
//				"	\tmovieid INT,\n" +
//				"	\trating INT,\n" +
//				"	\tunixtime STRING)\n" +
//				"ROW FORMAT DELIMITED\n" +
//				"FIELDS TERMINATED BY '\\t'\n" +
//				"STORED AS TEXTFILE;\n\n";
//
////		line += "LOAD DATA LOCAL INPATH '/home/jgerbe/h-h/ml-100k/u.data'\n" +
////				"OVERWRITE INTO TABLE u_data;\n\n";		
//
////		line += "LOAD DATA LOCAL INPATH 'ftp://giannis:aplagiaftp@meteora.di.uoa.gr/testArea/src'\n" +
////				"OVERWRITE INTO TABLE u_data;\n\n";		
////
//		line += "LOAD DATA LOCAL INPATH 'jdbc:postgresql://meteora.di.uoa.gr:5432/dellstore2?user=postgres&password=aplagiadb/SELECT * FROM spdtrees'\n" +
//				"OVERWRITE INTO TABLE u_data;\n\n";
////		
//		line += "add FILE /home/jgerbe/Desktop/script.py;\n\n";
//
////		line += "CREATE TABLE u_data_new (\n" +
////				"	\tuserid2 INT,\n" +
////				"	\trating2 INT,\n" +
////				"	\tmovieid2 INT,\n" +
////				"	\tweekday2 INT)\n" +
////				"ROW FORMAT DELIMITED\n" +
////				"FIELDS TERMINATED BY '\\t';\n\n";
//		
//		line += "CREATE TABLE u_data_new (\n" +
//				"	\tuserid2 INT,\n" +
//				"	\tmovieid2 INT,\n" +
//				"	\trating2 INT,\n" +
//				"	\tweekday2 INT)\n" +
//				"ROW FORMAT DELIMITED\n" +
//				"FIELDS TERMINATED BY '\\t';\n\n";
//		
////		/*Simple Select*/
////		line += "FROM u_data\n" +
////		"INSERT OVERWRITE TABLE u_data_new\n" +
////		"SELECT u_data.*;\n\n";
//		
////		"SELECT u_data.*\n" +
////		"GROUPBY rating;\n\n";
//		
////		"SELECT userid, movieid, rating, unixtime;\n\n";
//
//		/*Simple Select*/
////		line += "FROM u_data\n" +
////				"INSERT OVERWRITE TABLE u_data_new\n" +
////				"SELECT userid, movieid, rating, unixtime\n" +
//////				"SELECT userid, rating, movieid, unixtime\n" +
////				"WHERE u_data.rating > '0' AND movieid == 145;\n\n";
//		
//		/*Simple Select*/
////		line += "FROM u_data\n" +
////		"INSERT OVERWRITE TABLE u_data_new\n" +
////		"SELECT userid, movieid, rating, unixtime\n" +
//////		"SELECT userid, rating, movieid, unixtime\n" +
////		"WHERE u_data.rating > 0;\n\n";
//
//		/*Simple Transform*/
////		line += "FROM u_data\n" +
////		"INSERT OVERWRITE TABLE u_data_new\n" +
////		"SELECT\n" +
////		"	\tTRANSFORM(u_data.userid, movieid, unixtime)\n" +
////		"	\tUSING 'python weekday_mapper.py'\n" +
////		"	\tAS userid, movieid, rating, weekday\n" +
////		"WHERE u_data.rating > 0;\n\n";
//
//		/*Clustering*/
////		line += "FROM u_data\n" +
////		"INSERT OVERWRITE TABLE u_data_new\n" +
////		"SELECT userid, movieid, rating, unixtime\n" +
////		"DISTRIBUTE BY rating, userid;\n\n";
//		
//		line += "FROM u_data\n" +
//		"INSERT OVERWRITE TABLE u_data_new\n" +
//		"SELECT\n" +
//		"	\tTRANSFORM(userid, rating, movieid, unixtime)\n" +
//		"	\tUSING 'python script.py'\n" +
//		"	\tAS uid, rat, mid, wd\n" +
//		"	\tWHERE unixtime > 0\n" +
//		"	\tDISTRIBUTE BY mid;\n\n";
//		
//		line += "INSERT OVERWRITE LOCAL DIRECTORY '/haha/u_data_new'\n" +
//				"SELECT movieid2\n" +
//				"FROM u_data_new\n" +
//				"WHERE rating2 > 0;\n\n";
//
//		line += "DROP TABLE u_data;\n\n";
//		line += "DROP TABLE u_data_new;\n\n";
//		
//		return line;
//	}	

	public static String getSimpleLine() {
		String line = "";
		line += "CREATE TABLE wiki (\n" +
				"	\tid STRING,\n" +
				"	\tproperty STRING,\n" +
				"	\ttype STRING,\n" +
				"	\tcontent STRING,\n" +
				"	\tlang STRING)\n" +
				"ROW FORMAT DELIMITED\n" +
				"FIELDS TERMINATED BY '\\t'\n" +
				"STORED AS TEXTFILE;\n\n";
	
		line += "LOAD DATA LOCAL INPATH '/home/jgerbe/Desktop/in.txt'\n" +
				"OVERWRITE INTO TABLE wiki;\n\n";
	
	//	line += "LOAD DATA LOCAL INPATH 'ftp://giannis:aplagiaftp@meteora.di.uoa.gr/testArea/src'\n" +
	//			"OVERWRITE INTO TABLE u_data;\n\n";		
	//
//		line += "LOAD DATA LOCAL INPATH 'jdbc:postgresql://localhost:5432/mydb?user=postgres&password=aplagiadb/SELECT * FROM wikimulti LIMIT 100'\n" +
//				"OVERWRITE INTO TABLE wiki;\n\n";

//		line += "LOAD DATA LOCAL INPATH 'tm://bioline/?scope=/gcube/devNext'\n" +
//				"OVERWRITE INTO TABLE data;\n\n";
	//	
//		line += "add FILE /home/jgerbe/Desktop/test.jar;\n\n";
//		line += "add FILE /home/jgerbe/Desktop/weekday_mapper.py;\n\n";
	
//		line += "CREATE TABLE wiki_new (\n" +
//				"	\tproperty STRING,\n" +
//				"	\ttype STRING,\n" +
//				"	\tcontent STRING,\n" +
//				"	\tlang STRING)\n" +
//				"ROW FORMAT DELIMITED\n" +
//				"FIELDS TERMINATED BY '\\t'\n" +
//				"STORED AS TEXTFILE;\n\n";
		
//		line += "CREATE TABLE u_data_new (\n" +
//				"	\tuserid2 INT,\n" +
//				"	\tmovieid2 INT,\n" +
//				"	\trating2 INT,\n" +
//				"	\tweekday2 INT)\n" +
//				"ROW FORMAT DELIMITED\n" +
//				"FIELDS TERMINATED BY '\\t';\n\n";
		
//		/*Simple Select*/
//		line += "FROM wiki\n" +
//		"INSERT OVERWRITE TABLE wiki_new\n" +
//		"SELECT *;\n\n ";
	//		"WHERE rating < 13;\n\n";
		
	//	"SELECT u_data.*\n" +
	//	"GROUPBY rating;\n\n";
		
	//	"SELECT userid, movieid, rating, unixtime;\n\n";
	
		/*Simple Select*/
	//	line += "FROM u_data\n" +
	//			"INSERT OVERWRITE TABLE u_data_new\n" +
	//			"SELECT userid, movieid, rating, unixtime\n" +
	////			"SELECT userid, rating, movieid, unixtime\n" +
	//			"WHERE u_data.rating > '0' AND movieid == 145;\n\n";
		
		/*Simple Select*/
	//	line += "FROM u_data\n" +
	//	"INSERT OVERWRITE TABLE u_data_new\n" +
	//	"SELECT userid, movieid, rating, unixtime\n" +
	////	"SELECT userid, rating, movieid, unixtime\n" +
	//	"WHERE u_data.rating > 0;\n\n";
	
		/*Simple Transform*/
//		line += "FROM u_data\n" +
//		"INSERT OVERWRITE TABLE u_data_new\n" +
//		"SELECT\n" +
//		"	\tTRANSFORM(userid, movieid, rating, unixtime)\n" +
//		"	\tUSING 'cat'\n" +
////		"	\tUSING 'python weekday_mapper.py'\n" +
////		"	\tUSING 'java -jar test.jar'\n" +
//		"	\tAS uid, mid, rat, wd\n" +
//		"DISTRIBUTE BY 3;\n\n";
//		"WHERE u_data.rating > 0;\n\n";
	
		/*Clustering*/
	//	line += "FROM u_data\n" +
	//	"INSERT OVERWRITE TABLE u_data_new\n" +
	//	"SELECT userid, movieid, rating, unixtime\n" +
	//	"DISTRIBUTE BY rating, userid;\n\n";
		
//		line += "FROM u_data\n" +
//		"INSERT OVERWRITE TABLE u_data_new\n" +
//		"SELECT\n" +
//		"	\tTRANSFORM(userid, movieid, rating, unixtime)\n" +
//		"	\tUSING 'python script.py'\n" +
//		"	\tAS uid, mid, rat, wd\n" +
//		"	\tWHERE rating > 2\n" +
//		"	\tDISTRIBUTE BY rat;\n\n";
		
//		line += "INSERT OVERWRITE LOCAL DIRECTORY 'jdbc:postgresql://localhost:5432/mydb?user=postgres&password=aplagiadb/INSERT INTO wikimulti_new VALUES (?, ?, ?)'\n" +
		line += "INSERT OVERWRITE LOCAL DIRECTORY '/home/jgerbe/Desktop/tmp/'\n" +
				"SELECT id, property, content\n" +
				"FROM wiki;\n\n";
//				"WHERE rating2 > 0;\n\n";
	
		line += "DROP TABLE wiki;\n\n";
//		line += "DROP TABLE u_data_new;\n\n";
		
		return line;
}
	
	public static String getLine(String dist, String file, String script) {
		String line = "";
		line += "CREATE TABLE wiki (\n" +
				"	\tproperty STRING,\n" +
				"	\ttype STRING,\n" +
				"	\tvalue STRING,\n" +
				"	\tlang STRING)\n" +
				"ROW FORMAT DELIMITED\n" +
				"FIELDS TERMINATED BY '\\t'\n" +
				"STORED AS TEXTFILE;\n\n";
	
		line += "LOAD DATA LOCAL INPATH '" + file + "'\n" +
				"OVERWRITE INTO TABLE wiki;\n\n";		
	
		line += "add FILE " + script + ";\n\n";
	
		line += "CREATE TABLE wiki_new (\n" +
//				"	\tproperty STRING,\n" +
				"	\tlang STRING,"
				+ 	"\tfreq STRING)\n" +
				"ROW FORMAT DELIMITED\n" +
				"FIELDS TERMINATED BY '\\t';\n\n";
		
		/*Simple Transform*/
		line += "FROM wiki\n" +
		"INSERT OVERWRITE TABLE wiki_new\n" +
		"SELECT\n" +
		"	\tTRANSFORM(property, value, lang)\n" +
		"	\tUSING 'python counter.py'\n" +
		"	\tAS lang, content";
		if (!dist.equals("1"))
			line += "\nDISTRIBUTE BY " + dist + ";\n\n";
		else
			line += ";\n\n";
	
		line += "INSERT OVERWRITE LOCAL DIRECTORY '/home/jgerbe/Desktop/tmp/'\n" +
				"SELECT *\n" +
				"FROM wiki_new;\n\n";
	
		line += "DROP TABLE wiki;\n\n";
		line += "DROP TABLE wiki_new;\n\n";
		
		return line;
}
	
//	public static void main(String[] args) throws Exception {
//		String line = getLine();
//		
//		long start = System.currentTimeMillis();
//		
//		new HiveQLPlanner().processLine("CREATE TABLE IF NOT EXISTS warm_up (id INT);");
//		
//		long warmup = System.currentTimeMillis() - start;
//		long actual = 0;
//
//		try {
//			run(new String[] {line});
//			actual = System.currentTimeMillis() - start - warmup;
//			System.out.println("Terminated normally");
//		}catch (Exception e) {
//			log.warn("Got Exception", e);
//		}finally {
//			System.err.println("Warmup time: " + warmup);
//			System.err.println("Actual time: " + actual);
//			System.err.println("Total time: " + (System.currentTimeMillis() - start));
//		}
//	}

	public static void run(String[] args) throws Exception {
		HiveQLPlanner planner = new HiveQLPlanner();
		planner .processLine(args[0]);
		
//		System.out.println(planner.getConstructedPlan());
		System.out.println(OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()));
		
//		PlanNode a = OperatorAnalyzer.optimizePlan(planner.getConstructedPlan());
//		
//		PlanNode b = createPlan();
//		
//		System.out.println(a);
//		System.out.println(b);
//		System.out.println(a.equals(b));
	}

	public static PlanNode createPlan() throws UnsupportedEncodingException {
//		String queryString = new String(
//				 "jdbc:postgresql:/localhost:5432/test%3Fuser=postgres&password=aplagiadb/INSERT INTO WikiEN VALUES (?, ?, ?)"
//		);
		String selectString = "jdbc:postgresql://localhost:5432/mydb%3Fuser=postgres&password=aplagiadb/SELECT%20myproperty,mycontent,mylang%20FROM%20wikimulti";
		String insertString = new String(
				 "<q>" +
				 "<query>" +
				 "insert into wikimulti_new values (?, ?, ?)" +
				 "</query>" +
				 "<driverName>" +
				 "org.postgresql.Driver" +
				 "</driverName>" +
				 "<connectionString>" +
				 "jdbc:postgresql://localhost:5432/mydb?" +
				 "user=postgres&amp;password=aplagiadb" +
				 "</connectionString>" +
				 "</q>"
		);
//		queryString = "file:/home/jgerbe/testArea/source/u_data10.csv";
		
		PlanNode node;
		HashMap<String, String> args;
		ArrayList<PlanNode> children;
		
		args = new HashMap<String, String>();
		args.put("schema", "[myproperty, mycontentq, mylang]");
		args.put("delimiter", "\t");
		args.put("filterMask", "[0, 1, 2]");args.put("source", selectString);
//		args.put("filterMask", "[0, 2, 3]");args.put("source", "file:/home/jgerbe/Desktop/datasets/wiki/multi/input.dat");

		node = new DataSourceNode("wiki", args );
		
//		args = new HashMap<String, String>();
//		args.put("filterMask", "[0, 1, 2]");
//		args.put("schema", "[myproperty, mycontent, mylang]");
//		children = new ArrayList<PlanNode>();
//		children.add(node);
//		node = new OperatorNode(Functionality.SELECT, args, children);

		args = new HashMap<String, String>();
		args.put("schema", "[myproperty, mycontent, mylang]");
		args.put("clusterBy", "2");
		children = new ArrayList<PlanNode>();
		children.add(node);
		node = new OperatorNode(Functionality.PARTITION, args, children);
//		
		args = new HashMap<String, String>();
		args.put("schema", "[id, output]");
		args.put("scriptCmd", "python script.py");
		args.put("CDATA:script.py", Base64.encode(new HiveQLPlanner().comressedContentOfFile(new File("/home/jgerbe/Desktop/datasets/wiki/multi/counter.py"))));
		children = new ArrayList<PlanNode>();
		children.add(node);
		node = new OperatorNode(Functionality.SCRIPT, args, children);
		
		args = new HashMap<String, String>();
		args.put("schema", "[id, output]");
		children = new ArrayList<PlanNode>();
		children.add(node);
		node = new OperatorNode(Functionality.MERGE, args, children);

		args = new HashMap<String, String>();
		args.put("filterMask", "[0, 1]");
		args.put("schema", "[id, output]");
		children = new ArrayList<PlanNode>();
		children.add(node);
		node = new OperatorNode(Functionality.SELECT, args, children);

		args = new HashMap<String, String>();
//		args.put("schema", "[movieid2]");
		args.put("tableName", "wikimulti_new");
		args.put("sink", insertString);///Users/jgerbe/testArea/sink
//		args.put("sink", "file:/home/jgerbe/Desktop/tmp");///Users/jgerbe/testArea/sink
		children = new ArrayList<PlanNode>();
		children.add(node);
		node = new OperatorNode(Functionality.DATASINK, args, children);
		
		return node;
	}
	
}
