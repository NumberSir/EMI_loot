package fzzyhmstrs.emi_loot.parser;

import fzzyhmstrs.emi_loot.mixins.FishingHookPredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.LightningBoltPredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.PlayerPredicateAccessor;
import fzzyhmstrs.emi_loot.mixins.SlimePredicateAccessor;
import fzzyhmstrs.emi_loot.parser.processor.ListProcessors;
import fzzyhmstrs.emi_loot.parser.processor.NumberProcessors;
import fzzyhmstrs.emi_loot.util.LText;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.stat.Stat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TypeSpecificPredicateParser {

    public static Text parseTypeSpecificPredicate(TypeSpecificPredicate predicate){
        if (predicate instanceof LightningBoltPredicate){
            return parseLightningBoltPredicate((LightningBoltPredicate)predicate);
        }
        
        if (predicate instanceof FishingHookPredicate){
            return parseFishingHookPredicate((FishingHookPredicate)predicate);
        }
        

        if (predicate instanceof PlayerPredicate){
            return parsePlayerPredicate((PlayerPredicate)predicate);
        }

        if (predicate instanceof SlimePredicate){
            return parseSlimePredicate((SlimePredicate)predicate);
        }

        return LText.translatable("emi_loot.entity_predicate.type_specific.any");
    }
    
    
    public static Text parseLightningBoltPredicate(LightningBoltPredicate predicate){
        NumberRange.IntRange blocksSetOnFire = ((LightningBoltPredicateAccessor)predicate).getBlocksSetOnFire();
        if (!blocksSetOnFire.equals(NumberRange.IntRange.ANY)){
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    NumberProcessors.processNumberRange(
                        blocksSetOnFire,
                        "emi_loot.entity_predicate.type_specific.lightning.blocks",
                        "emi_loot.entity_predicate.type_specific.lightning.blocks_2",
                        ""
                    )
            );
        }

        EntityPredicate entityStruck = ((LightningBoltPredicateAccessor)predicate).getEntityStruck();
        if(!entityStruck.equals(EntityPredicate.ANY)){
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.lightning",
                    LText.translatable(
                        "emi_loot.entity_predicate.type_specific.lightning.struck",
                        EntityPredicateParser.parseEntityPredicate(entityStruck).getString()
                    )
            );
        }
        return LText.translatable("emi_loot.entity_predicate.type_specific.any");
    }
    
    
    public static Text parseFishingHookPredicate(FishingHookPredicate predicate){
        boolean inOpenWater = ((FishingHookPredicateAccessor)predicate).getInOpenWater();
        return inOpenWater ? LText.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_true") : LText.translatable("emi_loot.entity_predicate.type_specific.fishing_hook_false");
    }

    public static parsePlayerPredicate(PlayerPredicate predicate){
        NumberRange.IntRange experienceLevel = ((PlayerPredicateAccessor)predicate).getExperienceLevel();
        if (!experienceLevel.equals(NumberRange.IntRange.ANY)){
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    NumberProcessors.processNumberRange(
                            experienceLevel,
                            "emi_loot.entity_predicate.type_specific.player.level",
                            "emi_loot.entity_predicate.type_specific.player.level_2",
                            ""
                    )
            );
        }

        GameMode gameMode = ((PlayerPredicateAccessor)predicate).getGameMode();
        if (gameMode != null){
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    LText.translatable("emi_loot.entity_predicate.type_specific.player.gamemode",gameMode.getName()));
        }

        Map<Stat<?>, NumberRange.IntRange> stats = ((PlayerPredicateAccessor)predicate).getStats();
        if (!stats.isEmpty()) {
            List<MutableText> list = new LinkedList<>();
            for (Map.Entry<Stat<?>, NumberRange.IntRange> entry : stats.entrySet()) {
                String name = entry.getKey().getName();
                String[] namePieces = name.split(":");
                if (namePieces.length == 2) {
                    String typeId = namePieces[0].replace('.', ':');
                    String valueId = namePieces[1].replace('.', ':');
                    MutableText num = NumberProcessors.processNumberRange(
                            entry.getValue(),
                            "emi_loot.entity_predicate.type_specific.player.stats.exact",
                            "emi_loot.entity_predicate.type_specific.player.stats.between",
                            "emi_loot.entity_predicate.type_specific.player.stats.fallback"
                    );
                    list.add(LText.translatable("emi_loot.entity_predicate.type_specific.player.stats.type." + typeId, valueId, num));
                }
            }
            if (!list.isEmpty()) {
                return LText.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        Object2BooleanMap<Identifier> recipes = ((PlayerPredicateAccessor)predicate).getRecipes();
        if (!recipes.isEmpty()){
            List<MutableText> list = new LinkedList<>();
            for (Object2BooleanMap.Entry<Identifier> entry: recipes.object2BooleanEntrySet()){
                list.add(
                        entry.getBooleanValue()
                        ?
                        LText.translatable("emi_loot.entity_predicate.type_specific.player.recipe_true",entry.getKey())
                        :
                        LText.translatable("emi_loot.entity_predicate.type_specific.player.recipe_false",entry.getKey())
                );
            }
            if (!list.isEmpty()){
                return LText.translatable(
                        "emi_loot.entity_predicate.type_specific.player",
                        ListProcessors.buildAndList(list)
                );
            }
        }

        EntityPredicate entityPredicate = ((PlayerPredicateAccessor)predicate).getLookingAt();
        if (!entityPredicate.equals(EntityPredicate.ANY)){
            return LText.translatable(
                    "emi_loot.entity_predicate.type_specific.player",
                    LText.translatable("emi_loot.entity_predicate.type_specific.player.looking", EntityPredicateParser.parseEntityPredicate(entityPredicate)));
        }
        return LText.translatable("emi_loot.entity_predicate.type_specific.any");
    }
    
    public static parseSlimePredicate(SlimePredicate predicate){
        NumberRange.IntRange size = ((SlimePredicateAccessor)predicate).getSize();
        return NumberProcessors.processNumberRange(size,"emi_loot.entity_predicate.type_specific.slime","emi_loot.entity_predicate.type_specific.slime_2","");
    }
}
