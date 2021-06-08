// IHideAdsService.aidl
package com.zev.noads;

// Declare any non-default types here with import statements

interface IHideAdsService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    oneway void startService();

    void stopService();
}
