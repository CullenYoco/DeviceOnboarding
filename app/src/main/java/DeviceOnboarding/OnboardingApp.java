package DeviceOnboarding;

import java.util.NoSuchElementException;

public class OnboardingApp {
    MockDeviceDB mockDB = new MockDeviceDB();

    public String processRequest(String requestString) {
        String splitRequest[] = requestString.split(" ");

        if (splitRequest[0].equals("/add")) {
            return addDevice(splitRequest[1]);
        } else if (splitRequest[0].equals("/delivery")) {
            return addDeliveryInfo(splitRequest[1], splitRequest[2], splitRequest[3]);
        } else if (splitRequest[0].equals("/damage")) {
            return addDamage(splitRequest[1], splitRequest[2]);
        } else {
            return getDeviceInfo(splitRequest[1]);
        }
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
}
