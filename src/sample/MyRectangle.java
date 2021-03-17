package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.Interfaces.SelectableNode;

public class MyRectangle extends Rectangle implements SelectableNode {
    public MyRectangle(double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
    }

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
