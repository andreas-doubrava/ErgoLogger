package org.doubrava.ergologger.bl;

import java.util.ArrayList;

public class FileExportEngines {

    private volatile static FileExportEngines uniqueFileExportEngines;

    private ArrayList<FileExportEngine> fileExportEngines;

    public static synchronized FileExportEngines getInstance() {
        if (uniqueFileExportEngines == null) {
            synchronized (FileExportEngines.class) {
                if (uniqueFileExportEngines == null) {
                    uniqueFileExportEngines = new FileExportEngines();
                }
            }
        }
        return uniqueFileExportEngines;
    }

    private FileExportEngines() {
        this.fileExportEngines = new ArrayList<FileExportEngine>();
        this.fileExportEngines.add(new ExportEngine_TCX());
        this.fileExportEngines.add(new ExportEngine_TXT());
    }

    public ArrayList<FileExportEngine> getEngines() { return this.fileExportEngines; }

    public FileExportEngine getEngine(String fileExtension) {
        if (fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(1);
        }
        for (FileExportEngine engine : this.fileExportEngines) {
            if (engine.getFileFormatExtension().toLowerCase().equals(fileExtension.toLowerCase())) {
                return engine;
            }
        }
        return null;
    }

}
