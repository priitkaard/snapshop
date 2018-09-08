package ee.ttu.iti0202_gui.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Helper class for image formatting.
 *
 * @author Priit Käärd
 */
public class ImageHelper {
    /**
     * Method get current device screen width.
     *
     * @param context       Application context.
     * @return              Screen width in pixels.
     */
    public static int getDeviceScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * Method get full width resized bitmap.
     *
     * @param context       Application context.
     * @param bitmap        Bitmap object.
     * @return              Resized bitmap.
     */
    public static Bitmap getFullWidthBitmap(Context context, Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int neededWidth = getDeviceScreenWidth(context);
        int neededHeight = ((originalHeight * neededWidth) / originalWidth);
        Log.d("ImageHelper", "getFullWidthBitmap: " + neededWidth + " x " + neededHeight);
        return Bitmap.createScaledBitmap(bitmap, neededWidth, neededHeight, false);
    }

    /**
     * Method to calculate needed sample size for decoded bitmap.
     *
     * @param options           Original bitmap options.
     * @param reqWidth          Requested width of bitmap.
     * @param reqHeight         Requested height of bitmap.
     * @return                  Sample size for requested sizes.
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Get raw bitmap data from Options.
        final int rawHeight = options.outHeight;
        final int rawWidth = options.outWidth;
        int inSampleSize = 1;

        // If current bitmap size is bigger than requested size.
        if (rawHeight > reqHeight || rawWidth > reqWidth) {
            final int halfHeight = rawHeight / 2;
            final int halfWidth = rawWidth / 2;

            // Increase the sample size until bitmap size in as close as possible to
            // requested sizes.
            try {
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }
        }

        return inSampleSize;
    }

    /**
     * Method to decode byte array to bitmap using sampled size.
     *
     * @param data              Byte array of an image.
     * @param reqWidth          Requested width.
     * @param reqHeight         Requested height.
     * @return                  Decoded and sampled bitmap.
     */
    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        // Check for image dimensions.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize.
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode sampled bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Method rotate an input bitmap.
     *
     * @param image     Bitmap instance.
     * @param angle     Angle - how much to rotate.
     * @return          Rotated bitmap.
     */
    public static Bitmap rotateImage(Bitmap image, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(),
                matrix, true);
    }
}
