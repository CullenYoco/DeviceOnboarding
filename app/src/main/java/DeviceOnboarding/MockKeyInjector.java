package DeviceOnboarding;

public class MockKeyInjector implements IKeyInjector{
    int mode;

    public MockKeyInjector(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean injectKey(byte[] key) {
        if (mode < 0) {
            throw new InjectionFailureException();
        }
        return mode == 0;
    }

    public class InjectionFailureException extends RuntimeException{}
}