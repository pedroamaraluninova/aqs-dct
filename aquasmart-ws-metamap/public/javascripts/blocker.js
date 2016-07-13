var busy = undefined;
//blocks the jQuery element given in the first parameter "element"
//MUST NOT BE USED TO CREATE AN OVERLAY OVER ANOTHER OVERLAY (have to adapt busy to be a stack)
function doBusy(element,rounded, text)
{
	console.log("called doBusy with "+text);
	if(busy != undefined)
		return;
	console.log("executed doBusy with "+text);
	busy = element;
	$.blockUI.defaults.css = { 
		backgroundColor : "rgb(245, 245, 245)",
		cursor : "wait",
	    "-moz-border-radius": "10px",
	    "-webkit-border-radius": "10px",
	    "-khtml-border-radius": "10px",
	    "border-radius": "10px"
			
	};
	
	var overlayCSS;
	
	if( rounded)
		overlayCSS = { 
		    
		        opacity:         0.6, 
		        cursor:          'wait' 
		    };
	else
	{ 
		overlayCSS = { 

		        opacity:         0.6, 
		        cursor:          'wait'
		    };
    }
	
	
	var mess = $('<div id="loading-indicator" ><div class="outer" style="padding:10px"><img class="loading" src="assets/images/loading.gif"/><span class="loading_text">' 
			+ text + ' Please Wait...</span></div></div>');
	
	element.block({ 
		message : mess,
		overlayCSS: overlayCSS, 
		fadeIn : 600,
		fadeOut : 700
	});

}

function undoBusy()
{
	if(busy == undefined)
	{
		console.log("not busy, unable do undoBusy()");
		return;
	}
	busy.unblock();
	$("#loading_indicator").remove();
	busy = undefined;
}
