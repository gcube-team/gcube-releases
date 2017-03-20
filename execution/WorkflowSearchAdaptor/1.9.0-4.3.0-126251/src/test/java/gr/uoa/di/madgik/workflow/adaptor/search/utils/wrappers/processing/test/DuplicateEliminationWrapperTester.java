//package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.test;
//
//import java.io.File;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.UUID;
//
//import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
//import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
//import gr.uoa.di.madgik.execution.datatype.IDataType.DataTypes;
//import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
//import gr.uoa.di.madgik.execution.datatype.NamedDataType;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
//import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
//import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
//import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
//import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.duplicategenerator.DuplicateGeneratorOp;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.DoubleGenerator;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.SerialIntegerGenerator;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.StringGenerator;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer.WrapperObserver;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.DuplicateEliminationWrapper;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.DuplicateEliminationWrapper.Variables;
//
///**
// * 
// * @author gerasimos.farantatos - DI NKUA
// *
// */
//public class DuplicateEliminationWrapperTester 
//{
//
//	public static void test() throws Exception 
//	{
//
//		NamedDataType resultVariable = DataTypeUtils.GetNamedDataType(true, "aVariable", null, DataTypes.ResultSet, null);
//		
//		DuplicateGeneratorOp dOp = new DuplicateGeneratorOp();
//		URI loc = dOp.compute(400, new String[]{"objId", "objRank", "field1", "field2"}, "objId", "objRank", new Generator[]{new SerialIntegerGenerator(), new DoubleGenerator(47l), new StringGenerator(10, 48l, true), new StringGenerator(10, 49l, true)}, false, false, 0.4f, 47, ProxyType.Local, new File("/home/gerasimos/Desktop/duplicates.txt"));
//		
//	//	gRSReader.read(loc);
//	//	System.exit(0);
//		
//		//return loc;
//		
//	//	RandomGeneratorOp rndOp = new RandomGeneratorOp();
//	//	URI loc = rndOp.compute(200, false,
//	//			new String[]{"field1", "field2"},
//	//			new Generator[]{new StringGenerator(30, 47l, true), new StringGenerator(30, 48l, true)}
//	//		, false, 0, 60, TimeUnit.SECONDS, 0.5f, null);
//		DuplicateEliminationWrapper wrapper = new DuplicateEliminationWrapper();
//		wrapper.setVariableName(Variables.OutputLocator, "aVariable");
//		
//		NamedDataType locDt = new NamedDataType();
//		locDt.Name = UUID.randomUUID().toString();
//		locDt.Token = locDt.Name;
//		locDt.Value = new DataTypeResultSet();
//		locDt.Value.SetValue(loc);
//		
//		wrapper.setInputLocator(locDt);
//		wrapper.setObjectIdFieldName("objId");
//		IPlanElement element = wrapper.constructPlanElements()[0];
//		System.out.println(element.ToXML());
//		
//		ExecutionPlan plan = new ExecutionPlan();
//		plan.Variables.Add(resultVariable);
//		
//		wrapper.addVariablesToPlan(plan);
//		plan.Root = element;
//		System.out.println(wrapper.getVariableName(Variables.MaximumRank));
//		System.out.println(wrapper.getVariableName(Variables.Timeout));
//		System.out.println(plan.Serialize());
//		
//		ExecutionHandle handle = ExecutionEngine.Submit(plan);
//		WrapperObserver observer = new WrapperObserver(handle);
//		handle.RegisterObserver(observer);
//		Object synchCompletion = observer.getSynchCompletionObject();
//		synchronized(synchCompletion) {
//			ExecutionEngine.Execute(handle);
//			try { synchCompletion.wait(); } catch(Exception e) { }
//		}
//		
//		System.out.println(plan.Variables.Get(resultVariable.Name).Value.GetStringValue());
//		
//		//gRSReader.read(new URI(plan.Variables.Get(resultVariable.Name).Value.GetStringValue()));
//	}
//	
//	public static void main(String[] args) throws Exception 
//	{
//		TCPConnectionManager.Init(new TCPConnectionManagerConfig("localhost",new ArrayList<gr.uoa.di.madgik.commons.server.PortRange>(),true));
//		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
//		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
//		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
//		
//		test();
//	}
//}
