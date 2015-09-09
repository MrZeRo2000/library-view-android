package com.romanpulov.wheelcontrol;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import com.romanpulov.library.views.SlideNumberPicker;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    final Button button = (Button) findViewById(R.id.button1);
	    button.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            // Perform action on click

	        	final SlideNumberPicker picker = (SlideNumberPicker) findViewById(R.id.slideNumberPicker1);
	        	
	        	Toast.makeText(getBaseContext(), "Value=" + picker.getValue(), Toast.LENGTH_SHORT).show();

	        }
	    });
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
