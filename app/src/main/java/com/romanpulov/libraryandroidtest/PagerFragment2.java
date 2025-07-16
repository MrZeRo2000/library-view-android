package com.romanpulov.libraryandroidtest;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import com.romanpulov.library.view.BarChart;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagerFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment2 extends Fragment {
    public static PagerFragment2 newInstance() {
        return new PagerFragment2();
    }

    public PagerFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result =  inflater.inflate(R.layout.fragment_pager_fragment2, container, false);

        final EditText maxCountEditText = result.findViewById(R.id.maxCountEditText);
        final EditText maxValueEditText = result.findViewById(R.id.maxValueEditText);

        Button generateButton = result.findViewById(R.id.generateFragmentButton);
        generateButton.setOnClickListener(v -> {
            final BarChart barChart = result.findViewById(R.id.barFragmentChart2);
            barChart.clearSeries();

            int maxCount = Integer.parseInt(maxCountEditText.getText().toString());
            int maxValue = Integer.parseInt(maxValueEditText.getText().toString());

            BarChart.Series series = barChart.addSeries();
            series.setGradientColors(0xffbf8aff, 0xff008a33);

            Random rnd = new Random();

            for (int x = 1; x <= maxCount; x++) {
                int y = rnd.nextInt(maxValue);
                series.addXY(x, String.valueOf(x), y);
                Log.d("BarChart", "New random values (" + x + ", " + y + ")");
            }
            Log.d("BarChart", "AfterSeriesAdded");

            barChart.updateSeriesListValueBounds();
            barChart.updateChartLayout();
            barChart.requestLayout();
            Log.d("BarChart", "Series=" + barChart.getSeries(0));
            barChart.invalidate();
        });

        Button generate2Button = result.findViewById(R.id.generate2FragmentButton);
        generate2Button.setOnClickListener(v -> {
            final BarChart barChart = result.findViewById(R.id.barFragmentChart2);
            barChart.clearSeries();

            int maxCount = Integer.parseInt(maxCountEditText.getText().toString());
            int maxValue = Integer.parseInt(maxValueEditText.getText().toString());

            Random rnd = new Random();

            BarChart.Series series = barChart.addSeries();
            series.setGradientColors(0xffbf8aff, 0xff008a33);
            for (int x = 1; x <= maxCount; x++) {
                int y = rnd.nextInt(maxValue);
                series.addXY(x, String.valueOf(x), y);
            }

            rnd = new Random();

            series = barChart.addSeries();
            series.setGradientColors(0xff8abfff, 0xff8a0033);
            for (int x = 1; x <= maxCount * 2; x+=2) {
                int y = rnd.nextInt(maxValue * 2);
                series.addXY(x, String.valueOf(x), y);
            }

            barChart.updateSeriesListValueBounds();
            barChart.updateChartLayout();
            barChart.requestLayout();
            barChart.invalidate();
        });

        Button generate3Button = result.findViewById(R.id.generate3FragmentButton);
        generate3Button.setOnClickListener(v -> {
            final BarChart barChart = result.findViewById(R.id.barFragmentChart2);
            barChart.clearSeries();

            int maxCount = Integer.parseInt(maxCountEditText.getText().toString());
            int maxValue = Integer.parseInt(maxValueEditText.getText().toString());

            Random rnd = new Random();

            BarChart.Series series = barChart.addSeries();
            series.setGradientColors(0xffbf8aff, 0xff008a33);
            for (int x = 1; x <= maxCount; x++) {
                int y = rnd.nextInt(maxValue);
                series.addXY(x, String.valueOf(x), y);
            }

            rnd = new Random();

            series = barChart.addSeries();
            series.setGradientColors(0xff8abfff, 0xff8a0033);
            for (int x = 1; x <= maxCount * 2; x+=2) {
                int y = rnd.nextInt(maxValue * 2);
                series.addXY(x, String.valueOf(x), y);
            }

            rnd = new Random();

            series = barChart.addSeries();
            series.setGradientColors(0xff8affbf, 0xff8a3300);
            for (int x = 1; x <= maxCount; x++) {
                int y = rnd.nextInt(maxValue * 2);
                series.addXY(x, "Very long label" + x, y);
            }

            barChart.updateSeriesListValueBounds();
            barChart.updateChartLayout();
            barChart.requestLayout();
            barChart.invalidate();
        });


        Button increaseWidthButton = result.findViewById(R.id.increaseWidthButton);
        increaseWidthButton.setOnClickListener(v -> {
            final BarChart barChart = result.findViewById(R.id.barFragmentChart2);
            barChart.setBarItemWidth(barChart.getBarItemWidth() + barChart.getBarItemWidth() / 10);
            barChart.updateChartLayout();
            barChart.requestLayout();
            barChart.invalidate();
        });

        Button decreaseWidthButton = result.findViewById(R.id.decreaseWidthButton);
        decreaseWidthButton.setOnClickListener(v -> {
            final BarChart barChart = result.findViewById(R.id.barFragmentChart2);
            barChart.setBarItemWidth(barChart.getBarItemWidth() - barChart.getBarItemWidth() / 10);
            barChart.updateChartLayout();
            barChart.requestLayout();
            barChart.invalidate();
        });

        return result;

    }
}
