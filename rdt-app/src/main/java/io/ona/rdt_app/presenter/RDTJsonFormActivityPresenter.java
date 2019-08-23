package io.ona.rdt_app.presenter;

import io.ona.rdt_app.contract.RDTJsonFormActivityContract;
import io.ona.rdt_app.fragment.RDTJsonFormFragment;

/**
 * Created by Vincent Karuri on 16/08/2019
 */
public class RDTJsonFormActivityPresenter implements RDTJsonFormActivityContract.Presenter {

    private RDTJsonFormActivityContract.View activity;

    public RDTJsonFormActivityPresenter(RDTJsonFormActivityContract.View activity) {
        this.activity = activity;
    }

    @Override
    public void onBackPress() {
        int currStep = RDTJsonFormFragment.getCurrentStep();
        // disable backpress for timer and rdt capture screens
        if (currStep != 13 && currStep != 14) {
            activity.onBackPress();
        }
    }
}
