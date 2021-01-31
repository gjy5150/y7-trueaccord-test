package trueaccord.debts.model;

import java.io.Serializable;

/**
 * 
 * Class representing the Debts Table
 * 
 * Desription: A debt, which is money is owed to a collector
 * @author gjy5150
 * 
 */
public class Debts implements Serializable {
    
    private static final long serialVersionUID = -1L;
    
    private Integer id;
    private Double amount;

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
    
}
