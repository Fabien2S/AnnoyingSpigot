package dev.fabien2s.annoyingapi.skin;

import javax.annotation.Nullable;

public interface ISkinHolder {

    @Nullable
    Skin getSkin();

    void setSkin(@Nullable Skin skin);

}
