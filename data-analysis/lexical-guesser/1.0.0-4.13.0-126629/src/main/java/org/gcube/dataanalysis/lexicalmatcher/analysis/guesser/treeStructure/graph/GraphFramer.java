package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.graph;

import java.awt.Event;
import java.awt.Frame;

public class GraphFramer extends Frame{
	
	public GraphDisplayer graphDisplayer;
	
	public GraphFramer(String frameName){
		super(frameName);
		graphDisplayer = new GraphDisplayer();
		add("Center",graphDisplayer);
		
	} 
	
	public void go(){
		
		graphDisplayer.init();
		
		this.resize(GraphDisplayer.WIDTHBOX, GraphDisplayer.HEIGHTBOX);
		this.show();
		graphDisplayer.start();
		
	}
	
	public boolean HandleEvent(Event event){
		
		if (event.id == Event.WINDOW_DESTROY)
		
			{
			try
			{graphDisplayer.stop();
			graphDisplayer.destroy();
			}catch(Exception e){e.printStackTrace();}
			System.exit(0);
			}
		return false;
	}
}
