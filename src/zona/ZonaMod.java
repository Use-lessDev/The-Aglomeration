package zona;
import arc.util.Log;
import mindustry.mod.Mod;
public class ZonaMod extends Mod {
    public ZonaMod() { Log.info("Zona Mod carregado."); }
    @Override public void loadContent() {
        Log.info("Carregando conteudo do Zona Mod...");
        ZonaBlocks.load();
        Constructions.load();
    }
}
