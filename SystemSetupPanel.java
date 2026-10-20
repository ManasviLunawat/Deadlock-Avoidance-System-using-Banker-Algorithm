import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SystemSetupPanel extends JPanel {

    private JTextField processField;
    private JTextField resourceField;
    private JTextField availableField;

    private JTable allocationTable;
    private JTable maximumTable;

    private DefaultTableModel allocationModel;
    private DefaultTableModel maximumModel;

    private JButton generateButton;
    private JButton initializeButton;

    private Runnable initializeAction;
    private Runnable inputChangedAction;

    private boolean updating = false;
    private boolean tablesGenerated = false;

    public SystemSetupPanel(Runnable initializeAction,
                            Runnable inputChangedAction) {

        this.initializeAction = initializeAction;
        this.inputChangedAction = inputChangedAction;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("System Setup");
        heading.setFont(new Font("Arial", Font.BOLD, 22));

        processField = new JTextField(5);
        resourceField = new JTextField(5);
        availableField = new JTextField(20);

        generateButton = new JButton("Generate Tables");
        initializeButton = new JButton("Initialize System");
        initializeButton.setEnabled(false);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        inputPanel.add(new JLabel("Number of Processes:"));
        inputPanel.add(processField);
        inputPanel.add(new JLabel("Number of Resource Types:"));
        inputPanel.add(resourceField);
        inputPanel.add(generateButton);

        allocationModel = new DefaultTableModel();
        maximumModel = new DefaultTableModel();

        allocationTable = new JTable(allocationModel);
        maximumTable = new JTable(maximumModel);

        JPanel allocationPanel = new JPanel(new BorderLayout(5, 5));
        allocationPanel.add(new JLabel("Allocation Matrix"), BorderLayout.NORTH);
        allocationPanel.add(new JScrollPane(allocationTable), BorderLayout.CENTER);

        JPanel maximumPanel = new JPanel(new BorderLayout(5, 5));
        maximumPanel.add(new JLabel("Maximum Matrix"), BorderLayout.NORTH);
        maximumPanel.add(new JScrollPane(maximumTable), BorderLayout.CENTER);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        tablesPanel.add(allocationPanel);
        tablesPanel.add(maximumPanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Available Resources (comma-separated):"));
        bottomPanel.add(availableField);
        bottomPanel.add(initializeButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateTables());

        initializeButton.addActionListener(e -> {
            if (initializeAction != null) {
                initializeAction.run();
            }
        });

        allocationModel.addTableModelListener(e -> notifyChanged());
        maximumModel.addTableModelListener(e -> notifyChanged());

        availableField.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    notifyChanged();
                }

                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    notifyChanged();
                }

                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    notifyChanged();
                }
            }
        );
    }

    private void generateTables() {
        try {
            int processes = Integer.parseInt(processField.getText().trim());
            int resources = Integer.parseInt(resourceField.getText().trim());

            if (processes <= 0 || resources <= 0
                    || processes > 20 || resources > 10) {
                JOptionPane.showMessageDialog(
                    this,
                    "Enter 1-20 processes and 1-10 resource types."
                );
                return;
            }

            updating = true;

            String[] columns = new String[resources + 1];
            columns[0] = "Process";

            for (int j = 1; j <= resources; j++) {
                columns[j] = "R" + (j - 1);
            }

            allocationModel.setDataVector(new Object[0][0], columns);
            maximumModel.setDataVector(new Object[0][0], columns);

            for (int i = 0; i < processes; i++) {
                Object[] allocationRow = new Object[resources + 1];
                Object[] maximumRow = new Object[resources + 1];

                allocationRow[0] = "P" + i;
                maximumRow[0] = "P" + i;

                for (int j = 1; j <= resources; j++) {
                    allocationRow[j] = "0";
                    maximumRow[j] = "0";
                }

                allocationModel.addRow(allocationRow);
                maximumModel.addRow(maximumRow);
            }

            allocationTable.getColumnModel().getColumn(0).setCellEditor(null);
            maximumTable.getColumnModel().getColumn(0).setCellEditor(null);

            availableField.setText("");

            tablesGenerated = true;
            initializeButton.setEnabled(true);

            updating = false;
            notifyChanged();

        } catch (NumberFormatException ex) {
            updating = false;
            JOptionPane.showMessageDialog(
                this,
                "Please enter valid process and resource counts."
            );
        }
    }

    private void notifyChanged() {
        if (!updating && tablesGenerated && inputChangedAction != null) {
            inputChangedAction.run();
        }
    }

    private void stopEditing(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    public int processCount() {
        if (!tablesGenerated) {
            throw new IllegalArgumentException("Generate the tables first.");
        }

        return allocationModel.getRowCount();
    }

    public int resourceCount() {
        if (!tablesGenerated) {
            throw new IllegalArgumentException("Generate the tables first.");
        }

        return allocationModel.getColumnCount() - 1;
    }

    public int[][] getAllocationMatrix() {
        stopEditing(allocationTable);

        int[][] allocation =
            new int[processCount()][resourceCount()];

        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[i].length; j++) {
                allocation[i][j] = Integer.parseInt(
                    allocationModel.getValueAt(i, j + 1).toString().trim()
                );
            }
        }

        return allocation;
    }

    public int[][] getMaximumMatrix() {
        stopEditing(maximumTable);

        int[][] maximum =
            new int[processCount()][resourceCount()];

        for (int i = 0; i < maximum.length; i++) {
            for (int j = 0; j < maximum[i].length; j++) {
                maximum[i][j] = Integer.parseInt(
                    maximumModel.getValueAt(i, j + 1).toString().trim()
                );
            }
        }

        return maximum;
    }

    public int[] getAvailableResources() {
        int resources = resourceCount();
        String text = availableField.getText().trim();

        if (text.isEmpty()) {
            throw new IllegalArgumentException(
                "Enter the available resources."
            );
        }

        String[] values = text.split(",");

        if (values.length != resources) {
            throw new IllegalArgumentException(
                "Enter exactly " + resources + " comma-separated values."
            );
        }

        int[] available = new int[resources];

        for (int i = 0; i < resources; i++) {
            available[i] = Integer.parseInt(values[i].trim());

            if (available[i] < 0) {
                throw new IllegalArgumentException(
                    "Resources cannot be negative."
                );
            }
        }

        return available;
    }

    public void updateAfterRequest(int[][] allocation, int[] available) {
        updating = true;

        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < available.length; j++) {
                allocationModel.setValueAt(allocation[i][j], i, j + 1);
            }
        }

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < available.length; i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(available[i]);
        }

        availableField.setText(text.toString());
        updating = false;
    }
}
