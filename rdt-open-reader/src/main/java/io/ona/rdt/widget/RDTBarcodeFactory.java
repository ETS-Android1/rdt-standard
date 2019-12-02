package io.ona.rdt.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.barcode.Barcode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.widgets.BarcodeFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.ona.rdt.fragment.RDTJsonFormFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.vijay.jsonwizard.constants.JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static io.ona.rdt.util.Constants.EXPIRED_PAGE_ADDRESS;
import static io.ona.rdt.util.Utils.convertDate;

/**
 * Created by Vincent Karuri on 19/06/2019
 */
public class RDTBarcodeFactory extends BarcodeFactory implements OnActivityResultListener {

    private WidgetArgs widgetArgs;

    private static final String TAG = RDTBarcodeFactory.class.getName();
    public static final String RDT_ID_LBL_ADDRESSES = "rdt_id_lbl_addresses";
    public static final String EXPIRATION_DATE_ADDRESS = "expiration_date_address";
    public static final String RDT_ID_ADDRESS = "rdt_id_address";

    public static final String OPEN_RDT_DATE_FORMAT = "ddMMyy";

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        return getViewsFromJson(stepName, context, formFragment, jsonObject, listener, false);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       JsonFormFragment formFragment, final JSONObject jsonObject,
                                       CommonListener listener, boolean popup) {

        widgetArgs = new WidgetArgs();
        widgetArgs.withContext(context)
                .withJsonObject(jsonObject)
                .withFormFragment(formFragment)
                .withStepName(stepName);

        List<View> views = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);

        RelativeLayout rootLayout = views == null ? null : (RelativeLayout) views.get(0);

        hideAndClickScanButton(rootLayout);

        return views;
    }

    private void hideAndClickScanButton(RelativeLayout rootLayout) {
        if (rootLayout != null) {
            Button scanButton = rootLayout.findViewById(com.vijay.jsonwizard.R.id.scan_button);
            scanButton.setVisibility(View.GONE);
            scanButton.performClick();
        }
    }

    @Override
    protected void addOnBarCodeResultListeners(final Context context, final MaterialEditText editText) {
        editText.setVisibility(View.GONE);
        if (context instanceof JsonApi) {
            ((JsonApi) context).addOnActivityResultListener(BARCODE_REQUEST_CODE, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final JsonApi jsonApi = (JsonApi) widgetArgs.getContext();
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Barcode barcode = data.getParcelableExtra(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_KEY);
                Log.d("Scanned QR Code", barcode.displayValue);
                String[] barcodeValues = barcode.displayValue.split(",");

                Date expDate = null;
                if (barcodeValues.length >= 2) {
                    expDate = convertDate(barcodeValues[1].trim(), OPEN_RDT_DATE_FORMAT);
                    populateRelevantFields(barcodeValues, jsonApi, expDate);
                }
                moveToNextStep(expDate);
            } catch (JSONException e) {
                Log.e(TAG, e.getStackTrace().toString());
            } catch (ParseException e) {
                Log.e(TAG, e.getStackTrace().toString());
            }
        } else if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            ((RDTJsonFormFragment) widgetArgs.getFormFragment()).setMoveBackOneStep(true);
        } else if (data == null) {
            Log.i("", "No result for qr code");
        }
    }

    private void populateRelevantFields(String[] barcodeValues, JsonApi jsonApi, Date expDate) throws JSONException {
        String idAndExpDate = barcodeValues[0] + "," + barcodeValues[1];
        JSONObject jsonObject = widgetArgs.getJsonObject();
        jsonObject.put(VALUE, idAndExpDate);

        // write barcode values to relevant widgets
        String rdtIdLblAddresses = jsonObject.optString(RDT_ID_LBL_ADDRESSES, "");
        String expirationDateAddress = jsonObject.optString(EXPIRATION_DATE_ADDRESS, "");
        String rdtIdAddress = jsonObject.optString(RDT_ID_ADDRESS, "");
        String[] stepAndId;

        // populate rdt id to all relevant txt labels
        String[] rdtIdAddresses = rdtIdLblAddresses.isEmpty() ? new String[0] : rdtIdLblAddresses.split(",");
        for (String addr : rdtIdAddresses) {
            stepAndId = addr.isEmpty() ? new String[0] : addr.split(":");
            if (stepAndId.length == 2) {
                jsonApi.writeValue(stepAndId[0].trim(), stepAndId[1].trim(), "RDT ID: " + barcodeValues[2].trim(), "", "", "", false);
            }
        }

        // write rdt id to hidden rdt id field
        stepAndId = rdtIdAddress.isEmpty() ? new String[0] : rdtIdAddress.split(":");
        if (stepAndId.length == 2) {
            jsonApi.writeValue(stepAndId[0].trim(), stepAndId[1].trim(), barcodeValues[2].trim(), "", "", "", false);
        }

        // populate exp. date to expiration date widget value
        stepAndId = expirationDateAddress.isEmpty() ? new String[0] : expirationDateAddress.split(":");
        if (stepAndId.length == 2) {
            jsonApi.writeValue(stepAndId[0].trim(), stepAndId[1].trim(), getDateStr(expDate), "", "", "", false);
        }
    }

    private void moveToNextStep(Date expDate) {
        JsonFormFragment formFragment = widgetArgs.getFormFragment();
        if (!isRDTExpired(expDate)) {
            if (!formFragment.next()) {
                formFragment.save(true);
            }
        } else {
            String expiredPageAddr = widgetArgs.getJsonObject().optString(EXPIRED_PAGE_ADDRESS, "step1");
            JsonFormFragment nextFragment = RDTJsonFormFragment.getFormFragment(expiredPageAddr);
            formFragment.transactThis(nextFragment);
        }
    }

    private String getDateStr(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return date == null ? "" : simpleDateFormat.format(date);
    }

    private boolean isRDTExpired(Date date) {
        return date == null ? true : new Date().after(date);
    }
}