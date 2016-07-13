package controllers;



import java.io.IOException;

import aquasmart.services.metamap.utils.STATIC.Json;
import play.*;
import play.mvc.*;
import views.html.*;

public class FrontEndController extends Controller
{


	
	/**
	 * Dummy test operation
	 * @return
	 * @throws IOException 
	 */
    public Result index() throws IOException 
    {
    	return ok(fileUpload.render("These words say this,","and these don't"));
        
    }
    
    public play.mvc.Result upload() throws IOException 
    {
    
    	play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart formFile = body.getFile("dataset");
        
        
        if (formFile != null) 
        {
        	
            String fileName = formFile.getFilename();
            String contentType = formFile.getContentType();
            java.io.File file = formFile.getFile();
            System.out.println("Filename: "+fileName);
            
            return ok("File uploaded");
            
        } else {
            flash("error", "Missing file");
            return badRequest();
        }
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result conceptModal()
    {
    	String headerName = request().body().asJson()
				.get(Json.INPUT.MAP_ATTRIBUTE.HEADER).asText();
    	return
    		ok(conceptModal.render(headerName));
    }
    

    
    @BodyParser.Of(BodyParser.Json.class)
    public Result attributeModal()
    {
    	String headerName = request().body().asJson()
				.get(Json.INPUT.MAP_ATTRIBUTE.HEADER).asText();
    	return
    		ok(attributeModal.render(headerName));
    }
    

    public Result speciesMapModal()
    {

    	return
    		ok(speciesMapModal.render());
    }
   
    public Result unmappedModal() 
    {
    	return
    		ok(unmappedModal.render());
    }
    
    public Result invalidsModal() 
    {
    	return
    		ok(invalidsModal.render());
    }
    
    public Result timeFormatModal()
    {
    	return
    		ok(timeFormatModal.render());
    }
    
    public Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
            Routes.javascriptRouter("frontRoutes",
                controllers.routes.javascript.FrontEndController.conceptModal(),
                controllers.routes.javascript.FrontEndController.attributeModal(),
                controllers.routes.javascript.FrontEndController.unmappedModal(),
                controllers.routes.javascript.FrontEndController.timeFormatModal(),
                controllers.routes.javascript.FrontEndController.invalidsModal(),
                controllers.routes.javascript.FrontEndController.speciesMapModal()
            )
        );
    }

}
