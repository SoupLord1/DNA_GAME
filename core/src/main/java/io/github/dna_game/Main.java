package io.github.dna_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import space.earlygrey.shapedrawer.ShapeDrawer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    Pixmap pixmap;
    Texture texture;

    TextureRegion textureRegion;

    ShapeDrawer shapeDrawer;

    Sprite playerSprite;

    Texture backgroundTexture;
    Texture sandTexture;


    Rectangle playerRectangle;

    BitmapFont font;

    Integer count;
    
    Config config = new Config();


    @Override
    public void create() {
        backgroundTexture = new Texture("test/background.png");
        sandTexture = new Texture("tiles/sand.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(config.screenTileWidth, config.screenTileHeight);
        
        playerSprite = new Sprite();
        playerSprite.setSize(1, 1);
        playerSprite.setTexture(new Texture(new Pixmap(1, 1, Format.RGBA8888)));


        playerRectangle = new Rectangle();

        count = 0;

        font = new BitmapFont();

        font.setUseIntegerPositions(false);
		font.getData().setScale((viewport.getWorldHeight() / Gdx.graphics.getHeight()*2));

        pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);

        texture = new Texture(pixmap);
        textureRegion = new TextureRegion(texture, 0, 0, 1, 1);
        shapeDrawer = new ShapeDrawer(spriteBatch, textureRegion);
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {

        float speed = 3.5f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerSprite.translateX(speed * delta);
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerSprite.translateX(-speed * delta);
        } 
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerSprite.translateY(speed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerSprite.translateY(-speed * delta);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float playerWidth = playerSprite.getWidth();
        float playerHeight = playerSprite.getHeight();

        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, worldWidth - playerWidth));
        
        float delta = Gdx.graphics.getDeltaTime();

        playerRectangle.set(playerSprite.getX(), playerSprite.getY(), playerWidth, playerHeight);

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
    
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        
        for (int i = 0; i < config.screenTileHeight; i++) {
            for (int j = 0; j < config.screenTileWidth; j++) {
                spriteBatch.draw(sandTexture,j,i, 1, 1);
            }
        }

        
        
        playerSprite.draw(spriteBatch);

        shapeDrawer.setColor(Color.BLUE);
        shapeDrawer.filledRectangle(playerRectangle);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
    }
}
