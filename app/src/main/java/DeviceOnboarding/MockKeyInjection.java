package DeviceOnboarding;

public class MockKeyInjection implements IKeyInjector{

    @Override
    public boolean injectKey(byte[] key) {
        return false;
    }
}