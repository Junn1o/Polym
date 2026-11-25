package com.junnio.polymclient.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.Identifier;

import java.util.List;

//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?} else {
/*import com.junnio.mixin.client.GuiGraphicsInvoker;
import net.minecraft.client.renderer.RenderType;
*///?}
//? if >=1.21.9
import com.mojang.blaze3d.platform.cursor.CursorTypes;

// Gui calls whose signature differs between the supported Minecraft versions
final class GuiCompat {
    private GuiCompat() {}

    static void blit(GuiGraphics g, Identifier texture, int x, int y, float u, float v, int w, int h, int texW, int texH) {
        //? if >=1.21.6 {
        g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, w, h, texW, texH);
        //?} else
        /*g.blit(RenderType::guiTextured, texture, x, y, u, v, w, h, texW, texH);*/
    }

    static void blitSprite(GuiGraphics g, Identifier sprite, int x, int y, int w, int h) {
        //? if >=1.21.6 {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, w, h);
        //?} else
        /*g.blitSprite(RenderType::guiTextured, sprite, x, y, w, h);*/
    }

    static void pushTransform(GuiGraphics g, float x, float y, float scale) {
        var pose = g.pose();
        //? if >=1.21.6 {
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);
        //?} else {
        /*pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1);
        *///?}
    }

    static void popTransform(GuiGraphics g) {
        //? if >=1.21.6 {
        g.pose().popMatrix();
        //?} else
        /*g.pose().popPose();*/
    }

    static void tooltip(GuiGraphics g, Font font, List<ClientTooltipComponent> components, int x, int y) {
        //? if >=1.21.6 {
        g.renderTooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);
        //?} else
        /*((GuiGraphicsInvoker) g).polym$renderTooltipInternal(font, components, x, y, DefaultTooltipPositioner.INSTANCE, null);*/
    }

    static void scrollerCursor(GuiGraphics g, boolean dragging) {
        //? if >=1.21.9
        g.requestCursor(dragging ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
    }

    static Identifier skin(PlayerInfo info) {
        //? if >=1.21.9 {
        return info.getSkin().body().texturePath();
        //?} else
        /*return info.getSkin().texture();*/
    }
}
