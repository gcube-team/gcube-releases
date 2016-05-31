Liferay = window.Liferay || {};

Liferay.namespace = AUI().namespace;

AUI().mix(
	AUI.defaults.io,
	{
		dataFormatter: function(data) {
			return AUI().Lang.toQueryString(data);
		},
		method: 'POST',
		uriFormatter: function(value) {
			return Liferay.Util.getURLWithSessionId(value);
		}
	},
	true
);

Liferay.Service = {
	actionUrl: themeDisplay.getPathMain() + '/portal/json_service',

	tunnelUrl: themeDisplay.getPathContext() + '/tunnel-web/secure/json',

	classNameSuffix: 'ServiceUtil',

	ajax: function(options, callback) {
		var instance = this;

		var type = 'POST';

		if (Liferay.PropsValues.NTLM_AUTH_ENABLED && Liferay.Browser.isIe()) {
			type = 'GET';
		}

		var serviceUrl = instance.actionUrl;

		var tunnelEnabled = (Liferay.ServiceAuth && Liferay.ServiceAuth.header);

		if (tunnelEnabled) {
			serviceUrl = instance.tunnelUrl;
		}

		options.serviceParameters = Liferay.Service.getParameters(options);
		options.doAsUserId = themeDisplay.getDoAsUserIdEncoded();

		var config = {
			cache: false,
			data: options,
			dataType: 'json',
			on: {}
		};

		var xHR = null;

		if (Liferay.PropsValues.NTLM_AUTH_ENABLED && Liferay.Browser.isIe()) {
			config.method = 'GET';
		}

		if (callback) {
			config.on.success = function(event, id, obj) {
				callback.call(this, this.get('responseData'), obj);
			};

			if (tunnelEnabled) {
				config.headers = {
					Authorization: Liferay.ServiceAuth.header
				};
			}
		}
		else {
			config.on.success = function(event, id, obj) {
				xHR = obj;
			};

			config.sync = true;
		}

		AUI().io.request(serviceUrl, config);

		if (xHR) {
			return eval('(' + xHR.responseText + ')');
		}
	},

	getParameters: function(options) {
		var instance = this;

		var serviceParameters = [];

		for (var key in options) {
			if ((key != 'serviceClassName') && (key != 'serviceMethodName') && (key != 'serviceParameterTypes')) {
				serviceParameters.push(key);
			}
		}

		return instance._getJSONParser().stringify(serviceParameters);
	},

	namespace: function(namespace) {
		var curLevel = Liferay || {};

		if (typeof namespace == 'string') {
			var levels = namespace.split('.');

			for (var i = (levels[0] == 'Liferay') ? 1 : 0; i < levels.length; i++) {
		 		curLevel[levels[i]] = curLevel[levels[i]] || {};
				curLevel = curLevel[levels[i]];
			}
		}
		else {
			curLevel = namespace || {};
		}

		return curLevel;
	},

	register: function(serviceName, servicePackage) {
		var module = Liferay.Service.namespace(serviceName);

		module.servicePackage = servicePackage.replace(/[.]$/, '') + '.';

		return module;
	},

	registerClass: function(serviceName, className, prototype) {
		var module = serviceName || {};
		var moduleClassName = module[className] = {};

		moduleClassName.serviceClassName = module.servicePackage + className + Liferay.Service.classNameSuffix;

		var Lang = AUI().Lang;

		AUI().Object.each(
			prototype,
			function(item, index, collection) {
				var handler = item;

				if (!Lang.isFunction(handler)) {
					handler = function(params, callback) {
						params.serviceClassName = moduleClassName.serviceClassName;
						params.serviceMethodName = index;

						return Liferay.Service.ajax(params, callback);
					};
				}

				moduleClassName[index] = handler;
			}
		);
	},

	_getJSONParser: function() {
		var instance = this;

		if (!instance._JSONParser) {
			var A = AUI();

			if (!A.JSON) {
				A = AUI({}).use('json');
			}

			instance._JSONParser = A.JSON;
		}

		return instance._JSONParser;
	}
};

Liferay.Template = {
	PORTLET: '<div class="portlet"><div class="portlet-topper"><div class="portlet-title"></div></div><div class="portlet-content"></div><div class="forbidden-action"></div></div>'
}