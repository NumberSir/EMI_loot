package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.item.Item;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class ItemPredicateParser {

    public static Text parseItemPredicate(ItemPredicate predicate) {
        Optional<RegistryEntryList<Item>> items = predicate.items();
        if (items.isPresent() && items.get().getTagKey().isPresent()) {
            return LText.translatable("emi_loot.item_predicate.tag", items.get().getTagKey().get().id());
        }

        if (items.isPresent() && items.get().size() > 0) {
            List<MutableText> list = items.get().stream().map((item) -> (MutableText)item.value().getName()).toList();
            return LText.translatable("emi_loot.item_predicate.items", ListProcessors.buildOrList(list));
        }

        NumberRange.IntRange count = predicate.count();
        if (count != NumberRange.IntRange.ANY) {
            int finalMax = count.max().orElse(0);
            int finalMin = count.min().orElse(0);
            return LText.translatable("emi_loot.item_predicate.count", Integer.toString(finalMin), Integer.toString(finalMax));
        }

        ComponentPredicate componentPredicate = predicate.components();
        if (!componentPredicate.isEmpty()) {
            return LText.translatable("emi_loot.predicate.component");
        }



        if (EMILoot.DEBUG) EMILoot.LOGGER.warn("Empty item predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }

}