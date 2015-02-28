package it.polimi.wifidirect.spinner;

import lombok.Getter;

/**
 * This class represents a row of the spinners.
 * <p></p>
 * Created by Stefano Cappa on 31/01/15.
 *
 */
public class SpinnerRow {
	
	@Getter private final String name;
	
    /**
     * Constructor of this class.
     * @param name String that represents the macaddress of the device in the Spinner's row.
     */
	public SpinnerRow(String name){
		this.name = name;
	}

	@Override
	public String toString() {
        return name;
	}
}