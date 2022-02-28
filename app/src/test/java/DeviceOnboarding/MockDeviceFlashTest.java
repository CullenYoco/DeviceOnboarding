package DeviceOnboarding;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import DeviceOnboarding.MockDeviceFlash.FlashFailureException;

public class MockDeviceFlashTest {
    @Test
    public void workingFlashTest() {
        MockDeviceFlash mdf = new MockDeviceFlash(0);

        assertTrue(mdf.flashDevice());
    }

    @Test
    public void failedButNotExceptionTest() {
        MockDeviceFlash mdf = new MockDeviceFlash(1);
        assertFalse(mdf.flashDevice());

        mdf = new MockDeviceFlash(5);
        assertFalse(mdf.flashDevice());
    }

    @Test
    public void exceptionFailureTest() {
        assertThrows(FlashFailureException.class, () -> {
            (new MockDeviceFlash(-1)).flashDevice();
        });

        assertThrows(FlashFailureException.class, () -> {
            (new MockDeviceFlash(-5)).flashDevice();
        });
    }
}
