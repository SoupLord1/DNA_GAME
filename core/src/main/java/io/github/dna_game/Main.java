package io.github.dna_game;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    Sprite bucketSprite;

    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;

    Array<Sprite> dropSprites;

    float dropTimer;

    Rectangle bucketRectangle;
    Rectangle dropRectangle;

    BitmapFont font;

    Integer count;

    public static void shutdown() throws RuntimeException, IOException {
    String shutdownCommand;
    String operatingSystem = System.getProperty("os.name");

    if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
        shutdownCommand = "shutdown -h now";
    }
    // This will work on any version of windows including version 11 
    else if (operatingSystem.contains("Windows")) {
        shutdownCommand = "shutdown.exe -s -t 0";
    }
    else {
        throw new RuntimeException("Unsupported operating system.");
    }

    Runtime.getRuntime().exec(shutdownCommand);
    System.exit(0);
}

    @Override
    public void create() {
        backgroundTexture = new Texture("test/background.png");
        bucketTexture = new Texture("test/bucket.png");
        dropTexture = new Texture("test/drop.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("test/drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("test/music.mp3"));
        
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        
        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1, 1);

        dropSprites = new Array<>();

        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();
        
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();

        count = 0;

        font = new BitmapFont();

        font.setUseIntegerPositions(false);
		font.getData().setScale((viewport.getWorldHeight() / Gdx.graphics.getHeight()*2));
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

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucketSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucketSprite.translateX(-speed * delta);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidth - bucketWidth));
        
        float delta = Gdx.graphics.getDeltaTime();

        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i);
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();

            dropSprite.translateY(-2f * delta);

            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropSprite.getWidth(), dropSprite.getHeight());

            

            if (dropSprite.getY() < -dropHeight){
                dropSprites.removeIndex(i); 
                try {
                    shutdown();
                } catch (RuntimeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (bucketRectangle.overlaps(dropRectangle)) {
                dropSprites.removeIndex(i);
                dropSound.play();
                count += 1;
            }
        }
        
        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createDroplet();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        
        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        bucketSprite.draw(spriteBatch);

        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }

        font.draw(spriteBatch, "Points: "+count, 1f,1f);

    
        spriteBatch.end();
    }

    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }

    @Override
    public void dispose() {
    }
}
