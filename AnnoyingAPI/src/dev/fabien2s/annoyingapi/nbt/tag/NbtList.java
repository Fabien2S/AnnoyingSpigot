package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NbtList<T extends NbtTag<U>, U> extends NbtTag<List<T>> implements List<T> {

    public NbtList() {
        super(NbtRegistry.LIST_TAG, new ArrayList<>());
    }

    public NbtList(int length) {
        super(NbtRegistry.LIST_TAG, new ArrayList<>(length));
    }

    public byte getTagId() {
        return value.isEmpty() ? NbtRegistry.END_TAG : value.get(0).getId();
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
    public boolean contains(Object o) {
        return value.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return value.toArray(a);
    }

    @Override
    public boolean add(T tag) {
        return value.add(tag);
    }

    @Override
    public boolean remove(Object o) {
        return value.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return value.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return value.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return value.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return value.retainAll(c);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public T get(int index) {
        return value.get(index);
    }

    @Override
    public T set(int index, T element) {
        return value.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        this.value.add(index, element);
    }

    @Override
    public T remove(int index) {
        return value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return value.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return value.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return value.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return value.subList(fromIndex, toIndex);
    }

}
