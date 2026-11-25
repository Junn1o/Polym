package com.junnio.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21.6 {
/*import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
*///?}

// before 1.21.6 GuiGraphics has no public tooltip method taking ClientTooltipComponent; empty on newer versions
@Mixin(GuiGraphics.class)
public interface GuiGraphicsInvoker {
    //? if <1.21.6 {
    /*@Invoker("renderTooltipInternal")
    void polym$renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int x, int y,
                                     ClientTooltipPositioner positioner, Identifier background);
    *///?}
}
