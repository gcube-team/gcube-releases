package gr.uoa.di.madgik.environment.is;

import java.util.List;

import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.environment.IEnvironmentProvider;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;

public interface IInformationSystemProvider extends IEnvironmentProvider
{
	public String RegisterNode(NodeInfo info, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public void UnregisterNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public NodeInfo GetNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public NodeInfo GetNode(String Hostname, String Port, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, NodeSelector selector, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<NodeInfo> GetMatchingNodes(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public List<String> RetrieveByQualifier(String qualifier,EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<String> Query(String query,EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<String> Query(Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public void UpdateGenericResource(String id, String content, Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public String GetGenericByID(String ID,EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<String> GetGenericByName(String Name,EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public String GetOpenSearchGenericByDescriptionDocumentURI(String URI,EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public String CreateGenericResource(String content, Query attributes, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public void DeleteGenericResource(String id, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public String GetLocalNodeHostName() throws EnvironmentInformationSystemException;
	public String GetLocalNodePort() throws EnvironmentInformationSystemException;
	public String GetLocalNodePE2ngPort(EnvHintCollection Hints) throws EnvironmentInformationSystemException;
}
