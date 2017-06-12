package org.gcube.contentmanagement.timeseries.geotools.vti.connectors;


public class Bathymetry {

	private static VTIBathymetry bathymetryObj;

	public static void init() throws Exception {
		bathymetryObj.open();
	}

	public static void close() throws Exception {
		bathymetryObj.close();
	}

	public static void initInstance(String bathfile ) throws Exception{
		if (bathymetryObj==null){
			bathymetryObj = new VTIBathymetry(bathfile);
			try {
				init();
			} catch (Exception e) {
				e.printStackTrace();
				close();
			}
		}
	}
	public Bathymetry(String bathfile) throws Exception {
		
		initInstance(bathfile);
	}
	
	public synchronized short[]  compute(java.awt.geom.Point2D.Double [] points) throws Exception{
		
		return bathymetryObj.getZ(points);
		
	}
	
	public static void main(String[] args) {

		VTIBathymetry vti = new VTIBathymetry("./cfg/gebco_08.nc");
		try {
			long t0 = System.currentTimeMillis();
			vti.open();
			System.out.println("file open");

			long t00 = System.currentTimeMillis();

			// x = long
			// y = lat

			double z = vti.getZ(1.75, 0.25);
			System.out.println("Z:" + z);
			long t01 = System.currentTimeMillis();
			System.out.println("computation elapsed " + (t01 - t00) + " ms");

			t00 = System.currentTimeMillis();
			java.awt.geom.Point2D.Double[] pp = new java.awt.geom.Point2D.Double[9];

			java.awt.geom.Point2D.Double point = new java.awt.geom.Point2D.Double(0.25f, 0.75f);
			pp[0] = point;
			point = new java.awt.geom.Point2D.Double(0.25f, 0.75f);
			pp[1] = point;
			point = new java.awt.geom.Point2D.Double(50f, 75f);
			pp[2] = point;
			point = new java.awt.geom.Point2D.Double(45f, 0.75f);
			pp[3] = point;
			point = new java.awt.geom.Point2D.Double(30f, 0.75f);
			pp[4] = point;
			point = new java.awt.geom.Point2D.Double(25f, 0.75f);
			pp[5] = point;
			point = new java.awt.geom.Point2D.Double(90f, 0.75f);
			pp[6] = point;
			point = new java.awt.geom.Point2D.Double(180f, 180f);
			pp[7] = point;
			point = new java.awt.geom.Point2D.Double(0.25f, 0.75f);
			pp[8] = point;

			short[] zz = vti.getZ(pp);
			System.out.println("ZZ:" + zz);
			t01 = System.currentTimeMillis();
			System.out.println("computation elapsed " + (t01 - t00) + " ms");
			for (short z1 : zz)
				System.out.println("Zz:" + z1);

			vti.close();
			System.out.println("file closed");
			long t1 = System.currentTimeMillis();
			System.out.println("elapsed " + (t1 - t0) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
