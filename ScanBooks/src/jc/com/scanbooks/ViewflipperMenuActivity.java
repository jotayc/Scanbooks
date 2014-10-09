package jc.com.scanbooks;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import jc.com.scanbooks.R;

public class ViewflipperMenuActivity extends Activity {

	public float init_x;
    private ViewFlipper vf;
    ImageView img2;
    ImageView img1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    boolean flag=true;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_menu_viewfliper);
        
        if(getIntent().getBooleanExtra("EXIT", false)){
        	finish();
        }
        
        vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        img2 = (ImageView) findViewById(R.id.menuPag2);
        img1 = (ImageView) findViewById(R.id.menuPag1);
        vf.setOnTouchListener(new ListenerTouchViewFlipper());
        img1.setOnTouchListener(new ListenerTouchViewFlipper());;
        img2.setOnTouchListener(new ListenerTouchViewFlipper());
 
       
 
    }
  /*
   * Nos creamos una clase que nos ayude a diferenciar los gestos
   */
    private class ListenerTouchViewFlipper implements View.OnTouchListener{
 
        @Override
        public boolean onTouch(View v, MotionEvent event) {
 
            switch (event.getAction()) {
            
            case MotionEvent.ACTION_DOWN: 		//Cuando el usuario toca la pantalla por primera vez
                init_x=event.getX();
                return true;
            case MotionEvent.ACTION_UP: 		//Cuando el usuario levanta el dedo 
                float distance =init_x-event.getX();
 
                if(distance>0) 					//si la distancia desde donde se toca la pantalla hasta donde se levanta es positiva, significa deslizamiento de derecha a izquierda (ya que p.ej. 1 - -7 = 8) 
                {
                     vf.setInAnimation(inFromRightAnimation());
                     vf.setOutAnimation(outToLeftAnimation());
                     vf.showPrevious();
                }
                if(distance<0) 					//distancia negativa, desplazamiento de izquierda a derecha
                {
                     vf.setInAnimation(inFromLeftAnimation());
                     vf.setOutAnimation(outToRightAnimation());                    
                     vf.showNext();
                }
                if(distance==0){                // si es igual significa que solo ha sido un toque. Se ha pulsado y levantado en el mismo punto
                	int index = vf.getDisplayedChild();
                	if(index==0){  		  		
                		Intent intent = new Intent(ViewflipperMenuActivity.this,CollectionListActivity.class);
                		startActivity(intent);	
                	}
                	if(index==1){
                   		Intent intent = new Intent(ViewflipperMenuActivity.this,WebBook.class);
                		startActivity(intent);
                		
                	}
                }
                
            default:
                break;
            } 
            return false;
        }
    }
 
    private Animation inFromRightAnimation() {
 
        Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f );
 
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        
        return inFromRight;
 
    }
 
    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }
 
    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
 
    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
   
}

