package ch.epfl.balelecbud.model;

public final class Picture {

    private String imageFileName;

    public Picture(String name) {
        this.imageFileName = name;
    }

    public String getImageFileName() { return imageFileName; }
}