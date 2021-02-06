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
import trueaccord.debts.dto.MessageOutput;
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
     * This method retrieves all information related to the debts and returns
     * the data as a list of DebtOutput objects.
     *
     * @return List<DebtOutput>
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
                    retLst.add(this.buildDebtOutput(debt, paymentPlanLst, paymentLst));
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
     * @return DebtOutput
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
                        retObj = this.buildDebtOutput(debt, paymentPlanLst, paymentLst);
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
     * This method contains all the necessary logic to build the debt output
     * object including the following calculations:
     *
     * 1.) Has Payment Plan Check. 2.) Remaining Amount Calculation. 3.) Next
     * Payment Due Date Calculation.
     *
     * @param debt
     * @param paymentPlanLst
     * @param paymentLst
     * @return DebtOutput
     */
    private DebtOutput buildDebtOutput(Debts debt, List<PaymentPlans> paymentPlanLst, List<Payments> paymentLst) {

        DebtOutput retObj = new DebtOutput();

        try {

            boolean hasPaymentPlan = false;
            PaymentPlans paymentPlan;
            Integer paymentPlanId;
            Double remainingDebtAmt = 0D;

            retObj.setId(debt.getId());
            retObj.setAmount(debt.getAmount());

            paymentPlan = this.retrievePaymentPlanId(debt.getId(), paymentPlanLst);

            //Determine if a debt has an associated payment plan
            hasPaymentPlan = paymentPlan == null ? false : true;

            retObj.setIs_in_payment_plan(hasPaymentPlan);
            remainingDebtAmt = paymentPlan == null ? debt.getAmount() : paymentPlan.getAmount_to_pay();
            paymentPlanId = paymentPlan == null ? null : paymentPlan.getId();

            //Calculate the remainging debt amount
            retObj.setRemaining_amount(this.calculateRemainingDebt(paymentPlanId, remainingDebtAmt, paymentLst));

            if (paymentPlan != null && retObj.getRemaining_amount() > 0D) {
                //Calculation the next payment due date
                retObj.setNext_payment_due_date(this.calculateNextPaymentDueDate(paymentPlanId, paymentPlan, paymentLst));
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "buildDebtOutput - " + e.getMessage(), e);
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

                //Rounding remaining amount to 3 decimal places.
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
            DateTimeFormatter dtf1 = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'[HH][:mm][:ss]'Z']")
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
                paymentDueDate = this.calcNextFrequencyDate(startDate, lastPaymentDate, paymentPlan.getInstallment_frequency());
            }

            retVal = paymentDueDate.format(dtf1);

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "calculateNextPaymentDueDate - " + e.getMessage(), e);
        }
        return retVal;
    }

    /**
     * This method calculates the next frequency date based on start date
     * (Payment Plan Start Date) and end date (Last Payment Date), and frequency
     * (e.g. WEEKLY = 1, BI WEEKLY = 2).
     *
     * @param startDt
     * @param endDt
     * @param frequency
     * @return
     */
    private LocalDateTime calcNextFrequencyDate(LocalDateTime startDt, LocalDateTime endDt, String frequency) {

        //Intialize to start date
        LocalDateTime retDate = startDt;

        try {

            int weeksToAdd = 0;
            if (frequency.equals(SystemConstants.FREQUENCY_WEEKLY)) {
                weeksToAdd = 1;
            } else if (frequency.equals(SystemConstants.FREQUENCY_BI_WEEKLY)) {
                weeksToAdd = 2;
            }

            if (weeksToAdd > 0) {
                //Loop through from the payment plan start date to the last payment date to calculate the next frequency date
                for (retDate = startDt; retDate.isBefore(endDt) || retDate.isEqual(endDt); retDate = retDate.plusWeeks(weeksToAdd));
            }

            //NOTE: Determine if the last payment was made earlier than the scheduled due date.
            //For example: For a BI WEEKLY payment schedule starting (2020-01-01), a payment was made on 2020-08-08 which
            //falls between 2020-07-29 and 2020-08-12 scheduled payment dates.  Therefore, I assume the payment was for the 2020-08-12 
            //scheduled payment due date.  Therefore, the next payment will fall to the 2020-08-26 scheduled due date. 
            if (endDt.isAfter(retDate.minusWeeks(weeksToAdd))) {
                //Add the next frequency date
                retDate = retDate.plusWeeks(weeksToAdd);
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "calcNextFrequencyDate - " + e.getMessage(), e);
        }
        return retDate;
    }

    /**
     * Main method to test the methods in this DebtsService class.
     *
     * @param args
     */
    public static void main(String args[]) {

        try {

            Integer debtId = 2;
            MessageOutput msgOut;
            ObjectMapper objMapper = new ObjectMapper();
            DebtsService ds = new DebtsService();
            List<DebtOutput> debtInfoLst = ds.retrieveAllDebts();
            String jsonStr = "";
            if (!debtInfoLst.isEmpty()) {

                jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtInfoLst);

                System.out.println(jsonStr);
            }

            System.out.println("\n---------------------------------------------------------");
            DebtOutput outObj = ds.retrieveSingleDebt(debtId);

            if (outObj.getId() != null) {
                jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outObj);
            } else {

                msgOut = new MessageOutput();
                msgOut.setStatus(MessageOutput.MessageStatus.ERROR);
                msgOut.setMessage("No data found for debt id: " + debtId);
                jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgOut);
            }
            System.out.println(jsonStr);

            /* NOTE: Uncomment to display the biweekly payment schedule
             DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'[HH][:mm][:ss]'Z']")
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
             
            LocalDateTime startDt = LocalDateTime.of(2020, Month.JANUARY, 01, 0, 0, 0);
            LocalDateTime endDt = LocalDateTime.of(2020, Month.DECEMBER, 31, 0, 0, 0);
            for (LocalDateTime ldt = startDt; ldt.isBefore(endDt) || ldt.isEqual(endDt); ldt = ldt.plusWeeks(2)) {
                System.out.println(dtf.format(ldt));
            }
             */
        } catch (Exception e) {
            Logger.getLogger(DebtsService.class.getName()).log(Level.SEVERE, "main - " + e.getMessage(), e);
        }
    }

}
