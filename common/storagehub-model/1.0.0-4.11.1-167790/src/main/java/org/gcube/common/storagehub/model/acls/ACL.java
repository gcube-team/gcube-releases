package org.gcube.common.storagehub.model.acls;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ACL {
	
	private String pricipal;
	private List<AccessType> accessTypes= new ArrayList<>(); 
	
}
