/**
 * 
 */
package bibreco.features;

import bibreco.model.Author;
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
public class AuthorsRefsCount extends AbstractFeature {

	public AuthorsRefsCount() {
	}

	public AuthorsRefsCount(double weight) {
		super(weight);
	}

	@Override
	public void process(Set<Record> records, List<Bibliography> bibs) {
		Map<Author, Integer> refs = new HashMap<>();
		Record[] recs = records.toArray(new Record[records.size()]);
		Integer[] rank = new Integer[records.size()];
		int maxrank = 1;
		for (int i = 0; i < recs.length; i++) {
			int maxref = 0;

			for (Author a : recs[i].getAuthors()) {
				int refcount = 1;

				if (!refs.containsKey(a)) {
					for (int k = i + 1; k < recs.length; k++) {
						if (recs[k].getAuthors().contains(a)) {
							refcount++;
						}
					}

					refs.put(a, refcount);
				}

				refcount = refs.get(a);

				if (refcount > maxref) {
					maxref = refcount;
				}
			}

			rank[i] = maxref;

			if (maxref > maxrank) {
				maxrank = maxref;
			}
		}

		for (int i = 0; i < recs.length; i++) {
			recs[i].addToRank(rank[i] * weight / maxrank);
		}
	}
}
