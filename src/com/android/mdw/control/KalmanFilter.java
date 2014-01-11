package com.android.mdw.control;

import com.android.mdw.model.Data;

public class KalmanFilter {
    // Gyro weight/smooting factor.

    private static final double W_GYRO = 10;
    // Marks first sample.
    boolean firstSample;
    // Notation "w" stands for one of the axes, so for example 
    // RwAccelerometer[0],RwAccelerometer[1],RwAccelerometer[2] means
    // RxAcc,RyAcc,RzAcc
    // Variables below must be global (their previous value is used in
    // getEstimatedInclination)
    //Rw estimated from combining RwAccelerometer and RwGyroscope
    double RwEstimated[] = new double[3];
    long lastMicros;

    public KalmanFilter() {
        setup();
    }

    private void setup() {

        firstSample = true;
    }

    public double[] filtrar(Data dato) {

        // Projection of normalized gravitation force vector on x/y/z axis, as
        // measured by accelerometer.
        double RwAccelerometer[] = {dato.getAccelerometerX(),
            dato.getAccelerometerY(), dato.getAccelerometerZ()};



        // Current time stamp.
        long newMicros = System.currentTimeMillis();

        //FIXME falta tener en cuenta el Z dato.getGiroZ()

        // Delta time.
        // Compute interval since last sampling time (in seconds).
        long deltaT = (newMicros - lastMicros) / 1000;
        // Save for next loop, please note interval will be invalid in first 
        // sample but we don't use it.
        lastMicros = newMicros;




        // Normalize vector (convert to a vector with same direction and with
        // length 1).
        normalize3DVector(RwAccelerometer);


        // The first time it only reads the accelerometer values.
        if (firstSample) {
            // Initialize with accelerometer readings
            for (int w = 0; w <= 2; w++) {
                RwEstimated[w] = RwAccelerometer[w];
            }

        } else {
            // Rw obtained from last estimated value and gyro movement
            double RwGyroscope[] = new double[3];

            // Evaluate RwGyroscope vector.
            if (Math.abs(RwEstimated[2]) < 0.1) {
                // Rz is too small and because it is used as reference for
                // computing Axz, Ayz it's error fluctuations will amplify
                // leading to bad results.
                // In this case skip the gyro data and just use previous 
                // estimate.

                // ESTIMATION with Gyroscope.
                for (int w = 0; w <= 2; w++) {
                    RwGyroscope[w] = RwEstimated[w];
                }


            } else {

                // Angles between projection of R on XZ/YZ plane and Z axis(deg)
                double proyectedAnglesOnXZYZ[] = new double[2];
                double[] gyroMedido = {dato.getGiroX(), dato.getGiroY(),
                    dato.getGiroZ()};
                //FIXME se puede usar los tres ejes como en el acelerometro

                //////////// UPDATE ///////
                // Calcular los ángulos con respecto al eje X
                // Get angles between projection of R on ZX/ZY plane and Z axis,
                // based on last RwEstimated.
                for (int w = 0; w <= 1; w++) {
                    // Get angle change in deg angle=w*t
                    double tmpf = gyroMedido[w] * deltaT;

                    // Get angle and convert to degrees.
                    proyectedAnglesOnXZYZ[w] = Math.atan2(RwEstimated[w],
                            RwEstimated[2]);
                    // Get updated angle according to gyro movement.
                    proyectedAnglesOnXZYZ[w] += tmpf;
                }

                // Reverse calculation of RwGyro from Awz angles, for formulas 
                // deductions see  http://starlino.com/imu_guide.html
                for (int w = 0; w <= 1; w++) {
                    RwGyroscope[0] = Math.sin(proyectedAnglesOnXZYZ[0]);
                    RwGyroscope[0] /= Math.sqrt(
                            1 + Math.pow(Math.cos(proyectedAnglesOnXZYZ[0]), 2)
                            * Math.pow(Math.tan(proyectedAnglesOnXZYZ[1]), 2));

                    RwGyroscope[1] = Math.sin(proyectedAnglesOnXZYZ[1]);
                    RwGyroscope[1] /= Math.sqrt(1
                            + Math.pow(Math.cos(proyectedAnglesOnXZYZ[1]), 2)
                            * Math.pow(Math.tan(proyectedAnglesOnXZYZ[0]), 2));
                }

                // Estimate sign of RzGyro by looking in what qudrant the angle
                // Axz is, RzGyro is positive if 
                // Axz in range -90 ..90 => cos(proyectedAnglesOnXZYZ) >= 0
                int signRzGyro = (Math.cos(proyectedAnglesOnXZYZ[0]) >= 0)
                        ? 1 : -1;

                // Reverse calculation of RwGyro from Awz angles, for formulas
                // deductions see  http://starlino.com/imu_guide.html
                for (int w = 0; w <= 1; w++) {
                    RwGyroscope[0] = Math.sin(proyectedAnglesOnXZYZ[0]);
                    RwGyroscope[0] /= Math.sqrt(1 + Math.pow(
                            Math.cos(proyectedAnglesOnXZYZ[0]), 2)
                            * Math.pow(Math.tan(proyectedAnglesOnXZYZ[1]), 2));
                    RwGyroscope[1] = Math.sin(proyectedAnglesOnXZYZ[1]);
                    RwGyroscope[1] /= Math.sqrt(1 + Math.pow(
                            Math.cos(proyectedAnglesOnXZYZ[1]), 2)
                            * Math.pow(Math.tan(proyectedAnglesOnXZYZ[0]), 2));
                }
                RwGyroscope[2] = signRzGyro * Math.sqrt(1 - Math.pow(
                        RwGyroscope[0], 2) - Math.pow(RwGyroscope[1], 2));
            }


            //////////// ESTIMATION////////////////////
            //combine Accelerometer and gyro readings
            for (int w = 0; w <= 2; w++) {
                // XXX falta sumarle la incertidumbre del medido
                RwEstimated[w] = (RwAccelerometer[w] + W_GYRO * RwGyroscope[w]) / (1 + W_GYRO);
            }

            normalize3DVector(RwEstimated);

        }

        firstSample = false;


        return RwEstimated;
    }

    public static void normalize3DVector(double[] RwAcc) {
        double R;
        R = Math.sqrt(RwAcc[0] * RwAcc[0] + RwAcc[1] * RwAcc[1] + RwAcc[2] *
                RwAcc[2]);
        RwAcc[0] /= R;
        RwAcc[1] /= R;
        RwAcc[2] /= R;
    }
}
