package org.doubrava.ergologger.bl;

import java.io.File;

public class ExportEngine_TCX implements FileExportEngine {

    private static final String FILEFORMAT_EXTENSION = "tcx";
    private static final String FILEFORMAT_NAME = "Training Center XML";

    @Override
    public String getFileFormatExtension() {
        return ExportEngine_TCX.FILEFORMAT_EXTENSION;
    }

    @Override
    public String getFileFormatName() {
        return ExportEngine_TCX.FILEFORMAT_NAME;
    }


    @Override
    public int export(DataAdapter dataAdapter, DataSet dataSet, File file) {
        System.out.println("Not implemented yet...");
        return 0;
    }
}
