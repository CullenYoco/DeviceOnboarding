package DeviceOnboarding.Commands;

import DeviceOnboarding.*;

public class StoreCommand extends Command{

    public StoreCommand(MockDeviceDB mockDB, MessagingTool messagingTool) {
        super(mockDB, messagingTool);
    }

    @Override
    public String runCommand(String[] requestArgs) {
        if (requestArgs.length < 9) {
            throw new IllegalArgumentException();
        }

        DeviceInfo deviceInfo = mockDB.getDevice(requestArgs[1]);
        WarehouseInfo warehouseInfo;
        
        try {
            warehouseInfo = new WarehouseInfo(Integer.parseInt(requestArgs[2]), Integer.parseInt(requestArgs[3]),
                                              Integer.parseInt(requestArgs[4]), Integer.parseInt(requestArgs[5]),
                                              Integer.parseInt(requestArgs[6]), stringToSegmentPosition(requestArgs[7], requestArgs[8]));
        } catch (IllegalArgumentException e) {
            return messagingTool.errorOutputString(deviceInfo, "SEGMENT POSITION INVALID {" + requestArgs[7] + ", " + requestArgs[8] + "}" );
        }

        deviceInfo.setWarehouse(warehouseInfo);

        return messagingTool.outputString(deviceInfo, "DEVICE STORED IN WAREHOUSE");
    }

    private SegmentPosition stringToSegmentPosition(String xSegmentPos, String ySegmentPos) {
        return SegmentPosition.valueOf((xSegmentPos + "_" + ySegmentPos).toUpperCase());
    }

    @Override
    public String toString() {
        return "/store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>";
    }
    
}
