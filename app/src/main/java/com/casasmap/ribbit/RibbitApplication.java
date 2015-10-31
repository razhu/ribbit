package com.casasmap.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by ramiro on 10/30/15.
 */
public class RibbitApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "1eqVwPjQAXANdM7qhdMXhRuKl1USNsc6mWRxPwEd", "mxPe7wphSjncVu17y9UV5ROU2KvJPM9Gp0n54eyf");
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foorrr", "barrrr");
        testObject.saveInBackground();
        //end Parse
    }
}
