package abeona.demos.salesman.gui;

public final class Program {
    public static void main(String[] args) {
        final var simulator = new Simulator();
        simulator.pack();
        simulator.setVisible(true);
        //        final LinesComponent comp = new LinesComponent();
        //        comp.setPreferredSize(new Dimension(320, 200));
        //        simulator.getContentPane().add(comp, BorderLayout.CENTER);
        //        JPanel buttonsPanel = new JPanel();
        //        JButton newLineButton = new JButton("New Line");
        //        JButton clearButton = new JButton("Clear");
        //        buttonsPanel.add(newLineButton);
        //        buttonsPanel.add(clearButton);
        //        simulator.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        //        newLineButton.addActionListener(new ActionListener() {
        //
        //            @Override
        //            public void actionPerformed(ActionEvent e) {
        //                int x1 = (int) (Math.random() * 320);
        //                int x2 = (int) (Math.random() * 320);
        //                int y1 = (int) (Math.random() * 200);
        //                int y2 = (int) (Math.random() * 200);
        //                Color randomColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        //                comp.addLine(x1, y1, x2, y2, randomColor);
        //            }
        //        });
        //        clearButton.addActionListener(new ActionListener() {
        //
        //            @Override
        //            public void actionPerformed(ActionEvent e) {
        //                comp.clearLines();
        //            }
        //        });
    }
}
