package fzzyhmstrs.emi_loot.parser;

import com.google.gson.JsonElement;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypePredicateParser{

    public static Text parseEntityTypePredicate(EntityTypePredicate predicate){
        JsonElement element = predicate.toJson();
        String jsonString = element.getAsString();
        if (jsonString != null){
            if (jsonString.startsWith("#")) {
                return LText.translatable("emi_loot.entity_predicate.type_tag", jsonString);
            } else {
                EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(jsonString));
                return LText.translatable("emi_loot.mob_type_predicate.type", type.getName().getString());
            }
        }
        if (EMILoot.DEBUG) EMILoot.LOGGER.warning("Empty or unparsable entity type predicate in table: "  + LootTableParser.currentTable);
        return LText.translatable("emi_loot.predicate.invalid");
    }
}
