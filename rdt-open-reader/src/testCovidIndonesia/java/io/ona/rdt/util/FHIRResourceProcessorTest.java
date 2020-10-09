package io.ona.rdt.util;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.DeviceDefinition;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ona.rdt.robolectric.RobolectricTest;

/**
 * Created by Vincent Karuri on 08/10/2020
 */
public class FHIRResourceProcessorTest extends RobolectricTest {

    @Test
    public void testFHIRQueriesOnDeviceDefinitionResource() throws Exception {
        PathEvaluatorLibrary.init(null, null, null, null);
        InputStream stream = RuntimeEnvironment.application.getAssets().open("DeviceDefinition.json");
        Bundle deviceDefinitionBundle = FHIRParser.parser(Format.JSON).parse(stream);

        // extract instructions
        String expression = "$this.entry.resource.where(identifier.where(value='d3fdac0e-061e-b068-2bed-5a95e803636f')).capability.where(type.where(text='instructions')).description.text";
        Assert.assertEquals("Collect blood sample", PathEvaluatorLibrary.getInstance().extractStringFromBundle(deviceDefinitionBundle, expression));

        // extract device IDs
        createDeviceIDToDeviceNameMap(deviceDefinitionBundle, extractDeviceIds(deviceDefinitionBundle));

        Assert.assertEquals("Wondfo SARS-CoV-2 Antibody Test", extractDeviceName(deviceDefinitionBundle, "d3fdac0e-061e-b068-2bed-5a95e803636f"));

        // extract manufacturer name
        expression = "$this.entry.resource.where(identifier.where(value='d3fdac0e-061e-b068-2bed-5a95e803636f')))";
        DeviceDefinition deviceDefinition =  (DeviceDefinition) PathEvaluatorLibrary.getInstance().extractResourceFromBundle(deviceDefinitionBundle, expression);
        Assert.assertEquals("Guangzhou Wondfo Biotech", deviceDefinition.getManufacturer().as(com.ibm.fhir.model.type.String.class).getValue());
    }

    private Map<String, String> createDeviceIDToDeviceNameMap(Bundle bundle, List<String> deviceIDs) {
        Map<String, String> deviceIDToDeviceName = new HashMap<>();
        for (String deviceID : deviceIDs) {
            deviceIDToDeviceName.put(deviceID, extractDeviceName(bundle, deviceID));
        }
        return deviceIDToDeviceName;
    }

    private List<String> extractDeviceIds(Bundle deviceDefinitionBundle) {
        String expression = "$this.entry.resource.identifier.value";
        return PathEvaluatorLibrary.getInstance().extractStringsFromBundle(deviceDefinitionBundle, expression);
    }

    private String extractDeviceName(Bundle deviceDefinitionBundle, String deviceId) {
        String expression = String.format("$this.entry.resource.where(identifier.where(value='%s')).deviceName.name", deviceId);
        return PathEvaluatorLibrary.getInstance().extractStringFromBundle(deviceDefinitionBundle, expression);
    }
}
