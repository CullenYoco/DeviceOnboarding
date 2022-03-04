package DeviceOnboarding;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import DeviceOnboarding.Commands.*;

public class OnboardingApp {
    private MockDeviceDB mockDB = new MockDeviceDB();
    private MockDeviceFlash mockDeviceFlash;
    private MockKeyInjector mockKeyInjector;
    private HashMap<String, Command> commandMap= new HashMap<String, Command>();
    private MessagingTool messagingTool = new MessagingTool();

    public static void main(String[] args) {
        OnboardingApp oa = new OnboardingApp();
        Scanner scanner = new Scanner(System.in);

        System.out.println(MessagingTool.welcomeMessageString());

        while (scanner.hasNextLine()) {
            String requestString = scanner.nextLine();

            if (requestString.equals("/quit") || requestString.equals("/q")) {
                break;
            }

            System.out.println(oa.processRequest(requestString));
        }

        scanner.close();
    }

    public OnboardingApp() {
        this(new MockDeviceFlash(0), new MockKeyInjector(0));
    }

    public OnboardingApp(MockDeviceFlash mockDeviceFlash, MockKeyInjector mockKeyInjector) {
        this.mockDeviceFlash = mockDeviceFlash;
        this.mockKeyInjector = mockKeyInjector;

        initCommandMap();
    }

    private void initCommandMap() {
        commandMap.put("/add", new AddDeviceCommand(mockDB, messagingTool));
        commandMap.put("/delivery", new DeliveryCommand(mockDB, messagingTool));
        commandMap.put("/damage", new DamageCommand(mockDB, messagingTool));
        commandMap.put("/sim", new SimCommand(mockDB, messagingTool));
        commandMap.put("/flash", new FlashCommand(mockDB, mockDeviceFlash, messagingTool));
        commandMap.put("/key", new KeyCommand(mockDB, mockKeyInjector, messagingTool));
        commandMap.put("/repack", new RepackCommand(mockDB, messagingTool));
        commandMap.put("/store", new StoreCommand(mockDB, messagingTool));
        commandMap.put("/info", new InfoCommand(mockDB));
    }

    public String processRequest(String requestString) {
        String splitRequest[] = requestString.split(" ");

        if (splitRequest.length < 2) {
            if (splitRequest[0].equals("/h") || splitRequest[0].equals("/help")) {
                return messagingTool.helpOutputString();
            }

            return messagingTool.illegalRequestOutputString();
        }

        return handleRequest(splitRequest);
    }

    private String handleRequest(String[] requestArgs) {
        String command = requestArgs[0];
        String serialNumber = requestArgs[1];

        try {
            if (commandMap.containsKey(command)) {
                return commandMap.get(command).runCommand(requestArgs);
            }
        } catch (NoSuchElementException e) {
            return messagingTool.errorOutputString("DEVICE {" + serialNumber + "}: Device NOT Found");
        } catch (IllegalStateException e) {
            return messagingTool.transitionExceptionOutputString(mockDB.getDevice(serialNumber), e);
        }

        return messagingTool.unrecognizedCommandOutputString(command);
    }
}
