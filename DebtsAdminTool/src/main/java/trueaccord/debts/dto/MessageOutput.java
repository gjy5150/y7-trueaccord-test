package trueaccord.debts.dto;

/**
 * Helper class to store messages.
 * 
 * @author gjy5150
 */
public class MessageOutput {
    
    public enum MessageStatus {
        SUCCESS, INFO, WARNING, ERROR;
    }
    
    private MessageStatus status;
    private String message;
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    
}
