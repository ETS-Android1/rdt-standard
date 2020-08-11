package io.ona.rdt.job;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.job.BaseJob;

import io.ona.rdt.application.RDTApplication;
import io.ona.rdt.sync.RDTSettingsSyncIntentService;

/**
 * Created by Vincent Karuri on 05/03/2020
 */
public class RDTSyncSettingsServiceJob extends BaseJob {

    public static final String TAG = "RDTSyncSettingsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Context applicationContext = RDTApplication.getInstance().getApplicationContext();
        Intent intent = new Intent(applicationContext, RDTSettingsSyncIntentService.class);
        applicationContext.startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
