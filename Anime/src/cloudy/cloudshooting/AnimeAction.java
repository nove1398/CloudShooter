package cloudy.cloudshooting;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.app.Activity;

public class AnimeAction extends Activity {

	Panel pane;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pane = new Panel(this.getApplicationContext());
		setContentView(pane);
 
	}
	
	@Override
	protected void onPause() {
		pane.onPause();
		super.onPause();
	}

	
	@Override
	protected void onResume() {
		pane.onResume();
		super.onResume();
	}

	
	@Override
	protected void onDestroy() {
		pane.onDestroy();
		super.onDestroy();
	}

	

	
}
