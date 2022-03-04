package DeviceOnboarding.Commands;

import DeviceOnboarding.*;
import DeviceOnboarding.DeviceInfo.IllegalSerialNumberException;

public class AddDeviceCommand extends Command {

    public AddDeviceCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
        super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        if (requestArgs.length < 2) {
            throw new IllegalArgumentException();
        }

        DeviceInfo deviceInfo;
        
        try {
            deviceInfo = new DeviceInfo(requestArgs[1]);
        } catch (IllegalSerialNumberException e) {
            return messagingTool.errorOutputString("ILLEGAL SERIAL NUMBER: " + requestArgs[1]);
        }

        mockDB.addDevice(deviceInfo);
        
        return messagingTool.outputString(deviceInfo, "DEVICE ADDED");
    }

    @Override
    public String toString() {
        return "/add <SerialNo>";
    }

}
