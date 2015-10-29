package de.intarsys.pdf.st;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class STTools {

	public static class EntryCollector implements IXRefEntryVisitor {

		private List<STXRefEntry> entries = new ArrayList<STXRefEntry>();

		public List<STXRefEntry> getEntries() {
			return entries;
		}

		public void visitFromCompressed(STXRefEntryCompressed entry)
				throws XRefEntryVisitorException {
		}

		public void visitFromFree(STXRefEntryFree entry)
				throws XRefEntryVisitorException {
		}

		public void visitFromOccupied(STXRefEntryOccupied entry)
				throws XRefEntryVisitorException {
			if (entry.getObjectNumber() == 0) {
				return;
			}
			entries.add(entry);
		}

	}

	public static List<STXRefEntry> getOccupiedEntries(STXRefSection section)
			throws IOException {
		EntryCollector collector = new EntryCollector();
		visitEntries(section, collector);
		return collector.getEntries();
	}

	public static void visitEntries(STXRefSection section,
			IXRefEntryVisitor visitor) throws IOException {
		Iterator i = section.subsectionIterator();
		while (i.hasNext()) {
			STXRefSubsection subsection = (STXRefSubsection) i.next();
			for (Iterator ie = subsection.getEntries().iterator(); ie.hasNext();) {
				try {
					((STXRefEntry) ie.next()).accept(visitor);
				} catch (XRefEntryVisitorException e) {
					// in this context the exception type is always an
					// IOException
					throw (IOException) e.getCause();
				}
			}
		}
	}
}
