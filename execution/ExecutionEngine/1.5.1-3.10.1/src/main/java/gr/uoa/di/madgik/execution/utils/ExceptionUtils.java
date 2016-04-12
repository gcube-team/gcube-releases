package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionEngineFullException;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExceptionUtils
{
	public static ExecutionException GetTransformedExecutionException(Exception ex)
	{
		if(ex instanceof ExecutionException) return (ExecutionException) ex;
		ExecutionRunTimeException rt = new ExecutionRunTimeException(ex.getMessage(),ex);
		rt.SetCause(ex);
		return rt;
	}
	
	public static void ThrowTransformedRunTimeException(Exception ex) throws ExecutionRunTimeException
	{
		//The usage of this method is discouraged outside the context of the engine because it hides exceptions that the engine depends on
		if (ex instanceof ExecutionRunTimeException) throw (ExecutionRunTimeException) ex;
		else
		{
			ExecutionRunTimeException rt = new ExecutionRunTimeException(ex.getMessage(),ex);
			rt.SetCause(ex);
			throw rt;
		}
	}
	
	public static void ThrowTransformedException(Exception ex) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		if (ex instanceof ExecutionRunTimeException) throw (ExecutionRunTimeException) ex;
		else if (ex instanceof ExecutionInternalErrorException) throw (ExecutionInternalErrorException) ex;
		else if (ex instanceof ExecutionCancelException) throw (ExecutionCancelException) ex;
		else if (ex instanceof ExecutionBreakException) throw (ExecutionBreakException) ex;
		else
		{
			ExecutionRunTimeException rt = new ExecutionRunTimeException(ex.getMessage(),ex);
			rt.SetCause(ex);
			throw rt;
		}
	}
	
	public static void ThrowTransformedException(Throwable ex) throws ExecutionRunTimeException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		if (ex instanceof ExecutionRunTimeException) throw (ExecutionRunTimeException) ex;
		else if (ex instanceof ExecutionInternalErrorException) throw (ExecutionInternalErrorException) ex;
		else if (ex instanceof ExecutionCancelException) throw (ExecutionCancelException) ex;
		else if (ex instanceof ExecutionBreakException) throw (ExecutionBreakException) ex;
		else
		{
			ExecutionRunTimeException rt = new ExecutionRunTimeException(ex.getMessage(),ex);
			rt.SetCause(ex);
			throw rt;
		}
	}

	public static void ThrowTransformedException(String CauseFullName,String CauseSimpleName, String Message) throws ExecutionRunTimeException
	{
		ExecutionRunTimeException rt = new ExecutionRunTimeException(Message);
		rt.SetCauseFullName(CauseFullName);
		rt.SetCauseSimpleName(CauseSimpleName);
		throw rt;
	}
	
	public static String ToXML(ExecutionException ex) throws ExecutionSerializationException
	{
		if(ex==null) throw new ExecutionSerializationException("Cannot serialize null exception");
		StringBuilder buf=new StringBuilder();
		buf.append("<execException type=\""+ex.getClass().getName()+"\">");
		if(ex.getMessage()!=null)buf.append("<message><![CDATA["+ex.getMessage()+"]]></message>");
		StringWriter w=new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		String trace=w.toString();
		if(trace!=null && trace.trim().length()!=0) buf.append("<trace><![CDATA["+trace+"]]></trace>");
		if(ex instanceof ExecutionRunTimeException)
		{
			if(((ExecutionRunTimeException)ex).GetCauseFullName()!=null) buf.append("<causeFullName>"+((ExecutionRunTimeException)ex).GetCauseFullName()+"</causeFullName>");
			if(((ExecutionRunTimeException)ex).GetCauseSimpleName()!=null) buf.append("<causeSimpleName>"+((ExecutionRunTimeException)ex).GetCauseSimpleName()+"</causeSimpleName>");
		}
		buf.append("</execException>");
		return buf.toString();
	}
	
	public static ExecutionException FromXML(String XML) throws ExecutionSerializationException
	{
		try
		{
			String msg=null;
			String trace=null;
			String causeFullName=null;
			String causeSimpleName=null;
			Document doc = XMLUtils.Deserialize(XML);
			if(!XMLUtils.AttributeExists(doc.getDocumentElement(), "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			String excType=XMLUtils.GetAttribute(doc.getDocumentElement(), "type");
			Element msgElem=XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "message");
			if(msgElem!=null) msg=XMLUtils.GetChildCDataText(msgElem);
			Element traceElem=XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "trace");
			if(traceElem!=null) trace=XMLUtils.GetChildCDataText(traceElem);
			if(excType.equals(ExecutionRunTimeException.class.getName()))
			{
				Element causeFullElem=XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "causeFullName");
				if(causeFullElem!=null) causeFullName=XMLUtils.GetChildText(causeFullElem);
				Element causeSimpleElem=XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "causeSimpleName");
				if(causeSimpleElem!=null) causeSimpleName=XMLUtils.GetChildText(causeSimpleElem);
			}
			ExecutionException o = ExceptionUtils.GetExecutionExceptionInstnace(excType, msg, trace);
			if(o instanceof ExecutionRunTimeException)
			{
				((ExecutionRunTimeException)o).SetCauseFullName(causeFullName);
				((ExecutionRunTimeException)o).SetCauseSimpleName(causeSimpleName);
			}
			return o;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
	
	private static ExecutionException GetExecutionExceptionInstnace(String ExcecutionExceptionName, String message, String stacktrace) throws ExecutionSerializationException
	{
		if(ExcecutionExceptionName.equals(ExecutionException.class.getName()))
		{
			return new ExecutionException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionRunTimeException.class.getName()))
		{
			return new ExecutionRunTimeException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionSerializationException.class.getName()))
		{
			return new ExecutionSerializationException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionValidationException.class.getName()))
		{
			return new ExecutionValidationException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionEngineFullException.class.getName()))
		{
			return new ExecutionEngineFullException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionCancelException.class.getName()))
		{
			return new ExecutionCancelException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionInternalErrorException.class.getName()))
		{
			return new ExecutionInternalErrorException(message,new Exception(stacktrace));
		}
		else if(ExcecutionExceptionName.equals(ExecutionBreakException.class.getName()))
		{
			return new ExecutionBreakException(message,new Exception(stacktrace));
		}
		else
		{
			throw new ExecutionSerializationException("unrecognized execution exception type "+ExcecutionExceptionName);
		}
	}
}
