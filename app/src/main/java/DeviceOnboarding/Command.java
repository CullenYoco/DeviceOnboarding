package DeviceOnboarding;

import java.util.NoSuchElementException;

public abstract class Command {
    protected MockDeviceDB mockDB;
    protected MockDeviceFlash mockDeviceFlash;
    protected MockKeyInjector mockKeyInjector;
    protected MessagingTool messagingTool;

    public Command(MockDeviceDB mockDB, MessagingTool messagingTool) {
        this(mockDB, null, null, messagingTool);
    }

    public Command(MockDeviceDB mockDB, MockDeviceFlash mockDeviceFlash, MockKeyInjector mockKeyInjector, MessagingTool messagingTool) {
        this.mockDB = mockDB;
        this.mockDeviceFlash = mockDeviceFlash;
        this.mockKeyInjector = mockKeyInjector;
        this.messagingTool = messagingTool;
    }

    public abstract String runCommand(String[] requestArgs) throws NoSuchElementException, IllegalStateException;
}
