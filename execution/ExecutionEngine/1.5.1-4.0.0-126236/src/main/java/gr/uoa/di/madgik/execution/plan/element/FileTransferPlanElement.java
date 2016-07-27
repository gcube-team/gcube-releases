package gr.uoa.di.madgik.execution.plan.element;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.contingency.ContingencyTrigger;
import gr.uoa.di.madgik.execution.plan.element.contingency.IContingencyReaction;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.plan.element.variable.IOutputParameter;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import gr.uoa.di.madgik.ss.StorageSystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class can be used to mediate at the execution level the staging of files to and from the {@link StorageSystem}.
 * The transfer can have a direction of either {@link FileTransferPlanElement.TransferDirection#Store} or
 * {@link FileTransferPlanElement.TransferDirection#Retrieve}. In case the direction is 
 * {@link FileTransferPlanElement.TransferDirection#Store}, the {@link FileTransferPlanElement#Input} parameter
 * is expected to have a value of the file path that is to be stored in the {@link StorageSystem} and is located in the 
 * same hosting machine as the one execution the element. After the operation is completed, the {@link FileTransferPlanElement#Output}
 * parameter will have as value the identifier assigned to the stored file by the {@link StorageSystem}. In case
 * the direction is {@link FileTransferPlanElement.TransferDirection#Retrieve}, the {@link FileTransferPlanElement#Input}
 * parameter is expected to have as value the identifier of the document that is stored in the {@link StorageSystem}
 * and after the execution, the {@link FileTransferPlanElement#Output} will have the local filename where the document is stored.
 * In case of the {@link FileTransferPlanElement#Direction} has a value of {@link FileTransferPlanElement.TransferDirection#Retrieve},
 * the {@link FileTransferPlanElement#MoveTo} parameter can also be set that can dictate a new filename to rename the retrieved
 * file to. In same case the permission attributes can also be set. The {@link FileTransferPlanElement#IsExecutable} flag can
 * be used to make the file executable, or the more flexible {@link FileTransferPlanElement#Permissions} field can be
 * used to set the full permissions of the file. For these permissions the common four digit UNIX format is used.
 * 
 * @author gpapanikos
 */
public class FileTransferPlanElement extends PlanElementBase
{
	
	/**
	 * The transfer direction
	 */
	public enum TransferDirection
	{
		
		/** Store to the {@link StorageSystem} */
		Store,
		
		/** Retrieve from the {@link StorageSystem} */
		Retrieve
	}
	
	public enum StoreMode
	{
		StorageSystem,
		Url
	}
	
	public static class AccessInfo
	{
		public String userId;
		public String password;
		public int port = -1;
	}
	
	/** The logger. */
	private static Logger logger=LoggerFactory.getLogger(FileTransferPlanElement.class);
	
	/** The ID of the element */
	private String ID = UUID.randomUUID().toString();
	
	/** The Name of the element */
	private String Name = FileTransferPlanElement.class.getSimpleName();
	
	/** This parameter holds the input value of the {@link FileTransferPlanElement} operation.
	 * If the {@link FileTransferPlanElement#Direction} field has a value of 
	 * {@link TransferDirection#Retrieve}, this parameter is expected to have the 
	 * {@link StorageSystem} identifier of the document to retrieve. in case the 
	 * {@link FileTransferPlanElement#Direction} field has a value of 
	 * {@link TransferDirection#Store}, this parameter is expected to have the path
	 * of the file to store to the {@link StorageSystem}*/
	public IInputParameter Input = null;
	
	/** After the file transfer operation this parameter will contain the respective 
	 * {@link StorageSystem} return value. in case the {@link FileTransferPlanElement#Direction} 
	 * field has a value of {@link TransferDirection#Retrieve}, this parameter will be set to 
	 * the name of the file that is retrieved by the {@link StorageSystem}. If the 
	 * {@link FileTransferPlanElement#MoveTo} parameter is set, the filename that is retrieved
	 * by this parameter is stored in {@link FileTransferPlanElement#Output}. If the direction 
	 * is set to {@link TransferDirection#Store}, the {@link StorageSystem} identifier 
	 * assigned to the stored file is set to {@link FileTransferPlanElement#Output} */
	public IOutputParameter Output = null;
	
	/** The Direction. */
	public TransferDirection Direction=TransferDirection.Store;
	
	/** After a file has been retrieved from the {@link StorageSystem}, it is stored in a 
	 * temporary location. This parameter can be set to retrieve a filename that the temporary
	 * file should be renamed to. This value is only considered in case the 
	 * {@link FileTransferPlanElement#Direction} field has a value of 
	 * {@link TransferDirection#Retrieve} */
	public IInputParameter MoveTo=null;
	
	/** The Permissions to set to the file transfered. The value follows the 
	 * UNIX pattern of the 4 digits where permissions for owner, group and 
	 * others are set. This value is only considered in case the 
	 * {@link FileTransferPlanElement#Direction} field has a value of 
	 * {@link TransferDirection#Retrieve} */
	public String Permissions=null;
	
	/** Flag indicating that the file transfered is executable and should have its 
	 * permissions set accordingly. This value is only considered in case the 
	 * {@link FileTransferPlanElement#Direction} field has a value of 
	 * {@link TransferDirection#Retrieve} */
	public boolean IsExecutable=false;
	
	public StoreMode OutputStoreMode=StoreMode.StorageSystem;
	public String StoreUrlLocation=null;
	public AccessInfo accessInfo = new AccessInfo();

	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(java.lang.String)
	 */
	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc=null;
		try{
			doc=XMLUtils.Deserialize(XML);
		}
		catch(Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#FromXML(org.w3c.dom.Element)
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException
	{
		try
		{
			if (!IPlanElement.PlanElementType.valueOf(XMLUtils.GetAttribute((Element) XML, "type")).equals(this.GetPlanElementType())) throw new ExecutionSerializationException("plan element type missmatch");
			this.ID = XMLUtils.GetAttribute((Element) XML, "id");
			this.Name = XMLUtils.GetAttribute((Element) XML, "name");
			this.IsExecutable = DataTypeUtils.GetValueAsBoolean(XMLUtils.GetAttribute((Element) XML, "isexec"));
			Element outelement=XMLUtils.GetChildElementWithName(XML, "output");
			if(outelement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			Element parelem=XMLUtils.GetChildElementWithName(outelement, "param");
			if(parelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			this.Output=(IOutputParameter)ParameterUtils.GetParameter(parelem);
			Element inelement=XMLUtils.GetChildElementWithName(XML, "input");
			if(inelement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			parelem=XMLUtils.GetChildElementWithName(inelement, "param");
			if(parelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
			this.Input=(IInputParameter)ParameterUtils.GetParameter(parelem);
			Element directionelement=XMLUtils.GetChildElementWithName(XML, "direction");
			if(directionelement==null) throw new ExecutionSerializationException("Provided serialization not valid");
			if(!XMLUtils.AttributeExists(directionelement, "value")) throw new ExecutionSerializationException("Provided serialization not valid");
			this.Direction = TransferDirection.valueOf(XMLUtils.GetAttribute(directionelement, "value"));
			Element mvelement=XMLUtils.GetChildElementWithName(XML, "mvto");
			if(mvelement!=null)
			{
				parelem=XMLUtils.GetChildElementWithName(mvelement, "param");
				if(parelem==null) throw new ExecutionSerializationException("Provided serialization not valid");
				this.MoveTo=(IInputParameter)ParameterUtils.GetParameter(parelem);
			}
			Element permselement=XMLUtils.GetChildElementWithName(XML, "perms");
			if(permselement!=null)
			{
				if(!XMLUtils.AttributeExists(permselement, "value")) throw new ExecutionSerializationException("Provided serialization not valid");
				this.Permissions=XMLUtils.GetAttribute(permselement, "value");
			}
			Element osmElement=XMLUtils.GetChildElementWithName(XML, "outputStoreMode");
			if(osmElement!=null)
			{
				if(!XMLUtils.AttributeExists(osmElement, "value")) throw new ExecutionSerializationException("Provided serialization not valid");
				this.OutputStoreMode=StoreMode.valueOf(XMLUtils.GetAttribute(osmElement, "value"));
			}
			if(this.OutputStoreMode.equals(StoreMode.Url))
			{
				Element slElement=XMLUtils.GetChildElementWithName(XML,"storeUrlLocation");
				if(slElement==null) throw new ExecutionSerializationException("Provided serialization not valid");
				if(!XMLUtils.AttributeExists(slElement, "value")) throw new ExecutionSerializationException("Provided serialization not valid");
				this.StoreUrlLocation=XMLUtils.GetAttribute(slElement, "value");
				
				Element aiElement=XMLUtils.GetChildElementWithName(XML, "accessInfo");
				Element userIdElement=XMLUtils.GetChildElementWithName(aiElement, "userId");
				if(userIdElement != null)
				{
					this.accessInfo.userId = XMLUtils.GetAttribute(userIdElement, "value");
					Element passwordElement=XMLUtils.GetChildElementWithName(aiElement, "password");
					if(passwordElement == null) throw new ExecutionSerializationException("Provided serialization not valid");
					this.accessInfo.password=XMLUtils.GetAttribute(passwordElement, "value");
				}
				Element portElement=XMLUtils.GetChildElementWithName(aiElement, "port");
				if(portElement!=null) this.accessInfo.port=Integer.parseInt(XMLUtils.GetAttribute(portElement, "value"));
			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetID()
	 */
	public String GetID()
	{
		return this.ID;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetName()
	 */
	public String GetName()
	{
		return this.Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetPlanElementType()
	 */
	public PlanElementType GetPlanElementType()
	{
		return IPlanElement.PlanElementType.FileTransfer;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Locate(java.lang.String)
	 */
	public IPlanElement Locate(String ID)
	{
		if (this.ID.equals(ID))
		{
			return this;
		} else
		{
			return null;
		}
	}

	
	public Set<IPlanElement> LocateActionElements()
	{
		HashSet<IPlanElement> acts=new HashSet<IPlanElement>();
		return acts;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetName(java.lang.String)
	 */
	public void SetName(String Name)
	{
		this.Name=Name;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ToXML()
	 */
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<planElement type=\""+this.GetPlanElementType().toString()+"\" id=\""+this.GetID()+"\" name=\""+this.GetName()+"\" isexec=\""+this.IsExecutable+"\">");
		buf.append("<input>");
		buf.append(this.Input.ToXML());
		buf.append("</input>");
		buf.append("<output>");
		buf.append(this.Output.ToXML());
		buf.append("</output>");
		if(this.MoveTo!=null)
		{
			buf.append("<mvto>");
			buf.append(this.MoveTo.ToXML());
			buf.append("</mvto>");
		}
		if(this.Permissions!=null && this.Permissions.trim().length()!=0)
		{
			buf.append("<perms value=\""+this.Permissions+"\"/>");
		}
		buf.append("<direction value=\""+this.Direction.toString()+"\"/>");
		buf.append("<outputStoreMode value=\""+this.OutputStoreMode.toString()+"\"/>");
		if(this.OutputStoreMode.equals(StoreMode.Url)) 
		{
			buf.append("<storeUrlLocation value=\""+this.StoreUrlLocation+"\"/>");
			buf.append("<accessInfo>");
			if(this.accessInfo.userId!=null)
			{
				buf.append("<userId value=\""+this.accessInfo.userId +"\"/>");
				buf.append("<password value=\""+this.accessInfo.password+"\"/>");
			}
			if(this.accessInfo.port!=-1)
			{
				buf.append("<port value=\""+this.accessInfo.port+"\"/>");
			}
			buf.append("</accessInfo>");
		}
		buf.append("</planElement>");
		return buf.toString();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#Validate()
	 */
	public void Validate() throws ExecutionValidationException
	{
		if(this.Input==null) throw new ExecutionValidationException("Input parameter not provided");
		if(this.Output==null) throw new ExecutionValidationException("Output parameter not provided");
		this.Input.Validate();
		this.Output.Validate();
		if(this.MoveTo!=null) this.MoveTo.Validate();
		if(this.Direction!=TransferDirection.Retrieve && this.MoveTo!=null) throw new ExecutionValidationException("Move to is only supported for "+TransferDirection.Retrieve+" operations");
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportedContingencyTriggers()
	 */
	public IContingencyReaction.ReactionType[] SupportedContingencyTriggers()
	{
		return new IContingencyReaction.ReactionType[0];
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SupportsContingencyTriggers()
	 */
	public boolean SupportsContingencyTriggers()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetContingencyTriggers()
	 */
	public List<ContingencyTrigger> GetContingencyTriggers()
	{
		return new ArrayList<ContingencyTrigger>();
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#SetContingencyResourcePick(gr.uoa.di.madgik.execution.engine.ExecutionHandle, java.lang.String)
	 */
	public void SetContingencyResourcePick(ExecutionHandle Handle, String Pick) throws ExecutionRunTimeException
	{
		// Nothing to set. Pick Contingency trigger not supported
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetModifiedVariableNames()
	 */
	public Set<String> GetModifiedVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.Output.GetModifiedVariableNames());
		vars.addAll(this.Input.GetModifiedVariableNames());
		if(this.MoveTo!=null) vars.addAll(this.MoveTo.GetModifiedVariableNames());
		return vars;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#GetNeededVariableNames()
	 */
	public Set<String> GetNeededVariableNames()
	{
		Set<String> vars=new HashSet<String>();
		vars.addAll(this.Output.GetNeededVariableNames());
		vars.addAll(this.Input.GetNeededVariableNames());
		if(this.MoveTo!=null) vars.addAll(this.MoveTo.GetNeededVariableNames());
		return vars;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#GetExtenderLogger()
	 */
	public Logger GetExtenderLogger()
	{
		return logger;
	}

	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.PlanElementBase#ExecuteExtender(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ExecuteExtender(ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException,ExecutionBreakException
	{
		this.StartClock(ClockType.Total);
		this.StartClock(ClockType.Init);
		
		int port = -1;
		String hostname = "Unknown";
		
		try {
			if(Handle.getHostingNodeInfo() != null) {
				String[] params = Handle.getHostingNodeInfo().split(":");
				hostname = params[0];
				if(params[1].compareTo("null") != 0)
					port = Integer.parseInt(params[1]);
			}
		}
		catch(Exception e) {
			logger.warn("Unexpected error occurred!", e);
		}
		
		logger.debug("Starting");
		this.CheckStatus(Handle);
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),1, 3, "Starting Execution of "+this.Name, this.Name, hostname, port));
		try
		{
			String outputValue=null;
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),2, 3, "Contacting Storage System", this.Name, hostname, port));
			this.StopClock(ClockType.Init);
			this.StartClock(ClockType.Children);
			switch(this.Direction)
			{
				case Retrieve:
				{
					String inputValue = DataTypeUtils.GetValueAsString(this.Input.GetParameterValue(Handle));
					logger.debug("Retrieveing ID "+inputValue);
					outputValue=StorageSystem.Retrieve(inputValue,Handle.GetPlan().EnvHints).getAbsolutePath();
					if(this.MoveTo!=null)
					{
						Object moveToName = this.MoveTo.GetParameterValue(Handle);
						if(moveToName==null) throw new ExecutionValidationException("Provided move to name is null");
						File targetFile=new File(moveToName.toString());
						if(!targetFile.isAbsolute() && Handle.IsIsolationRequested()) targetFile=new File(Handle.GetIsolationInfo().GetBaseDirFile(),moveToName.toString());
						if(targetFile.exists()) targetFile.delete();
						if(targetFile.getParentFile()!=null) targetFile.getParentFile().mkdirs();
						logger.debug("Retrieved and stored to local: " + targetFile.getAbsolutePath());
						File sourceFile=new File(outputValue);
						if(!sourceFile.renameTo(targetFile)) throw new ExecutionRunTimeException("Could not rename source file "+outputValue+" to "+moveToName.toString());
						outputValue=moveToName.toString();
						if(this.IsExecutable) FileUtils.MakeFileExecutable(targetFile);
						if(this.Permissions!=null) FileUtils.MakeFilePermissions(targetFile, this.Permissions);
					}
					break;
				}
				case Store:
				{
					String inputValue = DataTypeUtils.GetValueAsString(this.Input.GetParameterValue(Handle));
					logger.debug("Storing file "+inputValue);
					File targetinputFile=new File(DataTypeUtils.GetValueAsString(inputValue));
					targetinputFile=Handle.GetIsolatedFile(targetinputFile).getAbsoluteFile();
					logger.debug("Storing local: " + targetinputFile);
					//logger.info("Output store mode:  " + this.OutputStoreMode);
					if(this.OutputStoreMode == StoreMode.StorageSystem) outputValue=StorageSystem.Store(targetinputFile,Handle.GetPlan().EnvHints);
					else 
					{
						logger.info("Storing to output location");
						outputValue = storeOutputToLocation(targetinputFile);
						logger.info("Stored to output location");
					}
					if(this.OutputStoreMode == StoreMode.StorageSystem) Handle.GetPlan().CleanUpSS.Add(outputValue);
					break;
				}
			}
			this.StopClock(ClockType.Children);
			this.StartClock(ClockType.Finilization);
			logger.debug("Outcome was "+outputValue);
			this.Output.SetParameterValue(Handle, outputValue);
			this.StopClock(ClockType.Finilization);
		}
		catch(Exception ex)
		{
			logger.info("Caught exception: ", ex);
			ExceptionUtils.ThrowTransformedException(ex);
		}
		if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(this.GetID(),3, 3, "Finishing Execution of "+this.Name, this.Name, hostname, port));
		logger.debug("Exiting");
		this.StopClock(ClockType.Total);
		if(!Handle.GetPlan().Config.ChokePerformanceReporting) Handle.EmitEvent(this.GetPerformanceEvent());
	}

	private String constructRepositoryUrl() throws Exception
	{
		URL url = new URL(this.StoreUrlLocation);
		boolean usePort = false;
		boolean useAuthority = false;
		int portToUse = -1;
		if(url.getPort() <= 0 && this.accessInfo.port > 0) { usePort = true; portToUse = this.accessInfo.port; }
		if(url.getPort() > 0 ) { usePort = true; if(this.accessInfo.port <= 0) portToUse = url.getPort(); else portToUse = this.accessInfo.port; }
		if(this.accessInfo.userId != null) useAuthority = true;
		String afterProto = this.StoreUrlLocation.substring(this.StoreUrlLocation.indexOf("//")+2);
		String afterHost = afterProto.substring(afterProto.indexOf("/")+1);
		String newUrl = url.getProtocol() + "://" + (useAuthority ? this.accessInfo.userId + ":" + this.accessInfo.password + "@" : "") + 
		url.getHost() + (usePort ? ":" + portToUse : "") + "/" + afterHost;
		return newUrl;
	}
	private String storeOutputToLocation(File output) throws Exception
	{
		//String fName = output.getName() + "." + UUID.randomUUID().toString();
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		byte[] buf = new byte[1024];
		try
        {
			String repositoryUrl = constructRepositoryUrl();
			logger.trace("Storing output to location: " + repositoryUrl);
			URL url = new URL(repositoryUrl);
            URLConnection conn = url.openConnection();
            logger.trace("Opened connection to ftp");
            
            bos = new BufferedOutputStream(conn.getOutputStream());
            logger.trace("Opened output stream");
            bis = new BufferedInputStream(new FileInputStream(output));

            int i;
            // read byte by byte until end of stream
            while ((i = bis.read(buf)) != -1)
            {
               bos.write(buf, 0, i);
            }
         }
         finally
         {
            if (bis != null)
            {
               try { bis.close(); }
               catch (IOException e){ logger.error("Could not close input stream");}
            }
            if (bos != null)
            {
               try { bos.close(); }
               catch (IOException ioe) { logger.error("Could not upload output resource"); }
            }
         }
         return this.StoreUrlLocation;
	}
	
	/* (non-Javadoc)
	 * @see gr.uoa.di.madgik.execution.plan.element.IPlanElement#ValidatePreExecution(gr.uoa.di.madgik.execution.engine.ExecutionHandle)
	 */
	public void ValidatePreExecution(ExecutionHandle Handle) throws ExecutionValidationException
	{
		this.Validate();
		Set<String> ExcludeAvailableConstraint=this.GetModifiedVariableNames();
		this.Input.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
		this.Output.ValidatePreExecution(Handle, ExcludeAvailableConstraint);
	}
	
	public static void main(String[] args) throws Exception
	{
		FileTransferPlanElement ftr = new FileTransferPlanElement();
		ftr.StoreUrlLocation = "ftp://donald.di.uoa.gr:124";
	//	ftr.accessInfo.userId="pe2ng";
	//	ftr.accessInfo.password="pass";
		ftr.accessInfo.port = 123;
		String a = ftr.constructRepositoryUrl();
		System.out.println(a);
	}
}
