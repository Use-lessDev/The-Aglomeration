package zona;
import arc.graphics.g2d.Draw;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.content.Items;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;
public class BuiltZoneBlock extends Block {
    public final int zoneSize;
    public BuiltZoneBlock(String name,int size) {
        super(name); zoneSize=size; this.size=size;
        requirements(Category.effect,ItemStack.with(Items.copper,1));
        health=200*size*size; solid=true; update=true; configurable=true; destructible=true;
        buildVisibility=BuildVisibility.hidden; category=Category.effect;
        description="Construcao de Zona "+size+"x"+size+". Desmontagem devolve 75% dos recursos.";
    }
    public class BuiltZoneBuild extends mindustry.gen.Building {
        public Constructions.Construction sourceConstruction;
        public float dismantleProgress=-1f, dismantleTotal=0f;
        @Override public void updateTile() {
            if(dismantleProgress>=0f) { dismantleProgress+=Time.delta/(Math.max(1f,dismantleTotal)*60f); if(dismantleProgress>=1f) finishDismantle(); }
        }
        @Override public void buildConfiguration(Table table) {
            table.button((Drawable)Icon.trash,Styles.cleari,() -> {
                if(sourceConstruction==null) { Vars.ui.showInfo("Sem dados de construcao original."); return; }
                showDismantleConfirm();
            }).size(40f).tooltip("Desmontar (75% de reembolso)");
        }
        private void showDismantleConfirm() {
            BaseDialog dialog=new BaseDialog("Desmontar");
            dialog.cont.add("Desmontar esta construcao?\nDevolve 75% dos recursos.").pad(20f).row();
            dialog.cont.button("Confirmar",() -> { startDismantle(); dialog.hide(); }).size(140f,40f).pad(10f);
            dialog.cont.button("Cancelar",dialog::hide).size(140f,40f).pad(10f);
            dialog.show();
        }
        private void startDismantle() { dismantleTotal=sourceConstruction.timeSeconds*0.8f; dismantleProgress=0f; }
        private void finishDismantle() {
            if(sourceConstruction==null) return;
            if(!Vars.state.rules.infiniteResources) for(ItemStack stack:sourceConstruction.cost) {
                int refund=Math.round(stack.amount*0.75f); if(refund>0) team.items().add(stack.item,refund);
            }
            Tile origin=tile; int s=BuiltZoneBlock.this.zoneSize;
            origin.remove();
            for(int ox=0;ox<s;ox++) for(int oy=0;oy<s;oy++) {
                Tile t=Vars.world.tile(origin.x+ox,origin.y+oy);
                if(t!=null) t.setBlock(ZonaBlocks.zona,team);
            }
            dismantleProgress=-1f; sourceConstruction=null;
        }
        @Override public void draw() {
            super.draw();
            if(dismantleProgress>=0f) {
                Draw.z(35f); Draw.color(0.2f,0.2f,0.2f,0.8f);
                float w=BuiltZoneBlock.this.size*8f*0.7f, h=4f;
                Draw.rect("white",x,y+BuiltZoneBlock.this.size*4f+6f,w,h);
                Draw.color(0.9f,0.4f,0.2f,0.9f);
                Draw.rect("white",x-w/2f+w*dismantleProgress/2f,y+BuiltZoneBlock.this.size*4f+6f,w*dismantleProgress,h);
                Draw.reset();
            }
        }
    }
}
