package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class MyPainterAppController {
    private enum PenSize {
        SMALL(2),
        MEDIUM(4),
        LARGE(6);
        private final int radius;

        PenSize(int radius) { this.radius = radius; }
        public int getRadius() { return radius; }
    }

    private enum Shape {
        PEN(),
        LINE(),
        RECTANGLE(),
        OVAL(),
        ERASER()
    }

    double startX, startY, lastX,lastY,oldX,oldY;

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

    private Shape shape = Shape.PEN;
    private PenSize radius = PenSize.MEDIUM;
    private Paint brushColor;
    private int red, green, blue;
    private double alpha;
    private GraphicsContext graphicsContextEff, graphicsContext;

    public void initialize() {
        graphicsContextEff = canvasGo.getGraphicsContext2D();
        graphicsContext = canvas.getGraphicsContext2D();

        smallRadioButton.setUserData(PenSize.SMALL);
        mediumRadioButton.setUserData(PenSize.MEDIUM);
        largeRadioButton.setUserData(PenSize.LARGE);

        shapeRadioPen.setUserData(Shape.PEN);
        shapeRadioLine.setUserData(Shape.LINE);
        shapeRadioRectangle.setUserData(Shape.RECTANGLE);
        shapeRadioOval.setUserData(Shape.OVAL);
        eraserRadioButton.setUserData(Shape.ERASER);

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
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContextEff.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    void canvasOnMousePressed(MouseEvent event) {
        this.startX = event.getX();
        this.startY = event.getY();
        this.oldX = event.getX();
        this.oldY = event.getY();
    }

    @FXML
    void canvasOnMouseDragged(MouseEvent event) {
        this.lastX = event.getX();
        this.lastY = event.getY();
        double wh,hg;

        switch (shape) {
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
                graphicsContextEff.setFill(Color.rgb(255,255,255));
                graphicsContextEff.fillOval(lastX-radius.getRadius(), lastY-radius.getRadius(),
                    radius.getRadius()*2, radius.getRadius()*2);
                break;
        }
    }

    @FXML
    void canvasOnMouseReleased(MouseEvent event) {
        if (lastX != 0 && lastY != 0) {
            double wh,hg;
            switch (shape) {
                case LINE:
                    graphicsContext.setLineWidth(radius.getRadius());
                    graphicsContext.setStroke(brushColor);
                    graphicsContext.strokeLine(startX, startY, lastX, lastY);
                    break;
                case RECTANGLE:
                    wh = Math.abs(lastX - startX);
                    hg = Math.abs(lastY - startY);
                    graphicsContext.setLineWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        graphicsContext.setFill(brushColor);
                        graphicsContext.fillRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    }else{
                        graphicsContext.setStroke(brushColor);
                        graphicsContext.strokeRect(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    }
                    break;
                case OVAL:
                    wh = Math.abs(lastX - startX);
                    hg = Math.abs(lastY - startY);
                    graphicsContext.setLineWidth(radius.getRadius());

                    if(fillRadioButton.isSelected()){
                        graphicsContext.setFill(brushColor);
                        graphicsContext.fillOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    }else{
                        graphicsContext.setStroke(brushColor);
                        graphicsContext.strokeOval(Math.min(startX, lastX), Math.min(startY, lastY), wh, hg);
                    }
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
        shape = (Shape) shapeToggleGroup.getSelectedToggle().getUserData();
    }

    @FXML
    void undoButtonPressed(ActionEvent event) {
    }
}
