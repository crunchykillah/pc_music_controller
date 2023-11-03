import java.io.IOException;

//now it works with nircmd
public class ComputerControl {
    private String nirPath;
    public ComputerControl(String nirPath) {
        this.nirPath = nirPath;
    }
    public void setSystemVolume(int volume) {
        if(volume < 0 || volume > 100) {
            throw new RuntimeException("Error: " + volume + " is not a valid number. Choose a number between 0 and 100");
        }
        else {
            double endVolume = 655.35 * volume;
            Runtime rt = Runtime.getRuntime();
            Process pr;
            try {
                pr = rt.exec( nirPath + " setsysvolume " + endVolume);
                pr = rt.exec( nirPath + " mutesysvolume 0");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void muteVolume() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec( nirPath + " mutesysvolume 1");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void unMuteVolume() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec( nirPath + " mutesysvolume 0");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void maxVolume() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec( nirPath + " setsysvolume 65535");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void computerShotDown() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec( nirPath + " exitwin poweroff");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void standByMode() {
        Runtime rt = Runtime.getRuntime();
        Process pr;
        try {
            pr = rt.exec( nirPath + " standby");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
