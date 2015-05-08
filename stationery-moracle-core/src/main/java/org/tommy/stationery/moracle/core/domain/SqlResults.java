package org.tommy.stationery.moracle.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kun7788 on 15. 5. 8..
 */
public class SqlResults {
    private List<String> column = new ArrayList<String>();
    private List<List<String>> lists = new ArrayList<List<String>>();

    public List<List<String>> getListFromCursor() {
        return lists;
    }

    public void addRaw(List<String> row) {
        lists.add(row);
    }
}
