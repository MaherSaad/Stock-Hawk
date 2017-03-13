package com.udacity.stockhawk.ui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public FragmentDetail() {
        // Required empty public constructor
    }

    Cursor mCursor;

    TextView symbol;
    TextView price;
    TextView change;
    Spinner timeSpinner;

    LineChart lineChart;

    private final int LOADER_ID=100;
    private Uri uri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);
        symbol = (TextView)v.findViewById(R.id.symbol);
        price = (TextView)v.findViewById(R.id.price);
        change = (TextView)v.findViewById(R.id.change);

        timeSpinner = (Spinner)v.findViewById(R.id.timeSpinner);

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);

        ((DetailActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar=((DetailActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);

        lineChart = (LineChart) v.findViewById(R.id.chart);

        Intent intent = getActivity().getIntent();

        uri = Contract.Quote.URI.buildUpon().appendPath(intent.getStringExtra("symbol")).build();

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID,null,this);



        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drawChart(mCursor,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void drawChart(Cursor c,int pos){
        String [] mydata = c.getString(Contract.Quote.POSITION_HISTORY).split("\n");

        List<Entry> entries = new ArrayList<Entry>();
        int BreakPoint = 0;
        switch (pos){
            case 0:
                BreakPoint = 7;
                break;
            case 1:
                BreakPoint = 14;
                break;
            case 2:
                BreakPoint = mydata.length;
                break;
        }
        int count =0;
        final float base = Float.parseFloat(mydata[0].split(",")[0]);
        for (String rowData : mydata) {
            if (count==BreakPoint)break;
            // turn your data into Entry objects
            entries.add(new Entry(base - Float.parseFloat(rowData.split(",")[0]),Float.parseFloat(rowData.split(",")[1])));
            count++;

        }


        LineDataSet dataset = new LineDataSet(entries, "");

        dataset.setCircleColor(Color.WHITE);
        dataset.setColor(Color.parseColor("#0288D1"));
        dataset.setLineWidth(2);
        dataset.setCircleRadius(4);
        dataset.setValueTextColor(Color.parseColor("#0288D1"));
        dataset.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataset.setDrawHighlightIndicators(false);

        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getXAxis().setGridColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setGridColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setGridColor(Color.WHITE);
        LineData data = new LineData(dataset);

        data.setDrawValues(false);

        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                String formattedDate=new SimpleDateFormat("dd/MM/YY").format(value+base);

                return formattedDate;
            }
        });

        lineChart.setData(data);
        lineChart.invalidate();// set the data and list of lables into chart
        //lineChart.setDescription("Description");

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                uri,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        symbol.setText(data.getString(Contract.Quote.POSITION_SYMBOL));
        price.setText(data.getFloat(Contract.Quote.POSITION_PRICE)+" $");
        change.setText(data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE)+" ("+data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE)+"%) ");


        mCursor = data;
        drawChart(data,0);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
