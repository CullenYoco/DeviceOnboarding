package DeviceOnboarding;

public class MockDeviceFlash implements IFlashDevice{
    int mode;

    public MockDeviceFlash(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean flashDevice() {
        if (mode < 0) {
            throw new FlashFailureException();
        }

        return mode == 0;
    }

    public class FlashFailureException extends RuntimeException{}
}