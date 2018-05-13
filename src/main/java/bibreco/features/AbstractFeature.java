/**
 *
 */
package bibreco.features;

/**
 * @author Антон Ханджян
 *
 */
public abstract class AbstractFeature implements Feature {
	protected double weight = 1;

	AbstractFeature() {}

	public AbstractFeature(double weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight() {
		return weight;
	}

}
