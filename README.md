EmbeddedMongo-with-WebUI
========================
initial build by Michael Harris(MHarris021)
Embed Mongo and Embed Process projects by Michael Mosmann, flapdoodle-OSS(embedmongo.flapdoodle.de), et al.

This is a customization of the EmbeddedMongo project by FlapDoodle.
It has been combined with Spring to allow property configuration and a Web UI to start and stop mongodb.
Additionally, there is some support for viewing databases and collections with the ability to delete individual entities from within a collection.

This project allows for quickly starting and stopping an "embedded" mongodb instance inside its own Servlet Container.
This allows the database to exist independently of any other Servlet's being developed. 

File:
 /EmbeddedMongo/src/main/webapp/WEB-INF/properties/embedded.properties
 
 is where the properties for your individual "embedded" mongodb instance should be configured.
 
Project includes a task to clean up any files leftover from a bad shutdown.
  It uses a configured cron expression to determine when to run.
  This expression is currently set to once a minute on the 0th second.
 
Current Web MVC controller is mapped to localhost:[port]/utilities/mongo

Web UI requires Javascriptto be enabled to start and stop mongodb

Additional details on how to use the underlying EmbedMongo API can be found at:

https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de.git

Apache 2.0 License governs this body of work except where otherwise indicated.
