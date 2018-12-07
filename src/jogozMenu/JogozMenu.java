package jogozMenu;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.scene.menu.*;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.*;
import com.almasb.fxgl.util.*;
import com.almasb.fxgl.input.*;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import static com.almasb.fxgl.app.DSLKt.texture;
import static com.almasb.fxgl.app.FXGL.*;
import static com.almasb.fxgl.core.math.FXGLMath.random;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import jogoz.JogozApp;

public class JogozMenu extends FXGLMenu {

    private ParticleSystem particleSystem;
    private Text personalHighScore = getUIFactory().newText("a");

    public JogozMenu(GameApplication app, MenuType type) {
        super(app, type);

        getJogozApp().setMainMenuMusic(FXGL.getApp().getAssetLoader().loadMusic("battle.mp3"));
        getJogozApp().setGameMusic(getAssetLoader().loadMusic("field_of_hopes.mp3"));

        FXGL.getApp().getAudioPlayer().loopBGM("battle.mp3");
        FXGL.getApp().getAudioPlayer().setGlobalMusicVolume(0.10);

        setCursor("CursorIceCream.png", Point2D.ZERO);

        MenuBox menu = type == MenuType.MAIN_MENU
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        double menuX = app.getWidth() / 2 - 100;
        double menuY = app.getHeight() / 2 - menu.getLayoutHeight() / 2 - 125;

        menuRoot.setTranslateX(menuX);
        menuRoot.setTranslateY(menuY);

        contentRoot.setTranslateX(menuX);
        contentRoot.setTranslateY(menuY);

        // particle cherry
        Texture t = texture("Cherry.png", 128, 128).brighter().brighter();

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSourceImage(t.getImage());
        emitter.setSize(10, 20);
        emitter.setNumParticles(15);
        emitter.setEmissionRate(0.03);
        emitter.setVelocityFunction((i) -> new Point2D(random() * 2.5, -random(0.15, 1) * random(80, 120)));
        emitter.setExpireFunction((i) -> Duration.seconds(random(4, 6)));
        emitter.setScaleFunction((i) -> new Point2D(0.15, 0.15));
        emitter.setSpawnPointFunction((i) -> new Point2D(random(-70, app.getWidth()), 120));

        particleSystem = new ParticleSystem();
        particleSystem.addParticleEmitter(emitter, 0, app.getHeight());

        getContentRoot().getChildren().add(3, particleSystem.getPane());

        menuRoot.getChildren().addAll(menu);
        contentRoot.getChildren().add(EMPTY);

        activeProperty().addListener((observable, wasActive, isActive) -> {
            if (!isActive) {
                switchMenuTo(menu);
                switchMenuContentTo(EMPTY);
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        particleSystem.onUpdate(tpf);

        if (getJogozApp().isGameMusicPlaying()) {
            getJogozApp().setGameMusicPlaying(false);
            getAudioPlayer().pauseMusic(getJogozApp().getGameMusic());
        }

        if (!getJogozApp().isMenuMusicPlaying()) {
            getJogozApp().setMenuMusicPlaying(true);
            getAudioPlayer().playMusic(getJogozApp().getMainMenuMusic());
        }
    }

    public JogozApp getJogozApp() {
        return ((JogozApp) FXGL.getApp());
    }

    @Override
    protected Node createBackground(double width, double height) {
        Texture t = texture("IceCreamBackground.png");

        t.setX((width - t.getWidth()) / 2);
        t.setY((height - t.getHeight()) / 2);

        t.setScaleX(width / t.getWidth());
        t.setScaleY(height / t.getHeight());
        return t;
    }

    @Override
    protected Node createTitleView(String title) {
        Texture logoTexture = texture("IceCreamLogo.png");

        logoTexture.setScaleX((app.getWidth() - 50) / logoTexture.getWidth());
        logoTexture.setScaleY((app.getWidth() - 50) / logoTexture.getWidth());

        StackPane titleRoot = new StackPane();
        titleRoot.getChildren().addAll(logoTexture);

        titleRoot.setTranslateX(app.getWidth() / 2 - logoTexture.getWidth() / 2);
        titleRoot.setTranslateY(50);

        return titleRoot;
    }

    @Override
    protected Node createVersionView(String version) {
        Text view = FXGL.getUIFactory().newText(version);
        view.setTranslateY(app.getHeight() - 2);
        return view;
    }

    @Override
    protected Node createProfileView(String profileName) {
        Text view = FXGL.getUIFactory().newText(profileName);
        view.setTranslateY(app.getHeight() - 2);
        view.setTranslateX(app.getWidth() - view.getLayoutBounds().getWidth());

        if (profileName.equals("Profile: cheater") && !exists("Toggle Cheat Allowed")) {
            addCheats();
        }

        getJogozApp().setUser(profileName.substring(9));

        List<Node> listHighScoreViews = createHighScoreViews();

        personalHighScore.setText(((Text) listHighScoreViews.get(1)).getText());
        personalHighScore.setTranslateY(app.getHeight() - 16);
        personalHighScore.setFill(Color.PINK);

        getContentRoot().getChildren().addAll(listHighScoreViews.get(0), personalHighScore);

        return view;
    }

    private boolean exists(String userAction) {
        try {
            FXGL.getApp().getInput().getActionByName(userAction);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected MenuBox createMenuBodyMainMenu() {
        log.debug("createMenuBodyMainMenu()");

        MenuBox box = new MenuBox();
        box.setAlignment(Pos.CENTER);

        MenuButton itemNewGame = new MenuButton("menu.newGame");
        itemNewGame.setOnAction(e -> fireNewGame());
        box.add(itemNewGame);

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        MenuButton itemExit = new MenuButton("menu.exit");
        itemExit.setOnAction(e -> fireExit());
        box.add(itemExit);

        getJogozApp().setTextPersonalHighScoreMainMenu(personalHighScore);

        return box;
    }

    protected MenuBox createMenuBodyGameMenu() {
        log.debug("createMenuBodyGameMenu()");

        MenuBox box = new MenuBox();

        MenuButton itemResume = new MenuButton("menu.resume");
        itemResume.setOnAction(e -> fireResume());
        box.add(itemResume);

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        MenuButton itemExit = new MenuButton("menu.mainMenu");
        itemExit.setOnAction(e -> fireExitToMainMenu());
        box.add(itemExit);

        getJogozApp().setTextPersonalHighScoreGameMenu(personalHighScore);

        return box;
    }

    private MenuContent createContentJogozAudio() {
        MenuContent mc = createContentAudio(), mcJogoz = createContentJogoz();

        mc.setSpacing(10);
        mc.setLayoutX(-75);
        mc.setLayoutY(0);

        for (Node node : mc.getChildren()) {
            ((HBox) node).setAlignment(Pos.CENTER_LEFT);
        }

        ((Pane) mcJogoz.getChildren().get(0)).getChildren().add(mc);

        return mcJogoz;
    }

    private MenuContent createContentJogoz() {
        MenuContent mc = new MenuContent();

        Button btn = new Button("Voltar");
        btn.setOnAction(event -> {
            switchMenuContentTo(EMPTY);
        });
        btn.setLayoutX(- 90);
        btn.setLayoutY(- 140);

        Rectangle rct = new Rectangle(400, 600, Color.BLACK), rctGray = new Rectangle(410, 610, Color.GRAY);
        rct.setX(- 100);
        rct.setY(- 150);

        rctGray.setX(-105);
        rctGray.setY(-155);

        Pane pane = new Pane();
        pane.setMaxSize(0, 0);
        pane.getChildren().addAll(rctGray, rct, btn);

        mc.getChildren().add(pane);
        mc.setMaxSize(0, 0);

        return mc;
    }

    private MenuContent createContentJogozControls() {
        MenuContent mc = createContentControls(), mcJogoz = createContentJogoz();

        mc.setLayoutX(-75);
        mc.setLayoutY(0);

        ((Pane) mcJogoz.getChildren().get(0)).getChildren().add(mc);

        return mcJogoz;
    }

    protected MenuBox createOptionsMenu() {
        log.debug("createOptionsMenu()");

//        MenuButton itemGameplay = new MenuButton("menu.gameplay");
//        itemGameplay.setMenuContent(this::createContentGameplay);
        MenuButton itemControls = new MenuButton("menu.controls");
        itemControls.setMenuContent(this::createContentJogozControls);

//        MenuButton itemVideo = new MenuButton("menu.video");
//        itemVideo.setMenuContent(this::createContentVideo);
        MenuButton itemAudio = new MenuButton("menu.audio");
        itemAudio.setMenuContent(this::createContentJogozAudio);

        MenuButton btnRestore = new MenuButton("menu.restore");
        btnRestore.setOnAction(e -> {
            app.getDisplay().showConfirmationBox(FXGL.getLocalizedString("menu.settingsRestore"), yes -> {
                if (yes) {
                    switchMenuContentTo(EMPTY);
                    listener.restoreDefaultSettings();
                    getAudioPlayer().setGlobalMusicVolume(0.10);
                }
            });
        });

        return new MenuBox(itemControls, itemAudio, btnRestore);
    }

    @Override
    protected void switchMenuTo(Node menu) {
        Node oldMenu = menuRoot.getChildren().get(0);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), oldMenu);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            menu.setOpacity(0);
            menuRoot.getChildren().set(0, menu);
            oldMenu.setOpacity(1);

            FadeTransition ft2 = new FadeTransition(Duration.seconds(0.33), menu);
            ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    @Override
    protected void switchMenuContentTo(Node content) {
        contentRoot.getChildren().set(0, content);
    }

    private static class MenuBox extends VBox {

        MenuBox(MenuButton... items) {

            for (MenuButton item : items) {
                add(item);
            }

            setSpacing(20);
        }

        void add(MenuButton item) {
            item.setParent(this);
            getChildren().addAll(item);
        }

        double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    private class MenuButton extends Pane {

        private MenuBox parent;
        private MenuContent cachedContent = null;

        private Polygon p = new Polygon(-63, -3, 263, -3, 263, 43, -63, 43);
        private FXGLButton btn;

        MenuButton(String stringKey) {
            btn = new FXGLButton();
            btn.setAlignment(Pos.CENTER);
            btn.setStyle("-fx-text-fill: #D700BD;"
                    + "-fx-background-color: transparent;"
                    + "-fx-font: 24px \"Cooper Black\";"
                    + "-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 2 );");
            btn.textProperty().bind(localizedStringProperty(stringKey));
            btn.setFocusTraversable(true);
            btn.setScaleX(2);
            btn.setScaleY(2);

            p.setMouseTransparent(true);

            Paint g = new RadialGradient(0, 0, 0.5, 0.5, 0.35, true, CycleMethod.REPEAT,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(1, Color.TRANSPARENT),
                    new Stop(0.5, Color.color(1, 0, 0, 0.2)));

            p.fillProperty().bind(
                    Bindings.when(btn.pressedProperty()).then((Paint) Color.color(1, 0.8, 1, 0.75)).otherwise(g)
            );

//            p.setStroke(Color.color(0.1, 0.1, 0.1, 0.15));
//            p.setEffect(new GaussianBlur());
            // TODO: hover and/or focused?
            p.visibleProperty().bind(btn.hoverProperty());

            getChildren().addAll(p, btn);
        }

        public void setOnAction(EventHandler<ActionEvent> e) {
            btn.setOnAction(e);
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(Supplier<MenuContent> contentSupplier) {

            btn.addEventHandler(ActionEvent.ACTION, event -> {
                if (cachedContent == null) {
                    cachedContent = contentSupplier.get();
                }

                switchMenuContentTo(cachedContent);
            });
        }

        public void setChild(MenuBox menu) {
            MenuButton back = new MenuButton("menu.back");
            menu.getChildren().add(0, back);

            back.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(MenuButton.this.parent));

            btn.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(menu));
        }
    }

    @Override
    protected Button createActionButton(String name, Runnable action) {
        MenuButton btn = new MenuButton(name);
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn.btn;
    }

    @Override
    protected Button createActionButton(StringBinding name, Runnable action) {
        MenuButton btn = new MenuButton(name.getValue());
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn.btn;
    }

    private void addCheats() {
        Input input = FXGL.getApp().getInput();

        input.addAction(new UserAction("Toggle Cheat Allowed") {
            @Override
            protected void onActionBegin() {
                getGameState().setValue("Debug", !getGameState().getBoolean("Debug"));
            }
        }, KeyCode.B);

        input.addAction(new UserAction("IncrementCoins") {
            @Override
            protected void onActionBegin() {
                if (getGameState().getBoolean("Debug")) {
                    getGameState().increment("Coins", +10);
                }
            }
        }, KeyCode.DIGIT1);

        input.addAction(new UserAction("DecrementCoins") {
            @Override
            protected void onActionBegin() {
                getJogozApp().decrementTails();
            }
        }, KeyCode.DIGIT2);

        input.addAction(new UserAction("ToggleCoins") {
            @Override
            protected void onActionBegin() {
                getJogozApp().toggleSpawnCoins();
            }
        }, KeyCode.DIGIT3);

        input.addAction(new UserAction("SpeedUp") {
            @Override
            protected void onActionBegin() {
                if (getGameState().getBoolean("Debug")) {
                    getGameState().increment("MoveSpeed", +1);
                }
            }
        }, KeyCode.DIGIT4);

        input.addAction(new UserAction("SpeedDown") {
            @Override
            protected void onActionBegin() {
                if (getGameState().getBoolean("Debug")) {
                    getGameState().increment("MoveSpeed", -1);
                }
            }
        }, KeyCode.DIGIT5);

        input.addAction(new UserAction("ToggleInvincibility") {
            @Override
            protected void onActionBegin() {
                if (getGameState().getBoolean("Debug")) {
                    getGameState().setValue("Invincible", !getGameState().getBoolean("Invincible"));
                }
            }
        }, KeyCode.DIGIT6);

        input.addAction(new UserAction("ToggleSlowMotion") {
            @Override
            protected void onActionBegin() {
                if (getGameState().getBoolean("Debug")) {
                    getJogozApp().toggleSlowMotion();
                }
            }
        }, KeyCode.DIGIT7);
    }

    private List<Node> createHighScoreViews() {
        File recordes = new File("HighScores.txt");
        int personalHighScore = 0, highScoreGeral = 0;
        List<String> linhasLidas = null;

        if (!recordes.exists()) {
            try {
                recordes.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(JogozApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            linhasLidas = getJogozApp().leitura_linhas(recordes.getName());
        } catch (IOException ex) {
            Logger.getLogger(JogozApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < linhasLidas.size(); i++) {
            if (highScoreGeral < Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1))) {
                highScoreGeral = Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1));
            }

            if (linhasLidas.get(i).substring(0, linhasLidas.get(i).lastIndexOf(":")).equals(getJogozApp().getUser())) {
                personalHighScore = Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1));
            }
        }

        List<Node> recordesText = new ArrayList();

        Text personal = FXGL.getUIFactory().newText("Recorde pessoal: " + personalHighScore),
                geral = FXGL.getUIFactory().newText("Recorde geral: " + highScoreGeral);

        personal.setTranslateY(app.getHeight() - 16);
        personal.setFill(Color.PINK);

        geral.setTranslateY(app.getHeight() - 30);
        geral.setFill(Color.PINK);

        recordesText.add(geral);
        recordesText.add(personal);

        return recordesText;
    }
}
