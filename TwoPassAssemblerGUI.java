import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TwoPassAssemblerGUI extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton assembleButton;

    public TwoPassAssemblerGUI() {
        setTitle("Two-Pass Assembler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputArea = new JTextArea(10, 40);
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        assembleButton = new JButton("Assemble");

        assembleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = inputArea.getText();
                String assembledCode = assemble(code);
                outputArea.setText(assembledCode);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(inputArea), BorderLayout.NORTH);
        panel.add(assembleButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        add(panel);
    }

    // The method to assemble the code
    public String assemble(String code) {
        String[] lines = code.split("\n");
        Map<String, Integer> symbolTable = new HashMap<>();
        StringBuilder machineCode = new StringBuilder();

        // Pass 1: Build the symbol table
        int address = 0;
        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length == 0 || tokens[0].startsWith(";")) {
                continue; // Skip empty lines and comments
            }
            if (tokens[0].endsWith(":")) {
                // This is a label
                String label = tokens[0].substring(0, tokens[0].length() - 1);
                symbolTable.put(label, address);
            } else {
                address++;
            }
        }

        // Pass 2: Convert instructions to machine code
        address = 0;
        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            if (tokens.length == 0 || tokens[0].startsWith(";") || tokens[0].endsWith(":")) {
                continue; // Skip empty lines, comments, and labels
            }
            String instruction = tokens[0].toUpperCase();
            String operand = tokens.length > 1 ? tokens[1] : "";

            switch (instruction) {
                case "LOAD":
                    machineCode.append("01 ").append(getAddressOrLabel(operand, symbolTable)).append("\n");
                    break;
                case "STORE":
                    machineCode.append("02 ").append(getAddressOrLabel(operand, symbolTable)).append("\n");
                    break;
                case "ADD":
                    machineCode.append("03 ").append(getAddressOrLabel(operand, symbolTable)).append("\n");
                    break;
                case "SUB":
                    machineCode.append("04 ").append(getAddressOrLabel(operand, symbolTable)).append("\n");
                    break;
                case "JMP":
                    machineCode.append("05 ").append(getAddressOrLabel(operand, symbolTable)).append("\n");
                    break;
                case "HLT":
                    machineCode.append("FF\n");
                    break;
                default:
                    machineCode.append("?? UNKNOWN INSTRUCTION\n");
                    break;
            }
            address++;
        }

        return machineCode.toString();
    }

    // Helper method to convert an operand to an address or a label
    private String getAddressOrLabel(String operand, Map<String, Integer> symbolTable) {
        if (operand.matches("\\d+")) {
            return operand;
        } else if (symbolTable.containsKey(operand)) {
            return symbolTable.get(operand).toString();
        } else {
            return "??";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TwoPassAssemblerGUI().setVisible(true);
            }
        });
    }
}
