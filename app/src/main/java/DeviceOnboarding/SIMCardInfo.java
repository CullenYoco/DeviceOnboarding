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

    public String getIMEI() {
        return IMEI;
    }

    public String getIMSI() {
        return IMSI;
    }

    public String getSNN() {
        return SNN;
    }

    @Override
    public String toString() {
        String out = "SIM CARD:";
        
        out += "\n\tSNN: " + SNN;
        out += "\n\tIMSI: " + IMSI;
        out += "\n\tIMEI: " + IMEI;
        
        return out;
    }
}
