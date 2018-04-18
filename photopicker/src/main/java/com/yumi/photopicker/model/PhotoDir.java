package com.yumi.photopicker.model;

import java.util.List;

import java.io.File;
import java.util.List;

/**
 * Created by yumi on 2016/9/15.
 */
public class PhotoDir {
    /**
     *  文件家目录，不包括最后的"/"
     *  格式：xxx/xxx/.../xxx
     */
    public String dirPath;
    public List<String> photoPaths;

    public PhotoDir() {
    }

    public PhotoDir(String dirPath, List<String> photoPaths) {
        this.dirPath = dirPath;
        this.photoPaths = photoPaths;
    }

    public String getDirName() {
        return dirPath.substring(dirPath.lastIndexOf(File.separator) < 0 ? 0 : dirPath.lastIndexOf(File.separator) + 1, dirPath.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        PhotoDir other = (PhotoDir) o;
        if (null == dirPath) {
            if (other.dirPath != null) {
                return false;
            }
        } else if (!dirPath.equals(other.dirPath))
            return false;
        return true;
    }
}
