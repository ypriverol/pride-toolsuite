package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 30-Aug-2010
 * Time: 17:34:24
 */
public class RetrieveSpectrumTableTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, List<Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveSpectrumTableTask.class);

    /**
     * the size of each read iteration, for example: return every 100 spectra
     */
    private static final int DEFAULT_ITERATION_SIZE = 100;
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Spectra";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Spectra";
    /**
     * the start index
     */
    private int start;
    /**
     * the number of entries to retrieve
     */
    private int size;

    /**
     * Retrieve all the identifications.
     *
     * @param controller DataAccessController
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          thrown when there is error while reading the data.
     */
    public RetrieveSpectrumTableTask(DataAccessController controller) throws DataAccessException {
        this(controller, 0, controller.getNumberOfIdentifications());
    }

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     */
    public RetrieveSpectrumTableTask(DataAccessController controller, int start) {
        this(controller, start, DEFAULT_ITERATION_SIZE);
    }

    /**
     * Retrieve a subset of identifications.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     * @param size       the total size of the identifications to retrieve.
     */
    public RetrieveSpectrumTableTask(DataAccessController controller,
                                     int start,
                                     int size) {
        super(controller);
        this.start = start;
        this.size = size;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {
        try {
            Collection<Comparable> specIds = controller.getSpectrumIds();

            int specSize = specIds.size();
            if (start >= 0 && start < specSize && size > 0) {
                int stop = start + size;
                stop = stop > specSize ? specSize : stop;

                for (int i = start; i < stop; i++) {
                    List<Object> content = new ArrayList<Object>();
                    // spectrum id
                    Comparable specId = CollectionUtils.getElement(specIds, i);
                    content.add(specId);
                    //ms level
                    int msLevel = controller.getMsLevel(specId);
                    content.add(msLevel == -1 ? null : msLevel);
                    //identified spectra
                    content.add(controller.isIdentifiedSpectrum(specId));
                    // precursor charge
                    int pCharge = controller.getPrecursorCharge(specId);
                    content.add(pCharge == 0 ? null : pCharge);
                    // precursor m/z
                    double pMz = controller.getPrecursorMz(specId);
                    content.add(pMz == -1 ? null : NumberUtilities.scaleDouble(pMz, 4));
                    // precursor intensity
                    double pIntent = controller.getPrecursorIntensity(specId);
                    content.add(pIntent == -1 ? null : NumberUtilities.scaleDouble(pIntent, 1));
                    // sum of intensity
                    content.add(NumberUtilities.scaleDouble(controller.getSumOfIntensity(specId), 1));
                    // Number of peaks
                    content.add(controller.getNumberOfPeaks(specId));
                    publish(new Tuple<TableContentType, List<Object>>(TableContentType.SPECTRUM, content));

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }

            }
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve spectrum related data";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        } catch (InterruptedException ex) {
            logger.warn("Spectrum table update has been cancelled");
        }
        return null;
    }
}

