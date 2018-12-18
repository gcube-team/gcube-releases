package gr.cite.geoanalytics.geospatialbackend;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeospatialServices {

    public InputStream convertGeojsonToShapefile(InputStream geojsonStream,String shapefileName) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(geojsonStream, writer, "UTF8");
        String geojson = writer.toString();

        String fileName = FilenameUtils.removeExtension(shapefileName);

        File shpFolder = new File(fileName + "_zip");
        shpFolder.setWritable(true);
        shpFolder.mkdir();
        File shpFile = new File(shpFolder.getAbsolutePath() + "/" + fileName + ".shp");
        shpFile.getParentFile().setWritable(true);
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", shpFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        InputStream in = IOUtils.toInputStream(geojson);
        int decimals = 15;
        GeometryJSON gjson = new GeometryJSON(decimals);
        FeatureJSON fjson = new FeatureJSON(gjson);

        FeatureCollection<SimpleFeatureType, SimpleFeature> fc = fjson.readFeatureCollection(in);
        fc.getSchema();

        try {
            WriteShapefile shpWriter = new WriteShapefile(shpFile);

            boolean created = shpWriter.writeFeatures(fc, FilenameUtils.removeExtension(shapefileName));

            if (created != true) {
                //   Utils.deleteDir(shpFolder);
                return null;
            }

            WriteShapefile.pack(shpFolder.getAbsolutePath(), FilenameUtils.removeExtension(shapefileName) + ".zip");
            deleteDir(shpFolder);
            File shapefile = new File(FilenameUtils.removeExtension(shapefileName) + ".zip");
            InputStream shapeStream = new FileInputStream(shapefile);
            deleteDir(shapefile);
            return  shapeStream;
        } catch (FactoryException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteDir(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();

    }

}
