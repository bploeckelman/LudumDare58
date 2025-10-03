package lando.systems.ld58.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld58.game.components.collision.CollisionGrid;
import lando.systems.ld58.game.components.collision.CollisionMask;
import lando.systems.ld58.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Tilemap implements Component {

    private static final String TAG = Tilemap.class.getSimpleName();
    private static final TmxMapLoader.Parameters params = new TmxMapLoader.Parameters() {{
        generateMipMaps = true;
        textureMinFilter = Texture.TextureFilter.MipMapLinearLinear;
        textureMagFilter = Texture.TextureFilter.MipMapLinearLinear;
    }};

    public static final String DEFAULT_OBJECT_LAYER_NAME = "Objects";
    public static final String DEFAULT_COLLISION_LAYER_NAME = "solid";
    public static final float UNIT_SCALE = 1f;

    public final List<TiledMapTileLayer> layers;
    public final List<MapObject> objects = new ArrayList<>();
    public final String objectLayerName;
    public final Rectangle bounds;

    public final TiledMap map;
    public final int cols;
    public final int rows;
    public final int tileSize;

    public OrthographicCamera camera;

    public Tilemap(String tmxFilePath, OrthographicCamera worldCamera) {
        this(tmxFilePath, DEFAULT_OBJECT_LAYER_NAME, worldCamera);
    }

    public Tilemap(String tmxFilePath, String objectLayerName, OrthographicCamera camera) {
        this.camera = camera;
        this.objectLayerName = objectLayerName;
        this.map = (new TmxMapLoader()).load(tmxFilePath, params);
        this.layers = StreamSupport.stream(map.getLayers().spliterator(), false)
            .filter(layer -> !layer.getName().equals("solid"))
            .filter(layer -> layer instanceof TiledMapTileLayer)
            .map(layer -> (TiledMapTileLayer) layer)
            .collect(Collectors.toList());
        this.bounds = new Rectangle();

        var props = map.getProperties();
        this.cols = props.get("width", Integer.class);
        this.rows = props.get("height", Integer.class);
        this.tileSize = props.get("tilewidth", Integer.class);

        loadObjects();
    }

    public TiledMapRenderer newRenderer(SpriteBatch batch) {
        return new OrthogonalTiledMapRenderer(map, UNIT_SCALE, batch);
    }

    public Collider newGridCollider() {
        return newGridCollider(DEFAULT_COLLISION_LAYER_NAME);
    }

    public Collider newGridCollider(String layerName) {
        var layer = map.getLayers().get(layerName);
        if (layer instanceof TiledMapTileLayer) {
            var solidLayer = (TiledMapTileLayer) layer;
            var collider = Collider.grid(CollisionMask.SOLID, tileSize, cols, rows);
            var grid = collider.shape(CollisionGrid.class);
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    var isSolid = (null != solidLayer.getCell(x, y));
                    grid.set(x, y, isSolid);
                }
            }
            return collider;
        }
        throw new GdxRuntimeException(Stringf.format(
            "Unable to create grid collider, layer '%s' not found or not TiledMapTileLayer type", layerName));
    }

    public Bounds newBounds() {
        return new Bounds(calcBounds());
    }

    public Rectangle calcBounds() {
        return calcBounds(null);
    }

    public Rectangle calcBounds(Position pos) {
        var x = (pos != null) ? pos.x() : 0f;
        var y = (pos != null) ? pos.y() : 0f;
        return bounds.set(x, y, cols * tileSize, rows * tileSize);
    }

    private void loadObjects() {
        var objectLayer = map.getLayers().get(objectLayerName);
        if (objectLayer == null) {
            Util.log(TAG, Stringf.format("No object layer found with name '%s'", objectLayerName));
            return;
        }

        Util.log(TAG, "Loading map objects...");
        int count = 0;
        for (var mapObject : objectLayer.getObjects()) {
            var name  = mapObject.getName();
            var props = mapObject.getProperties();
            objects.add(mapObject);
            count++;

            var id = props.get("id", -1, Integer.class);
            Util.log(TAG, Stringf.format("Loaded object %d - '%s'", id, name));
        }
        Util.log(TAG, Stringf.format("Loaded %d map objects.", count));
    }
}
