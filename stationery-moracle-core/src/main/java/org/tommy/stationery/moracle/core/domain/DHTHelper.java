package org.tommy.stationery.moracle.core.domain;

/**
 * Created by kun7788 on 15. 4. 28..
 */
abstract class DHTHelper implements DHTMaker {
    private static String GLOBAL_KEY = "moracle";

    public String generateKeyName(String key) {
        return GLOBAL_KEY + "_" + key;
    }
}
