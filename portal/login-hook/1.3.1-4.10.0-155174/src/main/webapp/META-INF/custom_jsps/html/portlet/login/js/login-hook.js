$(document).ready(
		function() {
			$('.portlet-login #collapseMe').on('show', function(e){
				$('.portlet-login .icon-double-angle-down').removeClass('icon-double-angle-down').addClass('icon-double-angle-up');
			});
			
			$('.portlet-login #collapseMe').on('hidden', function(e){
				$('.portlet-login .icon-double-angle-up').removeClass('icon-double-angle-up').addClass('icon-double-angle-down');
			});
			
			$('#signinmodal #collapseMe').on('show', function(e){
				$('.portlet-login .icon-double-angle-down').removeClass('icon-double-angle-down').addClass('icon-double-angle-up');
			});
			
			$('#signinmodal #collapseMe').on('hidden', function(e){
				$('.portlet-login .icon-double-angle-up').removeClass('icon-double-angle-up').addClass('icon-double-angle-down');
			});
			
		});