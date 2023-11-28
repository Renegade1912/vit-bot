import de.vit.officebot.OfficeBot;
import de.vitbund.netmaze.connector.NetMazeConnector;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Connector {
    public static void main(String[] args) {
        OfficeBot bot = new OfficeBot();
        NetMazeConnector connector = new NetMazeConnector(bot);

        connector.showConnectionSettingsDialog();

        connector.play();
    }
}