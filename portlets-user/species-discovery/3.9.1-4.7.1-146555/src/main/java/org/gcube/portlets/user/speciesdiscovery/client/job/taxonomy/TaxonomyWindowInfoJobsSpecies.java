package org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy;


import org.gcube.portlets.user.speciesdiscovery.shared.JobTaxonomyModel;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TaxonomyWindowInfoJobsSpecies extends Window{
	
	private TaxonomyJobsInfoContainer jobInfo = new TaxonomyJobsInfoContainer(new GroupingStore<ModelData>());
	
	public TaxonomyWindowInfoJobsSpecies(TaxonomyJobsInfoContainer jobInfoContainer) {
		setModal(true);
		setSize(450, 300);
		this.jobInfo = jobInfoContainer;
		add(jobInfo);
	}
	
	
	public TaxonomyWindowInfoJobsSpecies() {
		setModal(true);
		setSize(450, 300);
		setLayout(new FitLayout());
		add(jobInfo);
	}
	
	public void updateListStore(JobTaxonomyModel jobModel){
		
		GroupingStore<ModelData> list = new GroupingStore<ModelData>();
		
//		System.out.println("jobModel.getId  " + jobModel.getIdentifier() + "  children num: " +jobModel.getListChildStatus().size());
		
	
//		GWT.log("taxonomy job:  "+jobModel);
		
		for (JobTaxonomyModel childJob : jobModel.getListChildStatus()) {
			BaseModelData data = new BaseModelData();
			data.set(TaxonomyJobInfoFields.NAME.getId(), childJob.getName());
			data.set(TaxonomyJobInfoFields.LOADING.getId(), childJob.getDownloadState());
			list.add(data);
		}
		
		updateJobsInfoContainer(list);

	}

	private void updateJobsInfoContainer(GroupingStore<ModelData> list) {
		this.jobInfo.updateStore(list);
	}

	public void setWindowTitle(String title) {
		this.setHeading(title);
		
	}

}
