import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;

    private SystemSetupPanel setupPanel;
    private AvoidancePanel avoidancePanel;

    private BankerAlgorithm banker;

    public MainFrame() {

        setTitle("Deadlock Avoidance System Using Banker's Algorithm");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createPanels();
        createLayout();
    }

    private void createPanels() {

        setupPanel = new SystemSetupPanel(
            this::initializeSystem,
            this::invalidateSystem
        );

        avoidancePanel = new AvoidancePanel(
            this::refreshAfterRequest
        );
    }

    private void createLayout() {

        setLayout(new BorderLayout());

        JLabel heading = new JLabel(
            "Deadlock Avoidance System Using Banker's Algorithm",
            SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setBorder(
            BorderFactory.createEmptyBorder(15, 5, 15, 5)
        );

        tabs = new JTabbedPane();

        tabs.addTab("System Setup", setupPanel);
        tabs.addTab("Safety & Resource Requests", avoidancePanel);

        JButton resetButton = new JButton("Reset System");
        JButton exitButton = new JButton("Exit");

        resetButton.addActionListener(e -> resetSystem());
        exitButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.add(resetButton);
        bottomPanel.add(exitButton);

        add(heading, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initializeSystem() {

        try {

            int[][] allocation = setupPanel.getAllocationMatrix();
            int[][] maximum = setupPanel.getMaximumMatrix();
            int[] available = setupPanel.getAvailableResources();

            banker = new BankerAlgorithm(
                allocation,
                maximum,
                available
            );

            avoidancePanel.setBanker(banker);

            JOptionPane.showMessageDialog(
                this,
                "System initialized successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            tabs.setSelectedIndex(1);

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Please check your input values.\n" + ex.getMessage(),
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void invalidateSystem() {

        banker = null;

        if (avoidancePanel != null) {
            avoidancePanel.setBanker(null);
        }
    }

    private void refreshAfterRequest() {

        if (banker != null) {

            setupPanel.updateAfterRequest(
                banker.getAllocation(),
                banker.getAvailable()
            );
        }
    }

    private void resetSystem() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the system?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            banker = null;

            setupPanel = new SystemSetupPanel(
                this::initializeSystem,
                this::invalidateSystem
            );

            tabs.setComponentAt(0, setupPanel);
            avoidancePanel.setBanker(null);

            tabs.setSelectedIndex(0);
        }
    }
}
