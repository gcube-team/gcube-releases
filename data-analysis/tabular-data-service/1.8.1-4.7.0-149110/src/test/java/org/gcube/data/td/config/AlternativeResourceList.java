package org.gcube.data.td.config;

import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.data.ResourceList;

@Singleton
@Alternative
public class AlternativeResourceList extends ResourceList {

}
