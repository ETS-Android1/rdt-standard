package io.ona.rdt.callback;

import android.view.View;

import com.vijay.jsonwizard.domain.WidgetArgs;

import org.json.JSONException;
import org.json.JSONObject;

import io.ona.rdt.application.RDTApplication;
import io.ona.rdt.fragment.RDTJsonFormFragment;
import io.ona.rdt.presenter.RDTJsonFormFragmentPresenter;
import io.ona.rdt.util.Constants;
import io.ona.rdt.util.StepStateConfig;
import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static io.ona.rdt.util.Constants.Step.SCAN_BARCODE_PAGE;
import static io.ona.rdt.util.Constants.Step.SCAN_CARESTART_PAGE;
import static io.ona.rdt.util.Constants.Step.SCAN_QR_PAGE;

/**
 * Created by Vincent Karuri on 27/11/2019
 */
public class OnLabelClickedListener implements View.OnClickListener {

    private WidgetArgs widgetArgs;

    public OnLabelClickedListener(WidgetArgs widgetArgs) {
        this.widgetArgs = widgetArgs;
    }

    @Override
    public void onClick(View v) {
        try {
            JSONObject jsonObject = widgetArgs.getJsonObject();
            RDTJsonFormFragment formFragment = (RDTJsonFormFragment) widgetArgs.getFormFragment();
            final String key = jsonObject.optString(KEY, "");
            StepStateConfig stepStateConfig = RDTApplication.getInstance().getStepStateConfiguration();

            String nextStep = "";
            if (Constants.FormFields.LBL_CARE_START.equals(key)) {
                formFragment.getRdtActivity().setRdtType(Constants.RDTType.CARESTART_RDT);
                nextStep = stepStateConfig.getStepStateObj().optString(SCAN_CARESTART_PAGE);
            } else if (Constants.FormFields.LBL_SCAN_QR_CODE.equals(key)) {
                formFragment.getRdtActivity().setRdtType(Constants.RDTType.ONA_RDT);
                nextStep = stepStateConfig.getStepStateObj().optString(SCAN_QR_PAGE);
            } else if (Constants.FormFields.LBL_SCAN_BARCODE.equals(key)) {
                nextStep = stepStateConfig.getStepStateObj().optString(SCAN_BARCODE_PAGE);
            } else if (Constants.FormFields.LBL_ENTER_RDT_MANUALLY.equals(key)) {
                // todo: add next step
            }
            ((RDTJsonFormFragmentPresenter) formFragment.getPresenter()).moveToNextStep(nextStep);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}
