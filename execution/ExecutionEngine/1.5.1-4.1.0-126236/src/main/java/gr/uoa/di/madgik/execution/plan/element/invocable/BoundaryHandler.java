package gr.uoa.di.madgik.execution.plan.element.invocable;

import gr.uoa.di.madgik.commons.channel.events.ObjectPayloadChannelEvent;
import gr.uoa.di.madgik.commons.channel.proxy.ChannelLocatorFactory;
import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.commons.server.ITCPConnectionManagerEntry;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.commons.utils.ZipUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.ExecutionPlan;
import gr.uoa.di.madgik.execution.plan.PlanConfig;
import gr.uoa.di.madgik.execution.plan.PlanConfig.ConnectionMode;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.attachment.ExecutionAttachment;
import gr.uoa.di.madgik.execution.plan.element.attachment.ExecutionAttachment.AttachmentLocation;
import gr.uoa.di.madgik.execution.plan.element.invocable.callback.CallbackManager;
import gr.uoa.di.madgik.execution.plan.element.invocable.callback.CallbackRegistryEntry;
import gr.uoa.di.madgik.execution.plan.element.variable.VariableCollection;
import gr.uoa.di.madgik.execution.utils.BoundaryIsolationInfo;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.PlanElementUtils;
import gr.uoa.di.madgik.execution.utils.ValueCollection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundaryHandler extends Thread implements Observer
{
	private static Logger logger=LoggerFactory.getLogger(BoundaryHandler.class);

	private Socket socket=null;
	private final Object synchCompletion=new Object();
	private NozzleHandler NzlHandler=null;

	private ExecutionHandle Handle=null;
	public boolean Completed=false;
	public boolean Successful=false;
	public boolean WeirdCompletion=false;
	public ExecutionException Error=null;

	private IPlanElement PlanToSend=null;
	private VariableCollection VarsToSend=null;
	private EnvHintCollection HintsToSend=null;
	private String ID=null;
	private String Name=null;
	private BoundaryConfig Config=null;
	private BoundaryIsolationInfo IsolationToSend=null;
	private PlanConfig PlanConfigToSend=null;
	private Set<ExecutionAttachment> Attachments=new HashSet<ExecutionAttachment>();
	
	public BoundaryHandler(Socket socket)
	{
		this.socket=socket;
		this.setName(BoundaryHandler.class.getName());
		this.setDaemon(true);
	}
	
	public BoundaryHandler(IPlanElement PlanToSend, VariableCollection VarsToSend, EnvHintCollection HintsToSend, ExecutionHandle Handle,String ID, String Name, BoundaryConfig Config,BoundaryIsolationInfo IsolationToSend,PlanConfig PlanConfigToSend,Set<ExecutionAttachment> Attachments)
	{
		this.PlanToSend=PlanToSend;
		this.VarsToSend=VarsToSend;
		this.HintsToSend=HintsToSend;
		this.Handle=Handle;
		this.ID=ID;
		this.Name=Name;
		this.Config =Config;
		this.IsolationToSend=IsolationToSend;
		this.PlanConfigToSend=PlanConfigToSend;
		this.Attachments=Attachments;
	}
	
	public void BoundarySideProtocol() throws Exception
	{
		if(this.socket==null || !this.isDaemon()) throw new ExecutionInternalErrorException("Handler not correctly initialized for engine side protocol");
		this.start();
	}
	
	public String EngineSideProtocol() throws ExecutionInternalErrorException, IOException, ExecutionRunTimeException, EnvironmentSerializationException, ExecutionSerializationException, Exception
	{
		if(this.PlanToSend==null || this.IsolationToSend==null || 
				this.PlanConfigToSend==null || this.VarsToSend==null || 
				this.Handle==null || this.ID==null || this.Name==null ||
				this.Config ==null) throw new ExecutionInternalErrorException("Handler not correctly initialized for engine side protocol");
		Socket clientSock = null;
		NozzleHandler Handler=null;
		CallbackRegistryEntry entryToCallback=null;
		DataOutputStream dout=null;
		DataInputStream din=null;
		
		//System.out.println("Engine side: \n" + Handle.GetPlan().Serialize()); //TODO remove
		try
		{
			if(this.PlanConfigToSend.ModeOfConnection==ConnectionMode.Callback)
			{
				entryToCallback=new CallbackRegistryEntry();
				entryToCallback.ID=UUID.randomUUID().toString();
				CallbackManager.RegisterCallback(entryToCallback);
			}
			
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID,"Contacting remote boundary element"));
			clientSock = new Socket(this.Config.HostName, this.Config.Port);
			logger.debug("Opening connection to " + this.Config.HostName + ":" + this.Config.Port);
			Handler=new NozzleHandler();
			IChannelLocator locator = Handler.CreateInletNozzle(Config.NozzleConfig, Handle);
			dout=new DataOutputStream(new BufferedOutputStream(clientSock.getOutputStream()));
			din=new DataInputStream(new BufferedInputStream(clientSock.getInputStream()));
			if(!Handle.GetPlan().Config.ChokeProgressReporting) Handle.EmitEvent(new ExecutionProgressReportStateEvent(ID, "Sending assembled info to remote boundary element"));
			dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.ExecutionEngine.toString());
			logger.debug("Sending locator : "+locator.ToURI());
			this.WriteArray(ZipUtils.ZipString(this.PlanToSend.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(this.VarsToSend.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(this.HintsToSend.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(this.PlanConfigToSend.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(this.IsolationToSend.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(locator.ToURI().toString()), dout);
			dout.writeInt(this.Attachments.size());
			for(ExecutionAttachment at : this.Attachments)
			{
				this.WriteArray(ZipUtils.ZipString(at.ToXML()), dout);
				if(at.LocationType==AttachmentLocation.LocalFile) at.WriteLocalToStream(dout);
			}
			if(this.PlanConfigToSend.ModeOfConnection==ConnectionMode.Callback)
			{
				logger.debug("Sending info on callback");
				dout.writeBoolean(true);
				dout.writeUTF(entryToCallback.ID);
				dout.writeUTF(TCPConnectionManager.GetConnectionManagerHostName());
				dout.writeInt(TCPConnectionManager.GetConnectionManagerPort());
			}
			else
			{
				dout.writeBoolean(false);
			}
			dout.flush();

			if(this.PlanConfigToSend.ModeOfConnection==ConnectionMode.Callback)
			{
				logger.debug("Closing original socket");
				dout.flush();
				dout.close();
				din.close();
				clientSock.close();
				entryToCallback.WaitForCallback(this.PlanConfigToSend.ConnectionCallbackTimeout);
				if(entryToCallback.Sock==null) throw new ExecutionRunTimeException("There was a timeout waiting for a callback from the remote boundary node");
				clientSock=entryToCallback.Sock;
				dout=new DataOutputStream(clientSock.getOutputStream());
				din=new DataInputStream(clientSock.getInputStream());
				logger.debug("Opened Callback socket");
			}

			this.Completed=din.readBoolean();
			this.Successful=din.readBoolean();
			this.WeirdCompletion=din.readBoolean();
			boolean excIncluded=din.readBoolean();
			if(excIncluded)
			{
				this.Error=ExceptionUtils.FromXML(ZipUtils.UnzipString(this.ReadArray(din)));
			}
			String ssIDsUpdate=ZipUtils.UnzipString(this.ReadArray(din));
			String varsUpdate=ZipUtils.UnzipString(this.ReadArray(din));
			dout.close();
			din.close();
			logger.debug("Received Updated variables : "+varsUpdate);
			if(varsUpdate==null || varsUpdate.trim().length()==0) throw new ExecutionInternalErrorException("Protocol error contacting remote boundary element");
			Handle.GetPlan().CleanUpSS.Merge(new ValueCollection(ssIDsUpdate));
			return varsUpdate;
		}catch(ExecutionInternalErrorException ex)
		{
			throw ex;
		}catch(UnknownHostException ex)
		{
			throw ex;
		}catch(ExecutionSerializationException ex)
		{
			throw ex;
		}catch(EnvironmentSerializationException ex)
		{
			throw ex;
		}catch(ExecutionRunTimeException ex)
		{
			throw ex;
		}catch(IOException ex)
		{
			throw ex;
		}catch(Exception ex)
		{
			throw ex;
		}
		finally
		{
			logger.debug("Cleaning up");
			if(entryToCallback!=null) try{CallbackManager.UnregisterCallback(entryToCallback);}catch(Exception ex){}
			try{if(dout!=null) dout.flush();}catch(Exception ex){}
			try{if(dout!=null) dout.close();}catch(Exception ex){}
			try{if(din!=null) din.close();}catch(Exception ex){}
			if(clientSock!=null) try{clientSock.close();}catch(Exception ex){}
			if(Handler!=null) try{Handler.Dispose();}catch(Exception ex){}
		}
	}
	
	@Override
	public void run()
	{
		this.NzlHandler=new NozzleHandler();
		boolean UseCallback=false;;
		String CallbackID=null;
		String CallbackHostName=null;
		int CallbackPort=0;
		Set<ExecutionAttachment> atts=new HashSet<ExecutionAttachment>();
		DataOutputStream dout=null;
		DataInputStream din=null;
		try
		{
			dout=new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
			din=new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));

			logger.debug("Receiving boundary plan info");	
			String RootXML=ZipUtils.UnzipString(this.ReadArray(din));
			String VariablesXML=ZipUtils.UnzipString(this.ReadArray(din));
			String EnvXML=ZipUtils.UnzipString(this.ReadArray(din));
			String ConfigXML=ZipUtils.UnzipString(this.ReadArray(din));
			String IsolationXML=ZipUtils.UnzipString(this.ReadArray(din));
			String LocatorSerialiazation=ZipUtils.UnzipString(this.ReadArray(din));
			int AttachmentsCount=din.readInt();
			for(int i=0;i<AttachmentsCount;i+=1)
			{
				ExecutionAttachment at = new ExecutionAttachment();
				at.FromXML(ZipUtils.UnzipString(this.ReadArray(din)));
				if(at.LocationType==AttachmentLocation.LocalFile) at.WriteLocalFromStream(din);
				atts.add(at);
			}
			
			UseCallback=din.readBoolean();
			if(UseCallback)
			{
				logger.debug("Reading callback info");
				CallbackID=din.readUTF();
				CallbackHostName=din.readUTF();
				CallbackPort=din.readInt();
				dout.flush();
				dout.close();
				din.close();
				this.socket.close();
			}
			ExecutionPlan Plan=new ExecutionPlan();
			BoundaryIsolationInfo isolatenfo=new BoundaryIsolationInfo();
			IChannelLocator loc=null;
			try
			{
				Plan.Root=PlanElementUtils.GetPlanElement(XMLUtils.Deserialize(RootXML).getDocumentElement());
				Plan.Variables=new VariableCollection(VariablesXML);
				Plan.EnvHints=new EnvHintCollection(EnvXML);
				Plan.Config=new PlanConfig(ConfigXML);
				isolatenfo.FromXML(IsolationXML);
				loc=ChannelLocatorFactory.GetLocator(new URI(LocatorSerialiazation));
			}catch(Exception ex)
			{
				logger.warn("Could not complete population of incoming info",ex);
				logger.warn("Plan: " + RootXML);
				if(UseCallback)
				{
					logger.debug("Opening callback socket to respond");
					this.socket = new Socket(CallbackHostName, CallbackPort);
					dout=new DataOutputStream(socket.getOutputStream());
					din=new DataInputStream(socket.getInputStream());
					dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.ExecutionEngineCallback.toString());
					dout.writeUTF(CallbackID);
					dout.flush();
				}
				dout.writeBoolean(true); //completed
				dout.writeBoolean(false); //not successful
				dout.writeBoolean(false); // not weird completion
				dout.writeBoolean(true); // error included
				this.WriteArray(ZipUtils.ZipString(ExceptionUtils.ToXML(ExceptionUtils.GetTransformedExecutionException(ex))), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.CleanUpSS.ToXML()), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.Variables.ToXML()), dout);
			}
			for(ExecutionAttachment at : atts)
			{
				if(at.LocationType==AttachmentLocation.StorageSystem) at.WriteLocalFromStorage(Plan.EnvHints);
			}
			
//			logger.debug("Received locator : "+loc.ToXML());
			this.NzlHandler.CreateOutletNozzle(loc);
//			logger.debug("Received sub-plan : "+Plan.Root.ToXML());
//			logger.debug("Received variables : "+Plan.Variables.ToXML());
//			logger.debug("Received isolation level : "+isolatenfo.ToXML());
			
			//System.out.println(Plan.Serialize()); //TODO remove
			try
			{
				this.Handle= ExecutionEngine.Submit(Plan);
			}catch(Exception ex)
			{
				logger.warn("Could not submit execution",ex);
				if(UseCallback)
				{
					logger.debug("Opening callback socket to respond");
					this.socket = new Socket(CallbackHostName, CallbackPort);
					dout=new DataOutputStream(socket.getOutputStream());
					din=new DataInputStream(socket.getInputStream());
					dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.ExecutionEngineCallback.toString());
					dout.writeUTF(CallbackID);
					dout.flush();
				}
				dout.writeBoolean(true); //completed
				dout.writeBoolean(false); //not successful
				dout.writeBoolean(false); // not weird completion
				dout.writeBoolean(true); // error included
				this.WriteArray(ZipUtils.ZipString(ExceptionUtils.ToXML(ExceptionUtils.GetTransformedExecutionException(ex))), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.CleanUpSS.ToXML()), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.Variables.ToXML()), dout);
			}
			
			try
			{
				this.Handle.SetIsolationInfo(isolatenfo);
				this.Handle.InitializeIsolation();
			}catch(Exception ex)
			{
				logger.warn("Could not initialize isolation",ex);
				if(UseCallback)
				{
					logger.debug("Opening callback socket to respond");
					this.socket = new Socket(CallbackHostName, CallbackPort);
					dout=new DataOutputStream(socket.getOutputStream());
					din=new DataInputStream(socket.getInputStream());
					dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.ExecutionEngineCallback.toString());
					dout.writeUTF(CallbackID);
					dout.flush();
				}
				dout.writeBoolean(true); //completed
				dout.writeBoolean(false); //not successful
				dout.writeBoolean(false); // not weird completion
				dout.writeBoolean(true); // error included
				this.WriteArray(ZipUtils.ZipString(ExceptionUtils.ToXML(ExceptionUtils.GetTransformedExecutionException(ex))), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.CleanUpSS.ToXML()), dout);
				this.WriteArray(ZipUtils.ZipString(Plan.Variables.ToXML()), dout);
			}

			for(ExecutionAttachment at : atts) at.MoveTmpToRestore(this.Handle);
			
			this.Handle.RegisterObserver(this);
			synchronized (this.synchCompletion)
			{
				ExecutionEngine.Execute(this.Handle);
				try
				{
					this.synchCompletion.wait();
				}catch(Exception ex){}
			}
			this.Completed=true;
			this.Successful=true;
			this.WeirdCompletion=false;
			if(!this.Handle.IsCompleted()) this.Completed=false; //this should not happen
			else if(this.Handle.IsCompletedWithSuccess()) this.Successful=true;
			else if(this.Handle.IsCompletedWithError()) 
			{
				this.Successful=false;
				this.Error = this.Handle.GetCompletionError();
			}
			else this.WeirdCompletion=true;
			if(UseCallback)
			{
				logger.debug("Opening callback socket to respond");
				this.socket = new Socket(CallbackHostName, CallbackPort);
				dout=new DataOutputStream(socket.getOutputStream());
				din=new DataInputStream(socket.getInputStream());
				dout.writeUTF(ITCPConnectionManagerEntry.NamedEntry.ExecutionEngineCallback.toString());
				dout.writeUTF(CallbackID);
				dout.flush();
			}
			dout.writeBoolean(this.Completed);
			dout.writeBoolean(this.Successful);
			dout.writeBoolean(this.WeirdCompletion);
			if(this.Error==null)dout.writeBoolean(false);
			else
			{
				dout.writeBoolean(true);
				this.WriteArray(ZipUtils.ZipString(ExceptionUtils.ToXML(this.Error)), dout);
			}
			this.WriteArray(ZipUtils.ZipString(Plan.CleanUpSS.ToXML()), dout);
			this.WriteArray(ZipUtils.ZipString(Plan.Variables.ToXML()), dout);
			dout.flush();
		}catch(Exception ex)
		{
			logger.warn("Could not complete boundary side handler protocol",ex);
		}
		finally
		{
			try{if(dout!=null) dout.flush();}catch(Exception ex){}
			try{if(dout!=null) dout.close();}catch(Exception ex){}
			try{if(din!=null) din.close();}catch(Exception ex){}
			if(this.socket!=null) try{this.socket.close();}catch(Exception exx){}
			if(this.NzlHandler!=null) try{this.NzlHandler.Dispose();}catch(Exception exx){}
			for(ExecutionAttachment at : atts)
			{
				if(at.tmpFile!=null)
				{
					try
					{
						FileUtils.CleanUp(at.tmpFile);
					}
					catch(Exception ex)
					{
						logger.warn("Couldn not cleanup tmp stored attachement "+at.tmpFile,ex);
					}
				}
				if(!at.CleanUpRestored) continue;
				try
				{
					if(at.RestoreLocationValue!=null)
					{
						File f=Handle.GetIsolatedFile(new File(at.RestoreLocationValue));
						FileUtils.CleanUp(f);
					}
					
				}
				catch(Exception ex)
				{
					logger.warn("Couldn not cleanup attachement "+at.RestoreLocationValue,ex);
				}
			}
			try
			{
				if(this.Handle!=null) this.Handle.FinalizeIsolation();
			}catch(Exception ex)
			{
				logger.warn("Could not finalize isolation",ex);
			}
			if(this.Handle!=null) try{this.Handle.Dispose();}catch(Exception exx){}
		}
	}
	
	private byte[] ReadArray(DataInputStream din) throws IOException
	{
		int length=din.readInt();
		byte[] buf=new byte[length];
		din.readFully(buf);
		return buf;
	}
	
	private void WriteArray(byte[] buf, DataOutputStream dout) throws IOException
	{
		dout.writeInt(buf.length);
		dout.write(buf);
	}

	public void update(Observable o, Object arg)
	{
		logger.debug("Caught event from local engine");
		if(o==null || arg==null) return;
		if(!o.getClass().getName().equals(arg.getClass().getName())) return;
		if(!(arg instanceof ExecutionStateEvent)) return;
		boolean emitEvent=true;
		logger.debug("Caught event is "+((ExecutionStateEvent)arg).GetEventName());
		switch(((ExecutionStateEvent)arg).GetEventName())
		{
			case ExecutionCompleted:
			{
				emitEvent=false;
				synchronized (this.synchCompletion)
				{
					this.synchCompletion.notify();
				}
				break;
			}
			case ExecutionCancel:
			case ExecutionPause:
			case ExecutionResume:
			case ExecutionStarted:
			{
				emitEvent=false;
				break;
			}
			case ExecutionPerformance:
			case ExecutionExternalProgress:
			case ExecutionProgress:
			{
				emitEvent=true;
				break;
			}
			default:
			{
				logger.warn("Received unrecognized event type "+((ExecutionStateEvent)arg).GetEventName().toString());
			}
		}
		logger.debug("Caught event from local engine can be emitted ("+emitEvent +") (" +(this.NzlHandler==null)+") = ("+(emitEvent && this.NzlHandler!=null)+")");
		if(emitEvent && this.NzlHandler!=null)
		{
			try
			{
				logger.debug("Emiting Caught event "+((ExecutionStateEvent)arg).GetEventName().toString());
				this.NzlHandler.Emitt(new ObjectPayloadChannelEvent(new NozzleEventPayload((ExecutionStateEvent)arg)));
			}catch(Exception ex)
			{
				logger.warn("Problem emitting event "+((ExecutionStateEvent)arg).GetEventName()+" back to engine",ex);
			}
		}
	}
}
