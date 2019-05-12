/**
 * 
 */
package bibreco.features;

import bibreco.model.Bibliography;
import bibreco.model.Record;

import java.util.List;
import java.util.Set;

/**
 * @author Антон Ханджян
 *
 */
public class BibliographiesCount extends AbstractFeature {

	public BibliographiesCount() {
	}

	public BibliographiesCount(double weight) {
		this.weight = weight;
	}

	@Override
	public void process(Set<Record> records, List<Bibliography> bibs) {
		Record[] recs = records.toArray(new Record[records.size()]);
		Integer[] rank = new Integer[records.size()];
		int maxrank = 1;
		for (int i = 0; i < recs.length; i++) {
			int count = 0;

			for (Bibliography b : bibs) {
				if (b.contains(recs[i])) {
					count++;
				}
			}

			rank[i] = count;

			if (count > maxrank) {
				maxrank = count;
			}
		}

		for (int i = 0; i < recs.length; i++) {
			recs[i].addToRank(rank[i] * weight);
		}
	}
}
