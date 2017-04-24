package publisher;

public class UnionFind {
	public int[] id;

	public UnionFind(int N) {
		id = new int[N];
		for (int i = 0; i < N; i++)
			id[i] = i;
	}
	
	public int root(int i) {
		while (i != id[i]) {
			id[i] = id[id[i]]; // compression technique
			i = id[i];
		}
		return i;
	}

	public boolean find(int p, int q) {
		return root(p) == root(q);
	}

	public void unite(int p, int q) {
		int i = root(p);
		int j = root(q);
		id[i] = j;
	}
}
