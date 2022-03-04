package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class DamageCommand extends Command {

    public DamageCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
        super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);

        try {
            deviceInfo.setDamage(stringToDamageRating(requestArgs[2]));
        } catch (IllegalArgumentException e) {
            return messagingTool.errorOutputString(deviceInfo, "DAMAGE STATUS INVALID {" + requestArgs[2] + "}" );
        }
        
        if (deviceInfo.getCurrentState() == DeviceState.DEVICE_DAMAGED) {
            return messagingTool.errorOutputString(deviceInfo, "DAMAGE TOO HIGH");
        }

        return messagingTool.outputString(deviceInfo, "DAMAGE ADDED");
    }
    
    private DamageRating stringToDamageRating(String damage) {
        return DamageRating.valueOf(damage.toUpperCase());
    }
}
