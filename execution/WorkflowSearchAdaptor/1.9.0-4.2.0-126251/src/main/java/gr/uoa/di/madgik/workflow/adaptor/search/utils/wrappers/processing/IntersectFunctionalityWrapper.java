package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.processing;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.join.RecordGenerationPolicy;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class IntersectFunctionalityWrapper extends JoinWrapper 
{

	public IntersectFunctionalityWrapper() throws Exception 
	{
		super();
		enableDuplicateElimination();
		setRecordGenerationPolicy(RecordGenerationPolicy.KeepLeft);
		// TODO Auto-generated constructor stub
	}

}
