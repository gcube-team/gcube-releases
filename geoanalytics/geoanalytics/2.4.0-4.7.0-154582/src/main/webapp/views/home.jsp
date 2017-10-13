<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>Geopolis</title>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=1">
	<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap-3.0.0.min.css">
	<link rel="stylesheet" href="resources/css/datepicker.css">
	<link rel="stylesheet" type="text/css" href="resources/css/jquery.jscrollpane.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/geopolis.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/geopolis-checkbox.css" />
	<!--[if lt IE 9]>
		<link rel="stylesheet" type="text/css" href="resources/css/geopolis-checkbox-reset.css" />
	<![endif]-->
	
	<link rel="icon" type="image/png" href="resources/img/logo3.png">
	
	<script src="resources/script/jquery-1.10.2.min.js"></script>
	<script src="resources/script/jquery-ui-1.10.3.min.js"></script>
	<script src="resources/script/jquery.dataTables.js"></script>
	<script src="resources/script/jquery.dataTables.pagingInfo.js"></script>
	<script src="resources/script/jquery.mousewheel.js"></script>
	<script src="resources/script/jquery.jscrollpane.js"></script>
	<script src="resources/script/bootstrap-3.0.0.min.js" > </script>
	<script src="resources/script/dataTables.bootstrap.js"></script>
	<script src="resources/script/typeahead.min.js" > </script>
	<script src="resources/script/bootstrap-datepicker.js" > </script>
	<script src="resources/script/bootstrap-datepicker.el.js" > </script>
	<script src="resources/script/utils.js" > </script>
	<script src="//maps.google.com/maps/api/js?v=3&sensor=false"></script>
	<script src="resources/script/OpenLayers.js"></script>
	<script src="resources/script/proj4js-compressed.js"></script>
	<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
	<![endif]-->
 
	<script src='resources/script/geopolis.js'></script>
	<script defer="defer" type="text/javascript">
        //defs["EPSG:2100"] = "+proj=tmerc +lat_0=0 +lon_0=24 +k=0.9996 +x_0=500000 +y_0=0 +ellps=GRS80 +towgs84=-199.87,74.79,246.62,0,0,0,0 +units=m +no_defs "; 
		var bounds = {}; bounds.minx = "${bounds.getMinX()}"; bounds.miny = "${bounds.getMinY()}";
		bounds.maxx = "${bounds.getMaxX()}"; bounds.maxy = "${bounds.getMaxY()}";
		var layers = {
			//server-side minScale actually corresponds to OpenLayers maxScale and vice versa
			<c:forEach items="${layers}" var="layer" varStatus="status">  
	                 '${layer.key}' : {name : '${layer.value.name}', maxScale : '${layer.value.minScale}', minScale : '${layer.value.maxScale}'}
			    <c:if test="${!status.last}">,</c:if>  
    		</c:forEach>  
		};
		
		var defs = {};
		defs.layerDatastore = "${layerDatastore}"
		defs.locale = "el";
		defs.i18n = {};
		defs.i18n.questionMark = ";";
		defs.i18n.lastUpdate = "τελ. ενημέρωση";
		defs.i18n.from = "από";
		defs.i18n.select = "Επιλέξτε";
		
		defs.i18n.confirm = {};
		defs.i18n.confirm.yes = "Ναι";
		defs.i18n.confirm.no = "Όχι";
		
		defs.i18n.nav = {};
		defs.i18n.nav.legend = "Πληροφορίες Χάρτη";
		defs.i18n.nav.search = "Αναζήτηση";
		defs.i18n.nav.projects = "Τα Έργα μου";
		defs.i18n.nav.locationInfo = "Πληροφορίες";
		defs.i18n.nav.projectMenu = {};
		defs.i18n.nav.projectMenu.info = "Στοιχεία";
		defs.i18n.nav.projectMenu.tasks = "Εργασίες";
		defs.i18n.nav.projectMenu.documents = "Έγγραφα";
		defs.i18n.nav.projectMenu.report = "Αναφορά";
		defs.i18n.nav.legendMenu = {};
		defs.i18n.nav.legendMenu.sites = "Ακίνητα";
		defs.i18n.nav.legendMenu.planning = "Χωροταξία";
		defs.i18n.nav.legendMenu.maps = "Χάρτες";
		defs.i18n.nav.legendMenu.baseMap = "Ψηφιακό Υπόβαθρο";
		defs.i18n.nav.legendMenu.additional = "Επιπλέον Πληροφορίες";
		defs.i18n.nav.legendMenu.poi = "Σημεία Ενδιαφέροντος";
		defs.i18n.nav.settings = "Ρυθμίσεις";
		defs.i18n.nav.login = "Είσοδος";
		defs.i18n.nav.logout = "Έξοδος";
		defs.i18n.nav.promotional = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis lectus massa, varius nec blandit vel, molestie nec felis. Phasellus nisi diam, " +
		"tempus in scelerisque vel, porttitor non odio. Sed consectetur ac lacus id pellentesque. Maecenas congue suscipit lobortis. Phasellus gravida ligula risus, vitae pretium odio" + 
		"placerat nec. </p> <p> Vivamus nisl nunc, tristique vel gravida non, consectetur non nisi. Fusce eu lectus neque. Fusce et quam enim. Nam sit amet vestibulum libero. Proin nisi nisl," +
		"dictum id odio quis, pharetra gravida augue. </p> <p Mauris vel nibh sit amet diam sollicitudin congue quis sit amet mi. Donec ac sollicitudin est, vitae consequat lorem. Nam vehicula" +
		"quam non lectus pretium bibendum. </p>";
		
		defs.i18n.action = {};
		defs.i18n.action.newItem = "Νέο";
		defs.i18n.action.addItem = "Προσθήκη";
		defs.i18n.action.newSearch = "Νέα Αναζήτηση";
		defs.i18n.action.newProject = "Νέο Έργο";
		defs.i18n.action.newTask = "Νέα Εργασία";
		defs.i18n.action.newDocument = "Νέο Έγγραφο";
		defs.i18n.action.newInfo = "Νέα Ιδιότητα";
		defs.i18n.action.newProjectSave = "Δημιουργία";
		defs.i18n.action.newTaskSave = "Δημιουργία";
		defs.i18n.action.newDocumentSave = "Αποθήκευση";
		defs.i18n.action.newAttributeSave = "Δημιουργία";
		
		defs.i18n.header = {};
		defs.i18n.header.newProject = "Δημιουργία Νέου Έργου";
		defs.i18n.header.newTask = "Δημιουργία Νέας Εργασίας";
		defs.i18n.header.newAttribute = "Δημιουργία Νέας Ιδιότητας";
		defs.i18n.header.newDocument = "Προσθήκη Νέου Εγγράφου";
		defs.i18n.header.documentTasks = "Συνδ. Εργασίες με το";
		defs.i18n.header.workflowInfo = "Καθορισμός Ροής Εργασίας";
		defs.i18n.header.optional = "Προαιρετικό";
		
		defs.i18n.info = {};
		defs.i18n.info.noProjects = "Δεν Βρέθηκαν Έργα";
		defs.i18n.info.noTasks = "Δεν Βρέθηκαν Εργασίες";
		defs.i18n.info.noDocuments = "Δεν Βρέθηκαν Έγγραφα";
		defs.i18n.info.noInfo = "Δεν Βρέθηκαν Πληροφορίες";
		defs.i18n.info.selectArea = "Επιλέξτε περιοχή στον χάρτη";
		
		defs.i18n.error = {};
		defs.i18n.error.failure = "Η ενέργεια απέτυχε";
		
		defs.i18n.drawing = {};
		defs.i18n.drawing.point = "Σημείο";
		defs.i18n.drawing.polygon = "Πολύγωνο";
		defs.i18n.drawing.freehand = "Ελεύθερο σχέδιο";
		defs.i18n.drawing.select = "Επιλογή σχημάτων";
		defs.i18n.drawing.modify = "Τροποποίηση σχήματος";
		defs.i18n.drawing.hand = "Μετακίνηση χάρτη";
		defs.i18n.drawing.drag = "Μετακίνηση σχήματος";
		defs.i18n.drawing['delete'] = "Διαγραφή σχήματος";
		defs.i18n.drawing.done = "Επιβεβαίωση αλλαγών";
		defs.i18n.drawing.cancel = "Ακύρωση αλλαγών";
		
		defs.i18n.search = {};
		defs.i18n.search.typeMap = 'Αναζήτηση στο χάρτη';
		defs.i18n.search.typeProjects = 'Αναζήτηση στις καταχωρήσεις';
		defs.i18n.search.all = ['Όλοι οι Νομοί', 'Όλοι οι Δήμοι', 'Όλοι οι Οικισμοί'];
		defs.i18n.search.noValues = 'Δεν βρέθηκαν τιμές';
		defs.i18n.search.selectAttribute = 'Επιλέξτε Ιδιότητα';
		defs.i18n.search.selectValueForAttribute = 'Τιμή Ιδιότητας';
		defs.i18n.search.location = 'Τοποθεσία';
		defs.i18n.search.typeRequiresAttributes = 'Ο συγκεκριμένος τύπος αναζήτησης απαιτεί την επιλογή τουλάχιστον μίας ιδιότητας αναζήτησης';
		
		defs.i18n.project = {};
		defs.i18n.project.tableHeader = {};
		defs.i18n.project.tableHeader.title = "Τίτλος";
		defs.i18n.project.tableHeader.start = "Έναρξη";
		defs.i18n.project.tableHeader.client = "Πελάτης";
		defs.i18n.project.attr = {};
		defs.i18n.project.attr.name = "Tίτλος";
		defs.i18n.project.attr.description = "Περιγραφή";
		defs.i18n.project.attr.creationDate = "Ημ/νία Δημιουργίας";
		defs.i18n.project.attr.client = "Πελάτης";
		defs.i18n.project.attr.template = "Πρότυπο";
		defs.i18n.project.attr.status = "Κατάσταση";
		defs.i18n.project.attr.creator = "Δημιουργός";
		defs.i18n.project.attr.shape = "Γεωμετρία";
		defs.i18n.project.confirm = {};
		defs.i18n.project.confirm.deletionText = "Να διαγραφεί το έργο με όνομα";
		defs.i18n.project.confirm.deletionTitle = "Διαγραφή Έργου";
		defs.i18n.project.status = {};
		defs.i18n.project.status.active = "Σε Εξέλιξη";
		defs.i18n.project.status.inactive = "Ανενεργό";
		defs.i18n.project.status.completed = "Ολοκληρωμένο";
		defs.i18n.project.status.cancelled = "Ακυρωμένο";
		defs.i18n.project.error = {};
		defs.i18n.project.error.notFound = "Το έργο δεν βρέθηκε";
		defs.i18n.project.error.existing = "Το έργο υπάρχει ήδη";
		defs.i18n.project.error.unauthorized = "Δεν είστε εξουσιοδοτημένος να εκτελέσετε αυτή την ενέργεια";
		defs.i18n.project.error.noClient = "Εισάγετε όνομα πελάτη";
		defs.i18n.project.error.noName = "Εισάγετε όνομα έργου";
		defs.i18n.project.error.noShape = "Δεν έχει οριστεί γεωμετρία";
		defs.i18n.project.error.noShapeFound = "Δεν υπάρχει γεωμετρία συσχετισμένη με το έργο";
		defs.i18n.project.error.singleShape = "Δεν υποστηρίζεται γεωμετρία πολλαπλών στοιχείων";

		defs.i18n.document = {}
		defs.i18n.document.tableHeader = {};
		defs.i18n.document.tableHeader.name = "Έγγραφο";
		defs.i18n.document.tableHeader.creationDate = "Προσθήκη";
		defs.i18n.document.tableHeader.taskCount = "Εξαρτ. Εργασίες";
		defs.i18n.document.attr = {};
		defs.i18n.document.attr.name = "Όνομα";
		defs.i18n.document.attr.creator = "Δημιουργός";
		defs.i18n.document.attr.description = "Περιγραφή";
		defs.i18n.document.attr.creationDate = "Ημ/νία Προσθήκης";
		defs.i18n.document.attr.type = "Τύπος";
		defs.i18n.document.attr.size = "Μέγεθος";
		defs.i18n.document.attr.tasks = "Εργασίες";
		defs.i18n.document.confirm = {};
		defs.i18n.document.confirm.deletionText = "Να διαγραφεί το έγγραφο με όνομα";
		defs.i18n.document.confirm.deletionTitle = "Διαγραφή Εγγράφου";
		defs.i18n.document.error = {};
		defs.i18n.document.error.notFound = "Το έγγραφο δεν βρέθηκε";
		defs.i18n.document.error.existing = "Το έγγραφο υπάρχει ήδη";
		defs.i18n.document.error.unauthorized = "Δεν είστε εξουσιοδοτημένος να εκτελέσετε αυτή την ενέργεια";
		defs.i18n.document.error.noName = "Εισάγετε όνομα εγγράφου";
		defs.i18n.document.error.noFile = "Επιλέξτε αρχείο για αποθήκευση";
		
		defs.i18n.workflow = {};
		defs.i18n.workflow.attr = {};
		defs.i18n.workflow.attr.startDate = "Έναρξη";
		defs.i18n.workflow.attr.endDate = "Λήξη";
		defs.i18n.workflow.attr.reminderDate = "Υπενθύμιση";
		defs.i18n.workflow.attr.name = "Όνομα";
		defs.i18n.workflow.attr.description = "Περιγραφή";
		
		defs.i18n.task = {};
		defs.i18n.task.tableHeader = {};
		defs.i18n.task.tableHeader.title = "Εργασία";
		defs.i18n.task.tableHeader.start = "Έναρξη";
		defs.i18n.task.tableHeader.reminder = "Υπεν/ση";
		defs.i18n.task.tableHeader.end = "Λήξη";
		defs.i18n.task.tableHeader.documentCount = "Έγγραφα";
		defs.i18n.task.attr = {};
		defs.i18n.task.attr.name = "Εργασία";
		defs.i18n.task.attr.startDate = "Έναρξη";
		defs.i18n.task.attr.reminderDate = "Υπενθύμιση";
		defs.i18n.task.attr.endDate = "Λήξη";
		defs.i18n.task.attr.status = "Κατάσταση";
		defs.i18n.task.attr.documents = "Έγγραφα";
		defs.i18n.task.confirm = {};
		defs.i18n.task.confirm.deletionText = "Να διαγραφεί η εργασία με όνομα";
		defs.i18n.task.confirm.deletionTitle = "Διαγραφή Εργασίας";
		defs.i18n.task.status = {};
		defs.i18n.task.status.active = "Σε Εξέλιξη";
		defs.i18n.task.status.inactive = "Ανενεργή";
		defs.i18n.task.status.completed = "Ολοκληρωμένη";
		defs.i18n.task.status.cancelled = "Ακυρωμένη";
		defs.i18n.task.criticality = {};
		defs.i18n.task.criticality.nonBlocking = "Μη Αναγκαία";
		defs.i18n.task.criticality.blocking = "Αναγκαία";
		defs.i18n.task.criticality.critical = "Κρίσιμη";
		defs.i18n.task.error = {};
		defs.i18n.task.error.notFound = "Η εργασία δεν βρέθηκε";
		defs.i18n.task.error.existing = "Η εργασία υπάρχει ήδη";
		defs.i18n.task.error.unauthorized = "Δεν είστε εξουσιοδοτημένος να εκτελέσετε αυτή την ενέργεια";
		defs.i18n.task.error.noName = "Εισάγετε όνομα εργασίας";
		
		defs.i18n.attribute = {};
		defs.i18n.attribute.header = "Επιλογή";
		defs.i18n.attribute.sourceExisting = "Επιλογή από υπάρχουσες ιδιότητες";
		defs.i18n.attribute.sourceNew = "Νέα ιδιότητα";
		defs.i18n.attribute.name = "Όνομα";
		defs.i18n.attribute.value = "Τιμή";
		defs.i18n.attribute.category = "Κατηγορία";
		defs.i18n.attribute.attribute = "Ιδιότητα";
		
		defs.i18n.taxon = {};
		defs.i18n.taxon.Planning = "Πολεοδομικά";
		defs.i18n.taxon.Legal = "Νομικά";
		defs.i18n.taxon.Evaluation = "Εκτίμηση";
		defs.i18n.taxon.Actions = "Ενέργειες";
		defs.i18n.taxon.geog = ["Νομός", "Δήμος", "Οικισμός"];
		defs.i18n.taxon.geogCausative = ["Νομό", "Δήμο", "Οικισμό"];
		defs.i18n.taxon.geogFull = [null, "Δήμος Ν.Καλλικράτη", null];
		defs.i18n.taxon.geogAlt = [null, "Δήμος Ν.Καποδίστρια", null];
		defs.i18n.taxon.GPS = "Γ.Π.Σ.";
		defs.i18n.taxon.GPS_FEK = "ΦΕΚ Γ.Π.Σ.";
		defs.i18n.taxon.PlanningUnit = "Πολεοδομική Ενότητα";
		defs.i18n.taxon.LandUse = "Χρήση Γης";
		defs.i18n.taxon.SD = "Σ.Δ./Τομέα";
		defs.i18n.taxon.MSD = "Μ.Σ.Δ./Γ.Π.Σ."
		defs.i18n.taxon.Ownership = "Ιδιοκτησία";
		defs.i18n.taxon.OwnershipRights = "Κυριότητα";
		defs.i18n.taxon.Beneficiary = "Επικαρπία";
		defs.i18n.taxon.OwnershipTitles = "Τίτλοι Ιδιοκτ.";
		defs.i18n.taxon.Notary = "Συμβ/φος";
		defs.i18n.taxon.Leasing = "Μίσθωση";
		defs.i18n.taxon.SiteCategory = "Κατηγορία Ακινήτου";
		defs.i18n.taxon.Address = "Δ/νση";
		defs.i18n.taxon.Location = "Θέση/Χ.Ψ";
		defs.i18n.taxon.Area = "Εμβαδό (m\u00B2)";
		defs.i18n.taxon.SAO = "Σ.Α.Ο.";
		defs.i18n.taxon.SE = "Σ.Ε.";
		
		/*
		 * private String id = null;
		private String workflow = null;
		private String user = null;
		private String name = null;
		private long startDate = -1;
		private long endDate = -1;
		private Long reminderDate = null;
		private long statusDate = -1;
		private String extraData = null;
		private WorkflowTaskStatus status;
		private Criticality critical;
		private int numDocuments = -1;
		 */
		defs.i18n.placeholder = {};
		defs.i18n.placeholder["SEARCH"] = "πληκτρολογήστε μια λέξη-κλειδί";
		
		defs.imgLoc = "resources/img";
		defs.imgThemeLoc = "orange";
		defs.img = {};
		defs.img["SEARCH_ICON"] = "search.png";
		defs.img["NAV_LEGEND_ICON"] = "orange_legend_icon.png";
		defs.img["NAV_SEARCH_ICON"] = "orange_search_icon.png";
		defs.img["NAV_SETTINGS_ICON"] = "orange_settings_icon.png";
		defs.img["NAV_PROJECTS_ICON"] = "orange_projects_icon.png";
		defs.img["NAV_CLOSE_ICON"] = "close_pane.png";
		defs.img["NAV_BACK_ICON"] = "back.png";
		defs.img["TOOLS_POLYGON_ICON"] = "polygon.png";
		defs.img["TOOLS_POINT_ICON"] = "point.png";
		defs.img["ACTIVE_ICON"] = "active_state.png";
		defs.img["COMPLETED_ICON"] = "completed_state.png";
		defs.img["INACTIVE_ICON"] = "paused_state.png";
		defs.img["CANCELLED_ICON"] = "cancelled_state.png";
		defs.img["ADD_DOCUMENT_ICON"] = "add_document.png";
		defs.img["DELETE_DOCUMENT_ICON"] = "delete_document.png";
		defs.img["VIEW_DOCUMENT_ICON"] = "view_doc.png";
		defs.img["DOWNLOAD_DOCUMENT_ICON"] = "download(12x14).png";
		defs.img["UPLOAD_ICON"] = "upload.png";
		defs.img["ITEM_NEW_ICON"] = "new_item.png";
		defs.img["ITEM_DELETE_ICON"] = "delete.png";
		defs.img["ITEM_DETAILS_ICON"] = "details.png";
		defs.img["CALENDAR_ICON"] = "calendar.png";
		defs.img["ITEM_EDIT_ICON"] = "edit.png";
		defs.img["ITEM_DONE_ICON"] = "tick_do.png";
		defs.img["ITEM_CANCEL_ICON"] = "delete(h14)dark_orange.png";
		defs.img["ITEM_ZOOM_ICON"] = "zoom_in.png";
		defs.img["PAGINATION_FIRST"] = "first.png";
		defs.img["PAGINATION_PREVIOUS"] = "previous.png";
		defs.img["PAGINATION_NEXT"] = "next.png";
		defs.img["PAGINATION_LAST"] = "last.png";
		defs.img["COLUMN_SORT_ASC"] = "up.png";
		defs.img["COLUMN_SORT_DESC"] = "down.png";
		defs.img["COLUMN_SORT_ASC_DISABLED"] = "up_disabled.png";
		defs.img["COLUMN_SORT_DESC_DISABLED"] = "desc_disabled.png";
		defs.img["COLUMN_SORT"] = "updown.png";
		defs.img["MARKER_ICON_LARGE"] = "pinpoint(25x35).png";
		defs.img["MARKER_ICON_SMALL"] = "pinpoint(20x28).png";
		
		defs.geographyTaxonomyType="${geographyTaxonomyType}";
		defs.planningTaxonomyType="${planningTaxonomyType}";
		defs.projectInfoCategories = [
				<c:forEach items="${projectInfoCategories}" var="category" varStatus="status">  
		                 '${category}'
				    <c:if test="${!status.last}">,</c:if>  
	    		</c:forEach>  
			];
		
		defs.projectInfoCategoryTypes = {
				<c:forEach items="${projectInfoCategoryTypes}" var="category" varStatus="status">
					"${category.key}" :  [
					                             	<c:forEach items="${category.value}" var="type" varStatus="typeStatus">
					                             		"${type}"
					                             		<c:if test="${!typeStatus.last}">,</c:if>
					                             	</c:forEach>
					                              ]
					<c:if test="${!status.last}">,</c:if>
				</c:forEach>
		};
		
		defs.projectEditableTaxonomies = [
			<c:forEach items="${projectEditableTaxonomies}" var="userTaxonomy" varStatus="status">
				'${userTaxonomy}'
				<c:if test="${!status.last}">,</c:if>
			</c:forEach>
				
		];
		
		defs.customUserTaxonomyNames = {
				<c:forEach items="${customUserTaxonomyNames}" var="cut" varStatus="status">
					'${cut.key}':'${cut.value}'
					<c:if test="${!status.last}">,</c:if>
				</c:forEach>
		};
		
		defs.geographyHierarchy = [
		      	<c:forEach items="${geographyHierarchy}" var="g" varStatus="status">
		      		'${g}'
		      		<c:if test="${!status.last}">,</c:if>
		      	</c:forEach>
		 ];

		defs.altGeographyHierarchy = [
		           		      	<c:forEach items="${altGeographyHierarchy}" var="g" varStatus="status">
		           		      		'${g}'
		           		      		<c:if test="${!status.last}">,</c:if>
		           		      	</c:forEach>
		           		 ];
		
		 $(document).ready(function () {
			show(defs, layers, bounds);
		 });
      </script>
</head>
	<sec:authorize access="isAnonymous()">
	   <body class="anonymous">
	</sec:authorize>
	<sec:authorize access="isAuthenticated()">
    		<body>
	</sec:authorize>
	<div id="navbar-top">
		<div id="corner-top"></div>
		<div id="separator_hor_corner" class="separator_hor"></div>
		<div id="navbar-top-toolbar">
			<div id="tools" class="navbar-top-item">
				<div id="tools-drawing">
					<div id="tools-drawing-point" class="drawing-tool"></div>
					<div id="tools-drawing-polygon" class="drawing-tool"></div>
					<div id="tools-drawing-freehand" class="drawing-tool"></div>
					<div id="tools-drawing-select" class="drawing-tool"></div>
					<div id="tools-drawing-modify" class="drawing-tool"></div>
					<div id="tools-drawing-drag" class="drawing-tool"></div>
					<div id="tools-drawing-hand" class="drawing-tool"></div>
					<div id="tools-drawing-delete" class="drawing-tool"></div>
					<div id="tools-drawing-done" class="drawing-tool"></div>
					<div id="tools-drawing-cancel" class="drawing-tool"></div>
				</div>
				<div id="loading"></div>
			</div>
			<div class="separator_hor"></div>
			<div id="title_bar">
				<div id="title" class="navbar-top-item"> 
					<h1></h1>
					<div id="back-right-pane" class="back-pane"></div>
					<div id="close-right-pane" class="close-pane"></div>
				</div>
			</div>
		</div>
	</div>
	<div id="navbar-left">
		<div id="search" class="navbar-left-item"></div>
		<div class="separator_ver"></div>
		<div id="legend" class="navbar-left-item"></div>
		<div class="separator_ver"></div>
		<div id="projects" class="navbar-left-item"></div>
		<div class="separator_ver"></div>
		<div id="settings" class="navbar-left-item"></div>
	</div>
	
		<sec:authorize access="isAnonymous()">
			<div id="user-widget" class="anonymous">
			    <p>
			        <a href="login">Sign In</a>
			    </p>
			</div>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
				<div id="user-widget">
	    			<sec:authorize access="hasRole('ROLE_admin')"><a href="admin"></sec:authorize>
					<p> <sec:authentication property="principal.username" /> <sec:authorize access="hasRole('ROLE_admin')"> (admin)</sec:authorize></p>
					<sec:authorize access="hasRole('ROLE_admin')"></a></sec:authorize>
	    			<div id="logout">
	    				<c:url var="logoutUrl" value="/logout"/>
						<form action="${logoutUrl}" method="post">
							<input type="submit" value="Sign out" />
						</form>
	    			</div>
	    		</div>
		</sec:authorize>
	</div>
	
	<div id="mainScreen">
		<div id = "mapCon">
			<div id="mapContainer">
					<div id="map">
					<%-- <br>
						<input type="checkbox" class="shadowed" id="chk1"/>
						<label for="chk1"><span></span>Label</label>
						<input type="radio" class="chkbox-small" id="group1Milk" name="group1" value="Milk">
						<label for="group1Milk"><span></span>Milk</label>
						<input type="radio" class="chkbox-small" id="group1Butter" name="group1" value="Butter" checked>
						<label for="group1Butter"><span></span>Butter</label>
						<input type="radio" class="chkbox-small" id="group1Cheese" name="group1" value="Cheese">
						<label for="group1Cheese"><span></span>Cheese</label>
						<input type="radio" class="chkbox-small" id="group2Water" name="group2" value="Water">
						<label for="group2Water"><span></span>Water</label>
						<input type="radio" class="chkbox-small" id="group2Beer" name="group2" value="Beer">
						<label for="group2Beer"><span></span>Beer</label>
						<input type="radio" class="chkbox-small" id="group2Wine" name="group2" value="Wine" checked>
						<label for="group2Wine"><span></span>Wine</label> --%>
					</div>
			</div>
		</div>
		<div id="right-pane-container">
			<!-- <div id="right-pane-offset-header">
			</div> -->
			<div id="right-pane">
				<div id="right-pane-overlay">
				<div id="right-pane-overlay-content">
				</div>
				</div>
			</div>
		</div>
		
		<div id="tools-info">
			<div id='tools-info-con'>
				<div class="dropup">
					<div class="dropdown-toggle" data-toggle="dropdown">
						<div id="tools-info-coords"></div>
						<span class="caret"></span>
					</div>
			        <ul class="dropdown-menu">
			          <li><a href="#" id="tools-info-coords-wgs">EPSG:4326 (WGS84)</a></li>
			          <li><a href="#" id="tools-info-coords-gwm">EPSG:900913 (Google Web Mercator)</a></li>
			          <li><a href="#" id="tools-info-coords-gg">EPSG:2100 (Greek Grid)</a></li>
			        </ul>
			    </div>
				<div id="tools-info-scale"></div>
			</div>
  		</div>
				   
		<div class = "modal" id="confirmationModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <div class="close-modal-pane" data-dismiss="modal" aria-hidden="true"></div>
	          <h4 id="confirmationModalTitle" class="modal-title"></h4>
	        </div>
	        <div class="modal-body" id="confirmationModalBody">
	        </div>
	        <div class="modal-footer" id="confirmationModalFooter">
	          <button type="button" id="confirmationModalYesButton" class="btn btn-primary" data-dismiss="modal"></button>
	          <button type="button" id="confirmationModalNoButton" class="btn btn-primary" data-dismiss="modal"></button>
	        </div>
	       </div>
	      </div>
		</div>
		
		<div class="modal" id="addProjectPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <div class="close-modal-pane" data-dismiss="modal" aria-hidden="true"></div>
	          <h4 id="addProjectTitle" class="modal-title"></h4>
	        </div>
	        <div class="modal-body" id="addProjectModalBody">
	        <form id="addProjectForm" class="form-horizontal">
					<div class="form-group">
						<label id="addProjectFormLabelname" for="addProjectFormTextBoxname" class="addProjectFormElement col-md-2 label-left"></label>
						<div class="addProjectFormElement col-md-4">
							<input type="text" id="addProjectFormTextBoxname" class="addProjectFormElement input-sm form-control" name="name">
						</div>
						<label id="addProjectFormLabelclient" for="addProjectFormTextBoxclient" class="addProjectFormElement col-md-2 label-left"></label>
						<div class="addProjectFormElement col-md-4">
							<input type="text" id="addProjectFormTextBoxclient" class="addProjectFormElement input-sm form-control" name="client">
						</div>
					</div>
					<div class="form-group chkbox-container">
							<input type="checkbox" id="addProjectFormCheckBoxtemplate" class="chkbox-bg-grey" name="template">
							<label id="addProjectFormLabeltemplate" for="addProjectFormCheckBoxtemplate" class="addProjectFormElement">
								<span></span>
							</label>
					</div>
					<div class="form-group">
						<label id="addProjectFormLabeldescription" for="addProjectFormTextBoxdescription" class="addProjectFormElement col-md-2 label-left"></label>
						<div class="addProjectFormElement col-md-10">
							<textarea rows="3" id="addProjectFormTextBoxdescription" class="addProjectFormElement form-control" name="description">
							</textarea>
						</div>
					</div>
					<div class="panel-group" id="addProjectFormOptional">
						<div class="">
					    <div class="panel-heading">
					      <h4 class="panel-title">
					        <a id="addProjectFormOptionalA" data-toggle="collapse" data-parent="#addProjectFormOptional" href="#addProjectFormWorkflowInfo">
					        </a>
					      </h4>
					    </div>
					    <div id="addProjectFormWorkflowInfo" class="panel-collapse collapse">
					      <div class="panel-body">
					      	<div class="form-group">
								<label id="addProjectFormLabelwname" for="addProjectFormTextBoxwname" class="addProjectFormElement col-md-2 label-left"></label>
								<div class="addProjectFormElement col-md-4">
									<input type="text" id="addProjectFormTextBoxwname" class="addProjectFormElement input-sm form-control" name="wname">
								</div>
							</div>
							<div class="form-group">
								<label id="addProjectFormLabelwstartDate" for="addProjectFormTextBoxwstartDate" class="addProjectFormElement col-md-2"></label>
								<div class="addProjectFormElement col-md-4">
									<div class="input-group">
										<input type="text" id="addProjectFormTextBoxwstartDate" class="addProjectFormElement input-sm form-control" name="wstartDate">
										<span class="input-group-addon">
										</span>
									</div>
								</div>
								<label id="addProjectFormLabelwendDate" for="addProjectFormTextBoxwendDate" class="addProjectFormElement col-md-2"></label>
								<div class="addProjectFormElement col-md-4">
									<div class="input-group">
										<input type="text" id="addProjectFormTextBoxwendDate" class="addProjectFormElement input-sm form-control" name="wendDate">
										<span class="input-group-addon">
										</span>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label id="addProjectFormLabelwreminderDate" for="addProjectFormTextBoxwreminderDate" class="addProjectFormElement col-md-offset-5 col-md-3"></label>
								<div class="addProjectFormElement col-md-4">
									<div class="input-group">
										<input type="text" id="addProjectFormTextBoxwreminderDate" class="addProjectFormElement input-sm form-control" name="wreminderDate">
										<span class="input-group-addon">
										</span>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label id="addProjectFormLabelwdescription" for="addProjectFormTextBoxwdescription" class="addProjectFormElement col-md-2 label-left"></label>
								<div class="addProjectFormElement col-md-10">
									<textarea rows="3" id="addProjectFormTextBoxwdescription" class="addProjectFormElement form-control" name="wdescription">
									</textarea>
								</div>
							</div>
					      </div>
					    </div>
					    </div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-6 col-md-3">
							<button type="button" id="addProjectFormShapeButton" class="addProjectFormButton btn btn-primary" value="Shape"></button>
						</div>
						<div class="col-md-2">
							<button type="button" id="addProjectFormSaveButton" class="addProjectFormButton btn btn-primary" value="Save"></button>
						</div>
					</div>
				</form>
	        </div>
	        <!-- <div class="modal-footer" id="addUserModalFooter">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	          <button type="button" class="btn btn-primary">Save</button>
	        </div> -->
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	</div>
	
	<div class="modal" id="addTaskPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <div class="close-modal-pane" data-dismiss="modal" aria-hidden="true"></div>
	          <h4 id="addTaskTitle" class="modal-title"></h4>
	        </div>
	        <div class="modal-body" id="addTaskModalBody">
	        	<form id="addTaskForm" class="form-horizontal">
					<div class="form-group">
						<label id="addTaskFormLabelname" for="addTaskFormTextBoxname" class="addTaskFormElement col-md-2 label-left"></label>
						<div class="addTaskFormElement col-md-6">
							<input type="text" id="addTaskFormTextBoxname" class="addTaskFormElement input-sm form-control" name="name">
						</div>
					</div>

					<div class="form-group">
						<label id="addTaskFormLabelstartDate" for="addTaskFormTextBoxstartDate" class="addTaskFormElement col-md-2"></label>
						<div class="addTaskFormElement col-md-4">
							<div class="input-group">
								<input type="text" id="addTaskFormTextBoxstartDate" class="addTaskFormElement input-sm form-control" name="startDate">
								<span class="input-group-addon">
								</span>
							</div>
						</div>
						<label id="addTaskFormLabelendDate" for="addTaskFormTextBoxendDate" class="addTaskFormElement col-md-2"></label>
						<div class="addTaskFormElement col-md-4">
							<div class="input-group">
								<input type="text" id="addTaskFormTextBoxendDate" class="addTaskFormElement input-sm form-control" name="endDate">
								<span class="input-group-addon">
								</span>
							</div>
						</div>
					</div>
					<div class="form-group">
						<label id="addTaskFormLabelreminderDate" for="addTaskFormTextBoxreminderDate" class="addTaskFormElement col-md-2"></label>
						<div class="addTaskFormElement col-md-4">
							<div class="input-group">
								<input type="text" id="addTaskFormTextBoxreminderDate" class="addTaskFormElement input-sm form-control" name="reminderDate">
								<span class="input-group-addon">
								</span>
							</div>
						</div>
					</div>
					<div class="form-group">
						<input type="radio" id="addTaskFormRadiocriticalityNB" class="" name="criticality" value="NONBLOCKING">
						<label id="addTaskFormLabelcriticalityNB" for="addTaskFormRadiocriticalityNB" class="addTaskFormElement">
							<span></span>
						</label>
						<input type="radio" id="addTaskFormRadiocriticalityB" class="" name="criticality" value="BLOCKING">
						<label id="addTaskFormLabelcriticalityB" for="addTaskFormRadiocriticalityB" class="addTaskFormElement">
							<span></span>
						</label>
						<input type="radio" id="addTaskFormRadiocriticalityC" class="" name="criticality" value="CRITICAL">
						<label id="addTaskFormLabelcriticalityC" for="addTaskFormRadiocriticalityC" class="addTaskFormElement">
							<span></span>
						</label>
					</div>
					<div class="form-group">
						<div class="col-md-offset-9 col-md-2">
							<button type="button" id="addTaskFormSaveButton" class="addTaskFormButton btn btn-primary" value="Save"></button>
						</div>
					</div>
				</form>
	        </div>
	        <!-- <div class="modal-footer" id="addUserModalFooter">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	          <button type="button" class="btn btn-primary">Save</button>
	        </div> -->
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<div class="modal" id="addAttributePopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <div class="close-modal-pane" data-dismiss="modal" aria-hidden="true"></div>
	          <h4 id="addAttributeTitle" class="modal-title"></h4>
	        </div>
	        <div class="modal-body" id="addAttributeModalBody">
	        	<form id="addAttributeForm" class="form-horizontal">
					<div class="form-group">
						<input type="radio" id="addAttributeFormRadiosourceExisting" class="col-md-2" name="attributeSource" value="existing">
						<label id="addAttributeFormLabelsourceExisting" for="addAttributeFormRadiosourceExisting" class="col-md-6 addAttributeFormElement">
							<span></span>
						</label>
						<input type="radio" id="addAttributeFormRadiosourceNew" class="col-md-2" name="attributeSource" value="new">
						<label id="addAttributeFormLabelsourceNew" for="addAttributeFormRadiosourceNew" class="col-md-6 addAttributeFormElement">
							<span></span>
						</label>
					</div>
					
					<div class="form-group">
						<label id="addAttributeFormLabelselector" for="addAttributeFormselector" class="addAttributeFormElement col-md-2 label-left"></label>
						<div class="addAttributeFormElement col-md-6">
							<select id="addAttributeFormselector" class="addAttributeFormElement input-sm form-control" ></select>
						</div>
					</div>
					
					<div class="form-group">
						<label id="addAttributeFormLabelname" for="addAttributeFormname" class="addAttributeFormElement col-md-2 label-left"></label>
						<div class="addAttributeFormElement col-md-4">
							<input type="text" id="addAttributeFormname" class="addAttributeFormElement input-sm form-control" name="attrName" />
						</div>
						<label id="addAttributeFormLabelvalue" for="addAttributeFormvalue" class="addAttributeFormElement col-md-2 label-left"></label>
						<div class="addAttributeFormElement col-md-4">
							<input type="text" id="addAttributeFormvalue" class="addAttributeFormElement input-sm form-control" name="attrValue" />
						</div>
					</div>
					
					<div class="form-group">
						<div class="col-md-offset-9 col-md-2">
							<button type="button" id="addAttributeFormSaveButton" class="addAttributeFormButton btn btn-primary" value="Save"></button>
						</div>
					</div>
				</form>
	        </div>
	        <!-- <div class="modal-footer" id="addUserModalFooter">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	          <button type="button" class="btn btn-primary">Save</button>
	        </div> -->
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<div class="modal" id="addDocumentPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <div class="close-modal-pane" data-dismiss="modal" aria-hidden="true"></div>
	          <h4 id="addDocumentTitle" class="modal-title"></h4>
	        </div>
	        <div class="modal-body" id="addDocumentModalBody">
	        	<form id="addDocumentForm" class="form-horizontal">
	        		<div class="form-group">
						<label id="addDocumentFormLabeldescription" for="addDocumentFormTextBoxdescription" class="addDocumentFormElement col-md-2 label-left"></label>
						<div class="addDocumentFormElement col-md-10">
							<textarea rows="3" id="addDocumentFormTextBoxdescription" class="addDocumentFormElement form-control" name="description">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<label id="addDocumentFormLabeldocument" for="addDocumentFormFiledocument" class="addDocumentFormElement col-md-2 label-left"></label>
						<div class="addDocumentFormElement col-md-6">
							<input type="file" id="addDocumentFormFiledocument" class="addDocumentFormElement input-sm form-control" name="documentFile">
						</div>
					</div>
					
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addDocumentFormSaveButton" class="addDocumentFormButton btn btn-primary" value="Save"></button>
						</div>
					</div>
				</form>
	        </div>
	        <!-- <div class="modal-footer" id="addUserModalFooter">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	          <button type="button" class="btn btn-primary">Save</button>
	        </div> -->
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
	
	<form id="editProjectForm" class="form-horizontal">
		<div class="form-group">
			<label id="editProjectFormLabelstatus" for="editProjectFormStatus" class="editProjectFormElement col-md-2 control-label label-left label-left"></label>
			<div id="editProjectFormStatus" class="editProjectFormElement col-md-8">
				<div class="row">
				<div class="col-xs-1">
					<div id="editProjectFormStatusIcon" class="editProjectFormElement statusIcon">
					</div>
				</div>
				<div id="editProjectFormStatusText">
				</div>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label id="editProjectFormLabelname" for="editProjectFormTextBoxname" class="editProjectFormElement col-md-2 control-label label-left"></label>
			<div class="editProjectFormElement col-md-6">
				<input type="text" id="editProjectFormTextBoxname" class="editProjectFormElement input-sm form-control" name="name">
			</div>
		</div>
		<div class="form-group">
			<label id="editProjectFormLabelclient" for="editProjectFormTextBoxclient" class="editProjectFormElement col-md-2 control-label label-left"></label>
			<div class="editProjectFormElement col-md-6">
				<input type="text" id="editProjectFormTextBoxclient" class="editProjectFormElement input-sm form-control" name="client">
			</div>
		</div>
		<div class="form-group">
			<label id="editProjectFormLabeldescription" for="editProjectFormTextBoxdescription" class="editProjectFormElement col-md-2 control-label label-left"></label>
			<div class="editProjectFormElement col-md-9">
				<textarea rows="2" id="editProjectFormTextBoxdescription" class="editProjectFormElement form-control" name="description">
				</textarea>
			</div>
		</div>
		<div class="form-group">
			<label id="editProjectFormLabelshape" for="editProjectFormEditShape" class="editProjectFormElement col-md-2 control-label label-left"></label>
			<div id="editProjectFormEditShape" class="col-md-1 editProjectFormElement">	
			</div>
		</div>
		<div class="form-group">
			<div class="col-md-12">
			<div class="row">
				<div class="col-md-10 col-md-offset-1 editFormDateSection">
					<label id="editProjectFormLabelwstartDate" for="editProjectFormTextBoxwstartDate" class="editProjectFormElement col-xs-4 control-label label-top"></label>
					<label id="editProjectFormLabelwendDate" for="editProjectFormTextBoxwendDate" class="editProjectFormElement col-xs-4 control-label label-top"></label>
					<label id="editProjectFormLabelwreminderDate" for="editProjectFormTextBoxwreminderDate" class="editProjectFormElement col-xs-4 control-label label-top"></label>
				</div>
			</div>
			</div>
		</div>
		<div class="form-group">
			<div class="col-md-12">
			<div class="row">
				<div class="col-md-10 col-md-offset-1">
					<div class="editProjectFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editProjectFormTextBoxwstartDate" class="editProjectFormElement input-sm form-control" name="wstartDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
					<div class="editProjectFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editProjectFormTextBoxwreminderDate" class="editProjectFormElement input-sm form-control" name="wreminderDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
					<div class="editProjectFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editProjectFormTextBoxwendDate" class="editProjectFormElement input-sm form-control" name="wendDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
				</div>
			</div>
			</div>
		</div>
	<!-- 	<div class="form-group">
			<div class="col-md-offset-9 col-md-2">
				<button type="button" id="editProjectFormSaveButton" class="editProjectFormButton btn btn-primary" value="Save"></button>
			</div>
		</div> -->
	</form>
	
	<form id="editTaskForm" class="form-horizontal">
		<div class="form-group">
			<label id="editTaskFormLabelstatus" for="editTaskFormStatus" class="editTaskFormElement col-md-2 control-label label-left"></label>
			<div id="editTaskFormStatus" class="editTaskFormElement col-md-8">
				<div class="row">
				<div class="col-xs-1">
					<div id="editTaskFormStatusIcon" class="editTaskFormElement statusIcon">
					</div>
				</div>
				<div id="editTaskFormStatusText">
				</div>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label id="editTaskFormLabelname" for="editTaskFormTextBoxname" class="editTaskFormElement col-md-2 control-label label-left"></label>
			<div class="editTaskFormElement col-md-6">
				<input type="text" id="editTaskFormTextBoxname" class="editTaskFormElement input-sm form-control" name="name">
			</div>
		</div>
		<div class="form-group">
			<label id="editTaskFormLabeldocuments" for="editTaskFormTabledocuments" class="editTaskFormElement col-md-2 control-label label-left"></label>
			<div id="editTaskFormDocuments" class="editTaskFormElement col-md-7">
				<table id="editTaskFormTabledocuments"></table>
			</div>
			<div id="editTaskFormnewDocumentWidget" class="editFormElement col-md-3">
			</div>
		</div>
		<div class="form-group">
			<div class="col-md-12">
			<div class="row">
				<div class="col-md-10 col-md-offset-1 editFormDateSection">
					<label id="editTaskFormLabelstartDate" for="editTaskFormTextBoxstartDate" class="editTaskFormElement col-xs-4 control-label label-top"></label>
					<label id="editTaskFormLabelendDate" for="editTaskFormTextBoxendDate" class="editTaskFormElement col-xs-4 control-label label-top"></label>
					<label id="editTaskFormLabelreminderDate" for="editTaskFormTextBoxreminderDate" class="editTaskFormElement col-xs-4 control-label label-top"></label>
				</div>
			</div>
			</div>
		</div>
		<div class="form-group">
			<div class="col-md-12">
			<div class="row">
				<div class="col-md-10 col-md-offset-1">
					<div class="editTaskFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editTaskFormTextBoxstartDate" class="editTaskFormElement input-sm form-control" name="startDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
					<div class="editTaskFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editTaskFormTextBoxreminderDate" class="editTaskFormElement input-sm form-control" name="reminderDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
					<div class="editTaskFormElement col-xs-4">
						<div class="input-group">
							<input type="text" id="editTaskFormTextBoxendDate" class="editTaskFormElement input-sm form-control" name="endDate">
							<span class="input-group-addon">
							</span>
						</div>
					</div>
				</div>
			</div>
			</div>
		</div>
		<div class="form-group">
			<div class="editFormRadio col-md-6 col-md-offset-3">
				<input type="radio" id="editTaskFormRadiocriticalityNB" class="" name="criticality" value="NONBLOCKING">
				<label id="editTaskFormLabelcriticalityNB" for="editTaskFormRadiocriticalityNB" class="editTaskFormElement">
					<span></span>
				</label>
				<input type="radio" id="editTaskFormRadiocriticalityB" class="" name="criticality" value="BLOCKING">
				<label id="editTaskFormLabelcriticalityB" for="editTaskFormRadiocriticalityB" class="editTaskFormElement">
					<span></span>
				</label>
				<input type="radio" id="editTaskFormRadiocriticalityC" class="" name="criticality" value="CRITICAL">
				<label id="editTaskFormLabelcriticalityC" for="editTaskFormRadiocriticalityC" class="editTaskFormElement">
					<span></span>
				</label>
			</div>
		</div>
	<!-- 	<div class="form-group">
			<div class="col-md-offset-9 col-md-2">
				<button type="button" id="editTaskFormSaveButton" class="editTaskFormButton btn btn-primary" value="Save"></button>
			</div>
		</div> -->
	</form>
	
	<form id="editDocumentForm" class="form-horizontal">
		<div class="form-group">
			<label id="editDocumentFormLabelname" for="editDocumentFormTextBoxname" class="editDocumentFormElement col-md-2 control-label label-left"></label>
			<div class="editDocumentFormElement col-md-6">
				<input type="text" id="editDocumentFormTextBoxname" class="editDocumentFormElement input-sm form-control" name="name">
			</div>
		</div>
		<div class="form-group">
			<label id="editDocumentFormLabeldescription" for="editDocumentFormTextBoxdescription" class="editDocumentFormElement col-md-2 control-label label-left"></label>
			<div class="editDocumentFormElement col-md-10">
				<textarea rows="3" id="editDocumentFormTextBoxdescription" class="editDocumentFormElement form-control" name="description">
				</textarea>
			</div>
		</div>
		<div class="form-group">
			<label id="editDocumentFormLabeldocument" for="editDocumentFormFiledocument" class="editDocumentFormElement col-md-2 control-label label-left"></label>
			<div class="editDocumentFormElement col-md-6">
				<input type="file" id="editDocumentFormFiledocument" class="editDocumentFormElement input-sm" name="documentFile">
				<div id="editDocumentFormuploadWidget">
				</div>
			</div>
		</div>
		<div class="form-group">
			<label id="editDocumentFormLabeltasks" for="editDocumentFormTabletasks" class="editDocumentFormElement col-md-2 control-label label-left"></label>
			<div id="editDocumentFormTasks" class="editDocumentFormElement col-md-7">
				<table id="editDocumentFormTabletasks"></table>
			</div>
			<div id="editDocumentFormnewTaskWidget" class="editFormElement col-md-3">
			</div>
		</div>
	<!-- 	<div class="form-group">
			<div class="col-md-offset-9 col-md-2">
				<button type="button" id="editDocumentFormSaveButton" class="editDocumentFormButton btn btn-primary" value="Save"></button>
			</div>
		</div> -->
	</form>
	
	<div id="ol-info-scale"></div>
	
	<div id="search-pane" class="geopolis-pane">
	</div>
	
	<div id="legend-pane" class="geopolis-pane">
	</div>
	
	<div id="projects-pane" class="geopolis-pane">
	</div>
	
	<div id="settings-pane" class="geopolis-pane">
	</div>
	
	<div id="project-nav" class="sub-nav">
		<!-- Nav tabs -->
		<ul class="nav nav-tabs">
		  <li><a href="#project-info" data-toggle="tab"></a></li>
		  <li><a href="#project-tasks" data-toggle="tab"></a></li>
		  <li><a href="#project-documents" data-toggle="tab"></a></li>
		  <li><a href="#project-report" data-toggle="tab"></a></li>
		</ul>
		
		<!-- Tab panes -->
		<div id="rpoc-project-tabs" class="tab-content">
		  <div class="tab-pane active" id="project-info"></div>
		  <div class="tab-pane" id="project-tasks"></div>
		  <div class="tab-pane" id="project-documents"></div>
		  <div class="tab-pane" id="project-report"></div>
		</div>
	</div>
	
	<div id="legend-nav" class="sub-nav">
	<!-- Nav tabs -->
		<ul class="nav nav-tabs">
		  <li><a href="#legend-sites" data-toggle="tab"></a></li>
		  <li><a href="#legend-planning" data-toggle="tab"></a></li>
		  <li><a href="#legend-maps" data-toggle="tab"></a></li>
		  <li><a href="#legend-poi" data-toggle="tab"></a></li>
		</ul>
		
		<!-- Tab panes -->
		<div id="rpoc-legend-tabs" class="tab-content">
		  <div class="tab-pane active" id="legend-sites"></div>
		  <div class="tab-pane" id="legend-planning"></div>
		  <div class="tab-pane" id="legend-maps"></div>
		  <div class="tab-pane" id="legend-poi"></div>
		</div>
	</div>
</body>
</html>