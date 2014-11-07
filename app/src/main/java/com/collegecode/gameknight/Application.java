package com.collegecode.gameknight;

import com.parse.Parse;

/**
 * Created by saurabh on 14-11-06.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        Parse.initialize(this, Secrets.parseAppKey, Secrets.parseSecretKey);
        super.onCreate();
    }
}
