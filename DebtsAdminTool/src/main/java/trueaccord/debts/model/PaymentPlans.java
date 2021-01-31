package trueaccord.debts.model;

import java.io.Serializable;

/**
 * Class that represents the Payment Plans Table
 * 
 * Description: A payment plan, which is an amount needed to resolve a debt, as well as the frequency of when it will be paid. One-to-one with debt.
 * 
 * @author gjy5150
 */
public class PaymentPlans implements Serializable {
    
    private static final long serialVersionUID = -1L;
    
    private Integer id;
    private Integer debt_id;
    private Double amount_to_pay;
    private String installment_frequency;
    private Double installment_amount;
    private String start_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDebt_id() {
        return debt_id;
    }

    public void setDebt_id(Integer debt_id) {
        this.debt_id = debt_id;
    }

    public Double getAmount_to_pay() {
        return amount_to_pay;
    }

    public void setAmount_to_pay(Double amount_to_pay) {
        this.amount_to_pay = amount_to_pay;
    }

    public String getInstallment_frequency() {
        return installment_frequency;
    }

    public void setInstallment_frequency(String installment_frequency) {
        this.installment_frequency = installment_frequency;
    }

    public Double getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(Double installment_amount) {
        this.installment_amount = installment_amount;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    
}
