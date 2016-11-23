package gr.uoa.di.madgik.hive.analyzer;

import gr.uoa.di.madgik.hive.HiveQLPlanner;
import gr.uoa.di.madgik.hive.plan.DataSourceNode;
import gr.uoa.di.madgik.hive.plan.Functionality;
import gr.uoa.di.madgik.hive.plan.OperatorNode;
import gr.uoa.di.madgik.hive.plan.PlanNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.ColumnInfo;
import org.apache.hadoop.hive.ql.exec.ExtractOperator;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator;
import org.apache.hadoop.hive.ql.exec.FilterOperator;
import org.apache.hadoop.hive.ql.exec.Operator;
import org.apache.hadoop.hive.ql.exec.ReduceSinkOperator;
import org.apache.hadoop.hive.ql.exec.RowSchema;
import org.apache.hadoop.hive.ql.exec.ScriptOperator;
import org.apache.hadoop.hive.ql.exec.SelectOperator;
import org.apache.hadoop.hive.ql.exec.TableScanOperator;
import org.apache.hadoop.hive.ql.plan.ExprNodeColumnDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeConstantDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.ExprNodeGenericFuncDesc;
import org.apache.hadoop.hive.ql.plan.OperatorDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyze Hive operators in order to create an abstract operator plan
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class OperatorAnalyzer {
	/**
	 * The logger used by the class
	 */
	private static Logger log = LoggerFactory.getLogger(OperatorAnalyzer.class.getName());

	private static PlanNode plan;
	
	private static String source;

	/**
	 * Analyzes the Hive operators plan
	 * 
	 * @param op
	 *            Hive plan
	 * @throws Exception
	 *             if parsing fails
	 */
	public static void analyzeOperator(Operator<? extends OperatorDesc> op) throws Exception {
		if (op instanceof TableScanOperator) {
			analyzeOperator((TableScanOperator) op);
		} else if (op instanceof FilterOperator) {
			analyzeOperator((FilterOperator) op);
		} else if (op instanceof SelectOperator) {
			analyzeOperator((SelectOperator) op);
		} else if (op instanceof FileSinkOperator) {
			analyzeOperator((FileSinkOperator) op);
		} else if (op instanceof ScriptOperator) {
			analyzeOperator((ScriptOperator) op);
		} else if (op instanceof ReduceSinkOperator) {
			analyzeOperator((ReduceSinkOperator) op);
		} else if (op instanceof ExtractOperator) {
			analyzeOperator((ExtractOperator) op);
		} else {
			log.warn("Unknown operator: " + op.getName());
			throw new Exception("Unknown operator: " + op.getName());
		}

		if (op.getChildOperators() == null)
			return;

		for (Operator<? extends OperatorDesc> ch : op.getChildOperators()) {
			analyzeOperator(ch);
		}
	}

	/**
	 * Analyze {@link TableScanOperator}
	 * 
	 * @param op
	 * @throws Exception
	 */
	private static void analyzeOperator(TableScanOperator op) throws Exception {
		source = op.getConf().getAlias();

		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		functionalArgs.put("filterMask", op.getNeededColumnIDs().toString());

		functionalArgs.put("delimiter", HiveQLPlanner.getTablesSource(source).getDelimiter());
		DataSourceNode node = new DataSourceNode(source, functionalArgs);

		List<String> colList = new ArrayList<String>();
		for (Integer i : op.getNeededColumnIDs()) {
			colList.add(op.getSchema().getSignature().get(i).getInternalName());
		}

		functionalArgs.put("schema", colList.toString());

		log.debug(op.getParentOperators() + "->" + op);
		log.debug("source: " + source);
		log.debug("args: " + functionalArgs);

		plan = node;
	}

	/**
	 * Analyze {@link FilterOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(FilterOperator op) {
		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		ArrayList<PlanNode> planList = new ArrayList<PlanNode>();
		planList.add(plan);
		OperatorNode node = new OperatorNode(Functionality.SELECT, functionalArgs, planList);

		try {
			functionalArgs.put("logicalExpressions", getExpression(op.getSchema(), op.getConf().getPredicate()));
		} catch (Exception e) {
			log.warn("Could not extract logical expression");
		}

		List<String> colList = new ArrayList<String>();
		for (ColumnInfo columnInfo : op.getSchema().getSignature()) {
			colList.add(columnInfo.getInternalName());
		}

		functionalArgs.put("schema", colList.toString());
		functionalArgs.put("filterMask", createFilterMask(plan.getFunctionalArgs().get("schema"), colList.toString()));

		log.debug(op.getParentOperators() + "->" + op);
		log.debug("expression: " + op.getConf().getPredicate().getExprString());
		log.debug("args: " + functionalArgs);

		plan = node;
	}

	/**
	 * Analyze {@link SelectOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(SelectOperator op) {
		List<String> currColList = new ArrayList<String>();
		for (ExprNodeDesc exprNodeDesc : op.getConf().getColList()) {
			currColList.add(exprNodeDesc.getCols().get(0));
		}

		String filterMask = createFilterMask(plan.getFunctionalArgs().get("schema"), currColList.toString());

		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		if (filterMask != null) {
			ArrayList<PlanNode> planList = new ArrayList<PlanNode>();
			planList.add(plan);
			OperatorNode node = new OperatorNode(Functionality.SELECT, functionalArgs, planList);

			functionalArgs.put("filterMask", filterMask);
			functionalArgs.put("schema", currColList.toString());
			plan = node;
		}

		log.debug(op.getParentOperators() + "->" + op);
		log.debug("args: " + functionalArgs);
	}

	/**
	 * Analyze {@link FileSinkOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(FileSinkOperator op) {
		String tableName = null;
		log.debug(op.getParentOperators() + "->" + op);
		if (op.getConf().getTableInfo().getTableName() != null) {
			tableName = op.getConf().getTableInfo().getTableName().replaceFirst("default\\.", "");
		} else // if no table name is provided use source
			tableName = source;

		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		ArrayList<PlanNode> planList = new ArrayList<PlanNode>();
		planList.add(plan);
		OperatorNode node = new OperatorNode(Functionality.DATASINK, functionalArgs, planList);

		functionalArgs.put("tableName", tableName);
		functionalArgs.put("delimiter", HiveQLPlanner.getTablesSource(tableName).getDelimiter());

		List<String> colList = new ArrayList<String>();
		for (ColumnInfo columnInfo : op.getSchema().getSignature()) {
			colList.add(columnInfo.getInternalName());
		}

		functionalArgs.put("schema", colList.toString());

		plan = node;

		log.debug("args: " + functionalArgs);
	}

	/**
	 * Analyze {@link ScriptOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(ScriptOperator op) {
		String scriptCmd = op.getConf().getScriptCmd();
		String schema = "[" + op.getConf().getScriptOutputInfo().getProperties().get("columns").toString().replace(",", ", ") + "]";

		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		ArrayList<PlanNode> planList = new ArrayList<PlanNode>();
		planList.add(plan);
		OperatorNode node = new OperatorNode(Functionality.SCRIPT, functionalArgs, planList);

		functionalArgs.put("scriptCmd", scriptCmd);
		functionalArgs.put("schema", schema);
		plan = node;

		log.debug(op.getParentOperators() + "->" + op);
		log.debug("args: " + functionalArgs);
	}

	/**
	 * Analyze {@link ReduceSinkOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(ReduceSinkOperator op) {
		int partNum = -1;
		String order = op.getConf().getKeySerializeInfo().getProperties().getProperty("serialization.sort.order");

		List<Integer> clustCol = new ArrayList<Integer>();
		for (ExprNodeDesc exprNodeDesc : op.getConf().getPartitionCols()) {
			for (int i = 0; i < op.getConf().getValueCols().size(); i++) {
				if (op.getConf().getValueCols().get(i).getExprString().equals(exprNodeDesc.getExprString()))
					clustCol.add(i);
			}
		}
		if (clustCol.isEmpty()) {
			if (op.getConf().getPartitionCols().get(0) instanceof ExprNodeConstantDesc)
				partNum = (Integer)((ExprNodeConstantDesc)op.getConf().getPartitionCols().get(0)).getValue();
		}

		PlanNode pointer = plan;
		while (true) {
			if (pointer instanceof DataSourceNode)
				break;
			else {
				if (((OperatorNode) pointer).getFunctionality() == Functionality.SELECT)
					break;
			}
			pointer = ((OperatorNode) pointer).getChildren().get(0);
		}

		HashMap<String, String> partFunctionalArgs = new HashMap<String, String>();
		if (!clustCol.isEmpty())
			partFunctionalArgs.put("clusterBy", clustCol.toString());
		else
			partFunctionalArgs.put("partitionBy", String.valueOf(partNum));
		partFunctionalArgs.put("schema", pointer.getFunctionalArgs().get("schema"));
		partFunctionalArgs.put("order", order);
		ArrayList<PlanNode> children = new ArrayList<PlanNode>();
		children.add(pointer);
		OperatorNode parent = pointer.getParent();
		pointer.setParent(null);
		OperatorNode partNode = new OperatorNode(Functionality.PARTITION, partFunctionalArgs, children);
		if (parent != null) {
			ArrayList<PlanNode> parentChildren = new ArrayList<PlanNode>();
			parentChildren.add(partNode);
			parent.setChildren(parentChildren);
		} else {
			pointer.setParent(partNode);
			plan = partNode;
		}
		
		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		ArrayList<PlanNode> planList = new ArrayList<PlanNode>();
		planList.add(plan);
		OperatorNode node = new OperatorNode(Functionality.MERGE, functionalArgs, planList);

		List<String> colList = new ArrayList<String>();
		for (ColumnInfo columnInfo : op.getSchema().getSignature()) {
			colList.add(columnInfo.getInternalName());
		}

		functionalArgs.put("schema", colList.toString());
//		functionalArgs.put("clustCol", clustCol.toString());
//		functionalArgs.put("order", order);

		log.debug(op.getParentOperators() + "->" + op);
		log.debug("args: " + functionalArgs);

		plan = node;
	}

	/**
	 * Analyze {@link ExtractOperator}
	 * 
	 * @param op
	 */
	private static void analyzeOperator(ExtractOperator op) {
		List<String> colList = new ArrayList<String>();
		for (ColumnInfo columnInfo : op.getSchema().getSignature()) {
			colList.add(columnInfo.getInternalName());
		}
		log.debug("colList: " + colList);
	}

	/**
	 * Merge two plans into one. main plan should be a typical query while exten
	 * query could also be an output query.
	 * 
	 * @param main
	 *            Main plan to be merged
	 * @param exten
	 *            Extension to main plan
	 * @return Concatenation of the plans
	 * @throws Exception
	 *             If plans can not be concatenated
	 */
	public static PlanNode concatPlans(PlanNode main, PlanNode exten) throws Exception {
		LinkedList<OperatorNode> operList = new LinkedList<OperatorNode>();

		//Remove exten Datasink
		exten = ((OperatorNode) exten).getChildren().get(0);
		
		PlanNode it = exten;
		while (!(it instanceof DataSourceNode)) {
			operList.add((OperatorNode) it);

			if (((OperatorNode) it).getChildren().size() > 1) {
				log.warn("Unexpected insert query: plan has multiple branches");
				throw new Exception("Unexpected insert query: plan has multiple branches");
			}

			it = ((OperatorNode) it).getChildren().get(0);
		}

		// Check if main exten plan has as a DataSourceNode main's plan head
		if (!((DataSourceNode) it).getSource().equals(((OperatorNode) main).getFunctionalArgs().get("tableName"))) {
			log.warn("Unexpected insert query: plan has multiple branches");
			throw new Exception("plans can not be concatenated");
		}

		// Add DataSource filterMask as a selector
		HashMap<String, String> functionalArgs = new HashMap<String, String>();
		functionalArgs.put("schema", it.getFunctionalArgs().get("schema"));
		functionalArgs.put("filterMask", it.getFunctionalArgs().get("filterMask"));
		OperatorNode select = new OperatorNode(Functionality.SELECT, functionalArgs, null);
		operList.add(select);

		while (!operList.isEmpty()) {
			OperatorNode last = operList.removeLast();

			last.setChildren(((OperatorNode) main).getChildren());

			ArrayList<PlanNode> children = new ArrayList<PlanNode>();
			children.add(last);
			((OperatorNode) main).setChildren(children);
		}

		// Head should have the same schema with previous
		main.getFunctionalArgs().put(
				"schema",
				((OperatorNode) main).getChildren().get(0).getFunctionalArgs().get("schema") != null ? ((OperatorNode) main).getChildren().get(0)
						.getFunctionalArgs().get("schema") : main.getFunctionalArgs().get("schema"));

		return main;
	}

	/**
	 * Optimize provided plan
	 * 
	 * @param node
	 *            The plan to be optimized
	 * @return The optimization of the plan
	 */
	public static PlanNode optimizePlan(PlanNode node) {
		int i, maxTries = 5;
		int hash = node.hashCode();
		for(i = 0; i < maxTries; i++) {
			log.debug("Optimization pass: " + i);
			node = optimizationPassPlan(node);

			if (node.hashCode() == hash)
				break;
			
			hash = node.hashCode();
		}
		
		if (i == maxTries)
			log.warn("Reached maximum (" + i + ") tries of optimization passes");
		return node;
	}
	private static PlanNode optimizationPassPlan(PlanNode node) {
		LinkedList<OperatorNode> operList = getAllOperators(node);
		
		while (!operList.isEmpty()) {
			OperatorNode op = operList.removeLast();
			
			if (op.getFunctionality() == Functionality.SELECT && op.getChildren().size() == 1) {
				PlanNode ch = op.getChildren().get(0);
				if (ch instanceof DataSourceNode) {
					if (op.getFunctionalArgs().containsKey("filterMask") && !op.getFunctionalArgs().containsKey("logicalExpressions")) {
						// Create initial source schema
						ArrayList<String> chFilterList = getList(ch.getFunctionalArgs().get("filterMask"));
						ArrayList<String> opFilterList = getList(op.getFunctionalArgs().get("filterMask"));

						ArrayList<String> newFilterList = new ArrayList<String>();
						for (String str : opFilterList) {
							newFilterList.add(chFilterList.get(Integer.parseInt(str)));
						}

						ArrayList<String> chSchemarList = getList(ch.getFunctionalArgs().get("schema"));
						ArrayList<String> newSchemaList = new ArrayList<String>();
						for (String str : opFilterList) {
							newSchemaList.add(chSchemarList.get(Integer.parseInt(str)));
						}
						
						ch.getFunctionalArgs().put("filterMask", newFilterList.toString());
						ch.getFunctionalArgs().put("schema", newSchemaList.toString());

						if (op.getFunctionalArgs().containsKey("logicalExpressions"))
							op.getFunctionalArgs().remove("filterMask");
						else {
							if (op.getParent() != null) {
								op.getParent().getChildren().remove(op);
								op.getParent().addChild(ch);
								operList.remove(op);
							}
						}

					}
				} else if (ch instanceof OperatorNode) {
					if (((OperatorNode) ch).getFunctionality() == Functionality.SELECT) {
						if (!op.getFunctionalArgs().containsKey("logicalExpressions")) {
							if (ch.getFunctionalArgs().containsKey("logicalExpressions")) {
								op.getFunctionalArgs().put("logicalExpressions", ch.getFunctionalArgs().get("logicalExpressions"));

								if(ch.getFunctionalArgs().containsKey("filterMask")) {
									ArrayList<String> chFilterList = getList(ch.getFunctionalArgs().get("filterMask"));
									ArrayList<String> opFilterList = getList(op.getFunctionalArgs().get("filterMask"));
	
									ArrayList<String> newFilterList = new ArrayList<String>();
									for (String str : opFilterList) {
										newFilterList.add(chFilterList.get(Integer.parseInt(str)));
									}
									
									op.getFunctionalArgs().put("filterMask", newFilterList.toString());

									ArrayList<String> chSchemarList = getList(ch.getFunctionalArgs().get("schema"));

									ArrayList<String> newSchemaList = new ArrayList<String>();
									for (String str : opFilterList) {
										newSchemaList.add(chSchemarList.get(Integer.parseInt(str)));
									}
									
									op.getFunctionalArgs().put("schema", newSchemaList.toString());
								}
								
								op.setChildren(((OperatorNode) ch).getChildren());
								operList.remove((OperatorNode) ch);
							} else{
								// Create initial source schema
								ArrayList<String> prevFilterList = getList(ch.getFunctionalArgs().get("filterMask"));
								ArrayList<String> currFilterList = getList(op.getFunctionalArgs().get("filterMask"));

								ArrayList<String> newFilterList = new ArrayList<String>();
								for (String str : currFilterList) {
									newFilterList.add(prevFilterList.get(Integer.parseInt(str)));
								}
								
								op.getFunctionalArgs().put("filterMask", newFilterList.toString());
								
								op.setChildren(((OperatorNode) ch).getChildren());
								operList.remove((OperatorNode) ch);
							}
						}
					}
				}
			}
		}

		return node;
	}

	private static LinkedList<OperatorNode> getAllOperators(PlanNode node) {
		LinkedList<OperatorNode> operList = new LinkedList<OperatorNode>();

		if (!(node instanceof DataSourceNode)) {
			operList.add((OperatorNode) node);

			for (PlanNode ch : ((OperatorNode) node).getChildren()) {
				operList.addAll(getAllOperators(ch));
			}
		}
		return operList;
	}

	/**
	 * Parses the {@link ExprNodeDesc} recursively and replaces columns with
	 * fields number
	 * 
	 * @param rowSchema
	 * @param exprNodeDesc
	 * @return
	 * @throws Exception
	 */
	private static String getExpression(RowSchema rowSchema, ExprNodeDesc exprNodeDesc) throws Exception {
		if (exprNodeDesc instanceof ExprNodeColumnDesc) {
			String col = ((ExprNodeColumnDesc) exprNodeDesc).getColumn();
			int i = 0;
			for (ColumnInfo columnInfo : rowSchema.getSignature()) {
				if (columnInfo.getInternalName().equals(col)) {
					break;
				}
				i++;
			}
			return "[" + i + "]";
		}

		if (exprNodeDesc instanceof ExprNodeConstantDesc) {
			String value = ((ExprNodeConstantDesc) exprNodeDesc).getValue().toString();
			return value.matches("\\d+")? value : "'" + ((ExprNodeConstantDesc) exprNodeDesc).getValue() + "'";
		}
		
		if (exprNodeDesc instanceof ExprNodeGenericFuncDesc) {
			String[] childrenExprStrings = new String[exprNodeDesc.getChildren().size()];
			for (int i = 0; i < childrenExprStrings.length; i++) {
				childrenExprStrings[i] = getExpression(rowSchema, exprNodeDesc.getChildren().get(i));
			}

			return ((ExprNodeGenericFuncDesc) exprNodeDesc).getGenericUDF().getDisplayString(childrenExprStrings);
		}
		log.warn("Unknown expression" + exprNodeDesc);
		throw new Exception("Unknown expression" + exprNodeDesc);
	}

	/**
	 * Compares two schemas. In case they are the same, it returns null.
	 * Otherwise, it creates a filter mask that describes the changes from
	 * previous schema to the desired one.
	 * 
	 * @param prevSchema
	 *            The previous schema
	 * @param currSchema
	 *            The current schema
	 * @return the filter mask
	 */
	private static String createFilterMask(String prevSchema, String currSchema) {

		if (Arrays.equals(prevSchema.split(",\\s*"), currSchema.split(",\\s*"))) {
			ArrayList<Integer> filterMask = new ArrayList<Integer>();
			
			for (int i = 0; i < currSchema.split(",\\s*").length; i++)
				filterMask.add(i);
			
			return filterMask.toString();
		}

		ArrayList<Integer> filterMask = new ArrayList<Integer>();

		ArrayList<String> currSchemaList = getList(prevSchema);

		currSchema = currSchema.substring(1, currSchema.length() - 1);

		String[] nextSchemaArr = currSchema.split(",\\s*");

		for (String str : nextSchemaArr) {
			filterMask.add(currSchemaList.indexOf(str));
		}

		return filterMask.toString();
	}

	private static ArrayList<String> getList(String strList) {
		strList = strList.substring(1, strList.length() - 1);
		ArrayList<String> list = new ArrayList<String>();

		for (String str : strList.split(",\\s*")) {
			list.add(str);
		}
		return list;
	}

	/**
	 * Get the created plan
	 * 
	 * @return the plan that was created
	 */
	public static PlanNode getPlan() {
		return plan;
	}
}
