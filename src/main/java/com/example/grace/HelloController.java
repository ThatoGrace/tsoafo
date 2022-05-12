package com.example.grace;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class HelloController implements Initializable {
    @FXML
    private VBox vBox;

    @FXML
    private MediaView mVideo;
    private MediaPlayer mpvideo;
    private Media mediaVideo;

    @FXML
    private HBox hBox;

    @FXML
    private HBox volume;

    @FXML
    private Button stop;

    @FXML
    private Button play;

    @FXML
    private Button pause;


    @FXML
    private Label currentDur;


    @FXML
    private Slider slider;

    @FXML
    private Slider slidervolume;

    private boolean atEndOfVideo=false;
    private boolean isPlaying = true;
    private boolean isMuted = true;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mediaVideo = new Media(new File("C:\\Users\\Computer\\IdeaProjects\\grace\\src\\main\\resources\\play.mp4").toURI().toString());
        mpvideo = new MediaPlayer(mediaVideo);
        mVideo.setMediaPlayer(mpvideo);

        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button buttonPlay = (Button) actionEvent.getSource();
                if(atEndOfVideo){
                    slider.setValue(0);
                    atEndOfVideo=false;
                    isPlaying=false;
                }

                else {
                    mpvideo.play();
                    isPlaying=true;
                }
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(isPlaying){
                    mpvideo.pause();
                    isPlaying=false;

                }
            }
        });
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                mpvideo.stop();

            }
        });

        hBox.getChildren().remove(slidervolume);
        mpvideo.volumeProperty().bindBidirectional(slidervolume.valueProperty());

        bindcurrentDur();

        slidervolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mpvideo.setVolume(slidervolume.getValue());

                if(mpvideo.getVolume() != 0.0){
                    isMuted=false;
                }
                else {
                    isMuted=true;
                }
            }
        });
        slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                if(!isChanging){
                    mpvideo.seek(Duration.seconds(slider.getValue()));
                }
            }
        });
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double currentTime = mpvideo.getCurrentTime().toSeconds();
                if(Math.abs(currentTime-newValue.doubleValue())> 0.5){
                    mpvideo.seek(Duration.seconds(newValue.doubleValue()));
                }

            }
        });
        mpvideo.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                if(!slider.isValueChanging()){
                    slider.setValue(newTime.toSeconds());
                }
            }
        });
    }



    private void bindcurrentDur() {
        currentDur.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mpvideo.getCurrentTime());
            }
        },mpvideo.currentTimeProperty()));
    }

    private String getTime(Duration currentTime) {
        int hours = (int) currentTime.toHours();
        int minutes = (int) currentTime.toMinutes();
        int seconds = (int) currentTime.toSeconds();

        if(seconds>59) seconds=seconds%60;
        if(minutes>59) minutes=minutes%60;
        if(hours>59) hours=hours%60;

        if(hours>0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);

        else return String.format("%02d:%02d",
                minutes,
                seconds);
    }

}