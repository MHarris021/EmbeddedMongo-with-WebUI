package sf.bank.utilities.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import sf.bank.utilities.beans.PathBean;

@Configuration
@EnableScheduling
public class CleanUpTempFilesTask {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private PathBean tempPath;

	@Value(value = "${embedded.mongo.executable.name}")
	private String executableName;

	@Value(value = "${embedded.mongo.databasefolder.name}")
	private String databaseFolderName;

	public List<File> getTemporaryExecutables() {
		List<File> files = new ArrayList<File>();
		IOFileFilter fileFilter = FileFilterUtils
				.prefixFileFilter(executableName);

		files.addAll(FileUtils.listFiles(tempPath.asFile(), fileFilter, null));
		return files;
	}

	public List<File> getTemporaryDatabaseFolders() {
		List<File> files = new ArrayList<File>();
		IOFileFilter fileFilter = FileFilterUtils
				.makeDirectoryOnly(FileFilterUtils
						.prefixFileFilter(databaseFolderName));
		IOFileFilter directoryFilter = FileFilterUtils.and(FileFilterUtils
				.notFileFilter(FileFilterUtils.nameFileFilter(tempPath.asFile()
						.getPath())), FileFilterUtils
				.prefixFileFilter(databaseFolderName));

		files.addAll(FileUtils.listFilesAndDirs(tempPath.asFile(), fileFilter,
				directoryFilter));
		files.remove(tempPath.asFile());

		return files;
	}

	public File findNewest(List<File> files) {
		File file = null;
		for (File tempFile : files) {
			if (null == file) {
				file = tempFile;
			} else if (FileUtils.isFileNewer(tempFile, file)) {
				file = tempFile;
			}
		}
		return file;
	}

	@Scheduled(cron = "${embedded.mongo.cleanup.cron}")
	public void deleteFiles() {
		logger.info("Performing routine cleanup:...\n");
		List<File> files = getTemporaryExecutables();
		File newestFile = findNewest(files);
		deleteOldFiles(files, newestFile);

		List<File> directories = getTemporaryDatabaseFolders();
		File newestDirectory = findNewest(directories);
		deleteOldFiles(directories, newestDirectory);
	}

	public void deleteOldFiles(List<File> files, File newestFile) {
		for (File file : files) {
			if (FileUtils.isFileOlder(file, newestFile)) {
				logger.info("Deleting file: " + file.getPath());
				try {
					FileUtils.forceDelete(file);
				} catch (IOException e) {
					logger.error("Could not delete file: " + file.getPath());
				}
			}
		}
	}

}
