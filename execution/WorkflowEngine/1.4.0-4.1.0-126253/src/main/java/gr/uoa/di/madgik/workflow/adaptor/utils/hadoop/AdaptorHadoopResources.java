package gr.uoa.di.madgik.workflow.adaptor.utils.hadoop;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.ss.StorageSystem;
import gr.uoa.di.madgik.workflow.adaptor.utils.IAdaptorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource.ResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.HadoopInOutDirectoryInfo.OutStoreMode;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdaptorHadoopResources implements IAdaptorResources
{
	public Set<AttachedHadoopResource> Resources=new HashSet<AttachedHadoopResource>();
	
	public void Validate() throws WorkflowValidationException
	{
		int jarCount=0;
		int mainCount=0;
		int confCount=0;
		for(AttachedHadoopResource res : this.Resources)
		{
			switch(res.TypeOfResource)
			{
				case Archive:
				{
					jarCount+=1;
					break;
				}
				case Configuration:
				{
					confCount+=1;
					break;
				}
				case File:
				{
					break;
				}
				case Jar:
				{
					jarCount+=1;
					break;
				}
				case MainClass:
				{
					mainCount+=1;
					break;
				}
				case Input:
				case Lib:
				case Output:
				case Argument:
				case Property:
				case Scope:
				{
					break;
				}
				default:
				{
					throw new WorkflowValidationException("Unrecognized resource type " + res.TypeOfResource.toString());
				}
			}
		}
		if(confCount>1) throw new WorkflowValidationException("At most one config resource can be defined");
		if(jarCount!=1) throw new WorkflowValidationException("Exactly one jar resource must be defined");
		if(mainCount!=1) throw new WorkflowValidationException("Exactly one main class resource must be defined");
		String parent=null;
		for(AttachedHadoopResource att : this.GetInputResources())
		{
			String tmp=new File(att.Key).getParent();
			if(parent==null) parent=tmp;
			else if(!parent.equals(tmp)) throw new WorkflowValidationException("All inputs must be under a single input directory");
			if(parent==null) throw new WorkflowValidationException("All inputs must be under a single input directory");
		}
	}
	
	public boolean ResourceExists(String ResourceKey)
	{
		for(AttachedHadoopResource res : this.Resources)
		{
			if(res.Key.equals(ResourceKey)) return true;
		}
		return false;
	}
	
	public AttachedHadoopResource GetResource(String ResourceKey)
	{
		for(AttachedHadoopResource res : this.Resources)
		{
			if(res.Key.equals(ResourceKey)) return res;
		}
		return null;
	}
	
	public void StoreResources(Set<AttachedHadoopResource.ResourceType> ResourcesToStore,EnvHintCollection Hints) throws Exception
	{
		for(AttachedHadoopResource att : this.Resources)
		{
			switch(att.ResourceLocationType)
			{
				case CMSReference:
				{
					if((att.Value==null || att.Value.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource) && !att.IsHDFSPresent) throw new WorkflowValidationException("CMS id not provided for "+att.ResourceLocationType+" provided attribute");
					att.StorageSystemID=att.Value;
					break;
				}
				case Reference:
				{
					if((att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource) && !att.IsHDFSPresent) att.StorageSystemID = StorageSystem.Store(new URL(att.Value),Hints);
					break;
				}
				case LocalFile:
				{
					if((att.StorageSystemID==null || att.StorageSystemID.trim().length()==0) && ResourcesToStore.contains(att.TypeOfResource) && !att.IsHDFSPresent) att.StorageSystemID = StorageSystem.Store(att.Value,Hints);
					break;
				}
				default: throw new WorkflowValidationException("Unrecognized resource type");
			}
		}
	}
	
	public AttachedHadoopResource GetScopeResource()
	{
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Scope)) return att;
		}
		return null;
	}
	
	public AttachedHadoopResource GetJarResource()
	{
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Jar)) return att;
		}
		return null;
	}
	
	public AttachedHadoopResource GetMainClassResource()
	{
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.MainClass)) return att;
		}
		return null;
	}
	
	public List<AttachedHadoopResource> GetArgumentResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Argument)) lst.add(att);
		}
		Collections.sort(lst);
		return lst;
	}
	
	public AttachedHadoopResource GetConfigurationResource()
	{
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Configuration)) return att;
		}
		return null;
	}
	
	public List<AttachedHadoopResource> GetPropertyResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Property)) lst.add(att);
		}
		return lst;
	}
	
	public List<AttachedHadoopResource> GetFileResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.File)) lst.add(att);
		}
		return lst;
	}
	
	public List<AttachedHadoopResource> GetLibResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Lib)) lst.add(att);
		}
		return lst;
	}
	
	public List<AttachedHadoopResource> GetArchiveResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Archive)) lst.add(att);
		}
		return lst;
	}
	
	public List<AttachedHadoopResource> GetInputResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Input)) lst.add(att);
		}
		return lst;
	}
	
	public Set<HadoopInOutDirectoryInfo> GetInputBaseDirs()
	{
		Set<HadoopInOutDirectoryInfo> infos=new HashSet<HadoopInOutDirectoryInfo>();
		Map<String, HadoopInOutDirectoryInfo> ins=new HashMap<String, HadoopInOutDirectoryInfo>();
		for(AttachedHadoopResource att : this.GetInputResources())
		{
			String dirKey=new File(att.Key).getParent();
			if(!ins.containsKey(dirKey))
			{
				HadoopInOutDirectoryInfo nfo=new HadoopInOutDirectoryInfo();
				nfo.CleanUp=att.CleanUp;
				nfo.Directory=dirKey;
				infos.add(nfo);
				ins.put(dirKey, nfo);
			}
			if(att.CleanUp) ins.get(dirKey).CleanUp=true;
		}
		return infos;
	}
	
	public List<AttachedHadoopResource> GetOutputResources()
	{
		ArrayList<AttachedHadoopResource> lst=new ArrayList<AttachedHadoopResource>();
		for(AttachedHadoopResource att : this.Resources)
		{
			if(att.TypeOfResource.equals(ResourceType.Output)) lst.add(att);
		}
		return lst;
	}
	
	public Set<HadoopInOutDirectoryInfo> GetOutputBaseDirs()
	{
		Set<HadoopInOutDirectoryInfo> infos=new HashSet<HadoopInOutDirectoryInfo>();
		Map<String, HadoopInOutDirectoryInfo> ins=new HashMap<String, HadoopInOutDirectoryInfo>();
		for(AttachedHadoopResource att : this.GetOutputResources())
		{
			String dirKey=att.Key;
//			String dirKey=new File(att.Key).getParent();
//			if(dirKey==null) dirKey=att.Key;
			if(!ins.containsKey(dirKey))
			{
				HadoopInOutDirectoryInfo nfo=new HadoopInOutDirectoryInfo();
				nfo.CleanUp=att.CleanUp;
				nfo.Directory=dirKey;
				if(att.ResourceLocationType == AttachedResourceType.Reference)
				{
					nfo.OutputStoreMode = OutStoreMode.Url;
					nfo.OutputStoreLocation = att.Value;
					nfo.accessInfo.userId = att.accessInfo.userId;
					nfo.accessInfo.password = att.accessInfo.password;
					nfo.accessInfo.port = att.accessInfo.port;
				}
				infos.add(nfo);
				ins.put(dirKey, nfo);
			}
			if(att.CleanUp) ins.get(dirKey).CleanUp=true;
		}
		return infos;
	}
}
