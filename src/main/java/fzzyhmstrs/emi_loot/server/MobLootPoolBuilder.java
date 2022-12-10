package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import fzzyhmstrs.emi_loot.util.TextKey;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MobLootPoolBuilder implements LootBuilder {

    public MobLootPoolBuilder(float rollWeight, List<LootTableParser.LootConditionResult> conditions, List<LootTableParser.LootFunctionResult> functions){
        this.rollWeight = rollWeight;
        this.conditions = conditions;
        this.functions = functions;
    }

    final HashMap<List<TextKey>, ChestLootPoolBuilder> map = new HashMap<>();
    final float rollWeight;
    final List<LootTableParser.LootConditionResult> conditions;
    final List<LootTableParser.LootFunctionResult> functions;
    boolean isSimple = false;
    ItemStack simpleStack = ItemStack.EMPTY;
    boolean isEmpty = false;
    HashMap<List<TextKey>, ChestLootPoolBuilder> builtMap = new HashMap<>();

    public void addItem(LootTableParser.ItemEntryResult result){
        List<TextKey> testKey = new LinkedList<>();
        testKey.addAll(result.functions());
        testKey.addAll(result.conditions());
        ChestLootPoolBuilder builder = map.getOrDefault(testKey,new ChestLootPoolBuilder(rollWeight));
        builder.addItem(result.item(),result.weight());
        map.put(testKey,builder);
    }

    @Override
    public void build() {

        if (map.isEmpty()){
            isEmpty = true;
            return;
        }

        for (List<TextKey> key: map.keySet()){
            ChestLootPoolBuilder builder = map.getOrDefault(key,new ChestLootPoolBuilder(rollWeight));
            builder.build();
            if (map.size() == 1 && builder.isEmpty){
                isEmpty = true;
                return;
            }
            if (map.size() == 1 && builder.isSimple && key.isEmpty()) {
                simpleStack = builder.simpleStack;
                isSimple = true;
            }
            builtMap.put(key,builder);
        }
    }

    @Override
    public List<LootTableParser.ItemEntryResult> revert() {
        List<LootTableParser.ItemEntryResult> list = new LinkedList<>();
        List<TextKey> topLevelKeys = new LinkedList<>();

        conditions.forEach((condition)-> topLevelKeys.add(condition.text()));
        functions.forEach((function)-> topLevelKeys.add(function.text()));

        map.forEach((keyList,builder)->{
            List<LootTableParser.ItemEntryResult> builderList = builder.revert();
            builderList.forEach((builderEntry)->{
                builderEntry.functions().addAll(keyList);
                builderEntry.conditions().addAll(topLevelKeys);
            });
            list.addAll(builderList);
        });
        return list;
    }
}
