package org.gcube.data.analysis.tabulardata.cube.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DatabaseWranglerTest {

	@Inject
	private DatabaseWrangler dw;

	@BeforeClass 
	public static void setup() {
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Inject
	ResourceFinder finder;

	@Test
	public void testAll(){

		System.out.println("Creating table");
		String tableName = dw.createTable();
		Assert.assertNotNull(tableName);
		Assert.assertFalse(tableName.isEmpty());

		System.out.println("Adding columns");
		dw.addColumn(tableName, "field0", new BooleanType());
		dw.addColumn(tableName, "field1", new DateType());
		dw.addColumn(tableName, "field2", new GeometryType(GeometryShape.LINESTRING, 4));
		dw.addColumn(tableName, "field3", new GeometryType(GeometryShape.POLYGON, 2));
		dw.addColumn(tableName, "field4", new IntegerType());
		dw.addColumn(tableName, "field5", new NumericType(3,1));
		dw.addColumn(tableName, "field6", new TextType(10));
		dw.addColumn(tableName, "field7", new GeometryType(2));

		dw.createIndex(tableName, "field2");
		dw.createIndex(tableName, "field3");
		dw.createIndex(tableName, "field4");

		String tmp;

		System.out.println("Cloning table with data");
		tmp = dw.cloneTable(tableName, true, false);
		Assert.assertNotNull(tmp);
		Assert.assertFalse(tmp.isEmpty());
		dw.removeTable(tmp);

		System.out.println("Cloning table without data");
		tmp = dw.cloneTable(tableName, false, false);
		Assert.assertNotNull(tmp);
		Assert.assertFalse(tmp.isEmpty());
		dw.removeTable(tmp);

		System.out.println("Removing columns");
		dw.removeColumn(tableName,"field6");
		dw.removeColumn(tableName,"field7");
	}

	@Test
	public void testUnloggedTable(){
		ScopeProvider.instance.set("/gcube/devsec");
		String tableName = dw.createTable(true);
		Assert.assertNotNull(tableName);
		Assert.assertFalse(tableName.isEmpty());
	}

	@Test
	public void retrieveSqlFunctions() {

		for (String file :finder.getResourcesPath(Pattern.compile(".*\\.sql"))){
			BufferedReader reader =null;
			InputStream is = ClassLoader.getSystemResourceAsStream("org/gcube/data/analysis/tabulardata/sql/"+file);
			if (is==null)
				continue;
			try{
				reader = new BufferedReader(new InputStreamReader(is));
				String         line = null;
				StringBuilder  stringBuilder = new StringBuilder();
				while( ( line = reader.readLine() ) != null ) 
					stringBuilder.append( line );
				System.out.println(stringBuilder.toString());
			}catch(Exception e){
				throw new RuntimeException("error initializing sql",e);
			}finally{
				if (reader!=null )
					try {
						reader.close();
					} catch (IOException e) {
					}
			}
		}
	}

}
