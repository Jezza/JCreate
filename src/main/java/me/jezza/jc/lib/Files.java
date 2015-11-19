package me.jezza.jc.lib;

import me.jezza.jc.JCreate;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Jezza
 */
public enum Files {
	;

	private static final int BUFFER_SIZE = 4096;
	private static final int EOL = -1;

	public static InputStream open(String file) {
		return Utils.class.getClassLoader().getResourceAsStream(file);
	}

	/**
	 * Deletes a dir recursively deleting anything inside it.
	 *
	 * @param dir The dir to delete
	 * @return true if the dir was successfully deleted
	 */
	public static boolean delete(File dir) {
		if (!dir.exists() || !dir.isDirectory())
			return false;
		File[] files = dir.listFiles();
		if (files == null)
			return false;
		for (File child : files) {
			if (child.isDirectory()) {
				delete(child);
			} else if (!child.delete()) {
				return false;
			}
		}
		return dir.delete();
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 *
	 * @param zipFile
	 * @param dest
	 */
	public static void openZip(String zipFile, File dest) {
		openZip(zipFile, dest, null);
	}

	public static void openZip(String zipFile, File dest, ZipEntryVisitor consumer) {
		if (!dest.exists())
			dest.mkdir();
		try (ZipInputStream zip = new ZipInputStream(open(zipFile))) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				String name = entry.getName();
				File file = new File(dest, name);
				//		JCreate.print("Extracting: {}, {}k -> {}k.", name, entry.getCompressedSize(), entry.getSize());
				if (entry.isDirectory()) {
					file.mkdirs();
					continue;
				}
				try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
					byte[] bytes = new byte[BUFFER_SIZE];
					int read;
					while ((read = zip.read(bytes)) != EOL)
						bos.write(bytes, 0, read);
				}
				if (consumer != null) {
					String newName = consumer.consume(name, entry, file);
					if (Utils.useable(newName))
						file.renameTo(new File(dest, newName));
				}
			}
		} catch (IOException e) {
			throw JCreate.error("Exception while reading zipFile: " + zipFile, e);
		}
	}

	@FunctionalInterface
	public interface ZipEntryVisitor {
		String consume(String name, ZipEntry entry, File file);
	}
}
