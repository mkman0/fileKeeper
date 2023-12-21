package org.example;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Arrays;

public class Keeper {


    public Mode getMode() {
        return mode;
    }

    public Sort getSort() {
        return sort;
    }

    enum Sort{
        BY_NAME,
        BY_MODIFICATION_DATE,
        BY_SIZE,
        BY_CREATION_DATE
    }
    enum Mode{
        KEEP_FIRST,
        KEEP_LAST
    }
    private String path;
    private ArrayList<File> files;
    private int keptFiles;
    private Mode mode = Mode.KEEP_LAST;
    private Sort sort = Sort.BY_CREATION_DATE;

    Keeper(String path, int keptFiles){
        this.path = path;
        this.keptFiles = keptFiles;

        File folder = new File(path);
        if(!folder.exists() || !folder.isDirectory()){
            throw new InvalidPathException(path, "Path is not a directory");
        }

        files = new ArrayList<File>();
        files.addAll(Arrays.asList(folder.listFiles()));
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public int getKeptFiles() {
        return keptFiles;
    }

    public void setKeptFiles(int keptFiles) {
        this.keptFiles = keptFiles;
    }

    public void keep(){
        if(files.size() <= keptFiles){
            return;
        }
        switch(mode){
            case KEEP_FIRST:
                switch(sort){
                    case BY_NAME:
                        files.sort((a, b) -> a.getName().compareTo(b.getName()));
                        break;
                    case BY_MODIFICATION_DATE:
                        files.sort((a, b) -> Long.compare(a.lastModified(), b.lastModified()));
                        break;
                    case BY_SIZE:
                        files.sort((a, b) -> Long.compare(a.length(), b.length()));
                        break;
                    case BY_CREATION_DATE:
                        files.sort((a, b) -> Long.compare(a.lastModified(), b.lastModified()));
                        break;
                }
                break;
            case KEEP_LAST:
                switch(sort){
                    case BY_NAME:
                        files.sort((a, b) -> b.getName().compareTo(a.getName()));
                        break;
                    case BY_MODIFICATION_DATE:
                        files.sort((a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                        break;
                    case BY_SIZE:
                        files.sort((a, b) -> Long.compare(b.length(), a.length()));
                        break;
                    case BY_CREATION_DATE:
                        files.sort((a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                        break;
                }
                break;
        }
        for(int i = keptFiles; i < files.size(); i++){
                System.out.println(files.get(i).getName() + " " + files.get(i).lastModified());
                files.get(i).delete();
        }
    }

}
