/**
 * Handles the form to create domain descriptions for attributes
 */

var currentDomains ;
//this comment exists so sbt will detect this as a changed file
// f-u sbt
var undefinedSpecies;

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
		
		var speciesOpt = $("<option>").attr("value",species[i]).text(species[i].split("#")[1])
							.attr("as_index",i);
		speciesSelect.append(speciesOpt);
		undefinedSpecies++;
	}
	
	var container = $("<div>").attr("id","speciesSelect")
							.append('<h5>Select species:</h5>')
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