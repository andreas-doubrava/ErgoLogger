package org.doubrava.ergologger.bl;

import java.util.ArrayList;

public class ExportEngines {

    private volatile static ExportEngines uniqueExportEngines;

    private ArrayList<ExportEngine> exportEngines;

    public static synchronized ExportEngines getInstance() {
        if (uniqueExportEngines == null) {
            synchronized (ExportEngines.class) {
                if (uniqueExportEngines == null) {
                    uniqueExportEngines = new ExportEngines();
                }
            }
        }
        return uniqueExportEngines;
    }

    private ExportEngines() {
        this.exportEngines = new ArrayList<ExportEngine>();
        this.exportEngines.add(new ExportEngine_TCX());
        this.exportEngines.add(new ExportEngine_TXT());
    }

    public ArrayList<ExportEngine> getEngines() { return this.exportEngines; }

    public ExportEngine getEngine(String fileExtension) {
        if (fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(1);
        }
        for (ExportEngine engine : this.exportEngines) {
            if (engine.getFileFormatExtension().toLowerCase().equals(fileExtension.toLowerCase())) {
                return engine;
            }
        }
        return null;
    }

}
