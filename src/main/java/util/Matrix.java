package util;

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

    public static float[][] getRotationMatrix(float rotX, float rotY, float rotZ) {
        float radX = (float) Math.toRadians(rotX);
        float radY = (float) Math.toRadians(rotY);
        float radZ = (float) Math.toRadians(rotZ);

        float cosX = (float) Math.cos(radX), sinX = (float) Math.sin(radX);
        float cosY = (float) Math.cos(radY), sinY = (float) Math.sin(radY);
        float cosZ = (float) Math.cos(radZ), sinZ = (float) Math.sin(radZ);

//        float[][] R = {
//                {cosY*cosZ, cosX*sinZ + sinX*sinY*cosZ, sinX*sinZ - cosX*sinY*cosZ, 0},
//                {-cosY*sinZ, cosX*cosZ - sinX*sinY*sinZ, sinX*cosZ + cosX*sinY*sinZ, 0},
//                {sinY, -sinX*cosY, cosX*cosY, 0},
//                {0,0,0,1}
//        };
        // transposed
        float[][] R = {
                {cosY*cosZ, -cosY*sinZ, sinY, 0},
                {cosX*sinZ + sinX*sinY*cosZ, cosX*cosZ - sinX*sinY*sinZ, -sinX*cosY, 0},
                {sinX*sinZ - cosX*sinY*cosZ, sinX*cosZ + cosX*sinY*sinZ, cosX*cosY, 0},
                {0, 0, 0, 1}
        };

        return R;
    }
}
