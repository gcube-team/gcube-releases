package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadBufferedWriter extends Thread
{
	private static Logger logger=LoggerFactory.getLogger(ThreadBufferedWriter.class);
	private OutputStream stream=null;
	private IInputParameter StdInParameter=null;
	private ExecutionHandle Handle=null;
	private boolean StdInIsFile=false;
	private Object lockstd=null;
	public boolean Done=false;
	
	public ThreadBufferedWriter(OutputStream stream,IInputParameter StdInParameter,boolean StdInIsFile, ExecutionHandle Handle,Object lockMe)
	{
		this.stream=stream;
		this.StdInParameter=StdInParameter;
		this.Handle=Handle;
		this.StdInIsFile=StdInIsFile;
		this.lockstd=lockMe;
	}
	
	public void Do()
	{
		this.setName(ThreadBufferedWriter.class.getName());
		this.setDaemon(true);
		this.start();
	}

	public void run()
	{
		logger.debug("Starting writer");
		try
		{
			if(StdInParameter!=null)
			{
				if(!this.StdInIsFile)
				{
					String StdIn= DataTypeUtils.GetValueAsString(StdInParameter.GetParameterValue(Handle));
					BufferedWriter w=new BufferedWriter(new OutputStreamWriter(stream));
					w.write(StdIn);
					w.close();
				}
				else
				{
					String StdInFile= DataTypeUtils.GetValueAsString(StdInParameter.GetParameterValue(Handle));
					File inf=new File(StdInFile);
					if(!inf.exists() || inf.isDirectory()) throw new ExecutionRunTimeException("Input file "+StdInFile+" not available or is directory");
					FileUtils.Copy(inf, stream);
				}
			}
			else this.stream.close();
		}catch(Exception ex)
		{
			logger.warn("Could not write in the background",ex);
		}
		finally
		{
			logger.debug("Notifying from writer");
			this.Done=true;
			synchronized(this.lockstd)
			{
				this.lockstd.notify();
			}
			logger.debug("Notified");
		}
	}
}
