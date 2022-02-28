package DeviceOnboarding;

public class SIMCardInfo {
    private String SNN;
    private String IMSI;
    private String IMEI;

    public SIMCardInfo(String SNN, String IMSI, String IMEI) {
        this.SNN = SNN;
        this.IMSI = IMSI;
        this.IMEI = IMEI;
    }

    public String getSNN() {
        return SNN;
    }

    public String getIMSI() {
        return IMSI;
    }

    public String getIMEI() {
        return IMEI;
    }
}
