/**
 *
 */
package bibreco.features;

import bibreco.model.Bibliography;
import bibreco.model.Record;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.StemmingPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Антон Ханджян
 *
 */
public class TitleSimilarity extends AbstractFeature {
    private int clustersNum;

	/**
	 * 
	 */
	public TitleSimilarity(int clustersNum) {
	    this.clustersNum = clustersNum;
	}

	/**
	 * @param weight
	 */
	public TitleSimilarity(int clustersNum, double weight) {
		super(weight);
        this.clustersNum = clustersNum;
	}

	@Override
	public void process(Set<Record> records, List<Bibliography> bibs)  {
        Logger.getLogger("org.deeplearning4j").setLevel(Level.SEVERE);
        Logger.getLogger("org.nd4j").setLevel(Level.SEVERE);
        Logger.getLogger("org.reflections").setLevel(Level.SEVERE);

	    Record[] recs = records.toArray(new Record[records.size()]);
        try {
            List<String> st = new ArrayList<>();
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/en"), StandardCharsets.UTF_8);

            for(Record r : recs){
                if(r.getTitle() != "") {
                    lines.add(r.getTranslTitle());
                    st.add(r.getTranslTitle());
                } else {
                    lines.add(r.getTranslTitle());
                    st.add(r.getTranslJournal());
                }
            }

            SentenceIterator iter = new CollectionSentenceIterator(lines);
            AbstractCache<VocabWord> cache = new AbstractCache<>();

            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new StemmingPreprocessor());

            LabelsSource source = new LabelsSource("DOC_");

            ParagraphVectors vec = new ParagraphVectors.Builder()
                    .minWordFrequency(1)
                    .iterations(10)
                    .epochs(1)
                    .layerSize(100)
                    .learningRate(0.025)
                    .labelsSource(source)
                    .windowSize(3)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .vocabCache(cache)
                    .trainWordVectors(false)
                    .sampling(0)
                    .build();
            vec.fit();

            KMeansClustering kMeansClustering = KMeansClustering.setup(clustersNum,500,"cosinesimilarity",true);
            List<Point> points = Point.toPoints(vec.inferVectorBatched(st));

            ClusterSet clusterSet = kMeansClustering.applyTo(points);



            for(int i = 0; i < recs.length; i++){
                recs[i].addToRank(weight * clusterSet.getDistanceFromNearestCluster(points.get(i)));
            }


        } catch (Exception e) {}


	}
}
