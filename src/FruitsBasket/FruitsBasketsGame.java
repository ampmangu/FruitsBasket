// Created by Ruiz Tueros, Ricardo. 3� Software
package FruitsBasket;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import simulador.Figura;
import simulador.Juego;

public class FruitsBasketsGame extends Juego {

    private static final Point3d cameraPosition = new Point3d(0.0f, 10.0f, 30.0f);
    private static final Point3d cameraTarget = new Point3d(10.0f, 0.5f, 0.0f);

    public FruitsBasketsGame() {
        super();
    }

    @Override
    public void cargarContenido() {

        // Basketball physic court
        float groundWidth = 20.0f;
        float groundHeigth = 20.0f;
        float positionXGround = 0.0f;
        float positionYGround = 0.0f;
        float positionZGround = 0.0f;

        FruitBasketsCourt ground = new FruitBasketsCourt(conjunto, listaObjetosFisicos, this, groundWidth, groundHeigth, positionXGround, positionYGround, positionZGround);
        ground.crearPropiedades(0.0f, 0.0f, 0.0f, positionXGround, positionYGround, positionZGround, mundoFisico);

        // Wood appearance
        Appearance woodAppearance = new Appearance();
        woodAppearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.87f, 0.72f, 0.53f), ColoringAttributes.NICEST));

        // Orange appearance
        Appearance orangeAppearance = new Appearance();
        orangeAppearance.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 0.68f, 0.06f), ColoringAttributes.NICEST));

        // Base of basket
        float baseWidth = 0.5f;
        float baseHeigth = 5.0f;
        float baseDepth = 0.2f;
        float positionXBase = 10.0f;
        float positionYBase = 0.5f;
        float positionZBase = 0.0f;

        PhysicBox basketBase = new PhysicBox(conjunto, listaObjetosFisicos, this, baseWidth, baseHeigth, baseDepth, woodAppearance, positionXBase, positionYBase, positionZBase);
        basketBase.crearPropiedades(0.0f, 0.0f, 0.0f, positionXBase, positionYBase, positionZBase, mundoFisico);

        // Basket elbow
        float elbowWidth = 0.5f;
        float elbowHeigth = 0.2f;
        float elbowDepth = 2.0f;
        float positionXElbow = 10.0f;
        float positionYElbow = 5.5f;
        float positionZElbow = 1.0f;

        PhysicBox basketElbow = new PhysicBox(conjunto, listaObjetosFisicos, this, elbowWidth, elbowHeigth, elbowDepth, woodAppearance, positionXElbow, positionYElbow, positionZElbow);
        basketElbow.crearPropiedades(0.0f, 0.0f, 0.0f, positionXElbow, positionYElbow, positionZElbow, mundoFisico);

        // Basket table
        float tableWidth = 2.0f;
        float tableHeigth = 1.5f;
        float tableDepth = 0.2f;
        float positionXTable = 10.0f;
        float positionYTable = 5.5f;
        float positionZTable = 2.9f;

        PhysicBox basketTable = new PhysicBox(conjunto, listaObjetosFisicos, this, tableWidth, tableHeigth, tableDepth, new Appearance(), positionXTable, positionYTable, positionZTable);
        basketTable.crearPropiedades(0.0f, 0.0f, 0.0f, positionXTable, positionYTable, positionZTable, mundoFisico);

        // Right side basket
        float widthRightSide = 0.2f;
        float heigthRightSide = 0.2f;
        float depthRightSide = 1.0f;
        float positionXRightSide = 10.7f;
        float positionYRightSide = 4.6f;
        float positionZRightSide = 5.0f;

        PhysicBox basketRightSide = new PhysicBox(conjunto, listaObjetosFisicos, this, widthRightSide, heigthRightSide, depthRightSide, orangeAppearance, positionXRightSide, positionYRightSide, positionZRightSide);
        basketRightSide.crearPropiedades(0.0f, 0.0f, 0.0f, positionXRightSide, positionYRightSide, positionZRightSide, mundoFisico);

        // Left side basket
        float widthLeftSide = 0.2f;
        float heigthLeftSide = 0.2f;
        float depthLeftSide = 1.0f;
        float positionXLeftSide = 8.5f;
        float positionYLeftSide = 4.6f;
        float positionZLeftSide = 5.0f;

        PhysicBox basketLeftSide = new PhysicBox(conjunto, listaObjetosFisicos, this, widthLeftSide, heigthLeftSide, depthLeftSide, orangeAppearance, positionXLeftSide, positionYLeftSide, positionZLeftSide);
        basketLeftSide.crearPropiedades(0.0f, 0.0f, 0.0f, positionXLeftSide, positionYLeftSide, positionZLeftSide, mundoFisico);

        // Front side basket
        float widthFrontSide = 1.0f;
        float heigthFrontSide = 0.2f;
        float depthFrontSide = 0.5f;
        float positionXFrontSide = 9.4f;
        float positionYFrontSide = 4.6f;
        float positionZFrontSide = 5.6f;

        PhysicBox basketFrontSide = new PhysicBox(conjunto, listaObjetosFisicos, this, widthFrontSide, heigthFrontSide, depthFrontSide, orangeAppearance, positionXFrontSide, positionYFrontSide, positionZFrontSide);
        basketFrontSide.crearPropiedades(0.0f, 0.0f, 0.0f, positionXFrontSide, positionYFrontSide, positionZFrontSide, mundoFisico);

    }

    @Override
    public void actualizar(float dt) {
        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS
        for (Figura listaObjetosFisico : this.listaObjetosFisicos) {
            listaObjetosFisico.actualizar();
        }

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS NO FISICAS
        for (int i = 0; i < this.listaObjetosNoFisicos.size(); i++) {
            listaObjetosNoFisicos.get(i).actualizar();
        }

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
        this.actualizandoFisicas = true;
        try {
            mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
        } catch (Exception e) {
            System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
        }
        this.actualizandoFisicas = false;
    }

    public static void main(String[] args) {
        FruitsBasketsGame x = new FruitsBasketsGame();
        x.setTitle("Practica_4_2_2");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, cameraPosition, cameraTarget);
    }
}
