package DeviceOnboarding;

import java.util.Arrays;

public class DeviceInfo {
    private String serialNumber = null;
    private String boxReference = null;
    private String crateReference = null;
    private DamageRating damage = null;
    private SIMCardInfo simCardInfo = null;
    private boolean isFlashed = false;
    private byte key[];
    private boolean isSentForRepack = false;
    private WarehouseInfo warehouseInfo = null;
    private DeviceState currentState;

    public DeviceInfo() {
        this.currentState = DeviceState.DEVICE_RECEIVED;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBoxReference() {
        return boxReference;
    }

    public String getCrateReference() {
        return crateReference;
    }

    public DamageRating getDamage() {
        return damage;
    }

    public SIMCardInfo getSIMCardInfo() {
        return simCardInfo;
    }

    public byte[] getKey() {
        return key;
    }

    public WarehouseInfo getWarehouseInfo() {
        return warehouseInfo;
    }

    public DeviceState getCurrentState() {
        return currentState;
    }

    public boolean isFlashed() {
        return isFlashed;
    }

    public boolean isSentForRepack() {
        return isSentForRepack;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;

        currentState = DeviceState.SERIAL_NUMBER_RECORDED;
    }

    public void setDeliveryInfo(String boxReference, String crateReference) {
        this.boxReference = boxReference;
        this.crateReference = crateReference;

        currentState = DeviceState.DELIVERY_INFO_RECORDED;
    }

    public void setDamage(DamageRating damage) {
        this.damage = damage;

        currentState = DeviceState.DAMAGE_RECORDED;
    }

    public void setSIMCard(SIMCardInfo simCard) {
        this.simCardInfo = simCard;

        currentState = DeviceState.SIM_INSERTED_AND_RECORDED;
    }

    public void flashDevice() {
        this.isFlashed = true;

        currentState = DeviceState.FLASHED;
    }

    public void injectKey(byte[] key) {
        this.key = key;

        currentState = DeviceState.KEY_INJECTED;
    }

    public void setWarehouse(WarehouseInfo warehouseInfo) {
        this.warehouseInfo = warehouseInfo;
    }

    @Override
    public String toString() {
        String out = "DEVICE {" + serialNumber + "}:";

        out += "\n\tBOX: " + boxReference;
        out += "\n\tCRATE: " + crateReference;
        out += "\n\tDAMAGE: " + damage;
        out += "\n\tSIM CARD:";
        out += "\n\t\tSNN: " + simCardInfo.getSNN();
        out += "\n\t\tIMSI: " + simCardInfo.getIMSI();
        out += "\n\t\tIMEI: " + simCardInfo.getIMEI();
        out += "\n\tFLASHED: " + isFlashed;
        out += "\n\tKEY: " + Arrays.toString(key);
        out += "\n\tSENT FOR REPACK: " + isSentForRepack;
        out += "\n\tWAREHOUSE:\n\t\t" + warehouseInfo;
        out += "\n\tSTATE: " + currentState + "\n";

        return out;
    }
}
