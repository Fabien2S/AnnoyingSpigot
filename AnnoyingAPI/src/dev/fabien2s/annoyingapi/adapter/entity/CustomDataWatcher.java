package dev.fabien2s.annoyingapi.adapter.entity;

import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CustomDataWatcher extends DataWatcher {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<DataWatcherObject<?>, Item<?>> itemMap = new HashMap<>();

    private boolean isDirty;
    private boolean hasElements;

    public CustomDataWatcher() {
        super(null);
    }

    @SuppressWarnings("unchecked")
    private <T> DataWatcher.Item<T> getItem(DataWatcherObject<T> object) {
        return (DataWatcher.Item<T>) this.itemMap.get(object);
    }

    @Override
    public <T> void a(DataWatcherObject<T> object, T value) {
        // a(DataWatcherObject<T> object, T value) -> define(EntityDataAccessor<T> accessor, T value)

        this.lock.writeLock().lock();
        this.itemMap.put(object, new Item<>(object, value));
        this.hasElements = true;
        this.lock.writeLock().unlock();
    }

    @Override
    public <T> T a(DataWatcherObject<T> object) {
        // a(DataWatcherObject<T> object) -> get(EntityDataAccessor<T> accessor)
        return getItem(object).b();
    }

    @Override
    public <T> void b(DataWatcherObject<T> object, T value) {
        // b(DataWatcherObject<T> object, T value) -> set(EntityDataAccessor<T> accessor, T value)

        Item<T> item = getItem(object);
        if (ObjectUtils.notEqual(value, item.b())) {
            item.a(value);
            item.a(true);
            this.isDirty = true;
        }
    }

    @Override
    public <T> void markDirty(DataWatcherObject<T> object) {
        Item<T> item = getItem(object);
        item.a(true);

        this.isDirty = true;
    }

    @Override
    public boolean a() {
        // a() -> isDirty()
        return this.isDirty;
    }

    @Override
    @Nullable
    public List<Item<?>> b() {
        // b() -> packDirty()

        List<Item<?>> itemList = null;
        this.lock.readLock().lock();

        if (this.isDirty) {
            itemList = new ArrayList<>();

            Collection<Item<?>> itemCollection = itemMap.values();
            for (Item<?> item : itemCollection) {
                // item.c() -> item.isDirty()
                if (!item.c())
                    continue;

                // item.a(false) -> item.setDirty(false)
                item.a(false);

                // item.d() -> item.copy();
                Item<?> itemCopy = item.d();
                itemList.add(itemCopy);
            }

            this.isDirty = false;
        }

        this.lock.readLock().unlock();
        return itemList;
    }

    @Override
    @Nullable
    public List<Item<?>> c() {
        // c() -> getAll()

        List<Item<?>> itemList = null;
        this.lock.readLock().lock();

        if (!itemMap.isEmpty()) {
            Collection<Item<?>> itemCollection = itemMap.values();
            itemList = List.copyOf(itemCollection);
        }

        this.lock.readLock().unlock();
        return itemList;
    }

    @Override
    public void a(List<Item<?>> list) {
        // a(List<Item<?>> list) -> assignValues(List<SynchedEntityData.DataItem<?>> list)

        this.lock.writeLock().lock();

        for (Item<?> item : list) {
            DataWatcherObject<?> object = item.a();

            @SuppressWarnings("unchecked")
            Item<Object> definedItem = (Item<Object>) itemMap.get(object);
            if (definedItem != null)
                definedItem.a(item.b());
        }

        this.isDirty = true;
        this.lock.writeLock().unlock();
    }

    @Override
    public boolean d() {
        // d() -> isEmpty()
        return !this.hasElements;
    }

    @Override
    public void e() {
        // e() -> clearDirty()

        this.isDirty = false;

        this.lock.readLock().lock();
        Collection<Item<?>> itemCollection = itemMap.values();
        for (Item<?> item : itemCollection)
            item.a(false);
        this.lock.readLock().unlock();
    }
}
