package com.charaza.resources;

import com.charaza.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;

import java.lang.System;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class CharazaCanvas extends View implements View.OnTouchListener
{
	private Canvas canvas;
	private Bitmap pigFace;
	private Bitmap sadPigFace;
	private Bitmap reallySadPigFace;
	private long delay;
	private long lastRefresh;
	private Context context;
	private int pigVerticalCentreOffset;//the vertical distance between the centre of the pig and that of the canvas upwords
	private Bitmap rightHand;
	private Bitmap leftHand;
	private Bitmap leftSlap;
	private Bitmap rightSlap;
	private int rightSlapWidth;
	private int leftHandTouchedFlag;
	private int rightHandTouchedFlag;
	private int rightHandXValue;
	private int rightHandYValue;
	private int leftHandXValue;
	private int leftHandYValue;
	private int leftSlapXValue;
	private int leftSlapYValue;
	private int rightSlapYValue;
	private int rightSlapXValue;
	private int canvasHeight;
	private int canvasWidth;
	private int slapVerticalCentreOffset;
	private MediaPlayer slapSound;
	private SoundManager soundManager;
	private final int SLAP_SOUND=0;
	private final int LAUGH=1;
	private final int LAUGH_2=2;
	private final int CRY=3;
	private final int CRY_2=4;
	private final int COMMENT_1=5;
	private final int COMMENT_2=6;
	private final int COMMENT_3=7;
	private int numberOfSlaps;
	private DisplayMetrics metrics;
	public CharazaCanvas(Context context,DisplayMetrics metrics)
	{
		super(context);
		
		//initialise views
		this.setOnTouchListener(this);
		
		//initialise Bitmaps
		Thread thread=new Thread(new BitmapLoader());
		thread.run();
		
		//initialise resources
		delay=1;
		lastRefresh=0;
		this.context=context;
		pigVerticalCentreOffset=80;
		leftHandTouchedFlag=0;
		rightHandTouchedFlag=0;
		slapVerticalCentreOffset=0;
		//resetLeftSlap();
		//resetRightSlap();
		soundManager=new SoundManager();
		soundManager.initSounds(context);
		soundManager.addSound(SLAP_SOUND, R.raw.slap);
		soundManager.addSound(LAUGH, R.raw.laugh);
		soundManager.addSound(LAUGH_2, R.raw.laugh2);
		soundManager.addSound(CRY, R.raw.cry);
		soundManager.addSound(CRY_2, R.raw.cry2);
		soundManager.addSound(COMMENT_1, R.raw.comment_1);
		soundManager.addSound(COMMENT_2, R.raw.comment_2);
		soundManager.addSound(COMMENT_3, R.raw.comment_3);
		numberOfSlaps=0;
		//slapSound=MediaPlayer.create(context, R.raw.slap);
		this.metrics=metrics;
		
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		long time=System.currentTimeMillis()-lastRefresh;
		if(time>=delay)
		{
			lastRefresh=System.currentTimeMillis();
			
			super.onDraw(canvas);
			
			Rect background=new Rect();
			background.set(0, 0, canvas.getWidth(), canvas.getHeight());
			
			Paint backgroundColor=new Paint();
			backgroundColor.setStyle(Paint.Style.FILL);
			backgroundColor.setColor(Color.WHITE);
			
			canvas.drawRect(background, backgroundColor);
			
			drawPigFace(canvas);
		}
		
		invalidate();
	}
	
	private void drawPigFace(Canvas canvas)
	{
		int pigFaceWidth=pigFace.getWidth();
		if(numberOfSlaps<=21)
		{
			canvas.drawBitmap(pigFace, canvas.getWidth()/2-pigFace.getWidth()/2, (canvas.getHeight()/2-pigFace.getHeight()/2)-pigVerticalCentreOffset, new Paint());
		}
		else if(numberOfSlaps>21 && numberOfSlaps<27)
		{
			canvas.drawBitmap(sadPigFace, canvas.getWidth()/2-sadPigFace.getWidth()/2, (canvas.getHeight()/2-sadPigFace.getHeight()/2)-pigVerticalCentreOffset, new Paint());
		}
		else if(numberOfSlaps>=27)
		{
			canvas.drawBitmap(reallySadPigFace, canvas.getWidth()/2-reallySadPigFace.getWidth()/2, (canvas.getHeight()/2-reallySadPigFace.getHeight()/2)-pigVerticalCentreOffset, new Paint());
		}
		
		rightHandXValue=canvas.getWidth()-rightHand.getWidth();
		rightHandYValue=canvas.getHeight()-rightHand.getHeight();
		canvasWidth=canvas.getWidth();
		canvasHeight=canvas.getHeight();
		if(rightHandTouchedFlag==0)
		{
			canvas.drawBitmap(rightHand, rightHandXValue, rightHandYValue, new Paint());
		}
		else
		{
			drawRightSlap(canvas, pigFaceWidth);
		}
		leftHandXValue=0;
		leftHandYValue=canvas.getHeight()-leftHand.getHeight();
		if(leftHandTouchedFlag==0)
		{
			canvas.drawBitmap(leftHand, leftHandXValue, leftHandYValue, new Paint());
		}
		else
		{
			drawLeftSlap(canvas,pigFaceWidth);
		}
	}
	
	private void resetLeftSlap()
	{
		leftSlapXValue=0;
		leftSlapYValue=canvasHeight/2+slapVerticalCentreOffset;
		Log.d("Slap coordinates", String.valueOf(leftSlapXValue));
		Log.d("Slap coordinates",String.valueOf(leftSlapYValue));
	}
	
	private void resetRightSlap()
	{
		rightSlapYValue=canvasHeight/2+slapVerticalCentreOffset;
		rightSlapXValue=canvasWidth-rightSlapWidth;
	}
	
	private void playSound()
	{
		if(numberOfSlaps==2 || numberOfSlaps==11)
		{
			soundManager.playSound(LAUGH);
		}
		else if(numberOfSlaps==6)
		{
			soundManager.playSound(COMMENT_1);
		}
		else if(numberOfSlaps==20)
		{
			soundManager.playSound(LAUGH_2);
		}
		else if(numberOfSlaps==22)
		{
			soundManager.playSound(COMMENT_2);
		}
		else if(numberOfSlaps==27)
		{
			soundManager.playSound(CRY);
		}
		else if(numberOfSlaps==38||(numberOfSlaps>50&&numberOfSlaps%9==0))
		{
			soundManager.playSound(CRY_2);
		}
		else if(numberOfSlaps==40)
		{
			soundManager.playSound(COMMENT_3);
		}
	}
	
	private void drawRightSlap(Canvas canvas,int pigFaceWidth)
	{
		canvas.drawBitmap(rightSlap, rightSlapXValue, rightSlapYValue, new Paint());
		rightSlapXValue=rightSlapXValue-10;
		rightSlapYValue=rightSlapYValue-10;
		if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM || metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
		{
			if(rightSlapXValue<=(canvasWidth/2-15))
			{
				rightHandTouchedFlag=0;
				soundManager.playSound(SLAP_SOUND);
				numberOfSlaps++;
				playSound();
			}
		}
		else
		{
			if(rightSlapXValue<=(canvasWidth/2)+7)
			{
				rightHandTouchedFlag=0;
				soundManager.playSound(SLAP_SOUND);
				numberOfSlaps++;
				playSound();
			}
		}
	}
	
	private void drawLeftSlap(Canvas canvas,int pigFaceWidth)
	{
		canvas.drawBitmap(leftSlap, leftSlapXValue, leftSlapYValue, new Paint());
		leftSlapXValue=leftSlapXValue+10;
		leftSlapYValue=leftSlapYValue-10;
		Log.d("slaps",String.valueOf(numberOfSlaps));
		if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM || metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
		{
			if(leftSlapXValue>=(((canvasWidth/2)-(pigFaceWidth/2+12))+18))
			{
				leftHandTouchedFlag=0;
				//slapSound.start();
				soundManager.playSound(SLAP_SOUND);
				numberOfSlaps++;
				playSound();
			}
		}
		else
		{
			if(leftSlapXValue>=((canvasWidth/2)-(pigFaceWidth/2+12)))
			{
				leftHandTouchedFlag=0;
				//slapSound.start();
				soundManager.playSound(SLAP_SOUND);
				numberOfSlaps++;
				playSound();
			}
		}
	}
	
	private class BitmapLoader implements Runnable
	{

		@Override
		public void run() 
		{
			pigFace=BitmapFactory.decodeResource(getResources(), R.drawable.pig_face);
			rightHand=BitmapFactory.decodeResource(getResources(), R.drawable.right_hand);
			leftHand=BitmapFactory.decodeResource(getResources(), R.drawable.left_hand);
			leftSlap=BitmapFactory.decodeResource(getResources(), R.drawable.left_slap);
			rightSlap=BitmapFactory.decodeResource(getResources(), R.drawable.right_slap);
			rightSlapWidth=rightSlap.getWidth();
			sadPigFace=BitmapFactory.decodeResource(getResources(), R.drawable.sad_pig_face);
			reallySadPigFace=BitmapFactory.decodeResource(getResources(), R.drawable.really_sad_pig_face);
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(v==this && event.getY()>=rightHandYValue && event.getY()<=rightHandYValue+rightHand.getHeight() && event.getX()>=rightHandXValue && event.getX()<=rightHandXValue+rightHand.getWidth())
		{
			Log.d("touch events", "right hand touched");
			if(rightHandTouchedFlag==0)
			{
				resetRightSlap();
				rightHandTouchedFlag=1;
			}
		}
		else if(v==this && event.getY()>=leftHandYValue && event.getY()<=leftHandYValue+leftHand.getHeight() && event.getX()>=leftHandXValue && event.getX()<=leftHandXValue+leftHand.getWidth())
		{
			Log.d("touch events", "left hand touched");
			if(leftHandTouchedFlag==0)
			{
				resetLeftSlap();
				leftHandTouchedFlag=1;
			}
		}
		return true;
	}

}
