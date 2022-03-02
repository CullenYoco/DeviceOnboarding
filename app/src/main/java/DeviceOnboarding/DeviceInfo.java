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
    private DeviceState currentState = DeviceState.DEVICE_RECEIVED;

    public DeviceInfo(String serialNumber) {
        if (!serialNumber.matches("[0-9]{4}-[0-9]{3}[0-9xX]")) {
            throw new IllegalSerialNumberException();
        }

        this.serialNumber = serialNumber;

        currentState = DeviceState.SERIAL_NUMBER_RECORDED;
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

    public void setDeliveryInfo(String boxReference, String crateReference) {
        if (currentState != DeviceState.SERIAL_NUMBER_RECORDED) {
            throw new IllegalStateException(DeviceState.DELIVERY_INFO_RECORDED + "");
        }

        this.boxReference = boxReference;
        this.crateReference = crateReference;

        currentState = DeviceState.DELIVERY_INFO_RECORDED;
    }

    public void setDamage(DamageRating damage) {
        if (currentState != DeviceState.DELIVERY_INFO_RECORDED) {
            throw new IllegalStateException(DeviceState.DAMAGE_RECORDED + "");
        }

        this.damage = damage;

        currentState = DeviceState.DAMAGE_RECORDED;
    }

    public void setSIMCard(SIMCardInfo simCard) {
        if (currentState != DeviceState.DAMAGE_RECORDED) {
            throw new IllegalStateException(DeviceState.SIM_INSERTED_AND_RECORDED + "");
        }

        this.simCardInfo = simCard;

        currentState = DeviceState.SIM_INSERTED_AND_RECORDED;
    }

    public void flashDevice() {
        if (currentState != DeviceState.SIM_INSERTED_AND_RECORDED) {
            throw new IllegalStateException(DeviceState.FLASHED + "");
        }

        this.isFlashed = true;

        currentState = DeviceState.FLASHED;
    }

    public void injectKey(byte[] key) {
        if (currentState != DeviceState.FLASHED) {
            throw new IllegalStateException(DeviceState.KEY_INJECTED + "");
        }

        this.key = key;

        currentState = DeviceState.KEY_INJECTED;
    }

    public void sendForRepack() {
        if (currentState != DeviceState.KEY_INJECTED) {
            throw new IllegalStateException(DeviceState.SENT_FOR_REPACK + "");
        }

        this.isSentForRepack = true;

        currentState = DeviceState.SENT_FOR_REPACK;
    }

    public void setWarehouse(WarehouseInfo warehouseInfo) {
        if (currentState != DeviceState.SENT_FOR_REPACK) {
            throw new IllegalStateException(DeviceState.STORED_IN_WAREHOUSE + "");
        }

        this.warehouseInfo = warehouseInfo;

        currentState = DeviceState.STORED_IN_WAREHOUSE;
    }

    public void flashFailure() {
        currentState = DeviceState.SEVERE_FLASH_FAILURE;
    }

    public void injectionFailure() {
        currentState = DeviceState.SEVERE_KEY_INJECTION_FAILURE;
    }

    @Override
    public String toString() {
        String out = "===DEVICE {" + serialNumber + "}===";

        out += "\nBOX: " + boxReference;
        out += "\nCRATE: " + crateReference;
        out += "\nDAMAGE: " + damage;
        out += "\n" + simCardInfo;
        out += "\nFLASHED: " + isFlashed;
        out += "\nKEY: " + Arrays.toString(key);
        out += "\nSENT FOR REPACK: " + isSentForRepack;
        out += "\n" + warehouseInfo;
        out += "\nSTATE: " + currentState;

        return out;
    }

    public class IllegalSerialNumberException extends RuntimeException{}
}
