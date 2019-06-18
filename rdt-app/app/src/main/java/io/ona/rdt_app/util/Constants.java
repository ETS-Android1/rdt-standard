package io.ona.rdt_app.util;

/**
 * Created by Vincent Karuri on 23/05/2019
 */
public interface Constants {
    int REQUEST_STORAGE_PERMISSION = 1000;
    int REQUEST_CODE_GET_JSON = 9388;

    String JSON_FORM_PARAM_JSON = "json";
    String METADATA = "metadata";
    String DETAILS = "details";
    String ENCOUNTER_TYPE = "encounter_type";
    String PATIENTS = "patients";

    interface DBConstants {
        String NAME = "name";
        String AGE = "age";
        String SEX = "sex";
    }
}
