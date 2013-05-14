package sf.bank.utilities.config;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sf.bank.utilities.beans.PathBean;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.Paths;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.MongodProcessOutputConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.process.store.IArtifactStore;

@Configuration
public class EmbeddedMongoConfiguration {

	@Value(value="${embedded.mongo.port}")
	private Integer mongoPort;

	@Resource
	private PathBean artifactoryPath;

	@Resource
	private PathBean tempPath;

	private Logger logger = Logger.getLogger(getClass().getName());

	@Value(value="${embedded.mongo.download.url}")
	private String downloadUrl;

	@Bean
	public MongodConfig mongodConfig() throws UnknownHostException {
		return new MongodConfig(Version.V2_4_1, mongoPort,
				Network.localhostIsIPv6());
	}

	public IRuntimeConfig runtimeConfig() {
		RuntimeConfigBuilder runtimeConfigBuilder = new RuntimeConfigBuilder();
		runtimeConfigBuilder.artifactStore(artifactStore())
				.processOutput(MongodProcessOutputConfig.getInstance(logger))
				.commandLinePostProcessor(new ICommandLinePostProcessor.Noop());

		return runtimeConfigBuilder.build();

	}

	public IArtifactStore artifactStore() {
		ArtifactStoreBuilder artifactStoreBuilder = new ArtifactStoreBuilder();
		artifactStoreBuilder.tempDir(tempPath)
				.executableNaming(new UUIDTempNaming()).cache(true)
				.download(downloadConfig())

		;
		IArtifactStore artifactStore = null;

		try {
			artifactStore = artifactStoreBuilder.build();
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					e.getMessage() + " File name: " + tempPath.getPathname());
		}
		return artifactStore;
	}

	public IDownloadConfig downloadConfig() {
		DownloadConfigBuilder downloadConfigBuilder = new DownloadConfigBuilder();
		downloadConfigBuilder
				.fileNaming(new UUIDTempNaming())
				.downloadPath(downloadUrl)
				.progressListener(new StandardConsoleProgressListener())
				.artifactStorePath(artifactoryPath)
				.downloadPrefix("embedmongo-download")
				.userAgent(
						"Mozilla/5.0 (compatible; Embedded MongoDB; +https://github.com/flapdoodle-oss/embedmongo.flapdoodle.de)")
				.packageResolver(new Paths(Command.MongoD));

		return downloadConfigBuilder.build();
	}

	@Bean(destroyMethod = "stop")
	public MongodExecutable mongodExecutable() throws UnknownHostException {
		MongodStarter mongodStarter = MongodStarter
				.getInstance(runtimeConfig());
		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = mongodStarter.prepare(mongodConfig());
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					e.getMessage() + " File name: " + tempPath.getPathname());
		}

		return mongodExecutable;
	}

}
