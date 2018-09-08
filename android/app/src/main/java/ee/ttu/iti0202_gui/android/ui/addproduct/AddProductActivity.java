package ee.ttu.iti0202_gui.android.ui.addproduct;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ee.ttu.iti0202_gui.android.R;
import ee.ttu.iti0202_gui.android.api.API;
import ee.ttu.iti0202_gui.android.async.AddProductTask;
import ee.ttu.iti0202_gui.android.async.ImageUploadTask;
import ee.ttu.iti0202_gui.android.callback.TaskCompletedCallback;
import ee.ttu.iti0202_gui.android.models.Category;
import ee.ttu.iti0202_gui.android.models.Product;
import ee.ttu.iti0202_gui.android.models.ProductBuilder;
import ee.ttu.iti0202_gui.android.ui.camera.CameraActivity;
import ee.ttu.iti0202_gui.android.ui.component.ImageSliderPager;
import ee.ttu.iti0202_gui.android.utils.ImageHelper;
import ee.ttu.iti0202_gui.android.utils.LocalDBHelper;
import ee.ttu.iti0202_gui.android.utils.Session;

import static android.view.View.GONE;

/**
 * UI Activity class for adding product.
 *
 * @author Priit Käärd
 */
public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    private static final int REQUEST_CODE = 1;

    private Session session = Session.getSession();
    private ImageSliderPager galleryPager;
    private Category category = null;
    private List<String> images = new ArrayList<>();
    private int imagesUploaded = 0;

    // Widgets
    private EditText title;
    private EditText price;
    private EditText description;
    private EditText location;
    private Spinner parentCategory;
    private Spinner subCategory;
    private TextView errorMessage;
    private Button publish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Widgets
        galleryPager = findViewById(R.id.view_pager);
        title = findViewById(R.id.title_view);
        price = findViewById(R.id.price_view);
        description = findViewById(R.id.description_view);
        location = findViewById(R.id.location_view);
        parentCategory = findViewById(R.id.category);
        subCategory = findViewById(R.id.subcategory);
        publish = findViewById(R.id.publish_button);
        errorMessage = findViewById(R.id.error_message);

        // On publish button clicked
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInput(false);
                // TODO: Validate input
                Product product = new ProductBuilder()
                        .setTitle(title.getText().toString())
                        .setPrice(BigDecimal.valueOf(Double.parseDouble(price.getText().toString())))
                        .setDescription(description.getText().toString())
                        .setActivated(true)
                        .setLocation(location.getText().toString())
                        .setCategory(category)
                        .createProduct();

                createProductAddTask(product);
            }
        });

        findViewById(R.id.add_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        setupCategories();

        // Set up gallery
        galleryPager.setAdapter(new ImageViewPagerAdapter());
        galleryPager.setOffscreenPageLimit(10);
        galleryPager.setCurrentItem(0);

        // Go to camera activity if no images.
        if (images.isEmpty() && savedInstanceState == null) {
            Log.d(TAG, "onCreate: Images empty");
            startCamera();
            return;
        }

        title.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("images", new ArrayList<>(images));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        images = savedInstanceState.getStringArrayList("images");
        if (galleryPager.getAdapter() != null) {
            galleryPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: Loading for data");
        if (resultCode == RESULT_OK) {
            images.add(data.getStringExtra("path"));
            if (galleryPager.getAdapter() != null) {
                galleryPager.getAdapter().notifyDataSetChanged();
            }
        }
        /*
        if (data == null) {
            if (images.isEmpty()) {
                Log.e(TAG, "onActivityResult: Passed data and image list empty.");
                finish();
            }
            Log.e(TAG, "onActivityResult: Passed data is empty");
            return;
        }

        Bundle extras = data.getExtras();
        if (extras != null) {
            String imagePath = extras.getString("path");
            if (imagePath == null) {
                Log.e(TAG, "onActivityResult: Invalid data passed");
                return;
            }

            images.add(imagePath);
            if (galleryPager.getAdapter() != null) {
                galleryPager.getAdapter().notifyDataSetChanged();
            }
        }*/
    }


    /**
     * Helper method to create an AddProduct Async task and execute it.
     *
     * @param product       New Product instance.
     */
    private void createProductAddTask(Product product) {
        new AddProductTask(API.getInstance(session.getCredentials()), product,
                new TaskCompletedCallback<Product>() {
            @Override
            public void onSuccess(Product object) {
                imagesUploaded = 0;
                Toast.makeText(getApplicationContext(),
                        "Success. Uploading images...", Toast.LENGTH_SHORT).show();

                for (String path : images) {
                    new ImageUploadTask(API.getInstance(session.getCredentials()),
                            object, new File(path), new TaskCompletedCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean object) {
                            imagesUploaded++;
                            if (!object) {
                                errorMessage.setText(R.string.error_occurred_uploading_image);
                            } else {
                                if (imagesUploaded == images.size()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Images uploaded.", Toast.LENGTH_SHORT)
                                            .show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e(TAG, "onFailure: " + message);
                            errorMessage.setText(String.format("%s%s",
                                    getString(R.string.could_not_upload_an_image), message));
                            setInput(true);
                        }
                    }).execute();
                }
            }

            @Override
            public void onFailure(String message) {
                setInput(true);
                errorMessage.setText(message);
            }
        }).execute();
    }

    /**
     * Helper to set up categories expandable list view.
     */
    private void setupCategories() {
        final List<Category> categories = new LocalDBHelper(AddProductActivity.this)
                .getCategories();
        List<Category> parents = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentCategory() == null) {
                parents.add(category);
            }
        }
        parentCategory.setAdapter(new CategorySpinnerAdapter(this, parents));

        parentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) parent.getItemAtPosition(position);

                if (category.getSubCategories().isEmpty()) {
                    subCategory.setAdapter(null);
                    subCategory.setVisibility(GONE);
                } else {
                    subCategory.setAdapter(new CategorySpinnerAdapter(AddProductActivity.this,
                            category.getSubCategories()));
                    subCategory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subCategory.setAdapter(null);
                subCategory.setVisibility(GONE);
            }
        });

        subCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });
    }

    /**
     * Method to enable or disable user input.
     *
     * @param enable        Enabled or disabled.
     */
    private void setInput(boolean enable) {
        title.setEnabled(enable);
        description.setEnabled(enable);
        price.setEnabled(enable);
        location.setEnabled(enable);

        publish.setEnabled(enable);
    }

    /**
     * Helper method to start the camera.
     */
    private void startCamera() {
        startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE);
    }

    /**
     * Adapter class for image gallery.
     */
    public class ImageViewPagerAdapter extends PagerAdapter {
        private int currentPosition = -1;

        ImageViewPagerAdapter() { }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View itemView = LayoutInflater.from(AddProductActivity.this)
                    .inflate(R.layout.home_product_gallery_item, container, false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            final ImageView imageView = itemView.findViewById(R.id.product_gallery_image_view);
            container.addView(itemView);


            File imageFile = new File(images.get(position));
            Log.d(TAG, "instantiateItem: " + imageFile.getAbsolutePath());
            byte[] data = new byte[(int) imageFile.length()];

            try {
                FileInputStream fileInputStream = new FileInputStream(imageFile);
                fileInputStream.read(data);
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not load image",
                        Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "instantiateItem: ImageView sizes:");
            Log.d(TAG, "instantiateItem: Width: " + imageView.getWidth());
            Log.d(TAG, "instantiateItem: Height:" + imageView.getHeight());
            imageView.setImageBitmap(ImageHelper.decodeSampledBitmapFromByteArray(data,
                    ImageHelper.getDeviceScreenWidth(AddProductActivity.this), 1));

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position,
                                   @NonNull Object object) {

            super.setPrimaryItem(container, position, object);

            if (position != currentPosition) {
                ImageSliderPager pager = (ImageSliderPager) container;
                ImageView imageView = ((View) object).findViewById(R.id.product_gallery_image_view);
                if (imageView == null) return;

                currentPosition = position;
                pager.measureCurrentView(imageView);
            }
        }
    }
}
