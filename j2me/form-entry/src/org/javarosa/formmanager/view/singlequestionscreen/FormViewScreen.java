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

package org.javarosa.formmanager.view.singlequestionscreen;

import org.javarosa.core.api.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.services.locale.Localization;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.formmanager.view.IFormEntryView;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.IconItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TreeItem;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.HashMap;

public class FormViewScreen extends Form implements IFormEntryView {

	private FormEntryModel model;
	// private FormViewManager parent;

	private TreeItem tree;
	private HashMap itemHash;
	private HashMap indexHash;

	// GUI elements
	public static Command selectCommand;
	public static Command exitNoSaveCommand;
	public static Command exitSaveCommand;
	public static Command sendCommand;
	public static Command backCommand;

	public FormViewScreen(FormEntryModel model) {
		// #style View_All_Form
		super(model.getForm().getTitle());
		this.model = model;
		setUpCommands();
		createView();
	}

	private void setUpCommands() {
		selectCommand = new Command("Select", Command.ITEM, 1);
		exitNoSaveCommand = new Command(Localization.get("menu.Exit"),
				Command.EXIT, 4);
		exitSaveCommand = new Command(Localization.get("menu.SaveAndExit"),
				Command.SCREEN, 4);
		sendCommand = new Command(Localization.get("menu.SendForm"),
				Command.SCREEN, 4);

		// next command is added on a per-widget basis
		this.addCommand(selectCommand);
		this.addCommand(exitNoSaveCommand);
		// screen.addCommand(exitSaveCommand);

		// TODO: FIXME
		// if(!model.isReadOnly()){
		// this.addCommand(sendCommand);
		// }
	}

	protected void createView() {
		tree = new TreeItem("Form");
		tree.setDefaultCommand(selectCommand);
		javax.microedition.lcdui.Item appendToRoot = tree.appendToRoot("Inbox",null);
		javax.microedition.lcdui.Item appendToNode = tree.appendToNode(appendToRoot, "from", null );
		itemHash = new HashMap();
		indexHash = new HashMap();
		FormIndex index = FormIndex.createBeginningOfFormIndex();
		FormDef form = model.getForm();
		while (!index.isEndOfFormIndex()) {
			if (index.isInForm() && model.isRelevant(index)) {
				FormEntryCaption[] hierachy = model.getCaptionHierarchy(index);
				String previous = null;
				System.out.println("===============");
				for (FormEntryCaption caption : hierachy) {
					FormIndex capIndex = caption.getIndex();
					System.out.println(capIndex.toString());
					if (indexHash.containsKey(capIndex.toString())) {
						previous = capIndex.toString();
						continue;
					} else {
						Item node = previous == null ? null : (Item) indexHash
								.get(previous);
						System.out.println("prev: " + (previous == null));
						System.out.println("node: " + (node == null));
						addItemToNode(node, caption, capIndex);
					}
				}
			}
			index = form.incrementIndex(index);
		}
		this.append(tree);
	}

	private String getStyleName(FormEntryPrompt prompt) {
		if (prompt.isRequired() && prompt.getAnswerValue() == null) {
			return Constants.STYLE_COMPULSORY;
		}
		return null;
	}

	private void addItemToNode(Item node, FormEntryCaption formEntryCaption,
			FormIndex index) {
		String line = "";
		String styleName = StyleSheet.defaultStyle.name;
		if (formEntryCaption instanceof FormEntryPrompt) {
			FormEntryPrompt prompt = (FormEntryPrompt) formEntryCaption;
			styleName = getStyleName(prompt);
			line += prompt.getLongText() + " => ";

			IAnswerData answerValue = prompt.getAnswerValue();
			if (answerValue != null) {
				line += answerValue.getDisplayText();
			}
		} else {
			line += formEntryCaption.getLongText();
		}
		Style style = styleName == null ? null : StyleSheet.getStyle(styleName);
		Item appended;
		if (node != null) {
			System.out.println("add to node: " + line);
			style = StyleSheet.getStyle(Constants.STYLE_COMPULSORY);
			appended = tree.appendToNode(node, line, null, style);
		} else {
			System.out.println("add to root: " + line);
			style = null;
			appended = new IconItem(line, null, style);
			tree.appendToRoot(appended);
		}
		itemHash.put(appended, index);
		indexHash.put(index.toString(), appended);

	}

	private String getHierachyLevelString(int length) {
		String header = "";
		for (int i = 0; i < length; i++) {
			header += "-";
		}
		return header;
	}

	public void destroy() {
	}

	public void show() {
		createView();
	}

	public FormIndex getSelectedIndex() {
		de.enough.polish.ui.Item item = tree.getFocusedItem();
		if (item == null) {
			return null;
		}
		return (FormIndex) itemHash.get(item);
	}
}
