// Created by Ruiz Tueros, Ricardo. 3º Software
package Weka;

import FruitsBasket.PhysicCylinder;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.input.KeyCode.R;
import javax.swing.JFrame;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;
import utilidades.TerrenoSimple;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;

public class LearningForce extends Juego {

    private PhysicCylinder cylinderA;
    private PhysicCylinder cylinderB;
    private final float cylinderAForce = 5.0f;
    private int initialForceTimes = 3;

    public LearningForce() {
        super();
    }

    @Override
    public void cargarContenido() {
        // 20x20 Simple green terrain
        TerrenoSimple terrain = new utilidades.TerrenoSimple(20, 20, 0.0f, 0.0f, 0.0f, "unaTextura_Desabilitada", conjunto, mundoFisico, 0.80f);

        // Cylinder agent A
        cylinderA = new PhysicCylinder(conjunto, listaObjetosFisicos, this, 0.8f, 3.0f, 5.0f, 2.5f, 5.0f);
        cylinderA.crearPropiedades(0.7f, 0.3f, 0.3f, 5.0f, 2.5f, 15.0f, mundoFisico);

        // Cylinder agent B
        cylinderB = new PhysicCylinder(conjunto, listaObjetosFisicos, this, 0.8f, 3.0f, 0.0f, 0.5f, 0.0f);
        cylinderB.crearPropiedades(0.7f, 0.5f, 0.3f, 15.0f, 0.5f, 6.0f, mundoFisico);

    }

    @Override
    public void actualizar(float dt) {
        FileReader trainingInstancesFile = null;
        try {
            //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS
            for (Figura listaObjetosFisico : this.listaObjetosFisicos) {
                listaObjetosFisico.actualizar();
            }
            
            Vector3f distanceAB = new Vector3f(cylinderB.posiciones[0] - cylinderA.posiciones[0], cylinderB.posiciones[1] - cylinderA.posiciones[1], cylinderB.posiciones[2] - cylinderA.posiciones[2]);
            Vector3f directionAB = new Vector3f();
            distanceAB.normalize(directionAB);

            // Read training instances files and set knowledge base
            trainingInstancesFile = new FileReader("forces.arff");
            Instances trainingInstances = new Instances(new BufferedReader(trainingInstancesFile));
            trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            M5P knowledge = new M5P();
            knowledge.buildClassifier(trainingInstances);

            // Train with 10 instances
            int numTraining = 10;

            for (int i = 0; i < numTraining; i++) {
                Instance trainingInstance = new Instance(trainingInstances.numAttributes());
                trainingInstance.setDataset(trainingInstances);
                Random random = new Random();
                trainingInstance.setValue(0, random.nextInt(4) - 2.0f);
                double estimatedForce = (float) knowledge.classifyInstance(trainingInstance);

                cylinderB.cuerpoRigido.applyCentralForce(new Vector3f((float) (directionAB.x * estimatedForce), (float) (directionAB.y * estimatedForce), (float) (directionAB.z * estimatedForce)));

                // Wait 5 s to calculate distance
                Vector3f afterDistanceAB = new Vector3f(cylinderB.posiciones[0] - cylinderA.posiciones[0], cylinderB.posiciones[1] - cylinderA.posiciones[1], cylinderB.posiciones[2] - cylinderA.posiciones[2]);
                trainingInstance.setClassValue(estimatedForce);
                trainingInstances.add(trainingInstance);
                knowledge.buildClassifier(trainingInstances);
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
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LearningForce.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LearningForce.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LearningForce.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                trainingInstancesFile.close();
            } catch (IOException ex) {
                Logger.getLogger(LearningForce.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        LearningForce x = new LearningForce();
        x.setTitle("Practica_4_2_1");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, new Point3d(20.5f, 20.0f, 30.0f), new Point3d(10.0f, 0.5f, 8.0f));
    }
}
