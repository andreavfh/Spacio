package net.redsierra.Spacio.config;

public enum ChannelKey {
    LOGS("logschannelid"),
    WELCOME("welcomechannelid"),
    COMMANDS("commandschannelid"),
    REPORTS("reportschannelid"),
    XP("xpchannels");


    private final String key;

    ChannelKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}