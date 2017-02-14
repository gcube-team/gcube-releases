package gr.uoa.di.madgik.execution.plan.element.invocable.simple;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.exception.ExecutionBreakException;
import gr.uoa.di.madgik.execution.exception.ExecutionCancelException;
import gr.uoa.di.madgik.execution.exception.ExecutionInternalErrorException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.CallBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.ExecutionContextConfigBase;
import gr.uoa.di.madgik.execution.plan.element.invocable.IExecutionContextEnabled;
import gr.uoa.di.madgik.execution.utils.ExceptionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassReflectionWrapper
{
	private static Logger logger=LoggerFactory.getLogger(ClassReflectionWrapper.class);

	private String ClassName=null;
	private Class<?> TheClass=null;
	private ExecutionHandle Handle=null;
	private Object TheInstance=null;
	private String ID=null;
	private boolean SupportsExecutionContext=false;
	private ExecutionContextConfigBase SuppliedContextProxy;
	
	public ClassReflectionWrapper(String ClassName, ExecutionHandle Handle,String ID, boolean SupportsExecutionContext,ExecutionContextConfigBase SuppliedContextProxy) throws ClassNotFoundException
	{
		this.ClassName=ClassName;
		this.Handle=Handle;
		this.ID=ID;
		this.SupportsExecutionContext=SupportsExecutionContext;
		this.SuppliedContextProxy=SuppliedContextProxy;
		this.TheClass= Class.forName(this.ClassName);
	}

	public void Instantiate(CallBase constructorCall) throws ExecutionRunTimeException, ExecutionValidationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,InvocationTargetException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		if(constructorCall==null) return; //static invocations only
		else
		{
			if(!constructorCall.MethodName.equals(this.ClassName)) throw new ExecutionValidationException("Provided constructor call is not constructor call");
			constructorCall.EvaluateArguments(Handle);
			Constructor<?> con =this.TheClass.getConstructor(constructorCall.GetArgumentTypeList(this.Handle));
			try
			{
				this.TheInstance=con.newInstance(constructorCall.GetArgumentValueList());
			} catch (InvocationTargetException ex)
			{
				if (ex.getCause()!=null) ExceptionUtils.ThrowTransformedException(ex.getCause());
				else throw ex;
			}
			if((this.TheInstance instanceof IExecutionContextEnabled) && this.SupportsExecutionContext && this.SuppliedContextProxy!=null && (this.SuppliedContextProxy instanceof SimpleExecutionContextConfig))
			{
				((IExecutionContextEnabled)this.TheInstance).SetExecutionContext(new SimpleExecutionContext(Handle, ID, this.TheInstance.getClass().getName(),(SimpleExecutionContextConfig)this.SuppliedContextProxy));
			}
		}
	}
	
	public void Invoke(CallBase methodCall) throws SecurityException, NoSuchMethodException, ExecutionValidationException, IllegalArgumentException, IllegalAccessException, ExecutionRunTimeException, InvocationTargetException, ExecutionInternalErrorException, ExecutionCancelException, ExecutionBreakException
	{
		methodCall.EvaluateArguments(Handle);
		Method meth= this.TheClass.getMethod(methodCall.MethodName,methodCall.GetArgumentTypeList(this.Handle));
		if(meth==null) throw new ExecutionValidationException("{Provided method not found");
		Object UseInstnace=this.TheInstance;
		if(Modifier.isStatic(meth.getModifiers())) UseInstnace=null;
		Object ret=null;
		try
		{
			ret = meth.invoke(UseInstnace, methodCall.GetArgumentValueList());
		} catch (InvocationTargetException ex)
		{
			if (ex.getCause()!=null) ExceptionUtils.ThrowTransformedException(ex.getCause());
			else throw ex;
		}
		logger.debug("Result of invocation was "+ret);
		if(methodCall.OutputParameter!=null) methodCall.OutputParameter.SetParameterValue(Handle, ret);
	}
}
