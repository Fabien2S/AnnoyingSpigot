package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NbtCompound extends NbtTag<Map<String, NbtTag<?>>> implements Map<String, NbtTag<?>> {

    public NbtCompound() {
        super(NbtRegistry.COMPOUND_TAG, new HashMap<>());
    }

    public <T extends NbtTag<?>> T get(String key, Class<T> clazz) {
        NbtTag<?> nbtTag = value.get(key);
        if (nbtTag == null)
            throw new NbtFormatException("Missing tag: " + key + " (" + clazz + ')');

        Class<?> aClass = nbtTag.getClass();
        if (clazz.isAssignableFrom(aClass))
            return clazz.cast(nbtTag);

        throw new NbtFormatException("Invalid tag type (" + aClass + " != " + clazz + ')');
    }

    @SuppressWarnings("unchecked")
    public <T extends NbtTag<U>, U> T getList(String key, byte tagId) {
        NbtList<?, ?> tagList = get(key, NbtList.class);
        byte listTagId = tagList.getTagId();
        if (!tagList.isEmpty() && listTagId != tagId)
            throw new NbtFormatException("Invalid list tag type (" + listTagId + " != " + tagId + ')');
        return (T) tagList;
    }

    public <L extends NbtList<T, V>, T extends NbtTag<V>, V> L getList(String key, Class<T> tClass, int length) {
        byte tagId = NbtRegistry.getID(tClass);
        L tagList = getList(key, tagId);
        int listSize = tagList.size();
        if (listSize != length)
            throw new NbtFormatException("Invalid list size (" + listSize + " != " + length + ')');
        return tagList;
    }

    public void addProperty(String key, String value) {
        this.put(key, new NbtString(value));
    }

    public void addProperty(String key, double value) {
        this.put(key, new NbtDouble(value));
    }

    public void addProperty(String key, float value) {
        this.put(key, new NbtFloat(value));
    }

    public void addProperty(String key, int[] value) {
        this.put(key, new NbtIntArray(value));
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public NbtTag<?> get(Object key) {
        return value.get(key);
    }

    @Nullable
    @Override
    public NbtTag<?> put(String key, NbtTag<?> value) {
        return this.value.put(key, value);
    }

    @Override
    public NbtTag<?> remove(Object key) {
        return value.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends NbtTag<?>> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return value.keySet();
    }

    @NotNull
    @Override
    public Collection<NbtTag<?>> values() {
        return value.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, NbtTag<?>>> entrySet() {
        return value.entrySet();
    }
}
