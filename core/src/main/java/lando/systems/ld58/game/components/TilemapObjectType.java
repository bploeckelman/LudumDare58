package lando.systems.ld58.game.components;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public interface TilemapObjectType {

    String mapTypeName();

    class Registry {
        private static final Map<String, TilemapObjectType> MAP = new HashMap<>();
        static {
            for (var value : Enemies.values()) MAP.put(value.mapTypeName(), value);
            for (var value : Blocks.values())  MAP.put(value.mapTypeName(), value);
            for (var value : Pickups.values()) MAP.put(value.mapTypeName(), value);
            for (var value : Misc.values())    MAP.put(value.mapTypeName(), value);
        }
        public static TilemapObjectType get(String mapTypeName) {
            return MAP.get(mapTypeName);
        }
    }

    @RequiredArgsConstructor
    enum Enemies implements TilemapObjectType {
          ANGRY_SUN("sun")
        , BULLET_BILL("bullet")
        , CAPTAIN_LOU("lou")
        , GOOMBA_CYBORG("goomba")
        , HAMMER_BRO("hammer")
        , KOOPA("koopa")
        , LAKITU("lakitu")
        , MARIO("mario")
        , MISTY("misty")
        ;
        public final String mapTypeName;
        public String mapTypeName() { return mapTypeName; }
    }

    @RequiredArgsConstructor
    enum Blocks implements TilemapObjectType {
          BREAK("block")
        , COIN("coin-block")
        , SPIKE("spike")
        , LAVA("lava")
        ;
        public final String mapTypeName;
        public String mapTypeName() { return mapTypeName; }
    }

    @RequiredArgsConstructor
    enum Pickups implements TilemapObjectType {
          COIN("coin")
        , RELIC_PLUNGER("plunger")
        , RELIC_TORCH("torch")
        , RELIC_WRENCH("wrench")
        , SHROOM("shroom")
        ;
        public final String mapTypeName;
        public String mapTypeName() { return mapTypeName; }
    }

    @RequiredArgsConstructor
    enum Misc implements TilemapObjectType {
        TRIGGER("trigger")
        ;
        public final String mapTypeName;
        public String mapTypeName() { return mapTypeName; }
    }
}
