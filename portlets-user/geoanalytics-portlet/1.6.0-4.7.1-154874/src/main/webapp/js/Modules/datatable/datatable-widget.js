$.widget('dt.PortletDataTable', {
    options : {
        selectStyle : "single",
        checkBox : true,
        toolbar : null,
        pageLength : 10,
        onInitDtCallback : null,
        onDrawDtCallback : null
    },
    _create : function() {

	    var widgetInstance = this;

	    this.toolbar = this.options.toolbar;
	    this.selectStyle = this.options.selectStyle;
	    this.checkBox = this.options.checkBox || this.checkBox;

	    var columnDefs = this.options.columnDefs;
	    var selectStyle = this.options.selectStyle;
	    var order = this.options.order;
	    var ajax = this.options.ajax;
	    var rowId = this.options.rowId;
	    var pageLength = this.options.pageLength || this.pageLength;
	    if(typeof this.options.onInitDtCallback !== 'undefined'){
	    	this.onInitDtCallback = this.options.onInitDtCallback;
	    }
	    if(typeof this.options.onDrawDtCallback !== 'undefined'){
	    	this.onDrawDtCallback = this.options.onDrawDtCallback;
	    }
	    var scrollX;
	    if(typeof this.options.scrollX !== 'undefined'){
	    	scrollX = this.options.scrollX;
	    } else {
	    	scrollX = false;
	    }

	    var columns = [];

	    this.element.addClass("dataTable portlet-datatable");

	    if (this.checkBox) {
		    columnDefs = [ {
		        width : "20px",
		        orderable : false,
		        searchable : false,
		        fieldName : "checkbox"
		    } ].concat(columnDefs);

		    for (var i = 0; i < order.length; i++) {
			    order[i][0]++;
		    }
	    }

	    for (var i = 0; i < columnDefs.length; i++) {
		    columnDefs[i].className = columnDefs[i].className == null ? "portlet-datatable-cell" : columnDefs[i].className + " portlet-datatable-cell";
		    columnDefs[i].targets = [ i ];
		    columns.push({
			    data : columnDefs[i].fieldName
		    });
	    }

	    columnDefs.push({
	        targets : "_all",
	        render : function(data, type, full, meta) {
		        return data == null ? "" : data;
	        }
	    });

	    this.wrapDataSrcCallBack(ajax);
	    this.wrapCompleteCallBack(ajax);

	    this.dataTable = this.element.DataTable({
	        ajax : ajax,
	        columns : columns,
	        columnDefs : columnDefs,
	        autoWidth : false,
	        lengthChange : false,
	        rowId : rowId, // persists selected rows after ajax.reload() using specific column value
	        order : order,
	        'scrollX' : scrollX,
	        pagingType : "full_numbers",
	        pageLength : pageLength,
	        language : {
	            emptyTable : "No data available",
	            search : "_INPUT_",
	            searchPlaceholder : "Search...",
	            paginate : {
	                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
	                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
	                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
	                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
	            }
	        },
	        fnDrawCallback : function() {
		        if (widgetInstance.getRowsCount() < pageLength) {
			        widgetInstance.element.siblings(".dataTables_paginate").hide();
		        } else {
			        widgetInstance.element.siblings(".dataTables_paginate").show();
		        }
	        },
	        select : {
		        style : selectStyle
	        }
	    });

	    this.styling();

	    this.initUIbindings();
    },
    initUIbindings : function() {
	    var widgetInstance = this;

	    // click and unclick events

	    $(document.body).on("click", "#" + this.element.attr("id") + " td.portlet-datatable-cell", function() {
		    var selectedRow = $(this).closest("tr");

		    if (selectedRow.hasClass("selected")) {
			    if (widgetInstance.selectStyle === "single") {
				    var rows = $(this).closest("tr").siblings("tr");
				    widgetInstance.deSelectRowCallBack(rows);
			    }

			    widgetInstance.selectRowCallBack(selectedRow);
		    } else {
			    widgetInstance.deSelectRowCallBack(selectedRow);
		    }
	    });

	    // draw tick checkboxes after changing page

	    this.element.on('draw.dt', function() {
		    if (widgetInstance.getRowsCount() > 0) {
			    widgetInstance.checkBox && widgetInstance.element.find("tr > td:first-child").addClass("checkbox");
			    widgetInstance.deSelectRowCallBack(widgetInstance.getRows());
			    widgetInstance.selectRowCallBack(widgetInstance.getSelectedRows());
		    }
		    
		  //	on draw.dt custom event
		    if(widgetInstance.onDrawDtCallback !== null && typeof widgetInstance.onDrawDtCallback !== 'undefined'){
		    	widgetInstance.onDrawDtCallback();
		    }
	    });

	    if (widgetInstance.selectStyle === "multi") {
		    this.element.find(".datatable-select-all-checkbox").on("click", function() {
			    var selectedRows = $(this).closest("table").find("tbody").find("tr");

			    if (widgetInstance.getSelectedRowsCount() == widgetInstance.getRowsCount()) {
				    widgetInstance.deselectAllRows();
				    widgetInstance.deSelectRowCallBack(selectedRows);
			    } else {
				    widgetInstance.checkBox && widgetInstance.element.find('.datatable-select-all-checkbox').prop('checked', true);
				    widgetInstance.selectAllRows();
				    widgetInstance.selectRowCallBack(selectedRows);
			    }
		    });
	    }
	    
	    //on inid.dt custom event
	    if(this.onInitDtCallback !== null && typeof this.onInitDtCallback !== 'undefined'){
	    	this.element.on('init.dt', function() {
	    		widgetInstance.onInitDtCallback();
	    	});
	    }
    },
    styling : function() {
	    var widgetInstance = this;

	    widgetInstance.toolbar.append(this.element.siblings(".dataTables_filter"));

	    if (widgetInstance.checkBox && widgetInstance.getRowsCount() > 0) {
		    widgetInstance.element.find("tr > td:first-child").addClass("checkbox");
	    }

	    if (widgetInstance.selectStyle === "multi" && widgetInstance.checkBox) {
		    var checkboxId = this.element.attr("id") + "-select-all-checkbox";
		    var checkbox = "<input type='checkbox' class='datatable-select-all-checkbox' + id='" + checkboxId + "'>";
		    var label = "<label for='" + checkboxId + "'></label>";
		    this.element.find("th:first").append($(checkbox)).append($(label));
	    }
    },
    addRow : function(data) {
	    this.dataTable.row.add(data).draw();
    },
    getSelectedRow : function() {
	    return this.element.find("tbody").find("tr.selected");
    },
    getSelectedRows : function() {
	    return this.element.find("tbody").find("tr.selected");
    },
    getSelectedRowsCount : function() {
	    return this.dataTable.rows('.selected').count();
    },
    getSelectedRowData : function() {
	    return this.dataTable.row(".selected").data();
    },
    getSelectedRowsData : function() {
	    return this.dataTable.rows(".selected").data();
    },
    getRows : function() {
	    return this.element.find("tbody").find("tr");
    },
    getRowsData : function() {
	    return this.dataTable.rows().data();
    },
    getRowsCount : function() {
	    return this.dataTable != null ? this.dataTable.rows().count() : 0;
    },
    removeSelectedRow : function() {
	    this.dataTable.row(".selected").remove().draw();
    },
    removeSelectedRows : function() {
	    this.dataTable.rows(".selected").remove().draw();

	    if (this.checkBox && this.selectStyle === "multi") {
		    this.element.find('.datatable-select-all-checkbox').prop('checked', false);
	    }
    },
    selectAllRows : function() {
	    this.dataTable.rows().select();
    },
    deselectAllRows : function() {
	    this.dataTable.rows().deselect();
    },
    getDataTable : function() {
	    return this.dataTable;
    },
    setSelectedRowData : function(data) {
	    this.dataTable.row(".selected").data(data);
    },
    selectRowCallBack : function(row) {
	    if (row != null) {
		    this.enableToolBarButtons();
		    this.tickCheckBox(row);
	    }
    },
    deSelectRowCallBack : function(row) {
	    if (row != null) {
		    this.disableToolBarButtons();
		    this.clearCheckBox(row);
	    }
    },
    tickCheckBox : function(row) {
	    if (this.checkBox) {
		    row.find(".checkbox").html("&#10004;");

		    if (this.selectStyle === "multi" && this.getSelectedRowsCount() === this.getRowsCount()) {
			    this.element.find('.datatable-select-all-checkbox').prop('checked', true);
		    }
	    }
    },
    clearCheckBox : function(row) {
	    if (this.checkBox) {
		    row.find(".checkbox").html("");

		    if (this.selectStyle === "multi") {
			    this.element.find('.datatable-select-all-checkbox').prop('checked', false);
		    }
	    }
    },
    refreshData : function() {
	    this.dataTable.ajax.reload();
    },
    destroyDataTable : function() {
	    this.element.DataTable().destroy();
	    this.element.empty();
	    this.element.removeData("dt-PortletDataTable");
	    this.toolbar.find(".dataTables_filter").remove();
    },
    wrapDataSrcCallBack : function(ajax) {
	    var dataSrcCallback = ajax.dataSrc;

	    var wrapperDataSrc = function(data) {
		    var results = dataSrcCallback && dataSrcCallback(data);

		    data = results || data;

		    for (var i = 0; i < data.length; i++) {
			    data[i].checkbox = null;
		    }

		    return data;
	    }

	    ajax.dataSrc = wrapperDataSrc;
    },
    wrapCompleteCallBack : function(ajax) {
	    var widgetInstance = this;
	    var completeCallBack = ajax.complete;

	    var wrapperCompleteCallBack = function() {
		    completeCallBack && completeCallBack();

		    if (widgetInstance.getSelectedRowsCount() > 0) {
			    widgetInstance.selectRowCallBack(widgetInstance.getSelectedRows());
			    widgetInstance.enableToolBarButtons();
		    } else {
			    widgetInstance.disableToolBarButtons();
		    }
	    }

	    ajax.complete = wrapperCompleteCallBack;
    },
    enableToolBarButtons : function() {
	    if (this.getSelectedRowsCount() > 0) {
		    this.toolbar.find(".toggle-on-row-selection").attr("disabled", false);
	    }
    },
    disableToolBarButtons : function() {
	    if (this.getSelectedRowsCount() < 1) {
		    this.toolbar.find(".toggle-on-row-selection").attr("disabled", true);
	    }
    }
});