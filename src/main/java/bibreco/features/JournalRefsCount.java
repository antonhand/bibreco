/**
 * 
 */
package bibreco.features;

import bibreco.model.Bibliography;
import bibreco.model.Record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Антон Ханджян
 *
 */
public class JournalRefsCount extends AbstractFeature {

	/**
	 * 
	 */
	public JournalRefsCount() {
	}

	/**
	 * @param weight
	 */
	public JournalRefsCount(double weight) {
		super(weight);
	}

	@Override
	public void process(Set<Record> records, List<Bibliography> bibs) {
		Map<String, Integer> refs = new HashMap<>();
		Record[] recs = records.toArray(new Record[records.size()]);
		Integer[] rank = new Integer[records.size()];
		int maxrank = 1;
		for (int i = 0; i < recs.length; i++) {
			int refcount = 1;
			String journ = recs[i].getJournal();
	
			if (!refs.containsKey(journ)) {
				for (int k = i + 1; k < recs.length; k++) {
					if (recs[k].getJournal() != null && recs[k].getJournal().equals(journ)) {
						refcount++;
					}
				}

				refs.put(journ, refcount);
			}

			refcount = refs.get(journ);


			rank[i] = refcount;

			if (refcount > maxrank) {
				maxrank = refcount;
			}
		}

		for (int i = 0; i < recs.length; i++) {
			recs[i].addToRank(rank[i] * weight / maxrank);
		}
	}

}
