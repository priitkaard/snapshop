package ee.ttu.iti0202_gui.android.config;

import java.util.regex.Pattern;

/**
 * Configuration class.
 *
 * @author Priit Käärd
 */
public class Config {
    public static final int MIN_USERNAME_LENGTH = 6;
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]{6,}$";
    public static final String USERNAME_REGEX_READABLE = "A-Z, a-z, 0-9, '.', '_', '-'";

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;

    public static final Pattern EMAIL_REGEX = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final int FRONT_PAGE_PRODUCTS_AMOUNT = 20;
}
