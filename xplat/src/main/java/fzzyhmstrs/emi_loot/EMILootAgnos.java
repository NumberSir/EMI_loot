package fzzyhmstrs.emi_loot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

public abstract class EMILootAgnos {
    public static EMILootAgnos delegate;

    static {
        try {
            Class.forName("fzzyhmstrs.emi_loot.fabric.EMILootAgnosFabric");
        } catch (Throwable t) {
        }
        try {
            Class.forName("fzzyhmstrs.emi_loot.forge.EMILootAgnosForge");
        } catch (Throwable t) {
        }
    }

    public static String getModName(String namespace) {
        return delegate.getModNameAgnos(namespace);
    }

    protected abstract String getModNameAgnos(String namespace);

    public static boolean isDevelopmentEnvironment() {
        return delegate.isDevelopmentEnvironmentAgnos();
    }

    protected abstract boolean isDevelopmentEnvironmentAgnos();

    public static boolean isModLoaded(String id) {
        return delegate.isModLoadedAgnos(id);
    }

    protected abstract boolean isModLoadedAgnos(String id);

    public static LootTable loadLootTable(Gson gson, Identifier id, JsonObject json) {
        return delegate.loadLootTableAgnos(gson, id, json);
    }

    protected abstract LootTable loadLootTableAgnos(Gson gson, Identifier id, JsonObject json);
}
