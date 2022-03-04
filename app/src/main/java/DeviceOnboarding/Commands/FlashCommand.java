package DeviceOnboarding.Commands;

import DeviceOnboarding.*;
import DeviceOnboarding.MockDeviceFlash.FlashFailureException;

public class FlashCommand extends Command{

    public FlashCommand(MockDeviceDB mockDB, MockDeviceFlash mockDeviceFlash, MessagingTool messagingTool) {
        super(mockDB, mockDeviceFlash, null, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        if (requestArgs.length < 2) {
            throw new IllegalArgumentException();
        }

        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        try {
            if (mockDeviceFlash.flashDevice()) {
                deviceInfo.flashDevice();
            } else {
                return messagingTool.warningOutputString(deviceInfo, "DEVICE FLASH FAILED");
            }
        } catch (FlashFailureException e) {
            deviceInfo.flashFailure();
 
            return messagingTool.errorOutputString(deviceInfo, "(CATASTROPHIC) DEVICE FLASH FAILED");
        }
 
        return messagingTool.outputString(deviceInfo, "DEVICE FLASHED");
    }

    @Override
    public String toString() {
        return "/flash <SerialNo>";
    }
    
}
