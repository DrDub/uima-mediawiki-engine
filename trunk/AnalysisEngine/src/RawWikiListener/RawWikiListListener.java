package RawWikiListener;

import java.util.Stack;

import org.wikimodel.wem.IWemListenerList;
import org.wikimodel.wem.WikiParameters;

public class RawWikiListListener extends RawWikiListener implements IWemListenerList {
	private Stack<ListContext>	listContext;

	public RawWikiListListener() {
		listContext = new Stack<ListContext>();
	}

	@Override
	public void beginDefinitionDescription() {
		addContent("\n");
	}

	@Override
	public void beginDefinitionList(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void beginDefinitionTerm() {
		addContent("\n");
	}

	@Override
	public void beginList(WikiParameters params, boolean ordered) {
		addContent("\n");
		listContext.push(new ListContext(ordered));
	}

	@Override
	public void beginListItem() {
		addContent(listContext.peek().getCount());
	}

	@Override
	public void beginQuotation(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void beginQuotationLine() {
		addContent("\n");
	}

	@Override
	public void endDefinitionDescription() {
		addContent("\n");
	}

	@Override
	public void endDefinitionList(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void endDefinitionTerm() {
		addContent("\n");
	}

	@Override
	public void endList(WikiParameters params, boolean ordered) {
		listContext.pop();
		addContent("\n");
	}

	@Override
	public void endListItem() {
		addContent("\n");
	}

	@Override
	public void endQuotation(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void endQuotationLine() {
		addContent("\n");
	}

	private void addContent(String str) {
		if (str != null) {
			textContent.append(str);
			currentOffset += str.length();
		}
	}

	private class ListContext {
		public final boolean	ordered;
		private int				count;

		public ListContext(boolean ordered) {
			this.ordered = ordered;
			count = 0;
		}

		public String getCount() {
			if (ordered) {
				count++;
				return Integer.toString(count) + "- ";
			} else
				return "* ";
		}
	}
}