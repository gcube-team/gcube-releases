Liferay.LayoutConfiguration = {
	toggle: function(){}
};

Liferay.provide(
	Liferay.LayoutConfiguration,
	'showTemplates',
	function() {
		var A = AUI();

		var url = themeDisplay.getPathMain() + '/layout_configuration/templates';

		var dialog = new A.Dialog(
			{
				centered: true,
				modal: true,
				title: Liferay.Language.get('layout'),
				width: 700
			}
		).render();

		dialog.plug(
			A.Plugin.IO,
			{
				data: {
					doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
					p_l_id: themeDisplay.getPlid(),
					redirect: Liferay.currentURL
				},
				uri: url
			}
		);
	},
	['aui-dialog']
);

AUI().add(
	'liferay-layout-configuration',
	function(A) {
		var DDM = A.DD.DDM;

		var LayoutConfiguration = {
			categories: [],
			portlets: [],
			showTimer: 0,

			init: function() {
				var instance = this;

				var menu = A.one('#portal_add_content');

				instance.menu = menu;

				if (menu) {
					instance.portlets = menu.all('.lfr-portlet-item');
					instance.categories = menu.all('.lfr-content-category');
					instance.categoryContainers = menu.all('.lfr-add-content');

					var data = function(node) {
						var id = node.attr('id');

						var dataNode = A.one('#' + id + 'CategoryPath');

						var value = (dataNode && dataNode.val()) || '';

						return [Liferay.Util.uncamelize(value), value].join(' ').toLowerCase();
					};

					var isVisible = function(item, index, collection) {
						return !item.hasClass('aui-helper-hidden');
					};

					new A.LiveSearch(
						{
							data: data,
							hide: function(node) {
								node.hide();
							},
							input: '#layout_configuration_content',
							nodes: '#portal_add_content .lfr-portlet-item',
							show: function(node) {
								node.show();

								var categoryParent = node.ancestorsByClassName('lfr-content-category');
								var contentParent = node.ancestorsByClassName('lfr-add-content');

								if (categoryParent) {
									categoryParent.show();
								}

								if (contentParent) {
									contentParent.replaceClass('collapsed', 'expanded');
									contentParent.show();
								}
							}
						}
					);

					new A.LiveSearch(
						{
							after: {
								search: function(event) {
									if (!this.query) {
										instance.categories.hide();
										instance.categoryContainers.replaceClass('expanded', 'collapsed');
										instance.categoryContainers.show();
									}

									if (this.query == '*') {
										instance.categories.show();
										instance.categoryContainers.replaceClass('collapsed', 'expanded');
										instance.categoryContainers.show();

										instance.portlets.show();
									}
								}
							},
							data: data,
							hide: function(node) {
								var children = node.all('.lfr-content-category > div');

								var action = 'hide';

								if (children.some(isVisible)) {
									action = 'show';
								}

								node[action]();
							},
							input: '#layout_configuration_content',
							nodes: '#portal_add_content .lfr-add-content'
						}
					);
				}
			},

			_addPortlet: function(portlet, options) {
				var instance = this;

				var portletMetaData = instance._getPortletMetaData(portlet);

				if (!portletMetaData.portletUsed) {
					var plid = portletMetaData.plid;
					var portletId = portletMetaData.portletId;
					var isInstanceable = portletMetaData.instanceable;

					if (!isInstanceable) {
						instance._disablePortletEntry(portletId);
					}

					var beforePortletLoaded = null;
					var onComplete = null;
					var placeHolder = A.Node.create('<div class="loading-animation" />');

					if (options) {
						var item = options.item;

						item.placeAfter(placeHolder);
						item.remove(true);

						beforePortletLoaded = options.beforePortletLoaded;
					}
					else {
						var layoutOptions = Liferay.Layout.options;
						var firstColumn = A.one(layoutOptions.dropNodes);

						if (firstColumn) {
							var dropColumn = firstColumn.one(layoutOptions.dropContainer);
							var referencePortlet = Liferay.Layout.findReferencePortlet(dropColumn);

							if (referencePortlet) {
								referencePortlet.placeBefore(placeHolder);
							}
							else {
								if (dropColumn) {
									dropColumn.append(placeHolder);
								}
							}
						}
					}

					var portletOptions = {
						beforePortletLoaded: beforePortletLoaded,
						onComplete: function(portletBoundary) {
							Liferay.Layout.syncDraggableClassUI();
							Liferay.Layout.updatePortletDropZones(portletBoundary);

							if (onComplete) {
								onComplete.apply(this, arguments);
							}
						},
						plid: plid,
						portletId: portletId,
						placeHolder: placeHolder
					};

					Liferay.Portlet.add(portletOptions);

					instance._loadPortletFiles(portletMetaData);
				}
			},

			_disablePortletEntry: function(portletId) {
				var instance = this;

				instance._eachPortletEntry(
					portletId,
					function(item, index) {
						item.addClass('lfr-portlet-used');
					}
				);
			},

			_eachPortletEntry: function(portletId, callback) {
				var instance = this;

				var portlets = A.all('[portletid=' + portletId + ']');

				portlets.each(callback);
			},

			_enablePortletEntry: function(portletId) {
				var instance = this;

				instance._eachPortletEntry(
					portletId,
					function(item, index) {
						item.removeClass('lfr-portlet-used');
					}
				);
			},

			_getPortletMetaData: function(portlet) {
				var instance = this;

				var portletMetaData = portlet._LFR_portletMetaData;

				if (!portletMetaData) {
					var instanceable = (portlet.attr('instanceable') == 'true');
					var plid = portlet.attr('plid');
					var portletId = portlet.attr('portletId');
					var portletUsed = portlet.hasClass('lfr-portlet-used');
					var footerPortalCssPaths = (portlet.attr('footerPortalCssPaths') || '').split(',');
					var footerPortalJavaScriptPaths = (portlet.attr('footerPortalJavaScriptPaths') || '').split(',');
					var footerPortletCssPaths = (portlet.attr('footerPortletCssPaths') || '').split(',');
		            var footerPortletJavaScriptPaths = (portlet.attr('footerPortletJavaScriptPaths') || '').split(',');
					var headerPortalCssPaths = (portlet.attr('headerPortalCssPaths') || '').split(',');
					var headerPortalJavaScriptPaths = (portlet.attr('headerPortalJavaScriptPaths') || '').split(',');
		            var headerPortletCssPaths = (portlet.attr('headerPortletCssPaths') || '').split(',');
					var headerPortletJavaScriptPaths = (portlet.attr('headerPortletJavaScriptPaths') || '').split(',');

					portletMetaData = {
						instanceable: instanceable,
						plid: plid,
						portletId: portletId,
						portalPaths: {
							footerPortalCssPaths: footerPortalCssPaths,
							footerPortalJavaScriptPaths: footerPortalJavaScriptPaths,
							headerPortalCssPaths: headerPortalCssPaths,
							headerPortalJavaScriptPaths: headerPortalJavaScriptPaths
						},
						portletPaths: {
							footerPortletCssPaths: footerPortletCssPaths,
							footerPortletJavaScriptPaths: footerPortletJavaScriptPaths,
							headerPortletCssPaths: headerPortletCssPaths,
							headerPortletJavaScriptPaths: headerPortletJavaScriptPaths
						},
						portletUsed: portletUsed
					};

					portlet._LFR_portletMetaData = portletMetaData;
				}

				return portletMetaData;
			},

			_loadContent: function() {
				var instance = this;

				Liferay.fire('initLayout');

				instance.init();

				Liferay.Util.addInputType();

				Liferay.on('closePortlet', instance._onPortletClose, instance);

				instance._portletItems = instance._dialogBody.all('div.lfr-portlet-item');

				var portlets = instance._portletItems;

				instance._dialogBody.delegate(
					'mousedown',
					function(event) {
						var link = event.currentTarget;
						var portlet = link.ancestor('.lfr-portlet-item');

						instance._addPortlet(portlet);
					},
					'a'
				);

				var portletItem = null;
				var layoutOptions = Liferay.Layout.options;

				var portletItemOptions = {
					delegateConfig: {
						container: '#portal_add_content',
						dragConfig: {
							clickPixelThresh: 0,
							clickTimeThresh: 0
						},
						invalid: '.lfr-portlet-used',
						target: false
					},
					dragNodes: '.lfr-portlet-item',
					dropContainer: function(dropNode) {
						return dropNode.one(layoutOptions.dropContainer);
					},
					on: Liferay.Layout.DEFAULT_LAYOUT_OPTIONS.on
				};

				if (layoutOptions.freeForm) {
					portletItem = new Liferay.Layout.FreeFormPortletItem(portletItemOptions);
				}
				else {
					portletItem = new Liferay.Layout.PortletItem(portletItemOptions);
				}

				if (Liferay.Browser.isIe()) {
					instance._dialogBody.delegate(
						'mouseenter',
						function(event) {
							event.currentTarget.addClass('over');
						},
						'.lfr-portlet-item'
					);

					instance._dialogBody.delegate(
						'mouseenter',
						function(event) {
							event.currentTarget.removeClass('over');
						},
						'.lfr-portlet-item'
					);
				}

				instance._dialogBody.delegate(
					'mousedown',
					function(event) {
						var heading = event.currentTarget.get('parentNode');
						var category = heading.one('> .lfr-content-category');

						if (category) {
							category.toggle();
						}

						if (heading) {
							heading.toggleClass('collapsed').toggleClass('expanded');
						}
					},
					'.lfr-add-content > h2'
				);

				Liferay.Util.focusFormField('#layout_configuration_content');
			},

			_loadPortletFiles: function(portletMetaData) {
				var instance = this;

				var headerPortalCssPaths = portletMetaData.portalPaths.headerPortalCssPaths;
				var footerPortalCssPaths = portletMetaData.portalPaths.footerPortalCssPaths;
				var headerPortletCssPaths = portletMetaData.portletPaths.headerPortletCssPaths;
				var footerPortletCssPaths = portletMetaData.portletPaths.footerPortletCssPaths;

				var headerPortalJavaScriptPaths = portletMetaData.portalPaths.headerPortalJavaScriptPaths;
				var footerPortalJavaScriptPaths = portletMetaData.portalPaths.footerPortalJavaScriptPaths;
				var headerPortletJavaScriptPaths = portletMetaData.portletPaths.headerPortletJavaScriptPaths;
				var footerPortletJavaScriptPaths = portletMetaData.portletPaths.footerPortletJavaScriptPaths;

				var head = A.one('head');
				var body = A.getBody();

				var headerCSS = headerPortalCssPaths.concat(headerPortletCssPaths);
				var footerCSS = footerPortalCssPaths.concat(footerPortletCssPaths);

				var headerJS = headerPortalJavaScriptPaths.concat(headerPortletJavaScriptPaths);
				var footerJS = footerPortalJavaScriptPaths.concat(footerPortletJavaScriptPaths);

				A.Get.css(
					headerCSS,
					{
						insertBefore: head.get('firstChild').getDOM(),
						onSuccess: function(event) {
							if (Liferay.Browser.isIe()) {
								A.all('body link').appendTo(head);

								A.all('link.lfr-css-file').each(
									function(item, index, collection) {
										document.createStyleSheet(item.get('href'));
									}
								);
							}
						}
					}
				);

				var lastChild = body.get('lastChild').getDOM();

				A.Get.css(
					footerCSS,
					{
						insertBefore: lastChild
					}
				);

				A.Get.script(headerJS);

				A.Get.script(
					footerJS,
					{
						insertBefore: lastChild
					}
				);
			},

			_onPortletClose: function(event) {
				var instance = this;

				var popup = A.one('#portal_add_content');

				if (popup) {
					var item = popup.one('.lfr-portlet-item[plid=' + event.plid + '][portletId=' + event.portletId + '][instanceable=false]');

					if (item && item.hasClass('lfr-portlet-used')) {
						var portletId = item.attr('portletId');

						instance._enablePortletEntry(portletId);
					}
				}
			}
		};

		var PROXY_NODE_ITEM = Liferay.Layout.PROXY_NODE_ITEM;

		var PortletItem = A.Component.create(
			{

				ATTRS: {
					lazyStart: {
						value: true
					},

					proxyNode: {
						value: PROXY_NODE_ITEM
					}
				},

				EXTENDS: Liferay.Layout.ColumnLayout,

				NAME: 'PortletItem',

				prototype: {
					PROXY_TITLE: PROXY_NODE_ITEM.one('.portlet-title'),

					bindUI: function() {
						var instance = this;

						PortletItem.superclass.bindUI.apply(this, arguments);

						instance.on('placeholderAlign', instance._onPlaceholderAlign);
					},

					_addPortlet: function(portletNode, options) {
						var instance = this;

						LayoutConfiguration._addPortlet(portletNode, options);
					},

					_getAppendNode: function() {
						var instance = this;

						instance.appendNode = DDM.activeDrag.get('node').clone();

						return instance.appendNode;
					},

					_onDragEnd: function(event) {
						var instance = this;

						PortletItem.superclass._onDragEnd.apply(this, arguments);

						var appendNode = instance.appendNode;

						if (appendNode && appendNode.inDoc()) {
							var portletNode = event.target.get('node');

							instance._addPortlet(
								portletNode,
								{
									item: instance.appendNode
								}
							);
						}
					},

					_onDragStart: function() {
						var instance = this;

						PortletItem.superclass._onDragStart.apply(this, arguments);

						instance._syncProxyTitle();
					},

					_onPlaceholderAlign: function(event) {
						var instance = this;

						var drop = event.drop;
						var portletItem = event.currentTarget;

						if (drop && portletItem) {
							var dropNodeId = drop.get('node').get('id');

							if (Liferay.Layout.EMPTY_COLUMNS[dropNodeId]) {
								portletItem.activeDrop = drop;
								portletItem.lazyEvents = false;
								portletItem.quadrant = 1;
							}
						}
					},

					_positionNode: function(event) {
						var instance = this;

						var portalLayout = event.currentTarget;
						var activeDrop = portalLayout.lastAlignDrop || portalLayout.activeDrop;

						if (activeDrop) {
							var dropNode = activeDrop.get('node');

							if (dropNode.isStatic) {
								var options = Liferay.Layout.options;
								var dropColumn = dropNode.ancestor(options.dropContainer);
								var foundReferencePortlet = Liferay.Layout.findReferencePortlet(dropColumn);

								if (!foundReferencePortlet) {
									foundReferencePortlet = Liferay.Layout.getLastPortletNode(dropColumn);
								}

								if (foundReferencePortlet) {
									var drop = DDM.getDrop(foundReferencePortlet);

									if (drop) {
										portalLayout.quadrant = 4;
										portalLayout.activeDrop = drop;
										portalLayout.lastAlignDrop = drop;
									}
								}
							}

							PortletItem.superclass._positionNode.apply(this, arguments);
						}
					},

					_syncProxyTitle: function() {
						var instance = this;

						var node = DDM.activeDrag.get('node');
						var title = node.attr('title');

						instance.PROXY_TITLE.html(title);
					}
				}
			}
		);

		var FreeFormPortletItem = A.Component.create(
			{
				ATTRS: {
					lazyStart: {
						value: false
					}
				},

				EXTENDS: PortletItem,

				NAME: 'FreeFormPortletItem',

				prototype: {
					initializer: function() {
						var instance = this;

						var placeholder = instance.get('placeholder');

						if (placeholder) {
							placeholder.addClass(Liferay.Layout.options.freeformPlaceholderClass);
						}
					}
				}
			}
		);

		Liferay.Layout.FreeFormPortletItem = FreeFormPortletItem;
		Liferay.Layout.PortletItem = PortletItem;

		A.mix(Liferay.LayoutConfiguration, LayoutConfiguration);
	},
	'',
	{
		requires: ['aui-live-search', 'dd', 'liferay-layout'],
		use: []
	}
);