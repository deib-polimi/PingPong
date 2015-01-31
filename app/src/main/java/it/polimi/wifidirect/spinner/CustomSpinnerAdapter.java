package it.polimi.wifidirect.spinner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.wifidirect.R;

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerRow> {

	private Activity context;
	ArrayList<SpinnerRow> spinnerRow;

	public CustomSpinnerAdapter(Activity context, int resource, ArrayList<SpinnerRow> spinnerRow) {
		super(context, resource, spinnerRow);
		this.context = context;
		this.spinnerRow = spinnerRow;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        return v;
}


	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {

		View row = convertView;

		if (row == null) {

			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.spinner_row, parent, false);

		}

		SpinnerRow current = spinnerRow.get(position);

		TextView name = (TextView) row.findViewById(R.id.spinner_row_label);
		name.setText(current.getName());


		return row;
	}

}
