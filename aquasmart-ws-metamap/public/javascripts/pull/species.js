/**
 * Handles the form to create domain descriptions for attributes
 */

var currentDomains ;
//this comment exists so sbt will detect this as a changed file
// f-u sbt!!
var undefinedSpecies;
var speciesMap;

function resetCurrentDomains()
{
	currentDomains = undefined;
	undefinedSpecies = 0;
}   

function resetDomainDescription()
{
	$("#type_select").val("None").change();
	$("#speciesSelect").remove();
	
	$("#domainDescription").empty();
	$("#selectDomainInput").append( makeMultipleSelectList() );
	
}

function makeMultipleSelectList()
{
	var speciesSelect = $("<select>").addClass("form-control")
							.attr("size","6").attr("multiple",""),
		i,
		buildStructure = ( currentDomains === undefined ) ;
	
	if( buildStructure )
		currentDomains = [];
	undefinedSpecies = 0;	
	for( i = 0 ; i < species.length; i++)
	{
		if( buildStructure )
			currentDomains[i] = {  
				index : i ,
				species : species[i],
				hasDomain : false,
				domain : undefined
			}; 
		else if( currentDomains[i].hasDomain )
			continue;
		var speciesText = species[i].split("#").length == 1 ? species[i] : species[i].split("#")[1];
		var speciesOpt = $("<option>").attr("value",species[i]).text(speciesText)
							.attr("as_index",i);
		speciesSelect.append(speciesOpt);
		undefinedSpecies++;
	}
	
	var container = $("<div>").attr("id","speciesSelect")
							.append('<h5>Select species:</h5>')
							.append(speciesSelect);
	
	return container;
}

function makeCustomMultipleSelectList(id,values)
{
	var speciesSelect = $("<select>").addClass("form-control")
							.attr("size","6").attr("multiple",""),
		i;;
	
	
	for( i = 0 ; i < values.length; i++)
	{
		
		
		var speciesOpt = $("<option>").attr("value",values[i]).text(values[i])
							.attr("as_index",i);
		speciesSelect.append(speciesOpt);

	}
	
	var container = $("<div>").attr("id",id)
							.append('<h5>Select value:</h5>')
							.append(speciesSelect);
	
	return container;
}

function categoricDefinition()
{
	var categDomains = [] , i;
	
	for( i = 0; i < currentDomains.length; i++)
	{
		categDomains[i] = {
			species : currentDomains[i].species,
			values: currentDomains[i].domain.values
		};
	}
		
	return categDomains;
}

function numericDefinition()
{
	var numDomains = [] , i;
	for( i = 0; i < currentDomains.length; i++)
	{
		numDomains[i] = {
			species : currentDomains[i].species,
			lowerBound : currentDomains[i].domain.lowerBound,
			upperBound : currentDomains[i].domain.upperBound,
			precision : currentDomains[i].domain.precision,
			unit : currentDomains[i].domain.unit
		};
	}
		
	return numDomains;
}

function temporalDefinition()
{
	var timeDomains = [] , i;
	for( i = 0; i < currentDomains.length; i++)
	{
		timeDomains[i] = {
			species : currentDomains[i].species,
			lowerBound : currentDomains[i].domain.lowerBound,
			upperBound : currentDomains[i].domain.upperBound,
			precision : currentDomains[i].domain.precision,
			format : currentDomains[i].domain.format
		};
	}
		
	return timeDomains;
}

function makeTemporalDomainForm()
{
	var markup = ('<div id="timePrecision" >'+
		'<h5>Select the value precision:</h5>'+
		'<select id="timePrec"  class="form-control">'+
		       		
			'<option id="SecondOption" value="Second" >Second</option>'+
			'<option id="MinuteOption" value="Minute" >Minute</option>'+
			'<option id="HourOption" value="Hour" >Hour</option>'+
			'<option id="DayOption" value="Day" >Day</option>'+
			'<option id="MonthOption" value="Month" >Month</option>'+
			'<option id="YearOption" value="Year" >Year</option>'+
		
		'</select>'+
		       	
		'<input type="text" class="form-control" placeholder="Date format" id="timeFormatInput">'+
		'<span id="helpBlock2" class="help-block">Example: yyyy-MM-dd HH:mm:ss for a date formatted as'+
		       		' 2015-08-29 19:00:01 [ Year(y), Month(M), Day(d), Hour(H), Minute(m), Second(s)]</span>'+
		       		
		'<h5>Select a value bounding:</h5>'+
		'<input type="text" class="form-control" placeholder="Start Value" id="timeStartInput">'+
		'<input type="text" class="form-control" placeholder="End Value" id="timeEndInput">'+
		'<span id="helpBlock3" class="help-block">Insert time values in the format yyyy-MM-dd HH:mm:ss</span>'+
	       	
	'</div>');
	
	return markup;
}

function makeTextualDomainForm()
{
	var markup = ('<div id="textPrecision" >'+
		'<h5>Select the value precision:</h5>'+
				'No options'+
	'</div>');
	
	return markup;
}

function makeNumericDomainForm()
{
	var markup = ('<div id="numPrecision" >'+
		'<h5>Select the value precision:</h5>'+
		'<select id="numPrec" class="form-control">'+ 
			'<option value="Integer" >Integer</option>'+
			'<option value="Real" >Real</option>'+
		'</select>'+
		       	
		'<h5>Select a value bounding</h5>'+
		'<input type="text" class="form-control" placeholder="Start Value" id="numStartInput">'+
		'<input type="text" class="form-control" placeholder="End Value" id="numEndInput">'+
		'<span id="helpBlock3" class="help-block">Insert numeric values in double format (X.YWZ, i.e. 3.141)</span>'+
		'<span id="helpBlock4" class="help-block">The default values presented are the minimum and maximum detected for this dataset</span>'+
		      		
		'<h5>Select a unit value:</h5>'+
		'<input type="text" class="form-control" placeholder="Unit" id="numUnitInput">'+
	'</div>');
	return markup;
	
}

function makeCategoricDomainForm()
{
	var markup = ('<div id="catPrecision" >'+
		'<h5>Select the value precision:</h5>'+
		'<textarea class="form-control" rows="3" id="cat_values"></textarea>'+
		'<span id="helpBlock" class="help-block">Enter values separated by #</span>'+
	'</div>');
	return markup;
}


function getSelectedSpecies()
{
	var brands = $('#speciesSelect option:selected');
    var selected = [];
    $(brands).each(function(index, brand){
        var opt = $(this);
        selected[selected.length] = {
        	index : opt.attr("as_index"),
        	species : opt.attr("value")
        };
    });
    return selected;
}

function getSelected(id)
{
	var brands = $(id+' option:selected');
    var selected = [];
    $(brands).each(function(index, brand){
        var opt = $(this);
        selected[selected.length] = {
        	index : opt.attr("as_index"),
        	species : opt.attr("value")
        };
    });
    return selected;
}

function handleNewSpecies(values, callback)
{
	
	var unhandled = values.filter( function(val,index,arr){
		var globalMatch = speciesMap.find( function(valS,indexS,arrS){
			var match = valS.value.find( function(v,i,a){
				return v == val;
			});
			return match !== undefined ;
		});
		return globalMatch === undefined;
	});
	
	console.log("handleNewSpecies");
	console.log(unhandled);
	
	if( unhandled.length != 0 )
		showSpeciesMapModal( unhandled, callback );
	else callback();
}

function showSpeciesMapModal( newSpeciesValues, callback )
{
	frontRoutes.controllers.FrontEndController.speciesMapModal().ajax({
		success : function(modal){
			console.log("got species modal");
			showModalWithEvent( modal, {
				onShow : undefined,
				onShown : function(){
					speciesMapModalOnShown(newSpeciesValues, callback);
				},
				onHide : undefined,
				onHidden : undefined
			})
		}
	});
}

var newValues;

function initSpeciesMapControls(newSpeciesValues)
{
	newValues = [];
	var x = 0;
	for( ; x < newSpeciesValues.length ; x++ )
	{
		newValues[x] = {
			mapped : false,
			mappedTo : undefined,
			value : newSpeciesValues[x]
		};
	}
}

function markAsMapped(value,to)
{
	var i = 0;
	for( ; i < newValues.length; i++)
		if( newValues[i].value == value.species )
		{	
			newValues[i].mapped = true;
			newValues[i].mappedTo = to ;
			break;
		}
}

function initSelectValueAndSpecies(doSelectList)
{
	$("#speciesWell1").empty();
	
	//append values to map
	var newValuesSelect = makeCustomMultipleSelectList("newValuesSelect",
			newValues.filter( function(val,ind,arr){ return ! val.mapped } )
			.map( function(val,ind,arr){ return val.value }));
	$("#speciesWell1").append( newValuesSelect );
	
	
	if( doSelectList ) {
		//append available species values
		var speciesSelect = $("#species_select");
		for( i = 0 ; i < species.length; i++)
		{
			var speciesOpt = $("<option>").attr("value",species[i]).text(species[i].split("#")[1])
								.attr("as_index",i);
			speciesSelect.append(speciesOpt);
		}
	}
}


function speciesMapModalOnShown(newSpeciesValues, callback )
{
	console.log("species map modal on shown");
	initSpeciesMapControls(newSpeciesValues);
	
	initSelectValueAndSpecies(true);
	console.log("inited values");
	//on click events on bottom submit buttons
	//TODO: disable/remove when there are no values left to map
	$("#submitSpecies").click( function(){
		
		//TODO: add bad usage safeguards
		console.log("click on submit species");
		$("#species_select option:selected").each( function(){
			var selected = getSelected("#newValuesSelect");
			var selectedSpecies = $(this)[0].value;
			console.log("selected");
			console.log(selectedSpecies);
			console.log(selected);
			
			selected.forEach( function(val,ind,arr){
				markAsMapped( val, selectedSpecies );
			});
			
			var remaining = newValues.filter( function(val,ind,arr){ return ! val.mapped } );
			if( remaining.length > 0 )
				initSelectValueAndSpecies(false);
			else
				showFinish();
			
		});
	});
	
	$("#createSpecies").click(function(){
		
		console.log(this);
		var button = $(this);
		console.log(button);
		console.log(button.val());
		if( button.val().trim() == "Create Species" )
		{
			button.val("Cancel");
			$("#speciesNameInput").val("");
			$("#createSpeciesDiv").removeClass("hidden").addClass("shown");
		} else
		{
			button.val("Create Species");
			$("#createSpeciesDiv").removeClass("shown").addClass("hidden");
		}
		
	});
	
	$("#createSpeciesSubmit").click( function(){
		console.log("create species submit");
		var newSpeciesName = $("#speciesNameInput").val();
		
		if( newSpeciesName.trim() == "" )
		{
			alert("Please insert a species name");
		}	
		else
		{
			doBusy( $("#speciesModal") , false, "Processing..." );
			var request = { speciesName : newSpeciesName };
			controller.createSpecies().ajax({
				data : JSON.stringify( request ),
        		type : "POST",
                contentType: "application/json; charset=utf-8",
                success : function(jsonData)
                {
                	undoBusy();
                	console.log( jsonData.speciesURI ) ;
                	var speciesSelect = $("#species_select");
            		
        			var speciesOpt = $("<option>").attr("value",jsonData.speciesURI).text(jsonData.speciesURI)
        								.attr("as_index",species.length);
        			speciesSelect.append(speciesOpt);
        			species.push( jsonData.speciesURI );
            	
        			$("#createSpecies").val("Create Species");
        			$("#createSpeciesDiv").removeClass("shown").addClass("hidden");
                },
                error : function(err)
                {
                	undoBusy();
                	alert(JSON.stringify( err )) ;      	
                }
				
			});
		}
	});
	
	//TODO: add disable when there are values left to map
	$("#submitAllSpecies").click( function(){

		var request = buildUpdateSpeciesMessage();
		console.log("request: "+JSON.stringify( request ));
		controller.updateSpeciesValues().ajax({
			data : JSON.stringify( request ),
			type : "POST",
	        contentType: "application/json; charset=utf-8",
	        success : function( data )
	        {
	        	console.log("success on submission of species");
	        	console.log("reply: "+JSON.stringify( data ));
	        	backModal();
	        	callback();
	        },
	        error : function (err)
	        {
	        	console.log("error on submission of species");
	        	console.log("reply: "+JSON.stringify( err ));
	        	backModal();
	        }
		});
//		callback();
	});
}

function buildUpdateSpeciesMessage()
{
	
	
	var grouped = groupBy( newValues, function(newValue){
		return newValue.mappedTo;
	});
	
	var request = {
			species : new Array(grouped.length)
	}, i;
	
	for( i = 0; i < grouped.length; i++)
	{
		request.species[i] = { speciesURI : undefined, values : []};
		request.species[i].speciesURI = grouped[i][0].mappedTo;
		request.species[i].values =	grouped[i].map(function(val,ind,arr){
			return val.value;
		});
	}
	
	return request;
}

function showFinish()
{
	$("#speciesWell1").parent().empty()
		.append($("<h3>").text("All done! Press Finish to continue..."));
	
	$("#submitSpecies").addClass("hidden");
	$("#submitAllSpecies").removeClass("hidden");
}





