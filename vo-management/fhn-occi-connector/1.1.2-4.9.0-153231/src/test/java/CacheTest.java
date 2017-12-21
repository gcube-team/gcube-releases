import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.vomanagement.occi.TemplatesCache;

/**
 * Created by ggiammat on 11/15/16.
 */
public class CacheTest {

  public static void main(String[] args){

    TemplatesCache tc = TemplatesCache.getInstance();
    
    ResourceTemplate rt = new ResourceTemplate();
    rt.setCores(10);
    rt.setMemory(100L);
    rt.setId("0001");
    tc.cache("myprov", rt);

    ResourceTemplate rtCached = tc.getResourceTemplate("myprov", "0001");

    System.out.println(rtCached.getMemory());

    //tc.closeCache();

  }
}
