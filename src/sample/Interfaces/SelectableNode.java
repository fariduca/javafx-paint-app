package sample.Interfaces;

/**
 * This interface is based on jfxtras-labs <a href="https://github.com/JFXtras/jfxtras-labs/blob/8.0/src/main/java/jfxtras/labs/scene/control/window/SelectableNode.java">SelectableNode</a>
 */
public interface SelectableNode {
    public boolean requestSelection(boolean select);

    public void notifySelection(boolean select);

    public void notifyDragged(double x, double y, double dx, double dy);

    public void notifyReleased();
}
