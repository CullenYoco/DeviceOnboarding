package DeviceOnboarding;

public class MockDeviceDB {
    private int deviceCount = 0;
    private String serialNumbers[] = new String[10];

    public void addDevice(String serialNo) {
        if (isIllegalSerialNumber(serialNo)) {
            throw new IllegalSerialNumberException();
        }

        serialNumbers[deviceCount++] = serialNo;
    }

    public boolean contains(String serialNo) {
        if (isIllegalSerialNumber(serialNo)) {
            throw new IllegalSerialNumberException();
        }

        return getIndex(serialNo) != -1;
    }

    public void removeDevice(String serialNo) {
        if (isIllegalSerialNumber(serialNo)) {
            throw new IllegalSerialNumberException();
        }

        if (isEmpty() || !contains(serialNo)) {
            throw new IllegalRemoveException();
        }

        updateDeviceStorage(serialNo);
    }

    public boolean isEmpty() {
        return deviceCount == 0;
    }

    private boolean isIllegalSerialNumber(String serialNo) {
        return !serialNo.matches("\\d{4}-\\d{3}[\\dxX]");
    }

    private int getIndex(String serialNo) {
        for (int i = 0; i < deviceCount; i++) {
            if (serialNumbers[i].equals(serialNo)) {
                return i;
            }
        }

        return -1;
    }

    private void updateDeviceStorage(String serialNo) {
        for (int i = getIndex(serialNo) + 1; i < deviceCount; i++) {
            serialNumbers[i - 1] = serialNumbers[i];
            serialNumbers[i] = null;
        }

        deviceCount--;
    }

    public class IllegalRemoveException extends RuntimeException {};

    public class IllegalSerialNumberException extends RuntimeException {};
}
