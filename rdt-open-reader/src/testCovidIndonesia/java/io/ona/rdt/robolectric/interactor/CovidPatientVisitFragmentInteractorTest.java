package io.ona.rdt.robolectric.interactor;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;

import java.util.ArrayList;
import java.util.List;

import io.ona.rdt.domain.Visit;
import io.ona.rdt.interactor.CovidPatientVisitFragmentInteractor;
import io.ona.rdt.repository.PatientHistoryRepository;
import io.ona.rdt.robolectric.RobolectricTest;

public class CovidPatientVisitFragmentInteractorTest extends RobolectricTest {

    private CovidPatientVisitFragmentInteractor interactor;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        interactor = new CovidPatientVisitFragmentInteractor();
    }

    @Test
    public void testGetPatientVisits() {
        PatientHistoryRepository patientHistoryRepository = Mockito.mock(PatientHistoryRepository.class);
        ReflectionHelpers.setField(interactor, "patientHistoryRepository", patientHistoryRepository);

        int listSize = 1;
        List<EventClient> eventClientList = new ArrayList<>(listSize);
        Event event = new Event();
        event.setEventDate(DateTime.now());
        eventClientList.add(new EventClient(event));

        Mockito.when(patientHistoryRepository.getEventsByUniqueDate(ArgumentMatchers.anyString())).thenReturn(eventClientList);

        List<Visit> visitList = interactor.getPatientVisits("");
        Assert.assertEquals(listSize, visitList.size());
        Assert.assertEquals("Visit 1", visitList.get(listSize - 1).getVisitName());
        Assert.assertNull(visitList.get(listSize - 1).getDateOfVisit());
    }

    @Test
    public void testFormatDate() throws Exception {
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parseDateTime("2020-07-20T09:00:00.000+05:00");
        String formattedDate = Whitebox.invokeMethod(interactor, "formatDate", dateTime);
        Assert.assertNull(formattedDate);
    }
}
