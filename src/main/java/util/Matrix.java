package util;

import model.Point3D;

/*
    Util class for Matrix arithmetics
 */
public class Matrix {
    public static float[][] multiplyMatrices(float[][] firstMatrix, float[][] secondMatrix) {
        float[][] result = new float[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }
        return result;
    }

    public static float multiplyMatricesCell(float[][] firstMatrix, float[][] secondMatrix, int row, int col) {
        float cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    public static float[] multiplyVectorByMatrix(float[] vector, float[][] matrix) {
        if (vector.length != matrix[0].length) {
            return null;
        }
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++){
            float cell = 0;
            for (int j = 0; j < matrix[0].length; j++){
                cell += vector[j]*matrix[j][i];
            }
            result[i] = cell;
        }
        return result;
    }

    public static float[] multiplyMatrixByVector(float[][] matrix, float[] vector) {
        if (vector.length != matrix[0].length) {
            return null;
        }
        float[] result = new float[vector.length];
        for (int i = 0; i < vector.length; i++){
            float cell = 0;
            for (int j = 0; j < matrix[0].length; j++){
                cell += vector[j]*matrix[i][j];
            }
            result[i] = cell;
        }
        return result;
    }

    public static float multiplyVectorByVector(float[] vector1, float[] vector2) {
        float result = 0;
        for (int i = 0; i < vector1.length; i++){
            result += vector1[i] * vector2[i];
        }
        return result;
    }
}
