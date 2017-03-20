package gr.uoa.di.madgik.hive.test;

import gr.uoa.di.madgik.hive.HiveQLPlanner;
import gr.uoa.di.madgik.hive.analyzer.OperatorAnalyzer;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Submitions {
		@BeforeClass
		public static void initializeEnv() throws Exception {
			warmup();
		}
		
		@AfterClass
		public static void cleanup() {
		}
		
		private static String createPreCommand() {
			String line = new String();
			
			line += "CREATE TABLE u_data (\n" +
					"	\tuserid INT,\n" +
					"	\tmovieid INT,\n" +
					"	\trating INT,\n" +
					"	\tunixtime STRING)\n" +
					"ROW FORMAT DELIMITED\n" +
					"FIELDS TERMINATED BY '\\t'\n" +
					"STORED AS TEXTFILE;\n\n";
			
			line += "LOAD DATA LOCAL INPATH '/home/jgerbe/h-h/ml-100k/u.data'\n" +
					"OVERWRITE INTO TABLE u_data;\n\n";
			
			line += "add FILE /home/jgerbe/h-h/ml-100k/weekday_mapper.py;\n\n";
			
			line += "CREATE TABLE u_data_new (\n" +
					"	\tuserid2 INT,\n" +
					"	\tmovieid2 INT,\n" +
					"	\trating2 INT,\n" +
					"	\tweekday2 INT)\n" +
					"ROW FORMAT DELIMITED\n" +
					"FIELDS TERMINATED BY '\\t';\n\n";
			
			return line;
		}
		
		private static String createPostCommand() {
			String line = new String();
			
			line += "INSERT OVERWRITE LOCAL DIRECTORY '/haha/u_data_new'\n" +
					"SELECT movieid2\n" +
					"FROM u_data_new\n" +
					"WHERE rating2 > 0;\n\n";
			
			line += "DROP TABLE u_data;\n\n";
			line += "DROP TABLE u_data_new;\n\n";
			
			return line;
		}
		
		public static void warmup() throws Exception {
			new HiveQLPlanner().processLine("CREATE TABLE IF NOT EXISTS warm_up (id INT);");
		}
		
		@Test
		public void simpleSelect1() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT u_data.*;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();
			
			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <DataSource>\n          <Source>u_data</Source>\n          <Indications>schema-[movieid, rating], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[1, 2]</Indications>\n        </DataSource>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			
			Assert.assertEquals(plan, output);
		}
		
		@Test
		public void simpleSelect2() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT userid, movieid, rating, unixtime\n" +
					"WHERE u_data.rating > 0;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();
			
			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <Operation>\n          <Functionality>SELECT</Functionality>\n          <Indications>logicalExpressions-([2] &gt; '0'), schema-[movieid, rating], filterMask-[1, 2]</Indications>\n          <Children>\n            <DataSource>\n              <Source>u_data</Source>\n              <Indications>schema-[userid, movieid, rating, unixtime], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[0, 1, 2, 3]</Indications>\n            </DataSource>\n          </Children>\n        </Operation>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			Assert.assertEquals(plan, output);
		}
		
		@Test
		public void simpleSelect3() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT userid, movieid, rating, unixtime\n" +
					"WHERE u_data.rating > '0' AND movieid == 145;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();
			
			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <Operation>\n          <Functionality>SELECT</Functionality>\n          <Indications>logicalExpressions-(([2] &gt; '0') and ([1] = '145')), schema-[movieid, rating], filterMask-[1, 2]</Indications>\n          <Children>\n            <DataSource>\n              <Source>u_data</Source>\n              <Indications>schema-[userid, movieid, rating, unixtime], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[0, 1, 2, 3]</Indications>\n            </DataSource>\n          </Children>\n        </Operation>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			Assert.assertEquals(plan, output);
		}
		
		@Test
		public void transform() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT\n" +
					"	\tTRANSFORM(u_data.userid, movieid, unixtime)\n" +
					"	\tUSING 'python weekday_mapper.py'\n" +
					"	\tAS userid, movieid, rating, weekday\n" +
					"WHERE u_data.rating > 0;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();

			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <Operation>\n          <Functionality>SELECT</Functionality>\n          <Indications>schema-[movieid2, rating2], filterMask-[1, 2]</Indications>\n          <Children>\n            <Operation>\n              <Functionality>SCRIPT</Functionality>\n              <Indications>schema-[_col0, _col1, _col2, _col3], source-/home/jgerbe/h-h/ml-100k/weekday_mapper.py, scriptCmd-python weekday_mapper.py</Indications>\n              <Children>\n                <Operation>\n                  <Functionality>SELECT</Functionality>\n                  <Indications>logicalExpressions-([2] &gt; '0'), schema-[userid, movieid, unixtime], filterMask-[0, 1, 3]</Indications>\n                  <Children>\n                    <DataSource>\n                      <Source>u_data</Source>\n                      <Indications>schema-[userid, movieid, rating, unixtime], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[0, 1, 2, 3]</Indications>\n                    </DataSource>\n                  </Children>\n                </Operation>\n              </Children>\n            </Operation>\n          </Children>\n        </Operation>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			Assert.assertEquals(plan, output);
		}
		
		@Test
		public void clustering() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT userid, movieid, rating, unixtime\n" +
					"DISTRIBUTE BY rating, userid;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();

			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <Operation>\n          <Functionality>SELECT</Functionality>\n          <Indications>schema-[movieid2, rating2], filterMask-[1, 2]</Indications>\n          <Children>\n            <Operation>\n              <Functionality>MERGE</Functionality>\n              <Indications>schema-[_col0, _col1, _col2, _col3]</Indications>\n              <Children>\n                <Operation>\n                  <Functionality>PARTITION</Functionality>\n                  <Indications>schema-[userid, movieid, rating, unixtime], clusterBy-[2, 0], order-</Indications>\n                  <Children>\n                    <DataSource>\n                      <Source>u_data</Source>\n                      <Indications>schema-[userid, movieid, rating, unixtime], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[0, 1, 2, 3]</Indications>\n                    </DataSource>\n                  </Children>\n                </Operation>\n              </Children>\n            </Operation>\n          </Children>\n        </Operation>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			Assert.assertEquals(plan, output);
		}

		@Test
		public void distTransform() throws Exception {
			String query = new String();
			query += "FROM u_data\n" +
					"INSERT OVERWRITE TABLE u_data_new\n" +
					"SELECT\n" +
					"	\tTRANSFORM(userid, movieid, rating, unixtime)\n" +
					"	\tUSING 'python weekday_mapper.py'\n" +
					"	\tAS uid, mid, rat, wd\n" +
					"	\tWHERE unixtime > 0\n" +
					"	\tDISTRIBUTE BY mid;\n\n";
			
			HiveQLPlanner planner = new HiveQLPlanner();
			planner .processLine(createPreCommand() + query + createPostCommand());
			
			String plan = OperatorAnalyzer.optimizePlan(planner.getCreatedPlan()).toString();
			System.out.println(plan);
			String output = "<Operation>\n  <Functionality>DATASINK</Functionality>\n  <Indications>schema-[movieid2], tableName-u_data_new, sink-file:/haha/u_data_new</Indications>\n  <Children>\n    <Operation>\n      <Functionality>SELECT</Functionality>\n      <Indications>logicalExpressions-([1] &gt; '0'), schema-[movieid2], filterMask-[0]</Indications>\n      <Children>\n        <Operation>\n          <Functionality>SELECT</Functionality>\n          <Indications>schema-[movieid2, rating2], filterMask-[1, 2]</Indications>\n          <Children>\n            <Operation>\n              <Functionality>MERGE</Functionality>\n              <Indications>schema-[_col0, _col1, _col2, _col3]</Indications>\n              <Children>\n                <Operation>\n                  <Functionality>SCRIPT</Functionality>\n                  <Indications>schema-[_col0, _col1, _col2, _col3], source-/home/jgerbe/h-h/ml-100k/weekday_mapper.py, scriptCmd-python weekday_mapper.py</Indications>\n                  <Children>\n                    <Operation>\n                      <Functionality>PARTITION</Functionality>\n                      <Indications>schema-[userid, movieid, rating, unixtime], clusterBy-[1], order-</Indications>\n                      <Children>\n                        <Operation>\n                          <Functionality>SELECT</Functionality>\n                          <Indications>logicalExpressions-([3] &gt; '0'), schema-[userid, movieid, rating, unixtime], filterMask-[0, 1, 2, 3]</Indications>\n                          <Children>\n                            <DataSource>\n                              <Source>u_data</Source>\n                              <Indications>schema-[userid, movieid, rating, unixtime], input-file:/home/jgerbe/h-h/ml-100k/u.data, filterMask-[0, 1, 2, 3]</Indications>\n                            </DataSource>\n                          </Children>\n                        </Operation>\n                      </Children>\n                    </Operation>\n                  </Children>\n                </Operation>\n              </Children>\n            </Operation>\n          </Children>\n        </Operation>\n      </Children>\n    </Operation>\n  </Children>\n</Operation>\n";
			Assert.assertEquals(plan, output);
		}
}
