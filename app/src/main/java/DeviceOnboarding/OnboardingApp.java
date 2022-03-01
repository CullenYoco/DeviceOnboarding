package DeviceOnboarding;

import java.util.NoSuchElementException;

import DeviceOnboarding.MockDeviceFlash.FlashFailureException;
import DeviceOnboarding.MockKeyInjector.InjectionFailureException;

public class OnboardingApp {
    MockDeviceDB mockDB = new MockDeviceDB();
    MockDeviceFlash mockDeviceFlash;
    MockKeyInjector mockKeyInjector;

    public OnboardingApp() {
        mockDeviceFlash = new MockDeviceFlash(0);
        mockKeyInjector = new MockKeyInjector(0);
    }

    public OnboardingApp(MockDeviceFlash mockDeviceFlash, MockKeyInjector mockKeyInjector) {
        this.mockDeviceFlash = mockDeviceFlash;
        this.mockKeyInjector = mockKeyInjector;
    }

    public String processRequest(String requestString) {
        String splitRequest[] = requestString.split(" ");

        if (splitRequest[0].equals("/add")) {
            return addDevice(splitRequest[1]);
        }
        
        if (splitRequest[0].equals("/delivery")) {
            return addDeliveryInfo(splitRequest[1], splitRequest[2], splitRequest[3]);
        }
        
        if (splitRequest[0].equals("/damage")) {
            return addDamage(splitRequest[1], splitRequest[2]);
        }

        if (splitRequest[0].equals("/sim")) {
            return addSIM(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4]);
        }

        if (splitRequest[0].equals("/flash")) {
            return flashDevice(splitRequest[1]);
        }

        if (splitRequest[0].equals("/key")) {
            return injectKey(splitRequest[1]);
        }

        if (splitRequest[0].equals("/repack")) {
            return repackDevice(splitRequest[1]);
        }

        if (splitRequest[0].equals("/store")) {
            return storeDevice(splitRequest[1], splitRequest[2], splitRequest[3], splitRequest[4], splitRequest[5], 
                               splitRequest[6], splitRequest[7], splitRequest[8]);
        }
        
        return getDeviceInfo(splitRequest[1]);
    }

    private String getDeviceInfo(String serialNumber) {
        DeviceInfo deviceInfo;

        try {
            deviceInfo = mockDB.getDevice(serialNumber);
        } catch (NoSuchElementException e) {
            return "ERROR -> DEVICE {" + serialNumber + "}: Device NOT Found";
        }

        return deviceInfo.toString();
    }

    private String addDevice(String serialNumber) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber(serialNumber);

        mockDB.addDevice(deviceInfo);
        return outputString(serialNumber, deviceInfo, "DEVICE ADDED");
    }

    private String addDeliveryInfo(String serialNumber, String boxReference, String crateReference) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        deviceInfo.setDeliveryInfo(boxReference, crateReference);

        return outputString(serialNumber, deviceInfo, "DELIVERY INFO ADDED");
    }

    private String addDamage(String serialNumber, String damage) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        try {
            deviceInfo.setDamage(DamageRating.LIGHT); // ??
        } catch (IllegalStateException e) {
            return transitionExceptionOutputString(serialNumber, deviceInfo, e);
        }

        return outputString(serialNumber, deviceInfo, "DAMAGE ADDED");
    }

    private String addSIM(String serialNumber, String SNN, String IMSI, String IMEI) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);
        SIMCardInfo simCard = new SIMCardInfo(SNN, IMSI, IMEI);

        try {
            deviceInfo.setSIMCard(simCard);
        } catch (IllegalStateException e) {
            return transitionExceptionOutputString(serialNumber, deviceInfo, e);
        }

        return outputString(serialNumber, deviceInfo, "SIM ADDED");
    }

    private String flashDevice(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

         try {
            if (mockDeviceFlash.flashDevice()) {
                deviceInfo.flashDevice();
            } else {
                return warningOutputString(serialNumber, deviceInfo, "DEVICE FLASH FAILED");
            }
        } catch (FlashFailureException e) {
            deviceInfo.flashFailure();

            return errorOutputString(serialNumber, deviceInfo, "(CATASTROPHIC) DEVICE FLASH FAILED");
        }

        return outputString(serialNumber, deviceInfo, "DEVICE FLASHED");
    }

    private String injectKey(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);
        byte key[] = new byte[128];

        try {
            if (mockKeyInjector.injectKey(key)) {
                deviceInfo.injectKey(key);
            } else {
                return warningOutputString(serialNumber, deviceInfo, "KEY INJECTION FAILED");
            }
        } catch (InjectionFailureException e) {
            deviceInfo.injectionFailure();

            return errorOutputString(serialNumber, deviceInfo, "(CATASTROPHIC) KEY INJECTION FAILED");
        }

        return outputString(serialNumber, deviceInfo, "KEY INJECTED");
    }

    private String repackDevice(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        deviceInfo.sendForRepack();

        return outputString(serialNumber, deviceInfo, "DEVICE SENT FOR REPACK");
    }

    private String storeDevice(String serialNumber, String warehouseNo, String sectionNo, String rowNo, String shelfNo,
            String segmentNo, String ySegmentPos, String xSegmentPos) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);
        WarehouseInfo warehouseInfo = new WarehouseInfo(Integer.parseInt(warehouseNo), Integer.parseInt(sectionNo), Integer.parseInt(rowNo), Integer.parseInt(shelfNo), Integer.parseInt(segmentNo), SegmentPosition.FRONTLEFT);

        deviceInfo.setWarehouse(warehouseInfo);

        return outputString(serialNumber, deviceInfo, "DEVICE STORED IN WAREHOUSE");
    }

    private String outputString(String serialNumber, DeviceInfo deviceInfo, String message) {
        return "DEVICE {" + serialNumber + "}: " + message + "\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String warningOutputString(String serialNumber, DeviceInfo deviceInfo, String message) {
        return "WARNING -> " + outputString(serialNumber, deviceInfo, message);
    }

    private String errorOutputString(String serialNumber, DeviceInfo deviceInfo, String message) {
        return "ERROR -> " + outputString(serialNumber, deviceInfo, message);
    }

    private String transitionExceptionOutputString(String serialNumber, DeviceInfo deviceInfo, IllegalStateException e) {
        return warningOutputString(serialNumber, deviceInfo, "ILLEGAL STATE TRANSITION (" + deviceInfo.getCurrentState() + " -> " + e.getMessage() + ")");
    }
}
