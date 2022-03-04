package DeviceOnboarding;

public class MessagingTool {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    public String errorOutputString(String message) {
        return red("ERROR") + " -> " + message;
    }

    public String errorOutputString(DeviceInfo deviceInfo, String message) {
        return red("ERROR") + " -> " + outputString(deviceInfo, message);
    }

    public String helpOutputString() {
        return "=== HELP ===\n" +
                "1) /add <SerialNo>\n" +
                "2) /delivery <SerialNo> <BoxRef> <CrateRef>\n" +
                "3) /damage <SerialNo> <DamageRating>\n" +
                "4) /sim <SerialNo> <SNN> <IMSI> <IMEI>\n" +
                "5) /flash <SerialNo>\n" +
                "6) /key <SerialNo>\n" +
                "7) /repack <SerialNo>\n" +
                "8) /store <SerialNo> <WarehouseNo> <SectionNo> <RowNo> <ShelfNo> <SegmentNo> <YSegmentPos> <XSegmentPos>";
    }

    public String illegalRequestOutputString() {
        return red("ERROR") + " -> ILLEGAL REQUEST FORMAT";
    }

    public String outputString(DeviceInfo deviceInfo, String message) {
        return "DEVICE {" + deviceInfo.getSerialNumber() + "}: " + message + "\n\tSTATUS: " + deviceInfo.getCurrentState();
    }

    private String red(String string) {
        return ANSI_RED + string + ANSI_RESET;
    }

    public String transitionExceptionOutputString(DeviceInfo deviceInfo, IllegalStateException e) {
        return warningOutputString(deviceInfo, "ILLEGAL STATE TRANSITION (" + deviceInfo.getCurrentState() + " -> " + e.getMessage() + ")");
    }

    public String unrecognizedCommandOutputString(String command) {
        return illegalRequestOutputString() + "\n\tUNRECOGNIZED COMMAND: " + command;
    }

    public String warningOutputString(DeviceInfo deviceInfo, String message) {
        return yellow("WARNING") + " -> " + outputString(deviceInfo, message);
    }

    public static String welcomeMessageString() {
        return "=== Welcome to Device Onboarding ===\nInput a command: (/h or /help for Help) (/q or /quit to Quit)";
    }

    private String yellow(String string) {
        return ANSI_YELLOW + string + ANSI_RESET;
    }
}
