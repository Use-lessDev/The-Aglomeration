package zona;
import arc.graphics.g2d.Draw;
import arc.scene.Element;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Stat;
public class ZonaBlock extends Block {
    public ZonaBlock(String name) {
        super(name);
        requirements(Category.effect, ItemStack.with(Items.copper, 10, Items.lead, 5));
        size=1; health=120; solid=true; update=true; configurable=true; destructible=true;
        buildVisibility=BuildVisibility.shown; category=Category.effect;
        description="Bloco de Zona. Coloque varios adjacentes para formar zonas de 1x1 ate 8x8.";
    }
    @Override public void setStats() { super.setStats(); stats.add(Stat.abilities, "Forma zonas de ate 8x8"); }
    public class ZonaBuild extends Building {
        public float constructProgress=-1f, constructTotal=0f;
        public Constructions.Construction current;
        @Override public void updateTile() {
            if(constructProgress>=0f) { constructProgress += Time.delta/(constructTotal*60f); if(constructProgress>=1f) finishConstruction(); }
        }
        @Override public void buildConfiguration(Table table) {
            table.button(Icon.hammer, Styles.cleari, () -> {
                int size=Math.max(1, detectZoneSize()); showConstructionDialog(size);
            }).size(40f).tooltip("Abrir menu de construcoes");
        }
        public int detectZoneSize() {
            int max=1;
            for(int s=2;s<=8 && isSquareOfSize(s);s++) max=s;
            return max;
        }
        private boolean isSquareOfSize(int s) {
            for(int ox=0;ox<s;ox++) for(int oy=0;oy<s;oy++) {
                Tile t=tile.nearby(ox,oy);
                if(t==null || !(t.build instanceof ZonaBlock.ZonaBuild)) return false;
            }
            return true;
        }
        private void showConstructionDialog(int size) {
            BaseDialog dialog=new BaseDialog("Zona "+size+"x"+size);
            dialog.cont.add("[accent]Construcoes disponiveis para "+size+"x"+size).pad(10f).row();
            Seq<Constructions.Construction> list=Constructions.forSize(size);
            if(list.isEmpty()) dialog.cont.add("Nenhuma construcao disponivel para este tamanho.").pad(20f);
            else for(Constructions.Construction c:list) {
                Table row=new Table();
                row.add("[white]"+c.name).left().growX().row();
                row.add("[lightgray]"+c.description).left().growX().row();
                StringBuilder cost=new StringBuilder("[gray]Custo: ");
                for(ItemStack stack:c.cost) cost.append(stack.item.emoji()).append(" ").append(stack.amount).append("  ");
                row.add(cost.toString()).left().growX().row();
                row.add("[gray]Tempo: "+(int)c.timeSeconds+"s").left();
                dialog.cont.add(row).growX().pad(8f).row();
                dialog.cont.button("Construir", () -> { startConstruction(c); dialog.hide(); }).size(140f,40f).padBottom(12f).row();
            }
            dialog.addCloseButton(); dialog.show();
        }
        private void startConstruction(Constructions.Construction c) {
            if(!Vars.state.rules.infiniteResources) {
                for(ItemStack stack:c.cost) if(!team.items().has(stack.item,stack.amount)) {
                    Vars.ui.showInfo("Recursos insuficientes: "+stack.item.localizedName); return;
                }
                for(ItemStack stack:c.cost) team.items().remove(stack.item,stack.amount);
            }
            current=c; constructTotal=Math.max(1f,c.timeSeconds); constructProgress=0f;
        }
        private void finishConstruction() {
            if(current==null) return;
            int s=current.size;
            Block target=ZonaBlocks.built[s];
            if(target==null) { constructProgress=-1f; current=null; return; }
            Tile origin=tile;
            for(int ox=0;ox<s;ox++) for(int oy=0;oy<s;oy++) {
                Tile t=origin.nearby(ox,oy);
                if(t!=null && t.build!=null) t.build.tile.remove();
            }
            origin.setBlock(target,team);
            Building building=origin.build;
            if(building instanceof BuiltZoneBlock.BuiltZoneBuild) ((BuiltZoneBlock.BuiltZoneBuild)building).sourceConstruction=current;
            constructProgress=-1f; current=null;
        }
        @Override public void draw() {
            super.draw();
            if(constructProgress>=0f) {
                Draw.z(35f); Draw.color(0.2f,0.2f,0.2f,0.8f);
                float w=8f*0.8f, h=3f;
                Draw.rect("white",x,y+8f,h==0?0:4f,w,h);
                Draw.color(0.2f,0.9f,0.3f,0.9f);
                Draw.rect("white",x-w/2f+w*constructProgress/2f,y+4f,w*constructProgress,h);
                Draw.reset();
            }
        }
        @Override public boolean onConfigureBuildTapped(Building other) { return this==other; }
    }
}
