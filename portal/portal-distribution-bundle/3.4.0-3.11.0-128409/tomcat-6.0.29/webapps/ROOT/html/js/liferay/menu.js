AUI().add(
	'liferay-menu',
	function(A) {
		var Menu = function() {
			var instance = this;

			if (!arguments.callee._hasRun) {
				arguments.callee._hasRun = true;

				instance._body = A.getBody();
				instance._document = A.getDoc();
				instance._window = A.getWin();

				instance._active = {
					menu: null,
					trigger: null
				};

				if (Liferay.Layout) {
					Liferay.Layout.on('drag:start', instance._closeActiveMenu, instance);
				}

				instance._window.on('resize', instance._positionActiveMenu, instance);

				var hideClass = 'aui-helper-hidden-accessible';

				instance._body.delegate(
					'click',
					function(event) {
						var trigger = event.currentTarget;
						var menu = trigger._AUI_MENU;

						if (!menu) {
							var list = trigger.one('ul');

							list.one('li:last-child').addClass('last');

							menu = A.Node.create('<div class="lfr-component lfr-menu-list" />');

							menu._hideClass = hideClass;

							menu.appendChild(list);
							menu.hide();

							instance._body.appendChild(menu);

							Liferay.Util.createFlyouts(
								{
									container: menu.getDOM()
								}
							);

							trigger._AUI_MENU = menu;
						}

						if (instance._active.menu && !instance._active.menu.compareTo(menu)) {
							instance._closeActiveMenu();
						}

						if (!menu.hasClass(hideClass)) {
							instance._closeActiveMenu();
						}
						else {
							instance._active.menu = menu;
							instance._active.trigger = trigger;

							if (!menu.focusManager) {
								instance._plugMenu(menu, trigger);
							}

							instance._positionActiveMenu();
						}

						event.halt();

						var anchor = trigger.one('a');

						if (anchor) {
							anchor.setAttrs(
								{
									'aria-haspopup': true,
									role: 'button'
								}
							);
						}
					},
					'.lfr-actions'
				);

				instance._document.on('click', instance._closeActiveMenu, instance);
			}
		};

		Menu.prototype = {
			_closeActiveMenu: function() {
				var instance = this;

				if (instance._active.menu) {
					instance._active.menu.hide();
					instance._active.menu = null;

					instance._active.trigger.removeClass('aui-state-active');
					instance._active.trigger = null;
				}
			},

			_plugMenu: function(menu, trigger) {
				var instance = this;

				menu.plug(
					A.Plugin.NodeFocusManager,
					{
						circular: true,
						descendants: 'a',
						focusClass: 'aui-focus',
						keys: {
							next: 'down:40',
							previous: 'down:38'
						}
					 }
				);

				var anchor = trigger.one('a');

				menu.on(
					'key',
					function(event) {
						instance._closeActiveMenu();

						if (anchor) {
							anchor.focus();
						}
					},
					'down:27,9'
				);

				menu.delegate(
					'mouseenter',
					function (event) {
						var focusManager = menu.focusManager;

						if (focusManager.get('focused')) {
							focusManager.focus(event.currentTarget.one('a'));
						}
					},
					'li'
				);

				menu.setAttrs(
					{
						'aria-labelledby': (anchor && anchor.get('id')),
						role: 'menu'
					}
				);

				menu.all('a').set('role', 'menuitem');
			},

			_positionActiveMenu: function() {
				var instance = this;

				var menu = instance._active.menu;
				var trigger = instance._active.trigger;

				if (menu) {
					var offset = trigger.get('region');

					cssClass = trigger.attr('className');

					var direction = 'auto';
					var vertical = 'bottom';
					var win = instance._window;

					if (cssClass.indexOf('right') > -1) {
						direction = 'right';
					}
					else if (cssClass.indexOf('left') > -1) {
						direction = 'left';
					}

					var menuHeight = menu.get('offsetHeight');
					var menuWidth = menu.get('offsetWidth');

					var triggerHeight = offset.height;
					var triggerWidth = offset.width;

					var menuTop = menuHeight + offset.top;
					var menuLeft = menuWidth + offset.left;

					var scrollTop = win.get('scrollTop');
					var scrollLeft = win.get('scrollLeft');

					var windowRegion = trigger.get('viewportRegion');

					var windowHeight = windowRegion.height + scrollTop;
					var windowWidth = windowRegion.width + scrollLeft;

					if (direction == 'auto') {
						if (menuTop > windowHeight
							&& !((offset.top - menuHeight) < 0)) {

							offset.top -= menuHeight;
						}
						else {
							offset.top += triggerHeight;
						}

						if ((menuLeft > windowWidth || ((menuWidth/2) + offset.left) > windowWidth/2)
							&& !((offset.left - menuWidth) < 0)) {

							offset.left -= (menuWidth - triggerWidth);
						}
					}
					else {
						if (direction == 'right') {
							offset.left -= (menuWidth - 2);
						}
						else if (direction == 'left') {
							offset.left += (triggerWidth + 2);
						}

						offset.top -= (menuHeight - triggerHeight);
					}

					menu.setStyle('position', 'absolute');

					menu.setXY([offset.left, offset.top]);

					menu.show();

					trigger.addClass('aui-state-active');

					instance._active = {
						menu: menu,
						trigger: trigger
					};

					var firstItem = menu.one('a');

					if (firstItem) {
						firstItem.focus();
					}
				}
			}
		};

		Menu.handleFocus = function(id) {
			var node = A.one(id);

			if (node) {
				node.delegate('mouseenter', A.rbind(Menu._targetLink, node, 'focus'), 'li');
				node.delegate('mouseleave', A.rbind(Menu._targetLink, node, 'blur'), 'li');
			}
		};

		Menu._targetLink = function(event, action) {
			event.currentTarget.one('a')[action]();
		};

		Liferay.Menu = Menu;
	},
	'',
	{
		requires: ['aui-base', 'node-focusmanager', 'selector-css3']
	}
);