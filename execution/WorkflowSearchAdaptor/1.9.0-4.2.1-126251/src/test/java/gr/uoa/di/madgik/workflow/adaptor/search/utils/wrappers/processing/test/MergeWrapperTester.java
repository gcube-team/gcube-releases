//package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.test;
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
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.duplicateeliminatoroperator.DistinctOp;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.Generator;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.generators.StringGenerator;
//import gr.uoa.di.madgik.searchlibrary.operatorlibrary.test.randomgenerator.RandomGeneratorOp;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.observer.WrapperObserver;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.MergeWrapper;
//import gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing.MergeWrapper.Variables;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Random;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
///**
// * 
// * @author gerasimos.farantatos - DI NKUA
// *
// */
//public class MergeWrapperTester 
//{
//	
//	private static int[] computeSizes(int count, boolean randomize, Long seed) {
//		Random rnd;
//		int base;
//		int[] sizes = new int[count];
//		if(randomize == true) {
//			if(seed == null)
//				seed = Calendar.getInstance().getTimeInMillis();
//			rnd = new Random(seed);
//			float dataSetSize = rnd.nextFloat();
//			if(dataSetSize < 0.5f)
//				base = 10000;
//			else
//				base = 1000;
//			sizes[0] = 4*(base + rnd.nextInt((int)(base*0.3f)));
//			sizes[1] = 6*(base + rnd.nextInt((int)(base*0.3f)));
//			sizes[2] = base + rnd.nextInt((int)(base*0.3f));
//			for(int i = 0; i < count; i++)
//				sizes[i] = base + rnd.nextInt((int)(base*0.3f));
//		}else 
//		{
//			sizes[0] = 4000000;
//			sizes[1] = 6000000;
//			sizes[2] = 1000000;
//			for(int i = 3; i < count; i++)
//				sizes[i] = sizes[2];
//		}
//		return sizes;
//	}
//	
//	public static void testNoDuplicateElimination() throws Exception 
//	{
//		
//		int[] sizes = computeSizes(3, false, null);
//		
//		RandomGeneratorOp RndOp1 = new RandomGeneratorOp();
//		RandomGeneratorOp RndOp2 = new RandomGeneratorOp();
//		RandomGeneratorOp RndOp3 = new RandomGeneratorOp();
//		
//		URI loc1 = RndOp1.compute(sizes[0], false, ProxyType.Local, new String[]{"field1"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 1, 60, TimeUnit.SECONDS, 100, null, null);
//		URI loc2 = RndOp2.compute(sizes[1], false, ProxyType.Local, new String[]{"field2"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 2, 60, TimeUnit.SECONDS, 100, null, null);
//		URI loc3 = RndOp3.compute(sizes[2], false, ProxyType.Local, new String[]{"field3"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 3, 60, TimeUnit.SECONDS, 100, null, null);
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
//		MergeWrapper wrapper = new MergeWrapper();
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
//		NamedDataType loc3Dt = new NamedDataType();
//		loc3Dt.Name = UUID.randomUUID().toString();
//		loc3Dt.Token = loc3Dt.Name;
//		loc3Dt.Value = new DataTypeResultSet();
//		loc3Dt.Value.SetValue(loc3);
//		
//		wrapper.setInputLocator(2, loc3Dt);
//		wrapper.setInputLocator(0, loc1Dt);
//		wrapper.setInputLocator(1, loc2Dt);
//		wrapper.setStatisticsContainer(new StatsContainer());
//		
//		IPlanElement element = wrapper.constructPlanElements()[0];
//		System.out.println(element.ToXML());
//		
//		ExecutionPlan plan = new ExecutionPlan();
//		
//		wrapper.addVariablesToPlan(plan);
//		plan.Root = element;
//		System.out.println(wrapper.getVariableName(Variables.ObjectRankFieldName));
//		System.out.println(wrapper.getVariableName(Variables.Timeout));
//		System.out.println(plan.Serialize());
//		
//		ExecutionHandle handle = ExecutionEngine.Submit(plan);
//		WrapperObserver observer = new WrapperObserver(handle);
//		handle.RegisterObserver(observer);
//		Object synchCompletion = observer.getSynchCompletionObject();
//		synchronized(synchCompletion) 
//		{
//			ExecutionEngine.Execute(handle);
//			try { synchCompletion.wait(); } catch(Exception e) { }
//		}
//		
//		System.out.println(plan.Variables.Get(wrapper.getVariableName(MergeWrapper.Variables.OutputLocator)).Value.GetStringValue());
//		
//		//gRSReader.read(new URI(plan.Variables.Get(wrapper.getVariableName(MergeWrapper.Variables.OutputLocator)).Value.GetStringValue()));
//	}
//	
//	public static void testWithDuplicateElimination() throws Exception 
//	{
//		int[] sizes = computeSizes(3, false, null);
//		
//		RandomGeneratorOp RndOp1 = new RandomGeneratorOp();
//		RandomGeneratorOp RndOp2 = new RandomGeneratorOp();
//		RandomGeneratorOp RndOp3 = new RandomGeneratorOp();
//		
//		URI loc1 = RndOp1.compute(sizes[0], false, ProxyType.Local, new String[]{"field1"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 1, 60, TimeUnit.SECONDS, 100, null, null);
//		URI loc2 = RndOp2.compute(sizes[1], false, ProxyType.Local, new String[]{"field2"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 2, 60, TimeUnit.SECONDS, 100, null, null);
//		URI loc3 = RndOp3.compute(sizes[2], false, ProxyType.Local, new String[]{"field3"}, new Generator[]{new StringGenerator(100, null, true)}, true, false, 3, 60, TimeUnit.SECONDS, 100, null, null);
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
//		MergeWrapper wrapper = new MergeWrapper();
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
//		NamedDataType loc3Dt = new NamedDataType();
//		loc3Dt.Name = UUID.randomUUID().toString();
//		loc3Dt.Token = loc3Dt.Name;
//		loc3Dt.Value = new DataTypeResultSet();
//		loc3Dt.Value.SetValue(loc3);
//		
//		wrapper.setVariableName(Variables.OutputLocator, "aVariable");
//		wrapper.setInputLocator(2, loc3Dt);
//		wrapper.setInputLocator(0, loc1Dt);
//		wrapper.setInputLocator(1, loc2Dt);
//		wrapper.setStatisticsContainer(new StatsContainer());
//		wrapper.enableDuplicateElimination(DistinctOp.class.getName());
//		
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
//		System.out.println(wrapper.getVariableName(Variables.ObjectRankFieldName));
//		System.out.println(wrapper.getVariableName(Variables.Timeout));
//		System.out.println(plan.Serialize());
//		
//		ExecutionHandle handle = ExecutionEngine.Submit(plan);
//		WrapperObserver observer = new WrapperObserver(handle);
//		handle.RegisterObserver(observer);
//		Object synchCompletion = observer.getSynchCompletionObject();
//		synchronized(synchCompletion) 
//		{
//			ExecutionEngine.Execute(handle);
//			try { synchCompletion.wait(); } catch(Exception e) { }
//		}
//		
//		System.out.println(plan.Variables.Get(wrapper.getVariableName(MergeWrapper.Variables.OutputLocator)).Value.GetStringValue());
//		
//		//gRSReader.read(new URI(plan.Variables.Get(wrapper.getVariableName(MergeWrapper.Variables.OutputLocator)).Value.GetStringValue()));
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
//		testNoDuplicateElimination();
//	}
//}
