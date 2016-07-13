
var csvFile;
var lines ;
var columns;
var headers;

var activeTab;
var finishedStats, finishedDoms;
// [ attribute* ]
var results = [];
// attribute = { name : colName, uri : attrURI, concept: conceptURI, checked: NOT_DEF, stats : NOT_DEF}

var statsDone, validateDone, mappingDone, autoMapPressed;

var datasetID, mongoDatasetID;

var formats;
var controller;
var species;
var submitedFormat;

//image preloader
//used to load the ajax loading gif in time for use
function preload(arrayOfImages) {
  $(arrayOfImages).each(function(){
      $('<img/>')[0].src = this;
      // Alternatively you could use:
      // (new Image()).src = this;
  });
}

function initialize()
{
	console.log("dct initialized");
	doBusy( $(document.body) , false, "Gathering initialization information...");
	//init globals
	statsDone = false;
	validateDone = false;
	mappingDone = false;
	autoMapPressed = false;
	results = [];
	finishedStats = 0;
	finishedDoms = 0;
	submitedFormat = false;
	activeTab = "main";
	theChart = undefined;
	numMappedDone = 0;
	formats = [];
	species = [];
	headers = [];
	controller = routes.controllers.DataPullMetaMapJsonController;

	controller.getSpecies().ajax({
		success : function(data)
		{
			species = data.results;
			speciesMap = data.map;
			controller.getDistincts( mongoDatasetID, "species", 4 ).ajax({
				success : function(distincts){
					undoBusy();
//					handleNewSpecies( distincts.values, function(){
						//launch time format modal
						showTimeFormatModal();
//					} ) ;
				}
			});
		}
	});
	preload(['assets/images/loading.gif']);
	
	
	//console.log("javascript is on!");
}

function showTimeFormatModal()
{
	var handleFormatSubmit = function()
	{
		if( submitedFormat == true )
			return true;
		
		var theFormat = $("#timePrec option:selected")[0].value;
		
		if( theFormat == "None" )
		{
			alert( "Please select a format for the day date values" );
			return false;
		
		}
		else
		{
			submitFormats( { precision : "Day" , format : theFormat } );
			return true;
		}
	};
	
	frontRoutes.controllers.FrontEndController.timeFormatModal().ajax({
		success: function(data)
		{
			showModalWithEvent(data,{
				onShow: undefined,
				onShown: function()
				{
					$("#submitTimeFormats").click(function(e){
						if(handleFormatSubmit())
						{
							submitedFormat = true;
							handledHide = true;
							backModal();
						}
					});
					
				},
				onHide: function (e)
				{
					handledHide = true;
					var theFormat = $("#timePrec option:selected")[0].value;
					
					if( theFormat == "None" )
					{
						alert( "Please select a format for the day date values" );
						e.preventDefault();
					
					}else 
					{
						handleFormatSubmit();
						submitedFormat = true;
						handledHide = true;
					}
					
					
					
				},
				onHidden: undefined
			});
		}
	});
	
}

function genFormatList( containerID, theFormats)
{
	
	var dl = $("<dl>").addClass("dl-horizontal")
					.attr("id","formatList"),
		i;
	for( i = 0; i < theFormats.length ; i++)
		genListValue(dl, theFormats[i].precision, theFormats[i].format);
	
	safeRemove("#formatList");
	safeRemove("#clear_formats_but");
	
	var formatsDiv = $("#storedFormats");
	formatsDiv.append(dl);
	
	var clearButton = $( '<button id="clear_formats_but" type="button" ' +
			'class="btn btn-primary btn_map right">' +
		  	"Clear formats" + '</button>');
	
	formatsDiv.append(clearButton);
	clearButton.click(function(){
		formats = [];
		genFormatList( containerID, formats );
	});
}


function submitFormats(dayFormat)
{	
	var request = {
			name : "dataset"+mongoDatasetID ,
			formats : [ { precision : "Year", format : "yyyy" }, dayFormat ] ,
			id : mongoDatasetID 
	};
	//console.log(request);
	
	controller.createDataset().ajax({
		data : JSON.stringify(request),
		type : "POST",
        contentType: "application/json; charset=utf-8",
		success : function(data)
		{
			//console.log(data);
			datasetID = data.datasetURI;

			
			activeTab = "main";
			$("#main_nav").removeClass("disabled");
			
			initializeResults();
			
		}
	});
	
}

function resetContents(elem)
{
	$( elem ).empty();
}

function initializeResults()
{

	//TODO: get meta from server
	controller.getMetadata(mongoDatasetID).ajax({
		
		success: function(data){
			headers = data.metadata;
			var	i = 0;
			lines = [];
			columns = [];
			
			for( i = 0; i < headers.length ; i++ )
			{
				columns[ i ] = [];
				headers[ i ].name = headers[ i ].name.replace(/(\r\n|\n|\r)/gm,"");
			}
			
			getMetadataForColumn(0);
		}
	});
	
//	var currLines = csvFile.split( "\n" ),
	
//	headers = currLines[0].split( ";" );

	
	
//	currLines = currLines.map(function(a,b,c){
//			return a.split(";");
//		}).filter(function(a,b,c){
//			return a.length == headers.length;
//		});
//	
//	//console.log(currLines);
//	
//	for( i = 1; i < currLines.length ; i++ )
//	{		
//		var line = currLines[ i ];
//		if( line.length != headers.length )
//		
//		lines[ lines.length ] = line;
//		
//		var j;
//		for(j = 0; j < headers.length ; j++ )
//			columns[ j ][ i-1 ] = line[ j ].trim();
//	}
	
	
}

function getMetadataForColumn(ind)
{
	if( ind == 0 )
	{
		doBusy( $( document.body ), false, "Initializing... " ) ;
	}

	
	//console.log("get metadata for "+ind);
	results[ind] = {
			name : headers[ind].name ,
			label : headers[ind].label ,
			isMapped : false,
			attrURI : undefined,
			dsAttrURI : undefined,
			conceptURI : undefined,
			concept : undefined,
			checked : false,
			outliers : [],
			stats : undefined,
			triedMap : false,
			distincts : []
		};
		//console.log("did results");
		controller.getDistincts(mongoDatasetID, headers[ind].label, ind).ajax({
			success : function(distincts)
			{
				//console.log(distincts);
				var index = distincts.index;
				results[index].distincts = distincts.values;
				var i = index + 1;
				//console.log("is "+i +" less than " + headers.length);
				if( i < headers.length )
				{
					getMetadataForColumn(i);
				}
				else
				{
					resetContents("#main");
					
					//console.log(results);
					showResultsPanel();
					showResultsTable();
					undoBusy();
				}
			}
		});
}

function signalMainUI(success)
{
	console.log( "posting end of cleaning to parent" );
	var mess = { status : success ? "DONE" : "FAILED" } ;
	parent.postMessage( mess, "*" );
	console.log( "post done to parent with value: " + JSON.stringify(mess) ) ;
}

function requestOutlierSubmission()
{
	controller.executeDelivery( mongoDatasetID ).ajax({
		success :  function()
		{
			console.log("SUCCESS: submit to data-staging-service success");

			signalMainUI(true);
			
			
		},
		error : function(err)
		{
			alert("ERROR: submit to data-staging-service");
			signalMainUI(false);
			console.log(err);
		}
	});
}

function showResultsPanel()
{
	//console.log(numMappedDone);
	var content = $("#main"),
		well = $("<div>").addClass("top_controls"),
		text = $("<span>").css("font-size","large"),
		autoMapText = !autoMapPressed  ? "Auto-Map All" : "Unmapped Summary",
		validateText = !validateDone ? "Validate All" : "Invalid Summary",
		button = 
			$( '<button id="map_but" type="button" ' +
				'class="btn btn-primary btn_map">' +
			  	autoMapText + '</button>'),
	  	buttonValidate = 
			$( '<button id="validate_but" type="button" ' +
				'class="btn btn-primary right">'+
			  	validateText + '</button>');
		
	text.text(" or select a line");
	
	well.append(button);
	if( ! autoMapPressed )
		well.append(text);
	
	well.append(buttonValidate);

	content.append(well);
	
	var disableMap = autoMapPressed || mappingDone,
		disableValidate = validateDone || ! mappingDone ;
	
	if(!disableMap)
		button.click( function(){
			//if button should be disabled but the event was added before this
			if(mappingDone)
				return false;
			
			autoMapPressed = true;
			
			mapColumn(0,true);
			disableButton(button);	
		});
	else if( ! mappingDone )
		button.click( function(){
			showUnmapped();
		});
	
	if( mappingDone )
		disableButton(button);

	if(!validateDone)
		buttonValidate.click( function(){
			if(validateDone || ! mappingDone)
				return false;
		
			doColumnValues();
			validateDone = true;
			disableButton(buttonValidate);
		});
	else 
	{
		if( gotInvalids() ) {
			buttonValidate.click( function(){
				showInvalids();
			});
		}
		
		
		
	}
	
	if( disableValidate && ! gotInvalids() )
		disableButton(buttonValidate);
	
	
}

function gotInvalids()
{
	return results.filter( function(val,index,arr){
		return val.outliers.length != 0;
	}).length != 0;
}

function disableButton(button)
{
	button.attr("disabled","disabled");
}

function enableButton(button)
{
	button.removeAttr("disabled");
}

var currentTable;

function showResultsTable()
{
	var heads = headers;
	var i = 0;
	var theTable = 
		$( '<table cellpadding="0" cellspacing="0" border="0"' 
				+ ' class="display" id="dataTable"></table>' );
	
	var tableHeads = [
         { title : "Index" },
	     { title : "Column Name" },
	     { title : "Attribute" },
	     { title : "Concept" },
	     { title : "Validated" }
	] ,
		tableRows = [];
	
	for( i = 0; i < headers.length ; i++)
	{
		var at = results[i];
		tableRows[i] = [ 
		   i, at.name , 
		   attributeLink(at.dsAttrURI,i) , 
		   conceptLink(at.conceptURI,i) , 
		   showBoolean(at.checked,at.outliers.length == 0) ];
		//console.log(tableRows[i]);
	}
	
	
	var tableView = $('<div id="tableView"></div>');
	tableView.css("padding-bottom","10px");
	$("#main").append(tableView);
	tableView.html( theTable );

    currentTable = $('#dataTable').dataTable( {
    	info : false,
		data : tableRows,
		columns : tableHeads,
		searching : true
	});
    
    handleTableContents();
    $("#dataTable").on('draw.dt', handleTableContents);
    
}

function conceptLink(conceptURI, index)
{
	
	if( conceptURI == undefined )
		return '<span class="glyphicon glyphicon-ban-circle gray" ></span>';
		
	return $("<div>").append(
				$("<button>").attr("type","button")
						.addClass("btn")
						.addClass("btn-default")
						.addClass("btn-pop")
						.attr("data-container", "body")
						.attr("data-toggle", "popover")
						.attr("data-placement", "bottom")
						.attr("data-content", results[ index ].concept.description )
						.attr("title", results[ index ].concept.name )
						.text( results[ index ].concept.name )
			).html();

}

function attributeLink(attrURI, index)
{
	
	if( attrURI == undefined )
	{
		if(results[ index ].triedMap)
		{
			return 	'<button id="map_but_'+index+'" type="button" ' +
					'class="btn btn-success btn_map"'+
					' onclick=\'createConceptModal(' + index + ')\' '+
					'>Manual Map</button>';
		}
		else
		{
			return 	'<button id="map_but_'+index+'" type="button" ' +
					'class="btn btn-primary btn_map"'+
					' onclick=\'doMapOrCreate(' + index + ')\' '+
					'>Auto-Map</button>' ;
		}
	}
		
	var infoBtn = $("<button>")
						.attr("type","button")
						.addClass("btn")
						.addClass("btn-info")
						.text( "Info" );
	
	if( validateDone )
		infoBtn.attr( "onclick", 'statsModal(' + index + ')');
	else
		infoBtn.attr( "disabled", "disabled" );
	
	return $("<div>").append( infoBtn ).html();

}

function handleTableContents()
{
	//center
	$("#dataTable thead tr th").addClass("center");
    $("#dataTable tbody tr td").addClass("center");
    
    $(".btn-pop").popover();
}

function makeDataTable(tableID, parentID, tableHeads, tableData, onClickEvent)
{
	var theTableID = '#'+tableID+"_table" ;
	var theTable = 
		$( '<table cellpadding="0" cellspacing="0" border="0"' 
				+ ' class="display" id="' + theTableID + '"></table>' );
	var tableView = $('<div id="' + tableID + '"></div>');
	tableView.css("padding-bottom","10px");
	$("#"+ parentID ).append(tableView);
	tableView.html( theTable );

    $(theTableID).dataTable( {
    	info : false,
		data : tableData,
		columns : tableHeads,
		searching : true
	});
    
    $('#'+tableID+"_table"+' tbody').on('click', 'tr', onClickEvent);
}

function showURI(uri, index, doMapButton)
{
	
	if(uri != undefined)  
		return uri.split("#")[1] ;
	
	if(results[ index ].triedMap)
	{
		return ( doMapButton ? 
				'<button id="map_but_'+index+'" type="button" ' +
				'class="btn btn-success btn_map"'+
				' onclick=\'createConceptModal(' + index + ')\' '+
				'>Manual</button>' :
				'Not mapped' );
	}
	else
	{
		return ( doMapButton ? 
				'<button id="map_but_'+index+'" type="button" ' +
				'class="btn btn-primary btn_map"'+
				' onclick=\'doMapOrCreate(' + index + ')\' '+
				'>Auto-Map</button>' :
				'Not mapped' ) ;
	}
}

function showBoolean(value,success)
{
	var icon = value ?  
			( success ? 'ok-circle green' : 'remove-circle red') : 
			'ban-circle gray';
	//console.log(icon);
	return '<span class="glyphicon glyphicon-'+icon+'" ></span>';
}

function doMapOrCreate(index)
{
    if( results[ index ].triedMap )
    	createConceptModal( index );
    else 
    {
    	
    	mapColumn( index , false );
    }
}

function isMapped(id)
{
	return results[ id ].isMapped ;
}

var numMappedDone = 0;

/**
 * 
 * @param id the identifier of the column to be mapped
 * @param applySeq "true" if this function should be applied
 * sequentially to all subsequent columns
 */
function mapColumn(id,applySeq)
{
	if(applySeq)
		doBusy( $("#main"), false , "Processing... ");
	

	//check if already mapped
	if(results[id].isMapped)
	{
		var button_parent = 
			$("#map_but_"+id).parent()
						.empty().html("Mapped");
			
		if( applySeq)
		{
			if( id+1 < results.length )
				mapColumn(id+1,applySeq);
			else
				resetTable();
		}
		return;
	}
	else
		$("#map_but_"+id).removeClass("btn-primary")
		.addClass("btn-success").html("Manual Map");
	
	var query = { header : results[ id ].name } ;
	results[ id ].triedMap = true;
	
	controller.mapToAttribute().ajax({
		data : JSON.stringify(query),
		type : "POST",
        contentType: "application/json; charset=utf-8",
		success : function(rawData)
		{
			var data = rawData;
			//console.log(data);
			if( (data.none === undefined) )
				useAttributeMapping(data, id, applySeq);
			else
			{
				//alert("Unable to auto-map attribute \""+ results[id].name+"\"");
				
				
				$("#map_but_"+id).removeClass("btn-primary")
					.addClass("btn-success").html("Manual Map");
				
				//if applying auto-map sequentially and have not reached the last one
				if( applySeq && ( id+1 < results.length ) )
				{
					
					//changeProgress( (numMappedDone/results.length)*100 );
	    			mapColumn(id+1,applySeq);
				}
	        	else if( applySeq ) //applying sequentially and reached the last one
	        	{
	        		resetTable();
	        		undoBusy();
	        	}
				
				if( finishedMapping() )
	        	{
	        		mappingDone = true;
	        		disableButton($("#map_but"));
	        		enableButton($("#validate_but"));
	        	}
			}
				
		}
	});
}

function resetTable()
{
	resetContents("#main");
	showResultsPanel();
	showResultsTable();
}

function useAttributeMapping(data, id, applySeq)
{
	results[ id ].isMapped = true;
	results[ id ].attrURI = data.attributeURI;
	results[ id ].conceptURI = data.conceptURI;
	numMappedDone++;
	//console.log(results);
	var request = { 
		datasetID : datasetID,
		columnName : results[ id ].name,
		columnLabel : results[ id ].label,
		conceptURI : data.attributeURI,
		concept : results[ id ].conceptURI,
		columnIndex : id,
		mongo : mongoDatasetID
	};

	controller.associateAttributeToDataset().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(jsonData)
        {
        	
        	results[ id ].dsAttrURI = jsonData.datasetAttributeURI;
			//console.log(request);
			
        	//console.log(json);
        	
        	results[ id ].isMapped = true;
        	
        	request = { conceptURI : results[ id ].conceptURI };
        	controller.getConceptInfo().ajax({
        		data : JSON.stringify( request ),
        		type : "POST",
                contentType: "application/json; charset=utf-8",
                success : function(jsonData)
                {
                	results[ id ].concept = jsonData;
                	$("#map_but_"+id).parent()
						.empty().html(attributeLink( results[ id ].dsAttrURI, id));
	        	
		        	if( applySeq && ( id+1 < results.length ) )
		    			mapColumn(id+1,applySeq);
		        	else
		        		resetTable();
		        	
		        	if(finishedMapping())
		        	{
		        		mappingDone = true;
		        		disableButton($("#map_but"));
		        		enableButton($("#validate_but"));
				        	}
		                }
		        	});
		        
		        	
		        	
		        }
			});
   
	
	
}

function getConceptInfo(conceptURI,id)
{
	
}
	
var currentAttribute;
function createConceptModal(id)
{
	var request = { header : results[ id ].name };
	safeRemove("#conceptModal");
		
	currentAttribute = id;
	frontRoutes.controllers.FrontEndController.conceptModal().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(htmlData)
        {
//        	if( activeModal() )
//        		switchModalWithDiscard( htmlData, function(e){} );
//        	else
    		showModal(htmlData);
    		
    		
        }
	});
}

function safeRemove(id)
{
	var elem = $(id);
	if( ! (elem === undefined) )
		elem.remove();
}

function searchConcept()
{
	var input = $( "#searchConceptInput" ).val();
	
	if( input.trim().length < 3 )
	{
		alert("Insert at least 3 letters");
		return ;
	}
	
	doBusy($("#conceptModal"),false,"Searching. ");
	//hide new concept creation form
	$("#createConcept").removeClass("shown").addClass("hidden");
	$("#newConceptButton").html("New Concept");
	
	var input = $( "#searchConceptInput" ).val();
	var request = { keyword : input }; 
		
	controller.searchConcept().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(jsonData)
        {
        	
        	var i, tableDatas = [],
        		tableHeaders = [
        		   { title : "order" },
        		   { title : "URI" },
        		   { title : "Name" }
            ];
        	
        	for( i = 0; i < jsonData.results.length ; i++ )
        	{
        		tableDatas[i] = new Array(3);
        		tableDatas[i] = [ i, 
	        		jsonData.results[i].conceptURI.split("#")[1],
	        		jsonData.results[i].name ];
        	}
        	
        	
        	makeHTMLTable("conceptTable", "modal_body", tableHeaders, tableDatas, function(e){
        		var children = $( this ).children(),
        			index = parseInt( $( children[0] ).text() ),
        			uri = "http://www.owl-ontologies.com/OntoGlossary.owl#" + $( children[1] ).text();
        		
        		makeAttribute(uri);
        	});
        	
        	undoBusy();
        }
	});
}

function toggleConceptFields()
{
	//hide concept search results
	var table = $("#conceptTable");
	if( ! ( table === undefined ) )
		table.remove();
	
	var buttonText = $("#newConceptButton").text();
	//console.log(buttonText);
	$("#conceptDescInput").empty();
	$("#conceptNameInput").empty();
	$("#anonCheckbox").removeAttr("checked");
	
	if( buttonText.trim() == "New Concept" )
	{
		$("#createConcept").removeClass("hidden").addClass("shown");
		$("#newConceptButton").html("Cancel");
	
	}
	else 
	{
		$("#createConcept").removeClass("shown").addClass("hidden");
		$("#newConceptButton").html("New Concept");s
	}
}

function handleSecretConcept()
{
	toggleConceptFields();
	
	$("#conceptNameInput").val("Anonymous Concept");
	$("#conceptDescInput").val("This is an anonymous concept. The company that owns this "
				+ "dataset does not wish to share the meaning of this column.");
	$("#anonCheckbox").attr("checked","");
}

function createConcept()
{
	var conceptName = $("#conceptNameInput").val(),
		conceptDesc = $("#conceptDescInput").val(),
		conceptLang = $("#conceptLanguageSelect option:selected")[0].value,
		secret = $("#conceptAnon").is(":checked");
	
	var request = {
			name : conceptName ,
			description : conceptDesc ,
			lang : conceptLang,
			anonymous : secret
	}
	
	controller.createConcept().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function( jsonData )
        {
        	var uri = jsonData.conceptURI;
        	//console.log(jsonData);
        	makeAttribute(uri);
        }
	});
}

function makeHTMLTable(tableID, parentID,tableHeaders,tableDatas, eventFun)
{
	
	var theTableID = tableID+"_table" ;
	var theTable2 = 
		$( '<table cellpadding="0" cellspacing="0" border="0"' 
				+ ' class="table table-striped table-hover" id="' + theTableID + '"></table>' );
	
	var thead = $("<thead>"),
		tbody = $("<tbody>"),
		tr = $("<tr>");
	
	var i, j;
	for( i = 0; i < tableHeaders.length ; i++)
		tr.append($("<th>").html(tableHeaders[i].title));
	thead.append(tr);
	
	for( i = 0; i < tableDatas.length; i++)
	{
		var row = $("<tr>");
		for( j = 0; j < tableDatas[i].length; j++)
			row.append( $("<td>").html(tableDatas[i][j]) );
		tbody.append(row);
	}
	
	theTable2.append(thead);
	theTable2.append(tbody);
	$("#"+tableID).remove();
	var tableView2 = $('<div id="' + tableID + '"></div>');
	tableView2.css("padding-bottom","10px");

	
	tableView2.append( theTable2 );
	
	$("#"+ parentID ).append(tableView2);
    $("#"+theTableID+' tbody').on('click', 'tr', eventFun);
}

//results[i] = {
//name : headers[i],
//isMapped : false,
//attrURI : undefined,
//dsAttrURI : undefined,
//conceptURI : undefined,
//checked : false,
//outliers : [],
//stats : undefined,
//triedMap : false
//}

function showUnmapped()
{
	frontRoutes.controllers.FrontEndController.unmappedModal().ajax({
        success : function( htmlData )
        {
        	var unmappedModal = $(htmlData);
        	unmappedModal.on("shown.bs.modal", function(){
        		var i, tableDatas = [], j,
    			tableHeaders = [
    			   { title : "Index" },
    			   { title : "Name" },
    			   { title : "Mapping" }
    			];
    			
    			j = 0 ;
    			for( i = 0; i < headers.length ; i++)
    			{
    				var at = results[i];
    				
    				if( ! (at.triedMap == true && at.isMapped == false) )
    					continue;
    				
    				tableDatas[j++] = [ 
    				   i, at.name , 
    				   attributeLink(at.dsAttrURI,i)  ];
    				//console.log(tableDatas[j-1]);
    			}
    		
    		
    			makeHTMLTable("unmappedTable", "modal_body", tableHeaders, tableDatas, function(e){});
        	});
        	
        	showModal(unmappedModal);
        }
	});
}

function showInvalids()
{
	frontRoutes.controllers.FrontEndController.invalidsModal().ajax({
        success : function( htmlData )
        {
        	var invalidsModal = $(htmlData);
        	invalidsModal.on("shown.bs.modal", function(){
        		var i, tableDatas = [], j,
    			tableHeaders = [
    			   { title : "Index" },
    			   { title : "Name" },
    			   { title : "Details" }
    			];
    			
    			j = 0 ;
    			for( i = 0; i < headers.length ; i++)
    			{
    				var at = results[i];
    				
    				if( at.outliers.length == 0 )
    					continue;
    				
    				tableDatas[j++] = [ 
    				   i, at.name , 
    				   attributeLink(at.dsAttrURI,i)  ];
    				//console.log(tableDatas[j-1]);
    			}
    		
    		
    			makeHTMLTable("invalidsTable", "modal_body", tableHeaders, tableDatas, function(e){});
        	});
        	
        	showModal(invalidsModal);
        }
	});
}

function makeAttribute(uri)
{
	safeRemove("#attributeModal");
	//console.log("make attr");
	var request = { header : results[ currentAttribute ].name };
	frontRoutes.controllers.FrontEndController.attributeModal().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function( htmlData )
        {
        	//console.log("got modal");
        	request = { conceptURI : uri };
        	controller.getConceptInfo().ajax({
        		data : JSON.stringify( request ),
        		type : "POST",
                contentType: "application/json; charset=utf-8",
                success : function(jsonData)
                {
                	//console.log("got info");
                	results[ currentAttribute ].concept = jsonData;
                }
        	});

          	showModalWithEvent( htmlData , {
        		onShow : undefined,
        		onShown : function(){
	        		//console.log("shown number 2");
	        		doBusy($("#attributeModal"),false,"Loading. ");
	        		fillDistinctValues( currentAttribute );
	            	activateSelectSwitcher();
	            	resetCurrentDomains();
	            	resetDomainDescription();
	            	
	            	initializeAttribute( uri );
	            	
	        		$("#domainDescription").parent().prepend( 
	        				$("<input>").addClass("btn btn-success pull-right")
	    					.attr("type","submit").attr("id","submitDomain")
	    					.attr("value","Create Domain")
	    					.css("margin-bottom","5px") );
	        		
	            	$( "#submitAttr" ).click( submitAttribute );
	            	$( "#submitDomain" ).click( submitDomain ) ;
	            	
	            	undoBusy();
        		},
        		onHide : undefined,
        		onHidden : undefined
        	});
        	
        }
	});
}

var newAttribute;

function activateSelectSwitcher()
{
	var type = $("#type_select");
	
	type.change(function(){
		$("#type_select option:selected").each(function(){
			var selected = $(this)[0].value;
			var domainContainer = $("#domainDescription");
			domainContainer.empty();
			
			if(selected == "Categorical" )
			{
				domainContainer
					.append( makeCategoricDomainForm() );
					
			}				
			else if(selected == "Textual")
			{
				domainContainer
					.append( makeTextualDomainForm() );
			}
			else if(selected == "Numeric")
			{
				domainContainer
					.append( makeNumericDomainForm() );
				
				var minMax = getMinMax(currentAttribute);
				if( minMax == undefined )
					alert("According to its values, this does not seem to be a numeric attribute");
				else
				{
					$("#numStartInput").val( minMax.min );
					$("#numEndInput").val( minMax.max );
					
					replaceDistinctsWithChart(currentAttribute);
				}
			}
			else if(selected == "Temporal")
			{
				domainContainer
					.append( makeTemporalDomainForm() );
			}
			else
			{
				domainContainer.empty();
			}
	
		});
	});
}


function replaceDistinctsWithChart( attrID )
{

	controller.getDistribution( mongoDatasetID, "numeric", results[attrID].label ).ajax({
		success : function(dist)
		{
			var freqDistribution = dist.values;
			results[ attrID ].distincts = freqDistribution;
			
			//split by axis
			var xVals = 
				freqDistribution.map( function(val,index,arr){
					return val.value == undefined ? 0 : val.value ;
				}),
			yVals = 
				freqDistribution.map( function(val,index,arr){
					return val.freq ;
		    });
			
			$("#distinctValues").empty();
			
			var chart = c3.generate({
				bindto : "#distinctValues",
			    data: {
			        x: results[ attrID ].name,
			        columns: [
			            [ results[ attrID ].name].concat(xVals),
			            [ "Value Distribution"].concat(yVals)
			        ]
			    },
			    zoom : {
			    	enabled : true
			    }
			});
		}
	});
	
}


function getMinMax( id )
{
	if( isNaN( results[ id ].distincts[0] ) )
		return undefined;
	
	var sorted = results[ id ].distincts.filter(function(val,index,arr){
		return val.trim() != "";
	}).map( function (val,index,arr){
		return Number(val.replace(",","."));
	}).sort(function(a,b){
		return a-b;
	});
	
	return {
		min : sorted[0],
		max : sorted[sorted.length -1]
	};
}

function groupBy( array , f )
{
	var groups = {};
	
	array.forEach( function( o ) 
	{
		var group = JSON.stringify( f(o) );
		groups[group] = groups[group] || [];
		groups[group].push( o );  
	});
	
	return Object.keys(groups).map( function( group )
			{
				return groups[group]; 
			});
}


function fillDistinctValues(id)
{
	var dumpDiv = $("#distinctValues"),
		column = columns[id];
	//console.log(column);
	var uniques = results[id].distincts;
	

//	console.log(uniques);
//	var sorter = function (a,b) {
//	    return a - b;
//	};
//	uniques.sort(sorter);
	var i;
	var ul = $("<ul>").addClass("list-group");
	for( i = 0 ; i < uniques.length && i < 10 ; i++ )
		ul.append( $("<li>").addClass("list-group-item").html( uniques[i] ) );
	
	dumpDiv.empty();
	dumpDiv.append(ul);	
}

function initializeAttribute(concept)
{
	newAttribute = {
			name : results[ currentAttribute ].name,
			conceptURI : concept,
			domain : undefined,
			domainType : undefined
			
	}
}

function submitDomain(event)
{
	//alert("submitDomain");
	$("#type_select option:selected").each( function(){
		var selectedSpecies = getSelectedSpecies();
		if( selectedSpecies.length == 0 )
		{
			alert("You must select at least one species for the domain definition");
			return;
		}
		
		var selected = $(this)[0].value;
		//console.log("selected="+selected);
		if(selected == "Categorical" )
		{
			var catPre = $("#catPrecision"),
				catValuesText = $("#cat_values").val();
			
			var catValues = catValuesText.split("#");
			//console.log(catValues);
			catValues = catValues.map( function(val,index,arr){ return val.trim() });
			
			newAttribute.domainType = "categoric";
			
			selectedSpecies.forEach(function(val,index,arr){
				currentDomains[val.index].hasDomain = true;
				currentDomains[val.index].domain = {
					values : catValues
				};
			});
		
		}
		else if(selected == "Textual")
		{
			var textPre = $("#textPrecision");
			
			newAttribute.domainType = "textual";
			
			selectedSpecies.forEach(function(val,index,arr){
				currentDomains[val.index].hasDomain = true;
			});
		}
		else if(selected == "Numeric")
		{
			var numPre = $("#numPrecision"),
			 	numPrecision = $("#numPrec option:selected")[0].value,
			 	low = $("#numStartInput").val().trim(),
			 	high = $("#numEndInput").val().trim(),
			 	units = $("#numUnitInput").val().trim();
			
			if( isNaN( low ) || isNaN( high ) )
			{
				alert("The bounding values must be numeric values");
				return;
			}
			
			newAttribute.domainType = "numeric";
			selectedSpecies.forEach(function(val,index,arr){
				currentDomains[val.index].hasDomain = true;
				currentDomains[val.index].domain = {
					precision : numPrecision,
					lowerBound : low,
					upperBound : high,
					unit : units
				};
			});
		
		}
		else if(selected == "Temporal")
		{
			var timePre = $("#timePrecision"),
				timePrecision = $("#timePrec option:selected")[0].value,
			 	low = $("#timeStartInput").val().trim(),
			 	high = $("#timeEndInput").val().trim(),
			 	format = $("#timeFormatInput").val().trim();
			
			newAttribute.domainType = "temporal";

			selectedSpecies.forEach(function(val,index,arr){
				currentDomains[val.index].hasDomain = true;
				currentDomains[val.index].domain = {
					precision : timePrecision,
					lowerBound : low,
					upperBound : high,
					format : format
				};
			});
			
		}else // selected == "None"
		{
			newAttribute.domainType = "textual";
			var selectedSpecies = getSelectedSpecies();
			selectedSpecies.forEach(function(val,index,arr){
				currentDomains[val.index].hasDomain = true;
			});
		}
		//console.log(newAttribute);
		resetDomainDescription();
		if( undefinedSpecies == 0 )
		{
			$( "#submitDomain" ).remove();
			$( "#mainSelectDomain").empty()
				.append($("<h3>").text("All done! Press \"Submit Attribute\" to continue..."));
    		$( "#submitAttr" ).removeClass( "hidden" );
		}
	});
}

function fillNewAttribute()
{
	if( newAttribute.domainType == "textual" )
		return;
	else if( newAttribute.domainType == "categoric" )
		newAttribute.domain = categoricDefinition();
	else if( newAttribute.domainType == "numeric" )
		newAttribute.domain = numericDefinition();
	else if( newAttribute.domainType == "temporal")
		newAttribute.domain = temporalDefinition();
	else alert("not implemented! - filleNewAttribute for domainType="+newAttribute.domainType);
	//console.log(newAttribute);
}


function submitAttribute(event)
{	
	if( undefinedSpecies > 0 )
	{
		alert("You must define a domain for all species before submitting the attribute");
		return;
	}
	fillNewAttribute();
	//and then
	saveCurrentAttribute();
}

function saveCurrentAttribute()
{
	//console.log("save attribute");
	controller.createAttribute().ajax({
		data : JSON.stringify( newAttribute ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function( jsonData )
        {
        	results[ currentAttribute ].isMapped = true;
        	results[ currentAttribute ].attrURI = jsonData.attributeURI;
        	//make remote call to selectConcept
        	//results[ currentAtribute ].dsAttrURI =
        	results[ currentAttribute ].conceptURI = newAttribute.conceptURI;
        	results[ currentAttribute ].triedMap = true ;
        	
        	//console.log(results);
        	//console.log("assign attribute");
        	var request = { 
        			datasetID : datasetID,
        			columnName : results[ currentAttribute ].name,
        			columnLabel : results[ currentAttribute ].label,
        			conceptURI : jsonData.attributeURI,
        			concept : results[ currentAttribute ].conceptURI,
        			columnIndex : currentAttribute,
        			mongo : mongoDatasetID
        	};

        	//console.log(request);
        	controller.associateAttributeToDataset().ajax({
        		data : JSON.stringify( request ),
        		type : "POST",
                contentType: "application/json; charset=utf-8",
                success : function(data)
                {
                	//console.log("result is");
                	//console.log(data);
                	results[ currentAttribute ].dsAttrURI = data.datasetAttributeURI;
                	
                	if(finishedMapping())
                		mappingDone = true;
                	//console.log(data);
                	//console.log(results);
                	//console.log(stack);
                	//console.log("closing modals");
                	closeModals();
                	resetTable();

                }
        	});
        }
	});
	
}

//results[i] = {
//		name : headers[i],
//		isMapped : false,
//		attrURI : undefined,
//		dsAttrURI : undefined,
//		conceptURI : undefined,
//		checked : false,
//		outliers : [],
//		stats : undefined,
//		triedMap : false
//}

var allMapped = undefined;
function finishedMapping()
{
	var notMapped = results.filter(function(val,index,arr){ return ! val.isMapped });
	allMapped = notMapped.length == 0;
	return allMapped;
}

var numChecks;
function checkColumnValues(i)
{
	//console.log(i);
//	var columnValues = columns[ i ], 
//		x, valuesWithSpecies = [];
//	
//	for( x = 0 ; x < columns[i].length ; x++ )
//		valuesWithSpecies[x] = [ columns[4][x] , columnValues[x] ];
	//console.log(results);
	//console.log(i);
	
	var	request = { 
			attrID : results[ i ].dsAttrURI,
			datasetID : mongoDatasetID,
			currentCol : results[i].label,
			speciesCol : "species"
	};
	
	//console.log(request);
	controller.analyseData().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(data)
        {
        	results[ i ].outliers = data.outliers;
        	results[ i ].checked = true;
        	numChecks++;
        	
        	if( i+1 < results.length  )
        	{
        		checkColumnValues(i+1);
        	}else
        	{
        		resetTable();
        		
        		validateDone = true;
        		//disableButton($("#validate_but"));
        		
        		undoBusy();
        		doStats();
        	}
        },
        error : function(err)
        {
        	results[ i ].outliers = ["NOT VALID"];
        	results[ i ].checked = true;
        	numChecks++;
        	
        	if( i+1 < results.length  )
        	{
        		checkColumnValues(i+1);
        	}else
        	{
        		resetTable();
        		
        		validateDone = true;
        		//disableButton($("#validate_but"));
        		
        		undoBusy();
        		doStats();
        	}
        }
	});
}

function doStats()
{
	doBusy($("#main"), false, "Generating statistics.." );
	disableButton($("#stats_but"));
	generateDataQuality(0);
}

function doColumnValues()
{
	var i;
	if( ! finishedMapping() )
	{
		alert("Please finish mapping before trying to validate");
		return;
	}
	
	numChecks = 0;
	busy = undefined;
	doBusy( $("#main") ,false , "Validating data... ");
	alert("passed busy");
	checkColumnValues(0);
	
	
}

function tryUnblockStats()
{
	if( finishedStats == results.length 
			&& finishedDoms == results.length )
	{
		resetTable();
		var continueButton = 
			$( '<button id="map_but" type="button" ' +
				'class="btn btn-info btn_map">' +
			  	'Finish cleaning iteration' + '</button>' );
		
		continueButton.click( requestOutlierSubmission );
		
		$("#backNavBar").empty().append( continueButton );
		undoBusy();
		//console.log(results);
		
		return true;
	}
	else
		return false;
}

function generateDataQuality(resultIndex)
{
	//get data quality stats	
	var request = { 
			attrID : results[resultIndex].dsAttrURI , 	
			datasetID : mongoDatasetID ,
			currentCol : results[resultIndex].label
	};
	
	controller.produceStatistics().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(data)
        {
        	results[resultIndex].stats = data;
        	finishedStats++;
        	if( (resultIndex + 1) < results.length ) generateDataQuality(resultIndex + 1);	
        	else tryUnblockStats();  
        },
        error: function(err)
        {
   
        	results[resultIndex].stats = undefined;
        	finishedStats++;
        	if( (resultIndex + 1) < results.length ) generateDataQuality(resultIndex + 1);	
        	else tryUnblockStats(); 
        }
		
	});
	
	if( results[resultIndex].domain != undefined)
		return false;
	
	request = { datasetAttributeURI : results[resultIndex].dsAttrURI };
	controller.getAttributeInfo().ajax({
		data : JSON.stringify( request ),
		type : "POST",
        contentType: "application/json; charset=utf-8",
        success : function(data)
        {
        	//val.domain = data.domain;
        	//console.log(data);
        	finishedDoms++;
        	tryUnblockStats();
        },
        error: function(err)
        {
        	alert(JSON.stringify(err));
        }
	});
		
	
//	//make data quality tab content from results array
//	//activate "Data Quality" tab
//	$("#quality_nav").removeClass("disabled");
}

function showDataQuality()
{
	if( numChecks != results.length )
		return false;
	
	alert("Data Quality tab");
	
}

function showMain()
{
	switchTab( "main" );
}

function about()
{
	switchTab( "about" );
}

function switchTab( toShow )
{
	if(activeTab == toShow )
		return false;
		
	$("#"+activeTab)
		.removeClass("shown")
		.addClass("hidden");
	$("#"+activeTab+"_nav").removeClass("active");
	
	activeTab = toShow;
	
	$("#"+activeTab)
		.removeClass("hidden")
		.addClass("shown");
	$("#"+activeTab+"_nav").addClass("active");
	return true;
	
}

function statsModal(id)
{
	if( numChecks != results.length || finishedStats != results.length)
		return false;
	
	var list = $("<ul>"), i;
	if( results[ id ].outliers.length == 0 )
		list = $("<p>").addClass("lead").text("No outliers detected");
	else
	{
		list.addClass("list-group");
		for( i = 0 ; i < results[ id ].outliers.length ; i++)
			$("<li>")
				.addClass("list-group-item")
				.text( results[ id ].outliers[i] )
				.appendTo(list);
			
	}
	
	var theModal = (
		'<div class="modal fade" id="statsModal" >' +
    		'<div class="modal-dialog">  '  +  
    			'<div class="modal-content" style="margin-left:-125px;width:850px">' +
    				'<div class="modal-header">' +
    					'<button type="button" class="close" onclick="backModal()" aria-hidden="true">&times;</button>' +
    					'<h3 class="modal-title">' +
    					'<span id="statsModalTitle" class="bold">' +
    						( "Details for \"" + results[ id ].name + "\"" ) +
    					'</span></h3>' +
    				'</div>' +
    				'<div id="modal_body" class="modal-body">' +
    					'<div class="well" id="statsModalOutliers">' +
    						'<h5>Detected Outliers:</h5>' +
    						$("<div>").append(list).html() + 
    					'</div>'+
    					'<div class="well" id="statsModalStats">' +
    						'<h5>Value Distribution:</h5>' +
    						'<div id="chart"></div>' + 
						'</div> ' +
						'<div class="well">' +
							'<h5>Other statistics:</h5>' +
							'<div id="otherStats"></div> ' +
						'</div> ' +
    				'</div> ' + 
    				'<div class="modal-footer">' +
    					'<button type="button" class="btn btn-default" onclick="backModal()">Back</button>'  +
    				'</div>' +
    			'</div>' +
    		'</div>' +
    	'</div>');
	
	theModal = $(theModal);
	
	showModalWithEvent(theModal,{
		onShow : undefined,
		onShown : function () {
			makeStats( results[ id ].stats, id );
		},
		onHide : function () {
			if(visChart)
				;
			else
			{
				if( theChart != undefined )
					theChart.destroy();
				safeRemove($("#theChart"));
			}
		},
		onHidden : function () {
			safeRemove($("#statsModal"));
		}
	});

}

function makeStats( stats, index)
{

	if( stats.type == "numeric" )
	{
		visChart = true;
		makeVisChart(stats, index);
	}
	else
	{	
		visChart = false;
		$('<canvas id="theChart" width="600" height="300"></canvas>')
			.appendTo($("#chart"));
		initChart();
		
		var data = {
		    labels: stats.valueDistribution.map( function(val,index,arr){
		    	return val.value.trim() == "" ? "No Value" : val.value.trim() ;
		    }),
		    datasets: [
		        {
		            label: "Value Distribution",
		            fillColor: "rgba(0,0,0,1)",
		            strokeColor: "rgba(220,220,220,0.8)",
		            highlightFill: "rgba(223,12,12,0.75)",
		            highlightStroke: "rgba(220,220,220,1)",
		            data: stats.valueDistribution.map( function(val,index,arr){
		    	    	return val.frequency ;
		    	    })
		        }
		    ]
		};
		
		makeBarChart(data);
	}
	
	var list = $("<dl>").addClass("dl-horizontal");
	genListValue( list, "Modal Value" , stats.mode.value + " (x" + stats.mode.frequency + ")" );
	
	if( stats.type != "categoric" && stats.type != "textual" )
	{
		genListValue( list, "Minimum", stats.min );
		genListValue( list, "Maximum", stats.max );
	}
	if(stats.type == "numeric")
		genListValue( list, "Average", stats.avg );
	
	$("#otherStats").append(list);	
}

function genListValue(parent, label, value)
{
	$("<dt>").text(label).appendTo(parent);
	$("<dd>").text(value).appendTo(parent);
}

function makeVisChart(stats, index)
{
	var xVals = 
			stats.valueDistribution.map( function(val,index,arr){
				return val.value == undefined ? 0 : val.value ;
			}),
		yVals = 
			stats.valueDistribution.map( function(val,index,arr){
				return val.frequency ;
	    });
	
	var chart = c3.generate({
	    data: {
	        x: results[ index ].name,
	        columns: [
	            [ results[ index ].name].concat(xVals),
	            [ "Value Distribution"].concat(yVals)
	           
	        ]
	    },
	    zoom : {
	    	enabled : true
	    }
	});
}

var ctx, theChart ;
var visChart;

function initChart()
{
	ctx = document.getElementById("theChart").getContext("2d");

	Chart.defaults.global = {
	    // Boolean - Whether to animate the chart
	    animation: true,

	    // Number - Number of animation steps
	    animationSteps: 60,

	    // String - Animation easing effect
	    // Possible effects are:
	    // [easeInOutQuart, linear, easeOutBounce, easeInBack, easeInOutQuad,
	    //  easeOutQuart, easeOutQuad, easeInOutBounce, easeOutSine, easeInOutCubic,
	    //  easeInExpo, easeInOutBack, easeInCirc, easeInOutElastic, easeOutBack,
	    //  easeInQuad, easeInOutExpo, easeInQuart, easeOutQuint, easeInOutCirc,
	    //  easeInSine, easeOutExpo, easeOutCirc, easeOutCubic, easeInQuint,
	    //  easeInElastic, easeInOutSine, easeInOutQuint, easeInBounce,
	    //  easeOutElastic, easeInCubic]
	    animationEasing: "easeOutElastic",

	    // Boolean - If we should show the scale at all
	    showScale: true,

	    // Boolean - If we want to override with a hard coded scale
	    scaleOverride: false,

	    // ** Required if scaleOverride is true **
	    // Number - The number of steps in a hard coded scale
	    scaleSteps: null,
	    // Number - The value jump in the hard coded scale
	    scaleStepWidth: null,
	    // Number - The scale starting value
	    scaleStartValue: null,

	    // String - Colour of the scale line
	    scaleLineColor: "rgba(0,0,0,.1)",

	    // Number - Pixel width of the scale line
	    scaleLineWidth: 1,

	    // Boolean - Whether to show labels on the scale
	    scaleShowLabels: true,

	    // Interpolated JS string - can access value
	    scaleLabel: "<%=value%>",

	    // Boolean - Whether the scale should stick to integers, not floats even if drawing space is there
	    scaleIntegersOnly: true,

	    // Boolean - Whether the scale should start at zero, or an order of magnitude down from the lowest value
	    scaleBeginAtZero: false,

	    // String - Scale label font declaration for the scale label
	    scaleFontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",

	    // Number - Scale label font size in pixels
	    scaleFontSize: 12,

	    // String - Scale label font weight style
	    scaleFontStyle: "normal",

	    // String - Scale label font colour
	    scaleFontColor: "#666",

	    // Boolean - whether or not the chart should be responsive and resize when the browser does.
	    responsive: true,

	    // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
	    maintainAspectRatio: true,

	    // Boolean - Determines whether to draw tooltips on the canvas or not
	    showTooltips: true,

	    // Function - Determines whether to execute the customTooltips function instead of drawing the built in tooltips (See [Advanced - External Tooltips](#advanced-usage-custom-tooltips))
	    customTooltips: false,

	    // Array - Array of string names to attach tooltip events
	    tooltipEvents: ["mousemove", "touchstart", "touchmove"],

	    // String - Tooltip background colour
	    tooltipFillColor: "rgba(0,0,0,0.8)",

	    // String - Tooltip label font declaration for the scale label
	    tooltipFontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",

	    // Number - Tooltip label font size in pixels
	    tooltipFontSize: 14,

	    // String - Tooltip font weight style
	    tooltipFontStyle: "normal",

	    // String - Tooltip label font colour
	    tooltipFontColor: "#fff",

	    // String - Tooltip title font declaration for the scale label
	    tooltipTitleFontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",

	    // Number - Tooltip title font size in pixels
	    tooltipTitleFontSize: 14,

	    // String - Tooltip title font weight style
	    tooltipTitleFontStyle: "bold",

	    // String - Tooltip title font colour
	    tooltipTitleFontColor: "#fff",

	    // Number - pixel width of padding around tooltip text
	    tooltipYPadding: 6,

	    // Number - pixel width of padding around tooltip text
	    tooltipXPadding: 6,

	    // Number - Size of the caret on the tooltip
	    tooltipCaretSize: 8,

	    // Number - Pixel radius of the tooltip border
	    tooltipCornerRadius: 6,

	    // Number - Pixel offset from point x to tooltip edge
	    tooltipXOffset: 10,

	    // String - Template string for single tooltips
	    tooltipTemplate: "<%if (label){%><%=label%>: <%}%><%= value %>",

	    // String - Template string for multiple tooltips
	    multiTooltipTemplate: "<%= value %>",

	    // Function - Will fire on animation progression.
	    onAnimationProgress: function(){},

	    // Function - Will fire on animation completion.
	    onAnimationComplete: function(){}
	};
}

function makeBarChart(data)
{
	var barOptions = 
	{
	    //Boolean - Whether the scale should start at zero, or an order of magnitude down from the lowest value
	    scaleBeginAtZero : true,

	    //Boolean - Whether grid lines are shown across the chart
	    scaleShowGridLines : true,

	    //String - Colour of the grid lines
	    scaleGridLineColor : "rgba(0,0,0,.05)",

	    //Number - Width of the grid lines
	    scaleGridLineWidth : 1,

	    //Boolean - Whether to show horizontal lines (except X axis)
	    scaleShowHorizontalLines: true,

	    //Boolean - Whether to show vertical lines (except Y axis)
	    scaleShowVerticalLines: true,

	    //Boolean - If there is a stroke on each bar
	    barShowStroke : true,

	    //Number - Pixel width of the bar stroke
	    barStrokeWidth : 2,

	    //Number - Spacing between each of the X value sets
	    barValueSpacing : 5,

	    //Number - Spacing between data sets within X values
	    barDatasetSpacing : 1,

	    //String - A legend template
	    legendTemplate : "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].fillColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"

	};

	
	theChart = new Chart(ctx).Bar(data, barOptions);
}



