package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class DeliveryCommand extends Command{

    public DeliveryCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
            super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        if (requestArgs.length < 4) {
            throw new IllegalArgumentException();
        }

        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        deviceInfo.setDeliveryInfo(requestArgs[2], requestArgs[3]);

        return messagingTool.outputString(deviceInfo, "DELIVERY INFO ADDED");
    }

    @Override
    public String toString() {
        return "/delivery <SerialNo> <BoxRef> <CrateRef>";
    }
    
}
