package dev.fabien2s.annoyingapi.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class Skin {

    public static final Skin MISSING = new Skin(
            "eyJ0aW1lc3RhbXAiOjE1MTg2NDYxNjk4MjksInByb2ZpbGVJZCI6IjllNjUwMmNmNmY4MDQ1MzlhNTM5MmRjMzc1Y2IxOWFlIiwicHJvZmlsZU5hbWUiOiJHYW1lc1Bvd2VyIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNzQ5M2QxNGY3ZGEzZWVhNTM0Y2RlY2Y5OTM3NTlkZGEzNGQ3NDIxNjc1OTVlOGJiNTFiOTg3NjZlMzc3ZDcifX19",
            "Ijz9mDp1PnPe7TWvvOp22WFT9L+MlueqhfsAJt51h0u+/icnNfRQsjSv3OdVnZw3sfHhGFqWDrFK+BMYdBNXubFFsq8b6CrpEIIrdTqX4+sTgEY+6T7V9n+4hBUydrjv+uO/MDVhXu9oBLpEfeRrqC4Jk5Wywp2wuMWPs+Y2AuX6xadFaNbyDYQ9kyLZHrP/1ATBxEaPzKmvs8iXkxrKj01eth6LWDcQJTKISJ7p1Ft76T8NiKwuknnqqUVYC0HFrdyiJJNYWbmJ3rgPCdARg3Q1HkXe/sKu//aOSTKIIKavwfwBsSdx5BXYATvhRWnkNP8jrD8E0zkwjMOLY4HVuhSlUtH/DInW36bqY7NIslhp7SBVpubi4OeICB9iqTKyWlogYxwQgBkb4mI1k9m+hQ1GlD2H9rqUDktbkaOo5rcdrB3yOgDxoFpIsMkeSnJu9RgOqMwhZ1jJJf0KgD2gbEAWqQk4r1mJQCKyqvCgJO+MI8JRn0irxe5dXZQ5YfcPnil04ghuNCff/Nn1XFZNtD0SQV6DTxf4bAlrjxE/jltYI4oBPZV9wIN5Rbk+M+/cQQWOC0vgvyk5Q7EoMVpipy1zJD5WQ4B7AxmDWhDz3qipUTByf1yZ/nYw7TQch9j+6sNN7cOO8MWucp0XWpHjStPIKhuBe6NAxWRLtF1kKIM="
    );

    private final SkinEntry[] entries;

    public Skin(String value, String signature) {
        this.entries = new SkinEntry[]{new SkinEntry(value, signature)};
    }

    public void apply(GameProfile gameProfile) {
        PropertyMap properties = gameProfile.getProperties();
        properties.clear();

        for (SkinEntry entry : entries) {
            Property property = new Property("textures", entry.value, entry.signature);
            properties.put("textures", property);
        }
    }

    public List<Property> toPropertyList() {
        ArrayList<Property> propertyList = new ArrayList<>();
        for (SkinEntry entry : entries)
            propertyList.add(new Property("textures", entry.value, entry.signature));
        return propertyList;
    }

    public static Skin fromProfile(GameProfile gameProfile) {
        PropertyMap properties = gameProfile.getProperties();
        Collection<Property> textures = properties.get("textures");

        SkinEntry[] skinEntries = new SkinEntry[textures.size()];
        int i = 0;
        for (Property texture : textures)
            skinEntries[i++] = new SkinEntry(texture.getValue(), texture.getSignature());

        return new Skin(skinEntries);
    }

    private record SkinEntry(String value, String signature) {
    }

}
