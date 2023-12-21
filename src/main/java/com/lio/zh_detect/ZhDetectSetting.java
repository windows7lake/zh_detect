package com.lio.zh_detect;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(name = "zhDetect", storages = {@Storage(value = "zhDetect.xml")})
public final class ZhDetectSetting implements PersistentStateComponent<ZhDetectSetting> {
    private Object[][] rowData;

    public static ZhDetectSetting getInstance(Project project) {
        return project.getService(ZhDetectSetting.class);
    }

    @Override
    public ZhDetectSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ZhDetectSetting zhDetectSetting) {
        rowData = zhDetectSetting.rowData;
    }

    public void setRowData(Object[][] rowData) {
        this.rowData = rowData;
    }

    public Object[][] getRowData() {
        return rowData;
    }
}
