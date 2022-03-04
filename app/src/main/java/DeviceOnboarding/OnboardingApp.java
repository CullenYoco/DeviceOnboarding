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

    public String processRequest(String requestString) {
        return handleRequest(requestString.split(" "));
    }

    private String handleRequest(String[] requestArgs) {
        String command = requestArgs[0];
        Command commandObject = null;

        if (command.equals("/h") || command.equals("/help")) {
            return messagingTool.helpOutputString(commandMap);
        }

        try {
            if (commandMap.containsKey(command)) {
                commandObject = commandMap.get(command);
                return commandObject.runCommand(requestArgs);
            }
        } catch (IllegalArgumentException e) {
            return messagingTool.errorOutputString("ILLEGAL ARGUMENTS\n\tEXPECTED: " + commandObject);
        } catch (NoSuchElementException e) {
            return messagingTool.errorOutputString("DEVICE {" + requestArgs[1] + "}: Device NOT Found");
        } catch (IllegalStateException e) {
            return messagingTool.transitionExceptionOutputString(mockDB.getDevice(requestArgs[1]), e);
        }

        return messagingTool.unrecognizedCommandOutputString(command);
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
}
