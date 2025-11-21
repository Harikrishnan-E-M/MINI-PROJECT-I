import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.*;


public class TrafficGUI extends JFrame {
    private TrafficController controller;
    private JComboBox<String> directionBox;
    private JTextField vehicleNumberField, vehicleTypeField;
    private JTextArea outputArea;

    public TrafficGUI() {
        controller = new TrafficController(); // use your logic
        setTitle("Traffic Signal Simulation System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Details"));

        inputPanel.add(new JLabel("Direction:"));
        directionBox = new JComboBox<>(new String[]{"North", "East", "South", "West"});
        inputPanel.add(directionBox);

        inputPanel.add(new JLabel("Vehicle Number:"));
        vehicleNumberField = new JTextField();
        inputPanel.add(vehicleNumberField);

        inputPanel.add(new JLabel("Vehicle Type:"));
        vehicleTypeField = new JTextField();
        inputPanel.add(vehicleTypeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton addButton = new JButton("Add Vehicle");
        JButton showButton = new JButton("Show Queues");
        JButton deleteButton = new JButton("Delete Vehicles");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(showButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Simulation Output"));

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);


        addButton.addActionListener(e -> {
            String dir = (String) directionBox.getSelectedItem();
            String num = vehicleNumberField.getText();
            String type = vehicleTypeField.getText();

            if (num.isEmpty() || type.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.addVehicle(dir, new Vehicle(num, type));

            DatabaseHelper.insertVehicle(dir, num, type);

            outputArea.append(" Added: " + num + " (" + type + ") to " + dir + "\n");
            vehicleNumberField.setText("");
            vehicleTypeField.setText("");
        });

        showButton.addActionListener(e -> {
            outputArea.setText("");
            outputArea.append(" Current Vehicle Queues by Direction:\n");
            outputArea.append("------------------------------------------------------------\n");

            try (ResultSet rs = DatabaseHelper.getAllVehicles()) {
                if (rs == null) {
                    outputArea.append(" Database error or no data found.\n");
                    return;
                }


                Map<String, List<String>> directionMap = new LinkedHashMap<>();
                directionMap.put("North", new ArrayList<>());
                directionMap.put("East", new ArrayList<>());
                directionMap.put("South", new ArrayList<>());
                directionMap.put("West", new ArrayList<>());

                while (rs.next()) {
                    String direction = rs.getString("direction");
                    String number = rs.getString("vehicleNumber");
                    String type = rs.getString("type");
                    directionMap.computeIfAbsent(direction, k -> new ArrayList<>())
                            .add(number + " (" + type + ")");
                }

                int total = 0;
                for (String dir : directionMap.keySet()) {
                    List<String> list = directionMap.get(dir);
                    outputArea.append(dir + " (" + list.size() + " vehicles):\n");
                    if (list.isEmpty()) {
                        outputArea.append("   - No vehicles\n");
                    } else {
                        for (String v : list) {
                            outputArea.append("   - " + v + "\n");
                        }
                    }
                    outputArea.append("------------------------------------------------------------\n");
                    total += list.size();
                }

                outputArea.append(" TOTAL VEHICLES IN DATABASE: " + total + "\n");

            } catch (Exception ex) {
                outputArea.append(" Error reading data: " + ex.getMessage() + "\n");
            }
        });

        deleteButton.addActionListener(e -> {
            String dir = (String) directionBox.getSelectedItem();

            controller.deleteVehiclesInDirection(dir);

            DatabaseHelper.deleteVehicles(dir);

            outputArea.append(" Deleted all vehicles in " + dir + "\n");
        });

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrafficGUI::new);
    }
}
