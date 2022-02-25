package DeviceOnboarding;

public interface IKeyInjector {
    public boolean injectKey(byte[] key); // can throw InjectionFailureException
}
