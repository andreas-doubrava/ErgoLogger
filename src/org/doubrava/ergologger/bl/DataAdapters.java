package org.doubrava.ergologger.bl;

import java.util.ArrayList;

public class DataAdapters {

    private volatile static DataAdapters uniqueDataAdapters;

    private ArrayList<DataAdapter> dataAdapters;

    public static synchronized DataAdapters getInstance() {
        if (uniqueDataAdapters == null) {
            synchronized (ExportEngines.class) {
                if (uniqueDataAdapters == null) {
                    uniqueDataAdapters = new DataAdapters();
                }
            }
        }
        return uniqueDataAdapters;
    }

    private DataAdapters() {
        this.dataAdapters = new ArrayList<DataAdapter>();
        this.dataAdapters.add(new DataAdapter_Virtual());
        this.dataAdapters.add(new DataAdapter_DaumErgoBike8008TRS4());
    }

    public ArrayList<DataAdapter> getDataAdapters() { return this.dataAdapters; }

    public DataAdapter getDataAdapter(String adapterName) {
        for (DataAdapter adapter : this.dataAdapters) {
            if (adapter.getName().toLowerCase().equals(adapterName.toLowerCase())) {
                return adapter;
            }
        }
        return null;
    }

}
