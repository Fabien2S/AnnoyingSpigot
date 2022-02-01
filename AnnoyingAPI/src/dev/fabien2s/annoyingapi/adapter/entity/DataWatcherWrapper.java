package dev.fabien2s.annoyingapi.adapter.entity;

import lombok.Getter;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataWatcherWrapper extends DataWatcher {

    @Getter private final Map<DataWatcherObject<?>, Item<?>> itemMap;
    private final List<Item<?>> updatedItems;

    @Getter private boolean dirty;

    public DataWatcherWrapper() {
        super(null);
        this.itemMap = new HashMap<>();
        this.updatedItems = new ArrayList<>();
    }

    public DataWatcherWrapper(List<Item<?>> output) {
        super(null);
        this.itemMap = new HashMap<>();
        this.updatedItems = output;
    }

    @SuppressWarnings("unchecked")
    public <T> DataWatcher.Item<T> getItem(DataWatcherObject<T> object) {
        return (DataWatcher.Item<T>) this.itemMap.get(object);
    }

    @Override
    public <T> void register(DataWatcherObject<T> object, T t0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(DataWatcherObject<T> object) {
        Item<T> item = getItem(object);
        return item != null ? item.b() : null;
    }

    @Override
    public <T> void set(DataWatcherObject<T> object, T value) {
        Item<T> item = getItem(object);
        if (item == null) {
            item = new Item<>(object, value);
            this.itemMap.put(object, item);
        } else
            item.a(value);

        if (!dirty) {
            this.updatedItems.clear();
            this.dirty = true;
        }

        this.updatedItems.add(item);
    }

    @Override
    public <T> void markDirty(DataWatcherObject<T> object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean a() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public List<Item<?>> b() {
        this.dirty = false;
        return updatedItems;
    }

    @Nullable
    @Override
    public List<Item<?>> c() {
        List<Item<?>> items = new ArrayList<>();
        for (Item<?> item : itemMap.values())
            items.add(item.d());
        return items;
    }

    @Override
    public boolean d() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void e() {
    }


}
