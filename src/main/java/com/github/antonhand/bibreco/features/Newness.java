/**
 * 
 */
package bibreco.features;

import bibreco.model.Bibliography;
import bibreco.model.Record;

import java.time.Year;
import java.util.List;
import java.util.Set;

/**
 * @author Антон Ханджян
 *
 */
public class Newness extends AbstractFeature {
	
	public Newness() {
	}

	public Newness(double weight) {
		this.weight = weight;
	}

	@Override
	public void process(Set<Record> records, List<Bibliography> bibs) {
		int newest = -100000;
		int oldest = 100000;

		for (Record r : records) {
			if (newest < r.getYear()) {
				newest = r.getYear();
			}

			if (oldest > r.getYear()) {
				oldest = r.getYear();
			}
		}

		for (Record r : records) {
			double rank;
			int curyear = Year.now().getValue();
			if (curyear - r.getYear() <= 10) {
				rank = 0.5 + (r.getYear() + 10 - curyear) * 0.05;
			} else {
				rank = 5. / (curyear - r.getYear());
			}
			r.addToRank(rank * weight);
		}
	}

}
