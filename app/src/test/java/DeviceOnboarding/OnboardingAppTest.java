package DeviceOnboarding;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

class OnboardingAppTest {

    OnboardingApp oa;

    @BeforeEach
    public void beforeEach() {
        oa = new OnboardingApp();
    }

    @Test
    public void simpleWorkFlowTest() {
        DeviceInfo expectedDeviceInfo = new DeviceInfo();
        expectedDeviceInfo.setSerialNumber("2049-3630");
        expectedDeviceInfo.setDeliveryInfo("boxRefNo", "crateRefNo");
        expectedDeviceInfo.setDamage(DamageRating.LIGHT);

        assertEquals("DEVICE {2049-3630}: DEVICE ADDED\n\tSTATUS: SERIAL_NUMBER_RECORDED", oa.processRequest("/add 2049-3630"));
        assertEquals("DEVICE {2049-3630}: DELIVERY INFO ADDED\n\tSTATUS: DELIVERY_INFO_RECORDED", oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo"));
        assertEquals("DEVICE {2049-3630}: DAMAGE ADDED\n\tSTATUS: DAMAGE_RECORDED", oa.processRequest("/damage 2049-3630 light"));

        assertEquals(expectedDeviceInfo.toString(), oa.processRequest("/info 2049-3630"));
    }

    @Test
    public void getInfoOnNonExistentDeviceTest() {
        assertEquals("ERROR -> DEVICE {2049-3630}: Device NOT Found", oa.processRequest("/info 2049-3630"));
    }
}
