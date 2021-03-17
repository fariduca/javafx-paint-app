package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
    private Canvas canvas, canvasGo;

    private ToolsEnum selectedTool = ToolsEnum.PEN;
    private PenSize radius = PenSize.MEDIUM;
    private Paint brushColor;
    private int red, green, blue;
    private double alpha;
    private GraphicsContext graphicsContextEff, graphicsContext;
    private SelectionHandler selectionHandler;

    public void initialize() {
        graphicsContextEff = canvasGo.getGraphicsContext2D();
        graphicsContext = canvas.getGraphicsContext2D();

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

        selectionHandler = new SelectionHandler(canvas.getParent());
        canvas.getParent().addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());
    }

    @FXML
    void clearButtonPressed(ActionEvent event) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContextEff.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    Path path = null;

    @FXML
    void canvasOnMousePressed(MouseEvent event) {
        this.startX = event.getX();
        this.startY = event.getY();
        this.oldX = event.getX();
        this.oldY = event.getY();

        if (selectedTool == ToolsEnum.PEN) {
            path = new MyPath();
            path.setStrokeWidth(radius.getRadius());
            path.setStroke(brushColor);
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
                graphicsContext.setLineWidth(radius.getRadius());
                graphicsContext.setStroke(brushColor);
                graphicsContext.strokeLine(oldX, oldY, lastX, lastY);

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
            case ERASER:
                graphicsContext.setFill(Color.rgb(255,255,255));
                graphicsContext.fillOval(lastX-radius.getRadius(), lastY-radius.getRadius(),
                        radius.getRadius()*2, radius.getRadius()*2);
                MyOval eras = new MyOval(lastX-radius.getRadius(), lastY-radius.getRadius(),
                        radius.getRadius()*2, radius.getRadius()*2);
                eras.setFill(Color.WHITE);
                //undoHistory.push(eras);
                break;
        }
    }

    @FXML
    void canvasOnMouseReleased(MouseEvent event) {
        graphicsContextEff.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (lastX != 0 && lastY != 0) {
            double wh,hg;
            switch (selectedTool) {
                case PEN:
                    undoHistory.push(path);
                    break;
                case LINE:
                    graphicsContext.setLineWidth(radius.getRadius());
                    graphicsContext.setStroke(brushColor);
                    graphicsContext.strokeLine(startX, startY, lastX, lastY);

                    MyLine ln = new MyLine(startX, startY, lastX, lastY);
                    ln.setStrokeWidth(radius.getRadius());
                    ln.setStroke(brushColor);
                    undoHistory.push(ln);

                    break;
                case RECTANGLE:
                    wh = Math.abs(lastX - startX);
                    hg = Math.abs(lastY - startY);
                    graphicsContext.setLineWidth(radius.getRadius());

                    MyRectangle rect = new MyRectangle(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    rect.setStrokeWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        graphicsContext.setFill(brushColor);
                        graphicsContext.fillRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                        rect.setFill(brushColor);
                        rect.setStroke(Color.TRANSPARENT);
                    }else{
                        graphicsContext.setStroke(brushColor);
                        graphicsContext.strokeRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                        rect.setStroke(brushColor);
                        rect.setFill(Color.TRANSPARENT);
                    }

                    undoHistory.push(rect);
                    break;
                case OVAL:
                    wh = Math.abs(lastX - startX);
                    hg = Math.abs(lastY - startY);
                    graphicsContext.setLineWidth(radius.getRadius());

                    MyOval oval = new MyOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    oval.setStrokeWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        graphicsContext.setFill(brushColor);
                        graphicsContext.fillOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);

                        oval.setFill(brushColor);
                        oval.setStroke(Color.TRANSPARENT);
                    }else{
                        graphicsContext.setStroke(brushColor);
                        graphicsContext.strokeOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);

                        oval.setStroke(brushColor);
                        oval.setFill(Color.TRANSPARENT);
                    }
                    undoHistory.push(oval);
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
        canvas.setMouseTransparent(selectedTool == ToolsEnum.SELECT);
    }

    @FXML
    void undoButtonPressed(ActionEvent event) {
        if (!undoHistory.empty()){
            graphicsContext.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
            undoHistory.pop();

            for(int i=0; i < undoHistory.size(); i++) {
                Shape shape = undoHistory.elementAt(i);

                graphicsContext.setLineWidth(shape.getStrokeWidth());
                graphicsContext.setStroke(shape.getStroke());
                graphicsContext.setFill(shape.getFill());

                if (shape.getClass() == MyLine.class) {
                    Line temp = (Line) shape;
                    graphicsContext.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                }
                else if(shape.getClass() == MyRectangle.class) {
                    Rectangle temp = (Rectangle) shape;

                    graphicsContext.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                    graphicsContext.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                }
                else if(shape.getClass() == MyOval.class) {
                    Ellipse temp = (Ellipse) shape;

                    graphicsContext.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                    graphicsContext.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                }
                else if (shape.getClass() == MyPath.class) {
                    Path path = (Path) shape;
                    graphicsContext.beginPath();
                    MoveTo m = (MoveTo) path.getElements().get(0);
                    graphicsContext.moveTo(m.getX(), m.getY());
                    for (int k = 1; k < path.getElements().size(); k++) {
                        LineTo l = (LineTo) path.getElements().get(k);
                        graphicsContext.lineTo(l.getX(), l.getY());
                    }
                    graphicsContext.stroke();
                }
            }
        }
    }
}
