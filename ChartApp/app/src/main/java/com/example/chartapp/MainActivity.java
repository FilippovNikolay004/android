package com.example.chartapp;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Линейная диаграмма
        LineChart lineChart = findViewById(R.id.lineChart);
        setupLineChart(lineChart);

        // Столбчатая диаграмма
        BarChart barChart = findViewById(R.id.barChart);
        setupBarChart(barChart);
    }

    private void setupLineChart(LineChart chart) {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 11200f));
        entries.add(new Entry(1, 12450f));
        entries.add(new Entry(2, 9800f));
        entries.add(new Entry(3, 13750f));
        entries.add(new Entry(4, 15600f));
        entries.add(new Entry(5, 14900f));

        LineDataSet dataSet = new LineDataSet(entries, "Продажи (грн)");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setLineWidth(3f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        String[] months = {"Янв", "Фев", "Мар", "Апр", "Май", "Июн"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }

    private void setupBarChart(BarChart chart) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 6f));
        entries.add(new BarEntry(1, 8f));
        entries.add(new BarEntry(2, 5f));
        entries.add(new BarEntry(3, 7f));
        entries.add(new BarEntry(4, 10f));
        entries.add(new BarEntry(5, 12f));
        entries.add(new BarEntry(6, 9f));

        BarDataSet dataSet = new BarDataSet(entries, "Температура (°C)");
        dataSet.setColor(Color.rgb(255, 102, 0));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);

        chart.setData(barData);

        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getDescription().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }
}