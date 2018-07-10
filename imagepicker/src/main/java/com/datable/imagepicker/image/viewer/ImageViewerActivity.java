package com.datable.imagepicker.image.viewer;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.datable.imagepicker.R;
import com.datable.imagepicker.image.viewer.adapter.ImageItemData;

import java.util.ArrayList;


public class ImageViewerActivity extends AppCompatActivity {

    public static final String IMAGE_URL_ARRAY = "imageUrlArray";
    public static final String IMAGE_POSITION = "imagePosition";

    private ImageViewerFragment imageViewerFragment;

    private ArrayList<ImageItemData> imageItemDatas;
    private ArrayList<String> imageUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_toolbar_base);

        if (null == imageViewerFragment)
            imageViewerFragment = new ImageViewerFragment();

        imageUrlList = getIntent().getStringArrayListExtra(IMAGE_URL_ARRAY);
        imageItemDatas = new ArrayList<>();

        for (int i = 0; i < imageUrlList.size(); i++) {
            ImageItemData imageItemData = new ImageItemData();
            imageItemData.setImagePath(imageUrlList.get(i));

            imageItemDatas.add(imageItemData);
        }

        imageViewerFragment.setImageItemDatas(imageItemDatas);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.frameLayout_container, imageViewerFragment).commitAllowingStateLoss();
    }

}
