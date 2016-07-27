package gr.uoa.di.madgik.workflow.plan.element.hook;

import gr.uoa.di.madgik.workflow.exception.WorkflowInternalErrorException;
import java.util.HashMap;

public class HookCollection
{
	private HashMap<String,IElementHook> Hooks=new HashMap<String, IElementHook>();
	
	private String GetHookKey(IElementHook.Direction direction, IElementHook.Type type, IElementHook.SubType subType, String key)
	{
		return direction.toString()+"#"+type.toString()+"#"+subType.toString()+"#"+key;
	}
	
	public void Add(IElementHook Hook) throws WorkflowInternalErrorException
	{
		String Key=this.GetHookKey(Hook.GetDirection(),Hook.GetType(),Hook.GetSubType(),Hook.GetKey());
		this.Hooks.put(Key, Hook);
	}
	
	public IElementHook GetHookOf(IElementHook.Direction direction, IElementHook.Type type, IElementHook.SubType subType, String key)
	{
		return Hooks.get(this.GetHookKey(direction, type, subType, key));
	}
	
	public boolean ContainsHook(IElementHook.Direction direction, IElementHook.Type type, IElementHook.SubType subType, String key)
	{
		return (this.GetHookOf(direction, type, subType, key)!=null);
	}
}
