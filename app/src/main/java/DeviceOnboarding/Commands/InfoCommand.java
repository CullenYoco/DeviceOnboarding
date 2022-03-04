package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class InfoCommand extends Command {

    public InfoCommand(MockDeviceDB mockDB) {
        super(mockDB, null);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        if (requestArgs.length < 2) {
            throw new IllegalArgumentException();
        }

        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        return deviceInfo.toString();
    }

    @Override
    public String toString() {
        return "/info <SerialNo>";
    }
    
}
