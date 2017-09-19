package org.gcube.dataanalysis.dataminerpoolmanager;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.util.AlgorithmBuilder;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.ServiceConfiguration;
import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;

/**
 * Created by ggiammat on 5/17/17.
 */
public class SVNTests {


  public static void main(String[] args) throws SVNException, IOException, InterruptedException {


    SVNUpdater svnUpdater = new SVNUpdater(new ServiceConfiguration("/home/ngalante/workspace/dataminer-pool-manager/src/main/resources/service.properties"));
    
    Algorithm algo = AlgorithmBuilder.create("http://data.d4science.org/cnFLNHYxR1ZDa1VNdEhrTUQyQlZjaWRBVVZlUHloUitHbWJQNStIS0N6Yz0");
    //algo.setClazz(algo.getClazz() + "TEST");
    System.out.println(algo.getAlgorithmType());
    System.out.println(algo.getCategory());
    System.out.println(algo.getSkipJava());
    
    //svnUpdater.updateSVNRProtoAlgorithmList(algo, "/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab", "Dataminer Pool Manager", "Proto");
    //svnUpdater.readRPRotoDeps(algo);
  }
}
