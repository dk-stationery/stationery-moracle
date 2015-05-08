package org.tommy.stationery.moracle.core.enums;

/**
 * Created by kun7788 on 15. 5. 8..
 */
public enum ConfigEnum {
    inputPath("inputPath"),
    seperator("seperator"),
    fileExtension("fileExtension"),
    fileEncoding("fileEncoding"),
    tmpPath("tmpPath"),
    isHeader("isHeader"),
    ;

    private String messgae;

    public String getMessage() {
        return messgae;
    }

    private ConfigEnum(String messgae) {
        this.messgae = messgae;
    }
}
