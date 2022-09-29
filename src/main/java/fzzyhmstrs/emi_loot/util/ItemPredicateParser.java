package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.mixins.EnchantmentPredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.ItemPredicateAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ItemPredicateParser {

    public static Text parseItemPredicate(ItemPredicate predicate){
        TagKey<Item> tag = ((ItemPredicateAccessor)predicate).getTag();
        if (tag != null){
            return Text.translatable("emi_loot.item_predicate.tag",tag.id());
        }
        Set<Item> items = ((ItemPredicateAccessor)predicate).getItems();
        if (items != null){
            int size = items.size();
            if (size == 1){
                return Text.translatable("emi_loot.item_predicate.items",items.stream().toList().get(0).getName());
            }
            if (size == 2){
                List<Item> list = items.stream().toList();
                return Text.translatable("emi_loot.item_predicate.items_2",list.get(0).getName(),list.get(1).getName());
            }
            if (size > 2){
                List<Item> list = items.stream().toList();
                MutableText finalText = Text.empty();
                for (int i = 0; i < size; i++){
                    Item item = list.get(i);
                    if (i == 0){
                        finalText.append(Text.translatable("emi_loot.item_predicate.items_3a", item.getName().getString()));
                    } else if (i == size - 2){
                        finalText.append(Text.translatable("emi_loot.item_predicate.items_3c", item.getName().getString()));
                    } else if (i == size - 1){
                        finalText.append(Text.translatable("emi_loot.item_predicate.items_3d", item.getName().getString()));
                    } else {
                        finalText.append(Text.translatable("emi_loot.item_predicate.items_3b", item.getName().getString()));
                    }
                }
                return finalText;
            }
        }
        NumberRange.IntRange count = ((ItemPredicateAccessor)predicate).getCount();
        if (count != NumberRange.IntRange.ANY){
            Integer max = count.getMax();
            Integer min = count.getMin();
            int finalMax = max != null ? max : 0;
            int finalMin = min != null ? min : 0;
            return Text.translatable("emi_loot.item_predicate.count", Integer.toString(finalMin), Integer.toString(finalMax));
        }
        NumberRange.IntRange durability = ((ItemPredicateAccessor)predicate).getDurability();
        if (durability != NumberRange.IntRange.ANY){
            Integer max = durability.getMax();
            Integer min = durability.getMin();
            int finalMax = max != null ? max : 0;
            int finalMin = min != null ? min : 0;
            return Text.translatable("emi_loot.item_predicate.durability", Integer.toString(finalMin), Integer.toString(finalMax));
        }
        EnchantmentPredicate[] enchants = ((ItemPredicateAccessor)predicate).getEnchantments();
        EnchantmentPredicate[] storedEnchants = ((ItemPredicateAccessor)predicate).getStoredEnchantments();
        if (enchants.length + storedEnchants.length > 0){
            List<EnchantmentPredicate> list = new LinkedList<>();
            list.addAll(Arrays.stream(enchants).toList());
            list.addAll(Arrays.stream(storedEnchants).toList());
            int size = list.size();
            if (size == 1){
                Enchantment enchant = ((EnchantmentPredicateAccessor)list.get(0)).getEnchantment();
                return Text.translatable("emi_loot.item_predicate.enchant",enchant.getName(1).getString());
            }
            if (size == 2){
                Enchantment enchant1 = ((EnchantmentPredicateAccessor)list.get(0)).getEnchantment();
                Enchantment enchant2 = ((EnchantmentPredicateAccessor)list.get(1)).getEnchantment();
                return Text.translatable("emi_loot.item_predicate.enchant_2",enchant1.getName(1).getString(),enchant2.getName(1).getString());
            }
            if (size > 2){
                MutableText finalText = Text.empty();
                for (int i = 0; i < size; i++){
                    Enchantment enchant = ((EnchantmentPredicateAccessor)list.get(i)).getEnchantment();
                    if (i == 0){
                        finalText.append(Text.translatable("emi_loot.item_predicate.enchant_3a", enchant.getName(1).getString()));
                    } else if (i == size - 2){
                        finalText.append(Text.translatable("emi_loot.item_predicate.enchant_3c", enchant.getName(1).getString()));
                    } else if (i == size - 1){
                        finalText.append(Text.translatable("emi_loot.item_predicate.enchant_3d", enchant.getName(1).getString()));
                    } else {
                        finalText.append(Text.translatable("emi_loot.item_predicate.enchant_3b",enchant.getName(1).getString()));
                    }
                }
                return finalText;
            }
        }
        return Text.translatable("emi_loot.item_predicate.empty");
    }

}