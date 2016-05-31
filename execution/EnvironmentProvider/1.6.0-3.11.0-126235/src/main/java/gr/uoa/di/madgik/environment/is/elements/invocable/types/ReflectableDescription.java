package gr.uoa.di.madgik.environment.is.elements.invocable.types;

import java.util.HashSet;
import java.util.Set;

public class ReflectableDescription implements IReflectableDescription
{
	private static final long serialVersionUID = -6855010193102679334L;
	public String Type=null;
	public Set<ReflectableDescriptionItem> Items=new HashSet<ReflectableDescriptionItem>();
}
