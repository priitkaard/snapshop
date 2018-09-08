package ee.ttu.iti0202_gui.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to use MD5 hashing function..
 *
 * @author Priit Käärd
 */
public class MD5 {
    /**
     * Function to hash any given string with MD5 digest.
     *
     * @param input     Input String.
     * @return          Hashed string.
     */
    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte inputDigest[] = digest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : inputDigest) {
                sb.append(Integer.toHexString(0xFF & b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
