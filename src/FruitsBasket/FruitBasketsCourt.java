// Created by Ruiz Tueros, Ricardo. 3º Software
package FruitsBasket;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.sun.j3d.utils.geometry.Box;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;

public class FruitBasketsCourt extends Figura {

    public FruitBasketsCourt(BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego, float width, float heigth, float positionX, float positionY, float positionZ) {
        super(conjunto, listaObjetosFisicos, juego);

        Appearance courtAppearance = new Appearance();
        
//        Texture courtTexture = new TextureLoader("BasketballCourt.jpg", juego).getTexture();
//        courtAppearance.setTexture(courtTexture);
//        TextureAttributes courtTextureAttributes = new TextureAttributes();
//        courtTextureAttributes.setTextureMode(TextureAttributes.MODULATE);
//        courtAppearance.setTextureAttributes(courtTextureAttributes);
        
        Material courtMaterial = new Material();
        courtMaterial.setDiffuseColor(new Color3f(0.8f, 0.2f, 0.0f));
        courtMaterial.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
        courtMaterial.setShininess(128.0f);
        courtAppearance.setMaterial(courtMaterial);        
               
        Box plane = new Box(width, 0.01f, heigth, courtAppearance);
        BoxShape boxShape = new BoxShape(new Vector3f(positionX, positionY, positionZ));

        esMDL = false;
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(boxShape);

        desplazamientoFigura.addChild(plane);
        ramaVisible.addChild(desplazamientoFigura);
    }
}
