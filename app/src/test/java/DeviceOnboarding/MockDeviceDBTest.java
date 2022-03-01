package DeviceOnboarding;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MockDeviceDBTest {
    private MockDeviceDB mdb;

    @BeforeEach
    public void beforeEach() {
        mdb = new MockDeviceDB();
    }
    
    @Test
    public void initTest() {
        assertFalse(mdb.contains("2049-3630"));
    }

    @Test
    public void addDeviceTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber("2049-3630");

        mdb.addDevice(deviceInfo);
        assertTrue(mdb.contains("2049-3630"));
    }

    @Test
    public void getDeviceTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber("2049-3630");

        mdb.addDevice(deviceInfo);
        deviceInfo = mdb.getDevice("2049-3630");

        assertNotNull(deviceInfo);
        assertEquals(deviceInfo.getSerialNumber(), "2049-3630");
    }

    @Test
    public void illegalDeviceTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            mdb.addDevice(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            mdb.addDevice(new DeviceInfo());
        });
    }

    @Test
    public void emptyNoSuchElementTest() {
        assertThrows(NoSuchElementException.class, () -> {
            mdb.getDevice("2049-3630");
        });
    }

    @Test
    public void fullNoSuchElementTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber("2049-3630");
        mdb.addDevice(deviceInfo);

        assertThrows(NoSuchElementException.class, () -> {
            mdb.getDevice("2049-3631");
        });
    }

    @Test
    public void addExistingElementTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber("2049-3630");

        mdb.addDevice(deviceInfo);

        assertThrows(IllegalArgumentException.class, () -> {
            mdb.addDevice(deviceInfo);
        });
    }
}
