package com.junnio.polym.screen;

import com.junnio.polym.Polym;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers{
    public static final MenuType<PolymTableScreenHandler> POLYM_TABLE_SCREEN_HANDLER =
            new MenuType<>(
                    (syncId, inventory) -> new PolymTableScreenHandler(syncId, inventory, inventory.player),
                    FeatureFlagSet.of()
            );
    public static final MenuType<ShopScreenHandler> SHOP_SCREEN_HANDLER =
            new MenuType<>(
                    (syncId, inventory) -> new ShopScreenHandler(syncId, inventory, inventory.player),
                    FeatureFlagSet.of()
            );

    public static void initialize() {
        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Polym.MOD_ID, "polym_table"), POLYM_TABLE_SCREEN_HANDLER);
        Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Polym.MOD_ID, "polym_shop"), SHOP_SCREEN_HANDLER);
    }
}