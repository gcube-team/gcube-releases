AUI().add(
	'liferay-enterprise-admin',
	function(A) {
		var Addresses = {
			getCountries: function(callback) {
				Liferay.Service.Portal.Country.getCountries(
					{
						active: true
					},
					callback
				);
			},

			getRegions: function(callback, selectKey) {
				Liferay.Service.Portal.Region.getRegions(
					{
						countryId: Number(selectKey),
						active: true
					},
					callback
				);
			}
		};

		var FormNavigator = function(options) {
			var instance = this;

			instance._container = A.one(options.container);

			instance._navigation = instance._container.one('.form-navigation');
			instance._sections = instance._container.all('.form-section');

			if (instance._navigation) {
				instance._navigation.delegate(
					'click',
					function(event) {
						event.preventDefault();

						var target = event.currentTarget;
						var li = target.get('parentNode');

						if (li && !li.test('.selected')) {
							instance._revealSection(target.attr('href'), li);

							var currentSection = target.attr('href').split('#');

							if (currentSection[1]) {
								A.later(0, instance, instance._updateHash, [currentSection[1]]);
							}
						}
					},
					'li a'
				);
			}

			if (options.modifiedSections) {
				instance._modifiedSections = A.all('[name=' + options.modifiedSections+ ']');

				if (!instance._modifiedSections) {
					instance._modifiedSections = A.Node.create('<input name="' + options.modifiedSections + '" type="hidden" />');
					instance._container.append(instance._modifiedSections);
				}
			}
			else {
				instance._modifiedSections = null;
			}

			if (options.defaultModifiedSections) {
				instance._modifiedSectionsArray = options.defaultModifiedSections;
			}
			else {
				instance._modifiedSectionsArray = [];
			}

			instance._revealSection(location.href);

			A.on(
				'enterpriseAdmin:trackChanges',
				function(element) {
					instance._trackChanges(element);
				}
			);

			var inputs = instance._container.all('input, select, textarea');

			if (inputs) {
				inputs.on(
					'change',
					function(event) {
						A.fire('enterpriseAdmin:trackChanges', event.target);
					}
				);
			}

			Liferay.on(
				'submitForm',
				function(event, data) {
					if (instance._modifiedSections) {
						instance._modifiedSections.val(instance._modifiedSectionsArray.join(','));
					}
				}
			);
		};

		FormNavigator.prototype = {
			_addModifiedSection: function (section) {
				var instance = this;

				if (A.Array.indexOf(section, instance._modifiedSectionsArray) == -1) {
					instance._modifiedSectionsArray.push(section);
				}
			},

			_getId: function(id) {
				var instance = this;

				id = id || '';

				if (id.indexOf('#') > -1) {
					id = id.split('#')[1] || '';

					id = id.replace(instance._hashKey, '');
				}
				else if (id.indexOf('historyKey=') > -1) {
					id = id.match(/historyKey=([^&#]+)/);
					id = id && id[1];
				}
				else {
					id = '';
				}

				return id;
			},

			_revealSection: function(id, currentNavItem) {
				var instance = this;

				id = instance._getId(id);

				if (id) {
					id = id.charAt(0) != '#' ? '#' + id : id;

					var li = currentNavItem || instance._navigation.one('[href$=' + id + ']').get('parentNode');

					id = id.split('#');

					if (!id[1]) {
						return;
					}

					Liferay.fire('enterpriseAdmin:reveal'+id[1]);

					id = '#' + id[1];

					var section = A.one(id);
					var selected = instance._navigation.one('li.selected');

					if (selected) {
						selected.removeClass('selected');
					}

					li.addClass('selected');

					instance._sections.removeClass('selected').hide('aui-helper-hidden-accessible');

					if (section) {
						section.addClass('selected').show('aui-helper-hidden-accessible');
					}
				}
			},

			_trackChanges: function(el) {
				var instance = this;

				var currentSection = A.one(el).ancestor('.form-section').attr('id');

				var currentSectionLink = A.one('#' + currentSection + 'Link');

				if (currentSectionLink) {
					currentSectionLink.get('parentNode').addClass('section-modified');
				}

				instance._addModifiedSection(currentSection);
			},

			_updateHash: function(section) {
				var instance = this;

				location.hash = instance._hashKey + section;
			},

			_hashKey: '_LFR_FN_'
		};

		Liferay.EnterpriseAdmin = {
			Addresses: Addresses,
			FormNavigator: FormNavigator
		};
	}
);