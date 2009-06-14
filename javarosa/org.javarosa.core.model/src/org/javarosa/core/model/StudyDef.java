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

package org.javarosa.core.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.core.util.externalizable.ExtWrapList;
import org.javarosa.core.util.externalizable.Externalizable;
import org.javarosa.core.util.externalizable.PrototypeFactory;

/**
 * This class encapsulates all form definitions of a particular study.
 * 
 * @author Mark Gerard
 *
 */
public class StudyDef implements Externalizable {
	
	private String name = Constants.EMPTY_STRING;
	private String variableName = Constants.EMPTY_STRING;
	
	private byte id = Constants.NULL_ID;
	
	private Vector forms;
	
	public StudyDef() {

	}
	
	/** Copy constructor. */
	public StudyDef(StudyDef studyDef) {
		this(studyDef.getId(),studyDef.getName(),studyDef.getVariableName());
		copyForms(studyDef.getForms());
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param variableName
	 */
	public StudyDef(byte id, String name, String variableName) {
		setId(id);
		setName(name);
		setVariableName(variableName);;
	}
	
	/** 
	 * Constructs a new study definition from the following parameters.
	 * 
	 * @param id - the numeric unique identifier of the study.
	 * @param name - the display name of the study.
	 * @param variableName - the text unique identifier of the study.
	 * @param forms - the collection of form definitions in the study.
	 */
	public StudyDef(byte id, String name, String variableName,Vector forms) {
		this(id,name,variableName);
		setForms(forms);
	}

	public Vector getForms() {
		return forms;
	}

	public void setForms(Vector forms) {
		this.forms = forms;
	}

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	public FormDef getFormAt(byte index){
		return (FormDef)forms.elementAt(index);
	}
	
	public void addForm(FormDef formDef){
		if(forms == null)
			forms = new Vector();
		forms.addElement(formDef);
	}
	
	public void addForms(Vector formList){
		if(formList != null){
			if(forms == null)
				forms = formList;
			else{
				for(byte i=0; i<formList.size(); i++ )
					forms.addElement(formList.elementAt(i));
			}
		}
	}
	
	/**
	 * Gets a form definition with a given string identifier.
	 * 
	 * @param varName - the string identifier.
	 * @return - the form definition.
	 */
	public FormDef getForm(String varName){
		for(byte i=0; i<forms.size(); i++){
			FormDef def = (FormDef)forms.elementAt(i);
			if(def.getTitle().equals(varName))
				return def;
		}
		
		return null;
	}
	
	/**
	 * Gets a form definition with a given numeric identifier.
	 * 
	 * @param formId - the numeric identifier.
	 * @return - the form definition.
	 */
	public FormDef getForm(int formId){
		for(byte i=0; i<forms.size(); i++){
			FormDef def = (FormDef)forms.elementAt(i);
			if(def.getID() == formId)
				return def;
		}
		
		return null;
	}

	public String toString() {
		return getName();
	}
	
	private void copyForms(Vector forms){
		this.forms = new Vector();
		for(byte i=0; i<forms.size(); i++) {
			//this.forms.addElement(new FormDef((FormDef)forms.elementAt(i)));
			//TODO: Write a copy constructor for this code
		}
	}
	
	/** 
	 * Reads the study definition object from the stream.
	 * 
	 * @param dis - the stream to read from.
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void readExternal(DataInputStream in, PrototypeFactory pf) throws IOException, DeserializationException {
		
		setId(in.readByte());
		setName(in.readUTF());
		setVariableName(in.readUTF());
		
		setForms(ExtUtil.nullIfEmpty((Vector)ExtUtil.read(in, new ExtWrapList(FormDef.class))));
	}

	/** 
	 * Writes the study collection object to the stream.
	 * 
	 * @param dos - the stream to write to.
	 * @throws IOException
	 */
	public void writeExternal(DataOutputStream out) throws IOException {
		
		out.writeByte(getId());
		out.writeUTF(getName());
		out.writeUTF(getVariableName());
		
		ExtUtil.write(out, new ExtWrapList(ExtUtil.emptyIfNull(getForms())));
		
	}

}
