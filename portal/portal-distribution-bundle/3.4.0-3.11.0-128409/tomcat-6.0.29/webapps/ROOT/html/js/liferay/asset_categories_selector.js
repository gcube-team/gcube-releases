AUI().add(
	'liferay-asset-categories-selector',
	function(A) {
		var Lang = A.Lang;

		var EMPTY_FN = Lang.emptyFn;

		var	getClassName = A.ClassNameManager.getClassName;

		var CSS_TAGS_LIST = 'lfr-categories-selector-list';

		var NAME = 'categoriesselector';

		/**
		 * OPTIONS
		 *
		 * Required
		 * curEntryIds (string): The ids of the current categories.
		 * curEntries (string): The names of the current categories.
		 * instanceVar {string}: The instance variable for this class.
		 * hiddenInput {string}: The hidden input used to pass in the current categories.
		 */

		var AssetCategoriesSelector = A.Component.create(
			{
				ATTRS: {
					curEntryIds: {
						setter: function(value) {
							var instance = this;

							if (Lang.isString(value)) {
								value = value.split(',');
							}

							return value;
						},
						value: ''
					}
				},

				EXTENDS: Liferay.AssetTagsSelector,

				NAME: NAME,

				prototype: {
					UI_EVENTS: {},
					TREEVIEWS: {},

					renderUI: function() {
						var instance = this;

						AssetCategoriesSelector.superclass.constructor.superclass.renderUI.apply(instance, arguments);

						instance._renderIcons();

						instance.inputContainer.hide('aui-helper-hidden-accessible');
					},

					bindUI: function() {
						var instance = this;

						AssetCategoriesSelector.superclass.bindUI.apply(instance, arguments);
					},

					syncUI: function() {
						var instance = this;

						AssetCategoriesSelector.superclass.constructor.superclass.syncUI.apply(instance, arguments);

						var matchKey = instance.get('matchKey');

						instance.entries.getKey = function(obj) {
							return obj.categoryId;
						};

						var curEntries = instance.get('curEntries');
						var curEntryIds = instance.get('curEntryIds');

						A.each(
							curEntryIds,
							function(item, index, collection) {
								var entry = {
									categoryId: item
								};

								entry[matchKey] = curEntries[index];

								instance.entries.add(entry);
							}
						);
					},

					_afterTBLFocusedChange: EMPTY_FN,

					_bindTagsSelector: EMPTY_FN,

					_formatJSONResult: function(json) {
						var instance = this;

						var output = [];

						A.each(
							json,
							function(item, index, collection) {
								var checked = false;
								var treeId = 'category' + item.categoryId;

								if (instance.entries.findIndexBy('categoryId', item.categoryId) > -1) {
									checked = true;
								}

								var newTreeNode = {
									after: {
										check: A.bind(instance._onCheckboxCheck, instance),
										uncheck: A.bind(instance._onCheckboxUncheck, instance)
									},
									checked: checked,
									id: treeId,
									label: item.name,
									leaf: !item.hasChildren,
									type: 'check'
								};

								output.push(newTreeNode);
							}
						);

						return output;
					},

					_formatRequestData: function(treeNode) {
						var instance = this;

						var data = {};
						var assetId = instance._getTreeNodeAssetId(treeNode);
						var assetType = instance._getTreeNodeAssetType(treeNode);

						if (Lang.isValue(assetId)) {
							if (assetType == 'category') {
								data.categoryId = assetId;
							}
							else {
								data.vocabularyId = assetId;
							}
						}

						return data;
					},

					_getEntries: function(callback) {
						var instance = this;

						Liferay.Service.Asset.AssetVocabulary.getGroupsVocabularies(
							{
								groupIds: [themeDisplay.getParentGroupId(), themeDisplay.getCompanyGroupId()]
							},
							callback
						);
					},

					_getTreeNodeAssetId: function(treeNode) {
						var treeId = treeNode.get('id');
						var match = treeId.match(/(\d+)$/);

						return (match ? match[1] : null);
					},

					_getTreeNodeAssetType: function(treeNode) {
						var treeId = treeNode.get('id');
						var match = treeId.match(/^(vocabulary|category)/);

						return (match ? match[1] : null);
					},

					_initSearch: function() {
						var instance = this;

						var popup = instance._popup;

						var options = {
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
							input: popup.searchField.get('node'),
							nodes: popup.entriesNode.all('.aui-tree-node')
						};

						if (popup.liveSearch) {
							popup.liveSearch.destroy();
						}

						popup.liveSearch = new A.LiveSearch(options);
					},

					_onBoundingBoxClick: EMPTY_FN,

					_onCheckboxCheck: function(event) {
						var instance = this;

						var treeNode = event.currentTarget;
						var assetId = instance._getTreeNodeAssetId(treeNode);
						var matchKey = instance.get('matchKey');

						var entry = {
							categoryId: assetId
						};

						entry[matchKey] = treeNode.get('label');

						instance.entries.add(entry);
					},

					_onCheckboxUncheck: function(event) {
						var instance = this;

						var treeNode = event.currentTarget;
						var assetId = instance._getTreeNodeAssetId(treeNode);

						instance.entries.removeKey(assetId);
					},

					_renderIcons: function() {
						var instance = this;

						var contentBox = instance.get('contentBox');

						instance.icons = new A.Toolbar(
							{
								children: [
									{
										handler: {
											context: instance,
											fn: instance._showSelectPopup
										},
										icon: 'search',
										id: 'selectCategories',
										label: Liferay.Language.get('select')
									}
								]
							}
						).render(contentBox);

						var iconsBoundingBox = instance.icons.get('boundingBox');

						instance.entryHolder.placeAfter(iconsBoundingBox);
					},

					_showSelectPopup: function(event) {
						var instance = this;

						instance._showPopup(event);

						var popup = instance._popup;

						popup.set('title', Liferay.Language.get('categories'));

						popup.entriesNode.addClass(CSS_TAGS_LIST);

						instance._getEntries(
							function(entries) {
								popup.entriesNode.empty();

								A.each(entries, instance._vocabulariesIterator, instance);

								A.each(
									instance.TREEVIEWS,
									function(item, index, collection) {
										item.expandAll();
									}
								);
							}
						);

						if (instance._bindSearchHandle) {
							instance._bindSearchHandle.detach();
						}

						var searchField = popup.searchField.get('boundingBox');

						instance._bindSearchHandle = searchField.on('focus', A.bind(instance._initSearch, instance));
					},

					_vocabulariesIterator: function(item, index, collection) {
						var instance = this;

						var popup = instance._popup;
						var vocabularyName = item.name;
						var vocabularyId = item.vocabularyId;

						if (item.groupId == themeDisplay.getCompanyGroupId()) {
							vocabularyName += ' (' + Liferay.Language.get('global') + ')';
						}

						var treeId = 'vocabulary' + vocabularyId;

						var vocabularyRootNode = {
							alwaysShowHitArea: true,
							id: treeId,
							label: vocabularyName,
							leaf: false,
							type: 'io'
						};

						instance.TREEVIEWS[vocabularyId] = new A.TreeView(
							{
								children: [vocabularyRootNode],
								io: {
									cfg: {
										data: A.bind(instance._formatRequestData, instance)
									},
									formatter: A.bind(instance._formatJSONResult, instance),
									url: themeDisplay.getPathMain() + '/asset/get_categories'
								},
								paginator: {
									limit: 50,
									offsetParam: 'start'
								}
							}
						).render(popup.entriesNode);
					}
				}
			}
		);

		Liferay.AssetCategoriesSelector = AssetCategoriesSelector;
	},
	'',
	{
		requires: ['aui-tree', 'liferay-asset-tags-selector']
	}
);