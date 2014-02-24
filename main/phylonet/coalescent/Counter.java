package phylonet.coalescent;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import phylonet.tree.model.Tree;
import phylonet.tree.model.sti.STITreeCluster;
import phylonet.tree.model.sti.STITreeCluster.Vertex;
import phylonet.util.BitSet;

public abstract class Counter <T> {

	protected ClusterCollection clusters;
	
	HashMap<T, Integer> weights;

	protected String getSpeciesName(String geneName) {
		String stName = geneName;
		if (GlobalMaps.taxonNameMap != null) {
			stName = GlobalMaps.taxonNameMap.getTaxonName(geneName);
		}
		return stName;
	}

	protected boolean addToClusters(STITreeCluster c, int size) {
		Vertex nv = c.new Vertex();
		return clusters.addCluster(nv, size);
	}

	void addAllPossibleSubClusters(STITreeCluster cluster, int size) {
		BitSet bs = (BitSet) cluster.getBitSet().clone();
		bs.clear(0, size);
		while (true) {
			int tsb = bs.nextClearBit(0);
			if (tsb >= size) {
				break;
			}
			bs.set(tsb);
			bs.clear(0, tsb);
			STITreeCluster c = new STITreeCluster();
			c.setCluster((BitSet) bs.clone());
			addToClusters(c, c.getClusterSize());
		}
		System.err
				.println("Number of Clusters After Adding All possible clusters: "
						+ clusters.getClusterCount());
	}

	public Integer getCalculatedWeight(T bi) {
//		if (!weights.containsKey(bi)) {
//			// weights.put(bi,calculateMissingWeight(bi));
//			return null;
//		}
		return weights.get(bi);
	}
	
	interface CalculateWeightTask {
		Integer compute();
	}
	
	public abstract CalculateWeightTask getWeightCalculateTask(T t);
	
	public abstract void preCalculateWeights(List<Tree> trees, List<Tree> extraTrees);

	public abstract void addExtraBipartitionsByInput(ClusterCollection extraClusters,
			List<Tree> trees, boolean extraTreeRooted);
	
	public abstract void computeTreePartitions(Inference<T> inference);
}