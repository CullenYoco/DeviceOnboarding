package DeviceOnboarding;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OnboardingAppTest {
    @Test void appHasAGreeting() {
        OnboardingApp classUnderTest = new OnboardingApp();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }
}
