// Created by Ruiz Tueros, Ricardo. 3� Software
package FruitsBasket;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.sun.j3d.utils.geometry.Box;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;

public class PhysicBox extends Figura {

    public PhysicBox(BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego, float width, float heigth, float depth, Appearance appearance, float posX, float posY, float posZ) {
        super(conjunto, listaObjetosFisicos, juego);

        Box box = new Box(width, heigth, depth, appearance);
        BoxShape boxShape = new BoxShape(new Vector3f(posX, posY, posZ));

        esMDL = false;
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(boxShape);

        desplazamientoFigura.addChild(box);
        ramaVisible.addChild(desplazamientoFigura);
    }
}
