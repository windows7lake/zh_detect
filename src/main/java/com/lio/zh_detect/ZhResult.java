package com.lio.zh_detect;

public class ZhResult {
    private final String path;

    private final String line;

    private final String zhSimplified;

    private final String zhFan;

    public ZhResult(String path, String line, String zhSimplified, String zhFan) {
        this.path = path;
        this.line = line;
        this.zhSimplified = zhSimplified;
        this.zhFan = zhFan;
    }

    public String getPath() {
        return path;
    }

    public String getLine() {
        return line;
    }

    public String getZhSimplified() {
        return zhSimplified;
    }

    public String getZhFan() {
        return zhFan;
    }
}
