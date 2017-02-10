package com.test.zappos.ilovezappos.model;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.test.zappos.ilovezappos.R;

/**
 * Created by root on 9/2/17.
 */

public class Product {

    public Product() {
        brandName = "";
        thumbnailImageUrl = "";
        productId = "";
        originalPrice = "";
        styleId = "";
        colorId = "";
        price = "";
        percentOff = "";
        productUrl = "";
        productName = "";
    }

    private String brandName;

    private String thumbnailImageUrl;

    private String productId;

    private String originalPrice;

    private String styleId;

    private String colorId;

    private String price;

    private String percentOff;

    private String productUrl;

    private String productName;

    public String getBrandName() {
        return brandName;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getStyleId() {
        return styleId;
    }

    public String getColorId() {
        return colorId;
    }

    public String getPrice() {
        return price;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void populateProductFromJson(JsonObject productJson) {
        if(productJson != null && !productJson.isJsonNull()) {
            brandName = productJson.get("brandName").getAsString();
            thumbnailImageUrl = productJson.get("thumbnailImageUrl").getAsString();
            productId = productJson.get("productId").getAsString();
            originalPrice = productJson.get("originalPrice").getAsString();
            styleId = productJson.get("styleId").getAsString();
            colorId = productJson.get("colorId").getAsString();
            price = productJson.get("price").getAsString();
            percentOff = productJson.get("percentOff").getAsString();
            productUrl = productJson.get("productUrl").getAsString();
            productName = productJson.get("productName").getAsString();
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.no_product)
                .resize(200, 200)
                .into(view);
    }
}
