AUI().add(
	'liferay-category-admin',
	function(A) {
		var AssetCategoryAdmin = A.Component.create(
			{
				NAME: 'assetcategoryadmin',

				EXTENDS: A.Base,

				constructor: function() {
					AssetCategoryAdmin.superclass.constructor.call(this);
				},

				prototype: {
					initializer: function(portletId) {
						var instance = this;

						var childrenContainer = A.one(instance._categoryContainerSelector);

						instance.portletId = portletId;
						instance._container = A.one('.vocabulary-container');

						A.all('.vocabulary-close').on(
							'click',
							function() {
								instance._closeEditSection();
							}
						);

						A.all('.vocabulary-save-category-properties').on(
							'click',
							function() {
								instance._saveCategoryProperties();
							}
						);

						instance._portletMessageContainer = A.Node.create('<div class="aui-helper-hidden lfr-message-response" id="vocabulary-messages" />');
						instance._categoryMessageContainer = A.Node.create('<div class="aui-helper-hidden lfr-message-response" id="vocabulary-category-messages" />');

						instance._container.placeBefore(instance._portletMessageContainer);
						childrenContainer.placeBefore(instance._categoryMessageContainer);

						var buttons = A.all('.vocabulary-buttons');
						var toolbar = A.all('.vocabulary-toolbar');

						var addCategoryLayer = A.one('.add-category-layer');
						var addVocabularyLayer = A.one('.add-vocabulary-layer');

						instance._toolbarCategoryPanel = new A.OverlayContextPanel(
							{
								align: {
									points: ['tr', 'br']
								},
								bodyContent: addCategoryLayer,
								trigger: '.add-category-button'
							}
						).render();

						instance._vocabularyCategoryPanel = new A.OverlayContextPanel(
							{
								align: {
									points: ['tr', 'br']
								},
								bodyContent: addVocabularyLayer,
								trigger: '.add-vocabulary-button'
							}
						).render();

						instance._vocabularyCategoryPanel.after(
							'visibleChange',
							function(event) {
								if (event.newVal) {
									instance._showToolBarVocabularySection();
								}
							}
						);

						instance._toolbarCategoryPanel.after(
							'visibleChange',
							function(event) {
								if (event.newVal) {
									instance._showToolBarCategorySection();
								}
							}
						);

						A.one('.permissions-categories-button').on(
							'click',
							function() {
								var categoryName = instance._selectedCategoryName;
								var categoryId = instance._selectedCategoryId;

								if (categoryName && categoryId) {
									var portletURL = instance._createPermissionURL(
										'com.liferay.portlet.asset.model.AssetCategory',
										categoryName,
										categoryId);

									submitForm(document.hrefFm, portletURL.toString());
								}
								else {
									instance._showToolBarCategorySection();
								}
							}
						);

						A.one('.permissions-vocabulary-button').on(
							'click',
							function() {
								var vocabularyName = instance._selectedVocabularyName;
								var vocabularyId = instance._selectedVocabularyId;

								if (vocabularyName && vocabularyId) {
									var portletURL = instance._createPermissionURL(
										'com.liferay.portlet.asset.model.AssetVocabulary',
										vocabularyName,
										vocabularyId);

									submitForm(document.hrefFm, portletURL.toString());
								}
								else {
									instance._showToolBarVocabularySection();
								}
							}
						);

						A.one('#vocabulary-select-search').on(
							'change',
							function(event) {
								var searchInput = A.one('#vocabulary-search-input');

								if (searchInput) {
									searchInput.focus();
								}

								instance._reloadSearch();
							}
						);

						var addCategory = function() {
							var categoryNameNode = addCategoryLayer.one('.vocabulary-category-name');
							var vocabularySelectNode = addCategoryLayer.one('.vocabulary-select-list');

							var categoryName = (categoryNameNode && categoryNameNode.val()) || '';
							var vocabularyId = (vocabularySelectNode && vocabularySelectNode.val()) || '';

							instance._hideAllMessages();
							instance._addCategory(categoryName, vocabularyId);
						};

						var addVocabulary = function() {
							var inputVocabularyNameNode = addVocabularyLayer.one('.vocabulary-name');
							var newVocabularyName = (inputVocabularyNameNode && inputVocabularyNameNode.val()) || '';

							instance._hideAllMessages();
							instance._addVocabulary(newVocabularyName);
						};

						A.one('input.category-save-button').on('click', addCategory);
						A.one('input.vocabulary-save-button').on('click', addVocabulary);

						A.all('.vocabulary-actions input').on(
							'keyup',
							function(event) {
								if (event.keyCode == 13) {
									var input = event.currentTarget;

									if (input.hasClass('vocabulary-category-name')) {
										addCategory();
									}
									else if (input.hasClass('vocabulary-name')) {
										addVocabulary();
									}

									event.halt();
								}
							}
						);

						A.one('input.vocabulary-delete-categories-button').on(
							'click',
							function() {
								if (confirm(Liferay.Language.get('are-you-sure-you-want-to-delete-this-category'))) {
									instance._deleteCategory(
										instance._selectedCategoryId,
										function(message) {
											var exception = message.exception;

											if (!exception) {
												instance._closeEditSection();
												instance._hideToolbarSections();
												instance._displayVocabularyCategories(instance._selectedVocabularyId);
											}
											else {
												if (exception.indexOf('auth.PrincipalException') > -1) {
													instance._sendMessage('error', Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource'));
												}
											}
										}
									);
								}
							}
						);

						A.one('input.vocabulary-delete-list-button').on(
							'click',
							function() {
								if (confirm(Liferay.Language.get('are-you-sure-you-want-to-delete-this-list'))) {
									instance._deleteVocabulary(
										instance._selectedVocabularyId,
										function(message) {
											var exception = message.exception;
											if (!exception) {
												instance._closeEditSection();
												instance._hideToolbarSections();
												instance._loadData();
											}
											else {
												if (exception.indexOf('auth.PrincipalException') > -1) {
													instance._sendMessage('error', Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource'));
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
								instance._hideToolbarSections();
							}
						);

						A.all('.aui-overlay input[type=text]').on(
							'keyup',
							function(event) {
								var ESC_KEY_CODE = 27;
								var keyCode = event.keyCode;

								if (keyCode == ESC_KEY_CODE) {
									instance._hideToolbarSections();
								}
							}
						);

						instance._loadData();

						instance.after('drop:hit', instance._afterDragDrop);
						instance.after('drop:enter', instance._afterDragEnter);
						instance.after('drop:exit', instance._afterDragExit);
					},

					_afterDragDrop: function(event) {
						var instance = this;

						var dragNode = event.drag.get('node');
						var dropNode = event.drop.get('node');

						var node = A.Widget.getByNode(dragNode);

						var vocabularyId = dropNode.attr('data-vocabularyid');
						var fromCategoryId = instance._getCategoryId(node);
						var fromCategoryName = instance._getCategoryName(node);

						instance._merge(fromCategoryId, fromCategoryName, 0, vocabularyId);

						instance._selectVocabulary(vocabularyId);

						dropNode.removeClass('active-area');
					},

					_afterDragEnter: function(event) {
						var instance = this;

						var dropNode = event.drop.get('node');

						dropNode.addClass('active-area');
					},

					_afterDragExit: function(event) {
						var instance = this;

						var dropNode = event.target.get('node');

						dropNode.removeClass('active-area');
					},

					_createPermissionURL: function(modelResource, modelResourceDescription, resourcePrimKey) {
						var instance = this;

						var portletURL = Liferay.PortletURL.createPermissionURL(
							instance.portletId, modelResource, modelResourceDescription, resourcePrimKey);

						return portletURL;
					},

					_displayVocabularyCategoriesImpl: function(categories, callback) {
						var instance = this;

						var buffer = [];

						var childrenList = A.one(instance._categoryContainerSelector);
						var boundingBox = A.Node.create('<div class="vocabulary-treeview-container" id="vocabularyTreeContainer"></div>');

						childrenList.empty();
						childrenList.append(boundingBox);

						if (instance.treeView) {
							instance.treeView.destroy();
						}

						instance.treeView = new VocabularyTree(
							{
								boundingBox: boundingBox,
								on: {
									dropAppend: function(event) {
										var tree = event.tree;
										var fromCategoryId = instance._getCategoryId(tree.dragNode);
										var fromCategoryName = instance._getCategoryName(tree.dragNode);
										var toCategoryId = instance._getCategoryId(tree.dropNode);
										var toCategoryName = instance._getCategoryName(tree.dropNode);
										var vocabularyId = instance._selectedVocabularyId;

										instance._merge(fromCategoryId, fromCategoryName, toCategoryId, vocabularyId);
									},
									dropInsert: function(event) {
										var tree = event.tree;
										var parentNode = tree.dropNode.get('parentNode');
										var fromCategoryId = instance._getCategoryId(tree.dragNode);
										var fromCategoryName = instance._getCategoryName(tree.dragNode);
										var toCategoryId = instance._getCategoryId(parentNode);
										var toCategoryName = instance._getCategoryName(parentNode);
										var vocabularyId = instance._selectedVocabularyId;

										instance._merge(fromCategoryId, fromCategoryName, toCategoryId, vocabularyId);
									}
								},
								type: 'normal'
							}
						).render();

						instance._buildCategoryTreeview(categories, 0);

						instance._reloadSearch();

						var vocabularyContainer = A.one(instance._vocabularyContainerSelector);
						var listLinks = vocabularyContainer.all('li');

						listLinks.unplug(A.Plugin.Drop);

						listLinks.plug(
							A.Plugin.Drop,
							{
								bubbleTargets: [instance, instance.treeView]
							}
						);

						if (callback) {
							callback();
						}
					},

					_displayList: function(callback) {
						var instance = this;

						var buffer = [];
						var list = A.one(instance._vocabularyContainerSelector);

						instance._showLoading('.vocabulary-categories, .vocabulary-list');

						buffer.push('<ul>');

						instance._getVocabularies(
							function(vocabularies) {
								A.each(
									vocabularies,
									function(item, index, collection) {
										buffer.push('<li');
										buffer.push(' class="vocabulary-category results-row');

										if (index == 0) {
											buffer.push(' selected ');
										}

										buffer.push('" data-vocabulary="');
										buffer.push(item.name);
										buffer.push('" data-vocabularyId="');
										buffer.push(item.vocabularyId);
										buffer.push('"><span><a href="javascript:;">');
										buffer.push(item.name);
										buffer.push('</a></span>');
										buffer.push('</li>');
									}
								);

								buffer.push('</ul>');

								list.html(buffer.join(''));

								var firstVocabulary = A.one(instance._vocabularyItemSelector);
								var vocabularyName = instance._getVocabularyName(firstVocabulary);
								var vocabularyId = instance._getVocabularyId(firstVocabulary);

								instance._selectedVocabularyName = vocabularyName;
								instance._selectedVocabularyId = vocabularyId;
								instance._feedVocabularySelect(vocabularies, vocabularyId);

								var listLinks = list.all('li');

								listLinks.on(
									'mousedown',
									function(event) {
										var vocabularyId = instance._getVocabularyId(event.currentTarget);

										instance._selectVocabulary(vocabularyId);
									}
								);

								var editableConfig = {
									eventType: 'dblclick',
									on: {
										contentTextChange: function(event) {
											if (!event.initial) {
												var editable = event.target;

												var vocabularyName = event.newVal;
												var vocabularyId = instance._selectedVocabularyId;

												var li = this.get('node').ancestor('li');

												li.setAttribute('data-vocabulary', event.newVal);

												instance._updateVocabulary(
													vocabularyId,
													vocabularyName,
													function(message) {
														var exception = message.exception;
														if (exception) {
															event.newVal = event.prevVal;

															editable._syncContentText(event);

															editable.set(
																'contentText',
																event.newVal,
																{
																	initial: true
																}
															);

															if (exception.indexOf('auth.PrincipalException') > -1) {
																instance._sendMessage('error', Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource'));
															}
															else if (exception.indexOf('VocabularyNameException') > -1) {
																instance._sendMessage('error', Liferay.Language.get('one-of-your-fields-contains-invalid-characters'));
															}
														}
														else {
															instance._displayList(
																function() {
																	var vocabulary = instance._selectVocabulary(message.vocabularyId);

																	instance._displayVocabularyCategories(instance._selectedVocabularyId);
																}
															);
														}
													}
												);
											}
										}
									}
								};

								var listEls = A.all('.vocabulary-list li span a');
								var listLength = listEls.size();

								for (var i = 0; i < listLength; i++) {
									editableConfig.node = listEls.item(i);

									new A.Editable(editableConfig);
								}

								if (callback) {
									callback();
								}
							}
						);
					},

					_displayCategoryProperties: function(categoryId) {
						var instance = this;

						instance._getCategoryProperties(
							categoryId,
							function(categoryProperties) {
								if (!categoryProperties.length) {
									categoryProperties = [{ key: '', value: '' }];
								}

								var total = categoryProperties.length;
								var totalRendered = A.all('div.vocabulary-property-row').size();

								if (totalRendered > total) {
									return;
								}

								A.each(
									categoryProperties,
									function(item, index, collection) {
										var baseCategoryRows = A.all('div.vocabulary-property-row');
										var lastIndex = baseCategoryRows.size() - 1;
										var baseRow = baseCategoryRows.item(lastIndex);

										instance._addCategoryProperty(baseRow, item.key, item.value);
									}
								);
							}
						);
					},

					_displayVocabularyCategories: function(vocabularyId, callback) {
						var instance = this;

						var categoryMessages = A.one('#vocabulary-category-messages');

						if (categoryMessages) {
							categoryMessages.hide();
						}

						instance._getVocabularyCategories(
							vocabularyId,
							function(categories) {
								instance._displayVocabularyCategoriesImpl(categories, callback);
							}
						);
					},

					_addCategory: function(categoryName, vocabularyId, callback) {
						var instance = this;

						var communityPermission = instance._getPermissionsEnabled('category', 'community');
						var guestPermission = instance._getPermissionsEnabled('category', 'guest');

						var titleMap = {};

						titleMap[themeDisplay.getDefaultLanguageId()] = categoryName;

						Liferay.Service.Asset.AssetCategory.addCategory(
							{
								parentCategoryId: 0,
								titleMap: A.JSON.stringify(titleMap),
								vocabularyId: vocabularyId,
								properties: [],
								serviceContext: A.JSON.stringify(
									{
										communityPermissions: communityPermission,
										guestPermissions: guestPermission,
										scopeGroupId: themeDisplay.getParentGroupId()
									}
								)
							},
							function(message) {
								var exception = message.exception;

								if (!exception && message.categoryId) {
									instance._sendMessage('success', Liferay.Language.get('your-request-processed-successfully'));

									instance._selectVocabulary(vocabularyId);

									instance._displayVocabularyCategories(
										instance._selectedVocabularyId,
										function() {
											instance._hideSection('.vocabulary-edit');
										}
									);

									instance._resetActionValues();
									instance._hideToolbarSections();

									if (callback) {
										callback(categoryName, vocabularyId);
									}
								}
								else {
									var errorKey = '';

									if (exception.indexOf('DuplicateCategoryException') > -1) {
										errorKey = 'that-category-already-exists';
									}
									else if ((exception.indexOf('CategoryNameException') > -1) ||
											 (exception.indexOf('AssetCategoryException') > -1)) {
										errorKey = 'one-of-your-fields-contains-invalid-characters';
									}
									else if (exception.indexOf('NoSuchVocabularyException') > -1) {
										errorKey = 'that-vocabulary-does-not-exist';
									}
									else if (exception.indexOf('auth.PrincipalException') > -1) {
										errorKey = 'you-do-not-have-permission-to-access-the-requested-resource';
									}
									if (errorKey) {
										instance._sendMessage('error', Liferay.Language.get(errorKey));
									}
								}
							}
						);
					},

					_addCategoryProperty: function(baseNode, key, value) {
						var instance = this;

						var baseCategoryProperty = A.one('div.vocabulary-property-row');

						if (baseCategoryProperty) {
							var newCategoryProperty = baseCategoryProperty.clone();

							var propertyKeyNode = newCategoryProperty.one('.category-property-key');
							var propertyValueNode = newCategoryProperty.one('.category-property-value');

							if (propertyKeyNode) {
								propertyKeyNode.val(key);
							}

							if (propertyValueNode) {
								propertyValueNode.val(value);
							}

							baseNode.placeAfter(newCategoryProperty);

							newCategoryProperty.show();

							if (!key && !value) {
								newCategoryProperty.one('input').focus();
							}

							instance._attachCategoryPropertyIconEvents(newCategoryProperty);
						}
					},

					_addVocabulary: function(vocabularyName, callback) {
						var instance = this;

						var communityPermission = instance._getPermissionsEnabled('vocabulary', 'community');
						var guestPermission = instance._getPermissionsEnabled('vocabulary', 'guest');

						var titleMap = {};

						titleMap[themeDisplay.getDefaultLanguageId()] = vocabularyName;

						Liferay.Service.Asset.AssetVocabulary.addVocabulary(
							{
								title: A.JSON.stringify(titleMap),
								description: '',
								settings: '',
								serviceContext: A.JSON.stringify(
									{
										communityPermissions: communityPermission,
										guestPermissions: guestPermission,
										scopeGroupId: themeDisplay.getParentGroupId()
									}
								)
							},
							function(message) {
								var exception = message.exception;

								if (!message.exception) {
									instance._sendMessage('success', Liferay.Language.get('your-request-processed-successfully'));

									instance._displayList(
										function() {
											var vocabulary = instance._selectVocabulary(message.vocabularyId);

											instance._displayVocabularyCategories(instance._selectedVocabularyId);

											if (vocabulary) {
												var scrollTop = vocabulary.get('region').top;

												A.one(instance._vocabularyContainerSelector).set('scrollTop', scrollTop);
											}
										}
									);

									instance._resetActionValues();

									if (callback) {
										callback(vocabulary);
									}
								}
								else {
									var errorKey = '';

									if (exception.indexOf('DuplicateVocabularyException') > -1) {
										errorKey = 'that-vocabulary-already-exists';
									}
									else if (exception.indexOf('VocabularyNameException') > -1) {
										errorKey = 'one-of-your-fields-contains-invalid-characters';
									}
									else if (exception.indexOf('NoSuchVocabularyException') > -1) {
										errorKey = 'that-parent-vocabulary-does-not-exist';
									}
									else if (exception.indexOf('auth.PrincipalException') > -1) {
										errorKey = 'you-do-not-have-permission-to-access-the-requested-resource';
									}

									if (errorKey) {
										instance._sendMessage('error', Liferay.Language.get(errorKey));
									}
								}
							}
						);
					},

					_alternateRows: function() {
						var instance = this;

						var categoriesScope = A.all(instance._categoryContainerSelector);

						var allRows = categoriesScope.all('li');

						allRows.removeClass('alt');
						allRows.odd().addClass('alt');
					},

					_attachCategoryPropertyIconEvents: function(categoryProperty) {
						var instance = this;

						var addProperty = categoryProperty.one('.add-category-property');
						var deleteProperty = categoryProperty.one('.delete-category-property');

						if (addProperty) {
							addProperty.on(
								'click',
								function() {
									instance._addCategoryProperty(categoryProperty, '', '');
								}
							);
						}

						if (deleteProperty) {
							deleteProperty.on(
								'click',
								function() {
									instance._removeCategoryProperty(categoryProperty);
								}
							);
						}
					},

					_buildCategoryTreeview: function(categories, parentCategoryId) {
						var instance = this;

						var children = instance._filterCategory(categories, parentCategoryId);

						A.each(
							children,
							function(item, index, collection) {
								var categoryId = item.categoryId;
								var hasChild = instance._filterCategory(categories, categoryId).length;

								var node = new A.TreeNode(
									{
										alwaysShowHitArea: false,
										id: 'categoryNode' + item.categoryId,
										label: item.name,
										leaf: false,
										on: {
											select: function(event) {
												var nodeId = event.target.get('id');
												var categoryId = nodeId.replace('categoryNode', '');
												var editContainer = A.one('.vocabulary-edit');

												instance._selectCategory(categoryId);
												instance._showSection(editContainer);
											}
										}
									}
								);

								var parentId = 'categoryNode' + parentCategoryId;
								var parentNode = instance.treeView.getNodeById(parentId) || instance.treeView;

								parentNode.appendChild(node);

								if (hasChild) {
									instance._buildCategoryTreeview(categories, categoryId);
								}
							}
						);

						return children.length;
					},

					_buildCategoryProperties: function() {
						var instance = this;

						var buffer = [];

						A.all('.vocabulary-property-row').each(
							function(item, index, collection) {
								if (!item.hasClass('aui-helper-hidden')) {
									var keyNode = item.one('input.category-property-key');
									var valueNode = item.one('input.category-property-value');

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

						instance._hideSection('.vocabulary-edit');

						A.all(instance._categoryContainerCellsSelector).setStyle('width', 'auto');
					},

					_deleteCategory: function(categoryId, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetCategory.deleteCategory(
							{
								categoryId: categoryId
							},
							callback
						);
					},

					_deleteVocabulary: function(vocabularyId, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetVocabulary.deleteVocabulary(
							{
								vocabularyId: vocabularyId
							},
							callback
						);
					},

					_feedVocabularySelect: function(vocabularies, defaultValue) {
						var instance = this;

						var select = A.one('select.vocabulary-select-list');

						if (select) {
							var buffer = [];

							A.each(
								vocabularies,
								function(item, index, collection) {
									var selected = (item.vocabularyId == defaultValue);

									buffer.push('<option');
									buffer.push(selected ? ' selected ' : '');
									buffer.push(' value="');
									buffer.push(item.vocabularyId);
									buffer.push('">');
									buffer.push(item.name);
									buffer.push('</option>');
								}
							);

							select.empty().append(buffer.join(''));
						}
					},

					_filterCategory: function(categories, parentCategoryId) {
						var instance = this;

						return A.Array.filter(
							categories,
							function(item, index, collection) {
								return (item.parentCategoryId == parentCategoryId);
							}
						);
					},

					_getCategory: function(categoryId) {
						var instance = this;

						return A.Widget.getByNode('#categoryNode' + categoryId);
					},

					_getCategoryId: function(node) {
						var instance = this;

						var nodeId = node.get('id') || '';
						var categoryId = nodeId.replace('categoryNode', '');

						if (A.Lang.isGuid(categoryId)) {
							categoryId = '';
						}

						return categoryId;
					},

					_getCategoryName: function(node) {
						var instance = this;

						return node.get('label');
					},

					_getParentCategoryId: function(node) {
						var instance = this;

						var parentNode = node.get('parentNode');

						return instance._getCategoryId(parentNode);
					},

					_getPermissionsEnabled: function(vocabularyType, type) {
						var instance = this;

						var buffer = [];
						var permissionsActions = A.one('.' + vocabularyType + '-permissions-actions');
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

					_getCategoryProperties: function(categoryId, callback) {
						var instance = this;

						Liferay.Service.Asset.AssetCategoryProperty.getCategoryProperties(
							{
								categoryId: categoryId
							},
							callback
						);
					},

					_getVocabularies: function(callback) {
						var instance = this;

						Liferay.Service.Asset.AssetVocabulary.getGroupVocabularies(
							{
								groupId: themeDisplay.getParentGroupId()
							},
							callback
						);
					},

					_getVocabulary: function(vocabularyId) {
						var instance = this;

						return A.one('li[data-vocabularyId="' + vocabularyId + '"]');
					},

					_getVocabularyCategories: function(vocabularyId, callback) {
						var instance = this;

						instance._showLoading(instance._categoryContainerSelector);

						Liferay.Service.Asset.AssetCategory.getVocabularyCategories(
							{
								vocabularyId: vocabularyId,
								start: -1,
								end: -1,
								obc: null
							},
							callback
						);
					},

					_getVocabularyId: function(exp) {
						var instance = this;

						return A.one(exp).attr('data-vocabularyId');
					},

					_getVocabularyName: function(exp) {
						var instance = this;

						return A.one(exp).attr('data-vocabulary');
					},

					_hideAllMessages: function() {
						var instance = this;

						instance._container.one('.lfr-message-response').hide();
					},

					_hideLoading: function(exp) {
						var instance = this;

						instance._container.one('div.loading-animation').remove();
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

					_hideToolbarSections: function() {
						var instance = this;

						instance._toolbarCategoryPanel.hide();
						instance._vocabularyCategoryPanel.hide();
					},

					_loadData: function() {
						var instance = this;

						instance._closeEditSection();

						instance._displayList(
							function() {
								instance._displayVocabularyCategories(instance._selectedVocabularyId);
							}
						);
					},

					_merge: function(fromCategoryId, fromCategoryName, toCategoryId, vocabularyId) {
						var instance = this;

						var categoryProperties = instance._buildCategoryProperties();

						vocabularyId = vocabularyId || instance._selectedVocabularyId;

						instance._updateCategory(fromCategoryId, vocabularyId, toCategoryId, fromCategoryName, categoryProperties);
					},

					_reloadSearch: function() {
						var	instance = this;

						var options = {
							input: '#vocabulary-search-input'
						};

						var vocabularySelectSearchNode = A.one('#vocabulary-select-search');
						var selected = (vocabularySelectSearchNode && vocabularySelectSearchNode.val()) || '';

						var input = A.one('#vocabulary-search-input');
						var vocabularyList = A.all(instance._vocabularyItemSelector);

						if (/vocabularies/.test(selected)) {
							A.mix(
								options,
								{
									data: function(node) {
										return node.one('a').html();
									},
									nodes: instance._vocabularyItemSelector
								}
							);
						}
						else {
							A.mix(
								options,
								{
									after: {
										search: function(event) {
											var results = event.liveSearch.results;

											A.each(
												results,
												function(item, index, collection) {
													var nodeWidget = A.Widget.getByNode(item.node);
													var nodeVisible = nodeWidget.get('boundingBox').hasClass('aui-helper-hidden');

													if (!nodeVisible) {
														nodeWidget.eachParent(
															function(parent) {
																parent.get('boundingBox').show();
															}
														);
													}
												}
											);
										}
									},
									data: function(node) {
										return node.one('.aui-tree-label').html();
									},
									nodes: instance._categoryItemSelector
								}
							);
						}

						if (instance.liveSearch) {
							instance.liveSearch.destroy();
						}

						instance.liveSearch = new A.LiveSearch(options);
					},

					_removeCategoryProperty: function(categoryProperty) {
						var instance = this;

						if (A.all('div.vocabulary-property-row').size() > 2) {
							categoryProperty.remove();
						}
					},

					_resetActionValues: function() {
						var instance = this;

						A.all('.vocabulary-actions input[type=text]').val('');

						instance._vocabularyCategoryPanel.hide();
					},

					_saveCategoryProperties: function() {
						var instance = this;

						var categoryId = instance._selectedCategoryId;
						var categoryNameNode = A.one('input.category-name');
						var categoryName = (categoryNameNode && categoryNameNode.val()) || instance._selectedCategoryName;
						var parentCategoryId = instance._selectedParentCategoryId;
						var categoryProperties = instance._buildCategoryProperties();
						var vocabularyId = instance._selectedVocabularyId;

						instance._updateCategory(categoryId, vocabularyId, parentCategoryId, categoryName, categoryProperties);
						instance._displayVocabularyCategories(vocabularyId);
					},

					_selectCurrentVocabulary: function(value) {
						var instance = this;

						var option = A.one('select.vocabulary-select-list option[value="' + value + '"]');

						if (option) {
							option.set('selected', true);
						}
					},

					_selectCategory: function(categoryId) {
						var instance = this;

						var category = instance._getCategory(categoryId);
						var categoryId = instance._getCategoryId(category);
						var categoryName = instance._getCategoryName(category);
						var parentCategoryId = instance._getParentCategoryId(category);

						instance._selectedCategoryId = categoryId;
						instance._selectedCategoryName = categoryName;
						instance._selectedParentCategoryId = parentCategoryId || 0;

						if (!categoryId) {
							return category;
						}

						var properties = A.all('div.vocabulary-property-row');
						var editContainer = A.one('.vocabulary-edit');
						var categoryNameField = editContainer.one('input.category-name');

						if (properties.size() > 1) {
							properties.each(
								function(item, index, collection) {
									if (index > 0) {
										item.remove();
									}
								}
							);
						}

						if (categoryNameField) {
							categoryNameField.val(categoryName);
						}

						instance._displayCategoryProperties(categoryId);

						instance._selectedCategory = category;

						return category;
					},

					_selectVocabulary: function(vocabularyId) {
						var instance = this;

						var vocabulary = instance._getVocabulary(vocabularyId);

						if (vocabulary) {
							var vocabularyName = instance._getVocabularyName(vocabulary);

							if (vocabulary.hasClass('selected')) {
								return vocabulary;
							}

							instance._hideAllMessages();
							instance._selectedVocabularyName = vocabularyName;
							instance._selectedVocabularyId = vocabularyId;
							instance._selectCurrentVocabulary(vocabularyId);

							instance._unselectAllVocabularies();
							instance._closeEditSection();

							vocabulary.addClass('selected');
							instance._displayVocabularyCategories(instance._selectedVocabularyId);
						}

						return vocabulary;
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

								instance._toolbarCategoryPanel.refreshAlign();
								instance._vocabularyCategoryPanel.refreshAlign();
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

								A.all(instance._categoryContainerCellsSelector).setStyle('width', '33%');
							}
						}
					},

					_showToolBarCategorySection: function() {
						var instance = this;

						var categoryPanel = instance._toolbarCategoryPanel;

						if (!instance._selectedVocabularyName) {
							instance._resetActionValues();

							categoryPanel.hide();

							instance._sendMessage('info', Liferay.Language.get('you-must-first-add-a-vocabulary'));

							instance._showToolBarVocabularySection();

							return;
						}

						categoryPanel.refreshAlign();

						instance._vocabularyCategoryPanel.hide();

						var inputCategoryNameNode = categoryPanel.get('contentBox').one('.vocabulary-category-name');

						Liferay.Util.focusFormField(inputCategoryNameNode);
					},

					_showToolBarVocabularySection: function() {
						var instance = this;

						var vocabularyCategoryPanel = instance._vocabularyCategoryPanel;

						vocabularyCategoryPanel.refreshAlign();

						instance._toolbarCategoryPanel.hide();

						var inputVocabularyNameNode = vocabularyCategoryPanel.get('contentBox').one('.vocabulary-name');

						Liferay.Util.focusFormField(inputVocabularyNameNode);
					},

					_unselectAllVocabularies: function() {
						var instance = this;

						A.all(instance._vocabularyItemSelector).removeClass('selected');
					},

					_updateCategory: function(categoryId, vocabularyId, parentCategoryId, name, categoryProperties, callback) {
						var instance = this;

						var titleMap = {};

						titleMap[themeDisplay.getDefaultLanguageId()] = name;

						Liferay.Service.Asset.AssetCategory.updateCategory(
							{
								categoryId: categoryId,
								parentCategoryId: parentCategoryId,
								titleMap: A.JSON.stringify(titleMap),
								vocabularyId: vocabularyId,
								properties: categoryProperties,
								serviceContext: null
							},
							function(message) {
								var exception = message.exception;

								if (!exception) {
									instance._selectedCategory.set('label', name);

									instance._closeEditSection();
								}
								else {
									var errorText = '';

									if (exception.indexOf('AssetCategoryNameException') > -1) {
										errorText = Liferay.Language.get('please-enter-a-valid-category-name');
									}
									else if (exception.indexOf('DuplicateCategoryException') > -1) {
										errorText = Liferay.Language.get('there-is-another-category-with-the-same-name-and-the-same-parent');
									}
									else if (exception.indexOf('NoSuchVocabularyException') > -1) {
										errorText = Liferay.Language.get('that-vocabulary-does-not-exist');
									}
									else if (exception.indexOf('NoSuchCategoryException') > -1) {
										errorText = Liferay.Language.get('that-parent-category-does-not-exist');
									}
									else if (exception.indexOf('auth.PrincipalException') > -1) {
										errorText = Liferay.Language.get('you-do-not-have-permission-to-access-the-requested-resource');
									}
									else if (exception.indexOf('Exception') > -1) {
										errorText = Liferay.Language.get('one-of-your-fields-contains-invalid-characters');
									}

									if (errorText) {
										instance._sendMessage('error', Liferay.Language.get(errorText));
									}
								}

								if (callback) {
									callback(message);
								}
							}
						);
					},

					_updateVocabulary: function(vocabularyId, vocabularyName, callback) {
						var titleMap = {};

						titleMap[themeDisplay.getDefaultLanguageId()] = vocabularyName;

						Liferay.Service.Asset.AssetVocabulary.updateVocabulary(
							{
								vocabularyId: vocabularyId,
								title: A.JSON.stringify(titleMap),
								description: '',
								settings: '',
								serviceContext: A.JSON.stringify(
									{
										scopeGroupId: themeDisplay.getParentGroupId()
									}
								)
							},
							callback
						);
					},

					_categoryItemSelector: '.vocabulary-categories .aui-tree-node',
					_categoryContainerCellsSelector: '.portlet-categories-admin .vocabulary-content td',
					_categoryContainerSelector: '.vocabulary-categories',
					_selectedCategoryName: null,
					_selectedVocabulary: null,
					_selectedVocabularyId: null,
					_selectedVocabularyName: null,
					_vocabularyItemSelector: '.vocabulary-list li',
					_vocabularyContainerSelector: '.vocabulary-list'
				}
			}
		);

		var VocabularyTree = A.Component.create(
			{
				NAME: 'VocabularyTree',

				EXTENDS: A.TreeViewDD,

				prototype: {
					_updateNodeState: function(event) {
						var instance = this;

						var dropNode = event.drop.get('node');

						if (dropNode && dropNode.hasClass('vocabulary-category')) {
							instance._appendState(dropNode);
						}
						else {
							VocabularyTree.superclass._updateNodeState.apply(instance, arguments);
						}
					}
				}
			}
		);

		Liferay.Portlet.AssetCategoryAdmin = AssetCategoryAdmin;
	},
	'',
	{
		requires: ['aui-editable', 'aui-live-search', 'aui-overlay-context-panel', 'aui-tree-view', 'dd', 'json', 'liferay-portlet-url']
	}
);