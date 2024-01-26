package com.Thelnfamous1.craft_of_the_wild.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class PartEntityController<P extends Entity, T extends Entity> {
    private final Map<String, Pair<T, PartInfo>> partsByName;
    private final Map<String, PartTicker<P, T>> tickersByName;
    private final P parent;

    public PartEntityController(P parent, Map<String, Pair<T, PartInfo>> partsByName, Map<String, PartTicker<P, T>> tickersByName){
        this.parent = parent;
        this.partsByName = partsByName;
        this.tickersByName = tickersByName;
    }

    public Optional<T> getOptionalPart(String name){
        return Optional.ofNullable(this.getPart(name));
    }

    @Nullable
    public T getPart(String name) {
        Pair<T, PartInfo> partInfoPair = this.partsByName.get(name);
        if(partInfoPair == null) return null;
        return partInfoPair.getFirst();
    }

    public List<T> collectParts() {
        List<T> parts = new ArrayList<>(this.partsByName.values().size());
        for(Pair<T, PartInfo> partInfoPair : this.partsByName.values()){
            parts.add(partInfoPair.getFirst());
        }
        return Collections.unmodifiableList(parts);
    }

    public void tick() {
        this.partsByName.forEach((name, partInfoPair) -> {
            T part = partInfoPair.getFirst();
            double prevX = part.getX();
            double prevY = part.getY();
            double prevZ = part.getZ();

            PartTicker<P, T> ticker = this.tickersByName.get(name);
            if(ticker != null) ticker.tickPart(part, this.parent, partInfoPair.getSecond());

            part.xo = prevX;
            part.yo = prevY;
            part.zo = prevZ;
            part.xOld = prevX;
            part.yOld = prevY;
            part.zOld = prevZ;
        });
    }

    public static class Builder<P extends Entity, T extends Entity>{
        private final Map<String, Pair<T, PartInfo>> partsByName = new LinkedHashMap<>();
        private final Map<String, PartTicker<P, T>> tickersByName = new LinkedHashMap<>();
        private final P parent;
        @Nullable
        private Function<T, String> nameProvider;

        public Builder(P parent){
            this.parent = parent;
        }

        public Builder<P, T> useNameProvider(Function<T, String> nameProvider){
            this.nameProvider = nameProvider;
            return this;
        }

        public Builder<P, T> addPart(T part, PartInfo partInfo){
            if(this.nameProvider == null) throw new IllegalStateException("Cannot add part without a name!");
            return this.addPart(this.nameProvider.apply(part), part, partInfo);
        }

        public Builder<P, T> addPart(String name, T part, PartInfo partInfo){
            this.partsByName.put(name, Pair.of(part, partInfo));
            return this;
        }

        public Builder<P, T> addPart(String name, T part, PartInfo partInfo, PartTicker<P, T> ticker){
            this.addPart(name, part, partInfo);
            this.tickersByName.put(name, ticker);
            return this;
        }

        public Builder<P, T> universalTicker(PartTicker<P, T> ticker){
            if(this.partsByName.keySet().isEmpty()) throw new IllegalStateException("Cannot add a universal ticker without any added parts!");
            this.partsByName.keySet().forEach(name -> this.tickersByName.put(name, ticker));
            return this;
        }

        public PartEntityController<P, T> build(){
            return new PartEntityController<>(this.parent, Collections.unmodifiableMap(this.partsByName), Collections.unmodifiableMap(this.tickersByName));
        }
    }

    @FunctionalInterface
    public interface PartTicker<P extends Entity, T extends Entity>{
        void tickPart(T part, P parent, PartInfo partInfo);
    }

    @FunctionalInterface
    public interface PartResizer<P extends Entity>{
        EntityDimensions resizePart(Entity part, P parent, EntityDimensions defaultSize);
    }

    public record PartInfo(String name, float width, float height, boolean bodyPart, double xOffset, double yOffset, double zOffset,
                           float scale) {
    }
}