package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;

import java.util.List;

/**
 * <p>Class to load the charts data in background.</p>
 *
 * @author Antonio Fabregat
 * Date: 26-ago-2010
 * Time: 11:59:12
 */
public class LoadChartDataTask extends AbstractDataAccessTask<List<PrideChartManager>, String> {
    private static final Logger logger = LoggerFactory.getLogger(LoadChartDataTask.class);

    public LoadChartDataTask(DataAccessController controller) {
        super(controller);
        this.setName("Loading chart data");
        this.setDescription("Loading chart data");
    }

    @Override
    protected List<PrideChartManager> retrieve() throws Exception {
        List<PrideChartManager> charts = null;
        try {
            charts = controller.getChartData();
        } catch (DataAccessException ex) {
            String msg = "Failed to get summary charts";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }
        return charts;
    }
}
