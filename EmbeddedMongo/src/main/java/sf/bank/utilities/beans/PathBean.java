package sf.bank.utilities.beans;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import de.flapdoodle.embed.process.io.directories.IDirectory;


public class PathBean implements IDirectory {

	private String pathname;
	
	private File file;
	
	@Autowired
	public PathBean(String pathname) {
		this.pathname = pathname;
		file = new File(pathname);
	}
	
	@Override
	public File asFile() {
		return file;
	}

	/**
	 * @return the pathname
	 */
	public String getPathname() {
		return pathname;
	}

	/**
	 * @param pathname the pathname to set
	 */
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

}
