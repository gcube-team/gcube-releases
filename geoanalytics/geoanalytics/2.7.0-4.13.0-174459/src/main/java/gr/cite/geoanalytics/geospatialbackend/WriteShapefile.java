package gr.cite.geoanalytics.geospatialbackend;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.referencing.CRS;
import org.geotools.util.SimpleInternationalString;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WriteShapefile {
    File outfile;
    private ShapefileDataStore shpDataStore;

    public WriteShapefile(File f) {
        outfile = f;

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        try {
            params.put("url", outfile.toURI().toURL());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.put("create spatial index", Boolean.TRUE);

        try {
            shpDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean writeFeatures(FeatureCollection<SimpleFeatureType, SimpleFeature> features, String shapefileName) throws FactoryException {

        if (shpDataStore == null) {
            throw new IllegalStateException("Datastore can not be null when writing");
        }
        SimpleFeatureType schema = features.getSchema();
        GeometryDescriptor geom = schema.getGeometryDescriptor();
        String oldGeomAttrib = "";
        try {

            Transaction transaction = new DefaultTransaction("create");
            String typeName = shpDataStore.getTypeNames()[0];
//            SimpleFeatureSource featureSource = shpDataStore
//                    .getFeatureSource(typeName);

            List<AttributeDescriptor> attributes = schema
                    .getAttributeDescriptors();
            GeometryType geomType = null;
            List<AttributeDescriptor> attribs = new ArrayList<AttributeDescriptor>();
            for (AttributeDescriptor attrib : attributes) {
                AttributeType type = attrib.getType();
                if (type instanceof GeometryType) {
                    geomType = (GeometryType) type;
                    oldGeomAttrib = attrib.getLocalName();

                } else {
                    attribs.add(attrib);
                }
            }


            GeometryTypeImpl gt = new GeometryTypeImpl(
                    new NameImpl("the_geom"), geomType.getBinding(),
                    geomType.getCoordinateReferenceSystem(),
                    geomType.isIdentified(), geomType.isAbstract(),
                    geomType.getRestrictions(), geomType.getSuper(),
                    geomType.getDescription());

            GeometryDescriptor geomDesc = new GeometryDescriptorImpl(
                    gt, new NameImpl("the_geom"),
                    geom.getMinOccurs(), geom.getMaxOccurs(),
                    geom.isNillable(), geom.getDefaultValue());

            attribs.add(0, geomDesc);

            SimpleFeatureType shpType = new SimpleFeatureTypeImpl(
                    schema.getName(), attribs, geomDesc,
                    schema.isAbstract(), schema.getRestrictions(),
                    schema.getSuper(), schema.getDescription());


            shpDataStore.createSchema(shpType);
            System.out.println("SHAP4E:" + shpType.getTypeName());

            SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(shpDataStore.getTypeNames()[0]);
            CoordinateReferenceSystem crs = null;



            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                List<SimpleFeature> feats = new ArrayList<SimpleFeature>();
                FeatureIterator<SimpleFeature> features2 = features.features();

                while (features2.hasNext()) {

                    SimpleFeature f = features2.next();
                    SimpleFeature reType =DataUtilities.reType(shpType, f, true);
                    //set the default Geom (the_geom) from the original Geom
                    reType.setAttribute("the_geom", f.getAttribute(oldGeomAttrib));
                    feats.add(reType);
                }
                features2.close();
                SimpleFeatureCollection collection = new ListFeatureCollection(shpType, feats);
                featureStore.setTransaction(transaction);
                try {
                    List<FeatureId> ids = featureStore.addFeatures(collection);
                    transaction.commit();
                } catch (Exception problem) {
                    problem.printStackTrace();
                    transaction.rollback();
                } finally {
                    transaction.close();
                }

                shpDataStore.dispose();

                return true;
            } else {
                shpDataStore.dispose();
                System.err.println("ShapefileStore is not writable");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
        Path p = Files.createFile(Paths.get(zipFilePath));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
    }
}
