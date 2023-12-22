package com.lio.zh_detect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;

public class ZhResultFrame extends JFrame {
    private JPanel panel;
    private JTable table;
    private JButton close;
    private JButton generate;

    private DefaultTableModel tableModel;

    private final String[] columnName = {"文件路径", "文件行号", "简体字", "繁体字"};
    private List<ZhResult> resultList;

    public ZhResultFrame(List<ZhResult> resultList) {
        setContentPane(panel);
        setAlwaysOnTop(true);
        getRootPane().setDefaultButton(generate);

        initData(resultList);
        initUI();
    }

    private void initData(List<ZhResult> resultList) {
        this.resultList = resultList;
        final Object[][] rowData = new Object[resultList.size()][4];
        for (int i = 0; i < resultList.size(); i++) {
            rowData[i][0] = resultList.get(i).getPath();
            rowData[i][1] = resultList.get(i).getLine();
            rowData[i][2] = resultList.get(i).getZhSimplified();
            rowData[i][3] = resultList.get(i).getZhFan();
        }
        tableModel = new DefaultTableModel(rowData, columnName);
    }

    private void initUI() {
        table.setModel(tableModel);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(400);
        table.getColumnModel().getColumn(1).setMinWidth(60);
        table.getColumnModel().getColumn(2).setMinWidth(60);
        table.getColumnModel().getColumn(3).setMinWidth(60);
        close.addActionListener(e -> onClose());
        generate.addActionListener(e -> onGenerate());
    }

    private void onClose() {
        dispose();
    }

    private void onGenerate() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getPath();
            String fileName = "繁体检测.xls";
            ExcelTool.export(filePath, fileName, columnName, resultList);
            dispose();
        }
    }
}
