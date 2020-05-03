package org.doubrava.ergologger.bl;

import java.io.File;

public interface ExportEngine {
    public String getFileFormatExtension();
    public String getFileFormatName();
    public int export(DataAdapter dataAdapter, DataSet dataSet, File file);
}
