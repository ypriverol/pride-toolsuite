package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static uk.ac.ebi.pride.gui.utils.Constants.LINE_SEPARATOR;
import static uk.ac.ebi.pride.gui.utils.Constants.TAB;

/**
 * Task to export peptide related information.
 * <p/>
 * User: rwang, yperez
 * Date: 13-Oct-2013
 * Time: 16:08:37
 */
public class ExportPeptideDescTask extends AbstractDataAccessTask<Void, Void> {

    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Exporting Peptide Descriptions";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Exporting Peptide Descriptions";
    /**
     * output File
     */
    private String outputFilePath;

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller     DataAccessController
     * @param outputFilePath file path to output the result.
     */
    public ExportPeptideDescTask(DataAccessController controller, String outputFilePath) {
        super(controller);
        this.outputFilePath = outputFilePath;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(new File(outputFilePath)));
            ExperimentMetaData exp = controller.getExperimentMetaData();

            // data source
            if (controller.getType().equals(DataAccessController.Type.XML_FILE) || controller.getType().equals(DataAccessController.Type.MZIDENTML)) {
                writer.println("# Data source: " + ((File) controller.getSource()).getAbsolutePath());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                writer.println("# Data source: pride public mysql instance");
            }

            // accession if exist
            String acc = (exp.getId() != null) ? exp.getId().toString() : null;
            if (acc != null) {
                writer.println("# Experiment accession: " + acc);
            }

            // number of spectrum
            if (controller.hasSpectrum()) {
                writer.println("# Number of spectra: " + controller.getNumberOfSpectra());
            }

            // number of protein identifications
            if (controller.hasProtein()) {
                writer.println("# Number of protein identifications: " + controller.getNumberOfProteins());
            }

            // number of peptides
            if (controller.hasPeptide()) {
                writer.println("# Number of peptides: " + controller.getNumberOfPeptides());
            }

            // in order to get a list of headers for export
            // first, we need to create an instance of PeptideTableModel
            PeptideTableModel pepTableModel = new PeptideTableModel(controller.getAvailablePeptideLevelScores());
            // a list of columns to be skipped
            List<Integer> skipIndexes = new ArrayList<Integer>();
            // skip identification id
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader()));
            // skip peptide id
            skipIndexes.add(pepTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader()));
            // get number of columns in peptide table model
            int numOfCols = pepTableModel.getColumnCount();
            // iterate over each column to construct the header
            StringBuilder header = new StringBuilder();
            // ignore the last two columns
            // ignore row number
            for (int i = 1; i < numOfCols; i++) {
                if (!skipIndexes.contains(i)) {
                    header.append(pepTableModel.getColumnName(i));
                    header.append(TAB);
                }
            }
            writer.println(header.toString());

            Collection<Comparable> identIds = controller.getProteinIds();

            for (Comparable identId : identIds) {
                Collection<Comparable> pepIds = controller.getPeptideIds(identId);
                if (pepIds != null) {
                    for (Comparable pepId : pepIds) {

                        // get row data
                        PeptideTableRow content = TableDataRetriever.getPeptideTableRow(controller, identId, pepId);

                        writer.print(content.getProteinAccession() == null ? "" : content.getProteinAccession().getAccession().toString());
                        writer.print(TAB);

                        writer.print(content.getProteinName() == null ? "" : content.getProteinName().toString());
                        writer.print(TAB);

                        // line break
                        writer.print(LINE_SEPARATOR);

                        // this is important for cancelling
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                    }
                }
            }
            writer.flush();
        } catch (DataAccessException e2) {
            String msg = "Failed to retrieve data from data source";
            logger.error(msg, e2);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (IOException e1) {
            String msg = "Failed to write data to the output file, please check you have the right permission";
            logger.error(msg, e1);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Export Error");
        } catch (InterruptedException e3) {
            logger.warn("Exporting peptide description has been interrupted");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}