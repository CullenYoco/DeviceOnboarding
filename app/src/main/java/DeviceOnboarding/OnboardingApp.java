package DeviceOnboarding;

import java.util.NoSuchElementException;

import DeviceOnboarding.MockDeviceFlash.FlashFailureException;
import DeviceOnboarding.MockKeyInjector.InjectionFailureException;

public class OnboardingApp {
    MockDeviceDB mockDB = new MockDeviceDB();
    MockDeviceFlash mockDeviceFlash;
    MockKeyInjector mockKeyInjector;

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
            return illegalRequestOutputString();
        }

        return handleRequest(splitRequest);
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
            return "ERROR -> DEVICE {" + serialNumber + "}: Device NOT Found";
        } catch (IllegalStateException e) {
            return transitionExceptionOutputString(deviceInfo, e);
        }

        return unrecognizedCommandOutputString(command);
    }

    private String unrecognizedCommandOutputString(String command) {
        return illegalRequestOutputString() + "\n\tUNRECOGNIZED COMMAND: " + command;
    }

    private String getDeviceInfo(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        return deviceInfo.toString();
    }

    private String addDevice(String serialNumber) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber(serialNumber);

        mockDB.addDevice(deviceInfo);
        return outputString(deviceInfo, "DEVICE ADDED");
    }

    private String addDeliveryInfo(DeviceInfo deviceInfo, String boxReference, String crateReference) {
        deviceInfo.setDeliveryInfo(boxReference, crateReference);

        return outputString(deviceInfo, "DELIVERY INFO ADDED");
    }

    private String addDamage(DeviceInfo deviceInfo, String damage) {
        deviceInfo.setDamage(DamageRating.LIGHT); // ??

        return outputString(deviceInfo, "DAMAGE ADDED");
    }

    private String addSIM(DeviceInfo deviceInfo, String SNN, String IMSI, String IMEI) {
        SIMCardInfo simCard = new SIMCardInfo(SNN, IMSI, IMEI);

        deviceInfo.setSIMCard(simCard);

        return outputString(deviceInfo, "SIM ADDED");
    }

    private String flashDevice(DeviceInfo deviceInfo) {
         try {
            if (mockDeviceFlash.flashDevice()) {
                deviceInfo.flashDevice();
            } else {
                return warningOutputString(deviceInfo, "DEVICE FLASH FAILED");
            }
        } catch (FlashFailureException e) {
            deviceInfo.flashFailure();

            return errorOutputString(deviceInfo, "(CATASTROPHIC) DEVICE FLASH FAILED");
        }

        return outputString(deviceInfo, "DEVICE FLASHED");
    }

    private String injectKey(DeviceInfo deviceInfo) {
        byte key[] = new byte[128];

        try {
            if (mockKeyInjector.injectKey(key)) {
                deviceInfo.injectKey(key);
            } else {
                return warningOutputString(deviceInfo, "KEY INJECTION FAILED");
            }
        } catch (InjectionFailureException e) {
            deviceInfo.injectionFailure();

            return errorOutputString(deviceInfo, "(CATASTROPHIC) KEY INJECTION FAILED");
        }

        return outputString(deviceInfo, "KEY INJECTED");
    }

    private String repackDevice(DeviceInfo deviceInfo) {
        deviceInfo.sendForRepack();

        return outputString(deviceInfo, "DEVICE SENT FOR REPACK");
    }

    private String storeDevice(DeviceInfo deviceInfo, String warehouseNo, String sectionNo, String rowNo, String shelfNo,
            String segmentNo, String ySegmentPos, String xSegmentPos) {
        WarehouseInfo warehouseInfo = new WarehouseInfo(Integer.parseInt(warehouseNo), Integer.parseInt(sectionNo), Integer.parseInt(rowNo), Integer.parseInt(shelfNo), Integer.parseInt(segmentNo), SegmentPosition.FRONTLEFT);

        deviceInfo.setWarehouse(warehouseInfo);

        return outputString(deviceInfo, "DEVICE STORED IN WAREHOUSE");
    }

    private String outputString(DeviceInfo deviceInfo, String message) {
        return "DEVICE {" + deviceInfo.getSerialNumber() + "}: " + message + "\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String warningOutputString(DeviceInfo deviceInfo, String message) {
        return "WARNING -> " + outputString(deviceInfo, message);
    }

    private String errorOutputString(DeviceInfo deviceInfo, String message) {
        return "ERROR -> " + outputString(deviceInfo, message);
    }

    private String transitionExceptionOutputString(DeviceInfo deviceInfo, IllegalStateException e) {
        return warningOutputString(deviceInfo, "ILLEGAL STATE TRANSITION (" + deviceInfo.getCurrentState() + " -> " + e.getMessage() + ")");
    }

    private String illegalRequestOutputString() {
        return "ERROR -> ILLEGAL REQUEST FORMAT";
    }
}
