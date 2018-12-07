package jogoz;

import com.almasb.fxgl.app.*;
import com.almasb.fxgl.audio.*;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.*;
import com.almasb.fxgl.entity.components.*;
import com.almasb.fxgl.physics.*;
import com.almasb.fxgl.core.math.*;
import com.almasb.fxgl.time.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import static javafx.application.Application.launch;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import jogoz.JogozComponents.PlayerControl;
import jogoz.JogozComponents.TrailComponent;
import jogozMenu.JogozDisplay;
import jogozMenu.JogozSceneFactory;

/**
 *
 * @author Pedro Thomaz Conzatti Martins
 */
public class JogozApp extends GameApplication {

    //entidades: player é o jogador, e os outros são cópias para simular a passagem parcial do jogador entre os lados da tela
    private Entity player, x, y, xy;

    //constantes utilizadas que são double
    private static final double SLOW_MOTION_MODIFIER = 0.02, ZOOM = 2.5;

    //constante significando a partir de qual "trail" que será contada a colisão
    private static final int LIMITE_COLIDIVEL = 13;

    //Variaveis para medir se uma parte do mundo ficará visivel quando for realizado o zoom na animação de morte
    private int limiteVisivelRightX,
            limiteVisivelBottomY,
            limiteVisivelX,
            limiteVisivelY;

    //variavel para definir a cor de cada "trail"
    private short color = 0;

    //valores booleanos
    private boolean slowMo = false, gameMusicPlaying = false, menuMusicPlaying = true;

    //Strings utilizadas para chamar a textura certa para cada "trail", e para armazenar o nome do usuário
    private String cor = "red", user;

    //Texts usados na UI
    private Text textX = new Text(), textY = new Text(), textCherrys = new Text("Cherrys: 0"), textCoins = new Text(), textSpeed = new Text(), textCoinsLabel = new Text("Coins:"), textSpeedLabel = new Text("Speed:");

    private Text textPersonalHighScoreMainMenu, textPersonalHighScoreGameMenu;

    //timer usado para spawnar moedas e dar animação à elas
    private TimerAction spawnCoins;

    //Musicas
    private Music mainMenuMusic, gameMusic;

    //Lista para tornar os controles mais intuitivos
    private ArrayList<String> ultimaDirecaoSelecionada = new ArrayList();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(900);
        settings.setHeight(900);
        settings.setTitle("Sorvetinho Adventures");
        settings.setVersion("1.0");
        settings.setMenuEnabled(true);
        settings.setApplicationMode(ApplicationMode.RELEASE);
        settings.setFullScreenAllowed(false);
        settings.setSceneFactory(new JogozSceneFactory());
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.getBindings().clear();

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                if (ultimaDirecaoEquals("Up", "Right")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 7);
                } else if (ultimaDirecaoEquals("Down", "Right")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 1);
                } else {
                    player.getComponent(PlayerControl.class).setDirection((short) 0);
                }

                ultimaDirecaoSelecionada.add("Right");
            }

            @Override
            protected void onActionEnd() {
                ultimaDirecaoSelecionada.remove("Right");
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                if (ultimaDirecaoEquals("Up", "Left")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 5);
                } else if (ultimaDirecaoEquals("Down", "Left")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 3);
                } else {
                    player.getComponent(PlayerControl.class).setDirection((short) 4);
                }

                ultimaDirecaoSelecionada.add("Left");
            }

            @Override
            protected void onActionEnd() {
                ultimaDirecaoSelecionada.remove("Left");
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                if (ultimaDirecaoEquals("Left", "Up")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 5);
                } else if (ultimaDirecaoEquals("Right", "Up")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 7);
                } else {
                    player.getComponent(PlayerControl.class).setDirection((short) 6);
                }

                ultimaDirecaoSelecionada.add("Up");
            }

            @Override
            protected void onActionEnd() {
                ultimaDirecaoSelecionada.remove("Up");
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                if (ultimaDirecaoEquals("Left", "Down")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 3);
                } else if (ultimaDirecaoEquals("Right", "Down")) {
                    player.getComponent(PlayerControl.class).setDirection((short) 1);
                } else {
                    player.getComponent(PlayerControl.class).setDirection((short) 2);
                }

                ultimaDirecaoSelecionada.add("Down");
            }

            @Override
            protected void onActionEnd() {
                ultimaDirecaoSelecionada.remove("Down");
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Toggle Advanced Stats") {
            @Override
            protected void onActionBegin() {
                toggleAdvancedStats();
            }
        }, KeyCode.F3);
    }

    private boolean ultimaDirecaoEquals(String tested, String tester) {
        if (ultimaDirecaoSelecionada.size() == 0) {
            return false;
        }

        switch (tester) {
            case "Left":
                if (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 1).equals("Right") && ultimaDirecaoSelecionada.size() != 1) {
                    return (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 2).equals(tested));
                }
                break;
            case "Right":
                if (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 1).equals("Left") && ultimaDirecaoSelecionada.size() != 1) {
                    return (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 2).equals(tested));
                }
                break;
            case "Up":
                if (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 1).equals("Down") && ultimaDirecaoSelecionada.size() != 1) {
                    return (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 2).equals(tested));
                }
                break;
            case "Down":
                if (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 1).equals("Up") && ultimaDirecaoSelecionada.size() != 1) {
                    return (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 2).equals(tested));
                }
                break;
        }

        return (ultimaDirecaoSelecionada.get(ultimaDirecaoSelecionada.size() - 1).equals(tested));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("Coins", 0);
        vars.put("CoinsOnWorld", 0);
        vars.put("Debug", false);
        vars.put("MoveSpeed", 5);
        vars.put("Invincible", false);
    }

    @Override
    protected void initGame() {
        textCherrys.setText("Cherrys: 0");

        getAudioPlayer().resumeMusic(gameMusic);
        getAudioPlayer().stopMusic(gameMusic);
        getAudioPlayer().stopMusic(mainMenuMusic);

        getAudioPlayer().loopBGM("field_of_hopes.mp3");
        gameMusicPlaying = true;

        getGameWorld().addEntityFactory(new JogozFactory());

        player = getGameWorld().spawn("player", ((getWidth() - 70) / 2), Math.round((getHeight() - 100) / 2));
        player.addComponent(new PlayerControl(SLOW_MOTION_MODIFIER));

        x = getGameWorld().spawn("player", -getWidth() * 2, -getHeight() * 2);

        y = getGameWorld().spawn("player", -getWidth() * 2, -getHeight() * 2);

        xy = getGameWorld().spawn("player", -getWidth() * 2, -getHeight() * 2);

        limiteVisivelRightX = getWidth() - (int) Math.round(getWidth() / ZOOM);
        limiteVisivelBottomY = getHeight() - (int) Math.round(getHeight() / ZOOM);
        limiteVisivelX = (int) Math.round(getWidth() / ZOOM);
        limiteVisivelY = (int) Math.round(getHeight() / ZOOM);

        for (Entity entity : getGameWorld().getEntitiesByType(JogozType.PLAYER)) {
            addIceCreamShapedHitBox(entity);
        }

        getMasterTimer().runAtInterval(() -> {
            if (!slowMo) {
                trailsUpdate();
            }
        }, Duration.millis(50));

        getMasterTimer().runAtInterval(() -> {
            if (slowMo) {
                trailsUpdate();
            }
        }, Duration.millis(50 / SLOW_MOTION_MODIFIER));

        spawnCoins = coinsSpawnAndAnimationTimer();
        getGameState().setValue("Coins", 0);
        getGameState().setValue("CoinsOnWorld", 0);
        getGameState().setValue("MoveSpeed", 5);
        getGameState().setValue("Invincible", false);
        player.getComponent(PlayerControl.class).setDirection((short) 0);
        slowMo = false;

        getGameScene().getViewport().unbind();
        getGameScene().getViewport().setX(0);
        getGameScene().getViewport().setY(0);
        getGameScene().getViewport().setZoom(1);

        player.getViewComponent().setAnimatedTexture(new AnimatedTexture(new AnimationChannel("IceCream2.png", 2, 70, 100, Duration.millis(1750), 0, 1)), true, false);
    }

    public void addIceCreamShapedHitBox(Entity entity) {
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(25, 0), BoundingShape.box(20, 95)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(15, 5), BoundingShape.box(40, 75)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(30, 95), BoundingShape.box(10, 5)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(20, 80), BoundingShape.box(30, 10)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(10, 10), BoundingShape.box(50, 60)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(5, 15), BoundingShape.box(60, 45)));
        entity.getBoundingBoxComponent()
                .addHitBox(new HitBox(new Point2D(0, 25), BoundingShape.box(70, 20)));
    }

    private void trailsUpdate() {
        switch (color) {
            case 0:
            case 1:
                cor = "Red";
                color++;
                break;
            case 2:
            case 3:
                cor = "Orange";
                color++;
                break;
            case 4:
            case 5:
                cor = "Yellow";
                color++;
                break;
            case 6:
            case 7:
                cor = "Green";
                color++;
                break;
            case 8:
            case 9:
                cor = "Blue";
                color++;
                break;
            case 10:
            case 11:
                cor = "Anil";
                color++;
                break;
            case 12:
                cor = "Pink";
                color++;
                break;
            case 13:
                cor = "Pink";
                color = 0;
                break;
        }

        Entity trail = new Entity();

        for (Entity players : getGameWorld().getEntitiesByType(JogozType.PLAYER)) {
            if (players.getRightX() > -limiteVisivelX && players.getBottomY() > -limiteVisivelY) {

                trail = getGameWorld().spawn("Trail",
                        players.getX(),
                        players.getY());

                trail.getViewComponent().setTexture("IceCream" + cor + ".png");
                trail.addComponent(new TrailComponent(LIMITE_COLIDIVEL));

            }
        }

        for (Entity entity : getGameWorld().getEntitiesByType(JogozType.TRAIL)) {
            entity.getComponent(TrailComponent.class).increaseCount();
        }
    }

    private TimerAction coinsSpawnAndAnimationTimer() {
        return getMasterTimer().runAtInterval(() -> {
            if (getGameState().getInt("CoinsOnWorld") < 5) {
                Entity cherry = getGameWorld().spawn("Coin",
                        FXGLMath.random(0, getWidth() - 40),
                        FXGLMath.random(-35, getHeight() - 75)
                );

                cherry.getView().setVisible(false);

                FadeTransition ft = new FadeTransition(Duration.seconds(1), cherry.getView());
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();

                Entities.animationBuilder()
                        .duration(Duration.seconds(1))
                        .repeat(1)
                        .rotate(cherry)
                        .rotateTo(360)
                        .buildAndPlay();

                Entities.animationBuilder()
                        .duration(Duration.seconds(1))
                        .repeat(1)
                        .scale(cherry)
                        .from(new Point2D(1.75, 1.75))
                        .to(new Point2D(1, 1))
                        .buildAndPlay()
                        .setOnFinished(() -> {
                            cherry.addComponent(new CollidableComponent(true));

                            Entity dustCloud = getGameWorld().spawn("ParticleDustCloud");
                            dustCloud.getView().setVisible(false);

                            dustCloud.setX(cherry.getX() + cherry.getWidth() / 2 - 503);
                            dustCloud.setY(cherry.getY() + cherry.getHeight() / 2 - 463);

                            Entities.animationBuilder()
                                    .duration(Duration.millis(300))
                                    .repeat(1)
                                    .scale(dustCloud)
                                    .from(Point2D.ZERO)
                                    .to(new Point2D(0.1, 0.1))
                                    .buildAndPlay().setOnFinished(() -> {
                                        FadeTransition ftDust = new FadeTransition(Duration.millis(300), dustCloud.getView());
                                        ftDust.setFromValue(0.7);
                                        ftDust.setToValue(0);
                                        ftDust.play();

                                        Entities.animationBuilder()
                                                .duration(Duration.millis(300))
                                                .repeat(1)
                                                .scale(dustCloud)
                                                .from(new Point2D(0.1, 0.1))
                                                .to(new Point2D(0.15, 0.15))
                                                .buildAndPlay().setOnFinished(() -> {
                                                    dustCloud.removeFromWorld();
                                                });
                                    });

                            getMasterTimer().runOnceAfter(() -> {
                                dustCloud.getView().setVisible(true);
                                dustCloud.getView().setOpacity(0.7);
                            }, Duration.millis(20));
                        });

                getMasterTimer().runOnceAfter(() -> {
                    cherry.getView().setVisible(true);
                }, Duration.millis(10));

                getGameState().increment("CoinsOnWorld", 1);
            }

        }, Duration.seconds(1));
    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundRepeat("GameBackground.png");
        getGameScene().setCursor("CursorIceCream.png", Point2D.ZERO);

        textCherrys.setTranslateX(0);
        textCherrys.setTranslateY(45);
        textCherrys.setFont(Font.font("Forte", FontWeight.BOLD, 60));
        textCherrys.setFill(Paint.valueOf("#8E0200"));
        textCherrys.setStyle("-fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 2 );");

        textX.setTranslateX(0);
        textX.setTranslateY(getHeight() - 32);

        textY.setTranslateX(0);
        textY.setTranslateY(getHeight() - 22);

        textCoins.setTranslateX(45);
        textCoins.setTranslateY(getHeight() - 12);

        textCoinsLabel.setTranslateX(0);
        textCoinsLabel.setTranslateY(getHeight() - 12);

        textSpeed.setTranslateX(50);
        textSpeed.setTranslateY(getHeight() - 2);

        textSpeedLabel.setTranslateX(0);
        textSpeedLabel.setTranslateY(getHeight() - 2);

        textCoins.textProperty().bind(getGameState().intProperty("Coins").asString());
        textSpeed.textProperty().bind(getGameState().intProperty("MoveSpeed").asString());

        getGameScene().addUINodes(textCherrys);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(JogozType.PLAYER, JogozType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                collectCoin(coin);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(JogozType.GHOST_PLAYER, JogozType.COIN) {
            @Override
            protected void onCollisionBegin(Entity ghostPlayer, Entity coin) {
                collectCoin(coin);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(JogozType.PLAYER, JogozType.TRAIL) {
            @Override
            protected void onCollisionBegin(Entity jogador, Entity trail) {
                if (!getGameState().getBoolean("Invincible")) {

                    getGameScene().getViewport().setZoom(ZOOM);
                    getGameScene().getViewport().bindToEntity(player,
                            -player.getWidth() / 2 + (getGameScene().getViewport().getWidth() / (2 * ZOOM)),
                            -player.getHeight() / 2 + (getGameScene().getViewport().getHeight() / (2 * ZOOM)));

                    slowMo = true;
                    player.getComponent(PlayerControl.class).setSlowMo(slowMo);
                    spawnCoins.pause();
                    getAudioPlayer().stopMusic(gameMusic);
                    getAudioPlayer().playSound("GameOver.wav");

                    player.getViewComponent().setAnimatedTexture(new AnimatedTexture(new AnimationChannel("IceCreamCrying.png", 5, 70, 100, Duration.millis(1750), 0, 4)), true, false);

                    getMasterTimer().runOnceAfter(() -> {
                        File recordes = new File("HighScores.txt");
                        int previousHighScore = 0, highScoreGeral = 0;
                        List<String> linhasLidas = null;
                        boolean found = false, newHighScore = false;

                        if (!recordes.exists()) {
                            try {
                                recordes.createNewFile();
                            } catch (IOException ex) {
                                Logger.getLogger(JogozApp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        try {
                            linhasLidas = leitura_linhas(recordes.getName());
                        } catch (IOException ex) {
                            Logger.getLogger(JogozApp.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        for (int i = 0; i < linhasLidas.size(); i++) {
                            if (highScoreGeral < Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1))) {
                                highScoreGeral = Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1));
                            }

                            if (linhasLidas.get(i).substring(0, linhasLidas.get(i).lastIndexOf(":")).equals(user)) {
                                found = true;

                                previousHighScore = Integer.parseInt(linhasLidas.get(i).substring(linhasLidas.get(i).lastIndexOf(" ") + 1));

                                if (previousHighScore < getGameState().getInt("Coins")) {
                                    linhasLidas.remove(i);
                                    linhasLidas.add(i, user + ": " + String.valueOf(getGameState().getInt("Coins")));
                                    newHighScore = true;
                                    textPersonalHighScoreGameMenu.setText("Recorde pessoal: " + getGameState().getInt("Coins"));
                                    textPersonalHighScoreMainMenu.setText("Recorde pessoal: " + getGameState().getInt("Coins"));
                                }
                            }
                        }

                        if (!found) {
                            linhasLidas.add(user + ": " + String.valueOf(getGameState().getInt("Coins")));
                        }

                        PrintStream ps = null;
                        try {
                            ps = new PrintStream(recordes.getName());
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(JogozApp.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        for (String linhasLida : linhasLidas) {
                            ps.println(linhasLida);
                        }

                        ps.close();

                        if (highScoreGeral < getGameState().getInt("Coins")) {
                            JogozDisplay.showMessageBox("Recomeçar",
                                    event -> {
                                        startNewGame();
                                    }, "Game Over", "Novo recorde geral!",
                                    "Pontuação final: " + getGameState().getInt("Coins"));
                        } else if (previousHighScore < getGameState().getInt("Coins")) {
                            JogozDisplay.showMessageBox("Recomeçar",
                                    event -> {
                                        startNewGame();
                                    }, "Game Over", "Recorde geral: " + highScoreGeral, "Novo recorde pessoal!",
                                    "Pontuação final: " + getGameState().getInt("Coins"));
                        } else {
                            JogozDisplay.showMessageBox("Recomeçar",
                                    event -> {
                                        startNewGame();
                                    }, "Game Over", "Recorde geral: " + highScoreGeral, "Recorde pessoal: " + previousHighScore,
                                    "Pontuação final: " + getGameState().getInt("Coins"));
                        }
                    }, Duration.seconds(3));

                    getGameState().setValue("Invincible", true);
                }
            }
        });
    }

    private void collectCoin(Entity coin) {
        coin.removeFromWorld();
        getGameState().increment("Coins", +1);
        if (getGameState().getInt("Coins") % 10 == 0) {
            getGameState().increment("MoveSpeed", +1);
        }
        getGameState().increment("CoinsOnWorld", -1);
        textCherrys.setText("Cherrys: " + getGameState().getInt("Coins"));
    }

    @Override
    protected void onUpdate(double tpf) {
        if (menuMusicPlaying) {
            getAudioPlayer().stopMusic(mainMenuMusic);
            menuMusicPlaying = false;
        }

        if (!gameMusicPlaying && !slowMo) {
            gameMusicPlaying = true;
            getAudioPlayer().resumeMusic(gameMusic);
        }

        xy.setX(-getWidth() * 2);
        xy.setY(-getHeight() * 2);

        if (player.getBottomY() > limiteVisivelBottomY) {
            y.setX(player.getX());
            y.setY(player.getY() - getHeight());
            if (player.getY() > getHeight()) {
                player.setY(player.getY() - getHeight());
            }
        } else if (player.getY() < limiteVisivelY) {
            y.setX(player.getX());
            y.setY(player.getY() + getHeight());
            if (player.getBottomY() < 0) {
                player.setY(player.getY() + getHeight());
            }
        } else {
            y.setX(-getWidth() * 2);
            y.setY(-getHeight() * 2);
        }

        if (player.getRightX() > limiteVisivelRightX) {
            x.setX(player.getX() - getWidth());
            x.setY(player.getY());
            if (player.getX() > getWidth()) {
                player.setX(player.getX() - getWidth());
            }

            if (player.getBottomY() > limiteVisivelBottomY) {
                xy.setX(player.getX() - getWidth());
                xy.setY(player.getY() - getHeight());
            } else if (player.getY() < limiteVisivelY) {
                xy.setX(player.getX() - getWidth());
                xy.setY(player.getY() + getHeight());
            }
        } else if (player.getX() < limiteVisivelX) {
            x.setX(player.getX() + getWidth());
            x.setY(player.getY());
            if (player.getRightX() < 0) {
                player.setX(player.getX() + getWidth());
            }

            if (player.getBottomY() > limiteVisivelBottomY) {
                xy.setX(player.getX() + getWidth());
                xy.setY(player.getY() - getHeight());
            } else if (player.getY() < limiteVisivelY) {
                xy.setX(player.getX() + getWidth());
                xy.setY(player.getY() + getHeight());
            }
        } else {
            x.setX(-getWidth() * 2);
            x.setY(-getHeight() * 2);
        }

        textX.setText("X: " + String.valueOf(player.getX()));
        textY.setText("Y: " + String.valueOf(player.getY()));
    }

    public void toggleSpawnCoins() {
        if (getGameState().getBoolean("Debug")) {
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(JogozType.COIN));
            if (spawnCoins.isPaused()) {
                spawnCoins.resume();
            } else {
                spawnCoins.pause();
            }
            getGameState().setValue("CoinsOnWorld", 0);
        }
    }

    public void decrementTails() {
        if (getGameState().getBoolean("Debug")) {
            getGameState().increment("Coins", -10);
        }
    }

    public void toggleSlowMotion() {
        slowMo = !slowMo;
        player.getComponent(PlayerControl.class).setSlowMo(slowMo);
    }

    public int getCoins() {
        return getGameState().getInt("Coins");
    }

    public Music getMainMenuMusic() {
        return mainMenuMusic;
    }

    public Music getGameMusic() {
        return gameMusic;
    }

    public void setMainMenuMusic(Music mainMenuMusic) {
        this.mainMenuMusic = mainMenuMusic;
    }

    public void setGameMusic(Music gameMusic) {
        this.gameMusic = gameMusic;
    }

    public boolean isGameMusicPlaying() {
        return gameMusicPlaying;
    }

    public void setGameMusicPlaying(boolean isGameMusicPlaying) {
        this.gameMusicPlaying = isGameMusicPlaying;
    }

    public boolean isMenuMusicPlaying() {
        return menuMusicPlaying;
    }

    public void setMenuMusicPlaying(boolean menuMusicPlaying) {
        this.menuMusicPlaying = menuMusicPlaying;
    }

    private void toggleAdvancedStats() {
        if (getGameScene().getUINodes().size() > 2) {
            getGameScene().removeUINodes(textX, textY, textCoins, textCoinsLabel, textSpeed, textSpeedLabel);
        } else {
            getGameScene().addUINodes(textX, textY, textCoins, textCoinsLabel, textSpeed, textSpeedLabel);
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public List<String> leitura_linhas(String filename) throws IOException {
        List<String> lines = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        do {
            line = br.readLine();
            if (line != null) {
                lines.add(line);
            }
        } while (line != null);
        br.close();
        return lines;
    }

    public void setTextPersonalHighScoreMainMenu(Text textPersonalHighScore) {
        this.textPersonalHighScoreMainMenu = textPersonalHighScore;
    }

    public void setTextPersonalHighScoreGameMenu(Text textPersonalHighScoreGameMenu) {
        this.textPersonalHighScoreGameMenu = textPersonalHighScoreGameMenu;
    }
}
