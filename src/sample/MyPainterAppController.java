package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.util.Stack;

public class MyPainterAppController {
    private enum PenSize {
        SMALL(2),
        MEDIUM(4),
        LARGE(6);
        private final int radius;

        PenSize(int radius) { this.radius = radius; }
        public int getRadius() { return radius; }
    }

    private enum ToolsEnum {
        PEN(),
        LINE(),
        RECTANGLE(),
        OVAL(),
        ERASER(),
        SELECT()
    }

    double startX, startY, lastX,lastY,oldX,oldY;

    Stack<Shape> undoHistory = new Stack<>();

    @FXML
    private TextField redTextField;

    @FXML
    private TextField greenTextField;

    @FXML
    private TextField blueTextField;

    @FXML
    private TextField alphaTextField;

    @FXML
    private Slider redSlider;

    @FXML
    private Slider greenSlider;

    @FXML
    private Slider blueSlider;

    @FXML
    private Slider alphaSlider;

    @FXML
    private RadioButton smallRadioButton;

    @FXML
    private ToggleGroup sizeToggleGroup;

    @FXML
    private RadioButton mediumRadioButton;

    @FXML
    private RadioButton largeRadioButton;

    @FXML
    private RadioButton shapeRadioPen;

    @FXML
    private ToggleGroup shapeToggleGroup;

    @FXML
    private RadioButton shapeRadioLine;

    @FXML
    private RadioButton shapeRadioRectangle;

    @FXML
    private RadioButton shapeRadioOval;

    @FXML
    private RadioButton eraserRadioButton;

    @FXML
    private RadioButton selectRadioButton;

    @FXML
    private RadioButton strokeRadioButton;

    @FXML
    private RadioButton fillRadioButton;

    @FXML
    private Rectangle brushColorWatch;

    @FXML
    private Button undoButton;

    @FXML
    private Button clearButton;

    @FXML
    private Canvas canvasGo;

    @FXML
    private AnchorPane anchorPane;

    private ToolsEnum selectedTool = ToolsEnum.PEN;
    private PenSize radius = PenSize.MEDIUM;
    private Paint brushColor;
    private int red, green, blue;
    private double alpha;
    private GraphicsContext graphicsContextEff;
    private SelectionHandler selectionHandler;
    private Path path = null;

    public void initialize() {
        selectionHandler = new SelectionHandler(anchorPane);
        graphicsContextEff = canvasGo.getGraphicsContext2D();

        smallRadioButton.setUserData(PenSize.SMALL);
        mediumRadioButton.setUserData(PenSize.MEDIUM);
        largeRadioButton.setUserData(PenSize.LARGE);

        shapeRadioPen.setUserData(ToolsEnum.PEN);
        shapeRadioLine.setUserData(ToolsEnum.LINE);
        shapeRadioRectangle.setUserData(ToolsEnum.RECTANGLE);
        shapeRadioOval.setUserData(ToolsEnum.OVAL);
        eraserRadioButton.setUserData(ToolsEnum.ERASER);
        selectRadioButton.setUserData(ToolsEnum.SELECT);

        mediumRadioButton.setSelected(true);
        shapeRadioPen.setSelected(true);
        strokeRadioButton.setSelected(true);

        redTextField.textProperty().bind(
                redSlider.valueProperty().asString("%.0f"));
        greenTextField.textProperty().bind(
                greenSlider.valueProperty().asString("%.0f"));
        blueTextField.textProperty().bind(
                blueSlider.valueProperty().asString("%.0f"));
        alphaTextField.textProperty().bind(
                alphaSlider.valueProperty().asString("%.2f"));

        red = redSlider.valueProperty().getValue().intValue();
        green = greenSlider.valueProperty().getValue().intValue();
        blue = blueSlider.valueProperty().getValue().intValue();
        alpha = alphaSlider.valueProperty().getValue();
        brushColor = Color.rgb(red, green, blue, alpha);
        brushColorWatch.setFill(brushColor);

        redSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number number, Number t1) {
                int _red = t1.intValue();
                green = greenSlider.valueProperty().getValue().intValue();
                blue = blueSlider.valueProperty().getValue().intValue();
                alpha = alphaSlider.valueProperty().getValue();
                brushColor = Color.rgb(_red, green, blue, alpha);
                brushColorWatch.setFill(brushColor);
            }
        });

        greenSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number number, Number t1) {
                int _green = t1.intValue();
                red = redSlider.valueProperty().getValue().intValue();
                blue = blueSlider.valueProperty().getValue().intValue();
                alpha = alphaSlider.valueProperty().getValue();
                brushColor = Color.rgb(red, _green, blue, alpha);
                brushColorWatch.setFill(brushColor);
            }
        });

        blueSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number number, Number t1) {
                int _blue = t1.intValue();
                red = redSlider.valueProperty().getValue().intValue();
                green = greenSlider.valueProperty().getValue().intValue();
                alpha = alphaSlider.valueProperty().getValue();
                brushColor = Color.rgb(red, green, _blue, alpha);
                brushColorWatch.setFill(brushColor);
            }
        });

        alphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue,
                                Number number, Number t1) {
                double _alpha = t1.doubleValue();
                red = redSlider.valueProperty().getValue().intValue();
                green = greenSlider.valueProperty().getValue().intValue();
                blue = blueSlider.valueProperty().getValue().intValue();
                brushColor = Color.rgb(red, green, blue, _alpha);
                brushColorWatch.setFill(brushColor);
            }
        });
    }

    @FXML
    void clearButtonPressed(ActionEvent event) {
        anchorPane.getChildren().removeAll(undoHistory);
    }

    @FXML
    void canvasOnMousePressed(MouseEvent event) {
        this.startX = event.getX();
        this.startY = event.getY();
        this.oldX = event.getX();
        this.oldY = event.getY();

        if (selectedTool == ToolsEnum.PEN || selectedTool == ToolsEnum.ERASER) {
            path = new MyPath();
            path.setStrokeWidth(radius.getRadius());
            path.setStroke(selectedTool == ToolsEnum.PEN ? brushColor : Color.WHITE);
            path.getElements().add(new MoveTo(startX, startY));
        }
    }

    @FXML
    void canvasOnMouseDragged(MouseEvent event) {
        this.lastX = event.getX();
        this.lastY = event.getY();
        double wh,hg;

        switch (selectedTool) {
            case LINE:
                graphicsContextEff.setLineWidth(radius.getRadius());
                graphicsContextEff.setStroke(brushColor);
                graphicsContextEff.clearRect(0,0, canvasGo.getWidth(), canvasGo.getHeight());
                graphicsContextEff.strokeLine(startX, startY, lastX, lastY);
                break;
            case PEN:
            case ERASER:
                graphicsContextEff.setLineWidth(radius.getRadius());
                graphicsContextEff.setStroke(selectedTool == ToolsEnum.PEN ? brushColor : Color.WHITE);
                graphicsContextEff.strokeLine(oldX, oldY, lastX, lastY);

                path.getElements().add(new LineTo(lastX, lastY));

                oldX = lastX;
                oldY = lastY;
                break;
            case RECTANGLE:
                wh = Math.abs(lastX - startX);
                hg = Math.abs(lastY - startY);
                graphicsContextEff.setLineWidth(radius.getRadius());

                if(fillRadioButton.isSelected()){
                    graphicsContextEff.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
                    graphicsContextEff.setFill(brushColor);
                    graphicsContextEff.fillRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                }else{
                    graphicsContextEff.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
                    graphicsContextEff.setStroke(brushColor);
                    graphicsContextEff.strokeRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg );
                }
                break;
            case OVAL:
                wh = Math.abs(lastX - startX);
                hg = Math.abs(lastY - startY);
                graphicsContextEff.setLineWidth(radius.getRadius());

                if(fillRadioButton.isSelected()){
                    graphicsContextEff.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
                    graphicsContextEff.setFill(brushColor);
                    graphicsContextEff.fillOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                }else{
                    graphicsContextEff.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
                    graphicsContextEff.setStroke(brushColor);
                    graphicsContextEff.strokeOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg );
                }
                break;
        }
    }

    @FXML
    void canvasOnMouseReleased(MouseEvent event) {
        graphicsContextEff.clearRect(0, 0, canvasGo.getWidth(), canvasGo.getHeight());
        if (lastX != 0 && lastY != 0) {
            double wh,hg;
            switch (selectedTool) {
                case PEN:
                case ERASER:
                    undoHistory.push(path);
                    anchorPane.getChildren().add(path);
                    break;
                case LINE:
                    MyLine ln = new MyLine(startX, startY, lastX, lastY);
                    ln.setStrokeWidth(radius.getRadius());
                    ln.setStroke(brushColor);

                    anchorPane.getChildren().add(ln);
                    undoHistory.push(ln);
                    break;
                case RECTANGLE:
                    wh = Math.abs(lastX - startX);
                    hg = Math.abs(lastY - startY);

                    MyRectangle rect = new MyRectangle(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    rect.setStrokeWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        rect.setFill(brushColor);
                        rect.setStroke(Color.TRANSPARENT);
                    }else{
                        rect.setStroke(brushColor);
                        rect.setFill(Color.TRANSPARENT);
                    }

                    undoHistory.push(rect);
                    anchorPane.getChildren().add(rect);
                    break;
                case OVAL:
                    wh = Math.abs(lastX - startX)/2;
                    hg = Math.abs(lastY - startY)/2;

                    MyOval oval = new MyOval((startX+lastX)/2, (startY+lastY)/2, wh, hg);
                    oval.setStrokeWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        oval.setFill(brushColor);
                        oval.setStroke(Color.TRANSPARENT);
                    }else{
                        oval.setStroke(brushColor);
                        oval.setFill(Color.TRANSPARENT);
                    }
                    undoHistory.push(oval);
                    anchorPane.getChildren().add(oval);
                    break;
            }

            this.lastX = 0;
            this.lastY = 0;
        }
    }


    @FXML
    void sizeRadioButtonSelected(ActionEvent event) {
        radius = (PenSize) sizeToggleGroup.getSelectedToggle().getUserData();
    }

    @FXML
    void shapeRadioButtonSelected(ActionEvent event) {
        selectedTool = (ToolsEnum) shapeToggleGroup.getSelectedToggle().getUserData();

        canvasGo.setMouseTransparent(selectedTool == ToolsEnum.SELECT);
        if (selectedTool == ToolsEnum.SELECT) {
            anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
            anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, selectionHandler.getMouseDraggedEventHandler());
        }
        else {
            anchorPane.removeEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
            anchorPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, selectionHandler.getMouseDraggedEventHandler());
        }
    }

    @FXML
    void undoButtonPressed(ActionEvent event) {
        if (!undoHistory.empty()){
            anchorPane.getChildren().removeAll(undoHistory);
            undoHistory.pop();

            for(int i=0; i < undoHistory.size(); i++) {
                Shape shape = undoHistory.elementAt(i);

                if (shape.getClass() == MyLine.class) {
                    Line temp = (Line) shape;
                    anchorPane.getChildren().add(temp);
                }
                else if(shape.getClass() == MyRectangle.class) {
                    Rectangle temp = (Rectangle) shape;
                    anchorPane.getChildren().add(temp);
                }
                else if(shape.getClass() == MyOval.class) {
                    Ellipse temp = (Ellipse) shape;
                    anchorPane.getChildren().add(temp);
                }
                else if (shape.getClass() == MyPath.class) {
                    Path path = (Path) shape;
                    anchorPane.getChildren().add(path);
                }
            }
        }
    }
}
