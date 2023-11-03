import java.io.OutputStream;
import javax.swing.JTextArea;

//Writes data from console
public class ConsoleOutputStream extends OutputStream {
    private JTextArea textArea;

    public ConsoleOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
