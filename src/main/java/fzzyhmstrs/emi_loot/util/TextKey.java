package fzzyhmstrs.emi_loot.util;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public record TextKey(int index, List<String> args){

    static final Map<String, Integer> keyMap = new HashMap<>();
    static final Map<Integer,String> keyReverseMap = new HashMap<>();
    static final Map<Integer, Function<TextKey, Text>> keyTextBuilderMap = new HashMap<>();
    static final Map<Integer, Identifier> keySpriteIdMap = new HashMap<>();
    static final Function<TextKey, Text> DEFAULT_FUNCTION = (key)-> LText.translatable("emi_loot.missing_key");
    static final Identifier EMPTY = new Identifier(EMILoot.MOD_ID,"textures/gui/empty.png");
    static int curDynamicIndex = 1000;

    static{
        mapBuilder(0,"emi_loot.function.empty",(key)->LText.empty(), EMPTY);
        mapBuilder(1,"emi_loot.function.bonus",(key)-> getOneArgText(1, key), new Identifier(EMILoot.MOD_ID,"textures/gui/bonus.png"));
        mapBuilder(2,"emi_loot.function.potion",(key) -> getOneArgText(2, key), new Identifier(EMILoot.MOD_ID,"textures/gui/potion.png"));
        mapBuilder(3,"emi_loot.function.set_count_add",(key)-> getBasicText(3), new Identifier(EMILoot.MOD_ID,"textures/gui/set_count_add.png"));
        mapBuilder(4,"emi_loot.function.randomly_enchanted_book",(key)-> getBasicText(4), new Identifier(EMILoot.MOD_ID,"textures/gui/random_book.png"));
        mapBuilder(5,"emi_loot.function.randomly_enchanted_item",(key)-> getBasicText(5), new Identifier(EMILoot.MOD_ID,"textures/gui/random_item.png"));
        mapBuilder(6,"emi_loot.function.set_enchant_book",(key)-> getBasicText(6), new Identifier(EMILoot.MOD_ID,"textures/gui/set_book.png"));
        mapBuilder(7,"emi_loot.function.set_enchant_item",(key)-> getBasicText(7), new Identifier(EMILoot.MOD_ID,"textures/gui/set_item.png"));
        mapBuilder(8,"emi_loot.function.smelt",(key)-> getBasicText(8), new Identifier(EMILoot.MOD_ID,"textures/gui/smelt.png"));
        mapBuilder(9,"emi_loot.function.looting",(key)-> getBasicText(9), new Identifier(EMILoot.MOD_ID,"textures/gui/looting.png"));
        mapBuilder(10,"emi_loot.function.map",(key) -> getOneArgText(10, key), new Identifier(EMILoot.MOD_ID,"textures/gui/map.png"));
        mapBuilder(11,"emi_loot.function.set_contents",(key)-> getBasicText(11), new Identifier(EMILoot.MOD_ID,"textures/gui/set_contents.png"));
        mapBuilder(12,"emi_loot.function.damage",(key)-> getOneArgText(12, key), new Identifier(EMILoot.MOD_ID,"textures/gui/damage.png"));
        mapBuilder(13,"emi_loot.function.copy_state",(key)-> getBasicText(13), new Identifier(EMILoot.MOD_ID,"textures/gui/copy.png"));
        mapBuilder(14,"emi_loot.function.decay",(key)-> getBasicText(14), new Identifier(EMILoot.MOD_ID,"textures/gui/tnt.png"));
        mapBuilder(24,"emi_loot.condition.survives_explosion",(key)-> getBasicText(24), new Identifier(EMILoot.MOD_ID,"textures/gui/tnt.png"));
        mapBuilder(25,"emi_loot.condition.blockstate",(key)-> getOneArgText(25, key), new Identifier(EMILoot.MOD_ID,"textures/gui/blockstate.png"));
        mapBuilder(26,"emi_loot.condition.table_bonus",(key)-> getOneArgText(26, key), new Identifier(EMILoot.MOD_ID,"textures/gui/percent.png"));
        mapBuilder(27,"emi_loot.condition.invert",(key)-> getInvertedText(27, key), new Identifier(EMILoot.MOD_ID,"textures/gui/invert.png"));
        mapBuilder(28,"emi_loot.condition.alternates",(key)-> getOneArgText(28, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(29,"emi_loot.condition.alternates_2",(key)-> getTwoArgText(29, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(30,"emi_loot.condition.alternates_3",(key)-> getAlternates3Text(30, key), new Identifier(EMILoot.MOD_ID,"textures/gui/or.png"));
        mapBuilder(31,"emi_loot.condition.killed_player",(key)-> getBasicText(31), new Identifier(EMILoot.MOD_ID,"textures/gui/steve.png"));
        mapBuilder(32,"emi_loot.condition.chance",(key)-> getOneArgText(32, key), new Identifier(EMILoot.MOD_ID,"textures/gui/percent.png"));
        mapBuilder(33,"emi_loot.condition.chance_looting",(key)-> getTwoArgText(33, key), new Identifier(EMILoot.MOD_ID,"textures/gui/chance_looting.png"));
        mapBuilder(34,"emi_loot.condition.damage_source",(key)-> getOneArgText(34, key), new Identifier(EMILoot.MOD_ID,"textures/gui/tiny_cactus.png"));
        mapBuilder(35,"emi_loot.condition.location",(key)-> getOneArgText(35, key), new Identifier(EMILoot.MOD_ID,"textures/gui/location.png"));
        mapBuilder(36,"emi_loot.condition.entity_props",(key)-> getOneArgText(36, key), new Identifier(EMILoot.MOD_ID,"textures/gui/entity_props.png"));
        mapBuilder(37,"emi_loot.condition.match_tool",(key)-> getOneArgText(37, key), new Identifier(EMILoot.MOD_ID,"textures/gui/match_tool.png"));
        mapBuilder(38,"emi_loot.condition.entity_scores",(key)-> getBasicText(38), new Identifier(EMILoot.MOD_ID,"textures/gui/score.png"));
        mapBuilder(39,"emi_loot.condition.reference",(key)-> getOneArgText(39, key), new Identifier(EMILoot.MOD_ID,"textures/gui/reference.png"));
        mapBuilder(40,"emi_loot.condition.time_check",(key)-> getOneArgText(40, key), new Identifier(EMILoot.MOD_ID,"textures/gui/time.png"));
        mapBuilder(41,"emi_loot.condition.value_check",(key)-> getTwoArgText(41, key), new Identifier(EMILoot.MOD_ID,"textures/gui/value.png"));
        mapBuilder(42,"emi_loot.condition.raining_true",(key)-> getBasicText(42), new Identifier(EMILoot.MOD_ID,"textures/gui/raining.png"));
        mapBuilder(43,"emi_loot.condition.raining_false",(key)-> getBasicText(43), new Identifier(EMILoot.MOD_ID,"textures/gui/sunny.png"));
        mapBuilder(44,"emi_loot.condition.thundering_true",(key)-> getBasicText(44), new Identifier(EMILoot.MOD_ID,"textures/gui/thundering.png"));
        mapBuilder(45,"emi_loot.condition.thundering_false",(key)-> getBasicText(45), new Identifier(EMILoot.MOD_ID,"textures/gui/not_thundering.png"));

        //tool tag textkeys
        //pickaxe
        mapBuilder(52,"emi_loot.pickaxe.wood",(key)-> getBasicText(52), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_wood.png"));
        mapBuilder(53,"emi_loot.pickaxe.stone",(key)-> getBasicText(53), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_stone.png"));
        mapBuilder(54,"emi_loot.pickaxe.iron",(key)-> getBasicText(54), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_iron.png"));
        mapBuilder(55,"emi_loot.pickaxe.diamond",(key)-> getBasicText(55), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_diamond.png"));
        mapBuilder(56,"emi_loot.pickaxe.netherite",(key)-> getBasicText(56), new Identifier(EMILoot.MOD_ID,"textures/gui/pickaxe_netherite.png"));
        //axe
        mapBuilder(67,"emi_loot.axe.wood",(key)-> getBasicText(67), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_wood.png"));
        mapBuilder(68,"emi_loot.axe.stone",(key)-> getBasicText(68), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_stone.png"));
        mapBuilder(69,"emi_loot.axe.iron",(key)-> getBasicText(69), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_iron.png"));
        mapBuilder(70,"emi_loot.axe.diamond",(key)-> getBasicText(70), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_diamond.png"));
        mapBuilder(71,"emi_loot.axe.netherite",(key)-> getBasicText(71), new Identifier(EMILoot.MOD_ID,"textures/gui/axe_netherite.png"));
        //shovel
        mapBuilder(82,"emi_loot.shovel.wood",(key)-> getBasicText(82), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_wood.png"));
        mapBuilder(83,"emi_loot.shovel.stone",(key)-> getBasicText(83), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_stone.png"));
        mapBuilder(84,"emi_loot.shovel.iron",(key)-> getBasicText(84), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_iron.png"));
        mapBuilder(85,"emi_loot.shovel.diamond",(key)-> getBasicText(85), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_diamond.png"));
        mapBuilder(86,"emi_loot.shovel.netherite",(key)-> getBasicText(86), new Identifier(EMILoot.MOD_ID,"textures/gui/shovel_netherite.png"));
        //hoe
        mapBuilder(97,"emi_loot.hoe.wood",(key)-> getBasicText(97), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_wood.png"));
        mapBuilder(98,"emi_loot.hoe.stone",(key)-> getBasicText(98), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_stone.png"));
        mapBuilder(99,"emi_loot.hoe.iron",(key)-> getBasicText(99), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_iron.png"));
        mapBuilder(100,"emi_loot.hoe.diamond",(key)-> getBasicText(100), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_diamond.png"));
        mapBuilder(101,"emi_loot.hoe.netherite",(key)-> getBasicText(101), new Identifier(EMILoot.MOD_ID,"textures/gui/hoe_netherite.png"));
        //Direct drops and builtin/lootify conditions
        mapBuilder(125,"emi_loot.condition.direct_drop",(key)-> getBasicText(125), new Identifier(EMILoot.MOD_ID,"textures/gui/direct_drops.png"));
        mapBuilder(126,"emi_loot.condition.spawns_with",(key)-> getBasicText(126), new Identifier(EMILoot.MOD_ID,"textures/gui/spawns_with.png"));
        mapBuilder(127,"emi_loot.condition.creeper",(key)-> getBasicText(127), new Identifier(EMILoot.MOD_ID,"textures/gui/creeper.png"));
        mapBuilder(128,"emi_loot.condition.wither_kill",(key)-> getBasicText(128), new Identifier(EMILoot.MOD_ID,"textures/gui/wither.png"));
        mapBuilder(129,"emi_loot.function.set_any_damage",(key)-> getBasicText(129), new Identifier(EMILoot.MOD_ID,"textures/gui/damage.png"));
        mapBuilder(130,"emi_loot.function.ominous_banner",(key)-> getBasicText(130), new Identifier(EMILoot.MOD_ID,"textures/gui/ominous.png"));
        //No conditions
        mapBuilder(150,"emi_loot.no_conditions",(key)-> getBasicText(150), EMPTY);
    }

    private static void mapBuilder(int index, String key, Function<TextKey, Text> function, Identifier spriteId){
        keyMap.put(key,index);
        keyReverseMap.put(index,key);
        keyTextBuilderMap.put(index,function);
        keySpriteIdMap.put(index, spriteId);
    }
    
    public static void register(String key, int args, Identifier sprite){
        if (keyMap.containsKey(key)) throw new IllegalArgumentException("Text key [" + key + "] already registered!");
        if (!sprite.toString().contains(".png")) throw new IllegalArgumentException("Text key [" + key + "] registered with sprite identifier [" + sprite + "] that isn't a png!)");
        int index = curDynamicIndex;
        curDynamicIndex++;
        switch (args) {
            case 0 -> mapBuilder(index, key, (tk) -> getBasicText(index), sprite);
            case 1 -> mapBuilder(index, key, (tk) -> getOneArgText(index, tk), sprite);
            case 2 -> mapBuilder(index, key, (tk) -> getTwoArgText(index, tk), sprite);
            default -> mapBuilder(index, key, (tk) -> getAlternates3Text(index, tk), sprite);
        }
    }

    private static Text getBasicText(int index){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        return LText.translatable(translationKey);
    }

    private static Text getOneArgText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try{
            arg = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg);
    }

    private static Text getInvertedText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg;
        try{
            arg = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg = "Missing";
        }
        return LText.translatable(translationKey, arg).formatted(Formatting.RED);
    }

    private static Text getTwoArgText(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        String arg1;
        String arg2;
        try{
            arg1 = key.args.get(0);
        } catch(Exception e) {
            e.printStackTrace();
            arg1 = "Missing";
        }
        try{
            arg2 = key.args.get(1);
        } catch(Exception e) {
            e.printStackTrace();
            arg2 = "Missing";
        }
        return LText.translatable(translationKey,arg1,arg2);
    }

    private static Text getAlternates3Text(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        MutableText finalText = LText.empty().copy();
        List<String> args = key.args;
        int size = args.size();
        for (int i = 0;i<size;i++){
            String arg = args.get(i);
            if (i == (size - 2)){
                finalText.append(LText.translatable("emi_loot.condition.alternates_3a", arg));
            } else if (i == (size - 1)){
                finalText.append(LText.translatable("emi_loot.condition.alternates", arg));
            } else {
                finalText.append(LText.translatable(translationKey,arg));
            }
        }
        return finalText;
    }

    public boolean isNotEmpty(){
        return index != 0;
    }
    
    public static int getIndex(String key){
        return keyMap.getOrDefault(key,-1);
    }

    public static String getKey(int index){
        return keyReverseMap.getOrDefault(index,"emi_loot.function.empty");
    }
                                                                                    
    public static Identifier getSpriteId(int index){
        return keySpriteIdMap.getOrDefault(index,EMPTY);
    }

    public static TextKey empty(){
        return new TextKey(0,new LinkedList<>());
    }

    public static TextKey of(String key, String ... args){
        if (keyMap.containsKey(key)){
            return new TextKey(keyMap.get(key), Arrays.stream(args).toList());
        } else {
            return new TextKey(0,new LinkedList<>());
        }
    }

    public static TextKey of(String key, List<String> args){
        if (keyMap.containsKey(key)){
           return new TextKey(keyMap.get(key), args);
        } else {
            return new TextKey(0,new LinkedList<>());
        }
    }

    public static TextKey of(String key){
        return TextKey.of(key,new LinkedList<>());
    }

    public static TextKey of (String key, String arg){
        return TextKey.of(key,Collections.singletonList(arg));
    }

    public TextKeyResult process(ItemStack stack, @Nullable World world){
        ItemStack finalStack;
        List<ItemStack> finalStacks = new LinkedList<>();
        //finalStacks.add(stack);
        if (this.index == 8 && world != null){
            Optional<SmeltingRecipe> opt = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING,new SimpleInventory(stack),world);
            if (opt.isPresent()){
                ItemStack tempStack = opt.get().getOutput();
                if (!tempStack.isEmpty()) {
                    //System.out.println(tempStack);
                    finalStack = tempStack.copy();
                    finalStacks.add(finalStack);
                }
            } else {
                finalStacks.add(stack);
            }
        } else {
            finalStacks.add(stack);
        }
        Text text = keyTextBuilderMap.getOrDefault(this.index,DEFAULT_FUNCTION).apply(this);
        return new TextKeyResult(text,finalStacks);
    }
                                                                                    
    public record TextKeyResult(Text text,List<ItemStack> stacks){}

    public static TextKey fromBuf(PacketByteBuf buf){
        int key = buf.readVarInt();
        int size = buf.readByte();
        List<String> args = new LinkedList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                args.add(buf.readString());
            }
        }
        return new TextKey(key,args);
    }

    public void toBuf(PacketByteBuf buf){
        buf.writeVarInt(this.index);
        if (args.isEmpty()){
            buf.writeByte(0);
        } else {
            buf.writeByte(args.size());
            for (String string: args) {
                buf.writeString(string);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextKey textKey = (TextKey) o;
        return index == textKey.index && args.equals(textKey.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, args);
    }
}
