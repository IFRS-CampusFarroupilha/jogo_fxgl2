package jogoz;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.*;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.physics.*;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author pedro
 */
public class JogozFactory implements EntityFactory {

    @Spawns("ghostPlayer")
    public Entity newGhostPlayer(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(JogozType.GHOST_PLAYER)
     //           .bbox(new HitBox(BoundingShape.polygon(new Point2D(25,0),new Point2D(45,0),new Point2D(45,5),new Point2D(55,5),new Point2D(55,10),new Point2D(60,10),new Point2D(60,15),new Point2D(65,15),new Point2D(65,25),new Point2D(70,25),new Point2D(70,45),new Point2D(65,45),new Point2D(65,60),new Point2D(60,60),new Point2D(60,70),new Point2D(55,70),new Point2D(55,80),new Point2D(50,80),new Point2D(50,90),new Point2D(45,90),new Point2D(45,95),new Point2D(40,95),new Point2D(40,100),new Point2D(30,100),new Point2D(30,95),new Point2D(25,95),new Point2D(25,90),new Point2D(20,90),new Point2D(20,80),new Point2D(15,80),new Point2D(15,70),new Point2D(10,70),new Point2D(10,60),new Point2D(5,60),new Point2D(5,45),new Point2D(0,45),new Point2D(0,25),new Point2D(5,25),new Point2D(5,15),new Point2D(10,15),new Point2D(10,10),new Point2D(15,10),new Point2D(15,5),new Point2D(25,5),new Point2D(25,0))))
                .viewFromAnimatedTexture(new AnimatedTexture(new AnimationChannel("IceCream2.png", 2, 70, 100, Duration.millis(1750), 0, 1)), true)
                .with(new CollidableComponent(true))
                .build();
    }
    
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(JogozType.PLAYER)
     //           .bbox(new HitBox(BoundingShape.polygon(new Point2D(25,0),new Point2D(45,0),new Point2D(45,5),new Point2D(55,5),new Point2D(55,10),new Point2D(60,10),new Point2D(60,15),new Point2D(65,15),new Point2D(65,25),new Point2D(70,25),new Point2D(70,45),new Point2D(65,45),new Point2D(65,60),new Point2D(60,60),new Point2D(60,70),new Point2D(55,70),new Point2D(55,80),new Point2D(50,80),new Point2D(50,90),new Point2D(45,90),new Point2D(45,95),new Point2D(40,95),new Point2D(40,100),new Point2D(30,100),new Point2D(30,95),new Point2D(25,95),new Point2D(25,90),new Point2D(20,90),new Point2D(20,80),new Point2D(15,80),new Point2D(15,70),new Point2D(10,70),new Point2D(10,60),new Point2D(5,60),new Point2D(5,45),new Point2D(0,45),new Point2D(0,25),new Point2D(5,25),new Point2D(5,15),new Point2D(10,15),new Point2D(10,10),new Point2D(15,10),new Point2D(15,5),new Point2D(25,5),new Point2D(25,0))))
                .viewFromAnimatedTexture(new AnimatedTexture(new AnimationChannel("IceCream2.png", 2, 70, 100, Duration.millis(1750), 0, 1)), true)
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("Coin")
    public Entity newCoin(SpawnData data) {
        Entity coin = Entities.builder()
                .from(data)
                .type(JogozType.COIN)
                .viewFromTexture("Cherry.png")
//                .with(new CollidableComponent(true), new ColorComponent())
                .build();
        
        coin.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(10,35), BoundingShape.box(20, 40)));
        coin.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(5,40), BoundingShape.box(30, 30)));
        coin.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(0,45), BoundingShape.box(40, 20)));
        
        return coin;
    }

    @Spawns("Trail")
    public Entity newTrail(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(JogozType.TRAIL)
                .renderLayer(new RenderLayer(1))
                .build();
    }
    
    @Spawns("ParticleDustCloud")
    public Entity newDustCloud(SpawnData data){
        return Entities.builder()
                .from(data)
                .renderLayer(new RenderLayer(2))
                .viewFromTexture("DustCloud.png")
                .build();
    }
}
