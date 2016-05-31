package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.filter.ParameterFilterBase;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ExecutionContextUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSSOAPSimpleProxy implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(WSSOAPSimpleProxy.class);

	public Object Invoke(URL ServiceEndPoint,WSSOAPCall methodCall, boolean SupportsExecutionContext, IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle) throws ExecutionValidationException,ExecutionRunTimeException, ExecutionSerializationException
	{
		String envelop=this.GetEnvelop(ServiceEndPoint,methodCall,SupportsExecutionContext,ContextChannel, ContextID,ContextConfig,Handle);
		String ret=this.CallWebService(ServiceEndPoint, envelop,methodCall);
		return ret;
	}
	
	private String GetEnvelop(URL ServiceEndPoint,WSSOAPCall methodCall, boolean SupportsExecutionContext , IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle) throws ExecutionValidationException, ExecutionSerializationException, ExecutionRunTimeException
	{
		Object []args=methodCall.GetArgumentValueList();
		if(args.length!=1) throw new ExecutionValidationException("Exactly one argument is expected");
		String s = DataTypeUtils.GetValueAsString(args[0]);
		Set<NamedDataType> additionalValueProviders=new HashSet<NamedDataType>();
		if(SupportsExecutionContext)
		{
			if(methodCall.ExecutionContextToken==null) throw new ExecutionValidationException("Requested execution context but no token provided");
			String engineHeader=ExecutionContextUtils.GenerateExecutionEngineSoapHeaderElement(ContextChannel, ContextID,ServiceEndPoint.toString()+"("+methodCall.MethodName+")" , ContextConfig);
			NamedDataType ndt=new NamedDataType();
			ndt.IsAvailable=true;
			ndt.Name="ContextHeader";
			ndt.Token=methodCall.ExecutionContextToken; //"[ExecutionEngineContext]";
			ndt.Value=new DataTypeString();
			((DataTypeString)ndt.Value).SetValue(engineHeader);
			additionalValueProviders.add(ndt);
		}
		Collections.sort(methodCall.PostCreationFilters);
		Object processed=s;
		for(ParameterFilterBase filter : methodCall.PostCreationFilters)
		{
			processed=filter.ProcessOnLine(processed,additionalValueProviders,Handle);
		}
		return DataTypeUtils.GetValueAsString(processed);
	}

	private String CallWebService(URL ServiceEndPoint,String envelop,WSSOAPCall methodCall) throws ExecutionRunTimeException
	{
		try
		{
			logger.trace("SOAP envelope: " + envelop);
			URLConnection connection = ServiceEndPoint.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
	
			byte[] b = envelop.getBytes("UTF-8");

			httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			//httpConn.setRequestProperty("Content-Type", "application/soap+xml; charset=UTF-8; action=\""+methodCall.ActionURN+"\"");
			httpConn.setRequestProperty("SOAPAction", "\""+methodCall.ActionURN+"\"");
			httpConn.setRequestProperty("Connection", "close");
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
	
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();
	
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			
			if(httpConn.getResponseCode()!=HttpURLConnection.HTTP_OK) throw new ExecutionRunTimeException("Invocation returned with response code "+httpConn.getResponseCode()+" and message: "+httpConn.getResponseMessage());
	
			StringBuilder buf = new StringBuilder();
			String inputLine;
	
			while ((inputLine = in.readLine()) != null)
			{
				buf.append(inputLine);
			}
			in.close();
			return buf.toString();
		}
		catch(Exception ex)
		{
			ExceptionUtils.ThrowTransformedRunTimeException(ex);
			return null; //so that the compiler thinks that a value is always returned
		}
	}
}
