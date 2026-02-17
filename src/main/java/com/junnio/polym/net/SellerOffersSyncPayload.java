package com.junnio.polym.net;

import com.junnio.polym.Polym;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record SellerOffersSyncPayload(List<ShopOfferData> offers) implements CustomPacketPayload {
    public static final Identifier PACKET_ID =
            Identifier.fromNamespaceAndPath(Polym.MOD_ID, "seller_offers_sync");
    public static final Type<SellerOffersSyncPayload> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SellerOffersSyncPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public SellerOffersSyncPayload decode(RegistryFriendlyByteBuf buf) {
                    int size = buf.readVarInt();
                    List<ShopOfferData> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) list.add(ShopOfferData.CODEC.decode(buf));
                    return new SellerOffersSyncPayload(list);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SellerOffersSyncPayload value) {
                    buf.writeVarInt(value.offers().size());
                    for (ShopOfferData o : value.offers()) ShopOfferData.CODEC.encode(buf, o);
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

