package DeviceOnboarding;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import DeviceOnboarding.MockDeviceDB.IllegalRemoveException;
import DeviceOnboarding.MockDeviceDB.IllegalSerialNumberException;

public class MockDeviceDBTest {
    private MockDeviceDB mdb;

    @BeforeEach
    public void beforeEach() {
        mdb = new MockDeviceDB();
    }
    
    @Test
    public void initTest() {
        assertTrue(mdb.isEmpty());
    }

    @Test
    public void addDeviceTest() {
        mdb.addDevice("2049-3630");
        assertFalse(mdb.isEmpty());
    }

    @Test
    public void removeDeviceTest() {
        mdb.addDevice("2049-3630");
        mdb.removeDevice("2049-3630");
        assertTrue(mdb.isEmpty());
    }

    @Test
    public void illegalSerialNumberTest() {
        assertThrows(IllegalSerialNumberException.class, () -> {
            mdb.addDevice("");
        });

        assertThrows(IllegalSerialNumberException.class, () -> {
            mdb.removeDevice("");
        });
    }

    @Test
    public void illegalRemoveOnEmptyDBTest() {
        assertThrows(IllegalRemoveException.class, () -> {
            mdb.removeDevice("2049-3630");
        });
    }

    @Test
    public void illegalRemoveNonExistentDeviceTest() {
        mdb.addDevice("2049-3630");

        assertThrows(IllegalRemoveException.class, () -> {
            mdb.removeDevice("2049-3631");
        });
    }

    @Test
    public void addRemoveAndTryToRemoveAgainTest() {
        mdb.addDevice("2049-3630");
        mdb.addDevice("2049-3631");
        mdb.removeDevice("2049-3630");

        assertThrows(IllegalRemoveException.class, () -> {
            mdb.removeDevice("2049-3630");
        });

        mdb.removeDevice("2049-3631");
    }

    @Test
    public void containsTest() {
        mdb.addDevice("2049-3630");

        assertTrue(mdb.contains("2049-3630"));
        assertFalse(mdb.contains("2049-3631"));

        assertThrows(IllegalSerialNumberException.class, () -> {
            mdb.contains("2049-");
        });
    }
}
