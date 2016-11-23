function removeArrowFromFirstTableColumn(){
	var tablesSelector = $('#usersManagementPortletContainer #CurrentUsersTable thead th:first, #usersManagementPortletContainer #usersRequestsTable thead th:first, #usersManagementPortletContainerSiteTeamsEditMode #GroupTeamsTable thead th:first');
	tablesSelector.removeClass('sorting_asc');
}