package DeviceOnboarding;

import java.util.NoSuchElementException;

public class OnboardingApp {
    MockDeviceDB mockDB = new MockDeviceDB();
    MockDeviceFlash mockDeviceFlash;

    public OnboardingApp() {
        mockDeviceFlash = new MockDeviceFlash(0);
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
        return "DEVICE {" + serialNumber + "}: DEVICE ADDED\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String addDeliveryInfo(String serialNumber, String boxReference, String crateReference) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        deviceInfo.setDeliveryInfo(boxReference, crateReference);

        return "DEVICE {" + serialNumber + "}: DELIVERY INFO ADDED\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String addDamage(String serialNumber, String damage) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);

        deviceInfo.setDamage(DamageRating.LIGHT); // ??

        return "DEVICE {" + serialNumber + "}: DAMAGE ADDED\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String addSIM(String serialNumber, String SNN, String IMSI, String IMEI) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);
        SIMCardInfo simCard = new SIMCardInfo(SNN, IMSI, IMEI);

        deviceInfo.setSIMCard(simCard);

        return "DEVICE {" + serialNumber + "}: SIM ADDED\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String flashDevice(String serialNumber) {
        DeviceInfo deviceInfo = mockDB.getDevice(serialNumber);
        
        if (mockDeviceFlash.flashDevice()) {
            deviceInfo.flashDevice();
        }

        return "DEVICE {" + serialNumber + "}: DEVICE FLASHED\n\tSTATUS: " + deviceInfo.getCurrentState();
    }
}
