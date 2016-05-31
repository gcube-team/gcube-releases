Liferay.Util.portletTitleEdit = function() {
};

AUI().use(
	'aui-io-request',
	'aui-overlay-context-panel',
	function(A) {
		var portletInformationEl = A.one('#cpContextPanelTemplate');
		var portletInformationIcon = A.one('#cpPortletTitleHelpIcon');

		if (portletInformationEl && portletInformationIcon) {
			var portletId = portletInformationEl.attr('data-portlet-id');
			var visible = (portletInformationEl.attr('data-visible-panel') == "true");

			var sessionData = {};

			var sessionKey = 'show-portlet-description-' + portletId;

			if (themeDisplay.isImpersonated()) {
				sessionData.doAsUserId = themeDisplay.getDoAsUserIdEncoded();	
			}

			sessionData[sessionKey] = false;

			portletInformationEl.show();

			var contextPanel = new A.OverlayContextPanel(
				{
					align: {
						points: [ 'tl', 'bl' ]
					},
					bodyContent: portletInformationEl,
					on: {
						hide: function() {
							A.io.request(
								themeDisplay.getPathMain() + '/portal/session_click',
								{
									data: sessionData
								}
							);
						}
					},
					trigger: portletInformationIcon,
					visible: false,
					width: 450
				}
			).render();

			if (visible) {
				contextPanel.show();
			}
		}
	}
);