package zona;

import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

/** Zona comercial / fazenda de comida - textura: zona-comercial.png */
public class ZonaComercialBlock extends Block {
    public ZonaComercialBlock(String name) {
        super(name);
        requirements(Category.effect, ItemStack.with(Items.copper, 20, Items.graphite, 10));
        size = 1;
        health = 160;
        solid = true;
        update = true;
        configurable = false;
        destructible = true;
        buildVisibility = BuildVisibility.shown;
        category = Category.effect;
        description = "Zona comercial e de fazenda de comida. Use para delimitar áreas de produção e comércio.";
    }
}
