package utils.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.collect.Lists.newArrayList;

public class Unzipper {

    public static List<File> unzip(InputStream zipInputStream) throws IOException {

        ZipInputStream entries = new ZipInputStream(zipInputStream);
        ZipEntry entry;

        List<File> files = newArrayList();

        while ((entry = entries.getNextEntry()) != null) {

            File tempFile = File.createTempFile("", entry.getName());
            ByteArrayOutputStream os = extractEntry(entries);

            Files.write(tempFile.toPath(), os.toByteArray());

            files.add(tempFile);

        }

        zipInputStream.close();
        entries.close();

        return files;

    }

    private static ByteArrayOutputStream extractEntry(InputStream is) throws IOException {
        ByteArrayOutputStream fos = null;
        try {
            fos = new ByteArrayOutputStream();
            final byte[] buf = new byte[2048];
            int read = 0;
            int length;
            while ((length = is.read(buf, 0, buf.length)) >= 0) {
                fos.write(buf, 0, length);
            }
        } catch (IOException ioex) {
            fos.close();
        }
        return fos;
    }


}
