package org.gcube.common.storagehub.model.types;

import java.util.List;

import org.gcube.common.storagehub.model.acls.ACL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ACLList {

	List<ACL> acls;
	
}
