package ee.ttu.iti0202_gui.android.ui.splash;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.models.Basket;
import ee.ttu.iti0202_gui.android.ui.home.HomeActivity;

/**
 * Splash activity class.
 *
 * @author Priit Käärd
 */
public class SplashActivity extends AppCompatActivity implements ISplashActivity {
    private static final String TAG = "SplashActivity";

    private VideoView bgVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupVideoView();

        // First fragment transaction
        doFragmentTransaction(new SplashLoadingFragment(),
                getString(R.string.tag_splash_loading), null, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumeVideoView();
    }

    /**
     * Method to set up Background video view.
     */
    private void setupVideoView() {
        Log.d(TAG, "setupVideoView: Settings up...");
        bgVideoView = findViewById(R.id.splash_videoview);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bgvideo);
        bgVideoView.setVideoURI(videoUri);
        
        Log.d(TAG, "setupVideoView: Set up.");
    }

    /**
     * Method to resume the Background video.
     */
    private void resumeVideoView() {
        Log.d(TAG, "resumeVideoView: Resuming video...");
        bgVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.setLooping(true);
            }
        });
        bgVideoView.start();
        Log.d(TAG, "resumeVideoView: Video resumed.");
    }

    /**
     * Interface method to allow fragments do transactions.
     *
     * @param fragment              New fragment.
     * @param tag                   Fragment tag.
     * @param addToBackStack        If current fragment should stay in stack.
     */
    @Override
    public void doFragmentTransaction(Fragment fragment, String tag, Bundle arguments,
                                      boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(arguments);
        transaction.replace(R.id.splash_container, fragment, tag);

        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Interface method to allow fragments to start home activity.
     */
    @Override
    public void handleLogin() {
        // Clear basket
        Basket.getInstance().getContent().clear();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
