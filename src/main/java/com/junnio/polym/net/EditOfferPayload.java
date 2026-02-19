package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EditOfferPayload(int index) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "seller_edit_offer");
    public static final Type<EditOfferPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, EditOfferPayload> CODEC =
            new StreamCodec<>() {
                @Override public EditOfferPayload decode(RegistryFriendlyByteBuf buf) {
                    return new EditOfferPayload(buf.readVarInt());
                }
                @Override public void encode(RegistryFriendlyByteBuf buf, EditOfferPayload value) {
                    buf.writeVarInt(value.index());
                }
            };

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

