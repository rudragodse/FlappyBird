package com.rudragodse.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	Texture toptube;
	Texture bottomtube;
	Texture gameover;
	BitmapFont font;


	int w=0;
	int h=0;
	int tw=0;
	int th=0;
	int flapstate=0;
	int score=0;
	int scoringtube=0;


	OrthographicCamera camera=null;
	float birdY=0;
	float velocity=0;
	int gameState=0;
	float gravity=2;
	float gap=400;
	float maxOffset;

	Random randomgenerator;
	int numberoftubes=4;
	float[] tubeX=new float[numberoftubes];
	float[]tubeOffset=new float[numberoftubes];
	float tubevelocity=4;
	float distanceBetweenTubes;

	Circle birdcircle;
	Rectangle[] toptuberectangles;
	Rectangle[]bottomtuberectangles;
	ShapeRenderer shapeRenderer;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background=new Texture("bg.png");
		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");
		toptube=new Texture("toptube.png");
		bottomtube=new Texture("bottomtube.png");
		gameover=new Texture("gameover.png");
		randomgenerator=new Random();
		distanceBetweenTubes=Gdx.graphics.getWidth()*3/4;
        shapeRenderer=new ShapeRenderer();
        birdcircle=new Circle();
        toptuberectangles=new Rectangle[numberoftubes];
        bottomtuberectangles=new Rectangle[numberoftubes];
        font=new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);


        startGame();

		w=Gdx.graphics.getWidth();
		h=Gdx.graphics.getHeight();
		camera=new OrthographicCamera(w,h);
		camera.position.set(w/2,h/2,0);
		camera.update();

        maxOffset=Gdx.graphics.getHeight()/2-gap-100;




	}

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());




        if (gameState == 1) {

            if (tubeX[scoringtube]<Gdx.graphics.getWidth()/2)
            {
                score++;
                Gdx.app.log("Score",String.valueOf(score));
                if (scoringtube<numberoftubes-1)
                {
                    scoringtube++;
                }else {
                    scoringtube=0;
                }
            }

            if (Gdx.input.justTouched())
            {
                velocity=-30;

            }
            for (int i=0;i<numberoftubes;i++)
            {
                if (tubeX[i]<-toptube.getWidth())
                {
                    tubeX[i]+=numberoftubes*distanceBetweenTubes;
                    tubeOffset[i]=(randomgenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
                }else {
                    tubeX[i] = tubeX[i] - tubevelocity;
                }
                batch.draw(toptube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
                batch.draw(bottomtube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight()+tubeOffset[i]);

                toptuberectangles[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],toptube.getWidth(),toptube.getHeight());
                bottomtuberectangles[i]=new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight() +tubeOffset[i],bottomtube.getWidth(),bottomtube.getHeight());

            }







            if (birdY>0)
            {
                velocity=velocity+gravity;
                birdY -= velocity;
            }else {
                gameState=2;
                //Its working great
            }





        }else if (gameState==0)
        {

            if (Gdx.input.justTouched()) {
                gameState=1;
            }

        }else if (gameState==2)
        {
            batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);
            if (Gdx.input.justTouched()) {
                gameState=1;
                startGame();
                score=0;
                velocity=0;
                scoringtube=0;
            }
        }
        if (flapstate == 0)
            flapstate = 1;
        else
            flapstate = 0;

        batch.draw(birds[flapstate], Gdx.graphics.getWidth()/2 - (birds[flapstate].getWidth() / 2), birdY);

        font.draw(batch,String.valueOf(score),100,200);





        birdcircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapstate].getHeight()/2,birds[flapstate].getWidth()/2);

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdcircle.x,birdcircle.y,birdcircle.radius);

        for (int i=0;i<numberoftubes;i++)
        {
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i],toptube.getWidth(),toptube.getHeight());
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight() +tubeOffset[i],bottomtube.getWidth(),bottomtube.getHeight());

            if (Intersector.overlaps(birdcircle,toptuberectangles[i])||Intersector.overlaps(birdcircle,bottomtuberectangles[i]))
            {
                Gdx.app.log("Collision","yes!");
                gameState=2;
            }
        }

        batch.end();
        //shapeRenderer.end();
    }

    private void startGame() {

        birdY=Gdx.graphics.getHeight()/2-(birds[0].getHeight()/2);
        for (int i=0;i<numberoftubes;i++)
        {
            tubeOffset[i]=(randomgenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
            tubeX[i]=Gdx.graphics.getWidth()/2-toptube.getWidth()/2+ Gdx.graphics.getWidth() + i*distanceBetweenTubes;
            toptuberectangles[i]=new Rectangle();
            bottomtuberectangles[i]=new Rectangle();
        }

    }

    @Override
	public void dispose () {
		batch.dispose();

	}
}
