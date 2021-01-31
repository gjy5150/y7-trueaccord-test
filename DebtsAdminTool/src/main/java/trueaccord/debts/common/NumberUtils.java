package trueaccord.debts.common;

/**
 * Helper class for numbers
 * 
 * @author gjy5150
 */
public class NumberUtils {
    
    /** 
     * Determines if a string is an integer.
     * @param str
     * @return boolean
     */
    public static boolean isInteger(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }
}
