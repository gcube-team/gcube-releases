<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<p id="geoadmin-users-notificator" style="display: none;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-users-toolbar">
	<div align='center' style='display: inline-block;' class="portlet-datatable-buttons" >
		<button type='button' id='geoadmin-refresh-user-button'>
			<i class="fa fa-refresh" aria-hidden="true"></i>
			Refresh
		</button>
	</div>
</div>

<table id="geoadmin-users-datatable"></table>