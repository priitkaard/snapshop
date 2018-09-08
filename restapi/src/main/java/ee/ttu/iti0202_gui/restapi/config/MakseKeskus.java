package ee.ttu.iti0202_gui.restapi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ttu.iti0202_gui.restapi.rest.model.Order;
import ee.ttu.iti0202_gui.restapi.rest.model.Product;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class MakseKeskus {
    private static final String AUTH_USERNAME = "f7741ab2-7445-45f9-9af4-0d0408ef1e4c";
    private static final String AUTH_PASSWORD = "pfOsGD9oPaFEILwqFLHEHkPf7vZz4j3t36nAcufP1abqT9l99koyuC1IWAOcBeqt";

    private static final String SERVER_URL = "https://api-test.maksekeskus.ee/v1/";
    private static final String ENDPOINT_TRANSACTIONS = "transactions";

    public MakseKeskus() { }

    /**
     * Helper to generate an auth string for http request.
     */
    private String getAuthString() {
        try {
            return "Basic " + Base64.getEncoder()
                    .encodeToString((AUTH_USERNAME + ":" + AUTH_PASSWORD).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Method to create transaction and retrieve the ID.
     * TODO: Handle RestTemplate Exceptions.
     *
     * @param order                 Order instance.
     * @return                      Transaction id as a String.
     * @throws IOException          IOException thrown on object mapping.
     */
    public String generateTransaction(Order order) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, getAuthString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(generateJSON(order), headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(SERVER_URL + ENDPOINT_TRANSACTIONS, HttpMethod.POST, entity, String.class);

        System.out.println(responseEntity.getBody());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(responseEntity.getBody(),
                new TypeReference<Map<String, Object>>() {});

        return (String) result.getOrDefault("id", "");
    }

    /**
     * Generate JSON String with data from order instance.
     *
     * @param order         Order instance.
     * @return              JSON String of order info.
     */
    private Map<String, Object> generateJSON(Order order) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Product p : order.getProducts()) {
            totalAmount = totalAmount.add(p.getPrice());
        }
        final BigDecimal finalAmount = totalAmount;

        Map<String, Object> result = new HashMap<>();
        result.put("transaction", new HashMap<String, Object>() {
            {
                put("amount", finalAmount.stripTrailingZeros().toPlainString());
                put("currency", "EUR");
                put("reference", order.getCustomer().getUsername() + "'s order");
                put("merchant_data", order.getCustomer().getUsername() +
                        " (" + order.getCustomer().getId() + ")");
                put("transaction_url", new HashMap<String, Object>() {
                    {
                        put("return_url", new HashMap<String, String>() {
                            {
                                put("url", "payment://success");
                                put("method", "POST");
                            }
                        });
                        put("cancel_url", new HashMap<String, String>() {
                            {
                                put("url", "payment://cancel");
                                put("method", "POST");
                            }
                        });
                        put("notification_url", new HashMap<String, String>() {
                            {
                                put("url", "payment://notification");
                                put("method", "POST");
                            }
                        });
                    }
                });
            }
        });
        result.put("customer", new HashMap<String, String>() {
            {
                put("email", order.getCustomer().getEmail());
                put("ip", "123.123.123.123");
                put("country", "et");
                put("locale", "et");
            }
        });

        return result;
    }
}
