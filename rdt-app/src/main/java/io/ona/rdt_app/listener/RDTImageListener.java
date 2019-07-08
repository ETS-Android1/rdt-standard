package io.ona.rdt_app.listener;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.android.volley.toolbox.ImageLoader;

import org.smartregister.util.OpenSRPImageListener;

import edu.washington.cs.ubicomplab.rdt_reader.callback.OnImageSavedCallBack;
import io.ona.rdt_app.BuildConfig;
import io.ona.rdt_app.application.RDTApplication;
import io.ona.rdt_app.util.RDTJsonFormUtils;

/**
 * Created by Vincent Karuri on 02/07/2019
 */
public class RDTImageListener extends OpenSRPImageListener {

    public RDTImageListener(ImageView imageView, int defaultImageResId, int errorImageResId) {
        super(imageView, defaultImageResId, errorImageResId);
    }

    public RDTImageListener(ImageView imageView, String entityId, int defaultImageResId, int errorImageResId) {
        super(imageView, entityId, defaultImageResId, errorImageResId);
    }

    public RDTImageListener(RemoteViews remoteView, int imageViewId, int defaultImageResId, int errorImageResId) {
        super(remoteView, imageViewId, defaultImageResId, errorImageResId);
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        Bitmap image = response.getBitmap();
        RDTApplication application = RDTApplication.getInstance();
        if (image != null) {
            RDTJsonFormUtils.saveStaticImageToDisk(application.getApplicationContext(), image, application.getContext().allSharedPreferences().fetchRegisteredANM(), BuildConfig.BASE_ENTITY_ID, new OnImageSavedCallBack() {
                @Override
                public void onImageSaved(String imageLocation) {
                    // do nothing
                }
            });
        }
    }
}
