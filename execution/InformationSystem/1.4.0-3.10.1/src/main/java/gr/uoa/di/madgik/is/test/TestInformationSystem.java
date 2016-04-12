//package gr.uoa.di.madgik.is.test;
//
//import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
//import java.io.File;
//
//public class TestInformationSystem
//{
//	public static void main(String []args) throws EnvironmentInformationSystemException
//	{
//		String nodeID=SetupEnvironment.RegisterNode("localhost");
//		SetupEnvironment.RegisterBoundaryListener(nodeID, 3000);
//		String invocableID=SetupEnvironment.RegisterInvocable(new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.Calculator.xml"));
//		SetupEnvironment.RegisterInvocableInstance(nodeID, invocableID);
//		SetupEnvironment.RegisterPlots(invocableID, new File("../CallablesTesting/InvocableProfiles/gr.uoa.di.madgik.callables.test.Calculator.plots.xml"));
//	}
//
//}
