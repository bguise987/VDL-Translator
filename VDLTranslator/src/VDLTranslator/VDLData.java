/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package VDLTranslator;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Calendar;
//import java.util.GregorianCalendar;

/**
 *
 * @author ben
 */

/* This is the base class for output data from the HERL Vibration Data Logger.
 * Data will be read in from the file generically to here and reassembled using
 * bitwise operations. Another class can inherit from this class to provide
 * specific methods for processing data applicable to that study.
 */
abstract class VDLData extends Thread {
        protected int[] beginTimeStamp;// = new int[7];
        protected int[] endTimeStamp;// = new int[7];
        
//        protected Calendar beginTime, endTime;
        
        protected ArrayList<int[]> initialStorage;
        protected boolean conversionStatus = false;   //This must be set to true after procesing complete
        
        ArrayList<Double> colOne = new ArrayList(4000);
        ArrayList<Double> colTwo = new ArrayList(4000);
        ArrayList<Double> colThree = new ArrayList(4000);
        ArrayList<Double> colFour = new ArrayList(4000);
        ArrayList<Double> colFive = new ArrayList(4000);
        ArrayList<Double> colSix = new ArrayList(4000);
        
        ArrayList<Double> refVoltage = new ArrayList(4000);
        
        public VDLData(int[] beginStamp, int[] endStamp, ArrayList<int[]> inputData) {  //Constructor
//            beginTime = new GregorianCalendar(beginStamp[6],beginStamp[5],beginStamp[4],beginStamp[3],beginStamp[2],beginStamp[1]);
//            beginTime.set(Calendar.MILLISECOND, beginStamp[0]);
//            endTime = new GregorianCalendar(endStamp[6],endStamp[5],endStamp[4],endStamp[3],endStamp[2],endStamp[1]);
//            endTime.set(Calendar.MILLISECOND, endStamp[0]);
            beginTimeStamp = beginStamp;
            endTimeStamp = endStamp;
            initialStorage = new ArrayList(inputData);
        }

        
        public void dataAssemble() {
            Iterator<int[]> rawDataItr = initialStorage.listIterator();
            int[] tempByteArray;
            while (rawDataItr.hasNext()) {


                tempByteArray = rawDataItr.next();   //tempByteArray will have 16 values

                
                //RefVoltage can be calculated at this time, so we'll do that here
                double refVolt = ((tempByteArray[12] << 8) | tempByteArray[13]) * (5.0 / 4095.0);
                refVoltage.add(refVolt);
                refVolt /= 4095.0;
                
                // Go through each column & bitwise operate to reassemble
                colOne.add(((tempByteArray[0] << 8) | tempByteArray[1]) * refVolt);
                colTwo.add(((tempByteArray[2] << 8) | tempByteArray[3]) * refVolt);
                colThree.add(((tempByteArray[4] << 8) | tempByteArray[5]) * refVolt);
                colFour.add(((tempByteArray[6] << 8) | tempByteArray[7]) * refVolt);
                colFive.add(((tempByteArray[8] << 8) | tempByteArray[9]) * refVolt);
                colSix.add(((tempByteArray[10] << 8) | tempByteArray[11]) * refVolt);
            }
        }
        
        public boolean getConversionStatus() { //Returns true if conversion is complete
            return conversionStatus;
        }
        
        protected void setConversionStatus(boolean status) {
            conversionStatus = status;
        }
        
        // This will need to be called to start conversion on this object, method calls must be added in subclasses
        public abstract void run();

}
