package DeviceOnboarding;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import DeviceOnboarding.MockKeyInjector.InjectionFailureException;

public class MockKeyInjectorTest {
    
    @Test
    public void workingInjectorTest() {
        MockKeyInjector mki = new MockKeyInjector(0);

        assertTrue(mki.injectKey(new byte[128]));
    }

    @Test
    public void failedButNotExceptionTest() {
        MockKeyInjector mki = new MockKeyInjector(1);
        assertFalse(mki.injectKey(new byte[128]));

        mki = new MockKeyInjector(5);
        assertFalse(mki.injectKey(new byte[128]));
    }

    @Test
    public void exceptionFailureTest() {
        assertThrows(InjectionFailureException.class, () -> {
            MockKeyInjector mki = new MockKeyInjector(-1);
            mki.injectKey(new byte [128]);
        });

        assertThrows(InjectionFailureException.class, () -> {
            MockKeyInjector mki = new MockKeyInjector(-5);
            mki.injectKey(new byte [128]);
        });
    }
}
