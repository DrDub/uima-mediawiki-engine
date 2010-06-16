/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikimodel.wem.impl.WikiScannerUtil;

/**
 * This is a default implementation of the {@link IWikiParams} interface.
 * 
 * @author kotelnikov
 * @author thomas.mortagne
 */
public class WikiParameters implements Iterable<WikiParameter> {

	/**
	 * The default character to use has escaping char.
	 */
	public static final char			DEFAULT_ESCAPECHAR	= '\\';

	/**
	 * An empty parameter list
	 */
	public final static WikiParameters	EMPTY				= new WikiParameters();

	/**
     *
     */
	private static final long			serialVersionUID	= 1253393289284318413L;

	public static WikiParameters newWikiParameters(String str) {
		return newWikiParameters(str, DEFAULT_ESCAPECHAR);
	}

	public static WikiParameters newWikiParameters(String str, char escapeChar) {
		if (str == null)
			return EMPTY;
		str = str.trim();
		if ("".equals(str))
			return EMPTY;
		return new WikiParameters(str, escapeChar);
	}

	protected final List<WikiParameter>		fList	= new ArrayList<WikiParameter>();

	private Map<String, WikiParameter[]>	fMap;

	private String							fStr;

	/**
     */
	protected WikiParameters() {
		this((String) null);
	}

	/**
	 * @param list
	 */
	public WikiParameters(Collection<WikiParameter> list) {
		super();
		fList.addAll(list);
	}

	/**
	 * @param str
	 */
	public WikiParameters(String str) {
		super();
		WikiScannerUtil.splitToPairs(str, fList);
	}

	public WikiParameters(String str, char escapeChar) {
		super();
		WikiScannerUtil.splitToPairs(str, fList, escapeChar);
	}

	/**
	 * @param str
	 * @param delimiter
	 */
	public WikiParameters(String str, String delimiter) {
		super();
		WikiScannerUtil.splitToPairs(str, fList, delimiter);
	}

	public WikiParameters(WikiParameters parameters) {
		super();
		fList.addAll(parameters.fList);
	}

	/**
	 * Creates a new copy of this parameter object with new specified key/value pair.
	 * 
	 * @param key
	 *            the parameter name
	 * @param value
	 *            the value of the parameter
	 * @return a new copy of parameters object with the given key/value pair
	 */
	public WikiParameters addParameter(String key, String value) {
		final WikiParameters result = new WikiParameters();
		result.fList.addAll(fList);
		result.fList.add(new WikiParameter(key, value));
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WikiParameters))
			return false;
		final WikiParameters params = (WikiParameters) obj;
		return fList.equals(params.fList);
	}

	/**
	 * @param pos
	 *            the position of the parameter
	 * @return the parameter from the specified position
	 */
	public WikiParameter getParameter(int pos) {
		return pos < 0 || pos >= fList.size() ? null : fList.get(pos);
	}

	/**
	 * @param key
	 *            the key of the parameter
	 * @return the wiki parameter by key
	 */
	public WikiParameter getParameter(String key) {
		final WikiParameter[] list = getParameters(key);
		return list != null ? list[0] : null;
	}

	private Map<String, WikiParameter[]> getParameters() {
		if (fMap == null) {
			fMap = new HashMap<String, WikiParameter[]>();
			for (final WikiParameter param : fList) {
				final String key = param.getKey();
				final WikiParameter[] list = fMap.get(key);
				final int len = list != null ? list.length : 0;
				final WikiParameter[] newList = new WikiParameter[len + 1];
				if (len > 0) {
					System.arraycopy(list, 0, newList, 0, len);
				}
				newList[len] = param;
				fMap.put(key, newList);
			}
		}
		return fMap;
	}

	/**
	 * Returns all parameters with this key
	 * 
	 * @param key
	 *            the key of the parameter
	 * @return the wiki parameter by key
	 */
	public WikiParameter[] getParameters(String key) {
		final Map<String, WikiParameter[]> map = getParameters();
		final WikiParameter[] list = map.get(key);
		return list;
	}

	/**
	 * Returns the number of parameters in the internal list.
	 * 
	 * @return the number of parameters in the internal list
	 */
	public int getSize() {
		return fList.size();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return fList.hashCode();
	}

	public Iterator<WikiParameter> iterator() {
		return fList.iterator();
	}

	/**
	 * @param key
	 *            the key of the parameter to remove
	 * @return a new copy of parameter list without the specified parameter; if this parameter list does not contain
	 *         such a key then this method returns a reference to this object
	 */
	public WikiParameters remove(String key) {
		int pos = 0;
		for (final WikiParameter param : fList) {
			if (key.equals(param.getKey())) {
				break;
			}
			pos++;
		}
		WikiParameters result = this;
		if (pos < fList.size()) {
			result = new WikiParameters(fList);
			result.fList.remove(pos);
		}
		return result;
	}

	/**
	 * Returns a new list containing all parameters defined in this object.
	 * 
	 * @return a list of all parameters
	 */
	public List<WikiParameter> toList() {
		final List<WikiParameter> result = new ArrayList<WikiParameter>(fList);
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (fStr == null) {
			final StringBuffer buf = new StringBuffer();
			final int len = fList.size();
			int counter = 0;
			for (int i = 0; i < len; i++) {
				final WikiParameter pair = fList.get(i);
				if (pair.isValid()) {
					buf.append(" ");
					buf.append(pair);
					counter++;
				}
			}
			fStr = buf.toString();
		}
		return fStr;
	}
}
