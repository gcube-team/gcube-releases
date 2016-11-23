package gr.uoa.di.madgik.visualisations.PieChart2.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;


public class PieChart2 extends AbsolutePanel implements EntryPoint{


	public void onModuleLoad() {
		this.setSize("500", "500");
		alert("Running PieChart2 component's onModuleLoad");
		
		pieChart2("aaa", "500", "500", "100", "Author", "");
		
		
	}
	
	
	public static native void alert(String msg) /*-{
	  $wnd.alert(msg);
	}-*/;

	
	public static native void pieChart2(String divID, String width, String height, String radius, String title, String dataJSON)/*-{
		  $wnd.$("#"+divID).html("");
		  
//		  $wnd.$("#"+divID).show();
		  
		  $wnd.$("#"+divID).css("width", width);
		  $wnd.$("#"+divID).css("height", height);
		  $wnd.$("#"+divID).css("margin", "0 auto");
		  
		    var r = $wnd.Raphael(divID),
		        pie = r.piechart(width/2, height/2, radius, [55, 20, 13, 32, 5, 1, 2, 10], { legend: ["a","b","c","d","e","f","g","h"]});
			
			
			if(title != "")
		    	r.text(320, 100, title).attr({ font: "20px sans-serif" });
		    	
		    pie.hover(function () {
		        this.sector.stop();
		        this.sector.scale(1.1, 1.1, this.cx, this.cy);
		
		        if (this.label) {
		            this.label[0].stop();
		            this.label[0].attr({ r: 7.5 });
		            this.label[1].attr({ "font-weight": 800 });
		        }
		    }, function () {
		        this.sector.animate({ transform: 's1 1 ' + this.cx + ' ' + this.cy }, 500, "bounce");
		
		        if (this.label) {
		            this.label[0].animate({ r: 5 }, 500, "bounce");
		            this.label[1].attr({ "font-weight": 400 });
		        }
		    });
		    
		    
			
	}-*/;


}