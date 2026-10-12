package theAglomeration;

import arc.Core;
import mindustry.mod.Mod;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class TheAglomeration extends Mod {
    public static final Block[] houses = new Block[9];

    @Override
    public void loadContent() {
        for (int size = 1; size <= 8; size++) {
            final int houseSize = size;
            Block house = new Block("zdcdc-house-" + size) {
                @Override
                public void load() {
                    super.load();
                    // Temporary visual placeholder: reuse a vanilla Mindustry sprite.
                    region = Core.atlas.find("copper-wall");
                }
            };

            house.size = houseSize;
            house.solid = true;
            house.destructible = true;
            house.health = 100 * houseSize * houseSize;
            house.buildVisibility = BuildVisibility.hidden;
            house.requirements = new mindustry.type.ItemStack[0];
            houses[size] = house;
        }

    }
}
