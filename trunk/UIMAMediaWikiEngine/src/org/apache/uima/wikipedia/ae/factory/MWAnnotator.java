package org.apache.uima.wikipedia.ae.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.wikipedia.types.Header;
import org.apache.uima.wikipedia.types.Link;
import org.apache.uima.wikipedia.types.Paragraph;
import org.apache.uima.wikipedia.types.Section;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * This class handles the creation of all the annotations for the CAS. The
 * {@link org.apache.uima.wikipedia.ae.parser.MWRevisionBuilder document builder} calls the method provided by
 * this factory to populate the CAS with annotations while processing the content.
 * <p>
 * For now, we handle {@link org.apache.uima.wikipedia.types.Header},
 * {@link org.apache.uima.wikipedia.types.Section}, {@link org.apache.uima.wikipedia.types.Paragraph} and
 * {@link org.apache.uima.wikipedia.types.Link} annotations.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWAnnotator {
	/** The CAS we are working on */
	private final JCas				cas;
	/** Lists of annotations relative to this CAS */
	private final List<Header>		headers;
	private final List<Link>		links;
	private final List<Paragraph>	paragraphs;
	private final List<Section>		sections;
	private final Stack<Section>	currentSections;

	/**
	 * Initialize the factory with a new CAS to process.
	 * 
	 * @param cas
	 *            the CAS to process
	 */
	public MWAnnotator(JCas cas) {
		this.cas = cas;
		headers = new ArrayList<Header>();
		links = new ArrayList<Link>();
		paragraphs = new ArrayList<Paragraph>();
		sections = new ArrayList<Section>();
		currentSections = new Stack<Section>();
	}

	/**
	 * Creates a new {@link org.apache.uima.wikipedia.types.Header} annotation of level <code>level</code>
	 * starting at <code>offset</code>.
	 * 
	 * @param level
	 *            the header's level
	 * @param offset
	 *            the starting offset
	 */
	public void newHeader(int level, int offset) {
		final Header h = new Header(cas);
		h.setBegin(offset);
		h.setLevel(level);
		headers.add(h);
	}

	/**
	 * Creates a new {@link org.apache.uima.wikipedia.types.Link} annotation.
	 * 
	 * @param label
	 *            the link's label (name)
	 * @param href
	 *            the link's adress
	 * @param offset
	 *            the starting offset
	 */
	public void newLink(String label, String href, int offset) {
		final Link l = new Link(cas);
		l.setBegin(offset);
		l.setLabel(label);
		l.setLink(href);
		l.setEnd(offset + label.length());
		links.add(l);
	}

	/**
	 * Creates a new block annotation of the provided type. Currently, only
	 * {@link org.apache.uima.wikipedia.types.Paragraph} are supported.
	 * 
	 * @param type
	 *            the block's type.
	 * @param offset
	 *            the starting offset.
	 */
	public void newBlock(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				final Paragraph p = new Paragraph(cas);
				p.setBegin(offset);
				paragraphs.add(p);
				break;
		}
	}

	/**
	 * Creates a new {@link org.apache.uima.wikipedia.types.Section} annotation. The section's level is the
	 * same as the corresponding header's.
	 * 
	 * @param level
	 *            the section's level.
	 * @param offset
	 *            the starting offset.
	 */
	public void newSection(int level, int offset) {
		final Section s = new Section(cas);
		s.setBegin(offset);
		s.setLevel(level);
		currentSections.push(s);
	}

	/**
	 * This method handles the closing of various annotations. For now, it handles closing headers, sections
	 * and it also closes the unclosed section when we reach the end of the document.
	 * 
	 * @param type
	 *            the type of the annotation we are closing.
	 * @param offset
	 *            the ending offset.
	 */
	public void end(String type, int offset) {

		if (type.equals("header")) {
			headers.get(headers.size() - 1).setEnd(offset);
			currentSections.peek().setTitle(headers.get(headers.size() - 1));
		} else if (type.equals("section")) {
			final Section s = currentSections.pop();
			s.setEnd(offset);
			s.setParent(currentSections.peek());
			sections.add(s);
		} else if (type.equals("unclosed")) {
			final Section root = currentSections.firstElement();
			currentSections.remove(0);
			for (final Section s : currentSections) {
				s.setEnd(offset);
				s.setParent(root);
				sections.add(s);
			}
			root.setEnd(offset);
			sections.add(root);
		}
	}

	/**
	 * This method handles the closing of block annotations. For the moment, only paragraphs are considered.
	 * 
	 * @param type
	 *            the block annotation type.
	 * @param offset
	 *            the ending offset.
	 */
	public void end(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				paragraphs.get(paragraphs.size() - 1).setEnd(offset);
				break;
		}
	}

	/**
	 * Creates one big list with all the different annotations.
	 * 
	 * @return all the annotations gathered by this factory.
	 */
	public List<Annotation> getAnnotations() {
		final List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.addAll(headers);
		annotations.addAll(links);
		annotations.addAll(paragraphs);
		annotations.addAll(sections);
		return annotations;
	}
}