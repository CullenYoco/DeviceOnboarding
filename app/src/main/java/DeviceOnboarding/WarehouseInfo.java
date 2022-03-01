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

    public int getWarehouseNumber() {
        return warehouseNumber;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getShelfNumber() {
        return shelfNumber;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public SegmentPosition getSegmentPosition() {
        return segmentPosition;
    }

    @Override
    public String toString() {
        String out = "WAREHOUSE: ";

        out += "\n\tWAREHOUSE NUMBER: " + warehouseNumber;
        out += "\n\tSECTION NUMBER: " + sectionNumber;
        out += "\n\tROW NUMBER: " + rowNumber;
        out += "\n\tSHELF NUMBER: " + shelfNumber;
        out += "\n\tSEGMENT NUMBER: " + segmentNumber;
        out += "\n\tSEGMENT POSITION: " + segmentPosition;

        return out;
    }
}
