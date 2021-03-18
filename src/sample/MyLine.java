package sample;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import sample.Interfaces.SelectableNode;

public class MyLine extends Line implements SelectableNode {
    public MyLine(double v, double v1, double v2, double v3) {
        super(v, v1, v2, v3);
    }

    @Override
    public boolean requestSelection(boolean select) {
        return true;
    }

    @Override
    public void notifySelection(boolean select) {
        if(select) {
            DropShadow shadow = new DropShadow();
            shadow.setOffsetX(0f);
            shadow.setOffsetY(0f);
            shadow.setColor(Color.BLUE);
            shadow.setWidth(70);
            shadow.setHeight(70);
            this.setEffect(shadow);
        }
        else {
            this.setEffect(null);
        }
    }

    @Override
    public void notifyDragged(double x, double y, double dx, double dy) {
        this.setTranslateX(dx - x);
        this.setTranslateY(dy - y);
    }
}
