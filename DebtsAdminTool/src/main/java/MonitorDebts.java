
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import trueaccord.debts.business.DebtsService;
import trueaccord.debts.common.NumberUtils;
import trueaccord.debts.dto.DebtOutput;
import trueaccord.debts.dto.MessageOutput;

/**
 * This class acts as the main entry point to retrieve all debts or retrieve a
 * single debt based on an id passed in as arguments to the main class.
 *
 *
 * @author gjy5150
 */
public class MonitorDebts {

    /**
     * Driver method to produce the debts output.
     * 
     * @param args 
     */
    public static void main(String args[]) {

        String jsonStr = "";
        ObjectMapper objMapper = new ObjectMapper();

        MessageOutput.MessageStatus status = MessageOutput.MessageStatus.SUCCESS;
        String statusMsg = "";

        try {

            DebtsService ds = new DebtsService();

            if (args.length == 0) {
                
                //No arguments produces all debts output
                List<DebtOutput> debtInfoLst = ds.retrieveAllDebts();

                if (!debtInfoLst.isEmpty()) {
                    jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(debtInfoLst);
                } else {
                    status = MessageOutput.MessageStatus.ERROR;
                    statusMsg = "No debt data found.";
                }

            } else if (args.length == 1 && NumberUtils.isInteger(args[0])) {
                //Output single debt
                DebtOutput outObj = ds.retrieveSingleDebt(Integer.valueOf(args[0]));

                if (outObj.getId() != null) {
                    jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(outObj);
                } else {
                    status = MessageOutput.MessageStatus.ERROR;
                    statusMsg = "No data found for debt id: " + args[0];
                }

            } else {
                status = MessageOutput.MessageStatus.ERROR;
                statusMsg = "Invalid number of arguments.";
            }

        } catch (Exception e) {
            status = MessageOutput.MessageStatus.ERROR;
            statusMsg = e.getMessage();
        } finally {

            if (status != MessageOutput.MessageStatus.SUCCESS) {
                MessageOutput msgOut = new MessageOutput();
                msgOut.setStatus(status);
                msgOut.setMessage(statusMsg);
                try {
                    jsonStr = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(msgOut);
                } catch (Exception ex) {
                    Logger.getLogger(MonitorDebts.class.getName()).log(Level.SEVERE, "main - " + ex.getMessage(), ex);
                }
            }

            System.out.println(jsonStr);
        }
    }
}
