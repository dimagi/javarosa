package org.javarosa.form.api;

import org.javarosa.core.model.FormIndex;

import java.io.Serializable;
import java.util.Vector;

/**
 * Records form entry actions, associating form indexes with user (string)
 * answers.  For simplicity's sake each form index appears only once in the
 * action list. If a question answer is updated, the original form index entry
 * is removed, and the updated entry is added to the end.
 *
 * @author Phillip Mates (pmates@dimagi.com).
 */
public class FormEntrySession implements FormEntrySessionRecorder, Serializable {
    private final Vector<FormEntryAction> actions = new Vector<FormEntryAction>();

    @Override
    public void addNewRepeat(FormIndex formIndex) {
        final String formIndexString = formIndex.toString();
        int insertIndex = removeDuplicateAction(formIndexString);
        actions.insertElementAt(FormEntryAction.buildNewRepeatAction(formIndexString), insertIndex);
    }

    private int removeDuplicateAction(String formIndexString) {
        for (int i = actions.size() - 1; i >= 0; i--) {
            if (actions.get(i).formIndexString.equals(formIndexString)) {
                actions.remove(i);
                return i;
            }
        }
        return actions.size();
    }

    @Override
    public void addValueSet(FormIndex formIndex, String value) {
        final String formIndexString = formIndex.toString();
        int insertIndex = removeDuplicateAction(formIndexString);
        actions.insertElementAt(FormEntryAction.buildValueSetAction(formIndexString, value), insertIndex);
    }

    @Override
    public void addQuestionSkip(FormIndex formIndex) {
        final String formIndexString = formIndex.toString();
        int insertIndex = removeDuplicateAction(formIndexString);
        actions.insertElementAt(FormEntryAction.buildSkipAction(formIndexString), insertIndex);
    }

    public FormEntryAction popAction() {
        if (actions.size() > 0) {
            return actions.remove(0);
        } else {
            return FormEntryAction.buildNullAction();
        }
    }

    public FormEntryAction peekAction() {
        if (actions.size() > 0) {
            return actions.get(0);
        } else {
            return FormEntryAction.buildNullAction();
        }
    }

    public int size() {
        return actions.size();
    }

    @Override
    public String toString() {
        StringBuilder sessionStringBuilder = new StringBuilder();

        for (FormEntryAction formEntryAction : actions) {
            sessionStringBuilder.append(formEntryAction).append(" ");
        }

        return sessionStringBuilder.toString().trim();
    }

    public static FormEntrySession fromString(String sessionString) {
        FormEntrySession formEntrySession = new FormEntrySession();
        for (String actionString : FormEntrySession.splitTopParens(sessionString)) {
            formEntrySession.actions.addElement(FormEntryAction.fromString(actionString));
        }

        return formEntrySession;
    }

    public static class FormEntryAction implements Serializable {
        public final String formIndexString;
        public final String value;
        public final String action;
        private final static String NEW_REPEAT = "NEW_REPEAT";
        private final static String SKIP = "SKIP";
        private final static String VALUE = "VALUE";

        private FormEntryAction(String formIndexString, String value, String action) {
            this.formIndexString = formIndexString;
            this.value = value;
            this.action = action;
        }

        public static FormEntryAction buildNewRepeatAction(String formIndexString) {
            return new FormEntryAction(formIndexString, "", NEW_REPEAT);
        }

        public static FormEntryAction buildValueSetAction(String formIndexString, String value) {
            return new FormEntryAction(formIndexString, value, VALUE);
        }

        public static FormEntryAction buildSkipAction(String formIndexString) {
            return new FormEntryAction(formIndexString, "", SKIP);
        }

        public static FormEntryAction buildNullAction() {
            return new FormEntryAction("", "", "");
        }

        @Override
        public String toString() {
            if (NEW_REPEAT.equals(action)) {
                return "((" + formIndexString + ") (NEW_REPEAT))";
            } else if (VALUE.equals(action)) {
                // TODO PLM: escape 'value' field
                return "((" + formIndexString + ") (VALUE) (" + value + "))";
            } else if (SKIP.equals(action)) {
                return "((" + formIndexString + ") (SKIP))";
            } else {
                return "";
            }
        }

        @Override
        public int hashCode() {
            return formIndexString.hashCode() ^ value.hashCode() ^ action.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof FormEntryAction) {
                FormEntryAction otherAction = (FormEntryAction)other;
                return formIndexString.equals(otherAction.formIndexString) &&
                        value.equals(otherAction.value) &&
                        action.equals(otherAction.action);
            }
            return false;
        }

        public static FormEntryAction fromString(String entryActionString) {
            String unwrappedEntryActionString =
                    entryActionString.substring(1, entryActionString.length() - 1);
            Vector<String> actionEntries = splitTopParens(unwrappedEntryActionString);
            int entryCount = actionEntries.size();

            if (entryCount != 2 && entryCount != 3) {
                throw new RuntimeException("Form entry action '" + entryActionString +
                        "' has an incorrect number of entries, expected 2 or 3, got " + entryCount);
            }

            String wrappedFormIndexString = actionEntries.get(0);
            String formIndexString = wrappedFormIndexString.substring(1, wrappedFormIndexString.length() - 1);
            if (entryCount == 2) {
                if (("(" + NEW_REPEAT + ")").equals(actionEntries.get(1))) {
                    return buildNewRepeatAction(formIndexString);
                } else {
                    return buildSkipAction(formIndexString);
                }
            } else {
                String wrappedValue = actionEntries.get(2);
                String value = wrappedValue.substring(1, wrappedValue.length() - 1);
                return buildValueSetAction(formIndexString, value);
            }
        }

        public boolean isNewRepeatAction() {
            return NEW_REPEAT.equals(action);
        }

        public boolean isSkipAction() {
            return SKIP.equals(action);
        }
    }

    public static Vector<String> splitTopParens(String sessionString) {
        boolean wasEscapeChar = false;
        int parenDepth = 0;
        int topParenStart = 0;
        Vector<String> tokens = new Vector<String>();

        for (int i = 0, n = sessionString.length(); i < n; i++) {
            char c = sessionString.charAt(i);
            if (c == '\\') {
                wasEscapeChar = !wasEscapeChar;
            } else if ((c == '(' || c == ')') && wasEscapeChar) {
                wasEscapeChar = false;
            } else if (c == '(') {
                parenDepth++;
                if (parenDepth == 1) {
                    topParenStart = i;
                }
            } else if (c == ')') {
                if (parenDepth == 1) {
                    tokens.addElement(sessionString.substring(topParenStart, i + 1));
                }
                parenDepth--;
            }
        }

        return tokens;
    }
}