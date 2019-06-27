/**
 *
 */
package org.gcube.datatransfer.resolver.parthenos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 26, 2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ParthenosRequest {

	@JsonProperty(value="entity_name", required=true)
	private String entity_name;

}
