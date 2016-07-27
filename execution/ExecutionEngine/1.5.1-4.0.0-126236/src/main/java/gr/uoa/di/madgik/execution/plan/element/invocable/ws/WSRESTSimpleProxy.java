package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import gr.uoa.di.madgik.execution.utils.ExecutionContextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WSRESTSimpleProxy implements Serializable
{
	private static final long serialVersionUID = 1L;

	public Object Invoke(URL ServiceEndPoint, String path, String resourceID, WSRESTCall methodCall, boolean SupportsExecutionContext, IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle, String scope) throws ExecutionValidationException,ExecutionRunTimeException, ExecutionSerializationException
	{
		URL endPoint=this.BuildEndPoint(ServiceEndPoint, path, resourceID, methodCall, SupportsExecutionContext, ContextChannel, ContextID, ContextConfig, Handle);
		
		byte[] postData = null;
		
			if (methodCall.MethodName.equalsIgnoreCase("POST")){
				try {
					postData = GetPostData(ServiceEndPoint, path, resourceID, methodCall, SupportsExecutionContext, ContextChannel, ContextID, ContextConfig, Handle, scope);
				} catch (UnsupportedEncodingException e) {
				}
			}
		
		String ret=this.CallRestService(endPoint, methodCall,Handle, scope, postData);
		return ret;
	}
	
	private String CallRestService(URL ServiceEndPoint,WSRESTCall methodCall,ExecutionHandle Handle, String scope, byte[] postData) throws ExecutionRunTimeException
	{
		try
		{
			URLConnection connection = ServiceEndPoint.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			
	
			
			httpConn.setRequestProperty("gcube-scope", scope);
			httpConn.setRequestMethod(methodCall.MethodName);
			
			if (methodCall.MethodName.equalsIgnoreCase("GET"))
				httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			else
				httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			
			httpConn.setRequestProperty("Connection", "close");
			httpConn.setDoInput(true);

//			byte[] b=this.GetPostContent(methodCall, Handle).getBytes("UTF-8");
//			if (b.length > 0) {
//				httpConn.setDoOutput(true);
//				OutputStream out = httpConn.getOutputStream();
//				out.write(b);
//				out.close();
//			}
			
			if (postData != null && postData.length > 0){
				httpConn.setRequestProperty("Content-Length", String.valueOf(postData.length));
				
				httpConn.setDoOutput(true);
				OutputStream out = httpConn.getOutputStream();
				out.write(postData);
				out.close();
			}
			
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
	
	private byte[] GetPostData(URL ServiceEndPoint, String path, String resourceID, WSRESTCall methodCall, boolean SupportsExecutionContext, IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle, String scope) throws ExecutionRunTimeException, ExecutionValidationException, UnsupportedEncodingException{
		List<String> params=this.GetQueryString(ServiceEndPoint, methodCall, SupportsExecutionContext, ContextChannel, ContextID, ContextConfig, Handle);
		
		StringBuilder buf = new StringBuilder();
		for(String par :params)
		{
			buf.append(par);
			buf.append("&");
		}
		if(buf.charAt(buf.length()-1)=='&') buf.deleteCharAt(buf.length()-1);
		
		
		byte[] postDataBytes = buf.toString().getBytes("UTF-8");
		
		return postDataBytes;
	}
	
	private String GetPostContent(WSRESTCall methodCall,ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		StringBuilder buf=new StringBuilder();
		for(ArgumentBase arg : methodCall.ArgumentList)
		{
			if(arg.ArgumentName!=null && arg.ArgumentName.trim().length()>=0) continue;
			buf.append(arg.Parameter.GetParameterValue(Handle).toString());
		}
		return buf.toString();
	}
	
	private URL BuildEndPoint(URL ServiceEndPoint, String path, String resourceID, WSRESTCall methodCall, boolean SupportsExecutionContext, IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		
		StringBuilder buf=new StringBuilder(ServiceEndPoint.toString().trim());
		
		if (!buf.toString().endsWith("/"))
			buf.append("/");
		buf.append(resourceID);
		if (!buf.toString().endsWith("/"))
			buf.append("/");
		buf.append(path);
	
		
		if (methodCall.MethodName.equalsIgnoreCase("POST")){
			
		} else {
			ArrayList<String> params=this.GetQueryString(ServiceEndPoint, methodCall, SupportsExecutionContext, ContextChannel, ContextID, ContextConfig, Handle);
			if(buf.indexOf("?")<0) buf.append("?");
			if(buf.charAt(buf.length()-1)!='&') buf.append("&");
			for(String par :params)
			{
				buf.append(par);
				buf.append("&");
			}
			if(buf.charAt(buf.length()-1)=='&') buf.deleteCharAt(buf.length()-1);
		}
		try
		{
			return new URL(buf.toString());
		} catch (MalformedURLException e)
		{
			throw new ExecutionValidationException("Could not create service end point", e);
		}
	}
	
	private ArrayList<String> GetQueryString(URL ServiceEndPoint,WSRESTCall call,boolean SupportsExecutionContext, IChannelLocator ContextChannel, String ContextID,WSExecutionContextConfig ContextConfig,ExecutionHandle Handle) throws ExecutionRunTimeException, ExecutionValidationException
	{
		Collections.sort(call.ArgumentList);
		ArrayList<String> params=new ArrayList<String>();
		for(ArgumentBase arg : call.ArgumentList)
		{
			if(arg.ArgumentName==null || arg.ArgumentName.trim().length()==0) continue;
			try {
				if(arg.Parameter.GetParameterValue(Handle).getClass().isArray()) {
					for (Object obj : (Object[]) arg.Parameter.GetParameterValue(Handle)) {
						params.add(URLEncoder.encode(arg.ArgumentName,"UTF-8")+"="+URLEncoder.encode(obj.toString(),"UTF-8"));
					}
				}
				else
					params.add(URLEncoder.encode(arg.ArgumentName,"UTF-8")+"="+URLEncoder.encode(arg.Parameter.GetParameterValue(Handle).toString(),"UTF-8"));
			} catch (UnsupportedEncodingException e)
			{
				throw new ExecutionRunTimeException("Could not encode for url usage parameter",e);
			}
		}
		if(SupportsExecutionContext)
		{
			if(call.ExecutionContextToken==null) throw new ExecutionValidationException("Requested execution context but no token provided");
			try
			{
				String engineHeader=ExecutionContextUtils.GenerateExecutionEngineSoapHeaderElement(ContextChannel, ContextID,ServiceEndPoint.toString()+"("+call.MethodName+")" , ContextConfig);
				StringBuilder buf=new StringBuilder();
				buf.append(URLEncoder.encode(call.ExecutionContextToken,"UTF-8"));
				buf.append("=");
				buf.append(URLEncoder.encode(engineHeader,"UTF-8"));
				params.add(buf.toString());
			} catch (ExecutionSerializationException e)
			{
				throw new ExecutionRunTimeException("Could not generate execution context header", e);
			} catch (UnsupportedEncodingException e)
			{
				throw new ExecutionRunTimeException("Could not encode for url usage parameter",e);
			}
		}
		return params;
	}
}
