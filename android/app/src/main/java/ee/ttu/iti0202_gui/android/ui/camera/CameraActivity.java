package ee.ttu.iti0202_gui.android.ui.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ee.ttu.iti0202_gui.android.R;

/**
 * New implementation for camera activity.
 *
 * @author Priit Käärd
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "CameraActivity";

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    boolean isPreviewing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = findViewById(R.id.camera_preview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //noinspection deprecation
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button captureButton = findViewById(R.id.shoot_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    /*
    private class TakePictureTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        private TakePictureTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Saving image...");
            File pictureFile = getTempOutputFile();
            if (pictureFile == null) {
                Log.e(TAG, "onActivityResult: Error creating temp file.");
                return null;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "onPictureTaken: File not found.");
            } catch (IOException e) {
                Log.e(TAG, "onPictureTaken: Error accessing the file.");
            }
            return pictureFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            Log.d(TAG, "onPostExecute: Image saved.");

            if (path == null) {
                Toast.makeText(getApplicationContext(),
                        "Failed to save the picture", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
    */

    /**
     * Callback for Camera instance if picture is taken.
     * Depending on screen orientation, rotates the image and compresses it back to byte array.
     */
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getTempOutputFile();
            if (pictureFile == null) {
                Log.e(TAG, "onActivityResult: Error creating temp file.");
                Toast.makeText(getApplicationContext(),
                        "Check storage permissions", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, "onPictureTaken: File not found.");
            } catch (IOException e) {
                Log.e(TAG, "onPictureTaken: Error accessing the file.");
            }

            Intent intent = new Intent();
            intent.putExtra("path", pictureFile.getAbsolutePath());
            setResult(RESULT_OK,intent);
            finish();

            // new TakePictureTask(data).execute();
        }
    };

    /**
     * Method to get a File instance for temporary image storage.
     *
     * @return      File instance.
     */
    private File getTempOutputFile(){
        File mediaStorageDir = new File(getCacheDir(), "uploads");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "getOutputMediaFile: Failed to create directories");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());

        return new File(mediaStorageDir.getPath()
                + File.separator + "IMG_"+ timeStamp + ".jpg");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        changeOrientation(holder);
    }

    private void changeOrientation(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = camera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                parameters.setRotation(90);
                Log.d(TAG, "changeOrientation: Portrait");
            } else {
                parameters.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                parameters.setRotation(0);
                Log.d(TAG, "changeOrientation: Landscape");
            }
            parameters.set("rotation", 90);
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            Log.d(TAG, "surfaceCreated: Failed to set orientation");
            exception.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (isPreviewing) {
            camera.stopPreview();
            isPreviewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                // changeOrientation(holder);
                // if (camera == null) throw new IOException("Test");
                isPreviewing = true;
            } catch (IOException e) {
                camera.release();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isPreviewing && camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            isPreviewing = false;
        }
    }


}
