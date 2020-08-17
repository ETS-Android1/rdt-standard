package io.ona.rdt.robolectric.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ReflectionHelpers;

import androidx.fragment.app.testing.FragmentScenario;
import io.ona.rdt.R;
import io.ona.rdt.fragment.RDTJsonFormFragment;
import io.ona.rdt.presenter.RDTJsonFormFragmentPresenter;
import timber.log.Timber;

import static io.ona.rdt.util.Constants.Encounter.RDT_TEST;
import static io.ona.rdt.util.Constants.FormFields.ENCOUNTER_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Vincent Karuri on 24/07/2020
 */
public class RDTJsonFormFragmentTest extends FragmentRobolectricTest {

    private FragmentScenario<RDTJsonFormFragment> fragmentScenario;
    private RDTJsonFormFragment jsonFormFragment;
    private String PRESENTER_FIELD = "presenter";
    private String STEP_3 = "step3";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragmentScenario =
                FragmentScenario.launchInContainer(RDTJsonFormFragment.class,
                        null, R.style.AppTheme, null);
        fragmentScenario.onFragment(fragment -> {
            jsonFormFragment = fragment;
            Whitebox.setInternalState(jsonFormFragment, PRESENTER_FIELD, mock(RDTJsonFormFragmentPresenter.class));
        });
    }

    @Test
    public void testFragmentCreationShouldInitializeFragmentState() {
        assertNotNull(jsonFormFragment.getRootLayout());
    }

    @Test
    public void testGetFormFragmentShouldSetStepAndReturnValidJsonFormFragment() {
        jsonFormFragment.setCurrentStep(2);
        RDTJsonFormFragment formFragment = (RDTJsonFormFragment) jsonFormFragment.getFormFragment(STEP_3);
        assertNotNull(formFragment);
        assertEquals(STEP_3, formFragment.getArguments().getString("stepName"));
        final int currStep = 3;
        final int prevStep = 2;
        assertEquals(currStep, jsonFormFragment.getCurrentStep());
        assertEquals(prevStep, (int) ReflectionHelpers.getField(jsonFormFragment, "prevStep"));
    }

    @Test
    public void testFormHasSpecialNavigationRulesShouldReturnCorrectStatus() {
        String methodName = "formHasSpecialNavigationRules";
        assertTrue(ReflectionHelpers.callInstanceMethod(jsonFormFragment,
                methodName,
                ReflectionHelpers.ClassParameter.from(String.class, RDT_TEST)));
        assertFalse(ReflectionHelpers.callInstanceMethod(jsonFormFragment,
                methodName,
                ReflectionHelpers.ClassParameter.from(String.class, "form")));
    }

    @Test
    public void testSetNextButtonStateShouldSetCorrectState() {
        View view = mock(View.class);
        doReturn(mock(GradientDrawable.class)).when(view).getBackground();

        jsonFormFragment.setNextButtonState(view, false);
        verify(view).setEnabled(eq(false));
        verify((GradientDrawable) view.getBackground()).setColor(eq(Color.parseColor("#D1D1D1")));

        jsonFormFragment.setNextButtonState(view, true);
        verify(view).setEnabled(eq(true));
        verify((GradientDrawable) view.getBackground()).setColor(eq(Color.parseColor("#0192D4")));
    }

    @Test
    public void testNavigateToNextStepShouldNavigateToNextStep() {
        JsonFormFragmentPresenter presenter = mock(JsonFormFragmentPresenter.class);
        ReflectionHelpers.setField(jsonFormFragment, PRESENTER_FIELD, presenter);
        jsonFormFragment.navigateToNextStep();
        verify(presenter).onNextClick(any());
    }

    @Test
    public void testSaveFormShouldSaveForm() {
        JsonFormFragmentPresenter presenter = mock(JsonFormFragmentPresenter.class);
        ReflectionHelpers.setField(jsonFormFragment, PRESENTER_FIELD, presenter);
        jsonFormFragment.saveForm();
        verify(presenter).onSaveClick(any());
    }

    @Test
    public void testbBackClickShouldShowConfirmationDialog() {
        jsonFormFragment.backClick();
        assertNotNull(ShadowAlertDialog.getLatestDialog());
        assertTrue(ShadowAlertDialog.getLatestDialog() instanceof AlertDialog);
    }

    @Test
    public void testNavigationShouldNavigateToCorrectStep() {
        ReflectionHelpers.callInstanceMethod(jsonFormFragment, "initializeBottomNavigation",
                ReflectionHelpers.ClassParameter.from(JSONObject.class, new JSONObject()),
                ReflectionHelpers.ClassParameter.from(View.class, jsonFormFragment.getRootLayout()));
        RDTJsonFormFragmentPresenter presenter = mock(RDTJsonFormFragmentPresenter.class);

        ReflectionHelpers.setField(jsonFormFragment, PRESENTER_FIELD, presenter);
        jsonFormFragment.getRootLayout().findViewById(com.vijay.jsonwizard.R.id.previous_button).performClick();
        verify(presenter).onSaveClick(any());

        JsonApi jsonApi = mock(JsonApi.class);
        JSONObject mJsonObject = new JSONObject();
        doReturn(mJsonObject).when(jsonApi).getmJSONObject();
        ReflectionHelpers.setField(jsonFormFragment, "mJsonApi", jsonApi);
        jsonFormFragment.getRootLayout().findViewById(com.vijay.jsonwizard.R.id.next_button).performClick();
        verify(jsonFormFragment.getFragmentPresenter()).submitOrMoveToNextStep(any());

        try {
            mJsonObject.put(ENCOUNTER_TYPE, RDT_TEST);
            jsonFormFragment.getRootLayout().findViewById(com.vijay.jsonwizard.R.id.next_button).performClick();
            verify(jsonFormFragment.getFragmentPresenter()).performNextButtonAction(anyString(), any());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Test
    public void testGetPresenterShouldNotBeNull() {
        Whitebox.setInternalState(jsonFormFragment, PRESENTER_FIELD, (RDTJsonFormFragmentPresenter) null);
        ReflectionHelpers.callInstanceMethod(jsonFormFragment, "createPresenter");
        Assert.assertNotNull(jsonFormFragment.getFragmentPresenter());
    }

    @Override
    public FragmentScenario getFragmentScenario() {
        return fragmentScenario;
    }
}