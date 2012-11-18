package cloudy.cloudshooting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cloudy.cloudshooting.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class Panel extends SurfaceView implements Runnable {
	static Canvas canvas;
	static Bitmap sling, rock, cross, rock2, rock3, rock4, cloud;
	static SurfaceHolder surfHolder;
	static float initX, initY, finalX, finalY, startX, startY, moveToX, moveToY, speedX, speedY, animX, animY, xVal, yVal = 0;
	static Thread panelThread 	= null;
	static final long FPS 		= 40;
	static Boolean running 		= false;
	boolean bumpY, bumpX, randomVals  = false;
	Paint outOfBounds, filterPaint;
	Context context;
	Activity activity = new AnimeAction(); 
	Matrix matrix     = new Matrix();
	List<Bitmap> imgList = new ArrayList<Bitmap>();
	List<Integer> xVals  = new ArrayList<Integer>();
	List<Integer> yVals  = new ArrayList<Integer>();
	SoundKeeper Sounds;
	Explosions Explode;

	
	/*
	 * PANEL CONTRUCTOR AND RESOURCE INIT
	 */
	public Panel(Context context) {
		super(context);
		this.context = context;
		
		Explode = new Explosions(context);
		Sounds  = new SoundKeeper();
		
		//INITIALIZE DRAWABLES
		sling 	   =  BitmapFactory.decodeResource(getResources(),R.drawable.sling);
		rock  	   =  BitmapFactory.decodeResource(getResources(), R.drawable.rock);
		cross      =  BitmapFactory.decodeResource(getResources(),R.drawable.crosshair);
		cloud 	   =  BitmapFactory.decodeResource(getResources(),R.drawable.cloud2);
		surfHolder = getHolder();
		
		//SOUND IMPORTS
		Sounds.initSounds(context);
		Sounds.addSound(1, R.raw.explosion);
		
		//GRAPHIC SETUP
		Explode.initExplosions(0, R.drawable.explosion1);
		Explode.initExplosions(1, R.drawable.explosion2);
		Explode.initExplosions(2, R.drawable.explosion3);
		
		//SETS UP TOUCH LISTENER FOR SURFACEVIEW
		this.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				Panel.this.onTouch(event);
				return true;
			}
		});
		
		//PAINT OBJECT AND COLOR SCHEME
		outOfBounds = new Paint(); 
		filterPaint = new Paint();
		filterPaint.setFilterBitmap(true);
		outOfBounds.setARGB(255, 171, 171, 171);
		
		//CREATE BITMAP LIST ARRAY
		initAnswerBitmaps();
		
		
		
	}

	
	
	/*
	 * RUNNABLE FUNCTION CALL 
	 */
	public void run() {
		long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        
		while (running) {
			if (!surfHolder.getSurface().isValid())
				continue;
			startTime = System.currentTimeMillis();
			
			synchronized(surfHolder){
				displayGame(ticksPS);
				
			}
		   sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
		   try {
               if (sleepTime > 0)
                      Thread.sleep(sleepTime);
               else
                      Thread.sleep(10);
		   } catch (Exception e) {}
		}
		
	}

	/*
	 * THIS DRAWS THE BITMAPS TO SCREEN WITH GIVEN TOUCHED COORDINATES
	 */
	private void displayGame(long ticks) {
		canvas = surfHolder.lockCanvas();
		canvas.drawRGB(15,03,175);
		startX = getWidth()/2 - sling.getWidth()/2;
		startY = getHeight() - sling.getHeight();
	
	
		if(!randomVals){
			for(int i=0;i<imgList.size();i++){
				xVal = new Random().nextInt(getWidth()-70);
				yVal = new Random().nextInt(getHeight()-sling.getHeight()-70);
				
				xVals.add(i, (int)xVal);
				yVals.add(i, (int)yVal);
					
			}
			
			randomVals = true;
		}
		
		for (int i=0;i<imgList.size();i++) {
				
			canvas.drawBitmap(imgList.get(i), xVals.get(i), yVals.get(i), null);
			
			//CHECK FOR COLLISION
			if(isCollided(i)){
				
				if(imgList.indexOf(imgList.get(i)) == 0){
						
						activity.runOnUiThread(new Runnable() {
							public void run() {
								//Toast.makeText(context, "Score: "+, Toast.LENGTH_SHORT).show();
							}});
					
				
						
				imgList.remove(i);
				canvas.drawBitmap(Explode.exploding(2), xVals.get(i), yVals.get(i), null);
				xVals.remove(i);
				yVals.remove(i);
				Sounds.playSound(1);
				resetValues();
				}
			}
			
		}
		

		//DRAW OUT OF BOUNDS AREA
		canvas.drawRoundRect(new RectF(0, (float)(getHeight()-sling.getHeight()) - 40, (float)getWidth(), (float)getHeight()), 6, 6, outOfBounds);
		
		//DRAW SELECT TARGET AREA
		if (initX != 0 && initY != 0)
			canvas.drawBitmap(cross, initX - (cross.getWidth() / 2), initY - (cross.getHeight() / 2), null);
	
		//DRAW SLING LAUNCHER
		canvas.drawBitmap(sling,(getWidth() / 2) - (sling.getWidth() / 2),getHeight() - sling.getHeight(), null);
		
		//DRAW ROCK OR RETURNS IF CLICK OUT OF BOUNDS
			if (finalX != 0 && finalY != 0 && finalY < (canvas.getHeight()-sling.getHeight() - 50)){			
					matrix.postRotate(10);
					Bitmap resizedBitmap = Bitmap.createBitmap(rock, 0, 0, rock.getWidth(), rock.getHeight(), matrix, true);
					canvas.drawBitmap(resizedBitmap, startX + animX, startY + animY, filterPaint);
			}else{
					resetValues();
					surfHolder.unlockCanvasAndPost(canvas);
					return;
				}
				
		//UP Y ANIMATION
		if(animY*-1 < getHeight() && !bumpY){
			
			animY += speedY;
			
			//IF ROCK IS NOT AT EXTREME LEFT OR EXTREME RIGHT KEEP ON TRACK
			if(animX < (getWidth()/2) || animX*-1 < (getWidth()/2) && !bumpX){
				animX += speedX;
				
				//IF ROCK HITS EDGE LEFT/RIGHT RETURN TO SENDER (INDICATE BUMP)
				if(animX*-1 > (getWidth()/2) - rock.getWidth()/2 || animX > (getWidth()/2) - rock.getWidth()/2){
					bumpX = true;
					bumpY = true;
				}
			}
			
			//IF Y HITS TOP OF SCREEN
			if(animY*-1 > getHeight()-rock.getHeight()){
				bumpY = true;
			}
		}
		
		//DOWN Y ANIMATION
		if(animY < 0 && bumpY){	
			
			//REVERSE DIRECTION OF Y
			 animY  -= speedY;
			 
			 //IF ROCK HITS TOP OR SIDE REVERSE X DIRECTION
			 if(bumpX || bumpY)
					animX -= speedX;
			 
			 //IF AT STARTING POINT REVERSE
			 if(animY > 0){
					bumpY = false;
					bumpX = false;
				}
		}
		
		surfHolder.unlockCanvasAndPost(canvas);		
	}

	/*
	 * GRAB BITMAP FROM RESOURCES
	 */
	private Bitmap createBitmap(int resouce) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        bmp = Bitmap.createScaledBitmap(bmp, 70, 50, true);
        return bmp;
	}
	
	/*
	 * ADD BITMAPS TO LIST ARRAY
	 */
	private void initAnswerBitmaps(){
		imgList.add(0,createBitmap(R.drawable.cloud2));
		imgList.add(1,createBitmap(R.drawable.rock));
		imgList.add(2,createBitmap(R.drawable.crosshair));
		imgList.add(3,createBitmap(R.drawable.sling));
		
		
	}
	
	/*
	 * CHECKS FOR COLLISION ON CANVAS
	 */
	private boolean isCollided(int indexX) {
		
			return getHeight() - Math.round(animY*-1) <= yVals.get(indexX)+70 
					&& getHeight() - Math.round(animY*-1) >= yVals.get(indexX)
					&& Math.round(animX*-1) >= getWidth()/2 - (xVals.get(indexX)+70) 
					&& Math.round(animX*-1)<= (getWidth()/2 - (xVals.get(indexX)+70)+70);
	}

	/*
	 * LISTEN FOR TOUCHES ON THE SCREEN TO LAUNCH THE ROCK
	 */
	public boolean onTouch(MotionEvent event) {
		synchronized(panelThread){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				resetValues();
				initX = event.getX();
				initY = event.getY();
				bumpY = false;
				bumpX = false;
				break;
			case MotionEvent.ACTION_UP:
				finalX = event.getX() - (cross.getWidth() / 2);
				finalY = event.getY() - (cross.getHeight() / 2);
				moveToX = finalX - startX;
				moveToY = finalY - startY;
				speedX = moveToX / 30;
				speedY = moveToY / 30;
				break;
			case MotionEvent.ACTION_MOVE:
				initX = (int)event.getX();
				initY = (int)event.getY();
				break;
			}
			
			return true;
		}
	
	}

	/*
	 * RESETS TOUCH POSITIONS
	 */
	private void resetValues() {
		 finalX = finalY = moveToX = moveToY = speedX = speedY = animX = animY = 0;
	}

	public void onPause() {
		running = false;
		while (true) {
			try {
				panelThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public void onResume() {
		running 	= true;
		panelThread = new Thread(this);
		panelThread.start();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		//START THREAD RUNNABLE
				panelThread = new Thread(this);
				panelThread.start();	
				
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		running = false;
		panelThread = null;
		resetValues();
		initX = initY = 0;
	}



	public void onDestroy() {
		running = false;
		panelThread = null;
		resetValues();
		initX = initY = 0;
		
	}

}
