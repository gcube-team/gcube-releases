package gr.uoa.di.madgik.is;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.IInformationSystemProvider;
import gr.uoa.di.madgik.environment.is.InformationSystemProvider;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.environment.is.Query;

import java.util.List;

public class InformationSystem
{
	private static IInformationSystemProvider Provider=null;
	private static String NodeSelector = "gr.uoa.di.madgik.commons.infra.nodeselection.random.RandomNodeSelector";
	private static Object lockMe=new Object();

	public static void Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		synchronized (InformationSystem.lockMe)
		{
			if(InformationSystem.Provider==null) InformationSystem.Provider = InformationSystemProvider.Init(ProviderName, Hints);
		}
	}
	
	public static NodeSelector GetDefaultNodeSelector() throws EnvironmentInformationSystemException
	{
		try { return (NodeSelector)Class.forName(NodeSelector).newInstance(); }
		catch(Exception e) { throw new EnvironmentInformationSystemException("Could not construct default node selector"); }
	}
	
	public static List<String> Query(String QueryString,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.Query(QueryString, Hints);
	}
	
	public static List<String> Query(Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.Query(query, Hints);
	}
	
	public static List<String> RetrieveByQualifier(String qualifier,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.RetrieveByQualifier(qualifier,Hints);
	}
	
	public static String RegisterNode(NodeInfo Info,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.RegisterNode(Info, Hints);
	}
	
	public static void UnregisterNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		InformationSystem.Provider.UnregisterNode(NodeID, Hints);
	}
	
//	public static String RegisterBoundaryListener(BoundaryListenerInfo info,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.RegisterBoundaryListener(info, Hints);
//	}
	
//	public static void RegisterInvocableInstance(String NodeID, String InvocableID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		InformationSystem.Provider.RegisterInvocableInstance(NodeID, InvocableID, Hints);
//	}
	
//	public static String RegisterInvocable(InvocableProfileInfo info,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.RegisterInvocable(info, Hints);
//	}
	
//	public static void RegisterPlotOfInvocable(String InvocableID, InvocablePlotInfo info,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		InformationSystem.Provider.RegisterPlotOfInvocable(InvocableID, info, Hints);
//	}
	
//	public static BoundaryListenerInfo GetBoundaryListenerInNode(String NodeID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetBoundaryListenerInNode(NodeID, Hints);
//	}
	
	public static NodeInfo GetNode(String NodeID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetNode(NodeID, Hints);
	}
	
	public static NodeInfo GetNode(String Hostname, String Port, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetNode(Hostname, Port, Hints);
	}
	
//	public static InvocableProfileInfo GetInvocableProfile(String InvocableProfileID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetInvocableProfile(InvocableProfileID, Hints);
//	}
	
//	public static BoundaryListenerInfo GetBoundaryListener(String ListenerID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetBoundaryListener(ListenerID, Hints);
//	}
	
	public static NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetMatchingNode(RankingExpression, RequirementsExpression, Hints);
	}
	
	public static NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, NodeSelector selector, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.GetMatchingNode(RankingExpression, RequirementsExpression, selector, Hints);
	}
	
	public static String CreateGenericResource(String content, Query attributes, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.CreateGenericResource(content, attributes, Hints);
	}
	
	public static List<NodeInfo> GetMatchingNodes(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetMatchingNodes(RankingExpression, RequirementsExpression, Hints);
	}
	
//	public static BoundaryListenerInfo GetRandomBoundaryListener(EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetRandomBoundaryListener(Hints);
//	}
	
//	public static NodeInfo GetRandomGCubeNodeContainingListener(EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetRandomGCubeNodeContainingListener(Hints);
//	}
	
//	public static NodeInfo GetRandomGridUINodeContainingListener(EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetRandomGridUINodeContainingListener(Hints);
//	}
	
//	public static NodeInfo GetRandomCondorUINodeContainingListener(EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetRandomCondorUINodeContainingListener(Hints);
//	}
	
//	public static NodeInfo GetRandomHadoopUINodeContainingListener(EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetRandomHadoopUINodeContainingListener(Hints);
//	}
	
//	public static InvocablePlotInfo GetPlotWithName(String PlotName,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetPlotWithName(PlotName, Hints);
//	}
	
//	public static String GetNodeHostingInvocable(String InvocableID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
//	{
//		return InformationSystem.Provider.GetNodeHostingInvocable(InvocableID, Hints);
//	}
	
	public static String GetGenericByID(String ID,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetGenericByID(ID,Hints);
	}
	
	public static List<String> GetGenericByName(String Name,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetGenericByName(Name,Hints);
	}
	
	public static String GetOpenSearchGenericByDescriptionDocumentURI(String URI,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return InformationSystem.Provider.GetOpenSearchGenericByDescriptionDocumentURI(URI,Hints);
	}
	
	public static void UpdateGenericResource(String id, String content, Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		InformationSystem.Provider.UpdateGenericResource(id, content, query, Hints);
	}
	
	public static void DeleteGenericResource(String id, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		InformationSystem.Provider.DeleteGenericResource(id, Hints);
	}
	
	public static String GetLocalNodeHostName() throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.GetLocalNodeHostName();
	}
	
	public static String GetLocalNodePort() throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.GetLocalNodePort();
	}

	public static String GetLocalNodePE2ngPort(EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return InformationSystem.Provider.GetLocalNodePE2ngPort(Hints);
	}
}
