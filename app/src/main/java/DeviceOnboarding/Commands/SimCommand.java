package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class SimCommand extends Command{

    public SimCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
        super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);
        SIMCardInfo simCard = new SIMCardInfo(requestArgs[2], requestArgs[3], requestArgs[4]);

        deviceInfo.setSIMCard(simCard);

        return messagingTool.outputString(deviceInfo, "SIM ADDED");
    }
    
}
