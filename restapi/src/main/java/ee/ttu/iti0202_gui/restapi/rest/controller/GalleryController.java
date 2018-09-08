package ee.ttu.iti0202_gui.restapi.rest.controller;

import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import ee.ttu.iti0202_gui.restapi.rest.service.ProductService;
import ee.ttu.iti0202_gui.restapi.rest.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller class for product images.
 */
@RestController
@RequestMapping("/gallery/")
public class GalleryController {
    private final StorageService storageService;
    private final ProductService productService;

    @Autowired
    public GalleryController(StorageService storageService, ProductService productService) {
        this.storageService = storageService;
        this.productService = productService;
    }

    /**
     * Endpoint to upload image file and attaching it to product via its ID.
     *
     * @param file              Image file.
     * @param productId         Product ID.
     * @return                  Response.
     */
    @PostMapping("/upload/{product_id}")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file,
                                     @PathVariable("product_id") Long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (!product.isPresent()) return ResponseEntity.badRequest().body("Product with that ID was not found.");

        try {
            storageService.addToGallery(productId, file);
            productService.addImageNameById(product.get(), file.getOriginalFilename());
            return ResponseEntity.ok("Image uploaded");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred");
        }
    }

    /**
     * Endpoint to load an image file by its name and products ID.
     *
     * @param productId         Products ID.
     * @param filename          Image file name.
     * @return                  Image file as a byte array.
     * @throws IOException      Possible exceptions thrown.
     */
    @GetMapping("/load/{product_id}/{filename:.+}")
    @ResponseBody
    public byte[] serveFile(@PathVariable("product_id") Long productId, @PathVariable String filename)
            throws IOException{
        return storageService.loadAsByteArray(productId, filename);
    }
}
