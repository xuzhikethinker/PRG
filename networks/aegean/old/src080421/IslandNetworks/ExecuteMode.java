/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

    public class ExecuteMode{
        public final static String [] exModes = {"Unknown","Ariadne","MultipleAnalysis","Analysis","NetworkGenerator"};
        final static int testLength=3;
        int modeNumber =1;
        
        public ExecuteMode(){
            
        }
        
        /**
         * Deep Copy.
         * @param old ExecuteMode to be deep copied.
         */
        public ExecuteMode(ExecuteMode old)
        {
            this.modeNumber = old.modeNumber;
        }
        
        /**
         * Sets mode number associated with input string.
         * @param s input string with mode name
         * @return mode number associated with string, 0 if unknown.
         */
        public int setMode(String s){
            return modeNumber = findMode(s);
        }
        
        /**
         * Finds mode number associated with input string.
         * @param s input string with mode name
         * @return mode number associated with string, 0 if unknown.
         */
        public int findMode(String s){
            for (int m=0; m<exModes.length; m++)
               if (exModes[m].substring(0,testLength).equalsIgnoreCase(s.substring(0,testLength))) return m;
            return 0;
        }
        
        public boolean isMode(String s){
            if (findMode(s) == modeNumber ) return true; else return false;
        }
        
        @Override
        public String toString(){return exModes[modeNumber];}

        /**
         * Gives a string with all the options.
         * @param sep separation string
         * @return string with all options listed
         */
        public String allOptionsString(String sep){
            String s=exModes[1];
            for (int m=2;m<exModes.length;m++) s= s+sep+exModes[m];
            return s;
        }

    }
