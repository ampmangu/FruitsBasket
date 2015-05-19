package utilidades;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import com.bulletphysics.util.ObjectArrayList;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import javax.vecmath.*;
import java.util.Random;

//Esta es una adaptacion de la clase SimpleLand.java, conviertiéndola en un figura fisica JBullet
public class TerrenoSimple extends BranchGroup {

    Point3f[] pts;
    int[] stripCounts;
    int[] contourCounts;
    float[] texts;
    public ObjectArrayList<Vector3f> vertices;
    GeometryInfo gi;
    Shape3D terreno = new Shape3D();

    /**
     * Creates a new instance of SimpleLand.
     *
     * @param length - the length of the flat rectangle at the centre of this
     * area.
     * @param width - the width of the flat rectangle at the centre of this
     * area.
     * @param texture - The location of the file to be used as a texture.
     */
    public TerrenoSimple(int length, int width, float posX, float posY, float posZ, String texture, BranchGroup conjunto, DiscreteDynamicsWorld mundoDinamico, float friccion) {
        BranchGroup ramaTerreno = new BranchGroup();
        Transform3D t3Dterreno = new Transform3D();
        t3Dterreno.set(new Vector3f(posX, posY, posZ));
        TransformGroup TGterreno = new TransformGroup(t3Dterreno);

        //Creacion de varios cuadrados visuales y fisicos, llamados aqui "láminas"
        float lenUnit = (float) (length / 4);
        float widUnit = (float) (width / 4);
        float[] yPoints = new float[5];
        float[] xPoints = new float[5];
        for (int i = 0; i < 5; i++) {
            yPoints[i] = (float) (i * lenUnit);
            xPoints[i] = (float) (i * widUnit);
        }
        pts = new Point3f[144];
        texts = new float[288];
        int counter = 0;
        int edgesize = 5;

        int moderator = 2;
        float[] zlist = new float[20];
        Random zMaker = new Random(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            zlist[i] = 2f * (zMaker.nextFloat() * (moderator)) - 1;
        }

        Point3f[][] grid = new Point3f[7][7];

        grid[0][0] = new Point3f((0 - edgesize), zlist[0], (0 - edgesize));

        for (int i = 0; i < 5; i++) {
            grid[i + 1][0] = new Point3f((i * lenUnit), zlist[i + 1], (0 - edgesize));
        }

        grid[6][0] = new Point3f((width + edgesize), zlist[5], (0 - edgesize));

        for (int j = 1; j < 6; j++) {
            grid[0][j] = new Point3f((0 - edgesize), zlist[5 + ((j - 1) * 2)], ((j - 1) * widUnit));
            for (int i = 1; i < 6; i++) {
                grid[i][j] = new Point3f(((i - 1) * lenUnit), 0, ((j - 1) * widUnit));
            }
            grid[6][j] = new Point3f((width + edgesize), zlist[5 + (j * 2)], ((j - 1) * widUnit));
        }
        grid[0][6] = new Point3f((0 - edgesize), zlist[14], (length + edgesize));
        for (int i = 0; i < 5; i++) {
            grid[i + 1][6] = new Point3f((i * lenUnit), zlist[15 + i], (length + edgesize));
        }
        grid[6][6] = new Point3f((width + edgesize), zlist[19], (length + edgesize));

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                pts[counter] = grid[i][j];
                counter++;
                pts[counter] = grid[i][j + 1];
                counter++;
                pts[counter] = grid[i + 1][j + 1];
                counter++;
                pts[counter] = grid[i + 1][j];
                counter++;
            }
        }
        for (int v = 0; v < 288; v++) {
            texts[v] = 0.0f;
            texts[++v] = 0.0f;
            texts[++v] = 0.0f;
            texts[++v] = widUnit;
            texts[++v] = lenUnit;
            texts[++v] = widUnit;
            texts[++v] = lenUnit;
            texts[++v] = 0.0f;
        }

        stripCounts = new int[36];
        contourCounts = new int[36];
        for (int i = 0; i < 36; i++) {
            stripCounts[i] = 4;
            contourCounts[i] = 1;
        }

        gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setTextureCoordinateParams(1, 2);
        gi.setTextureCoordinates(0, texts);
        gi.setCoordinates(pts);

        vertices = new ObjectArrayList<Vector3f>();
        for (int i = 0; i < gi.getCoordinates().length; i++) {
            Point3f r = new Point3f(gi.getCoordinates()[i]);
            vertices.add(new Vector3f(r.x, r.y, r.z));
        }
        gi.setStripCounts(stripCounts);
        gi.setContourCounts(contourCounts);
        NormalGenerator ng = new NormalGenerator();
        ng.setCreaseAngle((float) Math.toRadians(30));
        ng.generateNormals(gi);
        terreno.setGeometry(gi.getGeometryArray());

        //Lectura de textura
        //        Appearance aper = new Appearance();
        //	try      TextureLoader Texget=new TextureLoader(new java.net.URL(texture), null);
        //            Texture2D ourTex=(Texture2D) Texget.getTexture();
        //            TextureAttributes texatt=new TextureAttributes(TextureAttributes.BLEND, new Transform3D(), new Color4f(1.0f, 1.0f, 1.0f, 1.0f), TextureAttributes.NICEST);
        //            aper.setTextureAttributes(texatt);
        //            aper.setTexture(ourTex);
        //	} catch (Exception e)	{	System.err.println("error loading textures");		e.printStackTrace();}
        //        this.setAppearance(aper);
        ObjectArrayList<Vector3f> subvertices = new ObjectArrayList<Vector3f>();
        for (int i = 0; i < vertices.size(); i++) {
            subvertices.add(vertices.get(i));
            if (subvertices.size() >= 4) {
                inicializarPlaca(subvertices, posX, posY, posZ, mundoDinamico, 0, 0.1f, friccion);
                BranchGroup x = new BranchGroup();
                x.addChild(crearPlaca(subvertices));
                TGterreno.addChild(x);
                subvertices = new ObjectArrayList<Vector3f>();
            }
        }
        ramaTerreno.addChild(TGterreno);
        conjunto.addChild(ramaTerreno);
    }

    Shape3D crearPlaca(ObjectArrayList<Vector3f> subvertices) {
        QuadArray placa = new QuadArray(4, QuadArray.COORDINATES | LineArray.COLOR_3);
        Point3f p1 = new Point3f(subvertices.get(0).x, subvertices.get(0).y, subvertices.get(0).z);
        Point3f p2 = new Point3f(subvertices.get(1).x, subvertices.get(1).y, subvertices.get(1).z);
        Point3f p3 = new Point3f(subvertices.get(2).x, subvertices.get(2).y, subvertices.get(2).z);
        Point3f p4 = new Point3f(subvertices.get(3).x, subvertices.get(3).y, subvertices.get(3).z);
        placa.setCoordinate(0, p1);
        placa.setCoordinate(1, p2);
        placa.setCoordinate(2, p3);
        placa.setCoordinate(3, p4);
        placa.setColor(0, new Color3f(0.2f + subvertices.get(0).y / 6.1f, subvertices.get(0).y / 2f + 0.35f, 0.11f));
        placa.setColor(1, new Color3f(0.18f + subvertices.get(1).y / 6.2f, subvertices.get(1).y / 2f + 0.35f, 0.12f));
        placa.setColor(2, new Color3f(0.15f + subvertices.get(2).y / 6.3f, subvertices.get(2).y / 2f + 0.35f, 0.13f));
        placa.setColor(3, new Color3f(0.12f + subvertices.get(3).y / 6.4f, subvertices.get(3).y / 2f + 0.35f, 0.15f));
        return (new Shape3D(placa));
    }

    public static void inicializarPlaca(ObjectArrayList<Vector3f> vertices, float posX, float posY, float posZ, DiscreteDynamicsWorld mundoDinamico, float masa, float elasticidad, float friccion) {
        if (vertices.size() > 0) {
            Transform startTransform = new Transform();
            startTransform.setIdentity();
            startTransform.origin.set(new Vector3f(posX, posY, posZ));
            CollisionShape laminaFisica = new ConvexHullShape(vertices);
            boolean isDynamic = (masa != 0f);
            Vector3f localInertia = new Vector3f(0f, 0f, 0f);
            if (isDynamic) {
                laminaFisica.calculateLocalInertia(masa, localInertia);
            }
            DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
            RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(masa, myMotionState, laminaFisica, localInertia);
            cInfo.restitution = elasticidad;
            RigidBody body = new RigidBody(cInfo);
            body.setFriction(friccion);
            mundoDinamico.addRigidBody(body);
        }
    }
}
