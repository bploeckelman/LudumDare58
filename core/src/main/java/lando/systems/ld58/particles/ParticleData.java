package lando.systems.ld58.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld58.utils.SimplePath;

public class ParticleData implements Pool.Poolable {

    // TODO: add additional interpolators so that some properties can be interpolated independent of others (alpha vs anim for exaample)
    // TODO: add a 'drop shadow' flag to particle and initializer to improve readability for things like text particles
    // TODO: add optional [x|y]Jitter so several can be spawned at once with the same params but have a little variation in position
    // TODO: add optional arrays of sizes / rotations so that multiple values can be interpolated across throughout the lifetime of the particle
    // eg. sizes[{10,10}, {20, 20}, {10, 10}] would interpolate to twice the size by halfway through its lifetime, then back down to initial size by the end, scale this across an arbitrary number of values


    public static Initializer initializer(ParticleData particleData) {
        return new Initializer(particleData);
    }

    public SimplePath path;
    public Interpolation interpolation;

    public TextureRegion keyframe;

    public Animation<TextureRegion> animation;
    public float animTime;
    public boolean animUnlocked;

    public float xStart;
    public float yStart;

    public boolean targeted;
    public float xTarget;
    public float yTarget;

    public Vector2 velocity;
    public float bounceScale;

    public Vector2 accel;
    public float accDamp;

    public float widthStart;
    public float widthEnd;
    public float width;

    public float heightStart;
    public float heightEnd;
    public float height;

    public float rotationStart;
    public float rotationEnd;
    public float rotation;

    public float rStart, gStart, bStart, aStart;
    public float rEnd, gEnd, bEnd, aEnd;
    public float r, g, b, a;

    public boolean timed;
    public float ttlMax;
    public float ttl;

    public boolean dead;
    public boolean persistent;
    public Circle collisionBounds;
    public Rectangle collisionRect;

    public ParticleData() {
        velocity = new Vector2();
        accel = new Vector2();
        collisionBounds = new Circle();
        collisionRect = new Rectangle();
        reset();
    }

//    @Deprecated(since = "use renderable component")
//    public void render(SpriteBatch batch) {
//        if (keyframe == null) return;
//        batch.setColor(r, g, b, a);
//        batch.draw(keyframe,
//            position.x - width / 2f, position.y - height / 2f,
//            width / 2f, height / 2f,
//            width, height, 1f, 1f,
//            rotation);
//        batch.setColor(1f, 1f, 1f, 1f);
//    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void reset() {
        this.path = null;
        this.interpolation = Interpolation.linear;

        this.keyframe = null;

        this.animation = null;
        this.animUnlocked = false;
        this.animTime = 0f;

        this.xStart = 0f;
        this.yStart = 0f;

        this.targeted = false;
        this.xTarget = 0f;
        this.yTarget = 0f;

        this.velocity.set(0,0);
        this.bounceScale = .8f;

        this.accel.set(0, 0);
        this.accDamp = 0f;

        this.widthStart = 0f;
        this.widthEnd = 0f;
        this.width = 0f;

        this.heightStart = 0f;
        this.heightEnd = 0f;
        this.height = 0f;

        this.rotationStart = 0f;
        this.rotationEnd = 0f;
        this.rotation = 0f;

        this.rStart = 1f;
        this.gStart = 1f;
        this.bStart = 1f;
        this.aStart = 1f;
        this.rEnd = 1f;
        this.gEnd = 1f;
        this.bEnd = 1f;
        this.aEnd = 1f;
        this.r = rStart;
        this.g = gStart;
        this.b = bStart;
        this.a = aStart;

        this.timed = false;
        this.ttlMax = 0f;
        this.ttl = 0f;

        this.dead = true;
        this.persistent = false;
        this.collisionBounds.set(0,0,0);
    }

    // ------------------------------------------------------------------------

    public static class Initializer {

        private final ParticleData particleData;

        private SimplePath path = null;
        private TextureRegion keyframe = null;
        private Animation<TextureRegion> animation = null;
        private Interpolation interpolation = Interpolation.linear;
        private boolean animUnlocked = false;

        private float xStart = 0f;
        private float yStart = 0f;

        private boolean targeted = false;
        private float xTarget = 0f;
        private float yTarget = 0f;

        private float xVel = 0f;
        private float yVel = 0f;

        private float bounceScale = .8f;

        private float xAcc = 0f;
        private float yAcc = 0f;
        private float accDamp = 0f;

        private float widthStart = 0f;
        private float widthEnd = 0f;
        private boolean setWidthEnd = false;

        private float heightStart = 0f;
        private float heightEnd = 0f;
        private boolean setHeightEnd = false;

        private float rotationStart = 0f;
        private float rotationEnd = 0f;
        private boolean setRotationEnd = false;

        private float rStart = 1f;
        private float gStart = 1f;
        private float bStart = 1f;
        private float aStart = 1f;
        private float rEnd = 1f;
        private float gEnd = 1f;
        private float bEnd = 1f;
        private float aEnd = 1f;
        private boolean setColorEnd = false;
        private boolean setAlphaEnd = false;

        private boolean persistent = false;
        private boolean timed = false;
        private float ttlMax = 0f;

        public Initializer(ParticleData particleData) {
            this.particleData = particleData;
            this.particleData.reset();
        }

        public Initializer interpolation(Interpolation interpolation) {
            this.interpolation = interpolation;
            return this;
        }

        public Initializer path(SimplePath path) {
            this.path = path;
            return this;
        }

        public Initializer keyframe(TextureRegion keyframe) {
            this.keyframe = keyframe;
            return this;
        }

        public Initializer animation(Animation<TextureRegion> animation) {
            this.animation = animation;
            return this;
        }

        public Initializer animUnlocked(boolean animUnlocked) {
            this.animUnlocked = animUnlocked;
            return this;
        }

        public Initializer startPos(float x, float y) {
            this.xStart = x;
            this.yStart = y;
            return this;
        }

        public Initializer targetPos(float x, float y) {
            this.xTarget = x;
            this.yTarget = y;
            this.targeted = true;
            return this;
        }

        public Initializer velocity(float x, float y) {
            this.xVel = x;
            this.yVel = y;
            return this;
        }

        public Initializer velocityDirection(float angle, float magnitude) {
            this.xVel = MathUtils.cosDeg(angle) * magnitude;
            this.yVel = MathUtils.sinDeg(angle) * magnitude;
            return this;
        }

        public Initializer acceleration(float x, float y) {
            this.xAcc = x;
            this.yAcc = y;
            return this;
        }

        public Initializer accelerationDamping(float damp) {
            this.accDamp = damp;
            return this;
        }

        public Initializer startSize(float width, float height) {
            this.widthStart = width;
            this.heightStart = height;
            return this;
        }

        public Initializer startSize(float size) {
            this.widthStart = size;
            this.heightStart = size;
            return this;
        }

        public Initializer endSize(float width, float height) {
            this.widthEnd = width;
            this.heightEnd = height;
            this.setWidthEnd = true;
            this.setHeightEnd = true;
            return this;
        }

        public Initializer endSize(float size) {
            this.widthEnd = size;
            this.heightEnd = size;
            this.setWidthEnd = true;
            this.setHeightEnd = true;
            return this;
        }

        public Initializer startRotation(float rotation) {
            this.rotationStart = rotation;
            return this;
        }

        public Initializer endRotation(float rotation) {
            this.rotationEnd = rotation;
            this.setRotationEnd = true;
            return this;
        }

        public Initializer startColor(float r, float g, float b, float a) {
            this.rStart = r;
            this.gStart = g;
            this.bStart = b;
            this.aStart = a;
            return this;
        }

        public Initializer startColor(Color color) {
            this.rStart = color.r;
            this.gStart = color.g;
            this.bStart = color.b;
            this.aStart = color.a;
            return this;
        }

        public Initializer endColor(float r, float g, float b, float a) {
            this.rEnd = r;
            this.gEnd = g;
            this.bEnd = b;
            this.aEnd = a;
            this.setColorEnd = true;
            return this;
        }

        public Initializer endColor(Color color) {
            this.rEnd = color.r;
            this.gEnd = color.g;
            this.bEnd = color.b;
            this.aEnd = color.a;
            this.setColorEnd = true;
            return this;
        }

        public Initializer startAlpha(float a) {
            this.aStart = a;
            return this;
        }

        public Initializer endAlpha(float a) {
            this.aEnd = a;
            this.setAlphaEnd = true;
            return this;
        }

        public Initializer timeToLive(float ttl) {
            this.ttlMax = ttl;
            this.timed = true;
            return this;
        }

        public Initializer persist() {
            this.persistent = true;
            return this;
        }

        public ParticleData init() {
            if (keyframe  != null) {
                particleData.keyframe  = keyframe;
            }
            if (animation != null) {
                particleData.animation = animation;
                particleData.animTime = 0f;
                particleData.animUnlocked = animUnlocked;
            }
            if (path != null) {
                if (!timed) {
                    throw new GdxRuntimeException("Particles with a path must also have a time to live, is your Particle.Initializer missing a call to timeToLive()?");
                }
                particleData.path = path;
            }
            if (interpolation != null) {
                particleData.interpolation = interpolation;
            }

            particleData.xStart = xStart;
            particleData.yStart = yStart;

            particleData.targeted = targeted;
            particleData.xTarget = xTarget;
            particleData.yTarget = yTarget;
            if (targeted && !timed) {
                throw new GdxRuntimeException("Particles with a target must also have a time to live, is your Particle.Initializer missing a call to timeToLive()?");
            }

            particleData.velocity.set(xVel, yVel);
            particleData.bounceScale = bounceScale;

            particleData.accel.set(xAcc, yAcc);
            particleData.accDamp = accDamp;

            particleData.widthStart = widthStart;
            particleData.widthEnd = (setWidthEnd) ? widthEnd : widthStart;
            particleData.width = widthStart;

            particleData.heightStart = heightStart;
            particleData.heightEnd = (setHeightEnd) ? heightEnd : heightStart;
            particleData.height = heightStart;

            if ((particleData.widthStart  == 0f && particleData.widthEnd  == 0f)
                || (particleData.heightStart == 0f && particleData.heightEnd == 0f)) {
                Gdx.app.log("WARN", "A particle has been created with degenerate size (starting and ending width or height both equal zero), you probably didn't mean to do this as this means the particle won't be visible");
            }

            particleData.rotationStart = rotationStart;
            particleData.rotationEnd = (setRotationEnd) ? rotationEnd : rotationStart;
            particleData.rotation = rotationStart;

            particleData.rStart = rStart;
            particleData.gStart = gStart;
            particleData.bStart = bStart;
            particleData.aStart = aStart;
            particleData.rEnd = (setColorEnd) ? rEnd : rStart;
            particleData.gEnd = (setColorEnd) ? gEnd : gStart;
            particleData.bEnd = (setColorEnd) ? bEnd : bStart;
            particleData.aEnd = (setColorEnd || setAlphaEnd) ? aEnd : aStart;
            particleData.r = rStart;
            particleData.g = gStart;
            particleData.b = bStart;
            particleData.a = aStart;

            if (particleData.aStart == 0f && particleData.aEnd == 0f) {
                Gdx.app.log("WARN", "A particle has been created with degenerate alpha (starting and ending alpha both equal zero), you probably didn't mean to do this as this means the particle won't be visible");
            }

            particleData.timed = timed;
            particleData.ttlMax = ttlMax;
            particleData.ttl = ttlMax;

            particleData.persistent = persistent;
            particleData.dead = false;

            return particleData;
        }

    }

}
