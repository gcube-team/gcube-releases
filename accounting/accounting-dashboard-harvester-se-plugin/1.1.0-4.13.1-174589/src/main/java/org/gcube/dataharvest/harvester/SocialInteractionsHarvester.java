package org.gcube.dataharvest.harvester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class SocialInteractionsHarvester extends SocialNetworkingHarvester {

	private static Logger logger = LoggerFactory.getLogger(SocialInteractionsHarvester.class);

	private int likes;
	private int replies;
	private int posts;

	public static final String PATH = "/2/posts/get-posts-vre?gcube-token=";

	public SocialInteractionsHarvester(Date start, Date end) throws Exception {
		super(start, end);
	}

	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {

		String context = Utils.getCurrentContext();

		try {

			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();

			getJson();

			ScopeDescriptor scopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor();
			
			AccountingRecord likesAR = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.SOCIAL_LIKES), (long) likes);
			logger.debug("{} : {}", likesAR.getDimension().getId(), likesAR.getMeasure());
			accountingRecords.add(likesAR);
			
			AccountingRecord postsAR = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.SOCIAL_POSTS), (long) posts);
			logger.debug("{} : {}", postsAR.getDimension().getId(), postsAR.getMeasure());
			accountingRecords.add(postsAR);
			
			AccountingRecord repliesAR = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.SOCIAL_REPLIES), (long) replies);
			logger.debug("{} : {}", repliesAR.getDimension().getId(), repliesAR.getMeasure());
			accountingRecords.add(repliesAR);
			
			return accountingRecords;
		} catch(Exception e) {
			logger.error("Error Harvesting Social Interactions for context {}", context, e);
			throw e;
		}

	}



	private void getJson() throws Exception {
		JSONObject jsonObject = getJSONObject(PATH);

		Boolean success = (Boolean) jsonObject.get("success");
		if(success == false) {
			throw new IOException("Erro while getting posts");
		}

		JSONArray res = jsonObject.getJSONArray("result");
		int len = res.length();

		likes = replies = posts = 0;

		for(int i = 0; i < len; i++) {

			JSONObject item = res.getJSONObject(i);
			long time = item.getLong("time");

			if(start.getTime() <= time && time <= end.getTime()) {
				posts++;
				replies += item.getInt("comments_no");
				likes += item.getInt("likes_no");
			}

		}

	}
	

}
