package dev.fabien2s.annoyingapi.adapter.entity;

import lombok.Getter;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDataWatcher extends DataWatcher {

    @Getter
    private final Map<DataWatcherObject<?>, Item<?>> itemMap;
    private final List<Item<?>> updatedItems;

    @Getter private boolean dirty;

    public EntityDataWatcher(Entity entity) {
        super(null);


        this.itemMap = new HashMap<>();
        this.updatedItems = output;
    }

    @Override
    public <T> void a(DataWatcherObject<T> datawatcherobject, T t0) {
        super.a(datawatcherobject, t0);
    }

    @Override
    public <T> T a(DataWatcherObject<T> datawatcherobject) {
        return super.a(datawatcherobject);
    }

    @Override
    public <T> void b(DataWatcherObject<T> datawatcherobject, T t0) {
        super.b(datawatcherobject, t0);
    }

    @Override
    public <T> void markDirty(DataWatcherObject<T> datawatcherobject) {
        super.markDirty(datawatcherobject);
    }

    @Override
    public boolean a() {
        return super.a();
    }

    @Override
    public @Nullable List<Item<?>> b() {
        return super.b();
    }

    @Override
    public @Nullable List<Item<?>> c() {
        return super.c();
    }

    @Override
    public void a(List<Item<?>> list) {
        super.a(list);
    }

    @Override
    public boolean d() {
        return super.d();
    }

    @Override
    public void e() {
        super.e();
    }
}
