package com.Thelnfamous1.craft_of_the_wild.entity;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class PartEntityController<T extends Entity> {
    private final Map<String, T> partsByName;
    private final Map<String, PartTicker<T>> tickersByName;

    public PartEntityController(Map<String, T> partsByName, Map<String, PartTicker<T>> tickersByName){
        this.partsByName = partsByName;
        this.tickersByName = tickersByName;
    }

    public Optional<T> getOptionalPart(String name){
        return Optional.ofNullable(this.getPart(name));
    }

    @Nullable
    public T getPart(String name) {
        return this.partsByName.get(name);
    }

    public Collection<T> getParts() {
        return this.partsByName.values();
    }

    public void tick() {
        this.partsByName.forEach((name, part) -> {
            double prevX = part.getX();
            double prevY = part.getY();
            double prevZ = part.getZ();

            PartTicker<T> ticker = this.tickersByName.get(name);
            if(ticker != null) ticker.tick(part);

            part.xo = prevX;
            part.yo = prevY;
            part.zo = prevZ;
            part.xOld = prevX;
            part.yOld = prevY;
            part.zOld = prevZ;
        });
    }

    public static class Builder<T extends Entity>{
        private final Map<String, T> partsByName = new LinkedHashMap<>();
        private final Map<String, PartTicker<T>> tickersByName = new LinkedHashMap<>();
        @Nullable
        private Function<T, String> nameProvider;

        public Builder(){
        }

        public Builder<T> useNameProvider(Function<T, String> nameGetter){
            this.nameProvider = nameGetter;
            return this;
        }

        public Builder<T> addPart(T part){
            if(this.nameProvider == null) throw new IllegalStateException("Cannot add part without a name!");
            return this.addPart(this.nameProvider.apply(part), part);
        }

        public Builder<T> addPart(String name, T part){
            this.partsByName.put(name, part);
            return this;
        }

        public Builder<T> addPart(String name, T part, PartTicker<T> ticker){
            this.partsByName.put(name, part);
            this.tickersByName.put(name, ticker);
            return this;
        }

        public Builder<T> universalTicker(PartTicker<T> ticker){
            if(this.partsByName.keySet().isEmpty()) throw new IllegalStateException("Cannot add a universal ticker without any added parts!");
            this.partsByName.keySet().forEach(name -> this.tickersByName.put(name, ticker));
            return this;
        }

        public PartEntityController<T> build(){
            return new PartEntityController<>(Collections.unmodifiableMap(this.partsByName), Collections.unmodifiableMap(this.tickersByName));
        }
    }

    @FunctionalInterface
    public interface PartTicker<T extends Entity>{
        void tick(T part);
    }

    public record Info(String name, float width, float height, boolean bodyPart, double xOffset, double yOffset, double zOffset,
                       float scale) {
    }
}