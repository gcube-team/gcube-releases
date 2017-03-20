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
//import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
//import gr.uoa.di.madgik.execution.datatype.NamedDataType;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
//import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
//import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
//import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
//import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
//import gr.uoa.di.madgik.execution.plan.element.SequencePlanElement;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
//import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
//import gr.uoa.di.madgik.grs.proxy.IProxy.ProxyType;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.seqgenerator.SeqGeneratorOp;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer.WrapperObserver;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.JoinWrapper;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.JoinWrapper.Variables;
//
///**
// * 
// * @author gerasimos.farantatos - DI NKUA
// *
// */
//public class JoinWrapperTester 
//{
//	public static void testNoDuplicateElimination() throws Exception 
//	{
//		SeqGeneratorOp SeqOp1 = new SeqGeneratorOp();
//		SeqGeneratorOp SecOp2 = new SeqGeneratorOp();
//		URI loc1 = SeqOp1.compute(300, false, ProxyType.Local, false, true, 1, new String[]{"id", "title"}, 1, 47, 60, TimeUnit.SECONDS);
//		URI loc2 = SecOp2.compute(400, false, ProxyType.Local, false, true, 0, new String[]{"title", "price"}, 2, 98, 60, TimeUnit.SECONDS);
//		
//	//	gRSReader.read(loc2);
//	//	System.exit(0);
//		
//	//	return loc;
//		
//	//	RandomGeneratorOp rndOp = new RandomGeneratorOp();
//	//	URI loc = rndOp.compute(200, false,
//	//			new String[]{"field1", "field2"},
//	//			new Generator[]{new StringGenerator(30, 47l, true), new StringGenerator(30, 48l, true)}
//	//		, false, 0, 60, TimeUnit.SECONDS, 0.5f, null);
//		JoinWrapper wrapper = new JoinWrapper();
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
//		wrapper.setStatisticsContainer(new StatsContainer());
//		
//		wrapper.setLeftKeyFieldName("title");
//		wrapper.setRightKeyFieldName("title");
//		IPlanElement element = wrapper.constructPlanElements()[0];
//		System.out.println(element.ToXML());
//		
//		ExecutionPlan plan = new ExecutionPlan();
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
//		System.out.println(plan.Variables.Get(wrapper.getVariableName(JoinWrapper.Variables.OutputLocator)).Value.GetStringValue());
//		
//	//	gRSReader.read(new URI(plan.Variables.Get(wrapper.getVariableName(JoinWrapper.Variables.OutputLocator)).Value.GetStringValue()));
//	}
//	
//	public static void testWithDuplicateElimination() throws Exception 
//	{
//		SeqGeneratorOp SeqOp1 = new SeqGeneratorOp();
//		SeqGeneratorOp SecOp2 = new SeqGeneratorOp();
//		URI loc1 = SeqOp1.compute(300, false, ProxyType.Local, false, true, 1, new String[]{"id", "title"}, 1, 47, 60, TimeUnit.SECONDS);
//		URI loc2 = SecOp2.compute(400, false, ProxyType.Local, false, true, 0, new String[]{"title", "price"}, 2, 98, 60, TimeUnit.SECONDS);
//		
//	//	gRSReader.read(loc2);
//	//	System.exit(0);
//		
//	//	return loc;
//		
//	//	RandomGeneratorOp rndOp = new RandomGeneratorOp();
//	//	URI loc = rndOp.compute(200, false,
//	//			new String[]{"field1", "field2"},
//	//			new Generator[]{new StringGenerator(30, 47l, true), new StringGenerator(30, 48l, true)}
//	//		, false, 0, 60, TimeUnit.SECONDS, 0.5f, null);
//		JoinWrapper wrapper = new JoinWrapper();
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
//		
//		wrapper.setLeftInputLocator(loc1Dt);
//		wrapper.setRightInputLocator(loc2Dt);
//		wrapper.setStatisticsContainer(new StatsContainer());
//		wrapper.enableDuplicateElimination();
//		
//		wrapper.setLeftKeyFieldName("title");
//		wrapper.setRightKeyFieldName("title");
//		IPlanElement[] elements = wrapper.constructPlanElements();
//		System.out.println(elements[0].ToXML());
//		System.out.println(elements[1].ToXML());
//		
//		ExecutionPlan plan = new ExecutionPlan();
//		
//		wrapper.addVariablesToPlan(plan);
//		
//		SequencePlanElement seq = new SequencePlanElement();
//		seq.ElementCollection.add(elements[0]);
//		seq.ElementCollection.add(elements[1]);
//		plan.Root = seq;
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
//		System.out.println(plan.Variables.Get(wrapper.getVariableName(JoinWrapper.Variables.OutputLocator)).Value.GetStringValue());
//		
//		//gRSReader.read(new URI(plan.Variables.Get(wrapper.getVariableName(JoinWrapper.Variables.OutputLocator)).Value.GetStringValue()));
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
//		testWithDuplicateElimination();
//	
//	}
//}
