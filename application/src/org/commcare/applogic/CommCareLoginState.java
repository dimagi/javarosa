package org.commcare.applogic;

import java.io.IOException;
import java.io.InputStream;

import org.commcare.util.CommCareContext;
import org.commcare.util.CommCareUtil;
import org.javarosa.core.services.Logger;
import org.javarosa.core.services.PropertyManager;
import org.javarosa.j2me.view.J2MEDisplay;
import org.javarosa.log.activity.DeviceReportState;
import org.javarosa.log.properties.LogPropertyRules;
import org.javarosa.services.transport.TransportMessage;
import org.javarosa.services.transport.impl.simplehttp.SimpleHttpTransportMessage;
import org.javarosa.user.api.LoginController;
import org.javarosa.user.api.LoginState;
import org.javarosa.user.model.User;

public class CommCareLoginState extends LoginState {
	private final static String MIDLET_REMINDERS_PROPERTY = "CommCare-ShowReminders";
	
	protected LoginController getController () {		
		String ver = "CommCare " + CommCareUtil.getVersion(CommCareUtil.VERSION_MED);
		String[] extraText = (CommCareUtil.isTestingMode() ? new String[] {ver, "*** TEST BUILD ***"}
											  : new String[] {ver});
		
		return new LoginController(extraText);
	}

	/* (non-Javadoc)
	 * @see org.javarosa.user.api.transitions.LoginStateTransitions#exit()
	 */
	public void exit() {
		CommCareUtil.exit();
	}

	/* (non-Javadoc)
	 * @see org.javarosa.user.api.transitions.LoginStateTransitions#loggedIn(org.javarosa.user.model.User)
	 */
	public void loggedIn(User u) {
		CommCareContext._().setUser(u);
		Logger.log("login", u.getUserID() + "-" + u.getUsername());
		
		CommCareContext._().toggleDemoMode(User.DEMO_USER.equals(u.getUserType()));

		J2MEDisplay.startStateWithLoadingScreen(new DeviceReportState() {

			public TransportMessage constructMessageFromPayload(InputStream reportPayload) {
				SimpleHttpTransportMessage message;
				try {
					message = new SimpleHttpTransportMessage(reportPayload,PropertyManager._().getSingularProperty(LogPropertyRules.LOG_SUBMIT_URL));
					message.setCacheable(false);
					return message;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("Failed to read report payload while creating http transport data");
				}
			}

			public void done() {
				// Go to the home state if we're done or if we skip it.
				new CommCareHomeState().start();
			}
		});
	}
}
