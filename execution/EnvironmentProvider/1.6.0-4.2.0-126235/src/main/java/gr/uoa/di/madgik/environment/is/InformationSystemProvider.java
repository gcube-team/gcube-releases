package gr.uoa.di.madgik.environment.is;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;

public class InformationSystemProvider implements IInformationSystemProvider
{
	private static IInformationSystemProvider StaticProvider=null;
	private static final Object lockMe=new Object();

	private IInformationSystemProvider Provider=null;
	private EnvHintCollection InitHints=null;

	public static IInformationSystemProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		try
		{
			synchronized(InformationSystemProvider.lockMe)
			{
				if(InformationSystemProvider.StaticProvider==null)
				{
					StringBuffer classpath = new StringBuffer();
//					ClassLoader applicationClassLoader = InformationSystemProvider.class.getClassLoader();
//				     if (applicationClassLoader == null) {
//				         applicationClassLoader = ClassLoader.getSystemClassLoader();
//				     }
//				     URL[] urls = ((URLClassLoader)applicationClassLoader).getURLs();
//				      for(int i=0; i < urls.length; i++) {
//				          classpath.append(urls[i].getFile()).append("\r\n");
//				      }  
//				      
//				      System.out.println("Classpath: " + classpath.toString());
//					System.out.println("Information System Provider: " + ProviderName);
					if(ProviderName.equals(InformationSystemProvider.class.getName())) throw new EnvironmentValidationException("Class "+InformationSystemProvider.class.getName()+" cannot be defined as provider");
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof IInformationSystemProvider)) throw new EnvironmentValidationException("");
					InformationSystemProvider prov=new InformationSystemProvider();
					prov.Provider=(IInformationSystemProvider)o;
					prov.InitHints=Hints;
					InformationSystemProvider.StaticProvider=prov;
				}
			}
			return InformationSystemProvider.StaticProvider;
		}catch(Exception ex)
		{
			throw new EnvironmentValidationException("Could not initialize Information System Provider", ex);
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(InformationSystemProvider.lockMe)
		{
			return (InformationSystemProvider.StaticProvider!=null);
		}		
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}

	public List<String> RetrieveByQualifier(String qualifier,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.RetrieveByQualifier(qualifier, this.MergeHints(Hints));
	}

	public List<String> Query(Query query,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.Query(query, this.MergeHints(Hints));
	}
	
	public List<String> Query(String query,EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.Query(query, this.MergeHints(Hints));
	}

	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetMatchingNode(RankingExpression, RequirementsExpression, this.MergeHints(Hints));
	}
	
	public NodeInfo GetMatchingNode(String RankingExpression, String RequirementsExpression, NodeSelector selector, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetMatchingNode(RankingExpression, RequirementsExpression, selector, this.MergeHints(Hints));
	}

	public List<NodeInfo> GetMatchingNodes(String RankingExpression, String RequirementsExpression, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetMatchingNodes(RankingExpression, RequirementsExpression, this.MergeHints(Hints));
	}
	
	public NodeInfo GetNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetNode(NodeID, this.MergeHints(Hints));
	}
	
	public NodeInfo GetNode(String Hostname, String Port, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetNode(Hostname, Port, this.MergeHints(Hints));
	}

	public String RegisterNode(NodeInfo info, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.RegisterNode(info, this.MergeHints(Hints));
	}
	
	public void UnregisterNode(String NodeID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		this.Provider.UnregisterNode(NodeID, this.MergeHints(Hints));
	}

	public String GetGenericByID(String ID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetGenericByID(ID, this.MergeHints(Hints));
	}

	public List<String> GetGenericByName(String Name, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetGenericByName(Name, this.MergeHints(Hints));
	}

	public String GetOpenSearchGenericByDescriptionDocumentURI(String URI, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetOpenSearchGenericByDescriptionDocumentURI(URI, this.MergeHints(Hints));
	}

	@Override
	public String CreateGenericResource(String Content, Query attributes, EnvHintCollection Hints) throws EnvironmentInformationSystemException 
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.CreateGenericResource(Content, attributes, this.MergeHints(Hints));
	}

	@Override
	public void UpdateGenericResource(String ID, String Content, Query query, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		this.Provider.UpdateGenericResource(ID, Content, query, this.MergeHints(Hints));
	}
	
	@Override
	public void DeleteGenericResource(String ID, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		this.Provider.DeleteGenericResource(ID, this.MergeHints(Hints));
	}

	@Override
	public String GetLocalNodeHostName() throws EnvironmentInformationSystemException {
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetLocalNodeHostName();
	}

	@Override
	public String GetLocalNodePort() throws EnvironmentInformationSystemException {
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetLocalNodePort();
	}

	@Override
	public String GetLocalNodePE2ngPort(EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		if(this.Provider==null) throw new EnvironmentInformationSystemException("Provider not initialized");
		return this.Provider.GetLocalNodePE2ngPort(this.MergeHints(Hints));
	}

}
