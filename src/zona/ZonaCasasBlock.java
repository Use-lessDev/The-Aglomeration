package zona;

import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

/** Zona de construção de casas - textura: zona-casas.png */
public class ZonaCasasBlock extends Block {
    public ZonaCasasBlock(String name) {
        super(name);
        requirements(Category.effect, ItemStack.with(Items.copper, 15, Items.lead, 8));
        size = 1;
        health = 150;
        solid = true;
        update = true;
        configurable = false;
        destructible = true;
        buildVisibility = BuildVisibility.shown;
        category = Category.effect;
        description = "Zona de construção de casas. Use para delimitar áreas residenciais.";
    }
}
