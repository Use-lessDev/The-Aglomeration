package theAglomeration;

import arc.Core;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.BuildVisibility;
import mindustry.type.Category;
import mindustry.type.ItemStack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * The first ZDCDC zone block.
 *
 * Connected copies form a zone. The zone must form a complete rectangle;
 * the menu only offers house sizes that fit inside that rectangle.
 */
public class PercentBuildBase extends Block {
    private static final float[] BUILD_SECONDS = {0f, 5f, 15f, 30f, 50f, 80f, 120f, 170f, 240f};

    public PercentBuildBase(String name) {
        super(name);
        localizedName = "ZDCDC Construction Zone";
        description = "Connect these blocks into a rectangular zone, then choose a house size.";
        size = 1;
        solid = false;
        destructible = true;
        update = true;
        configurable = true;
        saveConfig = true;
        itemCapacity = 2000;
        buildVisibility = BuildVisibility.shown;
        requirements(Category.effect, ItemStack.with(Items.copper, 25));
        buildType = ZoneBuild::new;
        config(Integer.class, (ZoneBuild build, Integer houseSize) -> build.beginConstruction(houseSize));
    }

    @Override
    public void load() {
        super.load();
        // Temporary sprite until a dedicated zone texture is added.
        region = Core.atlas.find("copper-wall");
    }

    public class ZoneBuild extends Building {
        private boolean constructing;
        private int constructionSize;
        private float constructionProgress;
        private int constructionX;
        private int constructionY;

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
            table.button(Icon.hammer, Styles.cleari, () -> showConstructionMenu()).size(46f).tooltip("Build a house").row();
        }

        private void showConstructionMenu() {
            int[] bounds = findZoneBounds();
            Dialog dialog = new Dialog("Zona de construção de Casas (ZDCDC)");
            dialog.addCloseButton();

            if (bounds == null) {
                dialog.cont.add("No connected zone was found.").wrap().width(280f).row();
                dialog.show();
                return;
            }

            int width = bounds[2] - bounds[0] + 1;
            int height = bounds[3] - bounds[1] + 1;
            int area = countConnectedZoneTiles();

            if (area != width * height) {
                dialog.cont.add("This zone is not a complete rectangle.").wrap().width(280f).row();
                dialog.show();
                return;
            }

            int maxSize = Math.min(8, Math.min(width, height));
            dialog.cont.add("Zone: " + width + " x " + height).row();
            dialog.cont.add("Choose a house size:").row();

            if (constructing) {
                dialog.cont.add("Construction: " + Math.round(constructionProgress * 100f) + "%").row();
            } else {
                for (int houseSize = 1; houseSize <= maxSize; houseSize++) {
                    final int chosenSize = houseSize;
                    int copperCost = houseSize * houseSize * 10;
                    int leadCost = houseSize * houseSize * 3;
                    float seconds = BUILD_SECONDS[houseSize];
                    dialog.cont.button("ZDCDC-" + houseSize + " (" + houseSize + "x" + houseSize + ")\n" +
                        copperCost + " copper, " + leadCost + " lead | " + (int)seconds + "s",
                        Icon.home, Styles.flatBordert, () -> {
                            configure(chosenSize);
                            dialog.hide();
                        }).width(310f).height(58f).row();
                }
            }

            dialog.show();
        }

        private int[] findZoneBounds() {
            if (tile == null || Vars.world == null) return null;

            int mapWidth = Vars.world.width();
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            int start = tile.x + tile.y * mapWidth;
            queue.add(start);
            visited.add(start);

            int minX = tile.x, maxX = tile.x, minY = tile.y, maxY = tile.y;
            while (!queue.isEmpty()) {
                int packed = queue.removeFirst();
                int x = packed % mapWidth;
                int y = packed / mapWidth;
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);

                addZoneNeighbor(x - 1, y, mapWidth, queue, visited);
                addZoneNeighbor(x + 1, y, mapWidth, queue, visited);
                addZoneNeighbor(x, y - 1, mapWidth, queue, visited);
                addZoneNeighbor(x, y + 1, mapWidth, queue, visited);
            }

            return new int[]{minX, minY, maxX, maxY};
        }

        private void addZoneNeighbor(int x, int y, int mapWidth, ArrayDeque<Integer> queue, Set<Integer> visited) {
            if (x < 0 || y < 0 || x >= Vars.world.width() || y >= Vars.world.height()) return;
            int packed = x + y * mapWidth;
            if (visited.contains(packed)) return;
            Tile other = Vars.world.tile(x, y);
            if (other != null && other.build != null && other.build.block == PercentBuildBase.this) {
                visited.add(packed);
                queue.addLast(packed);
            }
        }

        private int countConnectedZoneTiles() {
            if (tile == null || Vars.world == null) return 0;
            int mapWidth = Vars.world.width();
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            int start = tile.x + tile.y * mapWidth;
            queue.add(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                int packed = queue.removeFirst();
                int x = packed % mapWidth;
                int y = packed / mapWidth;
                addZoneNeighbor(x - 1, y, mapWidth, queue, visited);
                addZoneNeighbor(x + 1, y, mapWidth, queue, visited);
                addZoneNeighbor(x, y - 1, mapWidth, queue, visited);
                addZoneNeighbor(x, y + 1, mapWidth, queue, visited);
            }
            return visited.size();
        }

        private void beginConstruction(int houseSize) {
            if (constructing || houseSize < 1 || houseSize > 8) return;

            int[] bounds = findZoneBounds();
            if (bounds == null) return;
            int width = bounds[2] - bounds[0] + 1;
            int height = bounds[3] - bounds[1] + 1;
            if (countConnectedZoneTiles() != width * height || houseSize > width || houseSize > height) {
                Vars.ui.showInfoToast("The zone must be a valid rectangle and fit this house.", 3f);
                return;
            }

            int copperCost = houseSize * houseSize * 10;
            int leadCost = houseSize * houseSize * 3;
            if (!items.has(Items.copper, copperCost) || !items.has(Items.lead, leadCost)) {
                Vars.ui.showInfoToast("This zone needs " + copperCost + " copper and " + leadCost + " lead.", 3f);
                return;
            }

            items.remove(Items.copper, copperCost);
            items.remove(Items.lead, leadCost);
            constructionSize = houseSize;
            constructionProgress = 0f;
            constructionX = bounds[0] + (width - houseSize) / 2;
            constructionY = bounds[1] + (height - houseSize) / 2;
            constructing = true;
            Vars.ui.showInfoToast("ZDCDC-" + houseSize + " construction started.", 3f);
        }

        @Override
        public void updateTile() {
            if (!constructing) return;
            constructionProgress = Math.min(1f, constructionProgress + Time.delta / (BUILD_SECONDS[constructionSize] * 60f));
            if (constructionProgress >= 1f) finishConstruction();
        }

        private void finishConstruction() {
            if (TheAglomeration.houses[constructionSize] == null) {
                constructing = false;
                Vars.ui.showInfoToast("House content was not loaded.", 3f);
                return;
            }

            int size = constructionSize;
            int startX = constructionX;
            int startY = constructionY;
            constructing = false;

            // Clear only the selected square; any extra zone tiles remain available.
            for (int x = startX; x < startX + size; x++) {
                for (int y = startY; y < startY + size; y++) {
                    Tile target = Vars.world.tile(x, y);
                    if (target != null && target.build != null && target.build.block == PercentBuildBase.this) {
                        target.setAir();
                    }
                }
            }

            Tile anchor = Vars.world.tile(startX, startY);
            if (anchor != null) anchor.setBlock(TheAglomeration.houses[size], team, 0);
            Vars.ui.showInfoToast("ZDCDC-" + size + " house completed.", 3f);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(constructing);
            write.i(constructionSize);
            write.f(constructionProgress);
            write.i(constructionX);
            write.i(constructionY);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            constructing = read.bool();
            constructionSize = read.i();
            constructionProgress = read.f();
            constructionX = read.i();
            constructionY = read.i();
        }
    }
}
