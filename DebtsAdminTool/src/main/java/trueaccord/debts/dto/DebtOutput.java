package trueaccord.debts.dto;

import java.io.Serializable;

/**
 * Class that represents the debt information for each debt output.
 * 
 * @author gjy5150
 */
public class DebtOutput implements Serializable {
    
    private static final long serialVersionUID = -1L;
    
    private Integer id; //Debt Id
    private Double amount; //Debt amount
    private boolean is_in_payment_plan;
    private Double remaining_amount;
    private String next_payment_due_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isIs_in_payment_plan() {
        return is_in_payment_plan;
    }

    public void setIs_in_payment_plan(boolean is_in_payment_plan) {
        this.is_in_payment_plan = is_in_payment_plan;
    }

    public Double getRemaining_amount() {
        return remaining_amount;
    }

    public void setRemaining_amount(Double remaining_amount) {
        this.remaining_amount = remaining_amount;
    }

    public String getNext_payment_due_date() {
        return next_payment_due_date;
    }

    public void setNext_payment_due_date(String next_payment_due_date) {
        this.next_payment_due_date = next_payment_due_date;
    }
    
}
