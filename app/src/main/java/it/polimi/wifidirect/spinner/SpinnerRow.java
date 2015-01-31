package it.polimi.wifidirect.spinner;

import lombok.Getter;

public class SpinnerRow {
	
	@Getter private String name;
	
	 //se voglio mettere un'icona vicono alla scritta nella spinner item posso farlo con questo passandogli il R.drawable....
	@Getter private int resourceId;

	public SpinnerRow(String name){
		this.name = name;
	}

	@Override
	public String toString() {
        return getName();
	}
}