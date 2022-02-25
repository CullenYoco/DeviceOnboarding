package DeviceOnboarding;

public class MockDeviceFlash implements IFlashDevice{

    @Override
    public boolean flashDevice() {
        return false;
    }
}