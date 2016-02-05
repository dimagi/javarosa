package org.javarosa.core.model;

import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.services.storage.Persistable;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.ExtWrapListPoly;
import org.javarosa.core.util.externalizable.ExtWrapMap;
import org.javarosa.core.util.externalizable.PrototypeFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Registers actions that should be triggered by certain events, and handles the triggering
 * of those actions when an event occurs.
 *
 * Created by amstone326 on 2/2/16.
 */
public class ActionController implements Persistable {

    private int id;

    // map from an event to the actions it should trigger
    private Hashtable<String, Vector<Action>> eventListeners = new Hashtable<String, Vector<Action>>();

    public Vector<Action> getListenersForEvent(String event) {
        if (this.eventListeners.containsKey(event)) {
            return eventListeners.get(event);
        }
        return new Vector<Action>();
    }

    public void registerEventListener(String event, Action action) {
        Vector<Action> actions;
        if (eventListeners.containsKey(event)) {
            actions = eventListeners.get(event);
        } else {
            actions = new Vector<Action>();
            eventListeners.put(event, actions);
        }
        actions.addElement(action);
    }

    public void triggerActionsFromEvent(String event, FormDef model, TreeReference contextForAction) {
        triggerActionsFromEvent(event, model, contextForAction, null);
    }

    public void triggerActionsFromEvent(String event, FormDef model, TreeReference contextForAction,
                                        ActionResultProcessor resultProcessor) {
        for (Action action : getListenersForEvent(event)) {
            TreeReference refSetByAction = action.processAction(model, contextForAction);
            if (resultProcessor != null && refSetByAction != null) {
                resultProcessor.processResultOfAction(refSetByAction, event);
            }
        }
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void readExternal(DataInputStream inStream, PrototypeFactory pf) throws IOException, DeserializationException {
        eventListeners = (Hashtable<String, Vector<Action>>) ExtUtil.read(inStream,
                new ExtWrapMap(String.class, new ExtWrapListPoly()), pf);
    }

    @Override
    public void writeExternal(DataOutputStream outStream) throws IOException {
        ExtUtil.write(outStream, new ExtWrapMap(eventListeners, new ExtWrapListPoly()));
    }

    public interface ActionResultProcessor {
        /**
         * @param targetRef - the ref that this action targeted
         * @param event - the event that triggered this action
         */
        void processResultOfAction(TreeReference targetRef, String event);
    }

}
