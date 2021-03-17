package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import sample.Interfaces.SelectableNode;

import java.util.ArrayList;
import java.util.List;

public class SelectionHandler {
    private Clipboard clipboard;

    private EventHandler<MouseEvent> mousePressedEventHandler;

    public SelectionHandler(final Parent root) {
        this.clipboard = new Clipboard();
        this.mousePressedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectionHandler.this.doOnMousePressed(root, event);
                event.consume();
            }
        };
    }

    public EventHandler<MouseEvent> getMousePressedEventHandler() {
        return mousePressedEventHandler;
    }

    private void doOnMousePressed(Parent root, MouseEvent event) {
        Node target = (Node) event.getTarget();
        var chi = root.getChildrenUnmodifiable();
        if(target.equals(root))
            clipboard.unselectAll();
        if(root.getChildrenUnmodifiable().contains(target) && target instanceof SelectableNode) {
            SelectableNode selectableTarget = (SelectableNode) target;
            if(!clipboard.getSelectedItems().contains(selectableTarget))
                clipboard.unselectAll();
            clipboard.select(selectableTarget, true);
        }
    }

    /**
     * This class is based on jfxtras-labs
     *  <a href="https://github.com/JFXtras/jfxtras-labs/blob/8.0/src/main/java/jfxtras/labs/scene/control/window/Clipboard.java">Clipboard</a>
     *  and
     *  <a href="https://github.com/JFXtras/jfxtras-labs/blob/8.0/src/main/java/jfxtras/labs/util/WindowUtil.java">WindowUtil</a>
     */
    private class Clipboard {
        private ObservableList<SelectableNode> selectedItems = FXCollections.observableArrayList();

        public ObservableList<SelectableNode> getSelectedItems() {
            return selectedItems;
        }

        public boolean select(SelectableNode n, boolean selected) {
            if(n.requestSelection(selected)) {
                if (selected) {
                    selectedItems.add(n);
                } else {
                    selectedItems.remove(n);
                }
                n.notifySelection(selected);
                return true;
            } else {
                return false;
            }
        }

        public void unselectAll() {
            List<SelectableNode> unselectList = new ArrayList<>();
            unselectList.addAll(selectedItems);

            for (SelectableNode sN : unselectList) {
                select(sN, false);
            }
        }
    }
}
