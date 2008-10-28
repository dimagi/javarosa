/**
 *
 */
package org.javarosa.user.activity;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import org.javarosa.core.Context;
import org.javarosa.core.JavaRosaServiceProvider;
import org.javarosa.core.api.Constants;
import org.javarosa.core.api.IActivity;
import org.javarosa.core.api.IShell;
import org.javarosa.core.api.IView;
import org.javarosa.user.view.LoginForm;

/**
 * @author Kieran Sharpey - Schafer
 *
 */
public class LoginActivity implements IActivity, CommandListener, ItemCommandListener {

	private LoginForm loginScreen = null;
	private IShell parent = null;
	public static final String COMMAND_KEY = "command";
	public static final String USER = "user";

	Context context;

	public LoginActivity(IShell p, String title) {
		this.parent = p;

	}

	public void start(Context context) {

		this.context = context;
		this.loginScreen = new LoginForm(this, "Login");
		this.loginScreen.setCommandListener(this);
		this.loginScreen.loginButton.setItemCommandListener(this);       // set item command listener
		parent.setDisplay(this, this.loginScreen);
	}

	public void commandAction(Command c, Item item) {
		if (c == this.loginScreen.loginButtonCommand) {
			System.out.println("login pressed");
			if (loginScreen.validateUser()) {
				final javax.microedition.lcdui.Alert success = loginScreen
						.successfulLogin();
				parent.setDisplay(this, new IView() {public Object getScreenObject() { return success;}});
				Hashtable returnArgs = new Hashtable();
				returnArgs.put(COMMAND_KEY, "USER_VALIDATED");
				returnArgs.put(USER, loginScreen.getLoggedInUser());
				parent.returnFromActivity(this, Constants.ACTIVITY_COMPLETE,
						returnArgs);
			} else {
				// /display an error that login failed and return to login
				// screen
				final javax.microedition.lcdui.Alert error = loginScreen.tryAgain();
				parent.setDisplay(this, new IView() {public Object getScreenObject() { return error;}});
				parent.setDisplay(this, this.loginScreen);

			}
		}
	}

	public void viewCompleted(Hashtable returnvals, int view_ID) {
	}

	public void contextChanged(Context context) {
		Vector contextChanges = this.context.mergeInContext(context);

		Enumeration en = contextChanges.elements();
		while (en.hasMoreElements()) {
			String changedValue = (String) en.nextElement();
			if (changedValue == Constants.USER_KEY) {
				// update username somewhere
			}
		}
	}

	public void halt() {

	}

	public void resume(Context context) {
		this.contextChanged(context);
		// Possibly want to check for new/updated forms
		JavaRosaServiceProvider.instance().showView(this.loginScreen);
	}

	public void destroy() {

	}

	public void commandAction(Command c, Displayable d) {
		if (c == loginScreen.CMD_CANCEL_LOGIN) {
			Hashtable returnArgs = new Hashtable();
			returnArgs.put(COMMAND_KEY, "USER_CANCELLED");
			parent.returnFromActivity(this, Constants.ACTIVITY_COMPLETE,returnArgs);
		} else if (c == loginScreen.loginButtonCommand) {
			System.out.println("login pressed");
			if (loginScreen.validateUser()) {
				final javax.microedition.lcdui.Alert success = loginScreen
						.successfulLogin();
				parent.setDisplay(this, new IView() {public Object getScreenObject() { return success;}});
				Hashtable returnArgs = new Hashtable();
				returnArgs.put(COMMAND_KEY, "USER_VALIDATED");
				returnArgs.put(USER, loginScreen.getLoggedInUser());
				parent.returnFromActivity(this, Constants.ACTIVITY_COMPLETE,
						returnArgs);
			} else {
				// /display an error that login failed and return to login
				// screen
				final javax.microedition.lcdui.Alert error = loginScreen.tryAgain();
				parent.setDisplay(this, new IView() {public Object getScreenObject() { return error;}});
				parent.setDisplay(this, this.loginScreen);

			}
		}
		// #endif
	}
	public Context getActivityContext() {
		return context;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.javarosa.core.api.IActivity#setShell(org.javarosa.core.api.IShell)
	 */
	public void setShell(IShell shell) {
		this.parent = shell;
	}
}
