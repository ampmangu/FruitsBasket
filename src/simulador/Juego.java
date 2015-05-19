package simulador;

import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import figuras.EsferaMDL;
import figuras.Esfera;

public class Juego extends JFrame implements Runnable {

    public int estadoJuego = 0;
    public SimpleUniverse universo;
    public BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    public Thread hebra = new Thread(this);
    public ArrayList<simulador.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    public ArrayList<simulador.Figura> listaObjetosNoFisicos = new ArrayList<Figura>();
    public DiscreteDynamicsWorld mundoFisico;
    public BranchGroup conjunto = new BranchGroup();
    public boolean actualizandoFisicas, mostrandoFisicas;
    public HebraCreadora creadora;

    // Pesonajes importantes del juego
    Figura personaje;  //golem;
    Figura soldado;
    EsferaMDL hada;
    Esfera animalEsferico;

    public Juego() {
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

        hebra.start();
    }

    public BranchGroup crearEscena() {
        BranchGroup objRoot = new BranchGroup();
        conjunto = new BranchGroup();
        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0); //Localizacion de fuente/paso de luz

        objRoot.addChild(LuzDireccional);
        mostrar.setSchedulingBounds(limites);
        LuzDireccional.setInfluencingBounds(limitesLuz);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);
        return objRoot;
    }

    public void cargarContenido() {
        float radio = 6;
        float masaConstruccion = 0;
        float elasticidad = 0.3f;           //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
        float dumpingLineal = 0.9f;    //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)
        Figura construccion1 = new Esfera(radio * 1.1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        construccion1.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 5, -3f, -0.5f, mundoFisico);

        Figura construccion2 = new Esfera(radio * 1.3f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        construccion2.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, -6f, -3f, 1.5f, mundoFisico);

        for (float y = -1f; y <= 1.5f; y = y + 2.1f) {
            for (int i = -7; i <= 5; i = i + 6) {
                Figura construccion4 = new Esfera(radio, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
                construccion4.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, i, y * 4 - 3f - i / 3f + 1f, -y * 9f + 1.0f, mundoFisico);
            }
        }

        float friccion = 0.97f;
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(30, 30, -5, -3f, -12, "unaTextura_Desabilitada", conjunto, mundoFisico, friccion);

        radio = 1f;
        float posX = 0f;
        float posY = 5f, posZ = 1f;
        float masa = 5f;
        personaje = new EsferaMDL("objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, true);
        personaje.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, posZ, mundoFisico);
        estadoJuego = 0;
    }

    public void actualizar(float dt) {
        //ACTUALIZAR EL ESTADO DEL JUEGO
        if (estadoJuego == 0) {
            creadora = new HebraCreadora(70, 0.5f, conjunto, listaObjetosFisicos, true, this, mundoFisico);
            creadora.start();
            estadoJuego = 1;
        } else if (estadoJuego == 1) {
            if (!creadora.isAlive()) {
                listaObjetosFisicos.get(listaObjetosFisicos.size() - 1).remover();     //Va eliminando la figura 10 y acorta la lista. Termina removiendo todos >10
                if (listaObjetosFisicos.size() <= 9) {
                    estadoJuego = 2;
                }
            }
        } else if (estadoJuego == 2) {
            colocarCamara(universo, new Point3d(6f, 4.5f, 32), new Point3d(8, 1, 0));

            //Creaci—n de una primera pareja  cazador-presa
            hada = new EsferaMDL("objetosMDL/pixie.mdl", 0.5f, conjunto, listaObjetosFisicos, this, false);
            if (!actualizandoFisicas) {
                hada.crearPropiedades(3, 0.5f, 0.5f, 22, -1, -16, mundoFisico);
            }
            hada.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
            animalEsferico = new Esfera(0.5f, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
            if (!actualizandoFisicas) {
                animalEsferico.crearPropiedades(3, 0.5f, 0.5f, 0, -1, 16, mundoFisico);
            }

            //Creaci—n de una segunda pareja  cazador-presa
            float radio = 1f;
            float elasticidad = 0.1f;
            float masaBalon = 1;
            soldado = new EsferaMDL("objetosMDL/Doomknight.mdl", radio, conjunto, listaObjetosFisicos, this, false);
            soldado.crearPropiedades(masaBalon, elasticidad, 0f, -3, 8, -3.5f, mundoFisico);
            elasticidad = 0.1f;
            float dumpingLineal = 0.5f;
            masaBalon = 1;
            personaje = new Esfera(radio, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
            Figura fig2 = new Esfera(radio, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
            Figura fig3 = new Esfera(radio, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
            if (!actualizandoFisicas) {
                personaje.crearPropiedades(masaBalon, elasticidad, dumpingLineal, 14f, 0f, 18f, mundoFisico);
            }
            if (!actualizandoFisicas) {
                fig2.crearPropiedades(masaBalon, elasticidad, dumpingLineal, 15.414f, 0f, 19.414f, mundoFisico);
            }
            if (!actualizandoFisicas) {
                fig3.crearPropiedades(masaBalon, elasticidad, dumpingLineal, 16.828f, 0f, 21.818f, mundoFisico);
            }
            RigidBody rbA = personaje.cuerpoRigido;
            RigidBody rbB = fig2.cuerpoRigido;
            RigidBody rbC = fig3.cuerpoRigido;
            Vector3f pivotInA = new Vector3f(radio, 0, 0);
            Vector3f pivotInB = new Vector3f(-radio, 0, 0);
            Point2PointConstraint x = new Point2PointConstraint(rbA, rbB, pivotInA, pivotInB);
            Point2PointConstraint y = new Point2PointConstraint(rbB, rbC, pivotInA, pivotInB);
            if (!actualizandoFisicas) {
                mundoFisico.addConstraint(x);
            }
            if (!actualizandoFisicas) {
                mundoFisico.addConstraint(y);
            }
            estadoJuego = 3;

        } else if (estadoJuego == 3) {   // BUSQUEDA DEL OBJETIVO Y EVASION DE OBSTACULOS CON ANGULOS FAVORABLES DE COLSION
            //Si hay varios personajes aut—nomos que persiguen a sus propios objetivos, este c—digo dener’a estar en el mŽtodo actualizar de Figura
            animalEsferico.asignarObjetivo(hada, 12f);
            personaje.asignarObjetivo(soldado, 75);
            if (hada.posiciones[2] > 10) {
                hada.asignarObjetivo(new Vector3f(15, 0, -15), 20f);
            }
            if (hada.posiciones[2] < -10) {
                hada.asignarObjetivo(new Vector3f(15, 0, 15), 20f);
            }
        }

        //ACTUALIZAR DATOS DE FUERZAS DEL PERSONAJE CONTROLADO POR EL JUGADOR
        if (personaje != null) {
            float fuerzaHaciaAdelante = 0, fuerzaLateral = 0;
            if (personaje.adelante) {
                fuerzaHaciaAdelante = personaje.masa * 10f * 2.5f;
            }
            if (personaje.atras) {
                fuerzaHaciaAdelante = -personaje.masa * 10f * 2.5f;
            }
            if (personaje.derecha) {
                fuerzaLateral = -personaje.masa * 4f;
            }
            if (personaje.izquierda) {
                fuerzaLateral = personaje.masa * 4f;
            }

            Vector3d direccionFrente = personaje.conseguirDireccionFrontal();
            personaje.cuerpoRigido.applyCentralForce(new Vector3f((float) direccionFrente.x * fuerzaHaciaAdelante * 0.1f, 0, (float) direccionFrente.z * fuerzaHaciaAdelante * 0.1f));
            personaje.cuerpoRigido.applyTorque(new Vector3f(0, fuerzaLateral, 0));
        }

        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
            listaObjetosFisicos.get(i).actualizar();
        }

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS NO FISICAS
        //for (int i=0; i< this.listaObjetosNoFisicos.size(); i++)
        // listaObjetosNoFisicos.get(i).actualizar(dt);
        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
        this.actualizandoFisicas = true;
        try {
            mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
        } catch (Exception e) {
            System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
        }
        this.actualizandoFisicas = false;

        personaje.posAnteriorMilimetros = new int[3];
        personaje.posAnteriorMilimetros[0] = (int) (personaje.posiciones[0] * 10000f);
        personaje.posAnteriorMilimetros[2] = (int) (personaje.posiciones[2] * 10000f);
    }

    public void mostrar() throws Exception {
   //MOSTRAR FIGURAS NO FISICAS (con base en sus datos de localizacion)
        // for (int i=0; i< this.listaObjetosNoFisicos.size(); i++)
        // listaObjetosNoFisicos.get(i).mostrar();

        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        this.mostrandoFisicas = true;
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicos.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicos.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicos.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        this.mostrandoFisicas = false;
    }

    public void run() {
        cargarContenido();
        float dt = 3f / 100f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (estadoJuego != -1) {
            try {
                actualizar(dt);
            } catch (Exception e) {
                System.out.println("Error durante actualizar. Estado del juego " + estadoJuego);
            }
            //mostrar();
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
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

    public static void main(String[] args) {
        Juego x = new Juego();
        x.setTitle("Simulacion basica");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, new Point3d(2.5f, 8f, 22f + 10f), new Point3d(3, 0, 0));
    }
}

class HebraCreadora extends Thread {

    final BranchGroup conjunto;
    final ArrayList<simulador.Figura> listaObjetosFisicos;
    DiscreteDynamicsWorld mundoFisico = null;
    final Juego juego;
    int maxEsferas;
    boolean mdl;
    float radio;

    public HebraCreadora(int maxEsferas, float radio, BranchGroup conjunto, ArrayList<simulador.Figura> listaObjetosFisicos, boolean mdl, Juego j, DiscreteDynamicsWorld mundoFisico) {
        this.conjunto = conjunto;
        this.listaObjetosFisicos = listaObjetosFisicos;
        this.mundoFisico = mundoFisico;
        this.juego = j;
        this.maxEsferas = maxEsferas;
        this.mdl = mdl;
        this.radio = radio;
    }

    public void run() {
        int numEsferas = 0;
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }
        while (numEsferas <= maxEsferas) {
            float elasticidad = 0.5f;
            float dumpingLineal = 0.5f;
            float masa = 5;
            for (float x = 3; x >= -3; x = x - 2f) {
                numEsferas++;
                Figura fig;
                if (mdl) {
                    fig = new EsferaMDL("objetosMDL/pixie.mdl", radio, conjunto, listaObjetosFisicos, juego, false);
                } else {
                    fig = new Esfera(radio, "texturas//tronco.jpg", conjunto, listaObjetosFisicos, juego);
                }
                if (!juego.actualizandoFisicas) {
                    fig.crearPropiedades(masa, elasticidad, dumpingLineal, x, 11f + (float) Math.random() * 2f, -x * 1.5f, mundoFisico);
                }
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }
}
