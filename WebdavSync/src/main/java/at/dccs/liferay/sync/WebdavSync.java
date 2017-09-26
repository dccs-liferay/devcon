package at.dccs.liferay.sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * This little tool allows to sync files by Webdav
 * It was specifically created to be used with Liferay Webdav
 * and uses the display name attribute to store the file
 * instead of the name sent by the remote service
 *
 */
public class WebdavSync {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please provide a config file in json format as first parameter!");
			return;
		}
		try {
			WebdavSync sync = new WebdavSync(args[0]);
			sync.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebdavSync(String configfile) throws JsonParseException, JsonMappingException, IOException {
		readConfig(configfile);
	}

	private void readConfig(String configfile) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		File file = new File(configfile);
		config = mapper.readValue(file, Configuration.class);
	}

	public void process() {
		for (Server server : config.getServer()) {
			if (server.isEnabled()) {
				try {
					System.out.println("--- Processing " + server.getName() + " ----");
					process(server);
					System.out.println("--- Successfully processed " + server.getName() + " ----");
					System.out.println();
				} catch (IOException e) {
					System.out.println("Synchronisation error for server " + server.getName() + " ----");
					e.printStackTrace();
				}
			}
		}
	}

	public void process(Server server) throws IOException {
		Sardine sardine = SardineFactory.begin(server.getUsername(), server.getPassword());
		for (Folder folder : server.getFolders()) {
			String basepath = folder.getRemotePath() + folder.getSyncFolder();
			syncFolder(sardine, server, folder, basepath);
		}
	}

	public void syncFolder(Sardine sardine, Server server, Folder folder, String basepath) throws IOException {
		String serverpath = server.getUrl() + basepath;

		List<DavResource> resources = sardine.list(serverpath, -1);
		for (DavResource davResource : resources) {
			if (davResource.isDirectory()) {
				if (davResource.getPath().equals(basepath))
					continue; // also returns the root path, ignore it
				syncFolder(sardine, server, folder, davResource.getPath());
			} else {
				processFile(sardine, davResource, server, folder);
			}
		}
	}

	public void processFile(Sardine sardine, DavResource davResource, Server server, Folder folder) throws IOException {

		if (server.isBlacklisted(davResource.getDisplayName()))
			return;

		String filePathRelativeToRemoteDirectory = davResource.getPath().replace(folder.getRemotePath(), "");
		filePathRelativeToRemoteDirectory = filePathRelativeToRemoteDirectory.replaceAll(davResource.getName(),
				davResource.getDisplayName());
		Path localFilePath = Paths.get(folder.getLocalPath(), filePathRelativeToRemoteDirectory);
		String remoteFileUrl = server.getUrl() + davResource.getPath();

		if (server.isUpload()) {
			// Remark: Structures can't be changed by upload, it's forbidden; Test for it? Diff & Warn?
			// Remark: Download and diff before uploading?
			File f = localFilePath.toFile();
			if (f.exists()) {
				InputStream fis = new FileInputStream(f);
				System.out.println("Uploading: " + localFilePath + " --> " + remoteFileUrl);
				sardine.put(remoteFileUrl, fis);
			}
		} else {
			Files.createDirectories(localFilePath.getParent());
			InputStream ioStream = sardine.get(remoteFileUrl);
			try {
				System.out.println("Downloading " + remoteFileUrl + " --> " + localFilePath);
				Files.copy(ioStream, localFilePath, StandardCopyOption.REPLACE_EXISTING);
			} finally {
				ioStream.close();
			}
		}
	}

	private Configuration config = null;
}
