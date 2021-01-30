package trueaccord.model;

/**
 * 
 * Class representing the Debts Table
 * 
 * Desription: A debt, which is money is owed to a collector
 * @author gjy5150
 * 
 */
public class Debts {
    
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
