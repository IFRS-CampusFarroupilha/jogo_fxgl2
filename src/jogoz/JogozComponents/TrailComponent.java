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
 * @author 05200215
 */
public class TrailComponent extends Component{
    private int count, collidableLimit = 0;
    
    public void increaseCount(){
        count++;
        
        if(count == collidableLimit){
            
            Entity trailQueMata = getEntity();
            trailQueMata.addComponent(new CollidableComponent(true));
            ((JogozApp)FXGL.getApp()).addIceCreamShapedHitBox(trailQueMata);
            
        } else if(count > ((JogozApp) FXGL.getApp()).getCoins()){
            
            getEntity().removeFromWorld();
        }
    }

    public TrailComponent(int collidableLimit) {
        this.collidableLimit = collidableLimit;
    }
}
