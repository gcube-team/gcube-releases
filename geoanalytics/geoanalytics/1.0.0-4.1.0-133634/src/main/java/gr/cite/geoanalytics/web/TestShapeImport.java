package gr.cite.geoanalytics.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/importTest")
public class TestShapeImport extends HttpServlet {

	private static final long serialVersionUID = 4622262028093269516L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// LayerFromShapeFileTest.importLayer("C:\\Users\\diljin\\Documents\\geopolis\\data\\Laconia\\kallikratis_16.shp",
			// 2100, "windows-1253", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
