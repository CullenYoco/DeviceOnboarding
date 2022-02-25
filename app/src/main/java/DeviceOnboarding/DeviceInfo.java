package DeviceOnboarding;

public class DeviceInfo {
    private String serialNumber = null;
    private String boxReference = null;
    private String crateReference = null;
    private DamageRating damage = null;
    private SIMCardInfo simCardInfo = null;
    private boolean isFlashed = false;
    private boolean isKeyInjected = false;
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

    public WarehouseInfo getWarehouseInfo() {
        return warehouseInfo;
    }

    public DeviceState getCurrentState() {
        return currentState;
    }

    public boolean isFlashed() {
        return isFlashed;
    }

    public boolean isKeyInjected() {
        return isKeyInjected;
    }

    public boolean isSentForRepack() {
        return isSentForRepack;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setBoxReference(String boxReference) {
        this.boxReference = boxReference;
    }

    public void setCrateReference(String crateReference) {
        this.crateReference = crateReference;
    }

    public void setDamage(DamageRating damage) {
        this.damage = damage;
    }

    public void setSIMCard(SIMCardInfo simCard) {
        this.simCardInfo = simCard;
    }

    public void setWarehouse(WarehouseInfo warehouseInfo) {
        this.warehouseInfo = warehouseInfo;
    }
}
