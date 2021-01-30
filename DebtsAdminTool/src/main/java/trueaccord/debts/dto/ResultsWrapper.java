package trueaccord.dto;

import java.util.List;

/**
 * This class is a wrapper class that contains the results from the different services performed.
 * 
 * @author gjy5150
 */
public class ResultsWrapper {
    
    private List<DebtInformation> debtInfoList;

    public List<DebtInformation> getDebtInfoList() {
        return debtInfoList;
    }

    public void setDebtInfoList(List<DebtInformation> debtInfoList) {
        this.debtInfoList = debtInfoList;
    } 
}
