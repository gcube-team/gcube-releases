AUI().add(
	'liferay-tags-admin',
	function(A) {
		var AssetTagsAdmin = A.Component.create(
			{
				NAME: 'assettagsadmin',

				EXTENDS: A.Base,

				constructor: function() {
					AssetTagsAdmin.superclass.constructor.call(this);
				},

				prototype: {
					initializer: function(portletId) {
						var instance = this;

						var tagsContainer = A.one(instance._tagsContainerSelector);

						instance.portletId = portletId;
						instance._tagsAdminContainer = A.one('.tags-admin-container');

						A.all('.tag-close').on(
							'click',
							function() {
								instance._unselectAllTags();
								instance._closeEditSection();
							}
						);

						A.all('.tag-save-properties').on(
							'click',
							function() {
								instance._saveProperties();
							}
						);

						instance._portletMessageContainer = A.Node.create('<div class="lfr-message-response" id="tag-portlet-messages" />');
						instance._tagMessageContainer = A.Node.create('<div class="lfr-message-response" id="tag-messages" />');

						instance._portletMessageContainer.hide();
						instance._tagMessageContainer.hide();

						instance._tagsAdminContainer.placeBefore(instance._portletMessageContainer);
						tagsContainer.placeBefore(instance._tagMessageContainer);

						var toolbar = A.all('.tags-admin-toolbar');

						var addTagLayer = A.one('.add-tag-layer');

						instance._addTagOverlay = new A.OverlayContextPanel(
							{
								align: {
									points: ['tr', 'br']
								},
								bodyContent: addTagLayer,
								trigger: '.add-tag-button'
							}
						).render();

						instance._addTagOverlay.after(
							'visibleChange',
							function(event) {
								if (event.newVal) {
									var inputTagNameNode = addTagLayer.one('.new-tag-name');

									Liferay.Util.focusFormField(inputTagNameNode);
								}
							}
						);

						A.one('.tag-permissions-button').on(
							'click',
							function() {
								var tagName = instance._selectedTagName;
								var tagId = instance._selectedTagId;

								if (tagName && tagId) {
									var portletURL = instance._createPermissionURL(
										'com.liferay.portlet.asset.model.AssetTag',
										tagName, tagId);

									submitForm(document.hrefFm, portletURL.toString());
								}
								else {
									alert(Liferay.Language.get('please-first-select-a-tag'));
								}
							}
						);

						var addTag = function() {
							var inputTagNameNode = addTagLayer.one('.new-tag-name');
							var newTagName = (inputTagNameNode && inputTagNameNode.val()) || '';

							instance._hideAllMessages();
							instance._addTag(newTagName);
						};

						A.one('input.tag-save-button').on('click', addTag);

						A.all('.tags-admin-actions input').on(
							'keyup',
							function(event) {
								if (event.keyCode == 13) {
									var input = event.currentTarget;

									addTag();

									event.halt();
								}
							}
						);

						A.one('input.tag-delete-button').on(
							'click',
							function() {
								if (confirm(Liferay.Language.get('are-you-sure-you-want-to-delete-this-tag'))) {
									instance._deleteTag(
										instance._selectedTagId,
										function(message) {
											var exception = message.exception;

											if (!exception) {
												instance._closeEditSection();
												instance._hideToolbarOverlays();
												instance._displayTags();
											}
											else {
												if (exception.indexOf('auth.PrincipalException') > -1) {
													var errorText = Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource');

													instance._sendMessage('error', errorText);
												}
											}
										}
									);
								}
							}
						);

						A.all('.close-panel').on(
							'click',
							function() {
								instance._hideToolbarOverlays();
							}
						);

						A.all('.aui-overlay input[type=text]').on(
							'keyup',
							function(event) {
								var ESC_KEY_CODE = 27;
								var keyCode = event.keyCode;

								if (keyCode == ESC_KEY_CODE) {
									instance._hideToolbarOverlays();
								}
							}
						);

						instance._loadData();

						instance.after('drag:drag', instance._afterDrag);
						instance.after('drag:drophit', instance._afterDragDrop);
						instance.after('drag:enter', instance._afterDragEnter);
						instance.after('drag:exit', instance._afterDragExit);
						instance.after('drag:start', instance._afterDragStart);
					},

					_addTag: function(tagName, callback) {
						var instance = this;
						var communityPermission = instance._getPermissionsEnabled('community');
						var guestPermission = instance._getPermissionsEnabled('guest');

						var serviceParameterTypes = [
							'java.lang.String',
							'[Ljava.lang.String;',
							'com.liferay.portal.service.ServiceContext'
						];

						Liferay.Service.Asset.AssetTag.addTag(
							{
								name: tagName,
								properties: [],
								serviceContext: A.JSON.stringify(
									{
										communityPermissions: communityPermission,
										guestPermissions: guestPermission,
										scopeGroupId: themeDisplay.getParentGroupId()
									}
								),
								serviceParameterTypes: A.JSON.stringify(serviceParameterTypes)
							},
							function(message) {
								var exception = message.exception;

								if (!exception && message.tagId) {
									instance._sendMessage('success', Liferay.Language.get('your-request-processed-successfully'));

									instance._displayTags(
										function() {
											var tag = instance._selectTag(message.tagId);

											if (tag) {
												var scrollTop = tag.get('region').top;

												A.one(instance._tagsContainerSelector).set('scrollTop', scrollTop);
											}

											instance._showSection('.tag-edit');
										}
									);

									instance._resetActionValues();
									instance._hideToolbarOverlays();

									if (callback) {
										callback(tagName);
									}
								}
								else {
									var errorText = '';

									if (exception.indexOf('DuplicateTagException') > -1) {
										errorText = Liferay.Language.get('that-tag-already-exists');
									}
									else if ((exception.indexOf('TagNameException') > -1) ||
											 (exception.indexOf('AssetTagException') > -1)) {

										errorText = Liferay.Language.get('one-of-your-fields-contains-invalid-characters');
									}
									else if (exception.indexOf('auth.PrincipalException') > -1) {
										errorText = Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource');
									}

									if (errorText) {
										instance._sendMessage('error', errorText);
									}
								}
							}
						);
					},

					_addProperty: function(baseNode, key, value) {
						var instance = this;

						var baseProperty = A.one('div.tag-property-row');

						if (baseProperty) {
							var newProperty = baseProperty.clone();

							var propertyKeyNode = newProperty.one('.property-key');
							var propertyValueNode = newProperty.one('.property-value');

							if (propertyKeyNode) {
								propertyKeyNode.val(key);
							}

							if (propertyValueNode) {
								propertyValueNode.val(value);
							}

							baseNode.placeAfter(newProperty);

							newProperty.show();

							if (!key && !value) {
								newProperty.one('input').focus();
							}

							instance._attachPropertyIconEvents(newProperty);
						}
					},

					_afterDrag: function(event) {
						var instance = this;

						A.DD.DDM.syncActiveShims(true);
					},

					_afterDragDrop: function(event) {
						var instance = this;

						var dropNode = event.drop.get('node');
						var node = event.target.get('node');

						dropNode.removeClass('active-area');

						instance._merge(node, dropNode);
					},

					_afterDragEnter: function(event) {
						var instance = this;

						var dropNode = event.drop.get('node');

						dropNode.addClass('active-area');
					},

					_afterDragExit: function(event) {
						var instance = this;

						var dropNode = event.drop.get('node');

						dropNode.removeClass('active-area');
					},

					_afterDragStart: function(event) {
						var instance = this;

						var drag = event.target;

						var proxyNode = drag.get('dragNode');
						var node = drag.get('node');

						var clone = proxyNode.get('firstChild');

						if (!clone) {
							clone = node.clone().empty();

							clone.addClass('portlet-tags-admin-helper');

							proxyNode.appendChild(clone);
						}

						clone.html(node.html());
					},

					_alternateRows: function() {
						var instance = this;

						var tagsScope = A.all(instance._tagsContainerSelector);

						var allRows = tagsScope.all('li');

						allRows.removeClass('alt');
						allRows.odd().addClass('alt');
					},

					_attachPropertyIconEvents: function(property) {
						var instance = this;

						var addProperty = property.one('.add-property');
						var deleteProperty = property.one('.delete-property');

						if (addProperty) {
							addProperty.on(
								'click',
								function() {
									instance._addProperty(property, '', '');
								}
							);
						}

						if (deleteProperty) {
							deleteProperty.on(
								'click',
								function() {
									instance._removeProperty(property);
								}
							);
						}
					},

					_buildProperties: function() {
						var instance = this;

						var buffer = [];

						A.all('.tag-property-row').each(
							function(item, index, collection) {
								if (!item.hasClass('aui-helper-hidden')) {
									var keyNode = item.one('input.property-key');
									var valueNode = item.one('input.property-value');

									if (keyNode && valueNode) {
										var rowValue = [keyNode.val(), ':', valueNode.val(), ','].join('');

										buffer.push(rowValue);
									}
								}
							}
						);

						return buffer.join('');
					},

					_closeEditSection: function() {
						var instance = this;

						instance._hideSection('.tag-edit');

						A.all(instance._tagsContainerCellsSelector).setStyle('width', 'auto');
					},

					_createPermissionURL: function(modelResource, modelResourceDescription, resourcePrimKey) {
						var instance = this;

						var portletURL = Liferay.PortletURL.createPermissionURL(
							instance.portletId, modelResource, modelResourceDescription, resourcePrimKey);

						return portletURL;
					},

					_deleteTag: function(tagId, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetTag.deleteTag(
							{
								tagId: tagId
							},
							callback
						);
					},

					_displayProperties: function(tagId) {
						var instance = this;

						instance._getProperties(
							tagId,
							function(properties) {
								if (!properties.length) {
									properties = [{ key: '', value: '' }];
								}

								var total = properties.length;
								var totalRendered = A.all('div.tag-property-row').size();

								if (totalRendered > total) {
									return;
								}

								A.each(
									properties,
									function(item, index, collection) {
										var baseRows = A.all('div.tag-property-row');
										var lastIndex = baseRows.size() - 1;
										var baseRow = baseRows.item(lastIndex);

										instance._addProperty(baseRow, item.key, item.value);
									}
								);
							}
						);
					},

					_displayTags: function(callback) {
						var instance = this;

						var tagMessages = A.one('#tag-messages');

						if (tagMessages) {
							tagMessages.hide();
						}

						instance._getTags(
							function(tags) {
								instance._displayTagsImpl(tags, callback);
							}
						);
					},

					_displayTagsImpl: function(tags, callback) {
						var instance = this;

						var buffer = [];
						var tagsContainer = A.one(instance._tagsContainerSelector);

						buffer.push('<ul>');

						A.each(
							tags,
							function(item, index, collection) {
								buffer.push('<li class="tag-item results-row" ');
								buffer.push('data-tag="');
								buffer.push(item.name);
								buffer.push('" data-tagId="');
								buffer.push(item.tagId);
								buffer.push('"><span><a href="javascript:;">');
								buffer.push(item.name);
								buffer.push('</a></span>');
								buffer.push('</li>');
							}
						);

						buffer.push('</ul>');

						if (!tags.length) {
							buffer = [];
							instance._sendMessage('info', Liferay.Language.get('no-tags-were-found'), '#tag-messages', true);
						}

						tagsContainer.html(buffer.join(''));

						instance._reloadSearch();

						var	tagsItems = A.all(instance._tagsItemsSelector);

						tagsItems.on(
							'click',
							function(event) {
								var tagId = instance._getTagId(event.currentTarget);

								instance._selectTag(tagId);
								instance._showSection('.tag-edit');
							}
						);

						tagsItems.each(
							function(item, index, collection) {
								var dd = new A.DD.Drag(
									{
										bubbleTargets: instance,
										node: item,
										target: true
									}
								);

								dd.plug(
									A.Plugin.DDProxy,
									{
										borderStyle: '0',
										moveOnEnd: false
									}
								);

								dd.plug(
									A.Plugin.DDConstrained,
									{
										constrain2node: tagsContainer
									}
								);

								dd.plug(
									A.Plugin.DDNodeScroll,
									{
										node: tagsContainer,
										scrollDelay: 100
									}
								);

								dd.removeInvalid('a');
							}
						);

						instance._alternateRows();

						if (callback) {
							callback();
						}
					},

					_getTag: function(tagId) {
						var instance = this;

						return A.one('li[data-tagId="' + tagId + '"]');
					},

					_getTagId: function(exp) {
						var instance = this;

						var elem = A.one(exp);

						if (elem) {
							var attr = elem.attr('data-tagId');
						}

						return attr;
					},

					_getTagName: function(exp) {
						var instance = this;

						var elem = A.one(exp);

						if (elem) {
							var attr = elem.attr('data-tag');
						}

						return attr;
					},

					_getPermissionsEnabled: function(type) {
						var instance = this;

						var buffer = [];
						var permissionsActions = A.one('.tag-permissions-actions');
						var permissions = permissionsActions.all('[name$=' + type + 'Permissions]');

						permissions.each(
							function(item, index, collection) {
								if (item.get('checked')) {
									buffer.push(item.val());
								}
							}
						);

						return buffer.join(',');
					},

					_getProperties: function(tagId, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetTagProperty.getTagProperties(
							{
								tagId: tagId
							},
							callback
						);
					},

					_getTags: function(callback) {
						var instance = this;

						instance._showLoading(instance._tagsContainerSelector);

						Liferay.Service.Asset.AssetTag.getGroupTags(
							{
								groupId: themeDisplay.getParentGroupId()
							},
							callback
						);
					},

					_hideAllMessages: function() {
						var instance = this;

						instance._tagsAdminContainer.one('.lfr-message-response').hide();
					},

					_hideLoading: function(exp) {
						var instance = this;

						instance._tagsAdminContainer.one('div.loading-animation').remove();
					},

					_hideSection: function(exp) {
						var instance = this;

						var node = A.one(exp);

						if (node) {
							var parentNode = node.get('parentNode');

							if (parentNode) {
								parentNode.hide();
							}
						}
					},

					_hideToolbarOverlays: function() {
						var instance = this;

						instance._addTagOverlay.hide();
					},

					_loadData: function() {
						var instance = this;

						instance._closeEditSection();

						instance._displayTags(
							function() {
								instance._getTagId(instance._tagsItemsSelector);
							}
						);
					},

					_merge: function(node, dropNode) {
						var instance = this;

						var fromTagId = instance._getTagId(node);
						var fromTagName = instance._getTagName(node);
						var toTagId = instance._getTagId(dropNode);
						var toTagName = instance._getTagName(dropNode);

						var destination = toTagName;

						var mergeText = Liferay.Language.get('are-you-sure-you-want-to-merge-x-into-x');

						mergeText = A.substitute(mergeText, [instance._getTagName(node), destination]);

						if (confirm(mergeText)) {
							instance._mergeTags(
								fromTagId,
								toTagId,
								function() {
									node.remove();
									instance._selectTag(toTagId);
									instance._alternateRows();
								}
							);
						}
					},

					_mergeTags: function(fromId, toId, callback) {
						Liferay.Service.Asset.AssetTag.mergeTags(
							{
								fromTagId: fromId,
								toTagId: toId
							},
							callback
						);
					},

					_reloadSearch: function() {
						var	instance = this;

						var options = {
							data: function(node) {
								return node.one('span a').html();
							},
							input: '#tags-admin-search-input',
							nodes: instance._tagsItemsSelector
						};

						if (instance.liveSearch) {
							instance.liveSearch.destroy();
						}

						instance.liveSearch = new A.LiveSearch(options);
					},

					_removeProperty: function(property) {
						var instance = this;

						if (A.all('div.tag-property-row').size() > 2) {
							property.remove();
						}
					},

					_resetActionValues: function() {
						var instance = this;

						A.all('.tags-admin-actions input[type=text]').val('');
					},

					_saveProperties: function() {
						var instance = this;

						var tagId = instance._selectedTagId;
						var tagNameNode = A.one('.tag-edit input.tag-name');
						var tagName = (tagNameNode && tagNameNode.val()) || instance._selectedTagName;
						var properties = instance._buildProperties();

						instance._updateTag(tagId, tagName, properties);
						instance._displayTags();
					},

					_selectTag: function(tagId) {
						var instance = this;

						var tag = instance._getTag(tagId);
						var tagId = instance._getTagId(tag);
						var tagName = instance._getTagName(tag);

						instance._selectedTagId = tagId;
						instance._selectedTagName = tagName;

						if (!tagId) {
							return tag;
						}

						instance._unselectAllTags();
						tag.addClass('selected');

						var editContainer = A.one('.tag-edit');
						var tagNameField = editContainer.one('input.tag-name');

						if (tagNameField) {
							tagNameField.val(tagName);
						}

						instance._displayProperties(tagId);

						instance._selectedTag = tag;

						return tag;
					},

					_sendMessage: function(type, message) {
						var instance = this;

						var output = instance._portletMessageContainer;
						var typeClass = 'portlet-msg-' + type;

						clearTimeout(instance._messageTimeout);

						output.removeClass('portlet-msg-error portlet-msg-success');
						output.addClass(typeClass);
						output.html(message);
						output.show();

						instance._messageTimeout = setTimeout(
							function() {
								output.hide();

								instance._addTagOverlay.refreshAlign();
							},
						7000);
					},

					_showLoading: function(container) {
						var instance = this;

						A.all(container).html('<div class="loading-animation" />');
					},

					_showSection: function(exp) {
						var instance = this;

						var element = A.one(exp);

						if (element) {
							var parentNode = element.get('parentNode');

							if (parentNode) {
								parentNode.show();
								element.one('input').focus();

								A.all(instance._tagsContainerCellsSelector).setStyle('width', '50%');
							}
						}
					},

					_unselectAllTags: function() {
						var instance = this;

						A.all(instance._tagsItemsSelector).removeClass('selected');

						var properties = A.all('div.tag-property-row');

						if (properties.size() > 1) {
							properties.each(
								function(item, index, collection) {
									if (index > 0) {
										item.remove();
									}
								}
							);
						}
					},

					_updateTag: function(tagId, name, properties, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetTag.updateTag(
							{
								tagId: tagId,
								name: name,
								properties: properties,
								serviceContext: null
							},
							function(message) {
								var exception = message.exception;

								if (!exception) {
									var selectedText = instance._selectedTag.one('> span > a');

									if (!selectedText) {
										selectedText = instance._selectedTag.one('> span');
									}

									if (selectedText) {
										instance._selectedTag.attr('data-tag', name);
										selectedText.text(name);

										instance._closeEditSection();
									}
								}
								else {
									var errorText = '';

									if (exception.indexOf('auth.PrincipalException') > -1) {
										errorText = Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource');
									}
									else if (exception.indexOf('Exception') > -1) {
										errorText = Liferay.Language.get('one-of-your-fields-contains-invalid-characters');
									}

									if (errorText) {
										instance._sendMessage('error', errorText);
									}
								}

								if (callback) {
									callback(message);
								}
							}
						);
					},

					_selectedTag: null,
					_selectedTagName: null,
					_tagsContainerCellsSelector: '.portlet-tags-admin .tags-admin-content td',
					_tagsContainerSelector: '.tags',
					_tagsItemsSelector: '.tags li'
				}
			}
		);

		Liferay.Portlet.AssetTagsAdmin = AssetTagsAdmin;
	},
	'',
	{
		requires: ['aui-live-search', 'aui-overlay-context-panel', 'base', 'dd', 'json', 'liferay-portlet-url', 'substitute']
	}
);