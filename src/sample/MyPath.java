package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import sample.Interfaces.SelectableNode;

public class MyPath extends Path implements SelectableNode {
    @Override
    public boolean requestSelection(boolean select) {
        return true;
    }

    @Override
    public void notifySelection(boolean select) {
        if(select)
            this.setFill(Color.RED);
        else
            this.setFill(Color.BLACK);
    }
}
