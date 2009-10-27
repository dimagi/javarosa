/*
 * Copyright (C) 2009 JavaRosa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.javarosa.j2me.view;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class J2MEDisplay {
	private static Display display;
	
	public static void init (MIDlet m) {
		setDisplay(Display.getDisplay(m));
	}
	
	public static void setDisplay (Display display) {
		J2MEDisplay.display = display;
	}

	public static Display getDisplay () {
		if (display == null) {
			throw new RuntimeException("Display has not been set from MIDlet; call J2MEDisplay.init()");
		}
		
		return display;
	}
	
	public static void setView (Displayable d) {
		display.setCurrent(d);
	}
	
	public static void showError (String title, String message) {
		showError(title, message, null, null);
	}
	
	public static Alert showError (String title, String message, Displayable next, CommandListener customListener) {
		//#style mailAlert
		final Alert alert = new Alert(title, message, null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);

		if (customListener != null) {
			if (next != null) {
				System.err.println("Warning: alert invoked with both custom listener and 'next' displayable. 'next' will be ignored; it must be switched to explicitly in the custom handler");
			}
			
			alert.setCommandListener(customListener);
		}
		
		if (next == null) {
			display.setCurrent(alert);
		} else {
			//display.setCurrent(alert, next);
			throw new RuntimeException("temporarily disabled due to polish build issue");
			//TODO: figure this out!!
		}
		
		return alert;
	}
}
