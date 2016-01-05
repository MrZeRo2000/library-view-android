package com.romanpulov.libraryandroidtest;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.romanpulov.library.view.BarChart;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagerFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PagerFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static PagerFragment2 newInstance(String param1, String param2) {
        PagerFragment2 fragment = new PagerFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PagerFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result =  inflater.inflate(R.layout.fragment_pager_fragment2, container, false);

        Button generateButton = (Button) result.findViewById(R.id.generateFragmentButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) result.findViewById(R.id.barFragmentChart2);
                barChart.clearSeries();

                int maxCount = 10;
                int maxValue = 17;

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
            }
        });

        Button generate2Button = (Button) result.findViewById(R.id.generate2FragmentButton);
        generate2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) result.findViewById(R.id.barFragmentChart2);
                barChart.clearSeries();

                int maxCount = 10;
                int maxValue = 17;

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
            }
        });

        Button generate3Button = (Button) result.findViewById(R.id.generate3FragmentButton);
        generate3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) result.findViewById(R.id.barFragmentChart2);
                barChart.clearSeries();

                int maxCount = 10;
                int maxValue = 17;

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
                    series.addXY(x, "Very long label" + String.valueOf(x), y);
                }

                barChart.updateSeriesListValueBounds();
                barChart.updateChartLayout();
                barChart.requestLayout();
                barChart.invalidate();
            }
        });


        Button increaseWidthButton = (Button) result.findViewById(R.id.increaseWidthButton);
        increaseWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) result.findViewById(R.id.barFragmentChart2);
                barChart.setBarItemWidth(barChart.getBarItemWidth() + barChart.getBarItemWidth() / 10);
                barChart.updateChartLayout();
                barChart.requestLayout();
                barChart.invalidate();
            }
        });

        Button decreaseWidthButton = (Button) result.findViewById(R.id.decreaseWidthButton);
        decreaseWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BarChart barChart = (BarChart) result.findViewById(R.id.barFragmentChart2);
                barChart.setBarItemWidth(barChart.getBarItemWidth() - barChart.getBarItemWidth() / 10);
                barChart.updateChartLayout();
                barChart.requestLayout();
                barChart.invalidate();
            }
        });

        return result;

    }


}
