package uk.ac.ebi.pride.gui.component.peptide;

import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.mzgraph.SpectrumViewPane;
import uk.ac.ebi.pride.gui.component.sequence.ProteinSequencePane;

import javax.swing.*;
import java.awt.*;

/**
 * Visualize both spectrum and protein sequence
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 16:43
 */
public class PeptideVizPane extends DataAccessControllerPane implements EventBusSubscribable {
    private static Logger logger = LoggerFactory.getLogger(PeptideVizPane.class);
    /**
     * the default background color
     */
    private static final Color BACKGROUND_COLOUR = Color.white;

    private SpectrumViewPane spectrumViewPane;
    private ProteinSequencePane proteinSequencePane;

    public PeptideVizPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @Override
    protected void addComponents() {
        // tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOUR);

        // tab index
        int tabIndex = 0;

        try {
            if (controller.hasSpectrum()) {
                // Spectrum view pane
                spectrumViewPane = new SpectrumViewPane(controller);
                tabbedPane.insertTab(appContext.getProperty("spectrum.tab.title"), null,
                        spectrumViewPane, appContext.getProperty("spectrum.tab.tooltip"), tabIndex);
                tabIndex++;
            }
        } catch (DataAccessException e) {
            String msg = "Failed to check the availability of spectrum";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // protein sequence pane
        proteinSequencePane = new ProteinSequencePane(controller);
        JScrollPane scrollPane = new JScrollPane(proteinSequencePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(BACKGROUND_COLOUR);
        tabbedPane.insertTab(appContext.getProperty("protein.sequence.tab.title"), null,
                scrollPane, appContext.getProperty("protein.sequence.tab.tooltip"), tabIndex);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        if (spectrumViewPane != null) {
            spectrumViewPane.subscribeToEventBus(null);
        }
        proteinSequencePane.subscribeToEventBus(null);
    }

}
