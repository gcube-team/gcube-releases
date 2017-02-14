package org.gcube.dataanalysis.ecoengine.signals.ssa;

import java.util.List;

public class SSAGroupList {
    
    private List groups;
    
    public SSAGroupList(List groups) {
        this.groups = groups;
    }
    
    public String toString() {
        String value = "";
        for (int i = 0; i < groups.size(); i++) {
            if(i != groups.size() - 1) {
                value += groups.get(i).toString() + ",";
            } else {
                value += groups.get(i).toString();
            }   
        }
        return value;
    }

    public List getGroups() {
        return groups;
    }
    
    
}
