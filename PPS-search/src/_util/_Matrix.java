package _util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import _BioStruct.Ligand;
import _BioStruct.Point3D;

public class _Matrix {
	
	public static double standart_deviationXColValue(double[][] data, int x) {
		double[] data_x = new double[data.length];
		for (int i = 0; i < data.length; i++){
			data_x[i] = data[i][x];
		}
		
		return _Math.standart_deviation(data_x);
	}
	
	public static double averageXColValue(double[][] data, int x) {
		double sum = 0.0;
		for (int i = 0; i < data.length; i++){
			sum += data[i][x];
		}
		return sum / data.length;
	}
	
	public static double[][] matrixCat(double[][]... ds) {
		int ansLen = 0;
		int paramLen = ds.length;
		for (int i = 0; i < paramLen; i++) {
			ansLen += ds[i].length;
		}

		double[][] ans = new double[ansLen][];
		int pos = 0;
		for (int i = 0; i < paramLen; i++) {
			for (int j = 0; j < ds[i].length; j++) {
				ans[pos] = ds[i][j];
				pos++;
			}
		}
		return ans;
	}
	
	public static double[][] matrixCatPerRow(double[][]... ds) {
		int ansLen = 0;
		int paramLen = ds.length;
		for (int i = 0; i < paramLen; i++) {
			ansLen += ds[i][0].length;
		}

		double[][] ans = new double[ds[0].length][ansLen];
		for (int i = 0; i < ans.length; i++){
			int pos = 0;
			for (int j = 0; j < paramLen; j++){
				for (int k = 0; k < ds[j][i].length; k++){
					ans[i][pos++] = ds[j][i][k];
				}
			}
		}
		return ans;
	}
	
	public static Vector<double[]> matrixCat(Vector<double[]> aim, double[][] add) {
		for (int i = 0; i < add.length; i++) {
			aim.add(add[i]);
		}
		return aim;
	}
	
	public static double[][] select(double[][] original, int[] selectPos,
			boolean isSelectePosStartZero) {
		double[][] ans = new double[original.length][selectPos.length];
		for (int i = 0; i < original.length; i++){
			ans[i] = _Vector.select(original[i], selectPos, isSelectePosStartZero);
		}
		return ans;
	}
	
	public static double[][] select(double[][] original, Vector<Integer> selectPos,
			boolean isSelectePosStartZero) {
		double[][] ans = new double[original.length][selectPos.size()];
		for (int i = 0; i < original.length; i++){
			ans[i] = _Vector.select(original[i], selectPos, isSelectePosStartZero);
		}
		return ans;
	}
	
	public static double[][] transpose(double[][] arr){
		double[][] ans = new double[arr[0].length][arr.length];
		for (int i = 0; i < arr.length; i++){
			for (int j = 0; j < arr[i].length; j++){
				ans[j][i] = arr[i][j];
			}
		}
		return ans;
	}
	
	public static double[][] exchangeVDArrToD2Arr(Vector<double[]> VDArr){
		int VDArr_size = VDArr.size();
		double[][] ans = new double[VDArr_size][VDArr.get(0).length];
		for (int i = 0; i < VDArr_size; i++){
			ans[i] = VDArr.get(i);
		}
		return ans;
	}
	
	public static Vector<double[]> exchangeD2ArrToVDArr(double[][] VDArr){
		int VDArr_len = VDArr.length;
		Vector<double[]> ans = new Vector<double[]>();
		for (int i = 0; i < VDArr_len; i++){
			ans.add(VDArr[i]);
		}
		return ans;
	}
	
	public static double[] exchangeVDArrToArr(Vector<double[]> VDArr){
		int VDArr_size = VDArr.size();
		int Arr_column = VDArr.get(0).length;
		double[] ans = new double[VDArr_size*Arr_column];
		for (int i = 0; i < VDArr_size; i++){
			for (int j = 0; j < Arr_column; j++)
			ans[i*Arr_column+j] = VDArr.get(i)[j];
		}
		return ans;
	}
	
	public static double[] exchangeD2ArrToArr(double[][] D2Arr){
		int D2Arr_size = D2Arr.length;
		int Arr_column = D2Arr[0].length;
		double[] ans = new double[D2Arr_size*Arr_column];
		for (int i = 0; i < D2Arr_size; i++){
			for (int j = 0; j < Arr_column; j++)
			ans[i*Arr_column+j] = D2Arr[i][j];
		}
		return ans;
	}
	
	public static double[][] loadMatrix(String matrixFilePath)throws Exception{
		Vector<double[]> tans = new Vector<double[]>();
		
		BufferedReader br = new BufferedReader(new FileReader(matrixFilePath));
		String line = br.readLine();
		while (null != line){
			String[] lineArr = line.trim().split(" +|	");
			double[] t = new double[lineArr.length];
			for (int i = 0; i < t.length; i++){
				t[i] = Double.parseDouble(lineArr[i]);
			}
			tans.add(t);
			line = br.readLine();
		}
		br.close();
		int ansLen = tans.size();
		double[][] ans = new double[ansLen][tans.get(0).length];
		for (int i = 0; i < ansLen; i++){
			for (int j = 0; j < tans.get(0).length; j++){
				ans[i][j] = tans.get(i)[j];
			}
		}
		return ans;
	}
	
	public static double[][] getImageMtx(double[][] oMtx){
		double[][] ans = new double[oMtx.length][oMtx[0].length];
		for (int i = 0; i < oMtx.length; i++){
			for (int j = 0; j < oMtx[0].length; j++){
				ans[i][j] = oMtx[i][j];
			}
		}
		
		return ans;
	}
	
	public static double[][] setAllElementsOfIthRowAndJthCol(double[][] mtx, int row, int col, double val){
		double[][] ans = new double[mtx.length][mtx[0].length];
		for (int i = 0; i < mtx.length; i++){
			for (int j = 0; j < mtx[0].length; j++){
				if (i == row || j == col){
					ans[i][j] = val;
				}else{
					ans[i][j] = mtx[i][j];
				}
			}
		}
		
		return ans;
	}
	
	public static double[][] pssmMulSCD(double[][] pssm, double[][] SCD){
		double[][] ans = new double[pssm.length][SCD[0].length];
		
		for (int i = 0; i < pssm.length; i++){
			for (int j = 0; j < SCD[0].length; j++){
				ans[i][j] = 0;
				for (int k = 0; k < SCD.length; k++){
					ans[i][j] += pssm[i][k] * SCD[k][j];
				}
			}
		}
		
		return ans;
	}
	
	public static double[][] aT_mul_b(double[][] a, double[][] b)throws Exception{
		int rows_a = a.length;
		int rows_b = b.length;
		if (rows_a != rows_b){
			throw new Exception("sorry! the row num of matrix a is not the same as matrix b.");
		}
		
		double[][] aT = transpose(a);
		return a_mul_b(aT, b);
	} 
	
	public static double[][] a_mul_b(double[][] a, double[][] b)throws Exception{
		if (a[0].length != b.length){
			throw new Exception("sorry! the column num of matrix a is not the same as the row num of matrix b.");
		}
		
		double[][] ans = new double[a.length][b[0].length];
		
		for (int i = 0; i < a.length; i++){
			for (int j = 0; j < b[0].length; j++){
				ans[i][j] = 0;
				for (int k = 0; k < b.length; k++){
					ans[i][j] += a[i][k] * b[k][j];
				}
			}
		}
		
		return ans;
	}
	
	/**
	 * @param mtx in R^{M*N}
	 * @param vec in R^{N*1}
	 * @return mtx*vec in R(M*1)
	 * @throws Exception
	 */
	public static double[] mtxMulVec(double[][] mtx, double[] vec)throws Exception{
		int mtxCol = mtx[0].length; 
		int vecNum = vec.length;
		if (mtxCol != vecNum){
			throw new Exception("Sorry! the colum of mtx is not equal to the element number of vec!");
		}
		
		double[] ans = new double[mtx.length];
		for (int i = 0; i < mtx.length; i++){
			ans[i] = 0.0;
			for (int j = 0; j < mtxCol; j++){
				ans[i] += mtx[i][j]*vec[j];
			}
		}
		
		return ans;
	}
	
	public static double[] mean_value_by_colum(double[][] matrix){
		double[] ans = new double[matrix[0].length];
		for (int i = 0; i < ans.length; i++) ans[i] = 0;
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				ans[j] += matrix[i][j];
			}
		}
		for (int i = 0; i < ans.length; i++) ans[i] /= matrix.length;
		return ans;
	}
	
	public static double[] diagonalElement(double[][] squarematrix) throws Exception{
		if (squarematrix.length != squarematrix[0].length){
			throw new Exception("sorry! the param is not square matrix.");
		}
		double[] ans = new double[squarematrix.length];
		for (int i = 0; i < squarematrix.length; i++){
			ans[i] = squarematrix[i][i];
		}
		
		return ans;
	}
	
	public static void saveMatrix(double[][] arr, String saveFilePath){
		File f = new File(saveFilePath);
		if (!f.isFile()) {
			System.out.println(saveFilePath + " is not exist! we create it!");
			try {
				f.createNewFile();
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
		
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#0.000000");

		try {
			FileWriter fw = new FileWriter(f); 
			for (int i = 0; i < arr.length; i++){
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < arr[i].length; j++) {
					sb.append(df.format(arr[i][j]) + " ");
				}
					fw.write(sb.toString().trim());
				fw.write("\n");
			}
			fw.close();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static double[][] normalizedColumn_Type_I(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		double[][] ans = new double[row][col];
		
		for (int c = 0; c < col; c++){
			// calculate the average 
			double c_ave = 0.0;
			for (int r = 0; r < row; r++){
				c_ave += matrix[r][c];
			}
			c_ave /= 1.0*row;
			// calculate the standard deviation 
			double s_dev = 0.0;
			for (int r = 0; r < row; r++){
				s_dev += (matrix[r][c]-c_ave)*(matrix[r][c]-c_ave);
			}
			s_dev = Math.sqrt(s_dev/1.0*row);
			
			if (0.0 == s_dev){
				s_dev += 0.000001;
			}
			//calculate the normalized elements
			for (int r = 0; r < row; r++){
				ans[r][c] = (matrix[r][c] - c_ave) / s_dev;
			}
		}
		
		return ans;
	}
	
	public static double[][] normalizedColumn_Type_II(double[][] matrix) {
		int row = matrix.length;
		int col = matrix[0].length;
		double[][] ans = new double[row][col];

		for (int c = 0; c < col; c++) {
			double max = Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for (int r = 0; r < row; r++) {
				if (matrix[r][c] > max) {
					max = matrix[r][c];
				}
				if (matrix[r][c] < min) {
					min = matrix[r][c];
				}
			}

			if (max == min) {
				max = min + 0.000001;
			}

			// calculate the normalized elements
			for (int r = 0; r < row; r++) {
				ans[r][c] = (matrix[r][c] - min) / (max - min);
			}
		}

		return ans;
	}
	
	/**
	 * x = (x-u)/sd for each row
	 * */
	public static double[][] normalizedRowByGaussEq(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		double[][] ans = new double[row][col];
		
		for (int i = 0; i < row; i++){
			double u = _Math.average(matrix[i]);
			double sd = _Math.standart_deviation(matrix[i]);
			
			for (int j = 0; j < col; j++){
				ans[i][j] = (matrix[i][j] - u) / (sd + 0.000001);
			}
		}
		
		return ans;
	}
	
	/**
	 * x = (x-min)/(max-min) for all
	 * */
	public static double[][] normalizedAllByMaxMin(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		double[][] ans = new double[row][col];
		
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++) {
				if (max < matrix[i][j]){
					max = matrix[i][j];
				}
				if (min > matrix[i][j]){
					min = matrix[i][j];
				}
			}
		}
		
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++){
				ans[i][j] = (matrix[i][j] - min) / (max - min);
			}
		}
		
		return ans;
	}
	
	public static double[] averageRow(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		double[] ans = new double[col];
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++){
				ans[j] += matrix[i][j];
			}
		}
		
		for (int j = 0; j < col; j++){
			ans[j] /= row;
		}
		
		return ans;
	}
	
	public static double[][] normalized_Type_II(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		double[][] ans = new double[row][col];

		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for (int r = 0; r < row; r++){
			for (int c = 0; c < col; c++){
				if (max < matrix[r][c]){
					max = matrix[r][c];
				}
				
				if (min > matrix[r][c]){
					min = matrix[r][c];
				}
			}
		}
		
		for (int r = 0; r < row; r++){
			for (int c = 0; c < col; c++){
				ans[r][c] = (matrix[r][c]-min)/(max-min);
			}
		}
		
		return ans;
	}
	
	public static double[][] eyeMatrix(int rowCol){
		double[][] ans = new double[rowCol][rowCol];
		for (int r = 0; r < rowCol; r++){
			for (int c = 0; c < rowCol; c++){
				ans[r][c] = 0.0;
				if (r == c)
					ans[r][c] = 1.0;
			}
		}
		return ans;
	}
	
	public static double[][] onesMatrix(int row, int col){
		double[][] ans = new double[row][col];
		for (int r = 0; r < row; r++){
			for (int c = 0; c < col; c++){
				ans[r][c] = 1.0;
			}
		}
		return ans;
	}
		
	public static boolean isSameMatrixes(double[][] a, double[][] b)throws Exception{
		int a_row = a.length;
		int a_col = a[0].length;
		int b_row = b.length;
		int b_col = b[0].length;
		
		if (a_row != b_row || a_col != b_col){
			throw new Exception("the size of a and b is not same.");
		}
		
		boolean ans = true;
		
		for (int i = 0; i  < a_row; i++){
			for (int j = 0; j < a_col; j++){
				if (a[i][j] != b[i][j]){
					ans = false;
					break;
				}
			}
			
			if (false == ans){
				break;
			}
		}
		
		return ans;
	}
	
	public static void copyContent(double[][] from, double[][] to)throws Exception{
		if (from == null || to == null){
			throw new Exception("the from or to matrix is null!");
		}
		
		int f_row = from.length;
		int f_col = from[0].length;
		int t_row = to.length;
		int t_col = to[0].length;
		
		if (f_row != t_row || f_col != t_col){
			throw new Exception("the size of a and b is not same.");
		}
		
		for (int i  =0; i < f_row; i++){
			for (int j = 0; j < f_col; j++){
				to[i][j] = from[i][j];
			}
		}
	}
	
	public static double[][] copyContent(double[][] from)throws Exception{
		if (from == null ){
			throw new Exception("the from matrix is null!");
		}
		
		int f_row = from.length;
		int f_col = from[0].length;
		double[][] to = new double[f_row][f_col];
		
		for (int i  =0; i < f_row; i++){
			for (int j = 0; j < f_col; j++){
				to[i][j] = from[i][j];
			}
		}
		
		return to;
	}
	
	public static HashMap<Double, Integer> searchElementTypesAndNum(double[][] matrix){
		int row = matrix.length;
		int col = matrix[0].length;
		HashMap<Double, Integer> ans = new HashMap<Double, Integer>();
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++){
				Integer tmp = ans.get(matrix[i][j]);
				if (null == tmp){
					tmp = new Integer(1);
					ans.put(matrix[i][j], tmp);
				}else{
					ans.put(matrix[i][j], tmp+1);
				}
			}
		}
		
		return ans;
	}
	
	public static double[][] a_add_b(double[][] a, double[][] b)throws Exception{
		int a_row = a.length;
		int a_col = a[0].length;
		int b_row = b.length;
		int b_col = b[0].length;
		
		if (a_row != b_row || a_col != b_col){
			throw new Exception("the size of a and b is not same.");
		}
		
		double[][] ans = new double[a_row][a_col];
		for (int i = 0; i  < a_row; i++){
			for (int j = 0; j < a_col; j++){
				ans[i][j] = a[i][j] + b[i][j];
			}
		}
		
		return ans;
	}
	
	public static double[][] a_sub_b(double[][] a, double[][] b)throws Exception{
		int a_row = a.length;
		int a_col = a[0].length;
		int b_row = b.length;
		int b_col = b[0].length;
		
		if (a_row != b_row || a_col != b_col){
			throw new Exception("the size of a and b is not same.");
		}
		
		double[][] ans = new double[a_row][a_col];
		for (int i = 0; i  < a_row; i++){
			for (int j = 0; j < a_col; j++){
				ans[i][j] = a[i][j] - b[i][j];
			}
		}
		
		return ans;
	}
	
	public static double[][] matrixDivideReal(double[][] a, double r)throws Exception{
		int a_row = a.length;
		int a_col = a[0].length;
		
		double[][] ans = new double[a_row][a_col];
		for (int i = 0; i  < a_row; i++){
			for (int j = 0; j < a_col; j++){
				ans[i][j] = a[i][j] / r;
			}
		}
		
		return ans;
	}

	
	public static double[] toVector_by_Row(double m[][]) {
		int row = m.length;
		int col = m[0].length;
		double[] result = new double[row * col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				result[i * col + j] = m[i][j];
			}
		}
		return result;
	}
	public static double[] toVector_by_Column(double m[][]) {
		int row = m.length;
		int col = m[0].length;
		double[] result = new double[row * col];
		for (int i = 0; i < col; i++) {
			for (int j = 0; j < row; j++) {
				result[i * row + j] = m[j][i];
			}
		}
		return result;
	}
	
	/**
	 * @param mtx : the library mtx
	 * @param vec : the query vec info
	 * @param len : the compare len, [0, len)
	 * @return if the [0, len) content of each vec in mtx is the same as the [0, len) content of the vec, return true, otherwise return false
	 */
	public static boolean isContains(Vector<double[]> mtx, double[] vec, int len){
		for (int i = 0; i < mtx.size(); i++){
			if (true == _Vector.isSame(mtx.get(i), vec, len)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param points : the original points (it is not change)
	 * @param rot : the rotation matrix
	 * @param trans : the translation vector
	 * @return : the transformed points
	 */
	public static Vector<Point3D> rotateAndTrans(final Vector<Point3D> points, double[][] rot, double[] trans){
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			double x = points.get(i).getX();
			double y = points.get(i).getY();
			double z = points.get(i).getZ();
			
			double xt = trans[0] + rot[0][0]*x + rot[0][1]*y + rot[0][2]*z;
			double yt = trans[1] + rot[1][0]*x + rot[1][1]*y + rot[1][2]*z;
			double zt = trans[2] + rot[2][0]*x + rot[2][1]*y + rot[2][2]*z;
			
			ans.add(new Point3D(xt, yt, zt));
		}
		
		return ans;
	}
	
	/**
	 * @param points : the original points (it is not change)
	 * @param rotAndtrans : the rotation matrix, obtained from TMalign or TMscore
	 * Code for rotating Structure B from (x,y,z) to (X,Y,Z):
		for(k=0; k<L; k++)
		{
   			X[k] = t[0] + u[0][0]*x[k] + u[0][1]*y[k] + u[0][2]*z[k]
   			Y[k] = t[1] + u[1][0]*x[k] + u[1][1]*y[k] + u[1][2]*z[k]
   			Z[k] = t[2] + u[2][0]*x[k] + u[2][1]*y[k] + u[2][2]*z[k]
		}
	 * @return : the transformed points
	 */
	public static Vector<Point3D> rotateAndTrans(final Vector<Point3D> points, double[][] rotAndtrans){
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			Point3D oxyz = points.get(i);
			double ox = oxyz.getX();
			double oy = oxyz.getY();
			double oz = oxyz.getZ();
			
			double nx = rotAndtrans[0][0]
					+rotAndtrans[0][1]*ox
					+rotAndtrans[0][2]*oy
					+rotAndtrans[0][3]*oz;
			double ny = rotAndtrans[1][0]
					+rotAndtrans[1][1]*ox
					+rotAndtrans[1][2]*oy
					+rotAndtrans[1][3]*oz;
			double nz = rotAndtrans[2][0]
					+rotAndtrans[2][1]*ox
					+rotAndtrans[2][2]*oy
					+rotAndtrans[2][3]*oz;
			
			Point3D nxyz = new Point3D(nx, ny, nz);
			ans.add(nxyz);
		}
		
		return ans;
	}
	
	public static Vector<Point3D> rotate(final Vector<Point3D> points, double[][] rot){
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			double x = points.get(i).getX();
			double y = points.get(i).getY();
			double z = points.get(i).getZ();
			
			double xt = rot[0][0]*x + rot[0][1]*y + rot[0][2]*z;
			double yt = rot[1][0]*x + rot[1][1]*y + rot[1][2]*z;
			double zt = rot[2][0]*x + rot[2][1]*y + rot[2][2]*z;
			
			ans.add(new Point3D(xt, yt, zt));
		}
		
		return ans;
	}
	
	/**
	 * @param points : the original points (it is not change)
	 * @param rot : the rotation matrix
	 * @param trans : the translation vector
	 * @return : the transformed points
	 */
	public static Ligand rotateAndTrans(final Ligand lig, final Point3D rotCenter, double[][] rot, double[] trans){
		double ax = rotCenter.getX();
		double ay = rotCenter.getY();
		double az = rotCenter.getZ();
		
		Vector<Point3D> new_atoms = new Vector<Point3D>();
		for (int i = 0; i < lig.size(); i++){
			Point3D atom = lig.getPoint(i);
			
			double x = atom.getX();
			double y = atom.getY();
			double z = atom.getZ();
			
			double xt = trans[0] + ax + rot[0][0]*(x-ax) + rot[0][1]*(y-ay) + rot[0][2]*(z-az);
			double yt = trans[1] + ay + rot[1][0]*(x-ax) + rot[1][1]*(y-ay) + rot[1][2]*(z-az);
			double zt = trans[2] + az + rot[2][0]*(x-ax) + rot[2][1]*(y-ay) + rot[2][2]*(z-az);
			
			Point3D new_atom = new Point3D(xt, yt, zt);
			new_atoms.add(new_atom);
		}
		
		Ligand new_lig = new Ligand(lig, true);
		new_lig.setPoints(new_atoms);
		return new_lig;
	}
	
	/**
	 * @param points : the original point (it is not change)
	 * @param rot : the rotation matrix
	 * @param trans : the translation vector
	 * @return : the transformed point
	 */
	public static Point3D rotateAndTrans(final Point3D point, double[][] rot, double[] trans){
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
			
		double xt = trans[0] + rot[0][0]*x + rot[0][1]*y + rot[0][2]*z;
		double yt = trans[1] + rot[1][0]*x + rot[1][1]*y + rot[1][2]*z;
		double zt = trans[2] + rot[2][0]*x + rot[2][1]*y + rot[2][2]*z;
		
		return new Point3D(xt, yt, zt);
	}
	
	public static Point3D rotate(final Point3D point, double[][] rot){
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
			
		double xt = rot[0][0]*x + rot[0][1]*y + rot[0][2]*z;
		double yt = rot[1][0]*x + rot[1][1]*y + rot[1][2]*z;
		double zt = rot[2][0]*x + rot[2][1]*y + rot[2][2]*z;
		
		return new Point3D(xt, yt, zt);
	}
	
	public static Vector<Point3D> transform(final Vector<Point3D> points, double[] trans){
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			double x = points.get(i).getX();
			double y = points.get(i).getY();
			double z = points.get(i).getZ();
			
			double xt = x + trans[0];
			double yt = y + trans[1];
			double zt = z + trans[2];
			
			ans.add(new Point3D(xt, yt, zt));
		}
		
		return ans;
	}
	
	public static Point3D transform(final Point3D point, double[] trans){
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
			
		double xt = x + trans[0];
		double yt = y + trans[1];
		double zt = z + trans[2];
		
		return new Point3D(xt, yt, zt);
	}
	
	/**
	 * @param a : in R^{L*3} (input)
	 * @param b : in R^{L*3} (input) 
	 * @param u	: the rotated mtx information (output)
	 * @param t : the translated vector information (output)
	 * @return : return the 
	 * @description : rotate a to b 
	 * 			at[i][0] = t[0] + u[0][0] * a[i][0] + u[0][1] * a[i][1] + u[0][2] * a[i][2];
	 *			at[i][1] = t[1] + u[1][0] * a[i][0] + u[1][1] * a[i][1] + u[1][2] * a[i][2];
	 *			at[i][2] = t[2] + u[2][0] * a[i][0] + u[2][1] * a[i][1] + u[2][2] * a[i][2];
	 */
	public static double calcutateRotMtxTransVec(Vector<Point3D> a, Vector<Point3D> b, double[][] u, double[] t){
		int row = a.size();
		
		double[][] xx = new double[3+1][row+1];
		double[][] yy = new double[3+1][row+1];
		for (int j = 1; j < row+1; j++){
			xx[1][j] = a.get(j-1).getX();
			xx[2][j] = a.get(j-1).getY();
			xx[3][j] = a.get(j-1).getZ();
			
			yy[1][j] = b.get(j-1).getX();
			yy[2][j] = b.get(j-1).getY();
			yy[3][j] = b.get(j-1).getZ();
		}
		
		double[][] uu = new double[4][4];
		double[] tt = new double[4];
		
		int mode = 1;
		int ier = 0;
		double rms = 0.0;
		int n = row;
		double ans = u3b(xx, yy, n, mode, rms, uu, tt, ier);
		
		if (null == u)
			u = new double[3][3];
		if (null == t)
			t = new double[3];
		
		for (int i = 1; i < 3+1; i++){
			for (int j = 1; j < 3+1; j++){
				u[i-1][j-1] = uu[i][j];
			}
			
			t[i-1] = tt[i];
		}
		
		return ans;
	}
	
	/**
	 * @param a : in R^{L*3} (input)
	 * @param b : in R^{L*3} (input) 
	 * @param u	: the rotated mtx information (output)
	 * @param t : the translated vector information (output)
	 * @return : return the 
	 * @description : rotate a to b 
	 * 			at[i][0] = t[0] + u[0][0] * a[i][0] + u[0][1] * a[i][1] + u[0][2] * a[i][2];
	 *			at[i][1] = t[1] + u[1][0] * a[i][0] + u[1][1] * a[i][1] + u[1][2] * a[i][2];
	 *			at[i][2] = t[2] + u[2][0] * a[i][0] + u[2][1] * a[i][1] + u[2][2] * a[i][2];
	 */
	public static double calcutateRotMtxTransVec(List<Point3D> a, List<Point3D> b, double[][] u, double[] t){
		int row = a.size();
		
		double[][] xx = new double[3+1][row+1];
		double[][] yy = new double[3+1][row+1];
		for (int j = 1; j < row+1; j++){
			xx[1][j] = a.get(j-1).getX();
			xx[2][j] = a.get(j-1).getY();
			xx[3][j] = a.get(j-1).getZ();
			
			yy[1][j] = b.get(j-1).getX();
			yy[2][j] = b.get(j-1).getY();
			yy[3][j] = b.get(j-1).getZ();
		}
		
		double[][] uu = new double[4][4];
		double[] tt = new double[4];
		
		int mode = 1;
		int ier = 0;
		double rms = 0.0;
		int n = row;
		double ans = u3b(xx, yy, n, mode, rms, uu, tt, ier);
		
		if (null == u)
			u = new double[3][3];
		if (null == t)
			t = new double[3];
		
		for (int i = 1; i < 3+1; i++){
			for (int j = 1; j < 3+1; j++){
				u[i-1][j-1] = uu[i][j];
			}
			
			t[i-1] = tt[i];
		}
		
		return ans;
	}
	
	/**
	 * @return which is copied in TM-score
	 */
	private static double u3b(double[][] x, double[][] y, int n, int mode, double rms, double[][] u, double[] t,
			int ier) {
		// cccccccccccccccc Calculate sum of (r_d-r_m)^2
		// cccccccccccccccccccccccccc
		// c w - w(m) is weight for atom pair c m (given)
		// c x - x(i,m) are coordinates of atom c m in set x (given)
		// c y - y(i,m) are coordinates of atom c m in set y (given)
		// c n - n is number of atom pairs (given)
		// c mode - 0:calculate rms only (given)
		// c 1:calculate rms,u,t (takes longer)
		// c rms - sum of w*(ux+t-y)**2 over all atom pairs (result)
		// c u - u(i,j) is rotation matrix for best superposition (result)
		// c t - t(i) is translation vector for best superposition (result)
		// c ier - 0: a unique optimal superposition has been determined(result)
		// c -1: superposition is not unique but optimal
		// c -2: no result obtained because of negative weights w
		// c or all weights equal to zero.
		// cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc
		int i, j, m, m1, l, k;
		double e0, d, h, g;
		double cth, sth, sqrth, p, det, sigma;
		double[] xc = new double[3 + 1];
		double[] yc = new double[3 + 1];
		double[][] a = new double[3 + 1][3 + 1];
		double[][] b = new double[3 + 1][3 + 1];
		double[][] r = new double[3 + 1][3 + 1];
		double[] e = new double[3 + 1];
		double[] rr = new double[6 + 1];
		double[] ss = new double[6 + 1];
		double sqrt3 = 1.73205080756888, tol = 0.01;
		int ip[] = { -100, 1, 2, 4, 2, 3, 5, 4, 5, 6 };
		int ip2312[] = { -100, 2, 3, 1, 2 };
		int a_failed = 0, b_failed = 0;
		double epsilon = 0.000000001;

		// initializtation
		rms = 0;
		e0 = 0;
		for (i = 1; i <= 3; i++) {
			xc[i] = 0.0;
			yc[i] = 0.0;
			t[i] = 0.0;
			for (j = 1; j <= 3; j++) {
				u[i][j] = 0.0;
				r[i][j] = 0.0;
				a[i][j] = 0.0;
				if (i == j) {
					u[i][j] = 1.0;
					a[i][j] = 1.0;
				}
			}
		}

		ier = -1;
		if (n < 1) {
			return -1000;
		}
		ier = -2;
		// compute centers for vector sets x, y
		for (m = 1; m <= n; m++) {
			for (i = 1; i <= 3; i++) {
				xc[i] = xc[i] + x[i][m];
				yc[i] = yc[i] + y[i][m];
			}
		}
		for (i = 1; i <= 3; i++) {
			xc[i] = xc[i] / n;
			yc[i] = yc[i] / n;
		}
		// compute e0 and matrix r
		for (m = 1; m <= n; m++) {
			for (i = 1; i <= 3; i++) {
				e0 = e0 + (x[i][m] - xc[i]) * (x[i][m] - xc[i]) + (y[i][m] - yc[i]) * (y[i][m] - yc[i]);
				d = y[i][m] - yc[i];
				for (j = 1; j <= 3; j++) {
					r[i][j] = r[i][j] + d * (x[j][m] - xc[j]);
				}
			}
		}
		// compute determinat of matrix r
		det = r[1][1] * ((r[2][2] * r[3][3]) - (r[2][3] * r[3][2]))
				- r[1][2] * ((r[2][1] * r[3][3]) - (r[2][3] * r[3][1]))
				+ r[1][3] * ((r[2][1] * r[3][2]) - (r[2][2] * r[3][1]));

		sigma = det;
		// compute tras(r)*r
		m = 0;
		for (j = 1; j <= 3; j++) {
			for (i = 1; i <= j; i++) {
				m = m + 1;
				rr[m] = r[1][i] * r[1][j] + r[2][i] * r[2][j] + r[3][i] * r[3][j];
			}
		}

		double spur = (rr[1] + rr[3] + rr[6]) / 3.0;
		double cof = (((((rr[3] * rr[6] - rr[5] * rr[5]) + rr[1] * rr[6]) - rr[4] * rr[4]) + rr[1] * rr[3])
				- rr[2] * rr[2]) / 3.0;
		det = det * det;

		for (i = 1; i <= 3; i++) {
			e[i] = spur;
		}
		// System.out.println("spur="+spur);
		// System.exit(1);
		if (spur > 0) {
			d = spur * spur;
			h = d - cof;
			g = (spur * cof - det) / 2.0 - spur * h;
			if (h > 0) {
				sqrth = Math.sqrt(h);
				d = h * h * h - g * g;
				if (d < 0.0) {
					d = 0.0;
				}
				d = Math.atan2(Math.sqrt(d), -g) / 3.0;
				cth = sqrth * Math.cos(d);
				sth = sqrth * sqrt3 * Math.sin(d);
				e[1] = (spur + cth) + cth;
				e[2] = (spur - cth) + sth;
				e[3] = (spur - cth) - sth;
				if (mode != 0) {// compute a
					for (l = 1; l <= 3; l = l + 2) {
						d = e[l];
						ss[1] = (d - rr[3]) * (d - rr[6]) - rr[5] * rr[5];
						ss[2] = (d - rr[6]) * rr[2] + rr[4] * rr[5];
						ss[3] = (d - rr[1]) * (d - rr[6]) - rr[4] * rr[4];
						ss[4] = (d - rr[3]) * rr[4] + rr[2] * rr[5];
						ss[5] = (d - rr[1]) * rr[5] + rr[2] * rr[4];
						ss[6] = (d - rr[1]) * (d - rr[3]) - rr[2] * rr[2];

						if (Math.abs(ss[0]) <= epsilon)
							ss[0] = 0.0;
						if (Math.abs(ss[1]) <= epsilon)
							ss[1] = 0.0;
						if (Math.abs(ss[2]) <= epsilon)
							ss[2] = 0.0;
						if (Math.abs(ss[3]) <= epsilon)
							ss[3] = 0.0;
						if (Math.abs(ss[4]) <= epsilon)
							ss[4] = 0.0;
						if (Math.abs(ss[5]) <= epsilon)
							ss[5] = 0.0;

						if (Math.abs(ss[1]) >= Math.abs(ss[3])) {
							j = 1;
							if (Math.abs(ss[1]) < Math.abs(ss[6])) {
								j = 3;
							}
						} else if (Math.abs(ss[3]) >= Math.abs(ss[6])) {
							j = 2;
						} else {
							j = 3;
						}

						d = 0.0;
						j = 3 * (j - 1);
						for (i = 1; i <= 3; i++) {
							k = ip[i + j];
							a[i][l] = ss[k];
							d = d + ss[k] * ss[k];
						}
						// if( d > 0.0 ) d = 1.0 / sqrt(d);
						if (d > 0)
							d = 1.0 / Math.sqrt(d);
						else
							d = 0.0;
						for (i = 1; i <= 3; i++) {
							a[i][l] = a[i][l] * d;
						}
					} // for l

					d = a[1][1] * a[1][3] + a[2][1] * a[2][3] + a[3][1] * a[3][3];

					if ((e[1] - e[2]) > (e[2] - e[3])) {
						m1 = 3;
						m = 1;
					} else {
						m1 = 1;
						m = 3;
					}
					p = 0;
					for (i = 1; i <= 3; i++) {
						a[i][m1] = a[i][m1] - d * a[i][m];
						p = p + a[i][m1] * a[i][m1];
					}

					if (p <= tol) {
						p = 1.0;
						KKK: for (i = 1; i <= 3; i++) {
							if (p < Math.abs(a[i][m])) {
								continue KKK;
							}
							p = Math.abs(a[i][m]);
							j = i;
						}
						k = ip2312[j];
						l = ip2312[j + 1];
						p = Math.sqrt(a[k][m] * a[k][m] + a[l][m] * a[l][m]);
						if (p > tol) {
							a[j][m1] = 0.0;
							a[k][m1] = -a[l][m] / p;
							a[l][m1] = a[k][m] / p;
						} else {// goto 40
							a_failed = 1;
						}
					} // if p<=tol
					else {
						p = 1.0 / Math.sqrt(p);
						for (i = 1; i <= 3; i++) {
							a[i][m1] = a[i][m1] * p;
						}
					} // else p<=tol
					if (a_failed != 1) {
						a[1][2] = a[2][3] * a[3][1] - a[2][1] * a[3][3];
						a[2][2] = a[3][3] * a[1][1] - a[3][1] * a[1][3];
						a[3][2] = a[1][3] * a[2][1] - a[1][1] * a[2][3];
					}
				} // if(mode!=0)
			} // h>0

			// compute b anyway
			if (mode != 0 && a_failed != 1) {// a is computed correctly
				// compute b
				for (l = 1; l <= 2; l++) {
					d = 0.0;
					for (i = 1; i <= 3; i++) {
						b[i][l] = r[i][1] * a[1][l] + r[i][2] * a[2][l] + r[i][3] * a[3][l];
						d = d + b[i][l] * b[i][l];
					}
					// if( d > 0 ) d = 1.0 / sqrt(d);
					if (d > 0)
						d = 1.0 / Math.sqrt(d);
					else
						d = 0.0;
					for (i = 1; i <= 3; i++) {
						b[i][l] = b[i][l] * d;
					}
				}
				d = b[1][1] * b[1][2] + b[2][1] * b[2][2] + b[3][1] * b[3][2];
				p = 0.0;

				for (i = 1; i <= 3; i++) {
					b[i][2] = b[i][2] - d * b[i][1];
					p = p + b[i][2] * b[i][2];
				}

				if (p <= tol) {
					p = 1.0;
					Line22: for (i = 1; i <= 3; i++) {
						if (p < Math.abs(b[i][1])) {
							continue Line22;
						}
						p = Math.abs(b[i][1]);
						j = i;
					}
					k = ip2312[j];
					l = ip2312[j + 1];
					p = Math.sqrt(b[k][1] * b[k][1] + b[l][1] * b[l][1]);
					if (p > tol) {
						b[j][2] = 0.0;
						b[k][2] = -b[l][1] / p;
						b[l][2] = b[k][1] / p;
					} else {
						// goto 40
						b_failed = 1;
					}
				} // if( p <= tol )
				else {
					p = 1.0 / Math.sqrt(p);
					for (i = 1; i <= 3; i++) {
						b[i][2] = b[i][2] * p;
					}
				}
				if (b_failed != 1) {
					b[1][3] = b[2][1] * b[3][2] - b[2][2] * b[3][1];
					b[2][3] = b[3][1] * b[1][2] - b[3][2] * b[1][1];
					b[3][3] = b[1][1] * b[2][2] - b[1][2] * b[2][1];
					// compute u
					for (i = 1; i <= 3; i++) {
						for (j = 1; j <= 3; j++) {
							u[i][j] = b[i][1] * a[j][1] + b[i][2] * a[j][2] + b[i][3] * a[j][3];
						}
					}
				}

				// compute t
				for (i = 1; i <= 3; i++) {
					t[i] = ((yc[i] - u[i][1] * xc[1]) - u[i][2] * xc[2]) - u[i][3] * xc[3];
				}
			} // if(mode!=0 && a_failed!=1)
		} // spur>0
		else // just compute t and errors
		{
			// compute t
			for (i = 1; i <= 3; i++) {
				t[i] = ((yc[i] - u[i][1] * xc[1]) - u[i][2] * xc[2]) - u[i][3] * xc[3];
			}
		} // else spur>0

		// compute rms
		for (i = 1; i <= 3; i++) {
			if (e[i] < 0)
				e[i] = 0;
			e[i] = Math.sqrt(e[i]);
		}
		ier = 0;
		if (e[2] <= (e[1] * 1.0e-05))
			ier = -1;
		d = e[3];
		if (sigma < 0.0) {
			d = -d;
			if ((e[2] - e[3]) <= (e[1] * 1.0e-05))
				ier = -1;
		}
		d = (d + e[2]) + e[1];
		rms = (e0 - d) - d;
		if (rms < 0.00000000001)
			rms = 0.0;
		return rms;
	}
	
	public static double[][] rotatedCoordinate(double alphaAngle, double betaAngle, double gammaAngle)throws Exception{
		double[][] xrot = rotatedXCoordinate(alphaAngle);
		double[][] yrot = rotatedYCoordinate(betaAngle);
		double[][] zrot = rotatedZCoordinate(gammaAngle);
		
		return _Matrix.a_mul_b(_Matrix.a_mul_b(zrot, yrot), xrot);
	}
	
	/****************************************************************
	 * For some applications, it is helpful to be able to make a rotation with a given axis. 
	 * Given a unit vector u = (ux, uy, uz), where ux*ux + uy*uy + uz*uz = 1, the matrix for a rotation
	 * by an angle of ¦È about an axis in the direction of u is following:
	 * 		R = [[cos¦È+ux*ux*(1-cos¦È)		ux*uy*(1-cos¦È)-uz*sin¦È		ux*uz*(1-cos¦È)+uy*sin¦È]
	 * 			 [uy*ux*(1-cos¦È)+uz*sin¦È	cos¦È+uy*uy*(1-cos¦È)			uy*uz*(1-cos¦È)-ux*sin¦È]
	 * 			 [uz*ux*(1-cos¦È)-uy*sin¦È	uz*uy*(1-cos¦È)+ux*sin¦È		cos¦È+uz*uz*(1-cos¦È)	  ]]
	 * 
	 * 
	 * The detail can be found in https://en.wikipedia.org/wiki/Rotation_matrix
	 * @param axis : the O(0, 0, 0) to axis(awx, awy, awz) means the axis or u
	 * @param angle : the rotated angle , ¦È
	 * @return the rotation matrix
	 ***************************************************************/
	public static double[][] rotatedCoordinate(Point3D axis, double ang){
		double awx = axis.getX();
		double awy = axis.getY();
		double awz = axis.getZ();
		
		double axisLen = axis.distOu(new Point3D(0.0, 0.0, 0.0));
		awx /= axisLen;
		awy /= axisLen;
		awz /= axisLen;
				
		double acos=Math.cos(ang);
		double asin=Math.sin(ang);
		double a11=awx*awx*(1-acos)+acos;
		double a12=awx*awy*(1-acos)-awz*asin;
		double a13=awx*awz*(1-acos)+awy*asin;
		double a21=awx*awy*(1-acos)+awz*asin;
		double a22=awy*awy*(1-acos)+acos;
		double a23=awy*awz*(1-acos)-awx*asin;
		double a31=awx*awz*(1-acos)-awy*asin;
		double a32=awy*awz*(1-acos)+awx*asin;
		double a33=awz*awz*(1-acos)+acos;
		
		double[][] rotmtx = new double[3][3];
		rotmtx[0][0] = a11;
		rotmtx[0][1] = a12;
		rotmtx[0][2] = a13;
		
		rotmtx[1][0] = a21;
		rotmtx[1][1] = a22;
		rotmtx[1][2] = a23;
		
		rotmtx[2][0] = a31;
		rotmtx[2][1] = a32;
		rotmtx[2][2] = a33;
		
		return rotmtx;
	}
	
	/****************************************************************
	 * 
	 * 	            xt(i)=ax+(x2n(i)-ax)*a11+(y2n(i)-ay)*a12+(z2n(i)-az)*a13
	 *	            yt(i)=ay+(x2n(i)-ax)*a21+(y2n(i)-ay)*a22+(z2n(i)-az)*a23
	 *	            zt(i)=az+(x2n(i)-ax)*a31+(y2n(i)-ay)*a32+(z2n(i)-az)*a33
	 * 
	 * @param points : the original points
	 * @param rotCenter : the center of the rotation
	 * @param rot	: the standard rotation matrix
	 * @return the rotatted points
	 ***************************************************************/
	public static Vector<Point3D> rotate(final Vector<Point3D> points, Point3D rotCenter, double[][] rot){
		double ax = rotCenter.getX();
		double ay = rotCenter.getY();
		double az = rotCenter.getZ();
		
		Vector<Point3D> ans = new Vector<Point3D>();
		for (int i = 0; i < points.size(); i++){
			double x = points.get(i).getX();
			double y = points.get(i).getY();
			double z = points.get(i).getZ();
			
			double xt = ax + rot[0][0]*(x-ax) + rot[0][1]*(y-ay) + rot[0][2]*(z-az);
			double yt = ay + rot[1][0]*(x-ax) + rot[1][1]*(y-ay) + rot[1][2]*(z-az);
			double zt = az + rot[2][0]*(x-ax) + rot[2][1]*(y-ay) + rot[2][2]*(z-az);
			
			ans.add(new Point3D(xt, yt, zt));
		}
		
		return ans;
	}
	
	/****************************************************************
	 * 
	 * 	            xt(i)=ax+(x(i)-ax)*a11+(y(i)-ay)*a12+(z(i)-az)*a13
	 *	            yt(i)=ay+(x(i)-ax)*a21+(y(i)-ay)*a22+(z(i)-az)*a23
	 *	            zt(i)=az+(x(i)-ax)*a31+(y(i)-ay)*a32+(z(i)-az)*a33
	 * 
	 * @param point : the original point
	 * @param rotCenter : the center of the rotation
	 * @param rot	: the standard rotation matrix
	 * @return the rotatted points
	 ***************************************************************/
	public static Point3D rotate(final Point3D point, Point3D rotCenter, double[][] rot){
		double ax = rotCenter.getX();
		double ay = rotCenter.getY();
		double az = rotCenter.getZ();
		
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();
			
		double xt = ax + rot[0][0]*(x-ax) + rot[0][1]*(y-ay) + rot[0][2]*(z-az);
		double yt = ay + rot[1][0]*(x-ax) + rot[1][1]*(y-ay) + rot[1][2]*(z-az);
		double zt = az + rot[2][0]*(x-ax) + rot[2][1]*(y-ay) + rot[2][2]*(z-az);
		
		return new Point3D(xt, yt, zt);
	}
	
	/**
	 * @param angle : the rotated 
	 * @return [1			0			0
	 * 			0		cos(angle)	-sin(angle)
	 * 			0		sin(angle)	cos(angle)]
	 */
	public static double[][] rotatedXCoordinate(double angle){
		double[][] xrot = new double[3][3];
		xrot[0][0] = 1;  xrot[0][1] = 0; 				xrot[0][2] = 0;
		xrot[1][0] = 0;  xrot[1][1] = Math.cos(angle); 	xrot[1][2] = -Math.sin(angle);
		xrot[2][0] = 0;  xrot[2][1] = Math.sin(angle); 	xrot[2][2] = Math.cos(angle);
		
		return xrot;
	}
	
	/**
	 * @param angle : the rotated 
	 * @return [cos(angle)			0			sin(angle)
	 * 			0					1			0
	 * 			-sin(angle)			0			cos(angle)]
	 */
	public static double[][] rotatedYCoordinate(double angle){
		double[][] xrot = new double[3][3];
		xrot[0][0] = Math.cos(angle);  	xrot[0][1] = 0; xrot[0][2] = Math.sin(angle);
		xrot[1][0] = 0;  				xrot[1][1] = 1; xrot[1][2] =0;
		xrot[2][0] = -Math.sin(angle);  xrot[2][1] = 0; xrot[2][2] = Math.cos(angle);
		
		return xrot;
	}
	
	/**
	 * @param angle : the rotated 
	 * @return [cos(angle)			-sin(angle)			0
	 * 			sin(angle)			cos(angle)			0
	 * 			0					0					1]
	 */
	public static double[][] rotatedZCoordinate(double angle){
		double[][] xrot = new double[3][3];
		xrot[0][0] = Math.cos(angle);  	xrot[0][1] = -Math.sin(angle); 	xrot[0][2] = 0;
		xrot[1][0] = Math.sin(angle);  	xrot[1][1] = Math.cos(angle); 	xrot[1][2] = 0;
		xrot[2][0] = 0;				  	xrot[2][1] = 0; 				xrot[2][2] = 1;
		
		return xrot;
	}
	
	public static void main(String[] args){
		double[][] a = {{1, 3}, {3, 9}};
		double[][] ans = matrixCatPerRow(a, a);
		System.out.println(ans[0][0] + " " + ans[0][1]+ " " + ans[0][2]+ " " + ans[0][3]);
	}
}
