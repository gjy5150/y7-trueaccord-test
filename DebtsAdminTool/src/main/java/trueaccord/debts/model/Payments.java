package trueaccord.debts.model;

import java.io.Serializable;

/**
 * Class that represents the Payments Table.
 * 
 * Description: An individual payment installment which is made on a payment plan. Many-to-one with debts.
 * 
 * @author gjy5150
 */
public class Payments implements Serializable {
    
    private Integer payment_plan_id;
    private Double amount;
    private String date;

    public Integer getPayment_plan_id() {
        return payment_plan_id;
    }

    public void setPayment_plan_id(Integer payment_plan_id) {
        this.payment_plan_id = payment_plan_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
}
