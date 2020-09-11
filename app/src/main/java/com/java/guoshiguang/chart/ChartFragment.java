package com.java.guoshiguang.chart;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.guoshiguang.R;
import com.java.guoshiguang.data.EpidemicData;
import com.java.guoshiguang.data.Manager;
import com.java.guoshiguang.data.Region;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import io.reactivex.functions.Consumer;

public class ChartFragment extends Fragment {

    String Province, Country;
    View view;
    HashMap<Region, EpidemicData> stored = null;
    String[][] indices = null;
    private LineChart mchart;
    private Spinner spinner1, spinner2;


    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chart, container, false);
        spinner1 = view.findViewById(R.id.spinner);
        spinner2 = view.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.country,
                android.R.layout.simple_spinner_item);//创建资源
        spinner1.setAdapter(adapter);//装配数据
        spinner1.setOnItemSelectedListener(new provinceClickListen());//监听事件
        spinner2.setOnItemSelectedListener(new CityClickListen());//监听事件

        mchart = (LineChart) view.findViewById(R.id.chart);
        setChartStyle(mchart);
        try {
            initChart("United States of America");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mchart.setPinchZoom(true);
        mchart.setDragEnabled(true);
        mchart.setScaleEnabled(true);
        return view;
    }

    private void setChartStyle(LineChart chart) {
        // 不显示数据描述
        chart.getDescription().setEnabled(true);
        // 没有数据的时候，显示“暂无数据”
        chart.setNoDataText("暂无数据或正在加载中···");
        // 不显示表格颜色
        chart.setDrawGridBackground(false);
        // 不可以缩放
        chart.setScaleEnabled(true);
        // 不显示y轴右边的值
        chart.getAxisRight().setEnabled(false);
        // 不显示图例
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        //legend.setExtra(new int []{Color.BLUE,Color.YELLOW,Color.GREEN,Color.RED}, new String[]{"CONFIRMED", "SUSPECTED", "CURED", "DEAD"});
        // 向左偏移15dp，抵消y轴向右偏移的30dp
        chart.setExtraLeftOffset(-15);

        XAxis xAxis = chart.getXAxis();
        // 不显示x轴
        xAxis.setDrawAxisLine(true);
        // 设置x轴数据的位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12);
        xAxis.setLabelCount(5);
        xAxis.setGridColor(Color.parseColor("#30FFFFFF"));
        xAxis.setAxisLineWidth(2);
        // 设置x轴数据偏移量
        xAxis.setYOffset(12);

        YAxis yAxis = chart.getAxisLeft();
        // 不显示y轴
        yAxis.setDrawAxisLine(true);
        // 设置y轴数据的位置
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 不从y轴发出横向直线
        yAxis.setDrawGridLines(false);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(10);
//        // 设置y轴数据偏移量
        yAxis.setXOffset(30);
//        yAxis.setYOffset(-3);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisLineWidth(2);

        Matrix matrix = new Matrix();
        // x轴缩放1.5倍
        //matrix.postScale(1.5f, 1f);
        //在图表动画显示之前进行缩放
        chart.getViewPortHandler().refresh(matrix, chart, false);
        // x轴执行动画
        chart.animateXY(2000, 2000);
        chart.setPinchZoom(true);
        chart.invalidate();
    }


    private void continue_proceed(final String region) throws ParseException {
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        ArrayList<String> x_lable = new ArrayList<>();
        for (int z = 0; z < 3; z++) {
            ArrayList<Entry> values = new ArrayList<>();
            Region nr = new Region(region.split("\\|"));
            System.out.println(nr);
            System.out.println(stored);
            EpidemicData ed = stored.get(nr);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = sdf.parse(ed.begin);
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            for (int i = 0; i < ed.data.size(); i++) {
                if (z == 0)
                    x_lable.add(sdf.format(c.getTime()));
                c.add(Calendar.DAY_OF_YEAR, 1);
                switch (z) {
                    case 0:
                        values.add(new Entry(i, ed.data.get(i).confirmed));
                        break;
                    case 1:
                        values.add(new Entry(i, ed.data.get(i).suspected));
                        break;
                    case 2:
                        values.add(new Entry(i, ed.data.get(i).cured));
                        break;
                    case 3:
                        values.add(new Entry(i, ed.data.get(i).dead));
                        break;
                    default:
                        break;
                }
            }
            LineDataSet d;
            if (z == 0) {
                d = new LineDataSet(values, "CONFIRMED");
                d.setCircleColor(Color.BLUE);
                d.setColor(Color.BLUE);
            } else if (z == 1) {
                d = new LineDataSet(values, "SUSPECTED");
                d.setCircleColor(Color.YELLOW);
                d.setColor(Color.YELLOW);
            } else if (z == 2) {
                d = new LineDataSet(values, "CURED");
                d.setCircleColor(Color.GREEN);
                d.setColor(Color.GREEN);
            } else {
                d = new LineDataSet(values, "DEAD");
                d.setCircleColor(Color.RED);
                d.setColor(Color.RED);
            }
            d.setLineWidth(2.5f);
            d.setCircleRadius(1f);
            dataSets.add(d);

        }

        ValueFormatter valueFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                System.out.println(value);
                if (value >= 0) {
                    return x_lable.get((int) value);
                } else {
                    return "";
                }
            }
        };

        mchart.getXAxis().setValueFormatter(valueFormatter);
        LineData data = new LineData(dataSets);
        mchart.setData(data);
        mchart.invalidate();
    }

    private void initChart(final String region) throws ParseException {

        if (stored == null) {
            Manager.I.fetchEpidemicData().subscribe(new Consumer<HashMap<Region, EpidemicData>>() {
                @Override
                public void accept(HashMap<Region, EpidemicData> regionEpidemicDataHashMap) throws Exception {
                    stored = regionEpidemicDataHashMap;
                    Manager.I.getIndices(stored).subscribe(new Consumer<String[][]>() {
                        @Override
                        public void accept(String[][] strings) throws Exception {
                            indices = strings;
                            continue_proceed(region);
                        }
                    });
                }
            });
        } else {
            continue_proceed(region);
        }

    }

    class provinceClickListen implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int i, long l) {
            Spinner spinner = (Spinner) parent;
            String pro = (String) spinner.getItemAtPosition(i);//获取当前选中项
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.country,
                    R.layout.item);//初始化
            Country = spinner1.getSelectedItem().toString();//当前选中的省份
            /** 根据省份，装配地市数据**/
            if (pro.equals("China")) {
                adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.China, R.layout.item);
            } else {
                adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.empty, R.layout.item);
            }
            spinner2.setAdapter(adapter);//装配地市数据
            if (!Objects.equals(Country, "China")) {
                try {
                    initChart(Country);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class CityClickListen implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int i, long l) {
            Province = spinner2.getSelectedItem().toString();//当前选中的地市
            if (!Objects.equals(Country, "China") ||
                    (Objects.equals(Country, "China") && (!Objects.equals(Province, "")))) {//当前还没选择地市
                Toast.makeText(view.getContext(), Country + " " + Province, Toast.LENGTH_LONG).show();//Toast显示选中的省份城市
            }
            try {
                initChart(Country + "|" + Province);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    }
}

