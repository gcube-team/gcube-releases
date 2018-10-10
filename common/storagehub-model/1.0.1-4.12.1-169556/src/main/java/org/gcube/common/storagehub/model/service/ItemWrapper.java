package org.gcube.common.storagehub.model.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.gcube.common.storagehub.model.items.Item;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWrapper<T extends Item> {

	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
	private  T item;
}
