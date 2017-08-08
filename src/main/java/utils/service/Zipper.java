package utils.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class Zipper {

    public static File prepareZip(List<File> files) throws IOException {
        final File zipFile = File.createTempFile("files", ".zip");
        log.info("Zip file: " + zipFile.getAbsolutePath());
        try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {

            for (final File file : files) {

                final ZipEntry entry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(entry);
                IOUtils.copy(new FileInputStream(file), zipOutputStream);
            }
            zipOutputStream.close();
            return zipFile;
        }
    }

}
