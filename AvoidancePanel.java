import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AvoidancePanel extends JPanel {

    private BankerAlgorithm banker;

    private DefaultTableModel needModel;
    private DefaultTableModel detailsModel;

    private JTable needTable;
    private JTable detailsTable;

    private JLabel availableLabel;
    private JLabel statusLabel;
    private JLabel detailLabel;

    private JTextField processField;
    private JTextField requestField;

    private JButton safetyButton;
    private JButton requestButton;

    private Runnable stateChangedAction;

    private final Color safeColor = new Color(20, 130, 55);
    private final Color unsafeColor = new Color(190, 35, 35);
    private final Color normalColor = Color.BLACK;

    public AvoidancePanel(Runnable stateChangedAction) {

        this.stateChangedAction = stateChangedAction;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel heading = new JLabel(
            "Safety Check and Resource Requests"
        );
        heading.setFont(new Font("Arial", Font.BOLD, 18));

        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Need Matrix
        needModel = new DefaultTableModel();
        needTable = new JTable(needModel);
        styleTable(needTable);

        JPanel needPanel = createTablePanel(
            "Need Matrix", needTable, 125
        );

        // Resource Details
        detailsModel = new DefaultTableModel();
        detailsTable = new JTable(detailsModel);
        styleTable(detailsTable);

        JPanel detailsPanel = createTablePanel(
            "Resource Details (Allocation, Maximum and Need)",
            detailsTable, 145
        );

        // Available Resources
        availableLabel = new JLabel(
            "Available Resources: Not initialized"
        );
        availableLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel availablePanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 5, 2)
        );
        availablePanel.add(availableLabel);
        availablePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        availablePanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 32)
        );

        // Safety Result - simple labels, no text box
        statusLabel = new JLabel("System not initialized");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 15));
        statusLabel.setForeground(normalColor);

        detailLabel = new JLabel(
            "Initialize the system and click Check Safe State."
        );
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel resultContent = new JPanel();
        resultContent.setLayout(
            new BoxLayout(resultContent, BoxLayout.Y_AXIS)
        );
        resultContent.setBorder(
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultContent.add(statusLabel);
        resultContent.add(Box.createVerticalStrut(6));
        resultContent.add(detailLabel);

        safetyButton = new JButton("Check Safe State");
        safetyButton.addActionListener(e -> checkSafety());

        JPanel safetyPanel = new JPanel(new BorderLayout(5, 5));
        safetyPanel.setBorder(
            BorderFactory.createTitledBorder("Safety Result")
        );
        safetyPanel.add(resultContent, BorderLayout.CENTER);
        safetyPanel.add(safetyButton, BorderLayout.SOUTH);

        safetyPanel.setPreferredSize(new Dimension(700, 105));
        safetyPanel.setMinimumSize(new Dimension(300, 95));
        safetyPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 115)
        );
        safetyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Resource Request
        processField = new JTextField(4);
        requestField = new JTextField(10);

        requestButton = new JButton("Submit Request");
        requestButton.addActionListener(e -> submitRequest());

        JPanel requestPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 5)
        );
        requestPanel.setBorder(
            BorderFactory.createTitledBorder("Resource Request")
        );

        requestPanel.add(new JLabel("Process (0-based):"));
        requestPanel.add(processField);
        requestPanel.add(new JLabel("Request (e.g. 1, 0):"));
        requestPanel.add(requestField);
        requestPanel.add(requestButton);

        requestPanel.setPreferredSize(new Dimension(700, 75));
        requestPanel.setMinimumSize(new Dimension(300, 65));
        requestPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 85)
        );
        requestPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Arrange sections
        content.add(needPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(detailsPanel);
        content.add(availablePanel);
        content.add(Box.createVerticalStrut(5));
        content.add(safetyPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(requestPanel);

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setBorder(null);
        contentScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        contentScroll.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(contentScroll, BorderLayout.CENTER);

        setControlsEnabled(false);
    }

    private JPanel createTablePanel(
            String title, JTable table, int height) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(700, height));
        panel.setMinimumSize(new Dimension(300, height));
        panel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, height)
        );
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private void styleTable(JTable table) {

        table.setRowHeight(23);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    public void setBanker(BankerAlgorithm banker) {

        this.banker = banker;

        if (banker == null) {

            needModel.setRowCount(0);
            needModel.setColumnCount(0);

            detailsModel.setRowCount(0);
            detailsModel.setColumnCount(0);

            availableLabel.setText(
                "Available Resources: Not initialized"
            );

            showNeutralResult(
                "System not initialized",
                "Initialize the system to begin."
            );

            setControlsEnabled(false);

        } else {

            refreshTables();

            showNeutralResult(
                "System initialized",
                "Click Check Safe State to check for a safe sequence."
            );

            setControlsEnabled(true);
        }
    }

    private void refreshTables() {

        int processes = banker.getProcessCount();
        int resources = banker.getResourceCount();

        int[][] allocation = banker.getAllocation();
        int[][] maximum = banker.getMaximum();
        int[][] need = banker.getNeed();
        int[] available = banker.getAvailable();

        // Need Matrix
        needModel.setRowCount(0);
        needModel.setColumnCount(0);
        needModel.addColumn("Process");

        for (int j = 0; j < resources; j++) {
            needModel.addColumn("R" + j);
        }

        for (int i = 0; i < processes; i++) {

            Object[] row = new Object[resources + 1];
            row[0] = "P" + i;

            for (int j = 0; j < resources; j++) {
                row[j + 1] = need[i][j];
            }

            needModel.addRow(row);
        }

        // Allocation, Maximum and Need Details
        detailsModel.setRowCount(0);
        detailsModel.setColumnCount(0);
        detailsModel.addColumn("Process");

        for (int j = 0; j < resources; j++) {
            detailsModel.addColumn("Alloc R" + j);
        }

        for (int j = 0; j < resources; j++) {
            detailsModel.addColumn("Max R" + j);
        }

        for (int j = 0; j < resources; j++) {
            detailsModel.addColumn("Need R" + j);
        }

        for (int i = 0; i < processes; i++) {

            Object[] row = new Object[1 + resources * 3];
            row[0] = "P" + i;

            int column = 1;

            for (int j = 0; j < resources; j++) {
                row[column++] = allocation[i][j];
            }

            for (int j = 0; j < resources; j++) {
                row[column++] = maximum[i][j];
            }

            for (int j = 0; j < resources; j++) {
                row[column++] = need[i][j];
            }

            detailsModel.addRow(row);
        }

        // Available Resources
        StringBuilder text = new StringBuilder(
            "Available Resources: "
        );

        for (int j = 0; j < resources; j++) {

            text.append("R")
                .append(j)
                .append(" = ")
                .append(available[j]);

            if (j < resources - 1) {
                text.append("    ");
            }
        }

        availableLabel.setText(text.toString());
    }

    // Green status when the system is safe
    private void showSafeResult(String sequence) {

        statusLabel.setText("SAFE STATE - NO DEADLOCK RISK");
        statusLabel.setForeground(safeColor);

        detailLabel.setText(
            "<html>The system is safe. Deadlock can be avoided.<br>"
            + "Safe Sequence: " + sequence
            + "</html>"
        );
    }

    // Red status when the system is unsafe
    private void showUnsafeResult() {

        statusLabel.setText("UNSAFE STATE - DEADLOCK POSSIBLE");
        statusLabel.setForeground(unsafeColor);

        detailLabel.setText(
            "<html>No safe sequence exists.<br>"
            + "Deadlock avoidance cannot be guaranteed "
            + "in the current state.</html>"
        );
    }

    // Neutral messages for initialization and denied requests
    private void showNeutralResult(
            String status, String details) {

        statusLabel.setText(status);
        statusLabel.setForeground(normalColor);

        detailLabel.setText("<html>" + details + "</html>");
    }

    private void checkSafety() {

        if (banker == null) {
            return;
        }

        String result = banker.checkSafety();

        if (result.startsWith("SAFE STATE")) {

            int marker = result.indexOf("Safe Sequence:");

            if (marker >= 0) {

                String sequence = result.substring(
                    marker + "Safe Sequence:".length()
                ).trim();

                showSafeResult(sequence);

            } else {
                showSafeResult("Not available");
            }

        } else {
            showUnsafeResult();
        }
    }

    private void submitRequest() {

        if (banker == null) {
            return;
        }

        try {

            int process = Integer.parseInt(
                processField.getText().trim()
            );

            String requestText = requestField.getText().trim();

            if (requestText.isEmpty()) {
                throw new IllegalArgumentException(
                    "Please enter the resource request."
                );
            }

            String[] values = requestText.split(",");
            int[] request = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                request[i] = Integer.parseInt(values[i].trim());
            }

            String result = banker.requestResources(process, request);

            if (result.startsWith("REQUEST GRANTED")) {

                int marker = result.indexOf("Safe Sequence:");
                String sequence = "Not available";

                if (marker >= 0) {
                    sequence = result.substring(
                        marker + "Safe Sequence:".length()
                    ).trim();
                }

                showNeutralResult(
                    "REQUEST GRANTED",
                    "Request accepted. Safe Sequence: " + sequence
                );
                statusLabel.setForeground(safeColor);

                refreshTables();

                if (stateChangedAction != null) {
                    stateChangedAction.run();
                }

            } else {

                showNeutralResult(
                    "REQUEST DENIED",
                    result.replace('\n', ' ')
                );
                statusLabel.setForeground(unsafeColor);
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Enter a valid process number and comma-separated integers.",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void setControlsEnabled(boolean enabled) {

        safetyButton.setEnabled(enabled);
        requestButton.setEnabled(enabled);
        processField.setEnabled(enabled);
        requestField.setEnabled(enabled);
    }
}
