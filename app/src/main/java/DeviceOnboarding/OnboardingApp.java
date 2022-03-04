package DeviceOnboarding;

import java.util.NoSuchElementException;
import java.util.Scanner;

import DeviceOnboarding.DeviceInfo.IllegalSerialNumberException;
import DeviceOnboarding.MockDeviceFlash.FlashFailureException;
import DeviceOnboarding.MockKeyInjector.InjectionFailureException;

public class OnboardingApp {
    private MockDeviceDB mockDB = new MockDeviceDB();
    private MockDeviceFlash mockDeviceFlash;
    private MockKeyInjector mockKeyInjector;
    private MessagingTool messagingTool = new MessagingTool();

    public static void main(String[] args) {
        OnboardingApp oa = new OnboardingApp();
        Scanner scanner = new Scanner(System.in);

        System.out.println(MessagingTool.welcomeMessageString());

        while (scanner.hasNextLine()) {
            String requestString = scanner.nextLine();

            if (requestString.equals("/quit") || requestString.equals("/q")) {
                break;
            }

            System.out.println(oa.processRequest(requestString));
        }

        scanner.close();
    }

    public OnboardingApp() {
        this(new MockDeviceFlash(0), new MockKeyInjector(0));
    }

    public OnboardingApp(MockDeviceFlash mockDeviceFlash, MockKeyInjector mockKeyInjector) {
        this.mockDeviceFlash = mockDeviceFlash;
        this.mockKeyInjector = mockKeyInjector;
    }

    public String processRequest(String requestString) {
        String splitRequest[] = requestString.split(" ");

        if (splitRequest.length < 2) {
            if (splitRequest[0].equals("/h") || splitRequest[0].equals("/help")) {
                return messagingTool.helpOutputString();
            }

            return messagingTool.illegalRequestOutputString();
        }

        return handleRequest(splitRequest);
    }

    private String addDamage(DeviceInfo deviceInfo, String damage) {
        try {
            deviceInfo.setDamage(stringToDamageRating(damage));
        } catch (IllegalArgumentException e) {
            return messagingTool.errorOutputString(deviceInfo, "DAMAGE STATUS INVALID {" + damage + "}" );
        }
        
        if (deviceInfo.getCurrentState() == DeviceState.DEVICE_DAMAGED) {
            return messagingTool.errorOutputString(deviceInfo, "DAMAGE TOO HIGH");
        }

        return messagingTool.outputString(deviceInfo, "DAMAGE ADDED");
    }

    private String addDeliveryInfo(DeviceInfo deviceInfo, String boxReference, String crateReference) {
        deviceInfo.setDeliveryInfo(boxReference, crateReference);

        return messagingTool.outputString(deviceInfo, "DELIVERY INFO ADDED");
    }

    private String addDevice(String serialNumber) {
        DeviceInfo deviceInfo;
        
        try {
            deviceInfo = new DeviceInfo(serialNumber);
        } catch (IllegalSerialNumberException e) {
            return messagingTool.errorOutputString("ILLEGAL SERIAL NUMBER: " + serialNumber);
        }

        mockDB.addDevice(deviceInfo);
        
        return messagingTool.outputString(deviceInfo, "DEVICE ADDED");
    }

    private String addSIM(DeviceInfo deviceInfo, String SNN, String IMSI, String IMEI) {
        SIMCardInfo simCard = new SIMCardInfo(SNN, IMSI, IMEI);

        deviceInfo.setSIMCard(simCard);

        return messagingTool.outputString(deviceInfo, "SIM ADDED");
    }

    private String flashDevice(DeviceInfo deviceInfo) {
        try {
           if (mockDeviceFlash.flashDevice()) {
               deviceInfo.flashDevice();
           } else {
               return messagingTool.warningOutputString(deviceInfo, "DEVICE FLASH FAILED");
           }
       } catch (FlashFailureException e) {
           deviceInfo.flashFailure();

           return messagingTool.errorOutputString(deviceInfo, "(CATASTROPHIC) DEVICE FLASH FAILED");
       }

       return messagingTool.outputString(deviceInfo, "DEVICE FLASHED");
   }

    private String getDeviceInfo(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        return deviceInfo.toString();
    }

    private String handleRequest(String[] requestArgs) {
        String command = requestArgs[0];
        String serialNumber = requestArgs[1];
        DeviceInfo deviceInfo = null;

        if (command.equals("/add")) {
            return addDevice(serialNumber);
        }

        try {
            deviceInfo = mockDB.getDevice(serialNumber);

            if (command.equals("/delivery")) {
                return addDeliveryInfo(deviceInfo, requestArgs[2], requestArgs[3]);
            }

            if (command.equals("/damage")) {
                return addDamage(deviceInfo, requestArgs[2]);
            }

            if (command.equals("/sim")) {
                return addSIM(deviceInfo, requestArgs[2], requestArgs[3], requestArgs[4]);
            }

            if (command.equals("/flash")) {
                return flashDevice(deviceInfo);
            }

            if (command.equals("/key")) {
                return injectKey(deviceInfo);
            }

            if (command.equals("/repack")) {
                return repackDevice(deviceInfo);
            }

            if (command.equals("/store")) {
                return storeDevice(deviceInfo, requestArgs[2], requestArgs[3], requestArgs[4], requestArgs[5], 
                                   requestArgs[6], requestArgs[7], requestArgs[8]);
            }

            if (command.equals("/info")) {
                return getDeviceInfo(requestArgs[1]);
            }
        } catch (NoSuchElementException e) {
            return messagingTool.errorOutputString("DEVICE {" + serialNumber + "}: Device NOT Found");
        } catch (IllegalStateException e) {
            return messagingTool.transitionExceptionOutputString(deviceInfo, e);
        }

        return messagingTool.unrecognizedCommandOutputString(command);
    }

    private String injectKey(DeviceInfo deviceInfo) {
        byte key[] = new byte[128];

        try {
            if (mockKeyInjector.injectKey(key)) {
                deviceInfo.injectKey(key);
            } else {
                return messagingTool.warningOutputString(deviceInfo, "KEY INJECTION FAILED");
            }
        } catch (InjectionFailureException e) {
            deviceInfo.injectionFailure();

            return messagingTool.errorOutputString(deviceInfo, "(CATASTROPHIC) KEY INJECTION FAILED");
        }

        return messagingTool.outputString(deviceInfo, "KEY INJECTED");
    }

    private String repackDevice(DeviceInfo deviceInfo) {
        deviceInfo.sendForRepack();

        return messagingTool.outputString(deviceInfo, "DEVICE SENT FOR REPACK");
    }

    private String storeDevice(DeviceInfo deviceInfo, String warehouseNo, String sectionNo, String rowNo, String shelfNo,
            String segmentNo, String ySegmentPos, String xSegmentPos) {
        WarehouseInfo warehouseInfo;
        
        try {
            warehouseInfo = new WarehouseInfo(Integer.parseInt(warehouseNo), Integer.parseInt(sectionNo),
                                                            Integer.parseInt(rowNo), Integer.parseInt(shelfNo),
                                                            Integer.parseInt(segmentNo), stringToSegmentPosition(ySegmentPos, xSegmentPos));
        } catch (IllegalArgumentException e) {
            return messagingTool.errorOutputString(deviceInfo, "SEGMENT POSITION INVALID {" + ySegmentPos + ", " + xSegmentPos + "}" );
        }
        

        deviceInfo.setWarehouse(warehouseInfo);

        return messagingTool.outputString(deviceInfo, "DEVICE STORED IN WAREHOUSE");
    }

    private DamageRating stringToDamageRating(String damage) {
        return DamageRating.valueOf(damage.toUpperCase());
    }

    private SegmentPosition stringToSegmentPosition(String xSegmentPos, String ySegmentPos) {
        return SegmentPosition.valueOf((xSegmentPos + "_" + ySegmentPos).toUpperCase());
    }
}
