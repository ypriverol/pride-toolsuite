package uk.ac.ebi.pride.gui.component.peptide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.PTMTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * This pane is to display the PTMs details for a peptide
 * <p/>
 * User: rwang
 * Date: 09-Sep-2010
 * Time: 08:37:03
 */
public class PeptidePTMPane extends DataAccessControllerPane<Spectrum, Void> {
    private static final Logger logger = LoggerFactory.getLogger(PeptidePTMPane.class);

    private static final String PTM_TABLE_DESC = "PTM Details";
    private JTable ptmTable;

    public PeptidePTMPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    protected void addComponents() {
                // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // table label
        JLabel tableLabel = new JLabel("<html><b>" + PTM_TABLE_DESC + "</b></html>");
        metaDataPanel.add(tableLabel);
        this.add(metaDataPanel, BorderLayout.NORTH);

        ptmTable = TableFactory.createPTMTable();

        JScrollPane scrollPane = new JScrollPane(ptmTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();

        if (DataAccessController.PEPTIDE_TYPE.equals(evtName)) {
            logger.debug("New peptide selected for PTM table");
            // table model
            PTMTableModel ptmTableModel = (PTMTableModel)ptmTable.getModel();
            // reset the sorting behavior
            ptmTable.setRowSorter(new NumberTableRowSorter(ptmTableModel));
            // delete all rows
            ptmTableModel.removeAllRows();
            // get peptide
            Peptide peptide = (Peptide) evt.getNewValue();
            Spectrum spectrum = null;
            if (peptide != null) {
                ptmTableModel.addData(peptide);
                spectrum = peptide.getSpectrum();
                if (spectrum != null) {
                    spectrum.setPeptide(peptide);
                }
            }
            this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", spectrum);
        }
    }
}
