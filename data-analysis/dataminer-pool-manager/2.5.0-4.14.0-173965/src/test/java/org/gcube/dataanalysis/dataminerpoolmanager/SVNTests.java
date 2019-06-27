package org.gcube.dataanalysis.dataminerpoolmanager;

import java.io.IOException;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.util.AlgorithmBuilder;
import org.gcube.dataanalysis.dataminer.poolmanager.util.SVNUpdater;
import org.gcube.dataanalysis.dataminer.poolmanager.util.impl.SVNUpdaterStaging;
import org.tmatesoft.svn.core.SVNException;

/**
 * Created by ggiammat on 5/17/17.
 */
public class SVNTests {
	
	

  public static void main(String[] args) throws SVNException, IOException, InterruptedException {


    SVNUpdater svnUpdater = new SVNUpdaterStaging();
    
    Algorithm algo = AlgorithmBuilder.create("http://data.d4science.org/YjJ3TmJab1dqYzVoTmppdjlsK0l0b1ZXWGtzWlQ1NHNHbWJQNStIS0N6Yz0");
    //algo.setClazz(algo.getClazz() + "TEST");
    algo.setAlgorithmType("transducerers");
    algo.setCategory("NLP");
    algo.setFullname("Ondřej Košarko");
    //System.out.println(algo.getCategory());
    //System.out.println(algo.getSkipJava());
    
	//svnUpdater.updateSVNAlgorithmList("/trunk/data-analysis/DataMinerConfiguration/algorithms/dev/algorithms", algo, "/gcube/devNext/NextNext", algo.getCategory(), algo.getAlgorithmType(), "");

    //svnUpdater.updateSVNRProtoAlgorithmList(algo, "/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab", "Dataminer Pool Manager", "Proto");
    //svnUpdater.readRPRotoDeps(algo);
  }
}
