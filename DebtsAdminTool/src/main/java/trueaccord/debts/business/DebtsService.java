package trueaccord.debts.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import trueaccord.debts.common.SystemConstants;
import trueaccord.debts.model.Debts;
import trueaccord.debts.model.PaymentPlans;
import trueaccord.debts.repository.DatabaseApiUtils;
import trueaccord.debts.dto.DebtOutput;
import trueaccord.debts.model.Payments;

/**
 *
 * Contains the methods necessary to calculate next payment due, remaining debt
 * amount and whether or not a debt is in a payment plan.
 *
 * @author gjy5150
 */
public class DebtsService implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * This method retrieves all information related to the debts and returns the data as a list of DebtInformation objects.
     * 
     * @return List<DebtInfortion>
     */
    public List<DebtOutput> retrieveAllDebts() {

        List<DebtOutput> retLst = new ArrayList<>();

        try {

            List<Debts> debtLst = DatabaseApiUtils.retrieveDebts();
            if (debtLst != null && !debtLst.isEmpty()) {

                List<PaymentPlans> paymentPlanLst = DatabaseApiUtils.retrievePaymentPlans();
                List<Payments> paymentLst = DatabaseApiUtils.retrievePayments();

                for (Debts debt : debtLst) {
                    //Build the debt output object to add to the list
                    retLst.add(this.buildDebtOutputObject(debt, paymentPlanLst, paymentLst));
                }
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "retrieveAllDebts - " + e.getMessage(), e);
        }

        return retLst;
    }

     /**
     * This method retrieves all the information related to a single debt .
     * 
     * @return DebtInformation
     */
    public DebtOutput retrieveSingleDebt(Integer debtId) {

        DebtOutput retObj = new DebtOutput();

        try {

            List<Debts> debtLst = DatabaseApiUtils.retrieveDebts();
            if (debtLst != null && !debtLst.isEmpty()) {

                List<PaymentPlans> paymentPlanLst = DatabaseApiUtils.retrievePaymentPlans();
                List<Payments> paymentLst = DatabaseApiUtils.retrievePayments();

                for (Debts debt : debtLst) {
                    
                    if (debt.getId().equals(debtId)) {
                        //Build the debt information object 
                        retObj = this.buildDebtOutputObject(debt, paymentPlanLst, paymentLst);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "retrieveSingleDebt - " + e.getMessage(), e);
        }

        return retObj;
    }
    
    /**
     * This method builds the debt output object with the remaining payment and next payment due date.
     * @param debt
     * @param paymentPlanLst
     * @param paymentLst
     * @return DebtOutput
     */
    private DebtOutput buildDebtOutputObject(Debts debt, List<PaymentPlans> paymentPlanLst, List<Payments> paymentLst) {
        
        DebtOutput retObj = new DebtOutput();
        
        try {

            boolean hasPaymentPlan = false;
            PaymentPlans paymentPlan;
            Integer paymentPlanId;
            Double remainingDebtAmt = 0D;

            retObj.setId(debt.getId());
            retObj.setAmount(debt.getAmount());

            paymentPlan = this.retrievePaymentPlanId(debt.getId(), paymentPlanLst);

            //Determine is a debt has an associated payment plan
            hasPaymentPlan = paymentPlan == null ? false : true;

            retObj.setIs_in_payment_plan(hasPaymentPlan);
            remainingDebtAmt = paymentPlan == null ? debt.getAmount() : paymentPlan.getAmount_to_pay();
            paymentPlanId = paymentPlan == null ? null : paymentPlan.getId();

            //Calculate the remainging debt amount
            retObj.setRemaining_amount(this.calculateRemainingDebt(paymentPlanId, remainingDebtAmt, paymentLst));

            if (paymentPlan != null && retObj.getRemaining_amount() > 0D) {
                retObj.setNext_payment_due_date(this.calculateNextPaymentDueDate(paymentPlanId, paymentPlan, paymentLst));
            }
        } catch (Exception e) {

        }

        return retObj;
    }

    /**
     * Method to retrieve the payment plan id for the debt.
     *
     * @param debtId
     * @param paymentPlanLst
     *
     * @return Integer - Payment plan Id
     */
    private PaymentPlans retrievePaymentPlanId(Integer debtId, List<PaymentPlans> paymentPlanLst) {

        PaymentPlans retObj = null;

        try {
            if (paymentPlanLst != null && !paymentPlanLst.isEmpty()) {
                for (PaymentPlans pp : paymentPlanLst) {
                    if (debtId.equals(pp.getDebt_id())) {
                        retObj = pp;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "retrievePaymentPlanId - " + e.getMessage(), e);
        }

        return retObj;
    }

    /**
     *
     * This method calculates the remaining debt amount.
     *
     * @param paymentPlanId
     * @param remainingDebtAmount
     * @param paymentLst
     *
     * @return Double - Remaining debt amount
     */
    private Double calculateRemainingDebt(Integer paymentPlanId, Double remainingDebtAmount, List<Payments> paymentLst) {

        BigDecimal retAmt = new BigDecimal(remainingDebtAmount);

        try {

            if (paymentLst != null && !paymentLst.isEmpty()) {

                DecimalFormat df = new DecimalFormat("0.00");
                BigDecimal totPaidAmt = new BigDecimal(0D);

                for (Payments payment : paymentLst) {
                    if (payment.getPayment_plan_id().equals(paymentPlanId)) {
                        totPaidAmt = totPaidAmt.add(new BigDecimal(payment.getAmount()));
                    }
                }

                //Rounding remaining amount to 2 decimal places
                retAmt = retAmt.subtract(totPaidAmt).setScale(SystemConstants.NUMBER_DECIMALS, RoundingMode.HALF_UP);

                if (retAmt.doubleValue() < 0D) {
                    retAmt = new BigDecimal(0D);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "calculateRemainingDebt - " + e.getMessage(), e);
        }

        return retAmt.doubleValue();
    }

    /**
     *
     * This method will calculate the next payment due date
     *
     * @param debtId
     * @param paymentPlanLst
     * @param paymentLst
     *
     * @return
     */
    private String calculateNextPaymentDueDate(Integer debtId, PaymentPlans paymentPlan, List<Payments> paymentLst) {

        String retVal = null;
        try {

            //ISO 8601 UTC date formart -e.g. 2020-09-28T16:18:30Z
            DateTimeFormatter dtf1 = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'[HH][:mm][:ss]Z]")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();

            LocalDateTime startDate = LocalDateTime.parse(paymentPlan.getStart_date(), dtf1);
            LocalDateTime paymentDueDate = null;
            LocalDateTime lastPaymentDate = null;

            if (paymentLst != null && !paymentLst.isEmpty()) {

                LocalDateTime tmpDate;

                //Grab latest payment date
                for (Payments payment : paymentLst) {

                    if (paymentPlan.getId().equals(payment.getPayment_plan_id())) {
                        tmpDate = LocalDateTime.parse(payment.getDate(), dtf1);

                        if (lastPaymentDate == null || tmpDate.isAfter(lastPaymentDate)) {
                            lastPaymentDate = tmpDate;
                        }
                    }
                }
            }

            //If we don't have a payment then set it to the start date of the payment plan
            if (lastPaymentDate == null) {
                paymentDueDate = startDate;
            } else {
                //Calculate the next payment due date
                if (paymentPlan.getInstallment_frequency().equals(SystemConstants.FREQUENCY_WEEKLY)) {
                    paymentDueDate = this.calcNextFrequencyDate(startDate, lastPaymentDate, 1);
                } else if (paymentPlan.getInstallment_frequency().equals(SystemConstants.FREQUENCY_BI_WEEKLY)) {
                    paymentDueDate = this.calcNextFrequencyDate(startDate, lastPaymentDate, 2);
                }
            }

            retVal = paymentDueDate.format(dtf1);

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "calculateNextPaymentDueDate - " + e.getMessage(), e);
        }
        return retVal;
    }

    /**
     * This method calculates the next frequency date based on start date, end date, and weeks to add (e.g. WEEKLY = 1, BI WEEKLY = 2)
     * @param startDt
     * @param endDt
     * @param weeksToAdd
     * @return 
     */
    private LocalDateTime calcNextFrequencyDate(LocalDateTime startDt, LocalDateTime endDt, Integer weeksToAdd) {
        LocalDateTime retDate = startDt;
        try {

            //Loop through the frequency dates starting from the payment plan start date and ending before or at the last payment date
            for (LocalDateTime ldt = startDt; ldt.isBefore(endDt) || ldt.isEqual(endDt); ldt = ldt.plusWeeks(weeksToAdd)) {
                retDate = ldt;
            }

            //Calculation the next frequency date
            retDate = retDate.plusWeeks(weeksToAdd);

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "calcNextFrequencyDate - " + e.getMessage(), e);
        }
        return retDate;
    }

    /**
     * Main method to test the methods in this class.
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            DebtsService ds = new DebtsService();
            List<DebtOutput> debtInfoLst = ds.retrieveAllDebts();
            String jsonStr;
            if (!debtInfoLst.isEmpty()) {
                
                jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtInfoLst);

                System.out.println(jsonStr);
            }

            System.out.println("\n---------------------------------------------------------");
            DebtOutput outObj = ds.retrieveSingleDebt(2);
            jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outObj);
            System.out.println(jsonStr);
            
        } catch (Exception e) {
            Logger.getLogger(DebtsService.class.getName()).log(Level.SEVERE, "main - " + e.getMessage(), e);
        }
    }

}
