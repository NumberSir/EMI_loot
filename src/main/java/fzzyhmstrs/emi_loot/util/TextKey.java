package fzzyhmstrs.emi_loot.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public record TextKey(int index, List<String> args){

    static Map<String, Integer> keyMap;
    static Map<Integer,String> keyReverseMap;
    static Map<Integer, Function<TextKey, Text>> keyTextBuilderMap;
    static final Function<TextKey, Text> DEFAULT_FUNCTION = (key)-> Text.translatable("emi_loot.missing_key");

    static{
        keyMap = new HashMap<>();
        keyReverseMap = new HashMap<>();
        keyTextBuilderMap = new HashMap<>();
        mapBuilder(0,"emi_loot.function.empty",(key)->Text.empty());
        mapBuilder(1,"emi_loot.function.bonus",(key)-> getOneArgText(1, key));
        mapBuilder(2,"emi_loot.function.potion",(key) -> getOneArgText(2, key));
        mapBuilder(3,"emi_loot.function.set_count_add",(key)-> getBasicText(3));
        mapBuilder(4,"emi_loot.function.randomly_enchanted_book",(key)-> getBasicText(4));
        mapBuilder(5,"emi_loot.function.randomly_enchanted_item",(key)-> getBasicText(5));
        mapBuilder(6,"emi_loot.function.set_enchant_book",(key)-> getBasicText(6));
        mapBuilder(7,"emi_loot.function.set_enchant_item",(key)-> getBasicText(7));
        mapBuilder(8,"emi_loot.function.smelt",(key)-> getBasicText(8));
        mapBuilder(9,"emi_loot.function.looting",(key)-> getBasicText(9));
        mapBuilder(10,"emi_loot.function.map",(key) -> getOneArgText(10, key));
        mapBuilder(11,"emi_loot.function.set_contents",(key)-> getBasicText(11));
        mapBuilder(12,"emi_loot.function.damage",(key)-> getOneArgText(12, key));
        mapBuilder(13,"emi_loot.function.copy_state",(key)-> getBasicText(13));
        mapBuilder(14,"emi_loot.function.decay",(key)-> getBasicText(14));
        mapBuilder(21,"emi_loot.condition.survives_explosion",(key)-> getBasicText(21));
        mapBuilder(22,"emi_loot.condition.blockstate",(key)-> getBasicText(22));
        mapBuilder(23,"emi_loot.condition.table_bonus",(key)-> getOneArgText(23, key));
        mapBuilder(24,"emi_loot.condition.invert",(key)-> getInvertedText(24, key));
        mapBuilder(25,"emi_loot.condition.alternates",(key)-> getOneArgText(25, key));
        mapBuilder(26,"emi_loot.condition.alternates_2",(key)-> getTwoArgText(26, key));
        mapBuilder(27,"emi_loot.condition.alternates_3",(key)-> getAlternates3Text(27, key));
        mapBuilder(28,"emi_loot.condition.killed_player",(key)-> getBasicText(28));
        mapBuilder(29,"emi_loot.condition.chance",(key)-> getOneArgText(29, key));
        mapBuilder(30,"emi_loot.condition.chance_looting",(key)-> getTwoArgText(30, key));
        mapBuilder(31,"emi_loot.function.damage_source",(key)-> getBasicText(31));
        mapBuilder(32,"emi_loot.function.location",(key)-> getBasicText(32));
        mapBuilder(33,"emi_loot.function.entity_props",(key)-> getBasicText(33));
        mapBuilder(34,"emi_loot.condition.match_tool",(key)-> getOneArgText(34, key));
    }

    private static void mapBuilder(int index, String key, Function<TextKey, Text> function){
        keyMap.put(key,index);
        keyReverseMap.put(index,key);
        keyTextBuilderMap.put(index,function);
    }

    private static Text getBasicText(int index){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        return Text.translatable(translationKey);
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
        return Text.translatable(translationKey, arg);
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
        return Text.translatable(translationKey, arg).formatted(Formatting.RED);
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
        return Text.translatable(translationKey,arg1,arg2);
    }

    private static Text getAlternates3Text(int index, TextKey key){
        String translationKey = keyReverseMap.getOrDefault(index, "emi_loot.missing_key");
        MutableText finalText = Text.empty();
        List<String> args = key.args;
        int size = args.size();
        for (int i = 0;i<size;i++){
            String arg = args.get(i);
            if (i == (size - 2)){
                finalText.append(Text.translatable("emi_loot.condition.alternates_3a", arg));
            } else if (i == (size - 1)){
                finalText.append(Text.translatable("emi_loot.condition.alternates", arg));
            } else {
                finalText.append(Text.translatable(translationKey,arg));
            }
        }
        return finalText;
    }

    public boolean isNotEmpty(){
        return index != 0;
    }

    public static TextKey empty(){
        return new TextKey(0,new LinkedList<>());
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
        List<String> args = new LinkedList<>();
        args.add(arg);
        return TextKey.of(key,args);
    }

    public TextKeyResult process(ItemStack stack, @Nullable World world){
        ItemStack finalStack;
        List<ItemStack> finalStacks = new LinkedList<>();
        finalStacks.add(stack);
        if (this.index == 6 && world != null){
            Optional<SmeltingRecipe> opt = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING,new SimpleInventory(stack),world);
            if (opt.isPresent()){
                ItemStack tempStack = opt.get().getOutput();
                if (!tempStack.isEmpty()) {
                    finalStack = tempStack.copy();
                    finalStacks.add(finalStack);
                }
            }
        }
        Text text = keyTextBuilderMap.getOrDefault(this.index,DEFAULT_FUNCTION).apply(this);
        return new TextKeyResult(text,finalStacks);
    }

    public static TextKey fromBuf(PacketByteBuf buf){
        int key = buf.readByte();
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
        buf.writeByte(this.index);
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

    public record TextKeyResult(Text text,List<ItemStack> stacks){}

}
