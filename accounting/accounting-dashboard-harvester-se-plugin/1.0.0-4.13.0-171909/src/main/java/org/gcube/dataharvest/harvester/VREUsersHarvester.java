package org.gcube.dataharvest.harvester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class VREUsersHarvester extends SocialNetworkingHarvester {

	private static Logger logger = LoggerFactory.getLogger(VREUsersHarvester.class);


	public static final String PATH = "/2/users/get-all-usernames?gcube-token=";

	public VREUsersHarvester(Date start, Date end) throws Exception {
		super(start, end);
	}

	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		try {
			// String context = Utils.getCurrentContext();
			int measure = get();
			
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			ScopeDescriptor scopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor();
			
			AccountingRecord ar = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.USERS), (long) measure);
			logger.debug("{} : {}", ar.getDimension().getId(), ar.getMeasure());
			accountingRecords.add(ar);
			
			return accountingRecords;
			
		} catch(Exception e) {
			throw e;
		}
	}

	private int get() throws Exception {
		JSONObject jsonObject = getJSONObject(PATH);

		int userNumber = 0;

		Boolean success = (Boolean) jsonObject.get("success");
		if(success == false) {
			throw new IOException("Erro while getting VRE Users");
		}

		userNumber = jsonObject.getJSONArray("result").length();
		return userNumber;
	}

}
