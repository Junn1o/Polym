package com.junnio.polym.net.seller;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DeleteOfferPayload(int index) implements CustomPacketPayload {
    public static final Identifier PACKET_ID = Identifier.fromNamespaceAndPath(Polym.MOD_ID, "seller_delete_offer");
    public static final Type<DeleteOfferPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, DeleteOfferPayload> CODEC =
            new StreamCodec<>() {
                @Override public DeleteOfferPayload decode(RegistryFriendlyByteBuf buf) {
                    return new DeleteOfferPayload(buf.readVarInt());
                }
                @Override public void encode(RegistryFriendlyByteBuf buf, DeleteOfferPayload value) {
                    buf.writeVarInt(value.index());
                }
            };

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
