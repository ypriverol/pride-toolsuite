package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Table with altering colours for each row
 *
 * User: rwang
 * Date: 22-Aug-2010
 * Time: 10:02:32
 */
public class AlterRowColorTable extends JXTable {

    private Color alterRowColor = Constants.ALTER_ROW_COLOUR;

    private int omitColumn = -1;

    public AlterRowColorTable() {
    }

    public AlterRowColorTable(TableModel dm) {
        super(dm);
    }

    public AlterRowColorTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public AlterRowColorTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public AlterRowColorTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public AlterRowColorTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public AlterRowColorTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isCellSelected(row, column)) {
            c.setBackground(colorForRow(row));
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
            if (omitColumn < 0 || column != omitColumn) {
                c.setBackground(Constants.ROW_SELECTION_BACKGROUD);
                c.setForeground(Constants.ROW_SELECTION_FOREGROUND);
            }
        }
        return c;
    }

    private Color colorForRow(int row) {
        return (row % 2 == 0) ? Constants.ALTER_ROW_COLOUR : getBackground();
    }

    public Color getAlterRowColor() {
        return alterRowColor;
    }

    public void setAlterRowColor(Color alterRowColor) {
        this.alterRowColor = alterRowColor;
    }

    public int getOmitColumn() {
        return omitColumn;
    }

    public void setOmitColumn(int omitColumn) {
        this.omitColumn = omitColumn;
    }

    @Override
    public Color getSelectionBackground() {
        return Constants.ROW_SELECTION_BACKGROUD;
    }
}
