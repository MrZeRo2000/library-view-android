package com.romanpulov.libraryandroidtest;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.view.View;

import com.romanpulov.library.view.*;

import java.util.Random;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        final Button startPagerButton = (Button) findViewById(R.id.startPagerButton);
        startPagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PagerActivity.class);
                startActivity(intent);
            }
        });
		
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

        final ProgressCircle ps = (ProgressCircle) findViewById(R.id.progressCircle1);

		final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ps.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button alwaysVisibleButton = (Button) findViewById(R.id.alwaysVisibleButton);
        alwaysVisibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ps.setAlwaysVisible(true);
                ps.requestLayout();
            }
        });

        final Button chartValuesButton = (Button) findViewById(R.id.setChartValuesButton);
        chartValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) findViewById(R.id.barChart1);
                BarChart.Series series = barChart.addSeries();
                series.setGradientColors(0xffbfff00, 0xff003300);
                series.addXY(1d, "Item 1 has very bit length and needs to be truncated", 2d);
                series.addXY(2d, "Item 2", 1d);
                series.addXY(3d, "Item 3", 4d);
                Log.d("BarChart", "AfterSeriesAdded");

                barChart.updateSeriesListValueBounds();
                barChart.updateChartLayout();
                barChart.requestLayout();
            }
        });

        final Button resetChartValuesButton = (Button) findViewById(R.id.resetChartValuesButton);
        resetChartValuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) findViewById(R.id.barChart1);
                barChart.clearSeries();

                barChart.updateSeriesListValueBounds();
                barChart.updateChartLayout();
                barChart.requestLayout();
            }
        });

        final Button generateButton = (Button) findViewById(R.id.generateButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) findViewById(R.id.barChart2);
                barChart.clearSeries();

                final EditText maxCountText = (EditText) findViewById(R.id.maxCount);
                final EditText maxValueText = (EditText) findViewById(R.id.maxValue);

                int maxCount = Integer.valueOf(maxCountText.getText().toString());
                int maxValue = Integer.valueOf(maxValueText.getText().toString());

                BarChart.Series series = barChart.addSeries();
                series.setGradientColors(0xffbf8aff, 0xff008a33);

                Random rnd = new Random();

                for (int x = 1; x <= maxCount; x++) {
                    int y = rnd.nextInt(maxValue);
                    series.addXY(x, String.valueOf(x), y);
                    Log.d("BarChart", "New random values (" + x + ", " + y + ")");
                }
                /*
                series.addXY(1d, "Item 1 has very bit length and needs to be truncated", 2d);
                series.addXY(2d, "Item 2", 1d);
                series.addXY(3d, "Item 3", 4d);
                */
                Log.d("BarChart", "AfterSeriesAdded");

                barChart.updateSeriesListValueBounds();
                barChart.updateChartLayout();
                barChart.requestLayout();
                Log.d("BarChart", "Series=" + barChart.getSeries(0));
                barChart.invalidate();
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
