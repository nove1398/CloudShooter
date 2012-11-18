package cloudy.cloudshooting;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class SoundKeeper {
	private  SoundPool SoundPool;
	private  SparseIntArray SoundsArray;
	private  AudioManager  SoundsManager;
	private  Context context;
	
	public void initSounds(Context mContext) {
	    context       = mContext;
	    SoundPool     = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	    SoundsArray   = new SparseIntArray();
	    SoundsManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}
	
	/*
	 * ADD A SOUND TO THE POOL
	 */
	public void addSound(int index, int SoundID)
	{
	    SoundsArray.put(index, SoundPool.load(context, SoundID, 1));
	}
	
	/*
	 * PLAYING A SOUND
	 */
	public void playSound(int index)
	{
	float streamVolume = SoundsManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	streamVolume = streamVolume / SoundsManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    SoundPool.play(SoundsArray.get(index), streamVolume, streamVolume, 1, 0, 1f);
	}
}
