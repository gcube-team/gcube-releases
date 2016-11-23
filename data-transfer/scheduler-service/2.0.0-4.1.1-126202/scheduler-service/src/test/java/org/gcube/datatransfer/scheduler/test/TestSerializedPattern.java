package org.gcube.datatransfer.scheduler.test;

import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;

import com.thoughtworks.xstream.XStream;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestSerializedPattern {

	public static void main (String [] args){
		Pattern pattern = Patterns.calendar();
		System.out.println("before: '"+pattern.toString()+"'");
		
		XStream xstreamC = new XStream();
		String patternString = 	xstreamC.toXML(pattern);
		
		Pattern desPattern = (Pattern)xstreamC.fromXML(patternString);
		System.out.println("after: '"+desPattern.toString()+"'");
	}
}
