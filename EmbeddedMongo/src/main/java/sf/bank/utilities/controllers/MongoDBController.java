package sf.bank.utilities.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

import sf.bank.utilities.beans.DataBaseStatus;
import sf.bank.utilities.services.MongoDBService;

import com.mongodb.DBObject;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = "/mongo")
public class MongoDBController {

	private static final Logger logger = LoggerFactory
			.getLogger(MongoDBController.class);

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private MongoDBService mongoDBService;

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public DataBaseStatus getDataBaseStatus() {
		DataBaseStatus status = mongoDBService.getDatabaseStatus();
		return status;
	}

	@ExceptionHandler
	public ResponseEntity<String> handleErrors(Exception exception) {
		String body = String.format("{\"reason\" : \"%s\"}",
				exception.getMessage());
		return new ResponseEntity<String>(body, HttpStatus.I_AM_A_TEAPOT);
	}

	@RequestMapping(value = "/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> setDataBaseStatus(
			@RequestBody DataBaseStatus status) throws IOException,
			IllegalArgumentException {
		if (MongoDBService.DATABASE_STARTED
				.equalsIgnoreCase(status.getStatus())) {
			mongoDBService.start();
		} else if (MongoDBService.DATABASE_STOPPED.equalsIgnoreCase(status
				.getStatus())) {

			mongoDBService.stop();
		} else {
			throw new IllegalArgumentException(String.format(
					"Status code '%s' not recognized", status.getStatus()));
		}

		mongoDBService.setDataBaseStatus(status);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(getClass()).slash("/status").toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.ACCEPTED);
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		DataBaseStatus status = mongoDBService.getDatabaseStatus();
		model.addAttribute("status", status);

		return "home";
	}

	@RequestMapping(value = "/databases", method = RequestMethod.GET)
	public String viewDatabaseCollections(@RequestParam String databaseName,
			Model model) {
		logger.info("Viewing collections in database: {}.", databaseName);

		getDataBaseStatus();
		List<String> collectionNames = getCollections(databaseName);
		model.addAttribute("currentDatabase", databaseName);
		model.addAttribute("collectionNames", collectionNames);

		return "databases";
	}

	@RequestMapping(value = "/databases/{databaseName}/collections", method = RequestMethod.GET)
	public String viewCollectionObjects(@PathVariable String databaseName,
			@RequestParam String collectionName, Model model) {
		logger.info("Viewing objects in collection: {}.", collectionName);

		getDataBaseStatus();
		List<DBObject> dbObjects = getDBObjects(databaseName, collectionName);
		model.addAttribute("currentDatabase", databaseName);
		model.addAttribute("currentCollection", collectionName);
		model.addAttribute("dbObjects", dbObjects);

		return "collections";
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/databases/{databaseName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getCollections(@PathVariable String databaseName) {
		List<String> names = mongoDBService.getCollectionNames(databaseName);
		return names;
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/databases/{databaseName}/{collectionName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DBObject> getDBObjects(@PathVariable String databaseName,
			@PathVariable String collectionName) {
		List<DBObject> dbObjects = mongoDBService.getDBObjects(databaseName,
				collectionName);
		return dbObjects;
	}

	@RequestMapping(value = "/databases/{databaseName}/{collectionName}/{objectId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteDBObject(
			@PathVariable String databaseName,
			@PathVariable String collectionName, @PathVariable String objectId) {
		mongoDBService.deleteDBObject(databaseName, collectionName, objectId);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(getClass()).slash("databases")
				.slash(databaseName).slash(collectionName).toUri());
		return new ResponseEntity<Void>(null, headers, HttpStatus.ACCEPTED);
	}
	

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/databases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getDatabases() {
		List<String> names = mongoDBService.getDatabaseNames();
		return names;
	}

	@ModelAttribute("status")
	public DataBaseStatus status() {
		DataBaseStatus status = mongoDBService.getDatabaseStatus();
		return status;
	}

	@ModelAttribute("serverTime")
	public String serverTime() {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);

		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}

	@ModelAttribute(value = "resourcesLocation")
	public String resourcesLocation() throws URISyntaxException {
		String servletName = webApplicationContext.getServletContext()
				.getContextPath();
		String resourcesLocation = String.format("%s/resources", servletName);
		return resourcesLocation;
	}

}