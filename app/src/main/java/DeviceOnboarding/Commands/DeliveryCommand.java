package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class DeliveryCommand extends Command{

    public DeliveryCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
            super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        deviceInfo.setDeliveryInfo(requestArgs[2], requestArgs[3]);

        return messagingTool.outputString(deviceInfo, "DELIVERY INFO ADDED");
    }
    
}
