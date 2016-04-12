Liferay.on = function(){};
Liferay.fire = function(){};
Liferay.detach = function(){};

AUI().use(
	'attribute',
	'oop',
	function(A) {
		A.augment(Liferay, A.Attribute, true);
	}
);