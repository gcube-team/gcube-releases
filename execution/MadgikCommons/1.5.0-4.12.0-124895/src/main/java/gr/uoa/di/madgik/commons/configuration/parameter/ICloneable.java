package gr.uoa.di.madgik.commons.configuration.parameter;

/**
 * Marking interface to show that the object implementing it can be cloned. This is needed in cases of
 * {@link ObjectParameter} parameters where the {@link ObjectParameter#IsShared()} has been set to <code>false</code>
 *
 * @author gpapanikos
 */
public interface ICloneable extends Cloneable
{

	/**
	 * Clones the implementing instnace
	 * 
	 * @return the new, cloned instnace
	 */
	public Object Clone();
}
