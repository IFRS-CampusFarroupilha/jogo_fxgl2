/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogozMenu;

import javafx.scene.layout.Pane;
import com.almasb.fxgl.texture.*;
import com.almasb.fxgl.app.*;
import java.io.File;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jogoz.JogozApp;

/**
 *
 * @author pedro
 */
public class JogozDisplay {

    public static Pane content(int fontSize, String... linesOfMessage) {
        Texture backgroundBox = FXGL.getAssetLoader().loadTexture("IceCreamDialog.png");

        backgroundBox.setX(-305 - (backgroundBox.getWidth() * (1 - 610 / backgroundBox.getWidth())) / 2);
        backgroundBox.setY(-122 - (backgroundBox.getHeight() * (1 - 238 / backgroundBox.getHeight())) / 2);
        backgroundBox.setScaleX(610 / backgroundBox.getWidth());
        backgroundBox.setScaleY(238 / backgroundBox.getHeight());

        Pane jogozPane = new Pane(backgroundBox);
        jogozPane.setMaxSize(0, 0);

        ArrayList<Text> linhas = new ArrayList();
        int i = 0;

        for (String string : linesOfMessage) {
            linhas.add(new Text(string));
            linhas.get(i).setFill(Color.rgb(247, 150, 161));
            linhas.get(i).setFont(Font.font("Cooper Black", FontWeight.BOLD, fontSize));
            linhas.get(i).setX(-linhas.get(i).getLayoutBounds().getWidth() / 2);
            linhas.get(i).setY(-linhas.get(i).getLayoutBounds().getHeight() * linesOfMessage.length / 2 + linhas.get(i).getLayoutBounds().getHeight() * i + 10);
            linhas.get(i).setStyle("-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 2 );");

            jogozPane.getChildren().add(linhas.get(i));
            i++;
        }

        return jogozPane;
    }

    //TODO: auto mostrar dialog
    public static Pane content(String... linesOfMessage) {
        return content(28, linesOfMessage);
    }

    public static Button button(String texto, EventHandler<ActionEvent> onAction) {
        Button btn = new Button(texto);
        btn.setBackground(Background.EMPTY);
        btn.setTextFill(Color.LIGHTBLUE);
        btn.setFont(Font.font("Cooper Black", FontWeight.BOLD, 25));
        btn.setStyle("-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 2 );");
        btn.setOnAction(onAction);
        
        btn.setOnMouseEntered(event -> {
            btn.setTextFill(Color.DARKBLUE);
            FXGL.getApp().getAudioPlayer().playSound(FXGL.getApp().getSettings().getSoundMenuSelect());
        });
        
        btn.setOnMouseExited(event -> {
            btn.setTextFill(Color.LIGHTBLUE);
        });
        return btn;
    }

    public static Button button(String texto) {
        return button(texto, event -> {
            FXGL.getApp().getAudioPlayer().playSound(FXGL.getApp().getSettings().getSoundMenuPress());
        });
    }

    public static void showMessageBox(String buttonText, String... linesOfMessage) {
        showMessageBox(buttonText, event -> {} ,linesOfMessage);
    }
    
    public static void showMessageBox(String buttonText, EventHandler<ActionEvent> onAction, String... linesOfMessage){
        FXGL.getApp().getDisplay().showBox("whatever",
                content(linesOfMessage),
                button(buttonText, onAction));
    }
}
