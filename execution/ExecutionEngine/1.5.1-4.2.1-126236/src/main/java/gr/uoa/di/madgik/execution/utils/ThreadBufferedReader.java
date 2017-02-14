package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterFilterBase;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadBufferedReader extends Thread
{
	private static Logger logger=LoggerFactory.getLogger(ThreadBufferedReader.class);
	private boolean IsFile=false;
	private InputStream stream;
	private String FileName;
	private ExecutionHandle Handle;
	public String Output=null;
	private Object lockstd=null;
	public Boolean Done=false;
	public ParameterFilterBase Filter=null;

	public ThreadBufferedReader(InputStream stream,boolean IsFile,String FileName,ExecutionHandle Handle,Object lockMe,ParameterFilterBase Filter)
	{
		this.IsFile=IsFile;
		this.stream=stream;
		this.FileName=FileName;
		this.Handle=Handle;
		this.lockstd=lockMe;
		this.Filter=Filter;
	}
	
	public void Do()
	{
		this.setName(ThreadBufferedWriter.class.getName());
		this.setDaemon(true);
		this.start();
	}

	public void run()
	{
		logger.debug("Starting reader "+FileName);
		try
		{
			if(!IsFile)
			{
				BufferedReader din = new BufferedReader(new InputStreamReader(stream));
				String line = null;
				StringBuilder buf = new StringBuilder();
				boolean firstline = true;
				while ((line = din.readLine()) != null)
				{
					if (!firstline)
					{
						buf.append("\n");
					}
					firstline = false;
					buf.append(line);
				}
				this.Output= buf.toString();
				if(this.Filter!=null)
				{
					this.Filter.ProcessOnLine(this.Output, null,Handle);
				}
			}
			else
			{
				if(FileName==null) throw new ExecutionRunTimeException("File name not provided to store process result");
				File outputFile=Handle.GetIsolatedFile(new File(FileName));
				if(this.Filter!=null) 
				{
					NamedDataType ndt=new NamedDataType();
					ndt.IsAvailable=true;
					ndt.Name=UUID.randomUUID().toString();
					ndt.Token=ndt.Name;
					ndt.Value=new DataTypeString();
					ndt.Value.SetValue(outputFile.toString());
					HashSet<NamedDataType> ndts=new HashSet<NamedDataType>();
					ndts.add(ndt);
					this.Filter.ProcessOnLine(stream, ndts,Handle);
				}
				else
				{
					FileUtils.Copy(stream, outputFile);
				}
				this.Output=FileName;
			}
		}catch(Exception ex)
		{
			logger.warn("Could not write in the background",ex);
		}
		finally
		{
			try{this.stream.close();}catch(Exception ex){}
			logger.debug("Notifying reader "+FileName);
			this.Done=true;
			synchronized(this.lockstd)
			{
				this.lockstd.notify();
			}
			logger.debug("Notified "+FileName);
		}
	}
}
