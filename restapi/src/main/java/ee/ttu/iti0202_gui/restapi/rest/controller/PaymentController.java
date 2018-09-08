package ee.ttu.iti0202_gui.restapi.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest controller class for REST API and MakseKeskus API communication.
 *
 * @author Priit Käärd
 */
@RestController
@RequestMapping(value = "/mkapi")
public class PaymentController {
    private static List<Map<String, String>> methods;

    /* Create dummy data */
    static {
        methods = new ArrayList<>();

        methods.add(new HashMap<String, String>() {
            {
                put("country", "ee");
                put("name", "swedbank");
                put("url", "https://payment-test.maksekeskus.ee/banklink.html?method=EE_SWED&trx=");
            }
        });
        methods.add(new HashMap<String, String>() {
            {
                put("country", "ee");
                put("name", "seb");
                put("url", "https://payment-test.maksekeskus.ee/banklink.html?method=EE_SEB&trx=");
            }
        });
        methods.add(new HashMap<String, String>() {
            {
                put("country", "ee");
                put("name", "lhv");
                put("url", "https://payment-test.maksekeskus.ee/banklink.html?method=EE_LHV&trx=");
            }
        });
    }

    /**
     * Method for endpoint /mkapi/methods to display all available payment methods.
     *
     * @return          Response entity with body of list of all payment methods.
     */
    @GetMapping("/methods")
    public ResponseEntity getPaymentMethods() {
        // Make a scheduled task to provide REST API with MK API payment methods.
        return ResponseEntity.ok(methods);
    }
}
