package DeviceOnboarding.Commands;

import DeviceOnboarding.*;
import DeviceOnboarding.MockKeyInjector.InjectionFailureException;

public class KeyCommand extends Command{

    public KeyCommand(MockDeviceDB mockDB, MockKeyInjector mockKeyInjector, MessagingTool messagingTool) {
        super(mockDB, null, mockKeyInjector, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);
        byte key[] = new byte[128];

        try {
            if (mockKeyInjector.injectKey(key)) {
                deviceInfo.injectKey(key);
            } else {
                return messagingTool.warningOutputString(deviceInfo, "KEY INJECTION FAILED");
            }
        } catch (InjectionFailureException e) {
            deviceInfo.injectionFailure();

            return messagingTool.errorOutputString(deviceInfo, "(CATASTROPHIC) KEY INJECTION FAILED");
        }

        return messagingTool.outputString(deviceInfo, "KEY INJECTED");
    }
    
}
