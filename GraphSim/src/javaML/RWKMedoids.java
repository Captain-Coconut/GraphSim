package javaML;

import java.util.Random;

import net.sf.javaml.clustering.*;
import net.sf.javaml.core.*;
import net.sf.javaml.distance.*;
import net.sf.javaml.tools.*;


// A modified version of KMedoids.java
public class RWKMedoids {
	/* Distance measure to measure the distance between instances */
	private DistanceMeasure dm;

	/* Number of clusters to generate */
	private int numberOfClusters;

	/* Random generator for selection of candidate medoids */
	private Random rg;

	/* The maximum number of iterations the algorithm is allowed to run. */
	private int maxIterations;

	// Default constructor
	public RWKMedoids() {
		this(4, 100, new RandomWalkDistance());
	}
	
	public RWKMedoids(int numberOfClusters, int maxIterations, DistanceMeasure dm) {
		super();
		this.numberOfClusters = numberOfClusters;
		this.maxIterations = maxIterations;
		this.dm = dm;
		rg = new Random(System.currentTimeMillis());
	}


}
