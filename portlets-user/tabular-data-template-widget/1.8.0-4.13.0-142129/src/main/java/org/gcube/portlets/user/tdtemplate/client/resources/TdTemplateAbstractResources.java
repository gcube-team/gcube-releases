/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 10, 2014
 *
 */
public class TdTemplateAbstractResources {

	
	public static final ResourcesTemplate INSTANCE = GWT.create(ResourcesTemplate.class);
	
	public static AbstractImagePrototype columnAdd(){
		return AbstractImagePrototype.create(INSTANCE.getAddColumn());
	}
	
	public static AbstractImagePrototype columnRemove(){
		return AbstractImagePrototype.create(INSTANCE.getRemoveColumn());
	}
	
	public static AbstractImagePrototype columnModify(){
		return AbstractImagePrototype.create(INSTANCE.getModifyColumn());
	}
	
	public static AbstractImagePrototype pencil(){
		return AbstractImagePrototype.create(INSTANCE.pencil());
	}
	
	public static AbstractImagePrototype addRule(){
		return AbstractImagePrototype.create(INSTANCE.rule());
	}
	
	public static AbstractImagePrototype ruleColumnAdd(){
		return AbstractImagePrototype.create(INSTANCE.ruleColumnAdd());
	}
	
	public static AbstractImagePrototype pencil10(){
		return AbstractImagePrototype.create(INSTANCE.pencil10());
	}
	
	public static AbstractImagePrototype delete(){
		return AbstractImagePrototype.create(INSTANCE.delete());
	}
	
	public static AbstractImagePrototype suggest(){
		return AbstractImagePrototype.create(INSTANCE.suggest());
	}
	
	public static AbstractImagePrototype handsUP(){
		return AbstractImagePrototype.create(INSTANCE.handsup());
	}
	
	public static AbstractImagePrototype submit(){
		return AbstractImagePrototype.create(INSTANCE.submit());
	}
	
	public static AbstractImagePrototype newtemplate(){
		return AbstractImagePrototype.create(INSTANCE.newtemplate());
	}
	
	public static AbstractImagePrototype error(){
		return AbstractImagePrototype.create(INSTANCE.error());
	}
	
	public static AbstractImagePrototype okicon(){
		return AbstractImagePrototype.create(INSTANCE.okicon());
	}
	
	public static AbstractImagePrototype alerticon(){
		return AbstractImagePrototype.create(INSTANCE.alerticon());
	}
	
	public static AbstractImagePrototype info(){
		return AbstractImagePrototype.create(INSTANCE.info());
	}
	
	public static AbstractImagePrototype lock(){
		return AbstractImagePrototype.create(INSTANCE.lock());
	}

	/**
	 * @return
	 */
	public static AbstractImagePrototype tip() {
		return AbstractImagePrototype.create(INSTANCE.tip());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype loader() {
		return AbstractImagePrototype.create(INSTANCE.loader());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype step1() {
		return AbstractImagePrototype.create(INSTANCE.step1());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype step2() {
		return AbstractImagePrototype.create(INSTANCE.step2());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype step3() {
		return AbstractImagePrototype.create(INSTANCE.step3());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype filter16() {
		return AbstractImagePrototype.create(INSTANCE.filter16());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype filter24() {
		return AbstractImagePrototype.create(INSTANCE.filter24());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype flow() {
		return AbstractImagePrototype.create(INSTANCE.flow());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype flow24() {
		return AbstractImagePrototype.create(INSTANCE.flow24());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype flow24Ok() {
		return AbstractImagePrototype.create(INSTANCE.flow24ok());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype close() {
		return AbstractImagePrototype.create(INSTANCE.close());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype view() {
		return AbstractImagePrototype.create(INSTANCE.view());
	}
	
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype timeAggregate() {
		return AbstractImagePrototype.create(INSTANCE.timeaggregate());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype timeAggregate24() {
		return AbstractImagePrototype.create(INSTANCE.timeaggregate24());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype action() {
		return AbstractImagePrototype.create(INSTANCE.action());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype back() {
		return AbstractImagePrototype.create(INSTANCE.back());
	}
	
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype undo24() {
		return AbstractImagePrototype.create(INSTANCE.undo24());
	}
	
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype history24() {
		return AbstractImagePrototype.create(INSTANCE.history24());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype legend() {
		return AbstractImagePrototype.create(INSTANCE.legend());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype timeGroup() {
		return AbstractImagePrototype.create(INSTANCE.timeGroup());
	}
	
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype pencil24() {
		return AbstractImagePrototype.create(INSTANCE.pencil24());
	}

	/**
	 * @return
	 */
	public static AbstractImagePrototype normalize24() {
		return AbstractImagePrototype.create(INSTANCE.normalize24());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype ruleTableAdd() {
		return AbstractImagePrototype.create(INSTANCE.ruleTableAdd());
	}
	
	/**
	 * @return
	 */
	public static AbstractImagePrototype columnClone() {
		return AbstractImagePrototype.create(INSTANCE.cloneColumn());
	}
}
