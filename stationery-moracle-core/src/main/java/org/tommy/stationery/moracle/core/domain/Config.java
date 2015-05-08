package org.tommy.stationery.moracle.core.domain;

import org.tommy.stationery.moracle.core.enums.ConfigEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kun7788 on 15. 4. 30..
 */
public class Config {
    private Map<ConfigEnum, String> params;

    public Config(Map<ConfigEnum, String> params) {
        this.params = params;
    }

    public static Map<ConfigEnum, String> decode(String url) {
        String[] _params = url.split("&");
        Map<ConfigEnum, String> params = new HashMap<ConfigEnum, String>();
        for (String param : _params) {
            String[] p1 = param.split("=");
            String name = p1[0];
            String value = p1[1];
            params.put(ConfigEnum.valueOf(name), value);
        }
        return params;
    }

    public String getString(ConfigEnum key) {
        return params.get(key);
    }
}
