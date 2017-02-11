package com.test.zappos.ilovezappos.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.test.zappos.ilovezappos.R;
import com.test.zappos.ilovezappos.common.Constants;
import com.test.zappos.ilovezappos.common.CustomBounceInterpolator;
import com.test.zappos.ilovezappos.databinding.ActivityProductBinding;
import com.test.zappos.ilovezappos.http.reponse.ProductSearchResponse;
import com.test.zappos.ilovezappos.model.Product;

/**
 * Created by Parag Dakle.
 */

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean addedToCard = false;
    private boolean discountedPriceShown = false;
    ActivityProductBinding binding;
    Snackbar snackbar;
    private ProductSearchResponse response;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        binding.toolbar.setTitle(R.string.app_name);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.SEARCH_RESULTS_KEY)) {
            response = savedInstanceState.getParcelable(Constants.SEARCH_RESULTS_KEY);
        }
        else {
            response = getIntent().getParcelableExtra(Constants.SEARCH_RESULTS_KEY);
        }

        product = new Product();
        binding.mainContent.setProduct(product);
        initializeProduct();

        binding.btnAddToCart.setOnClickListener(this);
        binding.mainContent.productDiscountedPrice.setOnClickListener(this);
    }

    /*
     * Loads the product from search response object in the product object.
     */
    private void initializeProduct() {
        if(response != null && response.results != null && response.results.size() > 0) {
            product.populateProductFromJson(response.results.get(0).getAsJsonObject());
        }
        else {
            showMessage(getString(R.string.product_load_failed_error_message));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddToCart:
                if(!addedToCard) {
                    animate(v);
                    binding.btnAddToCart.setImageResource(R.drawable.ic_added_to_cart);
                    showMessage(getString(R.string.product_added_to_cart_message));
                    addedToCard = true;
                }
                break;
            case R.id.productDiscountedPrice:
                if(!discountedPriceShown) {
                    showDiscountedPrice();
                    discountedPriceShown = true;
                }
                break;
        }
    }

    /*
     * Method shows the bounce animation for the addToCart FAB.
     */
    private void animate(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(ProductActivity.this, R.anim.fab_bounce_anim);
        CustomBounceInterpolator interpolator = new CustomBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }

    /*
     * Method shows the discounted price on user tap. Uses simple animation to reveal the cost.
     */
    private void showDiscountedPrice() {
        final Animation out = new AlphaAnimation(1.0f, 0.1f);
        out.setDuration(300);

        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(product.getPercentOff().equalsIgnoreCase("0%")) {
                    binding.mainContent.productDiscountedPrice.setText(getResources().getString(R.string.no_discount_price_string));
                }
                else {
                    binding.mainContent.productDiscountedPrice.setText(String.format(getString(R.string.discount_price_string), product.getPrice(), product.getPercentOff()));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.mainContent.productDiscountedPrice.startAnimation(out);
    }

    /* Function displays a message in a Snackbar.
     * @param message The message to display
     *
     * @return void
     */
    private void showMessage(String message) {
        if(message != null && !message.isEmpty()) {
            snackbar = Snackbar.make(binding.parentCoordinatorLayout, message, BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        }
    }

    /*
     * Method to save the necessary data in the save
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.SEARCH_RESULTS_KEY, response);
        super.onSaveInstanceState(outState);
    }
}
