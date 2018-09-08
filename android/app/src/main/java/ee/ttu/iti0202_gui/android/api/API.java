package ee.ttu.iti0202_gui.android.api;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ee.ttu.iti0202_gui.android.models.Category;
import ee.ttu.iti0202_gui.android.models.Credentials;
import ee.ttu.iti0202_gui.android.models.Order;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.models.User;
import ee.ttu.iti0202_gui.android.utils.ImageHelper;

/**
 * REST API functionality class using RestTemplate.
 *
 * @author Priit Käärd
 */
public class API {
    private static final String TAG = "API";
    // private static final String SERVER_URL = "http://10.0.2.2:8080";
    private static final String SERVER_URL = "http://138.197.189.216:8080";
    private static API instance;

    private Credentials credentials;

    /**
     * Constructor
     *
     * @param credentials       Basic auth credentials.
     */
    private API(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Static method to get API instance.
     *
     * @param credentials       Basic auth credentials.
     * @return                  API instance with given credentials.
     */
    public static API getInstance(Credentials credentials) {
        if (instance == null || instance.credentials != credentials) {
            instance = new API(credentials);
        }
        return instance;
    }


    /* REST API Functionality */

    /**
     * Method to check if current user is authenticated.
     *
     * @return      Boolean if user is authenticated.
     */
    public boolean isAuthenticated() throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if (credentials != null) {
            Log.d(TAG, "isAuthenticated: Credentials set. Setting basic auth headers...");
            headers.add("Authorization", getAuthString());
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User> responseEntity = restTemplate
                    .exchange(parseURL("/auth/check"), HttpMethod.GET, entity, User.class);
            Log.d(TAG, "isAuthenticated: Response code: " + responseEntity.getStatusCode());

            return responseEntity.getStatusCode().equals(HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            Log.e(TAG, "isAuthenticated: Response code: " + e.getStatusCode());
            return false;
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to register a new user instance.
     *
     * @param user          New user instance.
     */
    public User registerUser(User user) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity<User> entity = new HttpEntity<>(user);
        Log.d(TAG, "registerUser: User entity set.");

        try {
            ResponseEntity<User> responseEntity = restTemplate
                    .exchange(parseURL("/auth/register"), HttpMethod.POST, entity, User.class);
            Log.d(TAG, "registerUser: Response code: " + responseEntity.getStatusCode());

            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to load products depending on passed parameters.
     *
     * @param parameters                Query parameters for GET request.
     * @return                          List of products.
     * @throws RequestFailedException   Request failure exception.
     */
    public List<Product> loadProducts(Map<String, String> parameters) throws RequestFailedException {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();

        if (credentials != null) {
            Log.d(TAG, "loadProducts: Credentials set. Setting basic auth headers...");
            headers.add("Authorization", getAuthString());
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SERVER_URL + "/product/load");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        Log.d(TAG, "loadProducts: URL: " + builder.toUriString());

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Product[]> responseEntity = restTemplate
                    .exchange(builder.toUriString(), HttpMethod.GET, entity, Product[].class);
            Log.d(TAG, "loadProducts: Response code: " + responseEntity.getStatusCode());
            Log.d(TAG, "loadProducts: Response: " + Arrays.toString(responseEntity.getBody()));

            return Arrays.asList(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            Log.e(TAG, "loadProducts: Response code: " + e.getStatusCode());
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to load categories.
     */
    public List<Category> loadCategories() throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity entity = new HttpEntity(getHeaders());

        try {
            ResponseEntity<Category[]> responseEntity = restTemplate
                    .exchange(parseURL("/product/categories"), HttpMethod.GET,
                            entity, Category[].class);
            Log.d(TAG, "loadCategories: Response code: " + responseEntity.getStatusCode());

            return Arrays.asList(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to load bitmap for product from REST API.
     *
     * @param productId         Product id.
     * @param fileName          Product image file name.
     * @return                  Product image bitmap.
     * @throws RequestFailedException       Request failure exception.
     */
    public Bitmap loadProductBitmap(Long productId, String fileName,
                                    int viewWidth, int viewHeight) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity<?> entity = new HttpEntity<>(getHeaders());

        try {
            ResponseEntity<byte[]> responseEntity = restTemplate
                    .exchange(String.format(Locale.getDefault(),
                            "%s/gallery/load/%d/%s", SERVER_URL, productId, fileName),
                            HttpMethod.GET, entity, byte[].class);

            return ImageHelper.decodeSampledBitmapFromByteArray(responseEntity.getBody(),
                    viewWidth, viewHeight);
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to add a new product.
     *
     * @param product                       Product instance.
     * @return                              Saved product instance.
     * @throws RequestFailedException       Request failure exception.
     */
    public Product addNewProduct(Product product) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();

        if (credentials != null) {
            headers.add("Authorization", getAuthString());
        }

        HttpEntity<Product> entity = new HttpEntity<>(product, headers);

        try {
            ResponseEntity<Product> responseEntity = restTemplate.exchange(
                    parseURL("/product/new"), HttpMethod.POST, entity, Product.class);
            Log.d(TAG, "addNewProduct: Response code: " + responseEntity.getStatusCode());

            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to upload image depending on product and image file.
     *
     * @param product           Product instance.
     * @param file              Image file instance.
     */
    public boolean uploadImage(Product product, File file) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();


        MultiValueMap<String, Object> parts =
                new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(file));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (credentials != null) {
            headers.add("Authorization", getAuthString());
        }

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parts, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate
                    .exchange(parseURL("/gallery/upload/" + product.getId().toString()),
                            HttpMethod.POST, entity, String.class);
            Log.d(TAG, "uploadImage: Response code: " + responseEntity.getStatusCode());

            return responseEntity.getStatusCode().equals(HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure.");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to request payment methods from rest api.
     *
     * @return      list of payment method data.
     */
    public List<Map<String, String>> getPaymentMethods() {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if (credentials != null) {
            headers.add(HttpHeaders.AUTHORIZATION, getAuthString());
        }
        HttpEntity entity = new HttpEntity(headers);

        try {
            ResponseEntity responseEntity = restTemplate
                    .exchange(parseURL("/mkapi/methods"), HttpMethod.GET,
                    entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(responseEntity.getBody().toString(),
                    new TypeReference<List<Map<String, String>>>() { });
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to create a transaction through rest api.
     *
     * @param order                     Order instance.
     * @return                          Updated order instance..
     */
    public Order makeOrder(Order order) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();

        if (credentials != null) {
            headers.add(HttpHeaders.AUTHORIZATION, getAuthString());
        }

        HttpEntity<Order> entity = new HttpEntity<>(order, headers);

        try {
            return restTemplate.exchange(parseURL("/orders/makeorder"), HttpMethod.POST,
                    entity, Order.class).getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to confirm given order.
     *
     * @param order                         Order instance.
     * @return                              Updated order instance.
     * @throws RequestFailedException       Request failure exception.
     */
    public Order confirmOrder(Order order) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if (credentials != null) {
            headers.setAuthorization(new HttpBasicAuthentication(credentials.getUsername(),
                    credentials.getPassword()));
        }
        HttpEntity<Order> entity = new HttpEntity<>(order, headers);

        try {
            return restTemplate.exchange(parseURL("/orders/confirm"), HttpMethod.POST,
                    entity, Order.class).getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to request order cancellation.
     *
     * @param order         Order instance.
     */
    public void cancelOrder(Order order) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity<Order> entity = new HttpEntity<>(order, getHeaders());
        try {
            restTemplate.exchange(parseURL("/orders/cancel"), HttpMethod.POST,
                    entity, String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to load user orders from REST api.
     * @return          List of user orders.
     */
    public List<Order> loadUserOrders() throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity<Order> entity = new HttpEntity<>(getHeaders());
        try {
            ResponseEntity<Order[]> responseEntity = restTemplate
                    .exchange(parseURL("/orders/myorders"), HttpMethod.GET,
                    entity, Order[].class);
            return Arrays.asList(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Method to retrieve user data.
     *
     * @return                              User instance.
     * @throws RequestFailedException       Possible request failures.
     */
    public User getCurrentUserData() throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity entity = new HttpEntity(getHeaders());
        try {
            return restTemplate.exchange(parseURL("/auth/check"), HttpMethod.GET,
                    entity, User.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /**
     * Metho to update user data.
     *
     * @param user          Partial user object with updated data.
     * @return              Update user object.
     * @throws RequestFailedException       Possible errors on the request.
     */
    public User updateUserData(User user) throws RequestFailedException {
        RestTemplate restTemplate = getRestTemplate();

        HttpEntity<User> entity = new HttpEntity<>(user, getHeaders());

        try {
            return restTemplate.exchange(parseURL("/user/"), HttpMethod.PUT,
                    entity, User.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RequestFailedException(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RequestFailedException("Server failure");
        } catch (ResourceAccessException e) {
            throw new RequestFailedException("Check your internet connection");
        }
    }

    /* Helper functions */

    /**
     * Helper method to generate basic authentication string.
     *
     * @return          Basic Auth string.
     */
    private String getAuthString() {
        if (credentials == null) return null;
        String credsLine = credentials.getUsername() + ":" + credentials.getPassword();
        return "Basic " + Base64.encodeToString(credsLine.getBytes(), Base64.NO_WRAP);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (credentials != null) {
            headers.setAuthorization(new HttpBasicAuthentication(
                    credentials.getUsername(), credentials.getPassword()));
        }
        return headers;
    }

    /**
     * Helper method to get set up rest template.
     *
     * @return          RestTemplate instance.
     */
    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        // restTemplate.setErrorHandler(new MyResponseErrorHandler());

        return restTemplate;
    }

    /**
     * Helper method to parse url endpoint.
     *
     * @param endpoint          Endpoint path.
     * @return                  Absolute URL.
     */
    private String parseURL(String endpoint) {
        return SERVER_URL + endpoint;
    }

    /* REST API Exceptions */

    /**
     * Custom exception class for request failure.
     */
    public class RequestFailedException extends Exception {
        private String reason;

        RequestFailedException(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }
}
