package uk.ac.ebi.pride.mzgraph.gui;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;

import javax.swing.*;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsTableTest {
    public static void main(String[] args) {
        PrecursorIon precursorIon = new DefaultPrecursorIon(ExampleUtil.generatePeptide());

        ExperimentalFragmentedIonsTable table = new ExperimentalFragmentedIonsTable(precursorIon, ProductIonPair.B_Y, 2, ExampleUtil.mzArr, ExampleUtil.intentArr);

        //add annotation by hand
        ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) table.getModel();
        List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        tableModel.addAllManualAnnotations(annotationList);

        // test whether show auto and manual annotations, or not.
        table.setShowAutoAnnotations(false);
        table.setShowManualAnnotations(false);

        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(scrollPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
