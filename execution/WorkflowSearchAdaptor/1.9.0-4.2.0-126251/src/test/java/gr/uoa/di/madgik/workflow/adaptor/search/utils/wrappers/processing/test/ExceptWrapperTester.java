//package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.test;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
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
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.seqgenerator.SeqGeneratorOp;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer.WrapperObserver;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.ExceptWrapper;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.ExceptWrapper.Variables;
//
///**
// * 
// * @author gerasimos.farantatos - DI NKUA
// *
// */
//public class ExceptWrapperTester 
//{
//
//	public static void test() throws Exception 
//	{
//		NamedDataType resultVariable = DataTypeUtils.GetNamedDataType(true, "aVariable", null, DataTypes.ResultSet, null);
//		
//		SeqGeneratorOp SeqOp1 = new SeqGeneratorOp();
//		SeqGeneratorOp SeqOp2 = new SeqGeneratorOp();
//		URI loc1 = SeqOp1.compute(30, false, ProxyType.Local, false, true, 0, new String[]{"id", "title"}, 1, 47, 60, TimeUnit.SECONDS);
//		URI loc2 = SeqOp2.compute(40, false, ProxyType.Local, false, true, 0, new String[]{"id", "title"}, 2, 98, 60, TimeUnit.SECONDS);
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
//		
//		ExceptWrapper wrapper = new ExceptWrapper();
//		wrapper.setVariableName(Variables.OutputLocator, "aVariable");
//		
//		NamedDataType loc1Dt = new NamedDataType();
//		loc1Dt.Name = UUID.randomUUID().toString();
//		loc1Dt.Token = loc1Dt.Name;
//		loc1Dt.Value = new DataTypeResultSet();
//		loc1Dt.Value.SetValue(loc1);
//		
//		NamedDataType loc2Dt = new NamedDataType();
//		loc2Dt.Name = UUID.randomUUID().toString();
//		loc2Dt.Token = loc2Dt.Name;
//		loc2Dt.Value = new DataTypeResultSet();
//		loc2Dt.Value.SetValue(loc2);
//		
//		wrapper.setLeftInputLocator(loc1Dt);
//		wrapper.setRightInputLocator(loc2Dt);
//		wrapper.setLeftKeyFieldName("id");
//		wrapper.setRightKeyFieldName("id");
//		IPlanElement element = wrapper.constructPlanElements()[0];
//		System.out.println(element.ToXML());
//		
//		ExecutionPlan plan = new ExecutionPlan();
//		plan.Variables.Add(resultVariable);
//		
//		wrapper.addVariablesToPlan(plan);
//		plan.Root = element;
//		System.out.println(wrapper.getVariableName(Variables.LeftKeyFieldName));
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
