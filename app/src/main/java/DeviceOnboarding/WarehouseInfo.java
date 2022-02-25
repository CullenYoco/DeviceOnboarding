package DeviceOnboarding;

public class WarehouseInfo {
    private int warehouseNumber;
    private int sectionNumber;
    private int rowNumber;
    private int shelfNumber;
    private int segmentNumber;
    private SegmentPosition segmentPosition;

    public WarehouseInfo(int warehouseNumber, int sectionNumber, int rowNumber, int shelfNumber, int segmentNumber, SegmentPosition segmentPosition) {
        this.warehouseNumber = warehouseNumber;
        this.sectionNumber = sectionNumber;
        this.rowNumber = rowNumber;
        this.shelfNumber = shelfNumber;
        this.segmentNumber = segmentNumber;
        this.segmentPosition = segmentPosition;
    }
}
