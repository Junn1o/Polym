package com.junnio.polym.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;

public final class SellerShopJsonStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static SellerShopJsonStore INSTANCE;
    private final MinecraftServer server;
    private final Map<UUID, ShopEntry> shops = new HashMap<>();

    private SellerShopJsonStore(MinecraftServer server) {
        this.server = server;
        load();
    }

    public static SellerShopJsonStore get(MinecraftServer server) {
        if (INSTANCE == null) INSTANCE = new SellerShopJsonStore(server);
        return INSTANCE;
    }

    private Path filePath() {
        Path worldRoot = server.getWorldPath(LevelResource.ROOT); // nếu mapping khác, IDE sẽ gợi ý tương đương
        return worldRoot.resolve("polym").resolve("seller_shops.json");
    }

    public void setShop(UUID owner, String name, List<ShopOfferData> offers) {
        shops.put(owner, new ShopEntry(name, offers));
    }

    public List<ShopOfferData> getOffers(UUID owner) {
        ShopEntry e = shops.get(owner);
        return e == null ? List.of() : e.offers();
    }

    public void load() {
        Path p = filePath();
        if (!Files.exists(p)) return;

        try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            RootJson root = GSON.fromJson(r, RootJson.class);
            shops.clear();
            if (root == null || root.shops == null) return;

            root.shops.forEach((uuidStr, shopJson) -> {
                UUID uuid = UUID.fromString(uuidStr);
                List<ShopOfferData> offers = decodeOffers(shopJson.offers);
                shops.put(uuid, new ShopEntry(shopJson.name, offers));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveNow() {
        Path p = filePath();
        try {
            Files.createDirectories(p.getParent());

            RootJson root = encodeRoot();
            String json = GSON.toJson(root);

            // atomic-ish write: temp -> move replace
            Path tmp = p.resolveSibling(p.getFileName() + ".tmp");
            Files.writeString(tmp, json, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private RootJson encodeRoot() {
        RootJson root = new RootJson();
        root.shops = new HashMap<>();

        for (var e : shops.entrySet()) {
            UUID owner = e.getKey();
            ShopEntry entry = e.getValue();

            ShopJson sj = new ShopJson();
            sj.name = entry.name();
            sj.offers = new ArrayList<>();

            for (ShopOfferData o : entry.offers()) {
                OfferJson oj = new OfferJson();
                oj.buyA = encodeStack(o.buyA());
                oj.buyB = encodeStack(o.buyB());
                oj.sell = encodeStack(o.sell());
                sj.offers.add(oj);
            }

            root.shops.put(owner.toString(), sj);
        }

        return root;
    }
    private StackJson encodeStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return null;

        StackJson sj = new StackJson();
        sj.item = id.toString();
        sj.count = Math.max(1, Math.min(64, stack.getCount())); // clamp nhẹ
        return sj;
    }

    private ItemStack decodeStack(StackJson json) {
        if (json == null || json.item == null || json.item.isBlank()) return ItemStack.EMPTY;

        Identifier id = Identifier.tryParse(json.item);
        if (id == null) return ItemStack.EMPTY;
        Optional<Holder.Reference<Item>> opt = BuiltInRegistries.ITEM.get(id);
        if (opt.isEmpty()) return ItemStack.EMPTY;

        Item item = opt.get().value();
        int count = json.count <= 0 ? 1 : Math.min(json.count, 64);

        return new ItemStack(item, count);
    }
    // ---- decode offers list ----
    private List<ShopOfferData> decodeOffers(List<OfferJson> offersJson) {
        if (offersJson == null) return List.of();

        List<ShopOfferData> out = new ArrayList<>(offersJson.size());
        for (OfferJson oj : offersJson) {
            if (oj == null) continue;

            ItemStack buyA = decodeStack(oj.buyA);
            ItemStack buyB = decodeStack(oj.buyB);
            ItemStack sell = decodeStack(oj.sell);

            if (buyA.isEmpty() || sell.isEmpty()) continue;

            out.add(new ShopOfferData(buyA, buyB, sell));
        }
        return out;
    }
    public List<ShopOfferData> getAllOffers() {
        List<ShopOfferData> out = new ArrayList<>();
        for (ShopEntry e : shops.values()) {
            out.addAll(e.offers());
        }
        return out;
    }

    private static final class RootJson { Map<String, ShopJson> shops; }
    private static final class ShopJson { String name; List<OfferJson> offers; }
    private static final class OfferJson { StackJson buyA, buyB, sell; }
    private static final class StackJson { String item; int count; }
    private record ShopEntry(String name, List<ShopOfferData> offers) {}
}
