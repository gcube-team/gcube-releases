package org.gcube.application.framework.search.library.util;

public class DisableButtons {
	boolean back;
	boolean forward;

	/**
	 * The generic constructor
	 */
	public DisableButtons()
	{
		back = false;
		forward = false;
	}
	/**
	 * @return whether the back button must be disabled or not! (true: disabled) 
	 */
	public boolean getBack()
	{
		return back;
	}
	
	/**
	 * @return  whether the forward button must be disabled or not! (true: disabled)
	 */
	public boolean getForward()
	{
		return forward;
	}
	
	/**
	 * 
	 * @param b disable back or not (true: disabled)
	 */
	public void setBack(boolean b)
	{
		back = b;
	}
	
	/**
	 * @param f disable forward or not (true: disabled)
	 */
	public void setForward(boolean f)
	{
		forward = f;
	}

}
