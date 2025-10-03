package lando.systems.ld58.utils;

import lombok.AllArgsConstructor;

public final class Rng {

    public static final class Stream {

        private final long seed;
        private final long ctr; // immutable
        public Stream(long seed, long ctr) { this.seed = seed; this.ctr = ctr; }
        public long seed() { return seed; }
        public long ctr()  { return ctr;  }

        @AllArgsConstructor
        public static class Out {
            public int u32;
            public Stream next;
        }

        public Out nextU32() {
            long x = seed ^ ctr;
            long z = splitmix64(x);
            int u32 = (int)(z >>> 32);
            return new Out(u32, new Stream(seed, ctr + 1));
        }
    }

    @AllArgsConstructor
    public static class Streams {
        public Stream world;
        public Stream spawns;
        public Stream loot;
        public Stream ai;
    }

    public static Streams seedAll(long base) {
        return new Streams(
            new Stream(splitmix64(base ^ 0xC0FFEE01L), 0),
            new Stream(splitmix64(base ^ 0xC0FFEE02L), 0),
            new Stream(splitmix64(base ^ 0xC0FFEE03L), 0),
            new Stream(splitmix64(base ^ 0xC0FFEE04L), 0));
    }

    private static long splitmix64(long x) {
        x += 0x9E3779B97F4A7C15L;
        long z = x;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
