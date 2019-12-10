package tink.co.exchange.rates.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import tink.co.exchange.rates.App;
import tink.co.exchange.rates.R;

import static tink.co.exchange.rates.Config.CURRENCY;
import static tink.co.exchange.rates.Config.HISTORY_LIST;

/**
 * Created by Tourdyiev Roman on 2019-12-10.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener {

    private ImageView close;
    private TextView title;
    private LineChartView lineChart;

    private List<Line> lines;
    private List<AxisValue> axisValues;
    private List<PointValue> point_values;
    private HashMap<String, String> hashMap;
    private Line line;
    private ValueShape circle_shape = ValueShape.CIRCLE;
    private LineChartData data;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            App.getActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        findViews(rootView);
        initViews();
        return rootView;
    }

    private void findViews(View rootView) {
        close = rootView.findViewById(R.id.close);
        title = rootView.findViewById(R.id.title);
        lineChart = rootView.findViewById(R.id.line_chart);
    }

    private void initViews() {
        close.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(HISTORY_LIST)) {
                Type listType = new TypeToken<List<Pair<String, Double>>>() {}.getType();
                List<Pair<String, Double>> history = new Gson().fromJson(bundle.getString(HISTORY_LIST), listType);
                title.setText(bundle.getString(CURRENCY));

                initChart(history);

            }
        }
    }

    private void initChart(List<Pair<String, Double>> history){
        lines = new ArrayList<>();
        axisValues = new ArrayList<>();
        point_values = new ArrayList<>();
        hashMap = new HashMap<>();

        for (int x = 0; x < history.size(); x++) {
            float y = (new BigDecimal(history.get(x).second)).floatValue();
                PointValue point = new PointValue(x, y);
                point.setLabel(String.valueOf(y));
                point_values.add(point);
                axisValues.add(new AxisValue(x).setLabel(history.get(x).first));

        }

        line = new Line(point_values);
        line.setColor(getResources().getColor(R.color.colorAccent))
                .setShape(circle_shape)
                .setCubic(false)
                .setFilled(true)
                .setHasLabels(true)
                .setHasLabelsOnlyForSelected(false)
                .setHasLines(true)
                .setHasPoints(true);

        lines.add(line);

        data = new LineChartData(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY)
                .setValueLabelsTextColor(getActivity().getResources().getColor(android.R.color.white));

            data.setAxisXBottom(new Axis(axisValues)
                    .setHasLines(false)
                    .setLineColor(getActivity().getResources().getColor(R.color.colorPrimaryDark))
                    .setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark))
                    .setInside(false));
            data.setAxisYLeft(new Axis()
                    .setHasLines(true)
                    .setLineColor(getActivity().getResources().getColor(R.color.colorPrimaryDark))
                    .setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark))
                    .setHasSeparationLine(false));

        lineChart.setLineChartData(data);

        resetLineViewport(lineChart, history.size());
    }
    private void resetLineViewport(LineChartView lineChart, int numberOfPoints) {

        final Viewport v = new Viewport(lineChart.getMaximumViewport());
        final Viewport vmax = new Viewport(lineChart.getMaximumViewport());

        float fivePercent = v.height() * 0.05f;
        v.bottom = 0;
        v.top = v.top + fivePercent;
        v.bottom = v.bottom - fivePercent;


        v.left = numberOfPoints - 8;
        v.right = numberOfPoints - 1;

        vmax.bottom = 0;
        vmax.top = v.top;
        vmax.left = 0;
        vmax.right = v.right;

        lineChart.setMaximumViewport(vmax);
        lineChart.setCurrentViewport(v);
    }
}
