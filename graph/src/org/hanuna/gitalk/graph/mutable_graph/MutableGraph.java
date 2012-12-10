package org.hanuna.gitalk.graph.mutable_graph;

import org.hanuna.gitalk.common.ReadOnlyList;
import org.hanuna.gitalk.common.compressedlist.Replace;
import org.hanuna.gitalk.graph.Graph;
import org.hanuna.gitalk.graph.graph_elements.NodeRow;
import org.hanuna.gitalk.graph.mutable_graph.graph_elements_impl.MutableNodeRow;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author erokhins
 */
public class MutableGraph implements Graph {
    private final List<MutableNodeRow> allRows;
    private final List<MutableNodeRow> visibleRows;

    public MutableGraph(List<MutableNodeRow> allRows) {
        this.allRows = allRows;
        this.visibleRows = new ArrayList<MutableNodeRow>(allRows);
        recalculateRowIndex();
    }

    public Replace fixRowVisibility(int fromRowIndex, int toRowIndex) {
        if (fromRowIndex < 0 || toRowIndex >= allRows.size() || fromRowIndex > toRowIndex) {
            throw new IllegalArgumentException("fromRowIndex: " + fromRowIndex + "toRowIndex: " + toRowIndex);
        }
        MutableNodeRow upRow = visibleRows.get(fromRowIndex);
        MutableNodeRow downRow = visibleRows.get(toRowIndex);
        for (int i = upRow.getLogIndex(); i <= downRow.getLogIndex(); i++) {
            allRows.get(i).updateVisibleNodes();
        }
        recalculateRowIndex();
        return new Replace(fromRowIndex, toRowIndex, downRow.getRowIndex() - upRow.getRowIndex() - 1);
    }

    private void recalculateRowIndex() {
        visibleRows.clear();
        int rowIndex = 0;
        for (MutableNodeRow row : allRows) {
            if (row.hasVisibleNodes()) {
                row.setRowIndex(rowIndex);
                rowIndex++;
                visibleRows.add(row);
            }
        }
    }

    @NotNull
    @Override
    public ReadOnlyList<NodeRow> getNodeRows() {
        return ReadOnlyList.<NodeRow>newReadOnlyList(visibleRows);
    }

}
