// Creado por Ruiz Tueros, Ricardo. 3� Software
package Practicas;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class TestPhysicRotation extends JFrame {

    public SimpleUniverse universo;
    public DiscreteDynamicsWorld mundoFisico;

    public TestPhysicRotation() {
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        AxisSweep3 broadphase = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
    }

    public static void main(String[] args) {
        TestPhysicRotation x = new TestPhysicRotation();
        x.setTitle("Physics rotation test");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, new Point3d(0.0f, 0.0f, 5.0f), new Point3d(0, 0, 0));
    }

    private BranchGroup crearEscena() {
        BranchGroup objRoot = new BranchGroup();
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0); //Localizacion de fuente/paso de luz
        LuzDireccional.setInfluencingBounds(limitesLuz);

        Cylinder cylinder = new Cylinder(0.4f, 0.7f, new Appearance());
        CylinderShape cylinderShape = new CylinderShape(new Vector3f(2.0f, 0.0f, 0.0f));

        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(0.0f, 0.0f, 0.0f));

        DefaultMotionState EstadoDeMovimiento = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo InformacionCuerpoR = new RigidBodyConstructionInfo(0.0f, EstadoDeMovimiento, cylinderShape, new Vector3f(0.0f, 1.0f, 0.0f));
        InformacionCuerpoR.restitution = 0.1f;
        RigidBody cuerpoRigido = new RigidBody(InformacionCuerpoR);
        cuerpoRigido.setActivationState(RigidBody.DISABLE_DEACTIVATION);
        cuerpoRigido.setDamping(0.3f, 0.1f);
        cuerpoRigido.setFriction(0.3f);

        // Rigidbody rotation
        Transform rotation = new Transform();
        rotation.setIdentity();
        rotation.setRotation(new Quat4f(0.7071f, 0.0f, 0.0f, 0.7071f));
        
        /*
        rotation.set(new Matrix3f(1, 0, 0,
                                  0, 1, 0,
                                  0, 0, 1));
        */
        
        Transform rbTransform = new Transform();
        cuerpoRigido.getCenterOfMassTransform(rbTransform);

        rbTransform.mul(rotation);

        mundoFisico.addRigidBody(cuerpoRigido);

        // Structure
        objRoot.addChild(cylinder);
        objRoot.addChild(LuzDireccional);
        return objRoot;
    }

    public void colocarCamara(SimpleUniverse universo, Point3d posicionCamara, Point3d objetivoCamara) {
        posicionCamara = new Point3d(posicionCamara.x + 0.001, posicionCamara.y + 0.001d, posicionCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
