package sf.bank.utilities.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.AbstractMongoConfig.Net;

import sf.bank.utilities.beans.DataBaseStatus;

@Service
public class MongoDBService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MongoDBService.class);

	public static final String DATABASE_STARTED = "started";

	public static final String DATABASE_STOPPED = "stopped";

	private MongoClient mongoClient;

	private MongodProcess mongodProcess;

	@Autowired
	private MongodExecutable mongodExecutable;

	@Autowired
	private MongodConfig mongodConfig;

	@Autowired
	private DataBaseStatus status;

	/**
	 * @param status
	 *            the status to set
	 */
	public void setDataBaseStatus(DataBaseStatus status) {
		this.status.setStatus(status.getStatus());
	}

	@Value(value = "${embedded.mongo.launchOnStartup}")
	private boolean launchOnStartup;

	@PostConstruct
	public void init() throws URISyntaxException, IllegalArgumentException,
			IOException {

		Net net = mongodConfig.net();
		status.setPort(net.getPort());
		status.setStatus(DATABASE_STOPPED);
		String host = net.getServerAddress().getHostName();
		status.setHost(new URI(host));
		if (launchOnStartup) {
			status.setStatus(DATABASE_STARTED);
			start();
		}
	}

	public void stop() {
		if (mongoClient != null) {
			mongoClient.close();
		}
		if (mongodProcess != null) {
			mongodProcess.stop();
		}
	}

	public DataBaseStatus getDatabaseStatus() {
		List<String> databaseNames = null;
		if (mongoClient != null) {
			try {
				databaseNames = mongoClient.getDatabaseNames();

				status.setDatabaseNames(databaseNames);
			} catch (Exception e) {
				status.setDatabaseNames(databaseNames);
			}
		}
		return status;

	}

	public void start() throws IOException {
		try {
			mongodProcess = mongodExecutable.start();
		} catch (IOException e) {
			LOGGER.error("Could not start mongod process", e);
		}
		try{
		mongoClient = new MongoClient(mongodConfig.net().getServerAddress()
				.getHostName(), mongodConfig.net().getPort());
		}
		catch(IOException e){
			LOGGER.error("Could not connect to mongo", e);
		}
	}

	@PreDestroy
	public void teardown() {
		if (mongoClient != null) {
			mongoClient.close();
		}
		if (mongodProcess != null) {
			mongodProcess.stop();
		}
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	public List<String> getCollectionNames(String databaseName) {
		Set<String> collections = mongoClient.getDB(databaseName)
				.getCollectionNames();
		return new ArrayList<String>(collections);
	}

	public List<DBObject> getDBObjects(String databaseName,
			String collectionName) {
		DB database = mongoClient.getDB(databaseName);
		DBCollection collection = database
				.getCollectionFromString(collectionName);
		return collection.find().toArray();
	}

	public List<String> getDatabaseNames() {
		return mongoClient.getDatabaseNames();
	}

	public void deleteDBObject(String databaseName, String collectionName,
			String objectId) {
		DB database = mongoClient.getDB(databaseName);
		DBCollection collection = database
				.getCollectionFromString(collectionName);
		DBObject o = collection.findOne(new ObjectId(objectId));
		collection.remove(o);
	}

}
