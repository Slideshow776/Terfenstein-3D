package no.sandramoen.terfenstein3D.utils;

import com.badlogic.gdx.files.FileHandle;

public class Text {

    private String string;

    public Text() {

        this.string = new String("".getBytes());

    }

    public Text(byte[] data) {

        this.string = new String(data);

    }

    public Text(String string) {

        this.string = new String(string.getBytes());

    }

    public Text(FileHandle file) {

        this.string = new String(file.readBytes());

    }

    public Text(Text text) {

        this.string = new String(text.getString().getBytes());

    }

    public void setString(String string) {

        this.string = string;

    }

    public String getString() {

        return this.string;

    }

    public void clear() {

        this.string = new String("".getBytes());

    }

}