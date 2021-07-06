package com.example.myway;

public class PublicHolidays {

    String[] twentyTwentyOne;
    String[] twentyTwentyTwo;

    PublicHolidays() {
        this.twentyTwentyOne = new String[] {"01012021","12022021","13022021","02042021","01052021","13052021","26052021","20072021","09082021","04112021","25122021"};
        this.twentyTwentyTwo = new String[] {"01012022","01022022","02022022","15042022","01052022","02052022","15052022","09072022","09082022","24102022","25122022"};
    }

    public boolean isItPH(String date) {
        for(int i=0; i< twentyTwentyOne.length; i++) {
            if(date.equals(twentyTwentyOne[i])) {
                return true;
            }
        }
        for(int i=0; i< twentyTwentyTwo.length; i++) {
            if(date.equals(twentyTwentyTwo[i])) {
                return true;
            }
        }
        return false;
    }
}


