package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.db.DatabaseSearchPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

/**
 * Task to open database search pane
 * <p/>
 * User: rwang
 * Date: 27/05/11
 * Time: 14:44
 */
public class OpenDatabaseSearchPaneTask extends TaskAdapter<Void, Void> {
    private static final String DEFAULT_TASK_TITLE = "Open database search panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open database search panel";

    public OpenDatabaseSearchPaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        DatabaseSearchPane searchPane = context.getDatabaseSearchPane();
        if (searchPane == null) {
            // create a database search pane
            Runnable code = new Runnable() {
                @Override
                public void run() {
                    context.setDatabaseSearchPane(new DatabaseSearchPane(null));
                }
            };

            EDTUtils.invokeAndWait(code);

            // start loading
            SearchDatabaseTask searchTask = new SearchDatabaseTask();
            searchTask.setGUIBlocker(new DefaultGUIBlocker(searchTask, GUIBlocker.Scope.NONE, null));
            context.addTask(searchTask);
        }

        EventBus.publish(new DatabaseSearchEvent(DatabaseSearchEvent.Status.SHOW));
        return null;
    }
}
