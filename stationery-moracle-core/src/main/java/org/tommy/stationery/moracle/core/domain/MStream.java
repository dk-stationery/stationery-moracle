package org.tommy.stationery.moracle.core.domain;

/**
 * Created by kun7788 on 15. 4. 28..
 */
public class MStream extends DHTHelper {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getKeyName() {
        return super.generateKeyName(name);
    }
}
