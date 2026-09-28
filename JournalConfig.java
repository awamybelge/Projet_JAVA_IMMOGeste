
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public final class JournalConfig {

    private JournalConfig() {
    }

    /** Ajoute un gestionnaire de fichier au logger racine. */
    public static void activerJournalFichier() {
        Logger racine = Logger.getLogger("");
        try {
            FileHandler fichier = new FileHandler("immogest.log", true);
            fichier.setFormatter(new SimpleFormatter());
            racine.addHandler(fichier);
        } catch (IOException e) {
            racine.log(Level.SEVERE, "Impossible de créer le fichier de journal immogest.log", e);
        }
    }
}