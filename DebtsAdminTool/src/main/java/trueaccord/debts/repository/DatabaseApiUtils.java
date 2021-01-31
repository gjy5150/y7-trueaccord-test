package trueaccord.debts.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import trueaccord.debts.model.Debts;
import trueaccord.debts.model.PaymentPlans;
import trueaccord.debts.model.Payments;

/**
 * This class contains the methods to retrieve debts, payment plans, and
 * payments from the TrueAccord Database API Service.
 *
 * @author gjy5150
 */
public class DatabaseApiUtils {

    /**
     * This method retrieves all debts.
     * 
     * @return List<Debts>
     */
    public static List<Debts> retrieveDebts() {

        List<Debts> retLst = new ArrayList<>();

        try {

            String apiUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/debts";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> results = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);
            if (results != null) {
                ObjectMapper objMapper = new ObjectMapper();
                objMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                
                //Read result into JSON NOde
                JsonNode root = objMapper.readTree(results.getBody());
                CollectionType javaType = objMapper.getTypeFactory().constructCollectionType(List.class, Debts.class);

                if (javaType != null) {
                    // convert JSON Node to list of debt objects
                    retLst.addAll(objMapper.readValue(root.toString(), javaType));
                }

            }
        } catch (Exception e) {
            Logger.getLogger(DatabaseApiUtils.class.getName()).log(Level.SEVERE, "retrieveDebts - " + e.getMessage(), e);
        }

        return retLst;
    }

    /**
     * This method retrieves all the available payment plans. 
     * 
     * @return List<PaymentPlans>
     */
    public static List<PaymentPlans> retrievePaymentPlans() {

        List<PaymentPlans> retLst = new ArrayList<>();

        try {

            String apiUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payment_plans";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> results = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);
            if (results != null) {
                ObjectMapper objMapper = new ObjectMapper();
                objMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                
                //Read result into JSON NOde
                JsonNode root = objMapper.readTree(results.getBody());
                CollectionType javaType = objMapper.getTypeFactory().constructCollectionType(List.class, PaymentPlans.class);

                if (javaType != null) {
                    // convert JSON Node to list of payment plan objects
                    retLst.addAll(objMapper.readValue(root.toString(), javaType));                   
                }

            }
        } catch (Exception e) {
            Logger.getLogger(DatabaseApiUtils.class.getName()).log(Level.SEVERE, "retrievePaymentPlans - " + e.getMessage(), e);
        }

        return retLst;
    }
    
    /**
     * This method retrieves all payments associated to debts.
     * 
     * @return 
     */
    public static List<Payments> retrievePayments() {

        List<Payments> retLst = new ArrayList<>();

        try {

            String apiUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payments";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> results = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, String.class);
            if (results != null) {
                ObjectMapper objMapper = new ObjectMapper();
                objMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                
                //Read result into JSON NOde
                JsonNode root = objMapper.readTree(results.getBody());
                CollectionType javaType = objMapper.getTypeFactory().constructCollectionType(List.class, Payments.class);

                if (javaType != null) {
                    // convert JSON Node to list of payment plan objects
                    retLst.addAll(objMapper.readValue(root.toString(), javaType));
                }

            }
        } catch (Exception e) {
            Logger.getLogger(DatabaseApiUtils.class.getName()).log(Level.SEVERE, "retrievePayments - " + e.getMessage(), e);
        }

        return retLst;
    }
    
    /**
     * Main method to test the methods in this class.
     * 
     * @param args 
     */
    public static void main(String args[]) {
        try {
            
            System.out.println("Debts:");
            List<Debts> debtList = DatabaseApiUtils.retrieveDebts();
            if (debtList != null && !debtList.isEmpty()) {
                ObjectMapper objMapper = new ObjectMapper();
                String jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtList);
                
                System.out.println(jsonStr);
            }
            
            System.out.println("\nPayment Plans:");
            List<PaymentPlans> ppList = DatabaseApiUtils.retrievePaymentPlans();
            if (ppList != null && !ppList.isEmpty()) {
                ObjectMapper objMapper = new ObjectMapper();
                String jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ppList);
                
                System.out.println(jsonStr);
            }
            
            System.out.println("\nPayments:");
            List<Payments> paymentList = DatabaseApiUtils.retrievePayments();
            if (paymentList != null && !paymentList.isEmpty()) {
                ObjectMapper objMapper = new ObjectMapper();
                String jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentList);
                
                System.out.println(jsonStr);
            }
            
        } catch (Exception e) {
            Logger.getLogger(DatabaseApiUtils.class.getName()).log(Level.SEVERE, "main - " + e.getMessage(), e);
        }
    }

}
