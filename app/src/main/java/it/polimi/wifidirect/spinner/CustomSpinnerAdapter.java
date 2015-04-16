/*
Copyright 2015 Stefano Cappa, Politecnico di Milano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package it.polimi.wifidirect.spinner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.polimi.wifidirect.R;

/**
 * The custom spinner adapter
 * <p></p>
 * Created by Stefano Cappa on 31/01/15.
 */
public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerRow> {

	private final Activity context;
	private final ArrayList<SpinnerRow> spinnerRow;

    /**
     * Constructor with required parameters
     * @param context The context
     * @param resource The resource
     * @param spinnerRow The list of elements
     */
	public CustomSpinnerAdapter(Activity context, int resource, ArrayList<SpinnerRow> spinnerRow) {
		super(context, resource, spinnerRow);
		this.context = context;
		this.spinnerRow = spinnerRow;

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
