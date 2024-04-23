// These are 5  panels that we need to display here in the
// dashboard for facilitating things and preventing errors from
// loading an loading the panels in the correct way we need a helper class.

// This panel  is a navigation helper between the panels.
// We use this method jPanel loader inside the JPanel Loader
// class in order to make a smooth transition

import javax.swing.*;

public class JPanelLoader {
    public void jPanelLoader(JPanel Main,JPanel setPanel){
        Main.removeAll();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(Main);
        Main.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(setPanel, GroupLayout.Alignment.LEADING,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE,
                                Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(setPanel, GroupLayout.Alignment.LEADING,
                                GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE,
                                Short.MAX_VALUE)
        );
        System.gc();



    }








}
