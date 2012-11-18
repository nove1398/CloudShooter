package cloudy.cloudshooting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Explosions {
	private List<Bitmap> expList = new ArrayList<Bitmap>(2);
	private Bitmap exp1;
	Context context;
	
	
	public Explosions(Context context){
		this.context = context;
		
	}
	
	public void initExplosions(int index, int bmp){
		exp1 = BitmapFactory.decodeResource(context.getResources(), bmp);
		expList.add(index, exp1);
	}
	
	public Bitmap exploding (int index){
		Bitmap bmp = expList.get(index);
		 
        return Bitmap.createScaledBitmap(bmp, 75, 75, true);	
	}
	
	public List<Bitmap> getListSize(){
		return expList;
	}
	
}
