package jogozMenu;

import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.app.*;

public class JogozSceneFactory extends SceneFactory {

    public FXGLMenu newMainMenu(GameApplication app) {
        return new JogozMenu(app, MenuType.MAIN_MENU);
        }

    public FXGLMenu newGameMenu(GameApplication app) {
        return new JogozMenu(app, MenuType.GAME_MENU);
        }
}
