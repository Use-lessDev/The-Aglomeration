package zona;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.ItemStack;
public class Constructions {
    public static Seq<Construction> all = new Seq<>();
    public static void load() {
        all.clear();
        all.add(new Construction("armazem-pequeno", "Armazem Pequeno", 1, ItemStack.with(Items.copper, 50, Items.graphite, 20), 8f, "Armazena ate 200 itens."));
        all.add(new Construction("torre-basica", "Torre Basica", 1, ItemStack.with(Items.copper, 40, Items.lead, 15), 10f, "Torre de defesa fraca."));
        all.add(new Construction("fabrica-basica", "Fabrica Basica", 2, ItemStack.with(Items.copper, 120, Items.graphite, 60, Items.silicon, 30), 25f, "Produz graphite."));
        all.add(new Construction("plataforma-defesa", "Plataforma de Defesa", 2, ItemStack.with(Items.copper, 100, Items.lead, 40, Items.graphite, 20), 20f, "Muros e torre media."));
        all.add(new Construction("fabrica-media", "Fabrica Media", 3, ItemStack.with(Items.copper, 200, Items.silicon, 100, Items.titanium, 50), 45f, "Produz silicon."));
        all.add(new Construction("centro-energia", "Centro de Energia", 3, ItemStack.with(Items.lead, 150, Items.graphite, 80, Items.silicon, 40), 40f, "Gera energia."));
        all.add(new Construction("complexo-industrial", "Complexo Industrial", 4, ItemStack.with(Items.copper, 300, Items.silicon, 150, Items.titanium, 80), 70f, "Producao avancada."));
        all.add(new Construction("quartel", "Quartel", 4, ItemStack.with(Items.copper, 250, Items.lead, 100, Items.graphite, 60), 60f, "Produz unidades leves."));
        all.add(new Construction("hub-producao", "Hub de Producao", 5, ItemStack.with(Items.copper, 400, Items.silicon, 200, Items.titanium, 100, Items.thorium, 50), 100f, "Grande fabrica multi-produto."));
        all.add(new Construction("centro-pesquisa", "Centro de Pesquisa", 6, ItemStack.with(Items.silicon, 500, Items.titanium, 200, Items.thorium, 100), 140f, "Centro de pesquisa avancada."));
        all.add(new Construction("fortaleza", "Fortaleza", 7, ItemStack.with(Items.copper, 600, Items.silicon, 300, Items.titanium, 150, Items.thorium, 80), 180f, "Defesa pesada e producao."));
        all.add(new Construction("estrutura-mega", "Estrutura Mega", 8, ItemStack.with(Items.copper, 800, Items.silicon, 400, Items.titanium, 200, Items.thorium, 120, Items.surgeAlloy, 50), 240f, "Construcao final poderosa."));
    }
    public static Seq<Construction> forSize(int size) { return all.select(c -> c.size == size); }
    public static class Construction {
        public String id, name, description;
        public int size;
        public ItemStack[] cost;
        public float timeSeconds;
        public Construction(String id, String name, int size, ItemStack[] cost, float timeSeconds, String description) {
            this.id=id; this.name=name; this.size=size; this.cost=cost; this.timeSeconds=timeSeconds; this.description=description;
        }
    }
}
