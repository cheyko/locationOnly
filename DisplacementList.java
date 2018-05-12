package com.cheyko.locationonly;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ariel on 4/5/18.
 */

public class DisplacementList extends ArrayAdapter<Displacements> {

    private Activity context;
    private List<Displacements> displacementsList;

    public DisplacementList(Activity context, List<Displacements> displacementsList){

        super(context,R.layout.list_layout,displacementsList);
        this.context = context;
        this.displacementsList = displacementsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView textViewBusNum = listViewItem.findViewById(R.id.textViewBusNum);
        TextView textViewLatti = listViewItem.findViewById(R.id.textViewLatti);
        TextView textViewLongi = listViewItem.findViewById(R.id.textViewLongi);
        TextView textViewThisThus = listViewItem.findViewById(R.id.textViewTimeThus);

        Displacements displacement = displacementsList.get(position);

        textViewBusNum.setText("BUS : "+displacement.getBusNumber()+" ");
        textViewLatti.setText("LATITUDE = "+displacement.getPoints().getLatti()+" ");
        textViewLongi.setText("LONGITUDE = "+displacement.getPoints().getLongi()+" ");
        textViewThisThus.setText("TIME TRAVELLED --> "+displacement.getTimeThus()+" ");

        return listViewItem;

    }
}
