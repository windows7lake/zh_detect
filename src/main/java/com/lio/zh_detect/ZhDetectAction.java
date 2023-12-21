package com.lio.zh_detect;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class ZhDetectAction extends AnAction {

    public static Project project;
    public static VirtualFile currentDirectory;

    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        currentDirectory = e.getData(CommonDataKeys.VIRTUAL_FILE);

        ZhDetectFrame frame = new ZhDetectFrame();
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
