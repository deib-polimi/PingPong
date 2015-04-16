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