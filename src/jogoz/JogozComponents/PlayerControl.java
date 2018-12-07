/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogoz.JogozComponents;

import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.app.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import jogoz.JogozApp;
/**
 *
 * @author pedro
 */
public class PlayerControl extends Component{
    //0 = direita, 1 = direita-baixo, 2 = baixo, 3  = esquerda-baixo, 4 = esquerda, 5 = esquerda-cima, 6 = cima, 7 = direita-cima
    private short direction = 0;
    private double slowMotionModifier = 1;
    private boolean slowMo = false;

    public void setDirection(short direction) {
        this.direction = direction;
    }
    
    @Override
    public void onUpdate(double tpf){
        Double speed = FXGL.getApp().getGameState().getInt("MoveSpeed") * 60 * tpf;
        double diagonalSpeed;

        switch (direction) {
            case 0:
                moveX(speed);
                break;
            case 1:
                diagonalSpeed = Math.sqrt(Math.pow(speed, 2) / 2);
                moveX(diagonalSpeed);
                moveY(diagonalSpeed);
                break;
            case 2:
                moveY(speed);
                break;
            case 3:
                diagonalSpeed = Math.sqrt(Math.pow(speed, 2) / 2);
                moveX(-diagonalSpeed);
                moveY(diagonalSpeed);
                break;
            case 4:
                moveX(-speed);
                break;
            case 5:
                diagonalSpeed = Math.sqrt(Math.pow(speed, 2) / 2);
                moveX(-diagonalSpeed);
                moveY(-diagonalSpeed);
                break;
            case 6:
                moveY(-speed);
                break;
            case 7:
                diagonalSpeed = Math.sqrt(Math.pow(speed, 2) / 2);
                moveX(diagonalSpeed);
                moveY(-diagonalSpeed);
                break;
        }
    }
    
    private void moveX(double speed){
        if (slowMo) {
            getEntity().translateX(speed * slowMotionModifier);
        } else {
            getEntity().translateX(speed);
        }
    }
    
    private void moveY(double speed){
        if (slowMo) {
            getEntity().translateY(speed * slowMotionModifier);
        } else {
            getEntity().translateY(speed);
        }
    }
    
    public PlayerControl(double slowMotionModifier){
        this.slowMotionModifier = slowMotionModifier;
    }

    public void setSlowMo(boolean slowMo) {
        this.slowMo = slowMo;
    }
}
