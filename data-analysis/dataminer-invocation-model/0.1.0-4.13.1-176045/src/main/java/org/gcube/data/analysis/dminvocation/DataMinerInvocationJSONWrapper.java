/**
 *
 */

package org.gcube.data.analysis.dminvocation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;

import com.google.gson.annotations.SerializedName;



/**
 * The Class DataMinerInvocationJSONWrapper.
 * Used just to add the root name "dataminer-invocation" in the JSON
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 19, 2018
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class DataMinerInvocationJSONWrapper {

	@SerializedName("dataminer-invocation")
	private DataMinerInvocation dataminerInvocation;
}
