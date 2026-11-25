package com.junnio.polym.test;

import com.junnio.polym.command.ModCommand;
import com.junnio.polym.item.ModItems;
import com.junnio.polym.screen.PolymTableScreenHandler;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
//? if >=1.21.5 {
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
*///?}

// slot 0 = result, slots 1..9 = 3x3 grid (row-major)
//? if >=1.21.5 {
public class PolymTableGameTest {
//?} else
/*public class PolymTableGameTest implements FabricGameTest {*/

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void guildRecipeWithGuildTag(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.addTag(ModCommand.GUILD_TAG);
        PolymTableScreenHandler menu = table(player);
        fillGuildMoneyPattern(menu);

        expect(helper, menu.getSlot(0).getItem(), ModItems.GOLD_COIN, 1, "guild_money with Guild tag");
        helper.succeed();
    }

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void guildRecipeBlockedWithoutTag(GameTestHelper helper) {
        PolymTableScreenHandler menu = table(helper.makeMockPlayer(GameType.SURVIVAL));
        fillGuildMoneyPattern(menu);

        check(helper, menu.getSlot(0).getItem().isEmpty(), "guild_money must be empty without Guild tag");
        helper.succeed();
    }

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void polymShapelessRecipe(GameTestHelper helper) {
        PolymTableScreenHandler menu = table(helper.makeMockPlayer(GameType.SURVIVAL));
        menu.getSlot(5).set(new ItemStack(ModItems.GOLD_COIN_PILE));

        expect(helper, menu.getSlot(0).getItem(), ModItems.GOLD_COIN, 5, "gold_coin_pile -> gold_coin");
        helper.succeed();
    }

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void vanillaRecipeAndConsume(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        PolymTableScreenHandler menu = table(player);
        menu.getSlot(1).set(new ItemStack(Items.OAK_LOG, 2));

        ItemStack result = menu.getSlot(0).getItem();
        expect(helper, result, Items.OAK_PLANKS, 4, "oak_log -> oak_planks");

        menu.getSlot(0).onTake(player, result.copy());
        expect(helper, menu.getSlot(1).getItem(), Items.OAK_LOG, 1, "one log consumed");
        expect(helper, menu.getSlot(0).getItem(), Items.OAK_PLANKS, 4, "result refreshed for remaining log");
        helper.succeed();
    }

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void vanillaRecipeKeepsRemainder(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        PolymTableScreenHandler menu = table(player);
        menu.getSlot(1).set(new ItemStack(Items.HONEY_BOTTLE));

        ItemStack result = menu.getSlot(0).getItem();
        expect(helper, result, Items.SUGAR, 3, "honey_bottle -> sugar");

        menu.getSlot(0).onTake(player, result.copy());
        expect(helper, menu.getSlot(1).getItem(), Items.GLASS_BOTTLE, 1, "glass bottle left in grid");
        helper.succeed();
    }

    //? if >=1.21.5 {
    @GameTest
    //?} else
    /*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)*/
    public void noRecipeGivesEmptyResult(GameTestHelper helper) {
        PolymTableScreenHandler menu = table(helper.makeMockPlayer(GameType.SURVIVAL));
        menu.getSlot(1).set(new ItemStack(Items.DIRT));

        check(helper, menu.getSlot(0).getItem().isEmpty(), "dirt has no recipe");
        helper.succeed();
    }

    private static PolymTableScreenHandler table(Player player) {
        return new PolymTableScreenHandler(0, player.getInventory(), player);
    }

    // guild_money: AAA / ### / AAA, A = amethyst_shard, # = diamond
    private static void fillGuildMoneyPattern(PolymTableScreenHandler menu) {
        for (int i = 0; i < 9; i++) {
            boolean middleRow = i >= 3 && i < 6;
            menu.getSlot(1 + i).set(new ItemStack(middleRow ? Items.DIAMOND : Items.AMETHYST_SHARD));
        }
    }

    private static void expect(GameTestHelper helper, ItemStack stack, Item item, int count, String what) {
        check(helper, stack.is(item) && stack.getCount() == count,
                what + ": expected " + count + "x " + item + " but got " + stack.getCount() + "x " + stack.getItem());
    }

    private static void check(GameTestHelper helper, boolean ok, String message) {
        if (ok) return;
        //? if >=1.21.5 {
        helper.fail(Component.literal(message));
        //?} else
        /*helper.fail(message);*/
    }
}
