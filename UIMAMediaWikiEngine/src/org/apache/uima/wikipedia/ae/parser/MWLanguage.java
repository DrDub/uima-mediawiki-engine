package org.apache.uima.wikipedia.ae.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.wikipedia.ae.parser.block.MWHeaderBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWListBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWParagraphBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWPreformattedBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWTableBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWToCBlock;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

/*
 *  Copyright [2010] [Fabien Poulard &lt;fabien.poulard@univ-nantes.fr&gt;, Maxime Bury, Maxime Rihouey] 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *   This class is based on the work of the Eclipse Mylyn Open Source Project,
 *   wich is realeased under the Eclipse Public License:
 *   
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 */

public class MWLanguage extends MediaWikiLanguage {
	MWTemplateResolver	resolver;

	public MWLanguage() {
		super();
		resolver = new MWTemplateResolver();
		final List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	public void addMacro(String name, String replacement) {
		resolver.addMacro(name, replacement);
		final List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros()) {
			markupContent = preprocessContent(markupContent);
		}
		super.processContent(parser, markupContent, asDocument);
	}

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies. DO NOT REORDER ITEMS BELOW!!

		blocks.add(new MWHeaderBlock());
		blocks.add(new MWListBlock());
		blocks.add(new MWPreformattedBlock());
		blocks.add(new MWTableBlock());
		blocks.add(new MWToCBlock());

		for (final Block block : blocks) {
			// Paragraphs cant break themselves
			if (block instanceof MWParagraphBlock) {
				continue;
			}
			// Any other block causes a paragraph to be broken
			paragraphBreakingBlocks.add(block);
		}
	}

	@Override
	protected Block createParagraphBlock() {
		return new MWParagraphBlock();
	}

	private String preprocessContent(String markupContent) {
		return new MWTemplateProcessor(this).processTemplates(markupContent);
	}
}