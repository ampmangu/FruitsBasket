// Created by Ruiz Tueros, Ricardo. 3� Software
package FruitsBasket;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.utils.geometry.Cylinder;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;

public class PhysicCylinder extends Figura {

    public PhysicCylinder(BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego, float radius, float heigth, float posX, float posY, float posZ) {
        super(conjunto, listaObjetosFisicos, juego);

        Cylinder cylinder = new Cylinder(radius, heigth, new Appearance());
        CylinderShape cylinderShape = new CylinderShape(new Vector3f(posX, posY, posZ));

        esMDL = false;
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(cylinderShape);

        desplazamientoFigura.addChild(cylinder);
        ramaVisible.addChild(desplazamientoFigura);
    }
    
    public Vector3f GetNormalDirection() {
        Vector3d normalVector = conseguirDireccionFrontal();
        Vector3f normalizedVector = new Vector3f((float) normalVector.x, (float) normalVector.y, (float) normalVector.z);
        normalVector.normalize();
        return normalizedVector;
    }
    
    public void ApplyTransform(Transform transform) {
        Transform rigidBodyTransform = new Transform();
        cuerpoRigido.getWorldTransform(rigidBodyTransform);
        rigidBodyTransform.mul(transform);
    }

}
