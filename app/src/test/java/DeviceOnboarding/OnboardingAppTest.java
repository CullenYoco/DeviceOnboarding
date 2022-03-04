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
        DeviceInfo expectedDeviceInfo = new DeviceInfo("2049-3630");
        expectedDeviceInfo.setDeliveryInfo("boxRefNo", "crateRefNo");
        expectedDeviceInfo.setDamage(DamageRating.LIGHT);
        expectedDeviceInfo.setSIMCard(new SIMCardInfo("SNN", "IMSI", "IMEI"));
        expectedDeviceInfo.flashDevice();
        expectedDeviceInfo.injectKey(new byte[128]);
        expectedDeviceInfo.sendForRepack();

        WarehouseInfo warehouseInfo = new WarehouseInfo(1, 1, 1, 1, 1, SegmentPosition.FRONT_LEFT);
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
        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: Device NOT Found", oa.processRequest("/info 2049-3630"));
    }

    @Test
    public void illegalSerialNumberTest() {
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL SERIAL NUMBER: 1234", oa.processRequest("/add 1234"));
    }

    @Test
    public void faultyFlashTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(1), new MockKeyInjector(0));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");

        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: DEVICE FLASH FAILED\n\tSTATUS: SIM_INSERTED_AND_RECORDED",
                     customOnboardingApp.processRequest("/flash 2049-3630"));
    }

    @Test
    public void exceptionFaultFlashTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(-1), new MockKeyInjector(0));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");      

        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: (CATASTROPHIC) DEVICE FLASH FAILED\n\tSTATUS: SEVERE_FLASH_FAILURE",
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

        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: KEY INJECTION FAILED\n\tSTATUS: FLASHED",
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

        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: (CATASTROPHIC) KEY INJECTION FAILED\n\tSTATUS: SEVERE_KEY_INJECTION_FAILURE",
                     customOnboardingApp.processRequest("/key 2049-3630"));
    }

    @Test
    public void stateTransitionErrorTest() {
        oa.processRequest("/add 2049-3630");

        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> DAMAGE_RECORDED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/damage 2049-3630 light"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> SIM_INSERTED_AND_RECORDED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/sim 2049-3630 SNN IMSI IMEI"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SERIAL_NUMBER_RECORDED -> FLASHED)\n\tSTATUS: SERIAL_NUMBER_RECORDED",
                      oa.processRequest("/flash 2049-3630"));

        oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");

        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> KEY_INJECTED)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/key 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> SENT_FOR_REPACK)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/repack 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DELIVERY_INFO_RECORDED -> STORED_IN_WAREHOUSE)\n\tSTATUS: DELIVERY_INFO_RECORDED",
                      oa.processRequest("/store 2049-3630 1 1 1 1 1 front left"));

        oa.processRequest("/damage 2049-3630 light");

        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DAMAGE_RECORDED -> DELIVERY_INFO_RECORDED)\n\tSTATUS: DAMAGE_RECORDED",
                      oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo"));
    }

    @Test
    public void illegalRequestTest() {
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL REQUEST FORMAT\n\tUNRECOGNIZED COMMAND: requestString", oa.processRequest("requestString"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL REQUEST FORMAT\n\tUNRECOGNIZED COMMAND: /test", oa.processRequest("/test 1234"));

        oa.processRequest("/add 2049-3630");
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL REQUEST FORMAT\n\tUNRECOGNIZED COMMAND: /test", oa.processRequest("/test 2049-3630"));
    }

    @Test
    public void differentDamageRatingTest() {
        damageRatingTest(DamageRating.NONE, "none");
        damageRatingTest(DamageRating.MODERATE, "moderate");
        damageRatingTest(DamageRating.HIGH, "HIGH");
        damageRatingTest(DamageRating.UNUSABLE, "UnUsaBLe");
    }

    @Test
    public void differentSegmentPositionTest() {
        segmentPositionTest(SegmentPosition.BACK_CENTRE, "back centre");
        segmentPositionTest(SegmentPosition.BACK_LEFT, "bAcK LeFt");
        segmentPositionTest(SegmentPosition.BACK_RIGHT, "BACK right");
        segmentPositionTest(SegmentPosition.FRONT_CENTRE, "front CENTRE");
        segmentPositionTest(SegmentPosition.FRONT_RIGHT, "FRONT RIGHT");
    }

    @Test
    public void illegalStateProgressAttemptTest() {
        OnboardingApp customOnboardingApp = new OnboardingApp(new MockDeviceFlash(-1), new MockKeyInjector(0));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");
        customOnboardingApp.processRequest("/flash 2049-3630");
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SEVERE_FLASH_FAILURE -> KEY_INJECTED)\n\tSTATUS: SEVERE_FLASH_FAILURE",
                      customOnboardingApp.processRequest("/key 2049-3630"));
        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: (CATASTROPHIC) DEVICE FLASH FAILED\n\tSTATUS: SEVERE_FLASH_FAILURE",
                      customOnboardingApp.processRequest("/flash 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SEVERE_FLASH_FAILURE -> SENT_FOR_REPACK)\n\tSTATUS: SEVERE_FLASH_FAILURE",
                      customOnboardingApp.processRequest("/repack 2049-3630"));

        customOnboardingApp = new OnboardingApp(new MockDeviceFlash(0), new MockKeyInjector(-1));
        customOnboardingApp.processRequest("/add 2049-3630");
        customOnboardingApp.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        customOnboardingApp.processRequest("/damage 2049-3630 light");
        customOnboardingApp.processRequest("/sim 2049-3630 SNN IMSI IMEI");
        customOnboardingApp.processRequest("/flash 2049-3630");
        customOnboardingApp.processRequest("/key 2049-3630");
        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: (CATASTROPHIC) KEY INJECTION FAILED\n\tSTATUS: SEVERE_KEY_INJECTION_FAILURE",
                    customOnboardingApp.processRequest("/key 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SEVERE_KEY_INJECTION_FAILURE -> FLASHED)\n\tSTATUS: SEVERE_KEY_INJECTION_FAILURE",
                    customOnboardingApp.processRequest("/flash 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (SEVERE_KEY_INJECTION_FAILURE -> SENT_FOR_REPACK)\n\tSTATUS: SEVERE_KEY_INJECTION_FAILURE",
                    customOnboardingApp.processRequest("/repack 2049-3630"));
    }

    @Test
    public void tooMuchDamageTest() {
        oa.processRequest("/add 2049-3630");
        oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        
        assertEquals("\u001B[31mERROR\u001B[0m -> DEVICE {2049-3630}: DAMAGE TOO HIGH\n\tSTATUS: DEVICE_DAMAGED",
                      oa.processRequest("/damage 2049-3630 moderate"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DEVICE_DAMAGED -> KEY_INJECTED)\n\tSTATUS: DEVICE_DAMAGED",
                      oa.processRequest("/key 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DEVICE_DAMAGED -> FLASHED)\n\tSTATUS: DEVICE_DAMAGED",
                      oa.processRequest("/flash 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DEVICE_DAMAGED -> SENT_FOR_REPACK)\n\tSTATUS: DEVICE_DAMAGED",
                      oa.processRequest("/repack 2049-3630"));
        assertEquals("\u001B[33mWARNING\u001B[0m -> DEVICE {2049-3630}: ILLEGAL STATE TRANSITION (DEVICE_DAMAGED -> DAMAGE_RECORDED)\n\tSTATUS: DEVICE_DAMAGED",
                      oa.processRequest("/damage 2049-3630 light"));
    }

    @Test
    public void helpTest() {
        String expectedString = "=== HELP ===\n" +
                                "* /add <SerialNo>\n" +
                                "* /damage <SerialNo> <DamageRating>\n" +
                                "* /delivery <SerialNo> <BoxRef> <CrateRef>\n" +
                                "* /flash <SerialNo>\n" +
                                "* /info <SerialNo>\n" + 
                                "* /key <SerialNo>\n" +
                                "* /repack <SerialNo>\n" +
                                "* /sim <SerialNo> <SNN> <IMSI> <IMEI>\n" +
                                "* /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>";

        assertEquals(expectedString, oa.processRequest("/help"));
    }

    @Test
    public void illegalArgumentTest() {
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /add <SerialNo>", oa.processRequest("/add"));

        oa.processRequest("/add 2049-3630");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /delivery <SerialNo> <BoxRef> <CrateRef>", oa.processRequest("/delivery 2049-3630"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /delivery <SerialNo> <BoxRef> <CrateRef>", oa.processRequest("/delivery 2049-3630 box"));
        
        oa.processRequest("/delivery 2049-3630 box crate");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /damage <SerialNo> <DamageRating>", oa.processRequest("/damage 2049-3630"));

        oa.processRequest("/damage 2049-3630 none");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /sim <SerialNo> <SNN> <IMSI> <IMEI>", oa.processRequest("/sim 2049-3630"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /sim <SerialNo> <SNN> <IMSI> <IMEI>", oa.processRequest("/sim 2049-3630 SNN"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /sim <SerialNo> <SNN> <IMSI> <IMEI>", oa.processRequest("/sim 2049-3630 SNN IMSI"));

        oa.processRequest("/sim 2049-3630 SNN IMSI IMEI");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /flash <SerialNo>", oa.processRequest("/flash"));

        oa.processRequest("/flash 2049-3630");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /key <SerialNo>", oa.processRequest("/key"));

        oa.processRequest("/key 2049-3630");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /repack <SerialNo>", oa.processRequest("/repack"));

        oa.processRequest("/repack 2049-3630");

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1 2"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1 2 3"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1 2 3 4"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1 2 3 4 5"));
        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>",
                     oa.processRequest("/store 2049-3630 1 2 3 4 5 front"));

        assertEquals("\u001B[31mERROR\u001B[0m -> ILLEGAL ARGUMENTS\n\tEXPECTED: /info <SerialNo>", oa.processRequest("/info"));
    }

    private void damageRatingTest(DamageRating damageRating, String testString) {
        oa = new OnboardingApp();

        DeviceInfo expectedDeviceInfo = new DeviceInfo("2049-3630");
        expectedDeviceInfo.setDeliveryInfo("boxRefNo", "crateRefNo");
        expectedDeviceInfo.setDamage(damageRating);
        oa.processRequest("/add 2049-3630");
        oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        oa.processRequest("/damage 2049-3630 " + testString);

        assertEquals(expectedDeviceInfo.toString(), oa.processRequest("/info 2049-3630"));
    }

    private void segmentPositionTest(SegmentPosition segmentPosition, String testString) {
        oa = new OnboardingApp();

        DeviceInfo expectedDeviceInfo = new DeviceInfo("2049-3630");
        expectedDeviceInfo.setDeliveryInfo("boxRefNo", "crateRefNo");
        expectedDeviceInfo.setDamage(DamageRating.LIGHT);
        expectedDeviceInfo.setSIMCard(new SIMCardInfo("SNN", "IMSI", "IMEI"));
        expectedDeviceInfo.flashDevice();
        expectedDeviceInfo.injectKey(new byte[128]);
        expectedDeviceInfo.sendForRepack();

        WarehouseInfo warehouseInfo = new WarehouseInfo(1, 1, 1, 1, 1, segmentPosition);
        expectedDeviceInfo.setWarehouse(warehouseInfo);

        oa.processRequest("/add 2049-3630");
        oa.processRequest("/delivery 2049-3630 boxRefNo crateRefNo");
        oa.processRequest("/damage 2049-3630 light");
        oa.processRequest("/sim 2049-3630 SNN IMSI IMEI");
        oa.processRequest("/flash 2049-3630");
        oa.processRequest("/key 2049-3630");
        oa.processRequest("/repack 2049-3630");
        oa.processRequest("/store 2049-3630 1 1 1 1 1 " + testString);

        assertEquals(expectedDeviceInfo.toString(), oa.processRequest("/info 2049-3630"));
    }
}
