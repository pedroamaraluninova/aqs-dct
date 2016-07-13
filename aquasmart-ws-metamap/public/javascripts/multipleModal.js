var stack = [];
var currentModal;
var currentModalID,
	currentModalEvents;
var multiModalInited = false;
var handledHide;

function showModal(xml)
{
	showModalWithEvent(xml,undefined);
}

/**
 * 
 * @param xml
 * @param events must be a json as follows 
 * 		{
 * 			onShow: _____ ,
 *          onShown: ______ ,
 *          onHide: ________ ,
 *          onHidden : ______
 * 		}
 */
function showModalWithEvent(xml,events)
{
	
	var theXML = $(xml);
	var id= '#'+theXML.attr('id');
	//alert(id);

	if(stack.length != 0)
		switchModal(currentModalID,id,theXML,events);
	else
	{	
		initMultiModal();
		doModal(id,theXML,events);
	}
}

function initMultiModal()
{
	if( multiModalInited == true)
		return;
	
	multiModalInited = true;
	stack = [];
	handledHide = false;
}



function doModal(id,theModal,events)
{
	//alert("doModal(id="+id+")");
	
	currentModalID = id;
	currentModalEvents = events;
	
	$(document.body).append(theModal);
	
	$(id).on("shown.bs.modal", function (e){
		
		currentModal = $(id);
		stack.push( {
			id: id,
			code : theModal,
			events : events
		});
		if( ! (events === undefined) )
			if( ! ( events.onShown === undefined) )
				events.onShown(e);
	});
	
	$(id).on("hide.bs.modal", function(e){
		
		if( ! (events === undefined) )
			if( ! ( events.onHide === undefined) )
				events.onHide(e);
		
		if( handledHide )
		{	
			handledHide = false;
			return true;
		}
		
		e.preventDefault();
		backModal();
		return true;
		
	});
	
	if( ! (events === undefined) )
		if( ! ( events.onHidden === undefined) )
			$(id).on("hidden.bs.modal", function(e){
				events.onHidden(e);
			});
	if( ! (events === undefined) )
		if( ! ( events.onShow === undefined) )
			$(id).on("show.bs.modal", function(e){
				events.onShow(e);
			});
			
	
	$(id).modal('show');

}

function switchModal(id,toShowID,toShowModal,events)
{
	//alert('switch to '+toShowID+' from '+id);
	
	$(id).on('hidden.bs.modal', function (e) {
		e.preventDefault();
		if( ! (events === undefined) )
			if( ! ( events.onHidden === undefined) )	
				events.onHidden(e);
			
		$(id).remove();
		doModal(toShowID,toShowModal,events);	
	});
	handledHide = true;
	$(id).modal('hide');
}

//switches between the current modal and the parametrized one
//the backstack will not contain the replaced modal
//if there isnt a current modal, this function behaves like showModal
function switchModalWithDiscard(toShowModal, events)
{
	var toShowModalID = "#" + $(toShowModal).attr("id");
	//console.log("toShowModalID: "+toShowModalID);
	initMultiModal();
	if( stack.length == 0 )
	{
		//console.log("switchModalWithDiscard : stack.length == 0");
		doModal( toShowModalID, toShowModal, events );
		return true;
	}

	stack.pop();
	switchModal( currentModalID, toShowModalID, toShowModal, events );
	
}

function activeModal()
{
	return stack.length != 0;
}

function resetModal()
{
	var i =0;
	for(;i<stack.length; i++)
		$(stack.pop().id).remove();
}

function closeModals()
{
	handledHide = true;
	
	$(currentModalID).on('hidden.bs.modal', function (e) {

		if( ! ( currentModalEvents === undefined ) )
			if( ! ( currentModalEvents.onHidden === undefined ) )	
				currentModalEvents.onHidden(e);
		
		$(currentModalID).remove();
		stack = [];
	});
	
	$(currentModalID).modal('hide');
}

function closeModalWithoutOnHidden()
{
	handledHide = true;
	
	$(currentModalID).on('hidden.bs.modal', function (e) {

		
		
		$(currentModalID).remove();
		stack = [];
	});
	
	$(currentModalID).modal('hide');
}

function backModal()
{

	handledHide = true;
	
	if(stack.length > 1)
	{
		
		var curr = stack.pop();
		var prev = stack.pop();
	
		switchModal(curr.id,prev.id,prev.code,prev.events);
		
	}
	else
	{
		$(currentModalID).on('hidden.bs.modal', function (e) {
			
			if( ! ( currentModalEvents === undefined ) )
				if( ! ( currentModalEvents.onHidden === undefined ) )	
					currentModalEvents.onHidden(e);
			
			$(currentModalID).remove();
			stack = [];
		});
		$(currentModalID).modal('hide');
	}
		
}