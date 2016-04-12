package org.gcube.portlets.user.gisviewer.test.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;

public class GridExample extends ContentPanel {

	public GridExample() {
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Employee Name");
		column.setWidth(200);
		configs.add(column);

		column = new ColumnConfig("department", "Department", 150);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig("designation", "Designation", 150);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig("salary", "Slary", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		final NumberFormat number = NumberFormat.getFormat("0.00");
		GridCellRenderer<Employee> checkSalary = new GridCellRenderer<Employee>() {
			public String render(Employee model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Employee> employeeList, Grid<Employee> grid) {
				double val = (Double) model.get(property);
				String style = val < 70000 ? "red" : "green";
				return "<span style='color:" + style + "'>"
						+ number.format(val) + "</span>";
			}
		};
		column.setRenderer(checkSalary);
		configs.add(column);

		column = new ColumnConfig("joiningdate", "Joining Date", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
		configs.add(column);
		
	    ListStore<Employee> employeeList = new ListStore<Employee>();    
	    employeeList.add(new TestData().getEmployees());
	    
	    ColumnModel cm = new ColumnModel(configs);  
	    Grid<Employee> grid = new Grid<Employee>(employeeList, cm);   
	    grid.setStyleAttribute("borderTop", "none");   
	    grid.setAutoExpandColumn("name");   
	    grid.setBorders(true);   
	    grid.setStripeRows(true);
	    grid.setColumnLines(true);
	    
	    setBodyBorder(true);    
	    setHeading("Employee List");    
	    setButtonAlign(HorizontalAlignment.CENTER);    
	    setLayout(new FitLayout());    
	    setSize(700, 500);   
	    add(grid);
	}
	
	public Grid<Employee> getGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Employee Name");
		column.setWidth(200);
		configs.add(column);

		column = new ColumnConfig("department", "Department", 150);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig("designation", "Designation", 150);
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig("salary", "Slary", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		final NumberFormat number = NumberFormat.getFormat("0.00");
		GridCellRenderer<Employee> checkSalary = new GridCellRenderer<Employee>() {
			public String render(Employee model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<Employee> employeeList, Grid<Employee> grid) {
				double val = (Double) model.get(property);
				String style = val < 70000 ? "red" : "green";
				return "<span style='color:" + style + "'>"
						+ number.format(val) + "</span>";
			}
		};
		column.setRenderer(checkSalary);
		configs.add(column);

		column = new ColumnConfig("joiningdate", "Joining Date", 100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setDateTimeFormat(DateTimeFormat.getShortDateFormat());
		configs.add(column);
		
	    ListStore<Employee> employeeList = new ListStore<Employee>();    
	    employeeList.add(getEmployees());
	    
	    ColumnModel cm = new ColumnModel(configs);  
	    Grid<Employee> grid = new Grid<Employee>(employeeList, cm);   
	    grid.setStyleAttribute("borderTop", "none");   
	    grid.setAutoExpandColumn("name");   
	    grid.setBorders(true);   
	    grid.setStripeRows(true);
	    grid.setColumnLines(true);
	    
	    return grid;
	}
	
	private List<Employee> getEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		DateTimeFormat f = DateTimeFormat.getFormat("yyyy-mm-dd");
		employees.add(new Employee("Hollie Voss", "General Administration",
				"Executive Director", 150000, f.parse("2006-05-01")));
		employees.add(new Employee("Emerson Milton",
				"Information Technology", "CTO", 120000, f
						.parse("2007-03-01")));
		employees.add(new Employee("Christina Blake",
				"Information Technology", "Project Manager", 90000, f
						.parse("2008-08-01")));
		employees.add(new Employee("Heriberto Rush",
				"Information Technology", "Senior S/WEngineer", 70000, f
						.parse("2009-02-07")));
		employees.add(new Employee("Candice Carson",
				"Information Technology", "S/W Engineer", 60000, f
						.parse("2007-11-01")));
		employees.add(new Employee("Chad Andrews",
				"Information Technology", "Senior S/W Engineer", 70000, f
						.parse("2008-02-01")));
		employees.add(new Employee("Dirk Newman", "Information Technology",
				"S/W Engineer", 62000, f.parse("2009-03-01")));
		employees.add(new Employee("Bell Snedden",
				"Information Technology", "S/W Engineer", 73000, f
						.parse("2007-07-07")));
		employees.add(new Employee("Benito Meeks", "Marketing",
				"General Manager", 105000, f.parse("2008-02-01")));
		employees.add(new Employee("Gail Horton", "Marketing", "Executive",
				55000, f.parse("2009-05-01")));
		employees.add(new Employee("Claudio Engle", "Marketing",
				"Executive", 58000, f.parse("2008-09-03")));
		employees.add(new Employee("Buster misjenou", "Accounts",
				"Executive", 52000, f.parse("2008-02-07")));
		return employees;
	}
	

	// ////////////////////////////////
	public class Employee extends BaseModel {
		private static final long serialVersionUID = 1L;

		public Employee() {
		}

		public Employee(String name, String department, String designation,
				double salary, Date joiningdate) {
			set("name", name);
			set("department", department);
			set("designation", designation);
			set("salary", salary);
			set("joiningdate", joiningdate);
		}

		public Date getJoiningdate() {
			return (Date) get("joiningdate");
		}

		public String getName() {
			return (String) get("name");
		}

		public String getDepartment() {
			return (String) get("department");
		}

		public String getDesignation() {
			return (String) get("designation");
		}

		public double getSalary() {
			Double salary = (Double) get("salary");
			return salary.doubleValue();
		}

		public String toString() {
			return getName();
		}
	}

	public class TestData {
		public List<Employee> getEmployees() {
			List<Employee> employees = new ArrayList<Employee>();
			DateTimeFormat f = DateTimeFormat.getFormat("yyyy-mm-dd");
			employees.add(new Employee("Hollie Voss", "General Administration",
					"Executive Director", 150000, f.parse("2006-05-01")));
			employees.add(new Employee("Emerson Milton",
					"Information Technology", "CTO", 120000, f
							.parse("2007-03-01")));
			employees.add(new Employee("Christina Blake",
					"Information Technology", "Project Manager", 90000, f
							.parse("2008-08-01")));
			employees.add(new Employee("Heriberto Rush",
					"Information Technology", "Senior S/WEngineer", 70000, f
							.parse("2009-02-07")));
			employees.add(new Employee("Candice Carson",
					"Information Technology", "S/W Engineer", 60000, f
							.parse("2007-11-01")));
			employees.add(new Employee("Chad Andrews",
					"Information Technology", "Senior S/W Engineer", 70000, f
							.parse("2008-02-01")));
			employees.add(new Employee("Dirk Newman", "Information Technology",
					"S/W Engineer", 62000, f.parse("2009-03-01")));
			employees.add(new Employee("Bell Snedden",
					"Information Technology", "S/W Engineer", 73000, f
							.parse("2007-07-07")));
			employees.add(new Employee("Benito Meeks", "Marketing",
					"General Manager", 105000, f.parse("2008-02-01")));
			employees.add(new Employee("Gail Horton", "Marketing", "Executive",
					55000, f.parse("2009-05-01")));
			employees.add(new Employee("Claudio Engle", "Marketing",
					"Executive", 58000, f.parse("2008-09-03")));
			employees.add(new Employee("Buster misjenou", "Accounts",
					"Executive", 52000, f.parse("2008-02-07")));
			return employees;
		}
	}
}