package com.romanpulov.libraryandroidtest;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.view.View;

import com.romanpulov.library.view.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    final Button getValueButton = (Button) findViewById(R.id.getValueButton);
	    getValueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                final SlideNumberPicker picker = (SlideNumberPicker) findViewById(R.id.slideNumberPicker1);
                Toast.makeText(getBaseContext(), "Value=" + picker.getValue(), Toast.LENGTH_SHORT).show();

            }
        });

        final Button setValueButton = (Button) findViewById(R.id.setValueButton);
        setValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SlideNumberPicker picker = (SlideNumberPicker) findViewById(R.id.slideNumberPicker1);
                picker.setValue(25);
                Toast.makeText(getBaseContext(), "Set Value =" + picker.getValue(), Toast.LENGTH_SHORT).show();
            }
        });

		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final ProgressCircle ps = (ProgressCircle) findViewById(R.id.progressCircle1);
                ps.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final BarChart barChart = (BarChart) findViewById(R.id.barChart1);
        BarChart.Series series =  barChart.addSeries();
        series.addXY(1d, "Item 1", 2d);
        series.addXY(2d, "Item 2", 1d);
        series.addXY(3d, "Item 3", 5d);
        barChart.updateSeriesListValueBounds();
        Log.d("BarChart", "AfterSeriesAdded");
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
