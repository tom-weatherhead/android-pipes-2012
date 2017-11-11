package org.tomweatherhead.pipes;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

public class PipesActivity extends Activity {
    /** A handle to the View in which the game is running. */
    private PipesView mPipesView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mPipesView = (PipesView) findViewById(R.id.pipes);

        final ViewGroup mainLayout = (ViewGroup) findViewById(R.id.main_layout);
        
        mPipesView.setMainLayout(mainLayout);
        
        // give the PipesView a handle to the TextView used for messages
        final TextView statusText = (TextView) findViewById(R.id.text);
        
        mPipesView.setTextView(statusText);
        
        ViewTreeObserver vto1 = statusText.getViewTreeObserver();
        
        vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            	mPipesView.updateStatusTextHeight();
            }
        });
        
        // TODO: Restore the app state if savedInstanceState is not null.
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            mPipesView.doTouchDown(x, y);
    	}
    	
        // Let's try not stopping the propagation of this event.
        return false;
    }
}