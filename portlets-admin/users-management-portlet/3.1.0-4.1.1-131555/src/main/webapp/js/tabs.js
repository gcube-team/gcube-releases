function tabsFunctionality(){
	$('table#CurrentUsersTable').DataTable().columns.adjust().draw();
	$('table#CurrentUsersTable').DataTable().columns.adjust().responsive.recalc();
	
	$('li.unhit').on('click', function(){
		if($(this).hasClass('unhit')){
			$(this).removeClass('unhit');
			initializeGroupTeamsTable();
			fetchAllSiteTeamsForTheCurrentGroup();
			searchInputFixForSiteTeamsEditTable();
			siteTeamsTableEvents();
			constructToolbarForSiteTeamsTable();
			initializeSiteTeamUsersTable();
			searchInputFixForSiteTeamsUsersTable();
			
			setTimeout(function(){//If you don't add some time interval, the table won't redraw when you press the tab
				$('table#GroupTeamsTable').DataTable().columns.adjust().draw();
				$('table#GroupTeamsTable').DataTable().columns.adjust().responsive.recalc();

				removeArrowFromFirstTableColumn();
			},200);
		}
	});
//	a.lineBeneathTabTitle
	$(' ul#myTab li ').on('click', function(){
//		$(this).prev().tab('show');//tab('show') applies on data-toggle="tab" element, only
		var $appropriateTab = $(this).find('a.tabTitle');
		$appropriateTab.tab('show');
	});
}