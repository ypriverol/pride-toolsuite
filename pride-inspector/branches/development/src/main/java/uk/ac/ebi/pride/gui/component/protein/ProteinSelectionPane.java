package uk.ac.ebi.pride.gui.component.protein;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.action.impl.DecoyFilterAction;
import uk.ac.ebi.pride.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.gui.event.container.ProteinIdentificationEvent;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * IdentificationSelectionPane displays identification related details in a table.
 *
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 10:20:51
 */
public class ProteinSelectionPane extends DataAccessControllerPane {

    private static final Logger logger = LoggerFactory.getLogger(ProteinTabPane.class.getName());

    /**
     * table for display the identifications
     */
    private JTable identTable;

    /**
     * Constructor
     * @param controller    data access controller
     */
    public ProteinSelectionPane(DataAccessController controller) {
        super(controller);
    }

    /**
     * Setup the main pane
     */
    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        identTable = TableFactory.createIdentificationTable(controller);

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);


        // add row selection listener
        ListSelectionModel selectionModel = identTable.getSelectionModel();
        selectionModel.addListSelectionListener(new IdentificationSelectionListener(identTable));

        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This builds the top panel to display, it includes
     *
     * @return  JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JToolBar buttonPanel = buildButtonPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        return titlePanel;
    }

    /**
     * Build meta data pane, this panel displays the identification type, search engine and search database
     *
     * @return JPanel   meta data pane
     */
    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        try {
            // protein table label
            JLabel tableLabel =  new JLabel("<html><b>Protein</b></html>");
            metaDataPanel.add(tableLabel);
            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

            // identification type
            Collection<Comparable> identIds = controller.getIdentificationIds();
            Comparable identId = CollectionUtils.getElement(identIds, 0);
            Object type = identId == null ? "Unknown" : controller.getIdentificationType(identId);
            type = type == null ? "Unknown" : type;
            JLabel identLabel = new JLabel("<html><b>Type</b>: " + type + "</html>");
            identLabel.setToolTipText(type.toString());
            metaDataPanel.add(identLabel);
            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        } catch (DataAccessException e) {
            String msg = "Failed to createAttributedSequence meta data pane for identifications";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        return metaDataPanel;
    }

    /**
     * Build toolbar which contains all the buttons.
     *
     * @return  JToolbar    tool bar
     */
    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // load protein names
        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
        loadAllProteinNameButton.setForeground(Color.blue);

        loadAllProteinNameButton.setAction(new ExtraProteinDetailAction(controller));
        toolBar.add(loadAllProteinNameButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // decoy filter
        JButton decoyFilterButton = GUIUtilities.createLabelLikeButton(null, null);
        decoyFilterButton.setForeground(Color.blue);
        PrideAction action = appContext.getPrideAction(controller, DecoyFilterAction.class);
        if (action == null) {
            action = new DecoyFilterAction(controller);
            appContext.addPrideAction(controller, action);
        }
        decoyFilterButton.setAction(action);
        toolBar.add(decoyFilterButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(20, 10)));

        // shared peptide warning
        Icon sharedPeptideIcon = GUIUtilities.loadIcon(appContext.getProperty("shared.peptide.icon.small"));
        JButton sharedPeptideButton = GUIUtilities.createLabelLikeButton(sharedPeptideIcon, appContext.getProperty("shared.peptide.title"));
        sharedPeptideButton.setToolTipText(appContext.getProperty("shared.peptide.tooltip"));
        sharedPeptideButton.setForeground(Color.blue);
        CSH.setHelpIDString(sharedPeptideButton, "help.faq.shared.peptide");
        sharedPeptideButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(sharedPeptideButton);

        // expand button
        Icon expandIcon = GUIUtilities.loadIcon(appContext.getProperty("expand.table.icon.small"));
        JButton expandButton = GUIUtilities.createLabelLikeButton(expandIcon, null);
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                eventBus.publish(new ExpandPanelEvent(ProteinSelectionPane.this, ProteinSelectionPane.this));
            }
        });

        toolBar.add(expandButton);

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }

    /**
     * Return the identification table
     *
     * @return  JTable  identification details table.
     */
    public JTable getIdentificationTable() {
        return identTable;
    }


    /**
     * This selection listener listens to Identification table for any selection on the row.
     * It will then fire a property change event with the selected identification id.
     */
    private class IdentificationSelectionListener implements ListSelectionListener {
        private final JTable table;

        private IdentificationSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                int rowCnt = table.getRowCount();
                if (rowCnt > 0 && rowNum >= 0) {
                    // get table model
                    ProteinTableModel tableModel = (ProteinTableModel)identTable.getModel();
                    // fire a property change event with selected identification id
                    int columnNum = tableModel.getColumnIndex(ProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
                    Comparable identId = (Comparable)tableModel.getValueAt(table.convertRowIndexToModel(rowNum), columnNum);
                    // publish the event to local event bus
                    EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                    eventBus.publish(new ProteinIdentificationEvent(ProteinSelectionPane.this, controller, identId));
                }
            }
        }
    }
}
