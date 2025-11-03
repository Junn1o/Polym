package com.junnio.polymclient;

import com.junnio.polym.screen.PolymTableScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gl.RenderPipelines;


public class PolymTableScreen extends HandledScreen<PolymTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/crafting_table.png");

    public PolymTableScreen(PolymTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE,this.x, this.y, 0F, 0F, this.backgroundWidth, this.backgroundHeight,256,256);
    }
}