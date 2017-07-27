package eu.transkribus.languageresources.exceptions;

/**
 *
 * @author jnphilipp
 */
public class ARPAParseException extends Exception {
    public ARPAParseException() {
        super();
    }

    public ARPAParseException(String message) {
        super(message);
    }

    public ARPAParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
