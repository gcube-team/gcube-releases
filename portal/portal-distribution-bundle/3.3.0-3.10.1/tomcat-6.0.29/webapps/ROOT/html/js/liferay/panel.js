AUI().add(
	'liferay-panel',
	function(A) {

		/**
		 * OPTIONS
		 *
		 * Optional
		 * container {string|object}: A selector of the panel container if there are multiple panels handled by this one.
		 * panel {string|object}: A selector of the panel.
		 * panelContent {string|object}: A selector of the content section of the panel.
		 * header {string|object}: A selector of the panel's header area.
		 * titles {string|object}: A selector of the titles in the panel.
		 * footer {string|object}: A selector of the panel's footer area.
		 * accordion {boolean}: Whether or not the panels have accordion behavior (meaning only one panel can be open at a time).
		 * collapsible {boolean}: Whether or not the panel can be collapsed by clicking the title.
		 *
		 */

		var Panel = A.Component.create(
			{
				EXTENDS: A.Base,

				get: function(id) {
					var instance = this;

					return instance[instance._prefix + id];
				},

				prototype: {
					initializer: function(config) {
						var instance = this;

						var defaults = {
							accordion: false,
							collapsible: true,
							container: null,
							footer: '.lfr-panel-footer',
							header: '.lfr-panel-header',
							panel: '.lfr-panel',
							panelContent: '.lfr-panel-content',
							persistState: false,
							titles: '.lfr-panel-titlebar'
						};

						config = A.merge(defaults, config);

						instance._inContainer = false;
						instance._container = A.getBody();

						if (config.container) {
							instance._container = A.one(config.container);
							instance._inContainer = true;
						}

						instance._panel = instance._container.all(config.panel);

						instance._panelContent = instance._panel.all(config.panelContent);
						instance._header = instance._panel.all(config.header);
						instance._footer = instance._panel.all(config.footer);
						instance._panelTitles = instance._panel.all(config.titles);
						instance._accordion = config.accordion;

						instance._collapsible = config.collapsible;
						instance._persistState = config.persistState;

						if (instance._collapsible) {
							instance.makeCollapsible();

							instance._panelTitles.unselectable();

							instance._panelTitles.setStyle(
								{
									cursor: 'pointer'
								}
							);

							var collapsedPanels = instance._panel.all('.lfr-collapsed');

							if (instance._accordion && !collapsedPanels.size()) {
								instance._panel.item(0).addClass('lfr-collapsed');
							}
						}

						instance.set('container', instance._container);
						instance.set('panel', instance._panel);
						instance.set('panelContent', instance._panelContent);
						instance.set('panelTitles', instance._panelTitles);
					},

					makeCollapsible: function() {
						var instance = this;

						instance._panelTitles.each(
							function(item, index, collection) {
								var panel = item.ancestor('.lfr-panel');

								if (panel.hasClass('lfr-extended')) {
									var toggler = item.all('.lfr-panel-button');

									if (!toggler.size()) {
										item.append('<a class="lfr-panel-button" href="javascript:;"></a>');
									}
								}
							}
						);

						instance._panelTitles.on(
							'mousedown',
							function(event) {
								instance.onTitleClick(event.currentTarget);
							}
						);
					},

					onTitleClick: function(el) {
						var instance = this;

						var currentContainer = el.ancestor('.lfr-panel');

						currentContainer.toggleClass('lfr-collapsed');

						if (instance._accordion) {
							var siblings = currentContainer.siblings('.lfr-panel');

							siblings.each(
								function(item, index, collection) {
									var id = item.attr('id');

									if (id) {
										instance._saveState(id, 'closed');
									}

									item.addClass('lfr-collapsed');
								}
							);
						}

						var panelId = currentContainer.attr('id');
						var state = 'open';

						if (currentContainer.hasClass('lfr-collapsed')) {
							state = 'closed';
						}

						instance._saveState(panelId, state);

						instance.fire('titleClick');
					},

					_saveState: function (id, state) {
						var instance = this;

						if (instance._persistState) {
							var data = {};

							data[id] = state;

							A.io.request(
								themeDisplay.getPathMain() + '/portal/session_click',
								{
									data: data
								}
							);
						}
					}
				},

				register: function(id, panel) {
					var instance = this;

					instance[instance._prefix + id] = panel;
				},

				_prefix: '__'
			}
		);

		Liferay.Panel = Panel;
	},
	'',
	{
		requires: ['aui-base', 'aui-io-request']
	}
);