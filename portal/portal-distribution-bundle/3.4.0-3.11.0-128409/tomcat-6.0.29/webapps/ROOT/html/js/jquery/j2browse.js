/*

jQuery Browser Plugin
	* Version 1.2.0
	* 2008-05-26 14:39:29
	* URL: http://jquery.thewikies.com/browser
	* Description: jQuery Browser Plugin extends browser detection capabilities and can implements CSS browser selectors.
	* Author: Nate Cavanaugh, Minhchau Dang, & Jonathan Neal
	* Copyright: Copyright (c) 2008 Jonathan Neal under dual MIT/GPL license.

*/

(function($) {

	// Define whether Browser Selectors will be added automatically; set as false to disable.
	var addSelectors = true;

	// Define Navigator Properties.
	var p = navigator.platform;
	var u = navigator.userAgent;
	var b = /(Firefox|Opera|Safari|KDE|iCab|Flock|IE)/.exec(u);
	var os = /(Win|Mac|Linux|iPhone|Sun|Solaris)/.exec(p);
	var versionDefaults = [0,0];

	b = (!b || !b.length) ? (/(Mozilla)/.exec(u) || ['']) : b;
	os = (!os || !os.length) ? [''] : os;

	// Define Browser Properties.
	var o = jQuery.extend($.browser, {

		// Define the rendering client
		gecko: /Gecko/.test(u) && !/like Gecko/.test(u),
		webkit: /WebKit/.test(u),

		// Define the browser
		aol: /America Online Browser/.test(u),
		camino: /Camino/.test(u),
		firefox: /Firefox/.test(u),
		flock: /Flock/.test(u),
		icab: /iCab/.test(u),
		konqueror: /KDE/.test(u),
		mozilla: /mozilla/.test(u),
		ie: /MSIE/.test(u),
		netscape: /Netscape/.test(u),
		opera: /Opera/.test(u),
		safari: /Safari/.test(u),
		browser: b[0].toLowerCase(),

		// Define the opperating system
		win: /Win/.test(p),
		mac: /Mac/.test(p),
		linux: /Linux/.test(p),
		iphone: /iPhone/.test(p),
		sun: /Solaris|SunOS/.test(p),
		os: os[0].toLowerCase(),

		// Define the classic navigator properties
		platform: p,
		agent: u,

		// Define the 'addSelectors' function which adds Browser Selectors to a tag; by default <HTML>.
		addSelectors: function(e) {
			jQuery(e || 'html').addClass(o.selectors);
		},

		// Define the 'removeSelectors' function which removes Browser Selectors to a tag; by default <HTML>.
		removeSelectors: function(e) {
			jQuery(e || 'html').addClass(o.selectors);
		}

	});

	// Define the Browser Client Version.
	o.version = {
			string: (o.msie)
			? (/MSIE ([^;]+)/.exec(u) || versionDefaults)[1]
			: (o.firefox)
				? (/Firefox\/(.+)/.exec(u) || versionDefaults)[1]
				: (o.safari)
					? (/Version\/([^\s]+)/.exec(u) || versionDefaults)[1]
					: (o.opera)
						? (/Opera\/([^\s]+)/.exec(u) || versionDefaults)[1]
						: 'undefined' };
	o.version.number = parseFloat(o.version.string) || versionDefaults[0];
	o.version.major = /([^\.]+)/.exec(o.version.string)[1];

	// Define the Browser with Client Version.
	o[o.browser + o.version.major] = true;

	// Define the Rendering Client.
	o.renderer = (o.gecko) ? 'gecko' : (o.webkit) ? 'webkit' : '';

	// Define the selector.
	o.selectors = [o.renderer, o.browser, o.browser + o.version.major, o.os, 'js'].join(' ');

	// Run the 'addSelectors' Function if the 'addSelectors' Variable is set as true.
	if (addSelectors) o.addSelectors();

}(jQuery));