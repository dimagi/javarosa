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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;

import org.javarosa.core.api.State;
import org.javarosa.j2me.log.HandledThread;

import de.enough.polish.ui.Alert;
import de.enough.polish.ui.AlertType;
import de.enough.polish.ui.UiAccess;

public class J2MEDisplay {
	private static Display display;
	private static LoadingScreenThread loading;
	
	public static void init (MIDlet m) {
		setDisplay(Display.getDisplay(m));
		loading = new LoadingScreenThread(display);
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
	
	public static void startStateWithLoadingScreen(State state) {
		startStateWithLoadingScreen(state,null);
	}
	
	public static void startStateWithLoadingScreen(State state, ProgressIndicator indicator) {
		final State s = state;
		loading.cancelLoading();
		loading = new LoadingScreenThread(display);
		loading.startLoading(indicator);
		new HandledThread(new Runnable() {
			public void run() {
				s.start();
			}
		}).start();
	}
	
	public static void setView (Displayable d) {
		loading.cancelLoading();
		Displayable old = display.getCurrent();
		display.setCurrent(d);
		if(old instanceof Screen) {
			//cts: Polish crashes on resource release unless you've
			//initialized a menubar. NOTE: This probably won't
			//catch full context switches with a loading screen
			//in between. See if that's necessary
			try{
				old.addCommand(new Command("init menu bar",2,2));
				UiAccess.releaseResources((Screen)old);
			} catch(Exception e) {
				//Don't know if there are any landmines in these hacks for the future
				e.printStackTrace();
			}
		}
	}
	
	public static void showError (String title, String message) {
		showError(title, message, null, null, null);
	}
	
	public static void showError (String title, String message, Image image) {
		showError(title, message, image, null, null);
	}
	
	public static Alert showError (String title, String message, Image image, Displayable next, CommandListener customListener) {
		//#style mailAlert
		final Alert alert = new Alert(title, message, image, AlertType.ERROR) {
			int latches = 0;
			{
				getKeyStates();
			}
			
			/* (non-Javadoc)
			 * @see de.enough.polish.ui.Screen#handleKeyPressed(int, int)
			 */
			protected boolean handleKeyPressed(int keyCode, int gameAction) {
				return super.handleKeyPressed(keyCode, gameAction);
			}
			
			/* (non-Javadoc)
			 * @see de.enough.polish.ui.Screen#handleKeyReleased(int, int)
			 */
			protected boolean handleKeyReleased(int keyCode, int gameAction) {
				if(this.getKeyStates() > 0) {
					return super.handleKeyReleased(keyCode, gameAction);
				}
				return true;
			}
		};
		alert.setTimeout(Alert.FOREVER);

		if (customListener != null) {
			if (next != null) {
				System.err.println("Warning: alert invoked with both custom listener and 'next' displayable. 'next' will be ignored; it must be switched to explicitly in the custom handler");
			}
			
			alert.setCommandListener(customListener);
		}
		
		if (next == null) {
			setView(alert);
		} else {
			loading.cancelLoading();
			//#if !polish.LibraryBuild
			//# display.setCurrent(alert, next);
			//#endif
		}
		
		return alert;
	}
	

	public static int getScreenHeight(int fallback) {
		int staticHeight = -1;
		int guess = de.enough.polish.ui.Display.getScreenHeight();
		
		//#ifdef polish.fullcanvasheight:defined
		//#= staticHeight = ${ polish.fullcanvasheight };
		//#elifdef polish.screenheight:defined
		//#= staticHeight = ${ polish.screenheight };
		//#endif
		
		if(guess == -1 && staticHeight == -1) {
			return fallback;
		} else if(guess != 1) {
			return guess;
		} else {
			return staticHeight;
		}
	}

	public static int getScreenWidth(int fallback) {
		int staticWidth = -1;
		int guess = de.enough.polish.ui.Display.getScreenWidth();
		
		//#ifdef polish.fullcanvaswidth:defined
		//#= staticWidth = ${ polish.fullcanvaswidth };
		//#elifdef polish.screenwidth:defined
		//#= staticWidth = ${ polish.screenwidth };
		//#endif
		
		if(guess == -1 && staticWidth == -1) {
			return fallback;
		} else if(guess != 1) {
			return guess;
		} else {
			return staticWidth;
		}
	}
}
