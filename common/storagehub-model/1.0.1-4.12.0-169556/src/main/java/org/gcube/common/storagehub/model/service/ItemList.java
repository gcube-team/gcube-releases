package org.gcube.common.storagehub.model.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.gcube.common.storagehub.model.items.Item;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemList {

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	private List<? extends Item> itemlist;
}
