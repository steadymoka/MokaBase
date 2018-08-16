package io.moka.lib.imagepicker.image.viewer;


import android.os.Bundle;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import io.moka.lib.imagepicker.R;
import io.moka.lib.imagepicker.image.viewer.adapter.ImageItemData;


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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_container, imageViewerFragment)
                .commitAllowingStateLoss();
    }

}
