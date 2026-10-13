package zona;
import mindustry.world.Block;
public class ZonaBlocks {
    public static Block zona;
    public static Block[] built;
    public static void load() {
        zona = new ZonaBlock("zona");
        built = new Block[9];
        for (int i = 1; i <= 8; i++) built[i] = new BuiltZoneBlock("built-zona-" + i, i);
    }
}
