package ee.ttu.iti0202_gui.android.ui.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Custom View pager class for product image slider.
 * 
 * @author Priit Käärd
 */
public class ImageSliderPager extends ViewPager {
    private static final String TAG = "ImageSliderPager";

    private View currentView;
    private boolean animStarted = false;

    public ImageSliderPager(@NonNull Context context) {
        super(context);
    }

    public ImageSliderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: Called");

        if(!animStarted && currentView != null) {
            currentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int height = currentView.getMeasuredHeight();
            if (height < getMinimumHeight()) {
                height = getMinimumHeight();
            }

            // Animation for different image sizes
            int newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            if (getLayoutParams().height != 0 && heightMeasureSpec != newHeight) {
                final int targetHeight = height;
                final int currentHeight = getLayoutParams().height;
                final int heightChange = targetHeight - currentHeight;

                getLayoutParams().height = targetHeight;

                Animation a = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        if (interpolatedTime >= 1) {
                            getLayoutParams().height = targetHeight;
                        } else {
                            int stepHeight = (int) (heightChange * interpolatedTime);
                            getLayoutParams().height = currentHeight + stepHeight;
                        }
                        requestLayout();
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };

                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Log.d(TAG, "onAnimationStart: Started");
                        animStarted = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Log.d(TAG, "onAnimationEnd: Ended");
                        // animStarted = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                a.setDuration(300);
                startAnimation(a);
                animStarted = true;
            } else {
                heightMeasureSpec = newHeight;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void measureCurrentView(View currentView) {
        this.currentView = currentView;
        requestLayout();
    }
}
