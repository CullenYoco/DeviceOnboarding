package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class RepackCommand extends Command{

    public RepackCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
        super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        deviceInfo.sendForRepack();

        return messagingTool.outputString(deviceInfo, "DEVICE SENT FOR REPACK");
    }
    
}
