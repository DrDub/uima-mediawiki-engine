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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An immutable set of styles.
 * 
 * @author MikhailKotelnikov
 * @author VincentMassol
 */
public class WikiFormat {

	public static WikiFormat				EMPTY	= new WikiFormat();

	private String							fClosingTags;

	private String							fOpeningTags;

	private final LinkedHashSet<WikiStyle>	fStyles	= new LinkedHashSet<WikiStyle>();

	private WikiParameters					fParams	= WikiParameters.EMPTY;

	/**
     * 
     */
	public WikiFormat() {
		super();
	}

	/**
	 * @param styles
	 */
	public WikiFormat(Set<WikiStyle> styles) {
		super();
		fStyles.addAll(styles);
	}

	public WikiFormat(Set<WikiStyle> styles, Collection<WikiParameter> params) {
		this(styles);
		fParams = new WikiParameters(params);
	}

	public WikiFormat(Collection<WikiParameter> params) {
		this(Collections.<WikiStyle> emptySet(), params);
	}

	/**
	 * @param style
	 */
	public WikiFormat(WikiStyle style) {
		fStyles.add(style);
	}

	public WikiFormat(WikiStyle style, Collection<WikiParameter> params) {
		this(Collections.<WikiStyle> singleton(style));
	}

	/**
	 * @param styles
	 */
	public WikiFormat(WikiStyle[] styles) {
		super();
		for (final WikiStyle style : styles) {
			fStyles.add(style);
		}
	}

	public WikiFormat setParameters(Collection<WikiParameter> params) {
		return new WikiFormat(fStyles, params);
	}

	/**
	 * Creates a new style set and adds the given style to it.
	 * 
	 * @param style
	 *            the style to add
	 * @return a new copy of the style set containing the given style
	 */
	public WikiFormat addStyle(WikiStyle style) {
		if (fStyles.contains(style))
			return this;
		final WikiFormat clone = getClone();
		clone.fStyles.add(style);
		return clone;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof WikiFormat))
			return false;
		final WikiFormat set = (WikiFormat) obj;
		return fStyles.equals(set.fStyles) && fParams.equals(set.fParams);
	}

	/**
	 * @return a new clone of this format object
	 */
	protected WikiFormat getClone() {
		return new WikiFormat(fStyles, fParams.toList());
	}

	/**
	 * Returns opening or closing tags corresponding to the given format(it depends on the given flag).
	 * 
	 * @param open
	 *            if this flag is <code>true</code> then this method returns opening tags for this format
	 * @return opening or closing tags corresponding to the given format(it depends on the given flag)
	 */
	public String getTags(boolean open) {
		if (fOpeningTags == null) {
			final StringBuffer o = new StringBuffer();
			final StringBuffer c = new StringBuffer();
			for (final WikiStyle style : fStyles) {
				o.append("<").append(style).append(">");
				c.insert(0, ">").insert(0, style).insert(0, "</");
			}
			fOpeningTags = o.toString().intern();
			fClosingTags = c.toString().intern();
		}
		return open ? fOpeningTags : fClosingTags;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// Random number. See
		// http://www.geocities.com/technofundo/tech/java/equalhash.html
		// for the detail of this algorithm.
		int hash = 8;
		hash = 31 * hash + (null == fStyles ? 0 : fStyles.hashCode());
		hash = 31 * hash + (null == fParams ? 0 : fParams.hashCode());
		return hash;
	}

	/**
	 * @param style
	 *            the style to check
	 * @return <code>true</code> if this format has the specified style
	 */
	public boolean hasStyle(WikiStyle style) {
		return fStyles.contains(style);
	}

	/**
	 * Creates a new style set which does not contain the specified style.
	 * 
	 * @param style
	 *            the style to add
	 * @return a new copy of the style set containing the given style
	 */
	public WikiFormat removeStyle(WikiStyle style) {
		if (!fStyles.contains(style))
			return this;
		final WikiFormat clone = getClone();
		clone.fStyles.remove(style);
		return clone;
	}

	/**
	 * Creates a new format object where the specified style is switched: if this format contains the given style then
	 * the resulting format does not and vice versa.
	 * 
	 * @param wikiStyle
	 *            the style to switch
	 * @return a format object where the given style is inverted relatively to this format
	 */
	public WikiFormat switchStyle(WikiStyle wikiStyle) {
		final WikiFormat clone = getClone();
		if (clone.fStyles.contains(wikiStyle)) {
			clone.fStyles.remove(wikiStyle);
		} else {
			clone.fStyles.add(wikiStyle);
		}
		return clone;
	}

	/**
	 * @return the list of styles in the order in which they were created
	 */
	public List<WikiStyle> getStyles() {
		return new ArrayList<WikiStyle>(fStyles);
	}

	public List<WikiParameter> getParams() {
		return fParams.toList();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fStyles.toString();
	}
}