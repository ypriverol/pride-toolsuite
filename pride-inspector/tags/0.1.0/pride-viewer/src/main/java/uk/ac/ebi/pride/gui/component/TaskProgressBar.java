package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 12-Feb-2010
 * Time: 16:06:30
 */
public class TaskProgressBar extends JProgressBar implements TaskListener<Object, Object> {

    private Task task;

    public TaskProgressBar(Task task) {
        super();
        this.task = task;
        this.task.addTaskListener(this);
        
        this.updateMessage("");
        this.setIndeterminate(true);
        this.setStringPainted(true);
    }

    @Override
    public void process(TaskEvent<List<Object>> taskEvent) {
        List<Object> values = taskEvent.getValue();
        for(Object value : values) {
            if (value instanceof String)
                updateMessage((String)value);
        }
    }

    @Override
    public void finished(TaskEvent<Void> event) {
        this.setIndeterminate(false);
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        updateMessage(" - Failed!");
    }

    @Override
    public void succeed(TaskEvent<Object> taskEvent) {
        updateMessage(" - Succeed!");
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        updateMessage(" - Cancelled!");
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        updateMessage(" - Interrupted!");
    }

    private void updateMessage(String msg) {
        this.setString(task.getName() + msg);
        this.setToolTipText(task.getDescription() + msg);
    }
}
