package ee.ttu.iti0202_gui.restapi.rest.service;

import ee.ttu.iti0202_gui.restapi.rest.dao.StorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service layer class for storing files.
 */
@Service
public class StorageService {
    private final StorageDao storageDao;

    @Autowired
    public StorageService(StorageDao storageDao) {
        this.storageDao = storageDao;
    }

    /**
     * Method to add a new file to storage location.
     *
     * @param productId         Product ID.
     * @param file              Product image file.
     * @throws IOException      Possible exceptions.
     */
    public void addToGallery(Long productId, MultipartFile file) throws IOException{
        storageDao.createFile(productId, file);
    }

    /**
     * Method to load a file as a byte array.
     *
     * @param productId     Product ID to specify the folder.
     * @param filename      File name with extension.
     * @return              Byte array of the file.
     * @throws IOException  Possible exceptions.
     */
    public byte[] loadAsByteArray(Long productId, String filename) throws IOException{
        return storageDao.loadAsByteArray(productId, filename);
    }
}
