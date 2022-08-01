package com.rss.suchi.model;

public class DataForManageBackPress {
    private int flag_id;
    private String flag_type;

    public int getFlag_id() {
        return flag_id;
    }

    public void setFlag_id(int flag_id) {
        this.flag_id = flag_id;
    }

    public String getFlag_type() {
        return flag_type;
    }

    public void setFlag_type(String flag_type) {
        this.flag_type = flag_type;
    }

    public DataForManageBackPress(int flag_id, String flag_type) {
        this.flag_id = flag_id;
        this.flag_type = flag_type;
    }
}
