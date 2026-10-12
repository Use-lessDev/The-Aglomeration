package theAglomeration;

import arc.Core;
import mindustry.content.Items;
import mindustry.mod.Mod;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.type.Category;

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

        // Register this block directly in Java so Mindustry cannot silently
        // fall back to a normal Block when parsing a custom HJSON type.
        PercentBuildBase zone = new PercentBuildBase("zdcdc-construction-zone");
        zone.localizedName = "ZDCDC Construction Zone";
        zone.description = "Connect these blocks into a rectangular zone, then choose a house size.";
        zone.category = Category.effect;
        zone.buildVisibility = BuildVisibility.shown;
        zone.alwaysUnlocked = true;
        zone.size = 1;
        zone.solid = false;
        zone.destructible = true;
        zone.update = true;
        zone.configurable = true;
        zone.saveConfig = true;
        zone.hasItems = true;
        zone.itemCapacity = 2000;
        zone.requirements = ItemStack.with(Items.sand, 25);
    }
}
