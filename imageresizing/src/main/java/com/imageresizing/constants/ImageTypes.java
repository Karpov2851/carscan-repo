package com.imageresizing.constants;

/**
 * Created by vikas on 26-11-2020.
 */
public enum ImageTypes {
    JPG("jpg"),JPEG_MIME("image/jpeg"),PNG("png"),PNG_MIME("image/png");

    private String mimeTypeDesc;
    ImageTypes(String mimeTypeDesc){
        this.mimeTypeDesc = mimeTypeDesc;
    }

    public String getMimeTypeDesc() {
        return mimeTypeDesc;
    }
}
