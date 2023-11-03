import java.awt.*;
//Command Handler
public class ProcessCommand implements Runnable {
    private static ComputerControl computerControl;
    private String command;
    private String nirPath;
    public ProcessCommand(String command,ComputerControl computerControl) {
        this.command = command;
        this.computerControl = computerControl;
    }

    private void execTheCommand(String command) throws AWTException {
        String[] commandArray = command.split(";");
        if (commandArray[0].equals("/volume")) {
            computerControl.setSystemVolume(Integer.parseInt(commandArray[1]));
        }
        if (commandArray[0].equals("/mute")) {
            computerControl.muteVolume();
        }
        if (commandArray[0].equals("/unmute")) {
            computerControl.unMuteVolume();
        }
        if (commandArray[0].equals("/shutdown")) {
            computerControl.computerShotDown();
        }
        if (commandArray[0].equals("/standby")) {
            computerControl.standByMode();
        }

    }

    @Override
    public void run() {
        try {
            execTheCommand(command);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}
