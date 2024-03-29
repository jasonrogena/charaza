package com.charaza.resources;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

public class SoundManager 
{

	private  SoundPool mSoundPool; 
	private  HashMap<Integer, Integer> mSoundPoolMap; 
	private  AudioManager  mAudioManager;
	private  Context mContext;
	private  Vector<Integer> mAvailibleSounds = new Vector<Integer>();
	private  Vector<Integer> mKillSoundQueue = new Vector<Integer>();
	private  Handler mHandler = new Handler();

	public SoundManager(){}

	public void initSounds(Context theContext) 
	{ 
	   mContext = theContext;
	   mSoundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0); 
	   mSoundPoolMap = new HashMap<Integer, Integer>(); 
	   mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);       
	} 

	public void addSound(int Index, int SoundID)
	{
		mAvailibleSounds.add(Index);
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));

	}

	 public void playSound(int index)
	 { 
		 // don't have a sound for this object, return.
		 if(mAvailibleSounds.contains(index))
		 {

			 int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
			 int soundId = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);

			 mKillSoundQueue.add(soundId);

			 // schedule the current sound to stop after set milliseconds
			 mHandler.postDelayed(new Runnable() 
			 {

				 public void run()
				 {
					 if(!mKillSoundQueue.isEmpty())
					 {
						 mSoundPool.stop(mKillSoundQueue.firstElement());
					 }
				 }
			 }, 3000);
		 }
	 }
}
