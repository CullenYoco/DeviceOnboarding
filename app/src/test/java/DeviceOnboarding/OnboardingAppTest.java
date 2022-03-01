package DeviceOnboarding;

import org.junit.jupiter.api.Test;

import DeviceOnboarding.DeviceInfo.IllegalSerialNumberException;

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
        expectedDeviceInfo.setSIMCard(new SIMCardInfo("SNN", "IMSI", "IMEI"));
        expectedDeviceInfo.flashDevice();
        expectedDeviceInfo.injectKey(new byte[128]);
        expectedDeviceInfo.sendForRepack();

        WarehouseInfo warehouseInfo = new WarehouseInfo(1, 1, 1, 1, 1, SegmentPosition.FRONTLEFT);
        expectedDeviceInfo.setWarehouse(warehouseInfo);

        assertEquals("DEVICE {2049-3630}: DEVICE ADDED\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                     oa.processRequest("/add 2049-3630"));
        assertEquals("DEVICE {2049-3630}: DELIVERY INFO ADDED\n\tSTATUS: DELIVERY_INFO_RECORDED",
                     oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo"));
        assertEquals("DEVICE {2049-3630}: DAMAGE ADDED\n\tSTATUS: DAMAGE_RECORDED",
                     oa.processRequest("/damage 2049-3630 light"));
        assertEquals("DEVICE {2049-3630}: SIM ADDED\n\tSTATUS: SIM_INSERTED_AND_RECORDED",
                     oa.processRequest("/sim 2049-3630 SNN IMSI IMEI"));
        assertEquals("DEVICE {2049-3630}: DEVICE FLASHED\n\tSTATUS: FLASHED",
                     oa.processRequest("/flash 2049-3630"));
        assertEquals("DEVICE {2049-3630}: KEY INJECTED\n\tSTATUS: KEY_INJECTED",
                     oa.processRequest("/key 2049-3630"));
        assertEquals("DEVICE {2049-3630}: DEVICE SENT FOR REPACK\n\tSTATUS: SENT_FOR_REPACK",
                     oa.processRequest("/repack 2049-3630"));
        assertEquals("DEVICE {2049-3630}: DEVICE STORED IN WAREHOUSE\n\tSTATUS: STORED_IN_WAREHOUSE",
                     oa.processRequest("/store 2049-3630 1 1 1 1 1 front left"));

        assertEquals(expectedDeviceInfo.toString(), oa.processRequest("/info 2049-3630"));
    }

    @Test
    public void getInfoOnNonExistentDeviceTest() {
        assertEquals("ERROR -> DEVICE {2049-3630}: Device NOT Found", oa.processRequest("/info 2049-3630"));
    }

    @Test
    public void illegalSerialNumberTest() {
        assertThrows(IllegalSerialNumberException.class, () -> {
            oa.processRequest("/add 1234");
        });
    }

    @Test
    public void faultyFlashTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(1), new MockKeyInjector(0));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");

        assertEquals("WARNING -> DEVICE {2049-3630}: DEVICE FLASH FAILED\n\tSTATUS: SIM_INSERTED_AND_RECORDED",
                     customOnboardingApp.processRequest("/flash 2049-3630"));
    }

    @Test
    public void exceptionFaultFlashTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(-1), new MockKeyInjector(0));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");      

        assertEquals("ERROR -> DEVICE {2049-3630}: (CATASTROPHIC) DEVICE FLASH FAILED\n\tSTATUS: SEVERE_FLASH_FAILURE",
                     customOnboardingApp.processRequest("/flash 2049-3630"));
    }

    @Test
    public void faultyKeyInjectionTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(0), new MockKeyInjector(1));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");
        customOnboardingApp.processRequest("/flash 2049-3630");

        assertEquals("WARNING -> DEVICE {2049-3630}: KEY INJECTION FAILED\n\tSTATUS: FLASHED",
                     customOnboardingApp.processRequest("/key 2049-3630"));
    }

    @Test
    public void exceptionFaultKeyInjectionTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(0), new MockKeyInjector(-1));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");
        customOnboardingApp.processRequest("/flash 2049-3630");

        assertEquals("ERROR -> DEVICE {2049-3630}: (CATASTROPHIC) KEY INJECTION FAILED\n\tSTATUS: SEVERE_KEY_INJECTION_FAILURE",
                     customOnboardingApp.processRequest("/key 2049-3630"));
    }

    @Test
    public void stateTransitionErrorTest() {
        oa.processRequest("/add 2049-3630");

        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> DAMAGE_RECORDED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/damage 2049-3630 light"));
        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> SIM_INSERTED_AND_RECORDED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/sim 2049-3630 SNN IMSI IMEI"));
        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> FLASHED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/flash 2049-3630"));

        oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");

        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> KEY_INJECTED)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/key 2049-3630"));
        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> SENT_FOR_REPACK)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/repack 2049-3630"));
        assertEquals("WARNING -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> STORED_IN_WAREHOUSE)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/store 2049-3630 1 1 1 1 1 front left"));
    }
}
