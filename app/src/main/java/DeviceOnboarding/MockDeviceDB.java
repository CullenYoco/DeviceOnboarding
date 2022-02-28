package DeviceOnboarding;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MockDeviceDB {
    ArrayList<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();

    public void addDevice(DeviceInfo deviceInfo) {
        if (isDeviceInfoIllegal(deviceInfo)) {
            throw new IllegalArgumentException();
        }

        deviceList.add(deviceInfo);
    }

    public boolean contains(String serialNumber) {
        return getIndex(serialNumber) != -1;
    }

    public DeviceInfo getDevice(String serialNumber) throws NoSuchElementException {
        int index = getIndex(serialNumber);

        if (index == -1) {
            throw new NoSuchElementException();
        }

        return deviceList.get(index);
    }

    private int getIndex(String serialNumber) {
        for (int i = 0; i < deviceList.size(); i++) {
            if (isDesiredElement(serialNumber, i)) {
                return i;
            }
        }

        return -1;
    }

    private boolean isDesiredElement(String serialNumber, int i) {
        return deviceList.get(i).getSerialNumber().equals(serialNumber);
    }

    private boolean isDeviceInfoIllegal(DeviceInfo deviceInfo) {
        return deviceInfo == null || deviceInfo.getSerialNumber() == null;
    }
}
