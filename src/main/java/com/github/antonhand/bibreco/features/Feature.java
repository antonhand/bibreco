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
public interface Feature {
	double getWeight();

	void process(Set<Record> records, List<Bibliography> bibs);
}
