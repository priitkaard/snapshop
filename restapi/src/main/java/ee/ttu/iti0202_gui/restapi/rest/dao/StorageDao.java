package ee.ttu.iti0202_gui.restapi.rest.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data access layer class for Storing files.
 */
@Repository
public class StorageDao {
    @Value("${snapshop.upload.directory}")
    private String uploadFolder;

    /**
     * Method to save a given file depending on product ID.
     *
     * @param productId     Product ID.
     * @param file          File as multipart file instance.
     * @throws IOException  Possible exceptions thrown.
     */
    public void createFile(Long productId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new FileNotFoundException("Input file was not found.");

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadFolder + productId.toString() + "/");
        Files.createDirectories(path);
        path = Paths.get(path.toString(), file.getOriginalFilename());
        Files.write(path, bytes);
    }

    /**
     * Method to load a file depending on product ID.
     *
     * @param productId             Product ID.
     * @param filename              File name with extension.
     * @return                      Byte array of needed file.
     * @throws IOException          Possible exceptions thrown.
     */
    public byte[] loadAsByteArray(Long productId, String filename) throws IOException{
        Path path = Paths.get(uploadFolder + productId.toString() + "/" + filename);
        return Files.readAllBytes(path);
    }
}
