AUI().add(
	'liferay-session',
	function(A) {
		var Session = {
			autoExtend: false,
			sessionTimeout: 0,
			sessionTimeoutWarning: 0,
			redirectOnExpire: false,

			init: function(params) {
				var instance = this;

				params = params || {};
				instance.autoExtend = params.autoExtend || instance.autoExtend;

				instance._timeout = params.timeout || instance.sessionTimeout;
				instance._warning = params.timeoutWarning || instance.sessionTimeoutWarning;

				instance.sessionTimeout = instance._timeout * 60000;
				instance.sessionTimeoutWarning = instance._warning * 60000;
				instance._timeoutDiff = instance.sessionTimeout - instance.sessionTimeoutWarning;

				instance._currentTime = instance.sessionTimeoutWarning;

				instance.redirectOnExpire = params.redirectOnExpire || instance.redirectOnExpire;

				instance._cookieKey = 'LFR_SESSION_STATE_' + themeDisplay.getUserId();

				var urlBase = themeDisplay.getPathMain() + '/portal/';

				instance._sessionUrls = {
					expire: urlBase + 'expire_session',
					extend: urlBase + 'extend_session'
				};

				instance._stateCheck = setTimeout(
					function() {
						instance.checkState();
					},
					instance._timeoutDiff);

				instance._toggleText = {
					hide: Liferay.Language.get('hide'),
					show: Liferay.Language.get('show')
				};

				instance._expiredText = Liferay.Language.get('warning-your-session-has-expired');
				instance._extendText = Liferay.Language.get('extend');

				instance._warningText = Liferay.Language.get('warning-your-session-will-expire');
				instance._warningText = A.substitute(instance._warningText, ['<span class="countdown-timer"></span>', instance._timeout]);

				instance.setCookie();
			},

			checkState: function() {
				var instance = this;

				var currentTime = new Date().getTime();
				var sessionState = instance.getCookie();
				var newWaitTime = instance.sessionTimeoutWarning;
				var timeDiff = 0;

				clearTimeout(instance._stateCheck);

				if (sessionState == 'expired') {
					instance.expire();
				}
				else {
					timeDiff = currentTime - sessionState;

					if (!instance.autoExtend) {
						if ((timeDiff + 100) >= instance.sessionTimeoutWarning) {
								instance.warn();
						}
						else {
							newWaitTime = (instance.sessionTimeoutWarning - timeDiff) + 10000;

							instance._stateCheck = setTimeout(
								function() {
									instance.checkState();
								},
							newWaitTime);
						}
					}
					else {
						instance.extend();
					}
				}
			},

			getCookie: function() {
				var instance = this;

				return A.Cookie.get(instance._cookieKey) || 0;
			},

			expire: function() {
				var instance = this;

				document.title = instance._originalTitle;

				A.io.request(
					instance._sessionUrls.expire,
					{
						on: {
							success: function(event, id, obj) {
								if (instance.redirectOnExpire) {
									location.href = themeDisplay.getURLHome();
								}

								Liferay.fire('sessionExpired');
							}
						}
					}
				);

				instance.setCookie('expired');
			},

			extend: function() {
				var instance = this;

				if (instance._countdownTimer) {
					clearInterval(instance._countdownTimer);
				}

				A.io.request(instance._sessionUrls.extend);

				document.title = instance._originalTitle;

				instance._currentTime = instance.sessionTimeoutWarning;

				clearTimeout(instance._sessionExpired);

				if (instance._sessionWarning) {
					clearTimeout(instance._sessionWarning);
				}

				instance._sessionWarning = setTimeout(
					function() {
						if (!instance.autoExtend) {
							instance.warn();
						}
						else {
							instance.extend();
						}
					},
					instance._timeoutDiff
				);

				instance.setCookie();
			},

			setCookie: function(status) {
				var instance = this;

				var currentTime = new Date().getTime();

				var options = {
					secure: A.UA.secure
				};

				A.Cookie.set(instance._cookieKey, status || currentTime, options);
			},

			warn: function() {
				var instance = this;

				instance.banner = new Liferay.Notice(
					{
						closeText: instance._extendText,
						content: instance._warningText,
						onClose: function() {
							instance.extend();
						},
						toggleText: false
					}
				);

				instance._counter();

				instance._sessionExpired = setTimeout(
					function() {
						instance.expire();
					},
					instance.sessionTimeoutWarning);
			},

			_counter: function() {
				var instance = this;

				var banner = instance.banner;

				if (banner) {
					instance._counterText = banner.one('.countdown-timer');
					instance._originalTitle = document.title;

					var interval = 1000;

					instance._counterText.text(instance._setTime());
					document.title = instance.banner.text();

					instance._countdownTimer = setInterval(
						function() {
							var time = instance._setTime();

							instance._currentTime = instance._currentTime - interval;

							if (instance._currentTime > 0) {
								instance._counterText.text(time);
								document.title = instance.banner.text();
							}
							else {
								instance.banner.html(instance._expiredText);
								instance.banner.toggleClass('popup-alert-notice').toggleClass('popup-alert-warning');

								if (instance._countdownTimer) {
									clearInterval(instance._countdownTimer);
								}
							}
						},
						interval
					);
				}
			},

			_formatNumber: function(num) {
				var instance = this;

				if (!Liferay.Util.isArray(num)) {
					if (num <= 9) {
						num = '0' + num;
					}
				}
				else {
					num = A.Array.map(num, instance._formatNumber);
				}
				return num;
			},

			_setTime: function() {
				var instance = this;

				var amount = instance._currentTime;

				if (amount <= 0) {

				}
				else {
					var days=0, hours=0, minutes=0, seconds=0, output='';

					// Remove the milliseconds
					amount = Math.floor(amount/1000);

					hours = Math.floor(amount/3600);
					amount = amount%3600;

					minutes = Math.floor(amount/60);
					amount = amount%60;

					seconds = Math.floor(amount);

					return instance._formatNumber([hours, minutes, seconds]).join(':');
				}
			},

			_banner: [],
			_currentTime: 0,
			_originalTitle: '',
			_sessionUrls: {},
			_timeout: 0,
			_timeoutDiff: 0,
			_warning: 0
		};

		Liferay.Session = Session;
	},
	'',
	{
		requires: ['aui-io', 'collection', 'cookie', 'liferay-notice', 'substitute']
	}
);