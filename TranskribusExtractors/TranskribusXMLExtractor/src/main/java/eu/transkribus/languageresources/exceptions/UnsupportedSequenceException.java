package eu.transkribus.languageresources.exceptions;

/**
 *
 * @author jnphilipp
 */
public class UnsupportedSequenceException extends Exception {
    public UnsupportedSequenceException() {
        super();
    }

    public UnsupportedSequenceException(String message) {
        super(message);
    }

    public UnsupportedSequenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
