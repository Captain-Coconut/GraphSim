package matrix;

import Jama.Matrix;

/**
 * Use JAMA as an external library
 * @URL http://math.nist.gov/javanumerics/jama/
 * @author baichuanzhou
 *
 */
public class MatrixUtil {
	
	// Based on Random walk computation of similarities between nodes of a graph, with app to CF
	public Matrix getPseudoInverseL(Matrix D, Matrix A){
		int size = D.getRowDimension();
		
		// construct e
		double [][] e = new double [1][size];
		for(int i = 0; i < e[0].length; i++){
			e[0][i] = 0;
		}
		Matrix eMatrix = new Matrix(e);
		
		Matrix L = D.minus(A);
		Matrix temp = L.minus(eMatrix.arrayTimes(eMatrix.transpose()).times(1.0/size));
		return temp.inverse().plus(eMatrix.arrayTimes(eMatrix.transpose()).times(1.0/size));
	}
}
