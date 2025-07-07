package net.redsierra.Spacio.util;

import net.redsierra.Spacio.Spacio;
import java.io.InputStream;

public class Resources {

    public InputStream getResourceFile(String name) {
        InputStream inputStream = Spacio.class.getResourceAsStream("/" + name);

        assert inputStream != null;


        return inputStream;
    }

}
