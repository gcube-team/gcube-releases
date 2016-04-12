Liferay.LayoutExporter = {
	icons: {
		minus: themeDisplay.getPathThemeImages() + '/arrows/01_minus.png',
		plus: themeDisplay.getPathThemeImages() + '/arrows/01_plus.png'
	}
};

Liferay.provide(
	Liferay.LayoutExporter,
	'all',
	function(options) {
		var A = AUI();

		options = options || {};

		var namespace = options.namespace;
		var obj = options.obj;
		var pane = options.pane;
		var publish = options.publish;

		if (obj && obj.checked) {
			pane = A.one(pane);

			if (pane) {
				pane.hide();
			}
		}
	},
	['aui-base']
);

Liferay.provide(
	Liferay.LayoutExporter,
	'details',
	function(options) {
		var A = AUI();

		options = options || {};

		var detail = A.one(options.detail);
		var img = A.one(options.toggle);

		if (detail && img) {
			var icon = Liferay.LayoutExporter.icons.plus;

			if (detail.hasClass('aui-helper-hidden')) {
				detail.show();
				icon = Liferay.LayoutExporter.icons.minus;
			}
			else {
				detail.hide();
			}

			img.attr('src', icon);
		}
	},
	['aui-dialog']
);

Liferay.provide(
	Liferay.LayoutExporter,
	'proposeLayout',
	function(options) {
		var A = AUI();

		options = options || {};

		var url = options.url;
		var namespace = options.namespace;
		var reviewers = options.reviewers;
		var title = options.title;

		var contents =
			"<div>" +
				"<form action='" + url + "' method='post'>";

		if (reviewers.length > 0) {
			contents +=
				"<textarea name='" + namespace + "description' style='height: 100px; width: 284px;'></textarea><br /><br />" +
				Liferay.Language.get('reviewer') + " <select name='" + namespace + "reviewUserId'>";

			for (var i = 0; i < reviewers.length; i++) {
				contents += "<option value='" + reviewers[i].userId + "'>" + reviewers[i].fullName + "</option>";
			}

			contents +=
				"</select><br /><br />" +
				"<input type='submit' value='" + Liferay.Language.get('proceed') + "' />";
		}
		else {
			contents +=
				Liferay.Language.get('no-reviewers-were-found') + "<br />" +
				Liferay.Language.get('please-contact-the-administrator-to-assign-reviewers') + "<br /><br />";
		}

		contents +=
				"</form>" +
			"</div>";

		new A.Dialog(
			{
				bodyContent: contents,
				centered: true,
				destroyOnClose: true,
				modal: true,
				title: title,
				width: 350
			}
		).render();
	},
	['aui-dialog']
);

Liferay.provide(
	Liferay.LayoutExporter,
	'publishToLive',
	function(options) {
		var A = AUI();

		options = options || {};

		var url = options.url;
		var title = options.title;

		var dialog = new A.Dialog(
			{
				centered: true,
				destroyOnClose: true,
				modal: true,
				title: title,
				width: 600
			}
		).render();

		dialog.plug(
			A.Plugin.IO,
			{
				uri: url
			}
		);
	},
	['aui-dialog', 'aui-io']
);

Liferay.provide(
	Liferay.LayoutExporter,
	'selected',
	function(options) {
		var A = AUI();

		options = options || {};

		var namespace = options.namespace;
		var obj = options.obj;
		var pane = options.pane;
		var publish = options.publish;

		if (obj && obj.checked) {
			pane = A.one(pane);

			if (pane) {
				pane.show();
			}
		}
	},
	['aui-base']
);