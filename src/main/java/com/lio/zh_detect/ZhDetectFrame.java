package com.lio.zh_detect;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ZhDetectFrame extends JFrame {
    private JPanel panel;
    private JTable table;
    private JButton addRow;
    private JButton deleteRow;
    private JButton save;
    private JButton detect;

    private DefaultTableModel tableModel;

    private List<ZhResult> resultList = new ArrayList<>();

    public ZhDetectFrame() {
        setContentPane(panel);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(detect);

        initData();
        initUI();
    }

    private void initData() {
        final Object[] columnName = {"需要排除的文件名", "需要排除的文件行数（不填则排除整个文件）"};
        final Object[][] rowData = ZhDetectSetting.getInstance(ZhDetectAction.project).getRowData();
        tableModel = new DefaultTableModel(rowData, columnName);
    }

    private void initUI() {
        table.setModel(tableModel);
        table.setRowHeight(35);
        addRow.addActionListener(e -> addRow());
        deleteRow.addActionListener(e -> deleteRow());
        save.addActionListener(e -> onSave());
        detect.addActionListener(e -> onDetect());
    }

    private void addRow() {
        tableModel.addRow(new Object[]{"", ""});
    }

    private void deleteRow() {
        tableModel.removeRow(table.getSelectedRow());
    }

    private void onSave() {
        Vector<Vector> vectors = tableModel.getDataVector();
        Object[][] rowData = new Object[vectors.size()][2];
        for (int i = 0; i < vectors.size(); i++) {
            rowData[i] = vectors.get(i).toArray();
        }
        ZhDetectSetting.getInstance(ZhDetectAction.project).setRowData(rowData);
    }

    private void onDetect() {
        onSave();
        // 需要扫描的根目录
        VirtualFile directory = ZhDetectAction.currentDirectory;
        if (directory == null) {
            showDialog("请先在项目中选中想要扫描的目录");
            return;
        }
        File destinyDirectory = new File(directory.getPath());
        operateDirectory(destinyDirectory);
        showResult(resultList);
    }

    private List<String> inWhiteList(String path) {
        List<String> lineList = new ArrayList<>();
        // 需要排除扫描的文件白名单
        Object[][] whiteList = ZhDetectSetting.getInstance(ZhDetectAction.project).getRowData();
        for (Object[] filePath : whiteList) {
            String fileName = (String) filePath[0];
            String lineNumber = (String) filePath[1];
            if (fileName.isEmpty() && lineNumber.isEmpty()) {
                continue;
            }
            if (lineNumber.isEmpty()) {
                lineList = new ArrayList<>();
            } else {
                lineList = Arrays.stream(lineNumber.split(",")).toList();
            }
            if (path.contains(fileName)) {
                if (lineList.isEmpty()) {
                    lineList.add("0");
                }
                return lineList;
            }
        }
        return lineList;
    }

    private void operateDirectory(File directory) {
        if (directory.isDirectory()) {
            if (directory.listFiles() == null) return;
            for (File subDirectory : Objects.requireNonNull(directory.listFiles())) {
                operateDirectory(subDirectory);
            }
        } else if (directory.isFile()) {
            operateFile(directory);
        }
    }

    private void operateFile(File file) {
        List<String> lineList = inWhiteList(file.getPath());
        // 没有填写行数的文件，则直接过滤
        if (lineList.size() == 1 && Objects.equals(lineList.get(0), "0")) {
            System.out.println(file.getPath() + "  return " + lineList.size() + "--" + lineList.get(0));
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lineCount = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (lineList.contains(Integer.toString(lineCount))) {
                    continue;
                }
                final String lineContent = line.trim();
                final String lineNumber = Integer.toString(lineCount);
                if (lineContent.startsWith("//")
                        || lineContent.startsWith("print")
                        || lineContent.startsWith("log")) {
                    continue;
                }
                ZhData.map.forEach((zhFan, zhSimplified) -> {
                    if (lineContent.contains(zhSimplified)) {
                        resultList.add(new ZhResult(file.getPath(), lineNumber, zhSimplified, zhFan));
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showResult(List<ZhResult> resultList) {
        dispose();
        ZhResultFrame frame = new ZhResultFrame(resultList);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showDialog(String text) {
        MessageDialog dialog = new MessageDialog(text);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
