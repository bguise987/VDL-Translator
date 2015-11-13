/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package VDLTranslator;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Math;
import java.io.PrintWriter;
/**
 *
 * @author ben
 */
public class PathlockData extends VDLData {
    
    // colOne is Right Activation Level
    // colTwo is Left Activation Level
    // colThree is Acceleration (X axis)
    // colFour is Acceleration (Y axis)
    // colFive is Angular rate (X axis)
    // colSix is Angular rate (Y axis)
    // refVoltage is the reference voltage
    
    ArrayList actLevelLeft;
    ArrayList actLevelRight;
    ArrayList accelX;
    ArrayList accelY;
    ArrayList gyroX;
    ArrayList gyroY;
    ArrayList timeline = new ArrayList(4000);
    
    ArrayList pitchAngles, rollAngles;
    
    public PathlockData(int[] beginStamp, int[] endStamp, ArrayList<int[]> tempStorage) {  //Constructor
        super(beginStamp, endStamp, tempStorage);
    }
    
    private void generateTimeline() {
        double dt = 0.11714;
        double t = 0;
        timeline.add(t);
        for (int i = 1; i < colOne.size(); i++) {
            t += dt;
            timeline.add(t);
        }
    }
    
    //Methods to filter and manipulate data for Pathlock study
    
    public ArrayList compFilter(ArrayList accelData, ArrayList gyroData) {
        ArrayList results = new ArrayList(4000);
        double dt = 0.11714;
        results.add(accelData.get(0));
        for (int i = 1; i < accelData.size(); i++) {
            //results.add(0 * ((double)results.get(i - 1) + (double)gyroData.get(i) * dt) + 1.0 * (double)accelData.get(i));    //All Accel
            //results.add(1.0 * ((double)results.get(i - 1) + (double)gyroData.get(i) * dt) + 0 * (double)accelData.get(i));    //All Gyro
            
            //results.add(0.95 * ((double)results.get(i - 1) + (double)gyroData.get(i) * dt) + 0.05 * (double)accelData.get(i));
            results.add(0.85 * ((double)results.get(i - 1) + (double)gyroData.get(i) * dt) + 0.15 * (double)accelData.get(i));
        }
        return results;
    } 
    
    public ArrayList transToAngleFromOneAcc(ArrayList dataSet) {
        ArrayList results = new ArrayList(4000);
        Iterator dataSetItr = dataSet.listIterator();
        double temp;
        while (dataSetItr.hasNext()) {
            temp = (double)dataSetItr.next();
            temp = Math.asin(temp);
            temp = Math.toDegrees(temp);
            results.add(temp);
        }
        return results;
    }
    
    
    public ArrayList activationLevelFilter(ArrayList dataSet) {
        for (int i = 0; i < dataSet.size(); i++) {
            double currentValue = (double)dataSet.get(i);
            if (currentValue < 0.3)
                dataSet.set(i, 0);
            else if (0.3 < currentValue && currentValue < 0.8)
                dataSet.set(i, 1);
            else if (1.45 < currentValue && currentValue < 1.9)
                dataSet.set(i, 2);
            else if (2.48 < currentValue)
                dataSet.set(i, 3);
        }
        return dataSet;
    }

    
    // Translates data to acceleration based on equation in data sheet
    public void transToAcceleration() {
        Iterator accelXItr = accelX.listIterator();
        Iterator accelYItr = accelY.listIterator();
        
        Iterator refVoltItr = refVoltage.listIterator();
        ArrayList tempAccelX = new ArrayList(4000);
        ArrayList tempAccelY = new ArrayList(4000);
        
        while (refVoltItr.hasNext()) {
            double refVolt = (double)refVoltItr.next();
            tempAccelX.add(((double)accelXItr.next() - (refVolt / 2.0)) * 10.0/refVolt);
            tempAccelY.add(((double)accelYItr.next() - (refVolt / 2.0)) * 10.0/refVolt);
        }
        accelX = tempAccelX;
        accelY = tempAccelY;
    }
    
    // Translates data to angular rate based on equation in data sheet
    public void transToGyro() {
        Iterator gyroXItr = gyroX.listIterator();
        Iterator gyroYItr = gyroY.listIterator();
        //Iterator refVoltItr = refVoltage.listIterator();
        ArrayList tempGyroX = new ArrayList(4000);
        ArrayList tempGyroY = new ArrayList(4000);
        while (gyroXItr.hasNext()) {
            //double refVolt = (double)refVoltItr.next();
            // Divide by -0.002 so that integrated gyro values are same direction as accelerometer
//            tempGyroX.add((1.274 - (double)gyroXItr.next()) / (-0.002));
//            tempGyroY.add((1.274 - (double)gyroYItr.next()) / (-0.002));
              tempGyroX.add((1.23 - (double)gyroXItr.next()) / (-0.002));
              tempGyroY.add((1.23 - (double)gyroYItr.next()) / (-0.002));
        }
        gyroX = tempGyroX;
        gyroY = tempGyroY;
    }
    
    
    // Set VDLData ArrayList names to new ones for this file
    public void setDataNames() { 
//        actLevelLeft = colOne;
//        actLevelRight = colTwo;
        actLevelLeft = colTwo;
        actLevelRight = colOne;
        accelX = colThree;
        accelY = colFour;
        gyroX = colFive;
        gyroY = colSix;
    }
    

    
    
    public void dataDump(PrintWriter outputFile) {
        if (conversionStatus) {
            //Write beginStamp to output file    
            for (int i = 0; i < beginTimeStamp.length; i++) {
                if (i < 6) {
                    outputFile.print(beginTimeStamp[i] + ",");
                } else {
                    outputFile.println(beginTimeStamp[i] + ",Start");
                }
            }

            Iterator actLevelLeftItr = actLevelLeft.listIterator();
            Iterator actLevelRightItr = actLevelRight.listIterator();
            Iterator rollAnglesItr = rollAngles.listIterator();
            Iterator pitchAnglesItr = pitchAngles.listIterator();
            Iterator timelineItr = timeline.listIterator();

            //Iterate through data store and write all that to the file
            while (pitchAnglesItr.hasNext()) {
                outputFile.print(rollAnglesItr.next() + ",");
                outputFile.print(pitchAnglesItr.next() + ",");
                outputFile.print(actLevelLeftItr.next() + ",");
                outputFile.print(actLevelRightItr.next() + ",");
                
                outputFile.println(timelineItr.next());
            }
            //Write the end stamp
            for (int i = 0; i < endTimeStamp.length; i++) {
                if (i < 6) {
                    outputFile.print(endTimeStamp[i] + ",");
                } else {
                    outputFile.println(endTimeStamp[i] + ",Stop");
                }
            }
        }
    }
    
    public double getTotalTime() {
        double totalTime =  (double)timeline.get(timeline.size() - 1);
        return totalTime;
    }
    
    
    
    //This must call each of the post processing methods
    public void run() {
        super.dataAssemble();
        //At this point subsequent methods can be called to process assembled data
        setDataNames();
        transToAcceleration();
        transToGyro();
        
        actLevelLeft = activationLevelFilter(actLevelLeft);
        actLevelRight = activationLevelFilter(actLevelRight);
        accelX = transToAngleFromOneAcc(accelX);
        accelY = transToAngleFromOneAcc(accelY);
        
        rollAngles = compFilter(accelY, gyroY);
        pitchAngles = compFilter(accelX, gyroX);
        generateTimeline();
        
        
        //Used to test the timeline method
        if (timeline.size() != refVoltage.size()) {
            System.out.println("ERROR:  time data and refVoltage data not same size");
        }
        setConversionStatus(true);
    }
}
