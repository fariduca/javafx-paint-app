package sample;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import sample.Interfaces.SelectableNode;

public class MyOval extends Ellipse implements SelectableNode {
    private double _translateX;
    private double _translateY;

    public MyOval(double v, double v1, double v2, double v3) {
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
        setTranslateX(_translateX + dx - x);
        setTranslateY(_translateY + dy - y);
    }

    @Override
    public void notifyReleased() {
        _translateX = getTranslateX();
        _translateY = getTranslateY();
    }
}
