//package gr.cite.geoanalytics.util.test;
//
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeImportDao;
//
//import javax.inject.Inject;
//
//public class ShapeImportUtilTest
//{
//	private ShapeImportDao shapeImportDao;
//	private ShapeDao shapeDao;
//	
//	@Inject
//	public void setShapeImportDao(ShapeImportDao shapeImportDao) {
//		this.shapeImportDao = shapeImportDao;
//	}
//	
//	@Inject
//	public void setShapeDao(ShapeDao shapeDao) {
//		this.shapeDao = shapeDao;
//	}
//	
//	public Shape importShape(String filename, int srid, String charset, boolean axisInvert) throws Exception
//	{
//		return null;
//		/*Shape s = null;
//	
//		//UUID importUUID = ShapeImportUtil.fromShapeFile(filename, srid, charset, axisInvert);
//		UUID importUUID = ShapeImportUtil.fromShapefile(filename, srid, charset, axisInvert, new User());
//		
//		List<ShapeImport> result = shapeImportDao.getAll();
//
//        System.out.println(result.size() + " results");
//        for ( ShapeImport sh : (List<ShapeImport>) result ) {
//			System.out.println( "Shape (" + sh.getShapeIdentity() + ") : ");
//			System.out.println( "Shape (" + sh.getGeography() + ") : ");
//			if(importUUID.equals(sh.getShapeImport()))
//			{
//				s = new Shape();
//				String name = new File(filename).getName();
//				name = name.substring(0, name.indexOf("."));
//				s.setName(new File(filename).getName());
//				s.setCreationDate(sh.getCreationDate());
//				s.setLastUpdate(sh.getLastUpdate());
//				s.setCreator(sh.getCreator());
//				s.setExtraData(sh.getData());
//				s.setGeography(sh.getGeography());
//				s.setShapeImport(sh);
//				s.setShapeClass(1);
//				shapeDao.create(s);
//			}
//		}
//		System.out.println("------------------------");
//		return s;*/
//	}
//	
//	public void test() throws Exception
//	{
//		//Shape sp = importShape("C:\\Users\\diljin\\Documents\\geopolis\\data\\Laconia\\ZOE_PARNWNA.shp", 2100, "windows-1253", true);
//		Shape sp = importShape("C:\\Users\\Diljin\\Documents\\geopolis\\data\\Laconia\\ODIKO", 2100, "windows-1253", true);
//		sp.getClass();
//	}
//}
