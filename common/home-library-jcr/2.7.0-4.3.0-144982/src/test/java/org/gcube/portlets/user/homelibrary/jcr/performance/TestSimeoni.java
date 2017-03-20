package org.gcube.portlets.user.homelibrary.jcr.performance;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 3, 2013
 *
 */


public class TestSimeoni {

    public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV

    public static String TEST_USER = "fabio.simeoni";

    

    @Test

    public void accessTest() throws Exception {

        ScopeProvider.instance.set(DEFAULT_SCOPE);

        System.out.println("Start Test Simeoni");

        long time = System.currentTimeMillis();

        Workspace ws = HomeLibrary.getUserWorkspace(TEST_USER);

        for (WorkspaceItem item : ws.getRoot().getChildren())

            item.getProperties().getProperties().keySet();

        long diff = System.currentTimeMillis()-time;

        System.out.println("End Test Simeoni: "+diff);

    }

    

    @Test

    public void accessTest1() throws Exception {

        

        ScopeProvider.instance.set(DEFAULT_SCOPE);

        

        System.out.println("Start Test");

        long time = System.currentTimeMillis();

        long diff;

        

        System.out.println("Get worskpace");

        Workspace ws = HomeLibrary.getUserWorkspace(TEST_USER);

        diff = System.currentTimeMillis()-time;

        System.out.println("Worskapce returned in: "+diff);

        System.out.println("Get Root");

        WorkspaceFolder root = ws.getRoot();

        diff = System.currentTimeMillis()-time;

        System.out.println("Root returned in: "+diff);

        

        System.out.println("Get Children");

        List<WorkspaceItem> children = root.getChildren();

        diff = System.currentTimeMillis()-time;

        System.out.println("Get Children returned in: "+diff);

        

        System.out.println("Children size is: "+children.size());

        for (WorkspaceItem item : children){

            diff = System.currentTimeMillis()-time;

            System.out.println("Item: +"+item.getId()+", keyset: "+item.getProperties().getProperties().keySet() + ", Diff is: "+diff);

        }

        diff = System.currentTimeMillis()-time;

        System.out.println("Children properties returned in: "+diff);

        diff = System.currentTimeMillis()-time;

        System.out.println("End test: "+diff);

    }

}

